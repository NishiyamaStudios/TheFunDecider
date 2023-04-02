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
                    "page0" -> bottomNavigationView.menu.findItem(R.id.page0).isChecked = true
                    "page1" -> bottomNavigationView.menu.findItem(R.id.page1).isChecked = true
                    "page2" -> bottomNavigationView.menu.findItem(R.id.page2).isChecked = true
                }
            }
        }
        onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner

            callback
        )


        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        Firebase.auth.addAuthStateListener {

            if (Firebase.auth.currentUser != null) {

                supportFragmentManager.beginTransaction().replace(R.id.fragNavCon, StartFragment())
                    .commit()

            }
        }

        val fbUtil = FirebaseUtility()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page0 -> {
                    supportFragmentManager.beginTransaction().add(R.id.fragNavCon, StartFragment())
                        .addToBackStack("page0").commit()

                    true
                }
                R.id.page1 -> {

                    fbUtil.loadFavorites()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragNavCon, FavoritesFragment()).addToBackStack("page1").commit()

                    true
                }
                R.id.page2 -> {

                    fbUtil.loadBlacklist()
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragNavCon, BlacklistFragment()).addToBackStack("page2").commit()

                    true
                }
                R.id.page3 -> {

                    Firebase.auth.signOut()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragNavCon, LoginFragment()).commit()
                    bottomNavigationView.menu.findItem(R.id.page0).isChecked = true
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
