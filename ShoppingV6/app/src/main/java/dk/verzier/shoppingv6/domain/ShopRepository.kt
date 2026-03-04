package dk.verzier.shoppingv6.domain

import kotlinx.coroutines.flow.Flow

interface ShopRepository {
    fun getShops(): Flow<List<Shop>>
}