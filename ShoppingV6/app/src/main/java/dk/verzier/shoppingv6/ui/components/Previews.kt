package dk.verzier.shoppingv6.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.AndroidUiModes.UI_MODE_NIGHT_YES
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dk.verzier.shoppingv6.domain.Item
import dk.verzier.shoppingv6.domain.Shop
import java.util.UUID

class ShopProvider : PreviewParameterProvider<Shop> {
    override val values = sequenceOf(
        Shop(
            name = "Netto",
            imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
            brandColor = Color(color = 0xFFfbdc12)
        ),
        Shop(
            name = "Rema1000",
            imageUrl = "https://logowik.com/content/uploads/images/rema-10007971.logowik.com.webp",
            brandColor = Color(color = 0xFF023ea5)
        ),
        Shop(
            name = "Kvickly",
            imageUrl = "https://via.ritzau.dk/data/images/00354/3718f3a8-2a42-4efd-b039-fde93c537ba7.png",
            brandColor = Color(color = 0xFFc50e20)
        ),
        Shop(
            name = "SuperBrugsen",
            imageUrl = "https://banner2.cleanpng.com/20181112/gog/kisspng-superbrugsen-esbjerg-leader-esbjerg-storcenter-dag-5bea05f7ef56e6.6004540715420636079803.jpg",
            brandColor = Color(color = 0xFFc31315)
        ),
        Shop(
            name = "365Discount",
            imageUrl = "https://tse1.mm.bing.net/th/id/OIP.HW3Q43OHW-EHjvYIi8XNnwHaHa",
            brandColor = Color(color = 0xFF086036)
        ),
        Shop(
            name = "Føtex",
            imageUrl = "https://www.legout.dk/wp-content/uploads/2024/02/275850_Ftex.png",
            brandColor = Color(color = 0xFF0e223b)
        ),
        Shop(
            name = "Bilka",
            imageUrl = "https://foodpartners.dk/wp-content/uploads/2021/10/bilka_logo.png",
            brandColor = Color(color = 0xFF009fe3)
        ),
        Shop(
            name = "Hart Bakery",
            imageUrl = "https://images.squarespace-cdn.com/content/v1/53a0099de4b03f568ef67cea/1596556236454-82QYXKM4AXJ2L1DPTAOF/Hart-logo.png",
            brandColor = Color(color = 0xFF000000)
        )
    )
}

class ShopOrNullProvider : PreviewParameterProvider<Shop?> {
    override val values = sequenceOf(
        Shop(
            name = "Netto",
            imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
            brandColor = Color(color = 0xFFfbdc12)
        ),
        null
    )
}

class ItemOrNullProvider : PreviewParameterProvider<Item?> {
    override val values = sequenceOf(
        Item(
            id = UUID.randomUUID().toString(),
            what = "Coffee",
            where = "Føtex",
            description = "The one Bob showed us - with the blue label."
        ),
        null
    )
}

class BooleanProvider : PreviewParameterProvider<Boolean> {
    override val values = sequenceOf(
        true,
        false
    )
}

fun previewShops() = listOf(
    Shop(
        name = "Netto",
        imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
        brandColor = Color(color = 0xFFfbdc12)
    ),
    Shop(
        name = "Rema1000",
        imageUrl = "https://logowik.com/content/uploads/images/rema-10007971.logowik.com.webp",
        brandColor = Color(color = 0xFF023ea5)
    ),
    Shop(
        name = "Kvickly",
        imageUrl = "https://via.ritzau.dk/data/images/00354/3718f3a8-2a42-4efd-b039-fde93c537ba7.png",
        brandColor = Color(color = 0xFFc50e20)
    ),
    Shop(
        name = "SuperBrugsen",
        imageUrl = "https://banner2.cleanpng.com/20181112/gog/kisspng-superbrugsen-esbjerg-leader-esbjerg-storcenter-dag-5bea05f7ef56e6.6004540715420636079803.jpg",
        brandColor = Color(color = 0xFFc31315)
    ),
    Shop(
        name = "365Discount",
        imageUrl = "https://tse1.mm.bing.net/th/id/OIP.HW3Q43OHW-EHjvYIi8XNnwHaHa",
        brandColor = Color(color = 0xFF086036)
    ),
    Shop(
        name = "Føtex",
        imageUrl = "https://www.legout.dk/wp-content/uploads/2024/02/275850_Ftex.png",
        brandColor = Color(color = 0xFF0e223b)
    ),
    Shop(
        name = "Bilka",
        imageUrl = "https://foodpartners.dk/wp-content/uploads/2021/10/bilka_logo.png",
        brandColor = Color(color = 0xFF009fe3)
    ),
    Shop(
        name = "Hart Bakery",
        imageUrl = "https://images.squarespace-cdn.com/content/v1/53a0099de4b03f568ef67cea/1596556236454-82QYXKM4AXJ2L1DPTAOF/Hart-logo.png",
        brandColor = Color(color = 0xFF000000)
    )
)

fun previewShoppingList() = listOf(
    Item(
        id = UUID.randomUUID().toString(),
        what = "Coffee",
        where = "Føtex",
        description = "The one Bob showed us - with the blue label."
    ),
    Item(
        id = UUID.randomUUID().toString(),
        what = "Carrots",
        where = "Netto",
        description = ""
    ),
    Item(
        id = UUID.randomUUID().toString(),
        what = "Milk",
        where = "SuperBrugsen",
        description = "Both sødmælk and lactose-free minimælk"
    ),
    Item(
        id = "deep-link-item",
        what = "Bread",
        where = "Hart Bakery",
        description = "Half-a-loaf and a chocolate bun (please!)"
    ),
    Item(
        id = UUID.randomUUID().toString(),
        what = "Butter",
        where = "Føtex",
        description = ""
    )
)

@Preview(name = "Light Mode", uiMode = UI_MODE_NIGHT_NO, showBackground = true)
@Preview(name = "Dark Mode", uiMode = UI_MODE_NIGHT_YES, showBackground = true)
annotation class ThemedPreviews

