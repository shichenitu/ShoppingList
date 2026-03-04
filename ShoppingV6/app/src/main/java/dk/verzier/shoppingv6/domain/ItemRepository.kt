package dk.verzier.shoppingv6.domain

import kotlinx.coroutines.flow.Flow

interface ItemRepository {
    fun getShoppingList(): Flow<List<Item>>
    fun getItem(id: String): Flow<Item?>
    suspend fun addItem(item: Item)
    suspend fun removeItem(item: Item)
    suspend fun updateItem(item: Item)
}