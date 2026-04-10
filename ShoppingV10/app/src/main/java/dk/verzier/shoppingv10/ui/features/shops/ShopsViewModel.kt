package dk.verzier.shoppingv10.ui.features.shops

import android.annotation.SuppressLint
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.verzier.shoppingv10.core.GeofenceManager
import dk.verzier.shoppingv10.domain.Shop
import dk.verzier.shoppingv10.domain.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val shopRepository: ShopRepository,
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geofenceManager: GeofenceManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shopRepository.getShops().collect { shops ->
                if (shops.isEmpty()) {
                    shopRepository.insertShops()
                } else {
                    _uiState.update { currentState ->
                        val sortedShops = if (currentState.userLat != null && currentState.userLng != null) {
                            sortShopsByDistance(
                                userLat = currentState.userLat,
                                userLng = currentState.userLng,
                                currentShops = shops
                            )
                        } else {
                            shops
                        }
                        currentState.copy(shops = sortedShops)
                    }
                }
            }
        }
    }

    val uiEvents: UiEvents = object : UiEvents {
        override fun onShopSelected(shop: Shop) {
            _uiState.update { it.copy(selectedShop = shop) }
        }

        override fun onDismissShopDetails() {
            _uiState.update { it.copy(selectedShop = null) }
        }

        @SuppressLint("MissingPermission")
        override fun onLocationPermissionGranted() {
            Log.d(/* tag = */ "ShopsViewModel", /* msg = */ "Permission granted! Waking up the GPS...")

            fusedLocationClient.getCurrentLocation(
                /* p0 = */ Priority.PRIORITY_HIGH_ACCURACY,
                /* p1 = */ CancellationTokenSource().token
            ).addOnSuccessListener { location: Location? ->
                if (location != null) {
                    Log.d(/* tag = */ "ShopsViewModel", /* msg = */ "Location found: ${location.latitude}, ${location.longitude}")

                    _uiState.update { currentState ->
                        val sortedShops = sortShopsByDistance(
                            userLat = location.latitude,
                            userLng = location.longitude,
                            currentShops = currentState.shops
                        )
                        currentState.copy(
                            userLat = location.latitude,
                            userLng = location.longitude,
                            shops = sortedShops
                        )
                    }
                } else {
                    // This happens if the emulator has NEVER had its location set
                    Log.d(/* tag = */ "ShopsViewModel", /* msg = */ "Location is still null! Make sure to set a location in the Emulator settings.")
                }
            }.addOnFailureListener { exception ->
                Log.d(/* tag = */ "ShopsViewModel", /* msg = */ "Failed to get location: ${exception.message}")
            }
        }

        override fun onEnableGeofencingClick() {
            _uiState.update { it.copy(showBackgroundPermission = true) }
        }

        override fun onBackgroundPermissionHandled(isGranted: Boolean) {
            _uiState.update { it.copy(showBackgroundPermission = false) }

            if (isGranted) {
                val openShops = _uiState.value.shops.filter { it.isCurrentlyOpen() }
                geofenceManager.registerGeofences(shops = openShops)
            } else {
                Log.d(/* tag = */ "ShopsViewModel", /* msg = */ "Background location denied. Geofencing won't work in the background.")
            }
        }
    }

    private fun sortShopsByDistance(userLat: Double, userLng: Double, currentShops: List<Shop>): List<Shop> {
        val distanceResults = FloatArray(size = 1)

        return currentShops.sortedBy { shop ->
            Location.distanceBetween(
                /* startLatitude = */ userLat,
                /* startLongitude = */ userLng,
                /* endLatitude = */ shop.latitude,
                /* endLongitude = */ shop.longitude,
                /* results = */ distanceResults
            )

            val distanceInMeters = distanceResults[0]

            Log.d(/* tag = */ "ShopsViewModel", /* msg = */ "Distance to ${shop.name}: $distanceInMeters meters")

            distanceInMeters
        }
    }

    data class UiState(
        val shops: List<Shop> = emptyList(),
        val selectedShop: Shop? = null,
        val userLat: Double? = null,
        val userLng: Double? = null,
        val showBackgroundPermission: Boolean = false
    )

    @Immutable
    interface UiEvents {
        fun onShopSelected(shop: Shop)
        fun onDismissShopDetails()
        fun onLocationPermissionGranted()
        fun onEnableGeofencingClick()
        fun onBackgroundPermissionHandled(isGranted: Boolean)
    }
}