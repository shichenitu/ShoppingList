package dk.itu.nearesttoilet.data.remote

import dk.itu.nearesttoilet.data.CphFeatureCollection
import retrofit2.http.GET

interface CopenhagenApiService {
    @GET("k101/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=k101:toilet_puma_agg_tmf_kk&srsname=EPSG:4326&outputFormat=application/json")
    suspend fun getTMFToilets(): CphFeatureCollection

    @GET("k101/ows?service=WFS&version=1.0.0&request=GetFeature&typeName=k101:toilet_puma_agg_andre_kk&srsname=EPSG:4326&outputFormat=application/json")
    suspend fun getOtherToilets(): CphFeatureCollection
}