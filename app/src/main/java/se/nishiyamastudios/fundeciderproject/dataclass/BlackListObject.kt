package se.nishiyamastudios.fundeciderproject.dataclass

// Class used when saving and fetching black listed places from Firebase
data class BlackListObject(val placename: String? = null, val placeid : String? = null) {

    var fbid : String? = null

}