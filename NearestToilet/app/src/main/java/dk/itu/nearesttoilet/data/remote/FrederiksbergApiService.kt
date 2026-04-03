package dk.itu.nearesttoilet.data.remote

import dk.itu.nearesttoilet.data.FrederiksbergFeatureCollection
import retrofit2.http.GET

interface FrederiksbergApiService {
    @GET("api/v2/sql/frederiksberg?format=geojson&q=select%20*%20from%20byrum.toiletter_offentlige&srs=4326")
    suspend fun getToilets(): FrederiksbergFeatureCollection
}