package dk.itu.nearesttoilet.domain

data class PublicToilet(
    val id: String,
    val name: String,
    val address: String,
    val isHandicapAccessible: Boolean,
    val latitude: Double,
    val longitude: Double
)