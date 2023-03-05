package se.nishiyamastudios.fundeciderproject.ui.start

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.nishiyamastudios.fundeciderproject.R

class StartFragment : Fragment() {

    //private lateinit var bottomNavigationView: BottomNavigationView

    companion object {
        fun newInstance() = StartFragment()
    }

    private lateinit var viewModel: StartViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StartViewModel::class.java)
        // TODO: Use the ViewModel

        //private lateinit var bottomNavigationView: BottomNavigationView


        val bottomNavigationView = requireView().findViewById<BottomNavigationView>(R.id.bottom_navigation_view)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page1 -> {
                    findNavController().navigate(R.id.action_startFragment_to_favoritesFragment)
                    Log.i("PIA11DEBUG", "page1 blev tryckt på!")
                    true
                }
                R.id.page2 -> {
                    findNavController().navigate(R.id.action_startFragment_to_blacklistFragment)
                    true
                }
                R.id.page3 -> {
                    findNavController().navigate(R.id.action_startFragment_to_loginFragment)
                    true
                }
                else -> {
                    false
                }
            }
        }

        /*
       bottomNavigationView = findViewById(R.id.bottom_navigation_view)

       val badge = bottomNavigationView.getOrCreateBadge(R.id.page1)
       badge.isVisible = true
       badge.number = 90

       val textview = findViewById<TextView>(R.id.myTextTV)

       bottomNavigationView.setOnItemSelectedListener { item ->
           when(item.itemId) {
               R.id.page1 -> {
                   textview.setText("Pag1")
                   Log.i("PIA11DEBUG","page1 blev tryckt på!")
                   true
               }
               R.id.page2 -> {
                   true
               }
               R.id.page3 -> {
                   true
               }
               else -> {
                   false
               }
           }

       }
*/

    }

}