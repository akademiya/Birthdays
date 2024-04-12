package com.vadym.birthday

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import java.net.URL

abstract class BaseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, InternetConnectionReceiver.NetworkReceiverListener {

    internal lateinit var toolbar: Toolbar
    private lateinit var drawer: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle

    override fun setContentView(layoutResID: Int) {
        val fullView = layoutInflater.inflate(R.layout.activity_main, null) as DrawerLayout
        val activityContainer = fullView.findViewById<View>(R.id.content_base) as FrameLayout

        layoutInflater.inflate(layoutResID, activityContainer, true)
        super.setContentView(fullView)

        toolbar = findViewById<View>(R.id.toolbar) as Toolbar

        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        toggle = ActionBarDrawerToggle(this, drawer, toolbar, R.string.drawer_open, R.string.drawer_close)
        drawer.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = resources.getColor(R.color.white)
        toggle.syncState()

        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView.setNavigationItemSelectedListener(this)

    }

    override fun onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val id = menuItem.itemId
        when (id) {
            R.id.nav_home -> startActivity(Intent(this, MainActivity::class.java))
            R.id.nav_fb -> startActivity(Intent(openFacebook(this)))
            R.id.nav_ins -> startActivity(Intent(openInstagram(this)))
            R.id.nav_share -> {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                val shareBody = getString(R.string.share_body)
                sharingIntent.apply {
                    noAnimation()
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, "Birthday")
                    putExtra(Intent.EXTRA_TEXT, shareBody + URL("https", "play.google.com", "store/apps/details?id=me.vadym.adv.tfprayer"))
                }
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.share_by)))
            }

            R.id.nav_send -> {
                val uri = Uri.parse("mailto:vadym.adv@gmail.com")
                val sendIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
                sendIntent.data = uri
                sendIntent.noAnimation()
                startActivity(Intent.createChooser(sendIntent, "Birthday"))
            }
        }

        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    open fun openFacebook(context: Context): Intent? {
        val url = "https://www.facebook.com/LovelyHomeAnimals"
        return try {
            context.packageManager.getPackageInfo("com.facebook.katana", 0)
            Intent(Intent.ACTION_VIEW, Uri.parse("fb://facewebmodal/f?href=$url"))
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
    }

    open fun openInstagram(context: Context): Intent? {
        val url = "https://www.instagram.com/lovely_home_animals/"
        return try {
            context.packageManager.getPackageInfo("com.instagram.android", 0)
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        } catch (e: Exception) {
            Intent(Intent.ACTION_VIEW, Uri.parse(url))
        }
    }

    private fun doesAppInstalled(uri: String) : Boolean {
        val pm: PackageManager = packageManager
        return try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun setNavigationItemClicked(position: Int) {
        navigationView.menu.getItem(position).isChecked = true
    }


    abstract fun init(savedInstanceState: Bundle?)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)
    }


    private fun showMessageNoInternet(isConnected: Boolean) { //TODO show message no internet on any activity
        var mSnackBar: Snackbar? = null
        if (!isConnected) {
            mSnackBar = Snackbar.make(findViewById(R.id.content_base), R.string.no_internet, Snackbar.LENGTH_LONG)
            mSnackBar.show()
        } else
            mSnackBar?.dismiss()
    }

    override fun onNetworkConnectionChanged(isConnected: Boolean) {
        showMessageNoInternet(isConnected)
    }

}