package dk.verzier.shoppingv9.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import dk.verzier.shoppingv9.domain.Shop
import dk.verzier.shoppingv9.domain.ShopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(private val db: FirebaseFirestore) : ShopRepository {
    private val shopsCollection = db.collection(/* collectionPath = */ "shops")

    override fun getShops(): Flow<List<Shop>> {
        return shopsCollection.snapshots().map { snapshot ->
            snapshot.toObjects(/* clazz = */ ShopDto::class.java).map { it.toShop() }
        }
    }

    override suspend fun insertShops() {
        val shops = listOf(
            ShopDto(
                name = "Netto",
                imageUrl = "https://tse2.mm.bing.net/th/id/OIP.zLTC9xtHFbG3qlBlpreHLgHaHa",
                brandColor = "#fbdc12",
                latitude = 55.656233,
                longitude = 12.589648,
            ),
            ShopDto(
                name = "Rema1000",
                imageUrl = "https://logowik.com/content/uploads/images/rema-10007971.logowik.com.webp",
                brandColor = "#023ea5",
                latitude = 55.663435,
                longitude = 12.593617,
                closingHour = 21
            ),
            ShopDto(
                name = "Kvickly",
                imageUrl = "https://via.ritzau.dk/data/images/00354/3718f3a8-2a42-4efd-b039-fde93c537ba7.png",
                brandColor = "#c50e20",
                latitude = 55.656742,
                longitude = 12.603874,
            ),
            ShopDto(
                name = "SuperBrugsen",
                imageUrl = "https://banner2.cleanpng.com/20181112/gog/kisspng-superbrugsen-esbjerg-leader-esbjerg-storcenter-dag-5bea05f7ef56e6.6004540715420636079803.jpg",
                brandColor = "#c31315",
                latitude = 55.663025,
                longitude = 12.603596,
                closingHour = 21
            ),
            ShopDto(
                name = "365Discount",
                imageUrl = "https://tse1.mm.bing.net/th/id/OIP.HW3Q43OHW-EHjvYIi8XNnwHaHa",
                brandColor = "#086036",
                latitude = 55.665597,
                longitude = 12.599589,
            ),
            ShopDto(
                name = "Føtex",
                imageUrl = "https://www.legout.dk/wp-content/uploads/2024/02/275850_Ftex.png",
                brandColor = "#0e223b",
                latitude = 55.657358,
                longitude = 12.591640,
                closingHour = 21
            ),
            ShopDto(
                name = "Bilka",
                imageUrl = "https://foodpartners.dk/wp-content/uploads/2021/10/bilka_logo.png",
                brandColor = "#009fe3",
                latitude = 55.631522,
                longitude = 12.577700,
            ),
            ShopDto(
                name = "Hart Bakery",
                imageUrl = "https://images.squarespace-cdn.com/content/v1/53a0099de4b03f568ef67cea/1596556236454-82QYXKM4AXJ2L1DPTAOF/Hart-logo.png",
                brandColor = "#000000",
                latitude = 55.666864,
                longitude = 12.576995,
                openingHour = 8,
                closingHour = 18
            )
        )
        val batch = db.batch()
        shops.forEach { shopDto ->
            val docRef = shopsCollection.document(/* documentPath = */ shopDto.name) // Using name as ID
            batch.set(/* documentRef = */ docRef, /* data = */ shopDto)
        }
        batch.commit().await()
    }
}