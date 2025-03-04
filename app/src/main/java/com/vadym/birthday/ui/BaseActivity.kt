package com.vadym.birthday.ui

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.FirebaseApp
import com.vadym.birthday.R
import com.vadym.birthday.ui.gift.GiftCardActivity
import com.vadym.birthday.ui.home.MainActivity
import com.vadym.birthday.ui.info.InfoActivity
import java.net.URL


abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var toolbar: Toolbar
    lateinit var drawer: DrawerLayout
    lateinit var navigationView: NavigationView
    lateinit var toggle: ActionBarDrawerToggle
    lateinit var appVersion: TextView

    override fun setContentView(layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.activity_main, null) as DrawerLayout
        val activityContainer = fullView.findViewById<View>(R.id.content_base) as FrameLayout

        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar

        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        toggle = ActionBarDrawerToggle(this, drawer, toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        )
        drawer.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        toggle.syncState()

        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId
        when (id) {
            R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
            R.id.nav_tf -> startActivity(Intent(openUAShimjeong()))
            R.id.nav_gift -> startActivity(Intent(this, GiftCardActivity::class.java))
            R.id.nav_info -> startActivity(Intent(this, InfoActivity::class.java))
            R.id.nav_share -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                val shareBody = getString(R.string.share_body)
                sharingIntent.apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "BIRTHDAY")
                    putExtra(Intent.EXTRA_TEXT, shareBody + URL("https", "play.google.com", "store/apps/details?id=com.vadym.birthday"))
                }
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_by)))
            }

            R.id.nav_send -> {
                val uri = Uri.parse("mailto:vadym.adv@gmail.com")
                val sendIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                sendIntent.data = uri
                startActivity(Intent.createChooser(sendIntent, "Birthday"))
            }
        }

        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    private fun openUAShimjeong(): Intent {
        val packageName = "mattermost"
        val url = "https://umua.org/hpwords/channels/town-square"
        return packageManager.getLaunchIntentForPackage(packageName) ?: Intent(Intent.ACTION_VIEW, Uri.parse(url))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        init(savedInstanceState)
    }

    abstract fun init(savedInstanceState: Bundle?)

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return when {
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}