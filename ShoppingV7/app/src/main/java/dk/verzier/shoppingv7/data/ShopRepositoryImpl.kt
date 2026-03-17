package dk.verzier.shoppingv7.data

import dk.verzier.shoppingv7.data.database.ShopDao
import dk.verzier.shoppingv7.domain.Shop
import dk.verzier.shoppingv7.domain.ShopRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val shopDao: ShopDao
) : ShopRepository {

    override fun getShops(): Flow<List<Shop>> {
        return shopDao.getShops().map { entityList ->
            entityList.map { it.toShopDto().toShop() }
        }
    }
}
