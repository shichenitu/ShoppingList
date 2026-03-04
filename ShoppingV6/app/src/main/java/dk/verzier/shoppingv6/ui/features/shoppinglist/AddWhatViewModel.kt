package dk.verzier.shoppingv6.ui.features.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class AddWhatViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(value = UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    val uiEvents: UiEvents = object : UiEvents {
        override fun onWhatChange(what: String) {
            _uiState.update { it.copy(what = what, isError = false) }
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
            _uiState.update { it.copy(deadline = formattedDeadline) }
        }

        override fun onNextClick() {
            if (_uiState.value.what.isNotBlank()) {
                viewModelScope.launch {
                    _navigationEvents.emit(
                        value = NavigationEvent.NavigateToAddWhere(
                            what = uiState.value.what,
                            deadline = uiState.value.deadline
                        )
                    )
                }
            } else {
                _uiState.update { it.copy(isError = true) }
            }
        }

        override fun onUpClick() {
            viewModelScope.launch {
                _navigationEvents.emit(value = NavigationEvent.NavigateUp)
            }
        }
    }

    data class UiState(
        val what: String = "",
        val deadline: String? = null,
        val isError: Boolean = false
    )

    @Immutable
    interface UiEvents {
        fun onWhatChange(what: String)
        fun onDeadlineChange(deadline: Long?)
        fun onNextClick()
        fun onUpClick()
    }

    sealed class NavigationEvent {
        data object NavigateUp : NavigationEvent()
        data class NavigateToAddWhere(val what: String, val deadline: String?) : NavigationEvent()
    }
}
