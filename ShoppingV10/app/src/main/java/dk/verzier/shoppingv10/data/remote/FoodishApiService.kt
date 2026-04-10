package dk.verzier.shoppingv10.data.remote

import dk.verzier.shoppingv10.data.FoodishImage
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
