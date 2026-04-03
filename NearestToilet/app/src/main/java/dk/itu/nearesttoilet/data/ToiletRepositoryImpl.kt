package dk.itu.nearesttoilet.data

import android.location.Location
import dk.itu.nearesttoilet.data.remote.CopenhagenApiService
import dk.itu.nearesttoilet.data.remote.FrederiksbergApiService
import dk.itu.nearesttoilet.domain.PublicToilet
import dk.itu.nearesttoilet.domain.ToiletRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ToiletRepositoryImpl @Inject constructor(
    private val cphApi: CopenhagenApiService,
    private val frbApi: FrederiksbergApiService
) : ToiletRepository {

    // We cache the list so we don't redownload it every time the user takes a step!
    private var cachedToilets: List<PublicToilet>? = null

    override suspend fun getAllToilets(): List<PublicToilet> {
        // Return cache if we already have it
        cachedToilets?.let { return it }

        // Use coroutineScope to fetch from all 3 endpoints concurrently!
        val combinedList = coroutineScope {
            val tmfDeferred = async { cphApi.getTMFToilets() }
            val otherDeferred = async { cphApi.getOtherToilets() }
            val frbDeferred = async { frbApi.getToilets() }

            val tmfToilets = try {
                tmfDeferred.await().features.map { it.toPublicToilet() }
            } catch (_: Exception) {
                emptyList()
            }

            val otherToilets = try {
                otherDeferred.await().features.map { it.toPublicToilet() }
            } catch (_: Exception) {
                emptyList()
            }

            val frbToilets = try {
                frbDeferred.await().features.map { it.toPublicToilet() }
            } catch (_: Exception) {
                emptyList()
            }

            tmfToilets + otherToilets + frbToilets
        }

        cachedToilets = combinedList
        return combinedList
    }

    override suspend fun findClosestToilet(currentLocation: Location): PublicToilet? {
        val allToilets = getAllToilets()
        if (allToilets.isEmpty()) return null

        return allToilets.minByOrNull { toilet ->
            // Create a temporary Location object for the toilet to use distanceTo
            val toiletLocation = Location("").apply {
                latitude = toilet.latitude
                longitude = toilet.longitude
            }
            currentLocation.distanceTo(toiletLocation)
        }
    }
}