package se.nishiyamastudios.fundeciderproject

import android.Manifest
import android.content.Context
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import se.nishiyamastudios.fundeciderproject.ui.blacklist.BlacklistFragment
import se.nishiyamastudios.fundeciderproject.ui.favorites.FavoritesFragment
import se.nishiyamastudios.fundeciderproject.ui.login.LoginFragment
import se.nishiyamastudios.fundeciderproject.ui.start.StartFragment
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.utilityclass.LocationUtility


class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private val fbUtil = FirebaseUtility()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        // Request permission to access location
        val PERMISSION_ID = 1
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )

        // Let user know if location is not enabled
        if (!LocationEnable()) {
            val parentLayout = findViewById<View>(android.R.id.content)
            Snackbar.make(parentLayout, "Please enable location to be able to use this app.", Snackbar.LENGTH_LONG).show()
        }

        // Override backpress since there where issues with how the fragments acted
        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {

                supportFragmentManager.popBackStackImmediate()

                val backStackCount = supportFragmentManager.backStackEntryCount

                val backStackEntryName: String = try {
                    supportFragmentManager.getBackStackEntryAt(backStackCount - 1).name.toString()
                } catch (e: Exception) {
                    "page0"
                }

                when (backStackEntryName) {
                    "page0" -> bottomNavigationView.menu.findItem(R.id.startPage).isChecked = true
                    "page1" -> bottomNavigationView.menu.findItem(R.id.favoritesPage).isChecked = true
                    "page2" -> bottomNavigationView.menu.findItem(R.id.blacklistPage).isChecked = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(
            this,
            callback
        )

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        // Set fragment to start or login depending on if user is logged in or not
        Firebase.auth.addAuthStateListener {

            if (Firebase.auth.currentUser != null) {

                supportFragmentManager.beginTransaction().replace(R.id.fragNavCon, StartFragment()).commit()

            } else if (Firebase.auth.currentUser == null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragNavCon, LoginFragment()).commit()
                bottomNavigationView.visibility = View.GONE
            }
        }

        // Handle navigation through the bottom navigation view
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.startPage -> {
                    supportFragmentManager.beginTransaction().add(R.id.fragNavCon, StartFragment())
                        .addToBackStack("page0").commit()

                    true
                }
                R.id.favoritesPage -> {

                    fbUtil.loadFavorites()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragNavCon, FavoritesFragment()).addToBackStack("page1").commit()

                    true
                }
                R.id.blacklistPage -> {

                    fbUtil.loadBlacklist()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragNavCon, BlacklistFragment()).addToBackStack("page2").commit()

                    true
                }
                R.id.logout -> {

                    Firebase.auth.signOut()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragNavCon, LoginFragment()).commit()
                    bottomNavigationView.menu.findItem(R.id.startPage).isChecked = true
                    bottomNavigationView.visibility = View.GONE

                    false
                }
                else -> {
                    false
                }
            }

        }

    }

    // To check if location is enabled or not
    fun LocationEnable(): Boolean {
        var locationManager=getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
