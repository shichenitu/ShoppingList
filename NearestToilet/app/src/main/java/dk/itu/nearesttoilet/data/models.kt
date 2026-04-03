package dk.itu.nearesttoilet.data

import dk.itu.nearesttoilet.domain.PublicToilet
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FrederiksbergFeatureCollection(
    val type: String,
    val features: List<FrederiksbergFeature>
)

@Serializable
data class FrederiksbergFeature(
    val type: String,
    val geometry: FrederiksbergGeometry,
    val properties: FrederiksbergToiletProperties
)

@Serializable
data class FrederiksbergGeometry(
    val type: String,
    val coordinates: List<Double>
)

@Serializable
data class FrederiksbergToiletProperties(
    val id: Int,

    @SerialName("navn")
    val name: String? = null,

    @SerialName("ejer")
    val owner: String? = null,

    @SerialName("adresse")
    val address: String? = null,

    @SerialName("nr")
    val number: Int? = null,

    @SerialName("handikapvenlig")
    val isHandicapFriendly: String? = null,

    @SerialName("åbningstid")
    val openingHours: String? = null,

    @SerialName("betaling")
    val requiresPayment: String? = null
)

@Serializable
data class CphFeatureCollection(
    val type: String,
    val features: List<CphFeature>
)

@Serializable
data class CphFeature(
    val type: String,
    val geometry: CphGeometry,
    val properties: CphToiletProperties
)

@Serializable
data class CphGeometry(
    val type: String,
    val coordinates: List<Double>
)

@Serializable
data class CphToiletProperties(
    val id: Int,

    @SerialName("toilet_lokalitet")
    val locationName: String? = null,

    @SerialName("vejnavn_husnummer")
    val streetAndNumber: String? = null,

    @SerialName("postnummer")
    val zipCode: String? = null,

    @SerialName("handicapadgang")
    val handicapAccess: String? = null,

    @SerialName("aabningstid_doegn")
    val openingHours: String? = null,

    @SerialName("status")
    val status: String? = null
)

// Map Copenhagen's JSON to your Domain model
fun CphFeature.toPublicToilet(): PublicToilet {
    return PublicToilet(
        id = "CPH_${this.properties.id}",
        name = this.properties.locationName ?: "Public Toilet",
        address = this.properties.streetAndNumber ?: "Unknown Address",
        // The API still returns the Danish string "Ja", so we check for that
        isHandicapAccessible = this.properties.handicapAccess.equals("Ja", ignoreCase = true),
        latitude = this.geometry.coordinates[1],
        longitude = this.geometry.coordinates[0]
    )
}

// Map Frederiksberg's JSON to your Domain model
fun FrederiksbergFeature.toPublicToilet(): PublicToilet {
    return PublicToilet(
        id = "FRB_${this.properties.id}",
        name = this.properties.name ?: "Public Toilet",
        address = this.properties.address ?: "Unknown Address",
        // Frederiksberg uses lowercase "ja"
        isHandicapAccessible = this.properties.isHandicapFriendly.equals("ja", ignoreCase = true),
        latitude = this.geometry.coordinates[1],
        longitude = this.geometry.coordinates[0]
    )
}