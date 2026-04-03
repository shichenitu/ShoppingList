package dk.verzier.shoppingv9.data.remote

import dk.verzier.shoppingv9.data.FoodishImage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodishApiService {
    @GET(/* value = */ "api/images/{category}")
    suspend fun getImage(
        @Path(/* value = */ "category") category: String,
        @Query(/* value = */ "keyword") keyword: String? = null
    ): FoodishImage
}
