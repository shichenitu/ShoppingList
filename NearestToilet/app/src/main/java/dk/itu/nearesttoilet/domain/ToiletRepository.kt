package dk.itu.nearesttoilet.domain

import android.location.Location

interface ToiletRepository {
    // Fetches all toilets from all municipalities and combines them
    suspend fun getAllToilets(): List<PublicToilet>

    // Finds the single closest toilet to the user's current location
    suspend fun findClosestToilet(currentLocation: Location): PublicToilet?
}