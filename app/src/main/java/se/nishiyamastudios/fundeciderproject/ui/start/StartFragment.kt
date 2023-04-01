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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var myPlaces: MutableList<PlaceDetails>
    private lateinit var currentPlace: PlaceDetails
    private lateinit var placesUrl: String

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

        //TODO: Spara detaljer till databasen, fixa så att man kan få upp dessa i en detaljvy
        // från favoriter
        //TODO: Fixa en infoknapp på startfragment(?)
        //TODO: Fixa en huvudanimation i mitten
        //TODO: Fixa fler animationer till kategorierna
        //TODO: Fixa så att detaljer i linear layout blir GONE om dem inte har något värde
        //TODO: Fixa så att man inte kan lägga till samma ställe i favoriter och blacklist flera gånger
        //TODO: Fixa så att ställen från blacklist inte dyker upp i resultaten
        //TODO: Fixa så att snackbar dyker upp om man klickar på favorite, blacklist och share
        // även om där inte finns något ställe valt

        // Hide elements on creation
        binding.linearLayout.visibility = View.GONE
        binding.animationViewInfo.visibility = View.GONE

        //observera vårt felmeddelande
        val errorObserver  = Observer<String> {errorMess ->
            //Vad skall hända när det kommer ett felmeddelande
            Toast.makeText(requireContext(),errorMess, Toast.LENGTH_LONG).show()
        }

        model.errorMessage.observe(viewLifecycleOwner, errorObserver)

        val snackbarMessage: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        //TODO: Rensa upp detta, vad behövs?

        val snackbarObserver  = Observer<String> {mess ->
            Snackbar.make(requireView(),mess,Snackbar.LENGTH_LONG)
                .setAnchorView(binding.AutoCompleteTextview)
                .show()
        }

        snackbarMessage.observe(viewLifecycleOwner, snackbarObserver)


        val autoCompleteTextView = binding.AutoCompleteTextview

        val Subjects = arrayOf("Restaurant", "Bar", "Pub", "Cafe", "Fast Food", "Entertainment")

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, Subjects)
        autoCompleteTextView.setAdapter(adapter)

        Log.i("FUNDEBUG4",autoCompleteTextView.text.toString())


        // Set bottom navigation view to visible after logging in
        val activity  = view.context as? AppCompatActivity
        if (activity != null) {
            val navView = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.visibility = View.VISIBLE
        }

        binding.getPlacesButton.setOnClickListener {

            if (autoCompleteTextView.text.toString() == "") {
                snackbarMessage.value = "Choose a category from the drop down menu!"
                return@setOnClickListener
            }

            if (binding.AutoCompleteTextview.text.toString() != "") {
                currentPlace = model.getRandomPlace(myPlaces)
            }
            //val placesUrl = model.buildGeoapifyURL(autoCompleteTextView.text.toString())

            Log.i("FUNDEBUG", "I Gotted IT!")

            //Log.i("FUNDEBUG3", placesUrl)

            //val myPlaces = model.getPlaces(placesUrl)
            //model.sleep()
            //val myRandomPlace = model.getRandomPlace(myPlaces)
            //val myRandomPlace = model.getRandomPlace(placesUrl)
            //currentPlace = myRandomPlace as PlaceDetails

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

            binding.linearLayout.visibility = View.VISIBLE
            binding.animationView.visibility = View.GONE

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
                val placesUrl = model.buildGeoapifyURL(autoCompleteTextView.text.toString())
                binding.linearLayout.visibility = View.GONE
                binding.animationView.setAnimation(model.selectAnimation(autoCompleteTextView.text.toString()))
                binding.animationView.visibility = View.VISIBLE
                binding.animationView.playAnimation()
                myPlaces = model.getPlaces(placesUrl)
                currentPlace = model.getRandomPlace(myPlaces)
                binding.getPlacesButton.setText("Find your new favorite " + category +"!")
                binding.animationViewInfo.visibility = View.VISIBLE
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

            val browserIntent = model.buildMapBrowserIntent(currentPlace.street + " " +currentPlace.housenumber, "https://www.google.com/maps/search/?api=1&query=")
            try {
                startActivity(browserIntent)
            } catch (e: Exception) {
                snackbarMessage.value = "The website cannot be opened."
            }
        }

        binding.placePhoneTV.setOnClickListener {

            val phoneNumber = placePhoneTV.text
            val intent = Intent(Intent.ACTION_CALL)
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

            try {
                startActivity(Intent.createChooser(emailIntent, chooserTitle))
            } catch (e: Exception) {
                snackbarMessage.value = "Email cannot be accessed."
            }

        }

        binding.placeWebsiteTV.setOnClickListener {

            val browserIntent = model.buildBrowserIntent(currentPlace.website)

            try {
                startActivity(browserIntent)
            } catch (e: Exception) {
                snackbarMessage.value = "The website cannot be opened."
            }
        }

        binding.shareButton.setOnClickListener {

            try {
             startActivity(Intent.createChooser(model.sharePlace(currentPlace.name), "Share using"))
            } catch (e: Exception) {
                snackbarMessage.value = "The place could not be shared."
            }

        }

        }

    }

