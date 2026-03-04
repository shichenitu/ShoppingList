package dk.verzier.shoppingv6.ui.features.shoppinglist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.verzier.shoppingv6.domain.Item
import dk.verzier.shoppingv6.domain.ItemRepository
import dk.verzier.shoppingv6.domain.Shop
import dk.verzier.shoppingv6.domain.ShopRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ShoppingListViewModel @Inject constructor(
    private val itemRepository: ItemRepository,
    private val shopRepository: ShopRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val shoppingListArgs: ShoppingList = savedStateHandle.toRoute()
    private val itemId: String? = shoppingListArgs.itemId

    private val _uiState = MutableStateFlow(value = UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        viewModelScope.launch {
            combine(
                flow = itemRepository.getShoppingList(),
                flow2 = shopRepository.getShops()
            ) { shoppingList, shops ->
                _uiState.update { it.copy(shoppingList = shoppingList, shops = shops) }
            }.collect {}
        }
        itemId?.let {
            viewModelScope.launch {
                itemRepository.getItem(id = itemId).collect { item ->
                    _uiState.update { it.copy(selectedItem = item) }
                }
            }
        }
    }

    val uiEvents: UiEvents = object : UiEvents {
        override fun onAddItemClick() {
            viewModelScope.launch {
                _navigationEvents.emit(value = NavigationEvent.NavigateToAddWhat)
            }
        }

        override fun onEditItemClick(item: Item) {
            _uiState.update { it.copy(selectedItem = item) }
        }

        override fun onWhatChange(what: String) {
            _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(what = what)) }
        }

        override fun onWhereChange(where: String) {
            _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(where = where)) }
        }

        override fun onDescriptionChange(description: String) {
            _uiState.update { it.copy(selectedItem = it.selectedItem?.copy(description = description)) }
        }

        override fun onDeadlineChange(deadline: Long?) {
            val formattedDeadline = deadline?.let {
                val date = Date(/* date = */ it)
                val format = SimpleDateFormat(
                    /* pattern = */ "dd.MM.yyyy",
                    /* locale = */ Locale.getDefault()
                )
                format.format(date)
            }
            _uiState.update {
                // TODO add/update deadline in selected item
                it.copy(selectedItem = it.selectedItem?.copy())
            }
        }

        override fun onSaveClick(): Boolean {
            val item = _uiState.value.selectedItem ?: return true

            when {
                item.what.isNotBlank() && item.where.isNotBlank() -> {
                    _uiState.update { it.copy(isWhatError = false, isWhereError = false) }
                    viewModelScope.launch {
                        itemRepository.updateItem(item = item)
                    }
                    onDismissDetails()
                    return true
                }

                item.what.isBlank() && item.where.isBlank() ->
                    _uiState.update { it.copy(isWhatError = true, isWhereError = true) }

                item.what.isBlank() -> _uiState.update {
                    it.copy(
                        isWhatError = true,
                        isWhereError = false
                    )
                }

                item.where.isBlank() -> _uiState.update {
                    it.copy(
                        isWhatError = false,
                        isWhereError = true
                    )
                }
            }

            return false
        }

        override fun onDeleteClick() {
            _uiState.update { it.copy(showDeleteConfirmation = true) }
        }

        override fun onConfirmDelete() {
            _uiState.value.selectedItem?.let { item ->
                viewModelScope.launch {
                    itemRepository.removeItem(item = item)
                }
            }
            onDismissDetails()
        }

        override fun onDismissDeleteConfirmation() {
            _uiState.update { it.copy(showDeleteConfirmation = false) }
        }

        override fun onDismissDetails() {
            _uiState.update { it.copy(selectedItem = null, showDeleteConfirmation = false) }
        }
    }

    data class UiState(
        val shoppingList: List<Item> = emptyList(),
        val shops: List<Shop> = emptyList(),
        val selectedItem: Item? = null,
        val isWhatError: Boolean = false,
        val isWhereError: Boolean = false,
        val showDeleteConfirmation: Boolean = false
    )

    @Immutable
    interface UiEvents {
        fun onAddItemClick()
        fun onEditItemClick(item: Item)
        fun onWhatChange(what: String)
        fun onWhereChange(where: String)
        fun onDescriptionChange(description: String)
        fun onDeadlineChange(deadline: Long?)
        fun onSaveClick(): Boolean
        fun onDeleteClick()
        fun onConfirmDelete()
        fun onDismissDeleteConfirmation()
        fun onDismissDetails()
    }

    sealed class NavigationEvent {
        data object NavigateToAddWhat : NavigationEvent()
    }
}
