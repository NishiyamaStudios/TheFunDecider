package se.nishiyamastudios.fundeciderproject

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import se.nishiyamastudios.fundeciderproject.ui.blacklist.BlacklistFragment
import se.nishiyamastudios.fundeciderproject.ui.favorites.FavoritesFragment
import se.nishiyamastudios.fundeciderproject.ui.login.LoginFragment
import se.nishiyamastudios.fundeciderproject.ui.start.StartFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currentBackStackName = supportFragmentManager.saveBackStack("currentBackstack")


        val callback = object : OnBackPressedCallback(
            true // default to enabled
        ) {
            override fun handleOnBackPressed() {
                Log.i("FUNDEBUG3", "Backpressed!")

                supportFragmentManager.popBackStackImmediate()

                //supportFragmentManager

                var backStackCount = supportFragmentManager.backStackEntryCount

                var backStackEntryName = ""

                    try {
                        backStackEntryName = supportFragmentManager.getBackStackEntryAt(backStackCount-1).name.toString()
                    } catch (e : Exception) {
                        backStackEntryName = "page0"
                    }


                    Log.i("FUNDEBUG3",backStackEntryName)
                    val rId = "R.id." + backStackEntryName

                    when(backStackEntryName) {
                        "page0" -> bottomNavigationView.getMenu().findItem(R.id.page0).setChecked(true)
                        "page1" -> bottomNavigationView.getMenu().findItem(R.id.page1).setChecked(true)
                        "page2" -> bottomNavigationView.getMenu().findItem(R.id.page2).setChecked(true)
                    }
                    //Log.i("FUNDEBUG3",rId)
                    //val resId = resources.getIdentifier(rId.toString(), "id", packageName)
                    //val resID = resources.getIdentifier(rId, "id", packageName)
                    //Log.i("FUNDEBUG3",resId.toString())
                    //bottomNavigationView.getMenu().findItem(resId).setChecked(true)


                Log.i("FUNDEBUG3",supportFragmentManager.backStackEntryCount.toString())

            }
        }
        onBackPressedDispatcher.addCallback(
            this, // LifecycleOwner

            callback
        )


        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        //TODO: Koppla till Firebase
        //TODO: Skapa login fragment (navigation?)
        //TODO: Koppla till google places api
        //TODO: Se över att förenkla navigation, behövs all kod? Använd nav_graph PopUp behavior..

        Firebase.auth.addAuthStateListener {

            if (Firebase.auth.currentUser != null) {

                supportFragmentManager.beginTransaction().replace(R.id.fragNavCon, StartFragment()).commit()

            }
            }


        val fbUtil by viewModels<FirebaseUtility>()

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page0 -> {
                    supportFragmentManager.beginTransaction().add(R.id.fragNavCon, StartFragment()).addToBackStack("page0").commit()

                    //val sourceFragment = findNavController(R.id.fragNavCon).currentDestination?.label.toString()
                    //val targetFragment = "StartFragment"
                    //navigateAndKeepStack(sourceFragment, targetFragment)

                    true
                }
                R.id.page1 -> {

                    fbUtil.loadFavorites()
                    supportFragmentManager.beginTransaction().add(R.id.fragNavCon, FavoritesFragment()).addToBackStack("page1").commit()

                    /*
                    val sourceFragment = findNavController(R.id.fragNavCon).currentDestination?.label.toString()
                    val targetFragment = "fragment_favorites"
                    navigateAndKeepStack(sourceFragment, targetFragment)

                     */

                    true
                }
                R.id.page2 -> {

                    fbUtil.loadBlacklist()
                    supportFragmentManager.beginTransaction().add(R.id.fragNavCon, BlacklistFragment()).addToBackStack("page2").commit()
                    //val sourceFragment = findNavController(R.id.fragNavCon).currentDestination?.label.toString()
                    //val targetFragment = "fragment_blacklist"
                    //navigateAndKeepStack(sourceFragment, targetFragment)

                    true
                }
                R.id.page3 -> {

                    //val currentDestinationLabel = findNavController(R.id.fragNavCon).currentDestination?.label.toString()
                    Firebase.auth.signOut()
                    //supportFragmentManager.clearBackStack(currentBackStackName.toString())        replace<ExampleFragment>(R.id.fragment_container)
                    supportFragmentManager.beginTransaction().replace(R.id.fragNavCon, LoginFragment()).commit()
                    bottomNavigationView.getMenu().findItem(R.id.page0).setChecked(true)
                    bottomNavigationView.visibility = View.GONE
                    //navigateAndClearStack(currentDestinationLabel)

                    false
                }
                else -> {
                    false
                }
            }

        }

    }

    fun navigateAndClearStack(currentDestinationLabel : String) {

        when (currentDestinationLabel) {
            "StartFragment" -> findNavController(R.id.fragNavCon).navigate(R.id.nav_graph,null,
                NavOptions.Builder().setPopUpTo(findNavController(R.id.fragNavCon).graph.startDestinationId, true).build())
            "fragment_favorites" -> findNavController(R.id.fragNavCon).navigate(R.id.nav_graph,null,
                NavOptions.Builder().setPopUpTo(findNavController(R.id.fragNavCon).graph.startDestinationId, true).build())
            "fragment_blacklist" -> findNavController(R.id.fragNavCon).navigate(R.id.nav_graph,null,
                NavOptions.Builder().setPopUpTo(findNavController(R.id.fragNavCon).graph.startDestinationId, true).build())

        }
    }

    fun navigateAndKeepStack(sourceFragment : String, targetFragment : String) {

        when (sourceFragment) {

            targetFragment -> null
            "StartFragment" ->
                when (targetFragment) {
                    "fragment_favorites" -> supportFragmentManager.beginTransaction().add(R.id.fragNavCon, FavoritesFragment()).addToBackStack(null).commit()
                        //findNavController(R.id.fragNavCon).navigate(R.id.action_startFragment_to_favoritesFragment)
                    "fragment_blacklist" -> supportFragmentManager.beginTransaction().add(R.id.fragNavCon, BlacklistFragment()).addToBackStack(null).commit()
                        //findNavController(R.id.fragNavCon).navigate(R.id.action_startFragment_to_blacklistFragment)
                }
            "fragment_favorites" ->
                when (targetFragment) {
                    "fragment_blacklist" -> supportFragmentManager.beginTransaction().add(R.id.fragNavCon, BlacklistFragment()).addToBackStack(null).commit()
                        //findNavController(R.id.fragNavCon).navigate(R.id.action_favoritesFragment_to_blacklistFragment)
                    "StartFragment" -> supportFragmentManager.beginTransaction().add(R.id.fragNavCon, StartFragment()).addToBackStack(null).commit()
                        //findNavController(R.id.fragNavCon).navigate(R.id.action_favoritesFragment_to_startFragment)
                }
            "fragment_blacklist" ->
                when (targetFragment) {
                    "fragment_favorites" -> findNavController(R.id.fragNavCon).navigate(R.id.action_blacklistFragment_to_favoritesFragment)
                    "StartFragment" -> findNavController(R.id.fragNavCon).navigate(R.id.action_blacklistFragment_to_startFragment)
                }
        }

        }
    }
