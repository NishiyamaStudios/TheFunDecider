package se.nishiyamastudios.fundeciderproject.ui.favorites

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import se.nishiyamastudios.fundeciderproject.dataclass.FavoriteListObject
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility
import se.nishiyamastudios.fundeciderproject.utilityclass.IntentUtility
import se.nishiyamastudios.fundeciderproject.databinding.FragmentFavoritesBinding

class FavoritesFragment : Fragment() {

    private var _binding : FragmentFavoritesBinding? = null
    val binding get() = _binding!!

    val fbUtil = FirebaseUtility()
    private val intentUtil = IntentUtility()

    private var favoritesadapter = FavoritesAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        favoritesadapter.frag = this

        _binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val snackbarMessage: MutableLiveData<String> by lazy {
            MutableLiveData<String>()
        }

        val snackbarObserver = Observer<String> { mess ->
            Snackbar.make(requireView(), mess, Snackbar.LENGTH_LONG)
                .setAnchorView(binding.favoritePlaceStreetTV)
                .show()
        }

        snackbarMessage.observe(viewLifecycleOwner, snackbarObserver)

        binding.favoritesRV.adapter = favoritesadapter
        binding.favoritesRV.layoutManager = LinearLayoutManager(requireContext())

        val favoritesObserver = Observer<List<FavoriteListObject>> {
            favoritesadapter.notifyDataSetChanged()
        }

        fbUtil.favoritePlaces.observe(viewLifecycleOwner, favoritesObserver)

        fbUtil.loadFavorites()

        binding.linearLayoutFavoriteInfo.visibility = View.GONE

        binding.favoritePlaceStreetTV.setOnClickListener {

            val browserIntent = intentUtil.buildMapBrowserIntent(
                binding.favoritePlaceStreetTV.text.toString(),
                "https://www.google.com/maps/search/?api=1&query="
            )
            try {
                startActivity(browserIntent)
            } catch (e: Exception) {

                snackbarMessage.value = "The map cannot be opened."
            }
        }

        binding.favoritePlacePhoneTV.setOnClickListener {
            val phoneNumber = binding.favoritePlacePhoneTV.text
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$phoneNumber")

            val MY_PERMISSIONS_REQUEST_CALL_PHONE = 1

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.CALL_PHONE
                )
                != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(), arrayOf(Manifest.permission.CALL_PHONE),
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

        binding.favoritePlaceEmailTV.setOnClickListener {
            val email = binding.favoritePlaceEmailTV.text
            val chooserTitle = "Email client"
            val emailIntent =
                intentUtil.buildEmailIntent(email.chars().toString(), "Reservation", "")

            try {
                startActivity(Intent.createChooser(emailIntent, chooserTitle))
            } catch (e: Exception) {
                snackbarMessage.value = "Email cannot be accessed."
            }
        }

        binding.favoritePlaceWebsiteTV.setOnClickListener {
            val website = binding.favoritePlaceWebsiteTV.text
            val browserIntent = intentUtil.buildBrowserIntent(website.toString())

            try {
                startActivity(browserIntent)
            } catch (e: Exception) {
                snackbarMessage.value = "The website cannot be opened."
            }
        }
    }

}