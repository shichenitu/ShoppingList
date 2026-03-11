package dk.verzier.shoppingv7.data

import androidx.compose.ui.graphics.Color
import dk.verzier.shoppingv7.domain.Item
import dk.verzier.shoppingv7.domain.Shop
import java.util.UUID
import androidx.core.graphics.toColorInt
import dk.verzier.shoppingv7.data.database.ItemEntity
import dk.verzier.shoppingv7.data.database.ShopEntity
import kotlinx.serialization.Serializable

data class ItemDto(
    val id: String = UUID.randomUUID().toString(),
    val what: String,
    val where: String,
    val description: String = "",
    val deadline: String? = null,
    val imageUrl: String? = null
)

data class ShopDto(
    val name: String,
    val imageUrl: String,
    val brandColor: String
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

fun ItemDto.toEntity() = ItemEntity(
    id = id,
    what = what,
    where = where,
    description = description,
    deadline = deadline,
    imageUrl = imageUrl
)

fun ItemEntity.toItemDto() = ItemDto(
    id = id,
    what = what,
    where = where,
    description = description,
    deadline = deadline,
    imageUrl = imageUrl
)

fun ShopEntity.toShopDto() = ShopDto(
    name = name,
    imageUrl = imageUrl,
    brandColor = brandColor
)

@Serializable
data class FoodishImage(
    val image: String
)