package se.nishiyamastudios.fundeciderproject.ui.start

import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager
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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import se.nishiyamastudios.fundeciderproject.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.PlaceDetails
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentStartBinding


class StartFragment : Fragment() {

    //TODO: Kategorier att använda: Catering / restaurant, pub, cafe, fast food, entertainment + fler?
    //TODO: Välja stad? Hur funkar det? Kolla med hjälp av GPS i stället?
    //TODO: Lägg in så att inte loginrutan dyker upp vid start även fast man är inloggad.
    //TODO: Lägg in så att de TextViews som inte har info tas bort så att det inte finns mellanrum

    var _binding : FragmentStartBinding? = null
    val binding get() = _binding!!

    private lateinit var selectedPlace: TextView
    private lateinit var placePhoneTV: TextView
    private lateinit var placesClient: PlacesClient
    private lateinit var selectedCategory: String
    private lateinit var placeNames: MutableList<String>
    private lateinit var currentPlace: PlaceDetails

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

        val Subjects = arrayOf("Restaurant", "Bar", "Pub", "Cafe", "Fast Food", "Entertainment")

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, Subjects)
        autoCompleteTextView.setAdapter(adapter)


        // Set bottom navigation view to visible after logging in
        val activity  = view.context as? AppCompatActivity
        if (activity != null) {
            val navView = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.visibility = View.VISIBLE
        }

        binding.getPlacesButton.setOnClickListener {

            val placesUrl = model.buildGeoapifyURL(autoCompleteTextView.text.toString())

            Log.i("FUNDEBUG", "I Gotted IT!")

            Log.i("FUNDEBUG3", placesUrl)

            val myPlaces = model.getPlaces(placesUrl)
            Thread.sleep(3_000)

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

            val placeStreetTV = binding.placeStreetTV
            placePhoneTV = binding.placePhoneTV
            val phoneRegex = Regex("[^A-Za-z0-9+ ]")

            placePhoneTV.text = placePhone?.let { it1 -> phoneRegex.replace(it1, "") }
            placeStreetTV.paintFlags = android.graphics.Paint.UNDERLINE_TEXT_FLAG
            placeStreetTV.text = placeStreet + " " +placeStreetNumber

            var openingHours = placeOpeningHours?.replace(",","\n")
            openingHours = openingHours?.replace(";","\n")

            binding.placeEmailTV.text = placeEmail
            binding.placeWebsiteTV.text = placeWebsite
            binding.placeOpeningHoursMT.setText(openingHours)

        /*
            Log.i("FUNDEBUG", "Random name: " + currentPlace.name)
            Log.i("FUNDEBUG", "Random street: " + currentPlace.street + currentPlace.housenumber)
            Log.i("FUNDEBUG", "Random postcode: " + currentPlace.postcode)
            Log.i("FUNDEBUG", "Random email: " + currentPlace.email)
            Log.i("FUNDEBUG", "Random phone: " + currentPlace.phone)
            Log.i("FUNDEBUG", "Random website: " + currentPlace.website)
            Log.i("FUNDEBUG", "Random openinghours: " + currentPlace.openinghours)
            Log.i("FUNDEBUG", "Random placeId: " + currentPlace.placeid)

        */

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

            val browserIntent = model.buildBrowserIntent(currentPlace.street + " " +currentPlace.housenumber, "https://www.google.com/maps/search/?api=1&query=")
            startActivity(browserIntent)

        }

        binding.placePhoneTV.setOnClickListener {

            val phoneNumber = placePhoneTV.text
            val intent = Intent(Intent.ACTION_CALL);
            intent.data = Uri.parse("tel:$phoneNumber")

            val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(CALL_PHONE),
                    MY_PERMISSIONS_REQUEST_CALL_PHONE
                )

                // MY_PERMISSIONS_REQUEST_CALL_PHONE is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            } else {
                //You already have permission
                try {
                    startActivity(intent)
                } catch (e: SecurityException) {
                    e.printStackTrace()
                }
            }
        }

        binding.placeEmailTV.setOnClickListener {

            val chooserTitle = "Email client"
            val emailIntent = model.buildEmailIntent(currentPlace.email, "Reservation", "")
            startActivity(Intent.createChooser(emailIntent, chooserTitle))

        }

        binding.placeWebsiteTV.setOnClickListener {

            val browserIntent = model.buildBrowserIntent("", currentPlace.website)

            try {
                startActivity(browserIntent)
            } catch (e: Exception) {

            }


        }

        }

    }

