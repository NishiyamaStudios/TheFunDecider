package se.nishiyamastudios.fundeciderproject.ui.start

import android.util.Log
import androidx.compose.ui.text.substring
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

    // Variables used in getPlacesResponse function are initialized empty to prevent crash if JSON value is not available
    var name = ""
    var street = ""
    var houseNumber = ""
    var postcode = ""
    var phone = ""
    var email = ""
    var website = ""
    var openingHours = ""
    var placeId = ""

    /*
    fun getPlaces(url: String) {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.i("FUNDEBUG",e.toString())}
            override fun onResponse(call: Call, response: Response) = println(response.body()?.string())

        })
    }

     */

    fun getPlacesResponse(url: String): MutableList<PlaceDetails> {
        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) { Log.i("FUNDEBUG",e.toString())}
            override fun onResponse(call: Call, response: Response) {

                val jsonObject = JSONTokener(response.body()?.string()).nextValue() as JSONObject

                Log.i("FUNDEBUG","JSON-Object: " + jsonObject.toString())
                val jsonArray = jsonObject.getJSONArray("features")

                Log.i("FUNDEBUG","JSON-Array: " + jsonArray.toString())


                for (i in 0 until jsonArray.length()) {

                    val properties = jsonArray.getJSONObject(i).getJSONObject("properties")
                    val dataSource = properties.getJSONObject("datasource")
                    val rawData = dataSource.getJSONObject("raw")

                    // Some places might not have all the data, this prevents a crash in such instances
                    try {
                        name = properties.getString("name")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        street = properties.getString("street")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        houseNumber = properties.getString("housenumber")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        postcode = properties.getString("postcode")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        phone = rawData.getString("phone")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        email = rawData.getString("email")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        website = rawData.getString("website")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        openingHours = rawData.getString("opening_hours")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }
                    try {
                        placeId = properties.getString("place_id")
                    } catch (e: Exception) {
                        // Variable value is set to empty if JSON attribute is missing.
                    }

                    //myMutableList.add(name3)

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

}