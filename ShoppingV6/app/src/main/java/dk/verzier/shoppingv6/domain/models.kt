package dk.verzier.shoppingv6.domain

import android.content.Context
import androidx.compose.ui.graphics.Color
import dk.verzier.shoppingv6.R
import dk.verzier.shoppingv6.data.ItemDto
import dk.verzier.shoppingv6.data.ShopDto
import java.util.UUID

data class Item(
    val id: String = UUID.randomUUID().toString(),
    val what: String,
    val where: String,
    val description: String = "",
    // TODO: Add deadline to item
)

data class Shop(
    val name: String,
    val imageUrl: String,
    val brandColor: Color
)

enum class Theme {
    SYSTEM,
    LIGHT,
    DARK
}

fun Item.toDto(): ItemDto = ItemDto(id = this.id, what = this.what, where = this.where, description = this.description)
fun Shop.toDto(): ShopDto = ShopDto(name = this.name, imageUrl = this.imageUrl, brandColor = this.brandColor.value.toString())

fun Item.fullDescription(context: Context): String =
    context.getString(R.string.list_item_label, this.what.lowercase(), this.where)