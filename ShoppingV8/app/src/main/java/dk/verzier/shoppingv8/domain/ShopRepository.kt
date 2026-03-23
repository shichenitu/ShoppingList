package dk.verzier.shoppingv8.domain

import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getShops(): Flow<List<Shop>>

    suspend fun insertShops()
}