package com.vadym.birthday.ui.home

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.work.WorkManager
import com.google.android.gms.ads.AdView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.messaging.FirebaseMessaging
import com.vadym.birthday.R
import com.vadym.birthday.ui.Admob
import com.vadym.birthday.ui.BaseActivity
import com.vadym.birthday.ui.home.MainViewModel.GroupName
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity() {
    private lateinit var adapter: PersonAdapter
    private lateinit var itemTouchHelper: ItemTouchHelper
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var firebaseMessaging: FirebaseMessaging
    private val vm by viewModel<MainViewModel>()


    override fun init(savedInstanceState: Bundle?) {
        super.setContentView(R.layout.view_person_list)
        firebaseMessaging = FirebaseMessaging.getInstance()
        setSupportActionBar(toolbar)
        checkNotificationPermissions()
        val headerView = navigationView.getHeaderView(0)
        appVersion = headerView.findViewById(R.id.app_version)
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName
        appVersion.text = "app v. $versionName"
        sharedPreferences = getSharedPreferences("AppPreferences", MODE_PRIVATE)

        val adContainer: AdView = findViewById(R.id.adView)

        if (isNetworkAvailable(this)) {
            adContainer.visibility = View.VISIBLE
            Admob.initializeAdmob(this, adContainer)
        } else {
            adContainer.visibility = View.GONE
        }

        toggle = ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        firebaseMessaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Log.d("MainActivity", "Token: $token")
                // Send token to server
            } else {
                Log.w("MainActivity", "Failed to get token")
            }
        }

        val fab = findViewById<FloatingActionButton>(R.id.fab)
        val rvListPerson = findViewById<RecyclerView>(R.id.rv_list_person)
        val searchFAB = findViewById<FloatingActionButton>(R.id.search_fab)
        val searchLayout = findViewById<LinearLayout>(R.id.search_layout)
        val searchClose = findViewById<ImageView>(R.id.close_search)
        val searchText = findViewById<EditText>(R.id.search_text)

        searchFAB.setOnClickListener {
            searchLayout.visibility = View.VISIBLE
            animateFabToRow(searchFAB, searchLayout)
        }

        searchClose.setOnClickListener {
            animateRowToFab(searchFAB, searchLayout)
        }

        showOrHideFab(rvListPerson, searchFAB)

        searchText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                s?.let {
                    vm.searchByName(it.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        rvListPerson.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        rvListPerson.setHasFixedSize(true)

        appVersion.setOnClickListener {
            vm.clickByAppV()
        }

        vm.fabIsVisible.observe(this) { isVisible ->
            fab.visibility = isVisible
        }

        fab.setOnClickListener {
            val shared = this.getSharedPreferences("Position", MODE_PRIVATE)
            startActivity(Intent(this, CreatePersonItemActivity::class.java))
            with(shared.edit()) {
                putInt("lastPosition", vm.allPersons.lastIndex) // TODO: change to observe
                apply()
            }
        }

        adapter = PersonAdapter(
            context = this,
//            personList = emptyList(),
            WorkManager.getInstance(this),
            { id -> vm.onRemovePersonClick(id) },
            { person ->
                vm.isBirthToday(person.personId.toString(), person.personDayOfBirth.toString())
                vm.isBirthTodayLive.observe(this) { isToday ->
                    person.isBirthToday = isToday
                    sharedPreferences.edit().putBoolean("isToday", isToday).apply()
                }

                vm.isBirthOnWeek(person.personDayOfBirth.toString())
                vm.isBirthWeekLive.observe(this) { duringWeek ->
                    person.isBirthOnWeek = duringWeek
                }
            },
            itemTouchHelper = null
        )

        val callback = DragDropCallback(adapter, vm)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper.attachToRecyclerView(rvListPerson)
        adapter.setItemTouchHelper(itemTouchHelper)


        rvListPerson.adapter = adapter

        vm.resultPersonListLive.observe(this) { filteredPersons ->
            adapter.updateList(filteredPersons)
        }

        vm.getListPerson()

        vm.isPersonDeleted.observe(this) { isDeleted ->
            if (isDeleted) {
                vm.getListPerson()
            } else {
                Toast.makeText(this, "Failed to delete person", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.filter_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.today -> {
                vm.filterListByCategory("today")
                true
            }
            R.id.week -> {
                vm.filterListByCategory("week")
                true
            }
            R.id.preschoolers -> {
                vm.filterListByCategory(GroupName.PRESCHOOLERS.title)
                true
            }
            R.id.primary_school -> {
                vm.filterListByCategory(GroupName.ELEMENTARY_SCHOOL.title)
                true
            }
            R.id.secondary_school -> {
                vm.filterListByCategory(GroupName.SECONDARY_SCHOOL.title)
                true
            }
            R.id.high_school -> {
                vm.filterListByCategory(GroupName.HIGH_SCHOOL.title)
                true
            }
            R.id.adults -> {
                vm.filterListByCategory(GroupName.ADULTS.title)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun checkNotificationPermissions(): Boolean {
        // Check if notification permissions are granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val isEnabled = notificationManager.areNotificationsEnabled()

            if (!isEnabled) {
                // Open the app notification settings if notifications are not enabled
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
                return false
            }
        } else {
            val areEnabled = NotificationManagerCompat.from(this).areNotificationsEnabled()

            if (!areEnabled) {
                // Open the app notification settings if notifications are not enabled
                val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                intent.putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                startActivity(intent)
                return false
            }
        }

        // Permissions are granted
        return true
    }

    private fun animateFabToRow(searchFAB: FloatingActionButton, searchLayout: LinearLayout) {
        searchFAB.animate()
            .scaleX(0f)
            .scaleY(0f)
            .alpha(0f)
            .setDuration(700)
            .withStartAction {
                searchFAB.visibility = View.GONE
                searchLayout.visibility = View.VISIBLE
                searchLayout.alpha = 0f
                searchLayout.scaleX = 0f
                searchLayout.animate().alpha(1f).scaleX(1f).setDuration(500).start()
            }
            .start()
    }

    private fun animateRowToFab(searchFAB: FloatingActionButton, searchLayout: LinearLayout) {
        searchLayout.animate()
            .alpha(0f)
            .scaleX(0f)
            .setDuration(500)
            .withStartAction {
                searchLayout.visibility = View.GONE
                searchFAB.visibility = View.VISIBLE
                searchFAB.animate().scaleX(1f).scaleY(1f).alpha(1f).setDuration(300).start()
            }
            .start()
    }

    private fun showOrHideFab(rvListPerson: RecyclerView, searchFAB: FloatingActionButton) {
        rvListPerson.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0 && searchFAB.visibility == View.VISIBLE) {
                    searchFAB.hide()
                } else if (dy < 0 && searchFAB.visibility != View.VISIBLE) {
                    searchFAB.show()
                }
            }
        })
    }

}