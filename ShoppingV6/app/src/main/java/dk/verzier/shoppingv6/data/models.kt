package dk.verzier.shoppingv6.data

import androidx.compose.ui.graphics.Color
import dk.verzier.shoppingv6.domain.Item
import dk.verzier.shoppingv6.domain.Shop
import java.util.UUID
import androidx.core.graphics.toColorInt
import dk.verzier.shoppingv6.data.database.ItemEntity
import dk.verzier.shoppingv6.data.database.ShopEntity

data class ItemDto(
    val id: String = UUID.randomUUID().toString(),
    val what: String,
    val where: String,
    val description: String = "",
    // TODO: Add deadline to item
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
)

fun ItemEntity.toItemDto() = ItemDto(
    id = id,
    what = what,
    where = where,
    description = description,
)

fun ShopEntity.toShopDto() = ShopDto(
    name = name,
    imageUrl = imageUrl,
    brandColor = brandColor
)