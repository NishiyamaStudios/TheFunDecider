package se.nishiyamastudios.fundeciderproject.ui.start

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import se.nishiyamastudios.fundeciderproject.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.PlaceDetails
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentStartBinding
import se.nishiyamastudios.fundeciderproject.ui.favorites.FavoritesViewModel
import se.nishiyamastudios.fundeciderproject.ui.login.LoginViewModel
import java.io.IOException
import kotlin.random.Random

class StartFragment : Fragment() {

    //TODO: Kategorier att använda: Catering / restaurant, pub, cafe, fast food + fler?
    //TODO: Välja stad? Hur funkar det? Kolla med hjälp av GPS i stället?

    var _binding : FragmentStartBinding? = null
    val binding get() = _binding!!

    private lateinit var selectedPlace: TextView
    private lateinit var placesClient: PlacesClient
    private lateinit var selectedCategory: String
    private lateinit var placeNames: MutableList<String>
    private val random = Random

    val model by viewModels<StartViewModel>()
    val fbUtil by viewModels<FirebaseUtility>()


    companion object {
        fun newInstance() = StartFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentStartBinding.inflate(inflater, container, false)
        return binding.root

        selectedPlace = binding.selectedPlaceTV

        placesClient = Places.createClient(requireContext())

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val myPlaces = model.getPlaces("https://api.geoapify.com/v2/places?categories=catering.restaurant&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit=20&apiKey=d357192221064b8da71d4143f306b152")

        val placesObserver = Observer<List<Places>> {
            binding.selectedPlaceTV.text = myPlaces[0].name
        }

        // Set bottom navigation view to visible after logging in
        val activity  = view.context as? AppCompatActivity
        if (activity != null) {
            val navView = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.visibility = View.VISIBLE
        }


        binding.getPlacesButton.setOnClickListener {
            //model.getPlaces("https://api.geoapify.com/v2/places?categories=catering.restaurant&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit=2&apiKey=d357192221064b8da71d4143f306b152")
            Log.i("FUNDEBUG", "I Gotted IT!")

            val myRandomPlace = model.getRandomPlace(myPlaces)
            binding.selectedPlaceTV.text = myRandomPlace.name

            Log.i("FUNDEBUG", "Random name: " + myRandomPlace.name)
            Log.i("FUNDEBUG", "Random street: " + myRandomPlace.street + myRandomPlace.housenumber)
            Log.i("FUNDEBUG", "Random postcode: " + myRandomPlace.postcode)
            Log.i("FUNDEBUG", "Random email: " + myRandomPlace.email)
            Log.i("FUNDEBUG", "Random phone: " + myRandomPlace.phone)
            Log.i("FUNDEBUG", "Random website: " + myRandomPlace.website)
            Log.i("FUNDEBUG", "Random openinghours: " + myRandomPlace.openinghours)
            Log.i("FUNDEBUG", "Random placeId: " + myRandomPlace.placeid)

            binding.addFavoriteButton.setOnClickListener {

                if (binding.selectedPlaceTV.text != "") {
                    val placeName = myRandomPlace.name
                    val placeId = myRandomPlace.placeid

                    fbUtil.addFavoriteOrBlacklistItem("funfavorite",placeName, placeId)

                }
            }

        }


    }

}