package dk.itu.nearesttoilet

import android.annotation.SuppressLint
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.itu.nearesttoilet.domain.ToiletRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ToiletRepository,
    private val fusedLocationClient: FusedLocationProviderClient
) : ViewModel() {

    // Holds the final Google Maps URL, or null if loading/calculating
    private val _mapUrl = MutableStateFlow<String?>(null)
    val mapUrl: StateFlow<String?> = _mapUrl.asStateFlow()

    @SuppressLint("MissingPermission") // Safe, because the Activity checks permissions first
    fun fetchLocationAndFindToilet() {
        viewModelScope.launch {
            try {
                // Try to get the cached last location (Instant)
                var location: Location? = fusedLocationClient.lastLocation.await()

                // Fallback: If null, request a fresh, highly accurate location
                if (location == null) {
                    val cancellationTokenSource = CancellationTokenSource()
                    location = fusedLocationClient.getCurrentLocation(
                        Priority.PRIORITY_HIGH_ACCURACY,
                        cancellationTokenSource.token
                    ).await()
                }

                // If we successfully got a location, find the toilet
                if (location != null) {
                    findNearestToilet(location)
                } else {
                    // Handle failure (e.g., GPS is physically turned off)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun findNearestToilet(currentLocation: Location) {
        viewModelScope.launch {
            // 1. Ask the repository for the closest toilet
            val closestToilet = repository.findClosestToilet(currentLocation)

            if (closestToilet != null) {
                // 2. Generate a modern Google Maps Directions URL (Walking mode)
                val url =
                    "https://www.google.com/maps/dir/?api=1" +
                            "&origin=${currentLocation.latitude},${currentLocation.longitude}" +
                            "&destination=${closestToilet.latitude},${closestToilet.longitude}" +
                            "&travelmode=walking" +
                            "&basemap=satellite"

                // 3. Update the UI state!
                _mapUrl.value = url
            } else {
                // Fallback if the APIs fail or return empty lists
                _mapUrl.value = "https://www.google.com/maps"
            }
        }
    }
}