package se.nishiyamastudios.fundeciderproject

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import se.nishiyamastudios.fundeciderproject.ui.blacklist.BlacklistFragment
import se.nishiyamastudios.fundeciderproject.ui.favorites.FavoritesFragment
import se.nishiyamastudios.fundeciderproject.ui.login.LoginFragment
import se.nishiyamastudios.fundeciderproject.ui.start.StartFragment
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                Log.i("FUNDEBUG3", "Backpressed!")

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
            this, // LifecycleOwner

            callback
        )

        Log.i("FUNDEBUGLOGIN", Firebase.auth.currentUser.toString())
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        Firebase.auth.addAuthStateListener {

            if (Firebase.auth.currentUser != null) {

                supportFragmentManager.beginTransaction().replace(R.id.fragNavCon, StartFragment()).commit()

            } else if (Firebase.auth.currentUser == null) {
                supportFragmentManager.beginTransaction().replace(R.id.fragNavCon, LoginFragment()).commit()
                bottomNavigationView.visibility = View.GONE
            }
        }

        val fbUtil = FirebaseUtility()

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
}
