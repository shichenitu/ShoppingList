package dk.verzier.shoppingv8.data

import androidx.compose.ui.graphics.Color
import androidx.core.graphics.toColorInt
import com.google.firebase.firestore.DocumentId
import dk.verzier.shoppingv8.domain.Item
import dk.verzier.shoppingv8.domain.Shop
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
    val brandColor: String = ""
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
    name = this.name, imageUrl = this.imageUrl, brandColor = Color(
        color = this.brandColor.toColorInt()
    )
)

@Serializable
data class FoodishImage(
    val image: String
)