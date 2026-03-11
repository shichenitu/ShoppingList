package dk.verzier.shoppingv7.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dk.verzier.shoppingv7.data.ItemDto
import dk.verzier.shoppingv7.data.ShopDto
import java.util.UUID
import javax.inject.Provider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ItemEntity::class, ShopEntity::class], version = 1)
abstract class ShoppingDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDao
    abstract fun shopDao(): ShopDao

    class Callback(
        private val itemDaoProvider: Provider<ItemDao>,
        private val shopDaoProvider: Provider<ShopDao>
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db = db)
            CoroutineScope(context = Dispatchers.IO).launch {
                prepopulateItems()
                prepopulateShops()
            }
        }

        private suspend fun prepopulateItems() {
            val itemDao = itemDaoProvider.get()
            val initialItems = listOf(
                ItemDto(
                    id = UUID.randomUUID().toString(),
                    what = "Rice",
                    where = "Føtex",
                    description = "The one Bob showed us - with the blue label.",
                    imageUrl = "https://foodish-api.com/images/rice/rice6.jpg"
                ),
                ItemDto(
                    id = UUID.randomUUID().toString(),
                    what = "Pasta",
                    where = "Netto",
                    description = "",
                    imageUrl = "https://foodish-api.com/images/pasta/pasta20.jpg"
                ),
                ItemDto(
                    id = UUID.randomUUID().toString(),
                    what = "Burger",
                    where = "SuperBrugsen",
                    description = "Both beef and veggie burgers",
                    imageUrl = "https://foodish-api.com/images/burger/burger1.jpg"
                ),
                ItemDto(
                    id = "deep-link-item",
                    what = "Pizza",
                    where = "OTTO",
                    description = "",
                    imageUrl = "https://foodish-api.com/images/pizza/pizza82.jpg"
                ),
                ItemDto(
                    id = UUID.randomUUID().toString(),
                    what = "Dessert",
                    where = "Føtex",
                    description = "",
                    imageUrl = "https://foodish-api.com/images/dessert/dessert33.jpg"
                )
            )
            initialItems.forEach {
                itemDao.insert(
                    item = ItemEntity(
                        id = it.id,
                        what = it.what,
                        where = it.where,
                        description = it.description,
                        deadline = it.deadline,
                        imageUrl = it.imageUrl
                    )
                )

            }
        }

        private suspend fun prepopulateShops() {
            val shopDao = shopDaoProvider.get()
            val initialShops = listOf(
                ShopDto(
                    name = "Netto",
                    imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
                    brandColor = "#fbdc12"
                ),
                ShopDto(
                    name = "Rema1000",
                    imageUrl = "https://logowik.com/content/uploads/images/rema-10007971.logowik.com.webp",
                    brandColor = "#023ea5"
                ),
                ShopDto(
                    name = "Kvickly",
                    imageUrl = "https://via.ritzau.dk/data/images/00354/3718f3a8-2a42-4efd-b039-fde93c537ba7.png",
                    brandColor = "#c50e20"
                ),
                ShopDto(
                    name = "SuperBrugsen",
                    imageUrl = "https://banner2.cleanpng.com/20181112/gog/kisspng-superbrugsen-esbjerg-leader-esbjerg-storcenter-dag-5bea05f7ef56e6.6004540715420636079803.jpg",
                    brandColor = "#c31315"
                ),
                ShopDto(
                    name = "365Discount",
                    imageUrl = "https://tse1.mm.bing.net/th/id/OIP.HW3Q43OHW-EHjvYIi8XNnwHaHa",
                    brandColor = "#086036"
                ),
                ShopDto(
                    name = "Føtex",
                    imageUrl = "https://www.legout.dk/wp-content/uploads/2024/02/275850_Ftex.png",
                    brandColor = "#0e223b"
                ),
                ShopDto(
                    name = "Bilka",
                    imageUrl = "https://foodpartners.dk/wp-content/uploads/2021/10/bilka_logo.png",
                    brandColor = "#009fe3"
                ),
                ShopDto(
                    name = "Hart Bakery",
                    imageUrl = "https://images.squarespace-cdn.com/content/v1/53a0099de4b03f568ef67cea/1596556236454-82QYXKM4AXJ2L1DPTAOF/Hart-logo.png",
                    brandColor = "#000000"
                )
            )
            shopDao.insertAll(shops = initialShops.map {
                ShopEntity(
                    name = it.name,
                    imageUrl = it.imageUrl,
                    brandColor = it.brandColor
                )
            })
        }
    }
}
