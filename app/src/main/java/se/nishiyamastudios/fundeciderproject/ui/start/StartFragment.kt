package se.nishiyamastudios.fundeciderproject.ui.start

import android.Manifest
import android.Manifest.permission.CALL_PHONE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.os.Looper
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
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentStartBinding
import se.nishiyamastudios.fundeciderproject.dataclass.PlaceDetails
import se.nishiyamastudios.fundeciderproject.ui.help.HelpFragment
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.utilityclass.IntentUtility
import se.nishiyamastudios.fundeciderproject.utilityclass.LocationUtility


class StartFragment : Fragment() {

    private var _binding: FragmentStartBinding? = null
    val binding get() = _binding!!

    private lateinit var placePhoneTV: TextView
    private lateinit var myPlaces: MutableList<PlaceDetails>
    private lateinit var currentPlace: PlaceDetails

    private val model by viewModels<StartViewModel>()
    private val intentUtil = IntentUtility()
    private val fbUtil = FirebaseUtility()
    private val locationUtil = LocationUtility()

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

        //TODO: Errorhantering, refaktorering, snackbars, kommentera kod
        //TODO: Man borde inte kunna lägga till som favorite eller blacklist om dem redan finns i någon av listorna?
        //TODO: Förbättring: Lägg in setting för hur många resultat man skall hämta från API.
        //TODO: Skapa README på github

