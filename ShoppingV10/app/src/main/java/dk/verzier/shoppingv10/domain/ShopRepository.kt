package dk.verzier.shoppingv10.domain

import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getShops(): Flow<List<Shop>>

    suspend fun insertShops()
}