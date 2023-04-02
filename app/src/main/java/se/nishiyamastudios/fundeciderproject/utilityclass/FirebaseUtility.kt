package se.nishiyamastudios.fundeciderproject.utilityclass

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import se.nishiyamastudios.fundeciderproject.dataclass.FirebaseFavoriteListObject

class  FirebaseUtility : ViewModel() {

    val favoritePlaces: MutableLiveData<List<FirebaseFavoriteListObject>> by lazy {
        MutableLiveData<List<FirebaseFavoriteListObject>>()
    }

    val blacklistPlaces: MutableLiveData<List<FirebaseFavoriteListObject>> by lazy {
        MutableLiveData<List<FirebaseFavoriteListObject>>()
    }

    val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun loadFavorites() {

        val database = Firebase.database
        val shopRef = database.getReference("funfavorite").child(Firebase.auth.currentUser!!.uid)

        Log.i("FUNDEBUG",database.toString())
        Log.i("FUNDEBUG",shopRef.toString())

        shopRef.get().addOnSuccessListener {
            val shoplist = mutableListOf<FirebaseFavoriteListObject>()
            it.children.forEach {childsnap ->
                val tempShop = childsnap.getValue<FirebaseFavoriteListObject>()!!
                Log.i("FUNDEBUG",tempShop.placename.toString())
                tempShop.fbid = childsnap.key
                shoplist.add(tempShop)
            }
            favoritePlaces.value = shoplist
        }

    }

    fun loadBlacklist() {

        val database = Firebase.database
        val shopRef = database.getReference("funblacklist").child(Firebase.auth.currentUser!!.uid)
        shopRef.get().addOnSuccessListener {
            val shoplist = mutableListOf<FirebaseFavoriteListObject>()
            it.children.forEach {childsnap ->
                val tempShop = childsnap.getValue<FirebaseFavoriteListObject>()!!
                tempShop.fbid = childsnap.key
                shoplist.add(tempShop)
            }
            blacklistPlaces.value = shoplist
        }

    }

    fun loadFirebaseList(firebasepathname : String, placelist : MutableLiveData<List<FirebaseFavoriteListObject>>) {
        val database = Firebase.database
        val listRef = database.getReference(firebasepathname).child(Firebase.auth.currentUser!!.uid)
        listRef.get().addOnSuccessListener {
            val favoriteList = mutableListOf<FirebaseFavoriteListObject>()
            it.children.forEach {childsnap ->
                val tempShop = childsnap.getValue<FirebaseFavoriteListObject>()!!
                tempShop.fbid = childsnap.key
                favoriteList.add(tempShop)
            }
            placelist.value = favoriteList
        }
    }

    fun addFavoriteItem(firebasepathname : String,
                        placename : String,
                        placestreet : String?,
                        placehousenumber : String?,
                        placepostcode : String?,
                        placephone : String?,
                        placeemail : String?,
                        placewebsite : String?,
                        placeopeninghours : String?,
                        placeid : String) {

        if(placename == "" || placestreet == null) {
            errorMessage.value = "Field cannot be empty, please get a new place."
            return
        }

        if(placeid == null) {
            errorMessage.value = "Place id cannot be empty, please get a new place."
            return
        }

        errorMessage.value = ""

        val tempPlaceItem = FirebaseFavoriteListObject(placename, placestreet, placehousenumber, placepostcode, placephone, placeemail, placewebsite, placeopeninghours, placeid)

        val database = Firebase.database
        val listRef = database.getReference(firebasepathname).child(Firebase.auth.currentUser!!.uid)
        listRef.push().setValue(tempPlaceItem).addOnCompleteListener {
            //loadShopping()
        }

        //loadShopping()

    }

    fun addBlacklistItem(firebasepathname : String, placename : String, placeid : String) {

        if(placename == "") {
            errorMessage.value = "Field cannot be empty, please get a new place."
            return
        }

        if(placeid == null) {
            errorMessage.value = "Place id cannot be empty, please get a new place."
            return
        }

        errorMessage.value = ""

        val tempPlaceItem = FirebaseFavoriteListObject(placename, placeid)

        val database = Firebase.database
        val listRef = database.getReference(firebasepathname).child(Firebase.auth.currentUser!!.uid)
        listRef.push().setValue(tempPlaceItem).addOnCompleteListener {
            //loadShopping()
        }

        //loadShopping()

    }

    fun deleteFavoriteItem(deleteitem : FirebaseFavoriteListObject) {
        val database = Firebase.database
        val listRef = database.getReference("funfavorite").child(Firebase.auth.currentUser!!.uid)

        listRef.child(deleteitem.fbid!!).removeValue().addOnCompleteListener {
            loadFavorites()
        }

    }

    fun deleteBlacklistItem(deleteitem : FirebaseFavoriteListObject) {
        val database = Firebase.database
        val listRef = database.getReference("funblacklist").child(Firebase.auth.currentUser!!.uid)

        listRef.child(deleteitem.fbid!!).removeValue().addOnCompleteListener {
            loadBlacklist()
        }

    }

}