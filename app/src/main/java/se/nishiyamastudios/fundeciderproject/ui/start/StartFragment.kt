package se.nishiyamastudios.fundeciderproject.ui.start

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.nishiyamastudios.fundeciderproject.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.PlaceDetails
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentStartBinding
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
    private lateinit var currentPlace: PlaceDetails
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

        /*
        val placesObserver = Observer<List<Places>> {
            binding.selectedPlaceTV.text = myPlaces[0].name
        }

         */



        val autoCompleteTextView = binding.AutoCompleteTextview

        val Subjects = arrayOf("Restaurant", "Bar", "Café")

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, Subjects)
        autoCompleteTextView.setAdapter(adapter)

        // Set bottom navigation view to visible after logging in
        val activity  = view.context as? AppCompatActivity
        if (activity != null) {
            val navView = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.visibility = View.VISIBLE
        }

        binding.getPlacesButton.setOnClickListener {
            //model.getPlaces("https://api.geoapify.com/v2/places?categories=catering.restaurant&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit=2&apiKey=d357192221064b8da71d4143f306b152")
            Log.i("FUNDEBUG", "I Gotted IT!")

            val myPlaces =
                model.getPlaces("https://api.geoapify.com/v2/places?categories=catering.restaurant&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit=20&apiKey=d357192221064b8da71d4143f306b152")
            Thread.sleep(2_000)

            val myRandomPlace = model.getRandomPlace(myPlaces)
            currentPlace = myRandomPlace

            val placeName = currentPlace.name
            val placeStreet = currentPlace.street
            val placeStreetNumber = currentPlace.housenumber
            val placePhone = currentPlace.phone
            val placeEmail = currentPlace.email
            val placeWebsite = currentPlace.website
            val placeOpeningHours = currentPlace.openinghours


            binding.selectedPlaceTV.text = placeName
            binding.placeStreetTV.text = placeStreet + " " +placeStreetNumber

            Log.i("FUNDEBUG", "Random name: " + currentPlace.name)
            Log.i("FUNDEBUG", "Random street: " + currentPlace.street + currentPlace.housenumber)
            Log.i("FUNDEBUG", "Random postcode: " + currentPlace.postcode)
            Log.i("FUNDEBUG", "Random email: " + currentPlace.email)
            Log.i("FUNDEBUG", "Random phone: " + currentPlace.phone)
            Log.i("FUNDEBUG", "Random website: " + currentPlace.website)
            Log.i("FUNDEBUG", "Random openinghours: " + currentPlace.openinghours)
            Log.i("FUNDEBUG", "Random placeId: " + currentPlace.placeid)

        }

        binding.AutoCompleteTextview.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->
                val category = autoCompleteTextView.text.toString().lowercase()
                binding.getPlacesButton.setText("Find your new favorite " + category +"!")
               /*
                Toast.makeText(
                    ApplicationProvider.getApplicationContext<Context>(),
                    "" + autoCompleteTextView.text.toString(),
                    Toast.LENGTH_SHORT
                ).show()

                */
            }

            binding.addFavoriteButton.setOnClickListener {

                if (binding.selectedPlaceTV.text != "") {
                    val placeName = currentPlace.name
                    val placeId = currentPlace.placeid

                    fbUtil.addFavoriteOrBlacklistItem("funfavorite", placeName, placeId)

                }
            }

            binding.addBlacklistButton.setOnClickListener {

                if (binding.selectedPlaceTV.text != "") {
                    val placeName = currentPlace.name
                    val placeId = currentPlace.placeid

                    fbUtil.addFavoriteOrBlacklistItem("funblacklist", placeName, placeId)
                }
            }

        binding.placeStreetTV.setOnClickListener {
            val address = currentPlace.street + " " +currentPlace.housenumber
            val url = "https://www.google.com/maps/search/?api=1&query=$address"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(intent)
        }

        }

    }

