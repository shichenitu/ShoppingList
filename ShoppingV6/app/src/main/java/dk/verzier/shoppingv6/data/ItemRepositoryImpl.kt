package dk.verzier.shoppingv6.data

import dk.verzier.shoppingv6.data.database.ItemDao
import dk.verzier.shoppingv6.domain.Item
import dk.verzier.shoppingv6.domain.ItemRepository
import dk.verzier.shoppingv6.domain.toDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ItemRepositoryImpl @Inject constructor(
    private val itemDao: ItemDao
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
        val formattedItem =
            item.copy(what = item.what.toTitleCase(), where = item.where.toTitleCase())
        itemDao.insert(item = formattedItem.toDto().toEntity())
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
