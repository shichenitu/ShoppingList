package dk.verzier.shoppingv6.ui.features.shops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.verzier.shoppingv6.domain.Shop
import dk.verzier.shoppingv6.domain.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ShopsViewModel @Inject constructor(
    private val shopRepository: ShopRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            shopRepository.getShops().collect { shops ->
                _uiState.update { it.copy(shops = shops) }
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
    }

    data class UiState(
        val shops: List<Shop> = emptyList(),
        val selectedShop: Shop? = null
    )

    @Immutable
    interface UiEvents {
        fun onShopSelected(shop: Shop)
        fun onDismissShopDetails()
    }
}