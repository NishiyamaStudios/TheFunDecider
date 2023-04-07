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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.utilityclass.IntentUtility
import se.nishiyamastudios.fundeciderproject.dataclass.PlaceDetails
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentStartBinding


class StartFragment : Fragment() {

    var _binding : FragmentStartBinding? = null
    val binding get() = _binding!!

    private lateinit var placePhoneTV: TextView
    private lateinit var myPlaces: MutableList<PlaceDetails>
    private lateinit var currentPlace: PlaceDetails

    private val model by viewModels<StartViewModel>()
    private val intentUtil = IntentUtility()
    private val fbUtil = FirebaseUtility()


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

        //TODO: Fixa så att detaljer i linear layout blir GONE om dem inte har något värde
        //TODO: Fixa så att man inte kan lägga till samma ställe i favoriter och blacklist flera gånger
        //TODO: Fixa så att snackbar dyker upp om man klickar på favorite, blacklist och share även om där inte finns något ställe valt
        //TODO: Errorhantering, refaktorering, snackbars
        //TODO: Välja stad? Hur funkar det? Kolla med hjälp av GPS i stället?
        //TODO: Skapa hjälpruta med setting för hur många resultat samt borttagning av konto.

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

        val subjects = arrayOf("Restaurant", "Bar", "Pub", "Cafe", "Fast Food", "Entertainment")

        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, subjects)
        autoCompleteTextView.setAdapter(adapter)


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

            Log.i("FUNDEBUG", "I Gotted IT!")

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

            var openingHours = placeOpeningHours?.replace(",","\n")
            openingHours = openingHours?.replace(";","\n")

            binding.placeEmailTV.text = placeEmail
            binding.placeWebsiteTV.text = placeWebsite
            binding.placeOpeningHoursMT.setText(openingHours)

            binding.linearLayout.visibility = View.VISIBLE
            binding.animationView.visibility = View.GONE

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

                if (myPlaces.isEmpty()) {
                    snackbarMessage.value = "Could not get any more places, you blacklisted them all?!"
                    binding.getPlacesButton.isClickable = false
                } else {
                    currentPlace = model.getRandomPlace(myPlaces)
                    binding.getPlacesButton.isClickable = true
                }

                binding.getPlacesButton.text = "Find your new favorite $category!"
                binding.animationViewInfo.visibility = View.VISIBLE
            }

            binding.addFavoriteButton.setOnClickListener {

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

                    fbUtil.addFavoriteItem("funfavorite", placeName, placeStreet, placeHouseNumber, placePostCode, placePhone, placeEmail,placeWebsite, placeOpeningHours, placeId)
                    fbUtil.loadFavorites()
                }
            }

            binding.addBlacklistButton.setOnClickListener {

                if (binding.selectedPlaceTV.text != "") {
                    val placeName = currentPlace.name
                    val placeId = currentPlace.placeid

                    fbUtil.addBlacklistItem("funblacklist", placeName, placeId)
                    myPlaces.remove(currentPlace)
                    fbUtil.loadBlacklist()
                }
            }

        binding.placeStreetTV.setOnClickListener {

            val browserIntent = intentUtil.buildMapBrowserIntent(currentPlace.street + " " +currentPlace.housenumber, "https://www.google.com/maps/search/?api=1&query=")
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

            try {
             startActivity(Intent.createChooser(intentUtil.sharePlace(currentPlace.name,"", "Hey! Share this destiny with me!\n" +
                     "Let's check out"), "Share using"))
            } catch (e: Exception) {
                snackbarMessage.value = "The place could not be shared."
            }

        }

        }

    }

