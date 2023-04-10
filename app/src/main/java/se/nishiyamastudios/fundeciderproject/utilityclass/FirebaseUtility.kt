package se.nishiyamastudios.fundeciderproject.utilityclass

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import se.nishiyamastudios.fundeciderproject.dataclass.BlackListObject
import se.nishiyamastudios.fundeciderproject.dataclass.FavoriteListObject

class  FirebaseUtility {

    val favoritePlaces: MutableLiveData<List<FavoriteListObject>> by lazy {
        MutableLiveData<List<FavoriteListObject>>()
    }

    val blacklistPlaces: MutableLiveData<List<BlackListObject>> by lazy {
        MutableLiveData<List<BlackListObject>>()
    }

    private val errorMessage: MutableLiveData<String> by lazy {
        MutableLiveData<String>()
    }

    fun loadFavorites(): MutableLiveData<List<FavoriteListObject>> {

        val database = Firebase.database
        val favoritesRef = database.getReference("funfavorite").child(Firebase.auth.currentUser!!.uid)

        Log.i("FUNDEBUG",database.toString())
        Log.i("FUNDEBUG",favoritesRef.toString())

        favoritesRef.get().addOnSuccessListener {
            val favoriteList = mutableListOf<FavoriteListObject>()
            it.children.forEach {childsnap ->
                val tempFavorite = childsnap.getValue<FavoriteListObject>()!!
                Log.i("FUNDEBUG",tempFavorite.placename.toString())
                tempFavorite.fbid = childsnap.key
                favoriteList.add(tempFavorite)
            }
            favoritePlaces.value = favoriteList
        }
        return  favoritePlaces
    }

    fun loadBlacklist(): MutableLiveData<List<BlackListObject>> {

        val database = Firebase.database
        val blacklistRef = database.getReference("funblacklist").child(Firebase.auth.currentUser!!.uid)
        blacklistRef.get().addOnSuccessListener {
            val blackList = mutableListOf<BlackListObject>()
            it.children.forEach {childsnap ->
                val tempBlacklist = childsnap.getValue<BlackListObject>()!!
                tempBlacklist.fbid = childsnap.key
                blackList.add(tempBlacklist)
            }
            blacklistPlaces.value = blackList
        }

        return blacklistPlaces

    }

    fun loadFirebaseList(firebasepathname : String, placelist : MutableLiveData<List<FavoriteListObject>>) {
        val database = Firebase.database
        val listRef = database.getReference(firebasepathname).child(Firebase.auth.currentUser!!.uid)
        listRef.get().addOnSuccessListener {
            val favoriteList = mutableListOf<FavoriteListObject>()
            it.children.forEach {childsnap ->
                val tempBlacklist = childsnap.getValue<FavoriteListObject>()!!
                tempBlacklist.fbid = childsnap.key
                favoriteList.add(tempBlacklist)
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

        val tempPlaceItem = FavoriteListObject(placename, placestreet, placehousenumber, placepostcode, placephone, placeemail, placewebsite, placeopeninghours, placeid)

        val database = Firebase.database
        val listRef = database.getReference(firebasepathname).child(Firebase.auth.currentUser!!.uid)
        listRef.push().setValue(tempPlaceItem).addOnCompleteListener {
        }
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

        val tempPlaceItem = FavoriteListObject(placename, placeid)

        val database = Firebase.database
        val listRef = database.getReference(firebasepathname).child(Firebase.auth.currentUser!!.uid)
        listRef.push().setValue(tempPlaceItem).addOnCompleteListener {
        }
    }

    fun deleteFavoriteItem(deleteitem : FavoriteListObject) {
        val database = Firebase.database
        val listRef = database.getReference("funfavorite").child(Firebase.auth.currentUser!!.uid)
        listRef.child(deleteitem.fbid!!).removeValue().addOnCompleteListener {
            loadFavorites()
        }

    }

    fun deleteBlacklistItem(deleteitem: BlackListObject) {
        val database = Firebase.database
        val listRef = database.getReference("funblacklist").child(Firebase.auth.currentUser!!.uid)

        listRef.child(deleteitem.fbid!!).removeValue().addOnCompleteListener {
            loadBlacklist()
        }

    }

    fun deleteSavedUserData() {
        val database = Firebase.database
        val favoriteRef = database.getReference("funfavorite").child(Firebase.auth.currentUser!!.uid)
        val blacklistRef = database.getReference("funblacklist").child(Firebase.auth.currentUser!!.uid)

        if (favoriteRef != null) {
            favoriteRef.removeValue()
        }

        if (blacklistRef != null) {
            blacklistRef.removeValue()
        }
    }

}