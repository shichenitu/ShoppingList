package dk.verzier.shoppingv7.data

import dk.verzier.shoppingv7.data.database.ItemDao
import dk.verzier.shoppingv7.data.remote.FoodishApiService
import dk.verzier.shoppingv7.domain.Item
import dk.verzier.shoppingv7.domain.ItemRepository
import dk.verzier.shoppingv7.domain.toDto
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao,
    private val foodishApiService: FoodishApiService
) : ItemRepository {

    override fun getShoppingList(): Flow<List<Item>> {
        return itemDao.getShoppingList().map { entityList ->
            entityList.map { it.toItemDto().toItem() }
        }
    }

    override fun getItem(id: String): Flow<Item?> {
        return itemDao.getItem(id = id).map { it?.toItemDto()?.toItem() }
    }

    override suspend fun addItem(item: Item) {
        val imageUrl = fetchImage(itemWhat = item.what)
        val formattedItem =
            item.copy(
                what = item.what.toTitleCase(),
                where = item.where.toTitleCase(),
                imageUrl = imageUrl
            )
        itemDao.insert(item = formattedItem.toDto().toEntity())
    }

    private suspend fun fetchImage(itemWhat: String): String? {
        val (category, keyword) = if (itemWhat.contains(other = "pizza", ignoreCase = true)) {
            "pizza" to itemWhat.lowercase().replace("pizza", "").trim()
            // TODO: Define logic for extracting 'keyword' from pizza
        } else {
            itemWhat.lowercase().trim() to null
        }

        // TODO: Add a try-catch block for error handling
        var currentDelay = 1000L
        val maxRetries = 3

        for (attempt in 1..maxRetries) {
            try {
                return foodishApiService.getImage(category).image  // TODO: Pass 'keyword' to API

            } catch (e: Exception) {
                android.util.Log.e("ItemRepository", "Attempt $attempt failed, retrying in ${currentDelay}ms...", e)

                if (e is retrofit2.HttpException && e.code() == 404) {
                    return null
                }

                if (attempt == maxRetries) {
                    return null
                }

                val jitter = (0..500).random().toLong()
                delay(currentDelay + jitter)

                currentDelay *= 2
            }
        }
        return null
        // TODO: Add retry logic in your catch block - see slide titled "Intelligent retries & idempotency" for guidance
        // Handle 404 separately: don't retry, the image doesn't exist.
        // For other errors (including network errors), log and prepare to retry.
    }


    override suspend fun removeItem(item: Item) {
        itemDao.delete(id = item.id)
    }

    override suspend fun updateItem(item: Item) {
        val formattedItem =
            item.copy(what = item.what.toTitleCase(), where = item.where.toTitleCase())
        itemDao.update(item = formattedItem.toDto().toEntity())
    }

    private fun String.toTitleCase(): String {
        return this.trim().split(" ").joinToString(separator = " ") { word ->
            word.lowercase()
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
