package dk.verzier.shoppingv10.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.snapshots
import dk.verzier.shoppingv10.data.remote.FoodishApiService
import dk.verzier.shoppingv10.domain.Item
import dk.verzier.shoppingv10.domain.ItemRepository
import dk.verzier.shoppingv10.domain.toDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class ItemRepositoryImpl @Inject constructor(
    db: FirebaseFirestore,
    private val foodishApiService: FoodishApiService
) : ItemRepository {

    private val itemsCollection = db.collection("items")

    override fun getShoppingList(): Flow<List<Item>> {
        return itemsCollection
            .orderBy("where")
            .orderBy("what")
            .snapshots()
            .map { snapshot ->
                snapshot.toObjects(ItemDto::class.java).map { it.toItem() }
            }
    }

    override fun getItem(id: String): Flow<Item?> {
        return itemsCollection.document(id)
            .snapshots()
            .map { snapshot ->
                snapshot.toObject(ItemDto::class.java)?.toItem()
            }
    }

    override suspend fun addItem(item: Item) {
        val imageUrl = fetchImageWithRetry(itemWhat = item.what)
        val formattedItem =
            item.copy(
                what = item.what.toTitleCase(),
                where = item.where.toTitleCase(),
                imageUrl = imageUrl
            )
        val itemDto = formattedItem.toDto()
        itemsCollection.document(itemDto.id).set(itemDto).await()
    }

    private suspend fun fetchImageWithRetry(itemWhat: String): String? {
        var attempts = 0
        var delayMillis = 5000L // Start with 5 seconds
        val maxAttempts = 4 // Total attempts: 1 initial + 3 retries

        val (category, keyword) = if (itemWhat.contains(other = "pizza", ignoreCase = true)) {
            "pizza" to itemWhat.substringBefore(delimiter = "pizza").trim()
        } else {
            itemWhat to null
        }

        while (attempts < maxAttempts) {
            try {
                return foodishApiService.getImage(category, keyword).image
            } catch (e: Exception) {
                // Handle 404 separately: don't retry, the image doesn't exist.
                if (e is HttpException && e.code() == 404) {
                    Log.w(/* tag = */ "ItemRepositoryImpl", /* msg = */
                        "Image not found for category: $category (404)"
                    )
                    return null
                }

                // For other errors (including network errors), log and prepare to retry.
                val errorType =
                    if (e is IOException) "Network error (likely offline)" else "API error"
                Log.w(/* tag = */ "ItemRepositoryImpl", /* msg = */
                    "$errorType fetching image for $category. Attempt ${attempts + 1} of $maxAttempts."
                )

                attempts++
                if (attempts < maxAttempts) {
                    Log.d(/* tag = */ "ItemRepositoryImpl", /* msg = */
                        "Retrying in ${delayMillis / 1000} seconds..."
                    )
                    delay(timeMillis = delayMillis)
                    delayMillis *= 2 // Double the delay for the next attempt
                    delayMillis += Random.nextLong(from = 0, until = 1000) // Add jitter
                }
            }
        }
        Log.e(/* tag = */ "ItemRepositoryImpl", /* msg = */
            "Max retry attempts reached for category: $category. Giving up."
        )
        return null
    }


    override suspend fun removeItem(item: Item) {
        itemsCollection.document(item.id).delete().await()
    }

    override suspend fun updateItem(item: Item) {
        val formattedItem =
            item.copy(what = item.what.toTitleCase(), where = item.where.toTitleCase())
        val itemDto = formattedItem.toDto()
        itemsCollection.document(itemDto.id).set(itemDto, SetOptions.merge()).await()
    }

    private fun String.toTitleCase(): String {
        return this.trim().split(" ").joinToString(separator = " ") { word ->
            word.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