        val activity  = view.context as? AppCompatActivity

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                val lastLocation: Location? = p0.lastLocation
                if (lastLocation != null) {

                    try {
                        // Did not figure out a better way to access these values
                        binding.latitudeTV.setText(lastLocation.latitude.toString())
                        binding.longitudeTV.setText(lastLocation.longitude.toString())
                    } catch (e : Exception) {
                        // Do nothing, just catching issue with the binding when logging out.
                    }


                }
            }
        }

        val fusedLocation = LocationServices.getFusedLocationProviderClient(requireActivity())
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        fusedLocation.requestLocationUpdates(
            locationUtil.RequestLocation(30000, 3000, 100),
            locationCallback,
            Looper.myLooper()
        )

        // Hide elements on creation
        binding.linearLayout.visibility = View.GONE
        binding.animationViewInfo.visibility = View.GONE

        //observera vårt felmeddelande
        val errorObserver = Observer<String> { errorMess ->
            //Vad skall hända när det kommer ett felmeddelande
            Toast.makeText(requireContext(), errorMess, Toast.LENGTH_LONG).show()
        }

        model.errorMessage.observe(viewLifecycleOwner, errorObserver)

        val snackbarMessage: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        val snackbarObserver = Observer<String> { mess ->
            Snackbar.make(requireView(), mess, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.AutoCompleteTextview)
                .show()
        }

        snackbarMessage.observe(viewLifecycleOwner, snackbarObserver)

        val autoCompleteTextView = binding.AutoCompleteTextview

        val subjects = arrayOf("Restaurant", "Bar", "Pub", "Cafe", "Fast Food", "Entertainment")

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, subjects)
        autoCompleteTextView.setAdapter(adapter)


        // Set bottom navigation view to visible after logging in
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

            // Make sure textviews are visible, needed if there were empty values in the previous place details
            binding.placeStreetTV.visibility = View.VISIBLE
            binding.placePhoneTV.visibility = View.VISIBLE
            binding.placeEmailTV.visibility = View.VISIBLE
            binding.placeWebsiteTV.visibility = View.VISIBLE
            binding.placeOpeningHoursMT.visibility = View.VISIBLE

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
            val phoneRegex = Regex("[^A-Za-z\\d+ ]")

            placePhoneTV.text = placePhone?.let { it1 -> phoneRegex.replace(it1, "") }
            placeStreetTV.paintFlags = android.graphics.Paint.UNDERLINE_TEXT_FLAG
            placeStreetTV.text = "$placeStreet $placeStreetNumber"

            var openingHours = placeOpeningHours?.replace(",", "\n")
            openingHours = openingHours?.replace(";", "\n")

            binding.placeEmailTV.text = placeEmail
            binding.placeWebsiteTV.text = placeWebsite
            binding.placeOpeningHoursMT.setText(openingHours)

            // Show place layout and remove main animation
            binding.linearLayout.visibility = View.VISIBLE
            binding.animationView.visibility = View.GONE

            // Remove place details if they are empty
            if (binding.placeStreetTV.text.toString() == null || binding.placeStreetTV.text.toString() == "") {
                binding.placeStreetTV.visibility = View.GONE
            }
            if (binding.placePhoneTV.text.toString() == null || binding.placePhoneTV.text.toString() == "") {
                binding.placePhoneTV.visibility = View.GONE
            }
            if (binding.placeEmailTV.text.toString() == null || binding.placeEmailTV.text.toString() == "") {
                binding.placeEmailTV.visibility = View.GONE
            }
            if (binding.placeWebsiteTV.text.toString() == null || binding.placeWebsiteTV.text.toString() == "") {
                binding.placeWebsiteTV.visibility = View.GONE
            }
            if (binding.placeOpeningHoursMT.text.toString() == null || binding.placeOpeningHoursMT.text.toString() == "") {
                binding.placeOpeningHoursMT.visibility = View.GONE
            }
        }

        binding.AutoCompleteTextview.onItemClickListener =
            OnItemClickListener { parent, view, position, id ->

                val myLocation =
                    binding.longitudeTV.text.toString() + "," + binding.latitudeTV.text.toString()
                val category = autoCompleteTextView.text.toString().lowercase()
                //val placesUrl = model.buildGeoapifyURL(autoCompleteTextView.text.toString())
                val placesUrl = model.buildGeoapifyURLWithLatAndLong(
                    autoCompleteTextView.text.toString(),
                    myLocation,
                    "5000"
                )
                binding.linearLayout.visibility = View.GONE
                binding.animationView.setAnimation(model.selectAnimation(autoCompleteTextView.text.toString()))
                binding.animationView.visibility = View.VISIBLE
                binding.animationView.playAnimation()
                myPlaces = model.getPlaces(placesUrl)

                if (myPlaces.isEmpty()) {
                    snackbarMessage.value =
                        "Could not get any places."
                    binding.getPlacesButton.isClickable = false
                } else {
                    currentPlace = model.getRandomPlace(myPlaces)
                    binding.getPlacesButton.isClickable = true
                }

                binding.getPlacesButton.text = "Find your new favorite $category!"
                binding.animationViewInfo.visibility = View.VISIBLE
            }

        binding.addFavoriteButton.setOnClickListener {

            if (autoCompleteTextView.text.toString() == "") {
                snackbarMessage.value = "You need to find a place first."
                return@setOnClickListener
            }

            val placeNameTV = binding.selectedPlaceTV.text

            if (fbUtil.loadFavorites().value.toString().contains(placeNameTV)) {
                snackbarMessage.value = "$placeNameTV is already a favorite."
            } else {

                if (binding.selectedPlaceTV.text != "") {
                    val placeName = currentPlace.name
                    val placeId = currentPlace.placeid
                    val placeStreet = currentPlace.street
                    val placeHouseNumber = currentPlace.housenumber
                    val placePostCode = currentPlace.postcode
                    val placePhone = currentPlace.phone
                    val placeEmail = currentPlace.email
                    val placeWebsite = currentPlace.website
                    val placeOpeningHours = currentPlace.openinghours

                    fbUtil.addFavoriteItem(
                        "funfavorite",
                        placeName,
                        placeStreet,
                        placeHouseNumber,
                        placePostCode,
                        placePhone,
                        placeEmail,
                        placeWebsite,
                        placeOpeningHours,
                        placeId
                    )
                    fbUtil.loadFavorites()
                }

            }
        }

        binding.addBlacklistButton.setOnClickListener {

            if (autoCompleteTextView.text.toString() == "") {
                snackbarMessage.value = "You need to find a place first."
                return@setOnClickListener
            }

            val placeNameTV = binding.selectedPlaceTV.text

            if (fbUtil.loadBlacklist().value.toString().contains(placeNameTV)) {
                snackbarMessage.value = "$placeNameTV is already blacklisted."
            } else {

                if (binding.selectedPlaceTV.text != "") {
                    val placeName = currentPlace.name
                    val placeId = currentPlace.placeid

                    fbUtil.addBlacklistItem("funblacklist", placeName, placeId)
                    myPlaces.remove(currentPlace)
                    fbUtil.loadBlacklist()
                }
            }
        }

        binding.placeStreetTV.setOnClickListener {

            val browserIntent = intentUtil.buildMapBrowserIntent(
                currentPlace.street + " " + currentPlace.housenumber,
                "https://www.google.com/maps/search/?api=1&query="
            )
            try {
                startActivity(browserIntent)
            } catch (e: Exception) {
                snackbarMessage.value = "The map cannot be opened."
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
            val emailIntent = intentUtil.buildEmailIntent(currentPlace.email, "Reservation", "")

            try {
                startActivity(Intent.createChooser(emailIntent, chooserTitle))
            } catch (e: Exception) {
                snackbarMessage.value = "Email cannot be accessed."
            }

        }

        binding.placeWebsiteTV.setOnClickListener {

            val browserIntent = intentUtil.buildBrowserIntent(currentPlace.website)

            try {
                startActivity(browserIntent)
            } catch (e: Exception) {
                snackbarMessage.value = "The website cannot be opened."
            }
        }

        binding.shareButton.setOnClickListener {

            if (autoCompleteTextView.text.toString() == "") {
                snackbarMessage.value = "You need to find a place first."
                return@setOnClickListener
            }

            try {
                startActivity(
                    Intent.createChooser(
                        intentUtil.sharePlace(
                            currentPlace.name, "", "Hey! Share this destiny with me!\n" +
                                    "Let's check out"
                        ), "Share using"
                    )
                )
            } catch (e: Exception) {
                snackbarMessage.value = "The place could not be shared."
            }

        }

        binding.animationViewInfo.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.add(R.id.fragNavCon, HelpFragment())
                ?.addToBackStack("Help")?.commit()
        }


        binding.animationView.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.add(R.id.fragNavCon, HelpFragment())
                ?.addToBackStack("Help")?.commit()
        }

        // Request permission to access location
        // Code placed here to avoid issues with loading start fragment after accepting permission
        val PERMISSION_ID = 1
        ActivityCompat.requestPermissions(
            requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }
}

