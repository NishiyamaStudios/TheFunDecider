package se.nishiyamastudios.fundeciderproject.dataclass

// Class used when saving and fetching favorite places from Firebase
data class FavoriteListObject(val placename: String? = null,
                              val placestreet: String? = null,
                              val housenumber: String? = null,
                              val postcode: String? = null,
                              val phone: String? = null,
                              val email: String? = null,
                              val website: String? = null,
                              val openinghours: String? = null,
                              val placeid : String? = null) {

    var fbid : String? = null

}