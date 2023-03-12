package se.nishiyamastudios.fundeciderproject

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.RenderProcessGoneDetail
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        //TODO: Koppla till Firebase
        //TODO: Skapa login fragment (navigation?)
        //TODO: Koppla till google places api
        //TODO: Se över att förenkla navigation, behövs all kod? Använd nav_graph PopUp behavior..

        Firebase.auth.addAuthStateListener {

            if (Firebase.auth.currentUser != null) {

                findNavController(R.id.fragNavCon).navigate(R.id.action_loginFragment_to_startFragment)
                /*
                    findNavController(R.id.fragNavCon).navigate(
                        R.id.nav_graph, null,
                        NavOptions.Builder().setPopUpTo(R.id.fragNavCon,true).build()
                    )

                 */
                }
            }

        val badge = bottomNavigationView.getOrCreateBadge(R.id.page1)
        badge.isVisible = false
        badge.number = 90

        bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.page1 -> {

                    val sourceFragment = findNavController(R.id.fragNavCon).currentDestination?.label.toString()
                    val targetFragment = "fragment_favorites"
                    navigateAndKeepStack(sourceFragment, targetFragment)

                    true
                }
                R.id.page2 -> {

                    val sourceFragment = findNavController(R.id.fragNavCon).currentDestination?.label.toString()
                    val targetFragment = "fragment_blacklist"
                    navigateAndKeepStack(sourceFragment, targetFragment)

                    true
                }
                R.id.page3 -> {

                    val currentDestinationLabel = findNavController(R.id.fragNavCon).currentDestination?.label.toString()
                    Firebase.auth.signOut()
                    bottomNavigationView.visibility = View.GONE
                    navigateAndClearStack(currentDestinationLabel)

                    true
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
                    "fragment_favorites" -> findNavController(R.id.fragNavCon).navigate(R.id.action_startFragment_to_favoritesFragment)
                    "fragment_blacklist" -> findNavController(R.id.fragNavCon).navigate(R.id.action_startFragment_to_blacklistFragment)
                }
            "fragment_favorites" ->
                when (targetFragment) {
                    "fragment_blacklist" -> findNavController(R.id.fragNavCon).navigate(R.id.action_favoritesFragment_to_blacklistFragment)
                }
            "fragment_blacklist" ->
                when (targetFragment) {
                    "fragment_favorites" -> findNavController(R.id.fragNavCon).navigate(R.id.action_blacklistFragment_to_favoritesFragment)
                }
        }

        }
    }
