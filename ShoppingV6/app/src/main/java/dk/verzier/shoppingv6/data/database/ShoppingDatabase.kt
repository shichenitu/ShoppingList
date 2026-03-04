package dk.verzier.shoppingv6.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dk.verzier.shoppingv6.data.ItemDto
import dk.verzier.shoppingv6.data.ShopDto
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
                    what = "Coffee",
                    where = "Føtex",
                    description = "The one Bob showed us - with the blue label."
                ),
                ItemDto(
                    id = UUID.randomUUID().toString(),
                    what = "Carrots",
                    where = "Netto",
                    description = ""
                ),
                ItemDto(
                    id = UUID.randomUUID().toString(),
                    what = "Milk",
                    where = "SuperBrugsen",
                    description = "Both sødmælk and lactose-free minimælk"
                ),
                ItemDto(
                    id = "deep-link-item",
                    what = "Bread",
                    where = "Hart Bakery",
                    description = "Half-a-loaf and a chocolate bun (please!)"
                ),
                ItemDto(
                    id = UUID.randomUUID().toString(),
                    what = "Butter",
                    where = "Føtex",
                    description = ""
                )
            )
            initialItems.forEach {
                itemDao.insert(
                    item = ItemEntity(
                        id = it.id,
                        what = it.what,
                        where = it.where,
                        description = it.description,
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
