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
import se.nishiyamastudios.fundeciderproject.PlaceDetails
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.databinding.FragmentStartBinding
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

        val myplaces = model.getPlacesResponse("https://api.geoapify.com/v2/places?categories=catering.restaurant&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit=2&apiKey=d357192221064b8da71d4143f306b152")

        val placesObserver = Observer<List<Places>> {
            binding.selectedPlaceTV.text = myplaces.get(0).name
        }

        // Set bottom navigation view to visible after logging in
        val activity  = view.context as? AppCompatActivity
        if (activity != null) {
            val navView = activity.findViewById<BottomNavigationView>(R.id.bottom_navigation_view)
            navView.visibility = View.VISIBLE
        }

        /*
        val client: OkHttpClient = OkHttpClient().newBuilder()
            .build()
        val request: Request = OkHttpClient.Builder()
            .url("https://api.geoapify.com/v2/place-details?id=id%3D514d368a517c511e40594bfd7b574ec84740f00103f90135335d1c00000000920313416e61746f6d697363686573204d757365756d&apiKey=a672cf88394141c290c00df4ebaf739f")
            .method("GET", null)
            .build()
        val response: Response = client.newCall(request).execute()

         */

        binding.getPlacesButton.setOnClickListener {
            //model.getPlaces("https://api.geoapify.com/v2/places?categories=catering.restaurant&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit=2&apiKey=d357192221064b8da71d4143f306b152")
            Log.i("FUNDEBUG", "I Gotted IT!")

            myplaces.shuffle()
            binding.selectedPlaceTV.text = myplaces.get(0).placeid

        }


    }

}