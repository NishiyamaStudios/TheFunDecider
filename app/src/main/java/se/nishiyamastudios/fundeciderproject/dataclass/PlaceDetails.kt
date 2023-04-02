package se.nishiyamastudios.fundeciderproject.dataclass

// Class used to capture data from GeoApify API calls
data class PlaceDetails(
    val name: String,
    val street: String?,
    val housenumber: String?,
    val postcode: String?,
    val phone: String?,
    val email: String?,
    val website: String?,
    val openinghours: String?,
    val placeid: String
)