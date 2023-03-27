package se.nishiyamastudios.fundeciderproject.ui.start

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.ui.text.substring
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import se.nishiyamastudios.fundeciderproject.PlaceDetails
import java.io.IOException

class StartViewModel : ViewModel() {

    var places = mutableListOf<PlaceDetails>()

    private val client = OkHttpClient()


    fun getPlaces(url: String): MutableList<PlaceDetails> {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.i("FUNDEBUG",e.toString())}
            override fun onResponse(call: Call, response: Response) {

                // Clear mutable list or else results get jumbled after changing category.
                places.clear()

                // Get API response as a JSON object.
                val jsonObject = JSONTokener(response.body()?.string()).nextValue() as JSONObject

                // Get the features array from the JSON object.
                val jsonArray = jsonObject.getJSONArray("features")

                // Get objects and properties from features array.
                for (i in 0 until jsonArray.length()) {

                    //
                    val properties = jsonArray.getJSONObject(i).getJSONObject("properties")
                    val dataSource = properties.getJSONObject("datasource")
                    val rawData = dataSource.getJSONObject("raw")

                    // Variables used in getPlacesResponse function are initialized empty
                    // to prevent crash if JSON value is not available and to not get false values
                    var name = ""
                    var street = ""
                    var houseNumber = ""
                    var postcode = ""
                    var phone = ""
                    var email = ""
                    var website = ""
                    var openingHours = ""
                    var placeId = ""

                    // Some places might not have all the data and since we are OK with this,
                    // this ugly try-catch ladder prevents a crash in such instances.
                    try {
                        name = properties.getString("name")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        street = properties.getString("street")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        houseNumber = properties.getString("housenumber")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        postcode = properties.getString("postcode")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        phone = rawData.getString("phone")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        email = rawData.getString("email")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        website = rawData.getString("website")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        openingHours = rawData.getString("opening_hours")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }
                    try {
                        placeId = properties.getString("place_id")
                    } catch (e: Exception) {
                        // Variable value is already set to empty if JSON attribute is missing.
                    }

                    // Temporary place holder before adding to our list of places.
                    val currentPlace = PlaceDetails(
                        name,
                        street,
                        houseNumber,
                        postcode,
                        phone,
                        email,
                        website,
                        openingHours,
                        placeId
                    )

                    Log.i("FUNDEBUG","currentPlaces name: " + currentPlace.name)
                    Log.i("FUNDEBUG","currentPlaces street: " + currentPlace.street)
                    Log.i("FUNDEBUG","currentPlaces housenumber: " + currentPlace.housenumber)
                    Log.i("FUNDEBUG","currentPlaces street: " + currentPlace.street)
                    Log.i("FUNDEBUG","currentPlaces placeid: " + currentPlace.placeid)

                    // Add the place to our mutable list of places.
                    places.add(currentPlace)


                /*
                    Log.i("FUNDEBUG","Namn: " + name)
                    Log.i("FUNDEBUG","Namn2: " + properties.toString())
                    Log.i("FUNDEBUG","Namn3: " + name3.toString())
                    Log.i("FUNDEBUG",jsonArray.toString())
                    Log.i("FUNDEBUG","My list: " +myMutableList[0])

                    Log.i("FUNDEBUG","Vänta här nu..")
                    Log.i("FUNDEBUG","dataSource: " + dataSource)
                    Log.i("FUNDEBUG","rawData: " + rawData)
                    //Log.i("FUNDEBUG","amenity: " + amenity)

                */

                }
                Log.i("FUNDEBUG", "Places name one: " + places.get(0).placeid + "\n " + "Places name two: " +places.get(1).placeid)
            }

        })

        return places
    }

    fun getRandomPlace(places: MutableList<PlaceDetails>): PlaceDetails {

        return try {
            places.shuffle()
            places[0]
        } catch (e : Exception) {
            Thread.sleep(1000)
            places.shuffle()
            places[0]
        }
    }

    fun buildGeoapifyURL(category: String): String {

        val geoapifyBaseURL = "https://api.geoapify.com/v2/places?categories="
        var geoapifyCategory = ""
        when (category) {
            "Restaurant" -> geoapifyCategory = "catering."+category.lowercase()
            "Bar" -> geoapifyCategory = "catering."+category.lowercase()
            "Pub" -> geoapifyCategory = "catering."+category.lowercase()
            "Cafe" -> geoapifyCategory = "catering."+category.lowercase()
            "Fast Food" -> geoapifyCategory = "catering.fast_food"
            "Entertainment" -> geoapifyCategory = category.lowercase()
            else -> ""
        }
        val geoapifyPlace = "&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit="
        val geoapifyLimit = "20"
        val geoapifyKey = "&apiKey=d357192221064b8da71d4143f306b152"
        val placesURL = geoapifyBaseURL+geoapifyCategory+geoapifyPlace+geoapifyLimit+geoapifyKey

        return placesURL
    }

    fun buildBrowserIntent(address: String?, url: String?): Intent {
        var newUrl = ""
        if (url != null) {
            if (url.substring(0,3) == "www") {
                newUrl = url.replace("www", "http://www")
            } else {
                newUrl = url
            }
        }
        if (address != "") {
            newUrl+address
        } else {
            newUrl
        }
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(newUrl))

        return intent
    }

    fun buildEmailIntent(toaddress: String?, subject: String, body: String): Intent {
        val uri = Uri.parse("mailto:"+toaddress)
            .buildUpon()
            .appendQueryParameter("subject", subject)
            .appendQueryParameter("body", body)
            .appendQueryParameter("to",toaddress)
            .build()
        val intent = Intent(Intent.ACTION_SENDTO, uri)

        return intent
    }

}