package dk.verzier.shoppingv7.data.remote

import dk.verzier.shoppingv7.data.FoodishImage
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodishApiService {
    @GET(/* value = */ "api/images/{category}")
    suspend fun getImage(
        @Path(/* value = */ "category") category: String,
        // TODO: Add a query parameter for keyword
        @Query("keyword") keyword: String? = null
    ): FoodishImage
}
