package se.nishiyamastudios.fundeciderproject.ui.start

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import okhttp3.*
import org.json.JSONObject
import org.json.JSONTokener
import se.nishiyamastudios.fundeciderproject.dataclass.PlaceDetails
import se.nishiyamastudios.fundeciderproject.R
import se.nishiyamastudios.fundeciderproject.dataclass.FirebaseBlackListObject
import se.nishiyamastudios.fundeciderproject.utilityclass.FirebaseUtility
import java.io.IOException
import java.util.concurrent.CountDownLatch

class StartViewModel : ViewModel() {

    var places = mutableListOf<PlaceDetails>()

    val fbUtil = FirebaseUtility()
    var blackListedPlaces = fbUtil.loadBlacklist()
    val blacklistNameList = mutableListOf<String>()

    private val client = OkHttpClient()

    //live data till felmeddelande som vi kan lyssna på i LoginFragment
    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun getListOfBlacklistedPlaceNames(blacklistedplaces : MutableLiveData<List<FirebaseBlackListObject>>): MutableList<String> {
        for (i in 0 until blacklistedplaces.value?.size!!.toInt()) {
            val name = blacklistedplaces.value!![i].placename
            blacklistNameList.add(name!!)
        }

        return blacklistNameList
    }




    fun getPlaces(url: String): MutableList<PlaceDetails> {

        blackListedPlaces = fbUtil.loadBlacklist()
        var listOfBlacklistedPlaces = getListOfBlacklistedPlaceNames(blackListedPlaces)

        val request = Request.Builder()
            .url(url)
            .build()

        val countDownLatch = CountDownLatch(1)

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

                    // Add the place to the list of places if it's not blacklisted.
                    if (currentPlace.name !in listOfBlacklistedPlaces) {
                        Log.i("FUNDEBUGBLACKLISTCHECK", "${currentPlace.name} not in blacklist!")
                        places.add(currentPlace)
                    }
                }

                countDownLatch.countDown()
            }

        })

            countDownLatch.await()
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
            "Restaurant" -> geoapifyCategory = "catering." + category.lowercase()
            "Bar" -> geoapifyCategory = "catering." + category.lowercase()
            "Pub" -> geoapifyCategory = "catering." + category.lowercase()
            "Cafe" -> geoapifyCategory = "catering." + category.lowercase()
            "Fast Food" -> geoapifyCategory = "catering.fast_food"
            "Entertainment" -> geoapifyCategory = category.lowercase()
        }
        val geoapifyPlace =
            "&filter=place:51fab165f6780b2a4059a2e9e94ccbcb4b40f00101f901f3b6a20000000000c002069203064d616c6dc3b6&limit="
        val geoapifyLimit = "20"
        val geoapifyKey = "&apiKey=d357192221064b8da71d4143f306b152"

        return geoapifyBaseURL + geoapifyCategory + geoapifyPlace + geoapifyLimit + geoapifyKey
    }

    fun sharePlace(placename: String): Intent {

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val body = "Hey! Share this destiny with me!"
        val sub = "Hey! Share this destiny with me!\nLet's check out $placename together.. :D"
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.putExtra(Intent.EXTRA_TEXT, sub)

        return intent

    }

    fun selectAnimation(category: String): Int {
        var animationInt: Int = R.raw.animation_welcome
        //var currentCategory = category.replace(" ","")
        //currentCategory = currentCategory.lowercase()

        when (category) {
            "Restaurant" -> animationInt = R.raw.animation_restaurant
            "Bar" -> animationInt = R.raw.animation_bar
            "Pub" -> animationInt = R.raw.animation_pub
            "Cafe" -> animationInt = R.raw.animation_cafe
            "Fast Food" -> animationInt = R.raw.animation_fastfood
            "Entertainment" -> animationInt = R.raw.animation_entertainment
        }

        return animationInt
    }
}
