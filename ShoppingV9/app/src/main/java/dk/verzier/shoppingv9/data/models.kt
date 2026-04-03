package dk.verzier.shoppingv9.data

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.google.firebase.firestore.DocumentId
import dk.verzier.shoppingv9.domain.Item
import dk.verzier.shoppingv9.domain.Shop
import kotlinx.serialization.Serializable

data class ItemDto(
    @DocumentId
    val id: String = "",
    val what: String = "",
    val where: String = "",
    val description: String = "",
    val deadline: String? = null,
    val imageUrl: String? = null
)

data class ShopDto(
    val name: String = "",
    val imageUrl: String = "",
    val brandColor: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val openingHour: Int = 7,
    val closingHour: Int = 22,
)

fun ItemDto.toItem(): Item = Item(
    id = this.id,
    what = this.what,
    where = this.where,
    description = this.description,
    deadline = this.deadline,
    imageUrl = this.imageUrl
)

fun ShopDto.toShop(): Shop = Shop(
    name = this.name,
    imageUrl = this.imageUrl,
    brandColor = Color(color = this.brandColor.toColorInt()),
    latitude = this.latitude,
    longitude = this.longitude,
    openingHour = this.openingHour,
    closingHour = this.closingHour,
)

@Serializable
data class FoodishImage(
    val image: String
)