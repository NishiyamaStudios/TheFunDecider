package se.nishiyamastudios.fundeciderproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    //private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //TODO: Koppla till Firebase
        //TODO: Skapa login fragment (navigation?)
        //TODO: Koppla till google places api

        /*
        bottomNavigationView = findViewById(R.id.bottom_navigation_view)

        val badge = bottomNavigationView.getOrCreateBadge(R.id.page1)
        badge.isVisible = false
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