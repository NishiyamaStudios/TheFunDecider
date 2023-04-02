package se.nishiyamastudios.fundeciderproject.dataclass

data class FirebaseFavoriteListObject(val placename: String? = null,
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