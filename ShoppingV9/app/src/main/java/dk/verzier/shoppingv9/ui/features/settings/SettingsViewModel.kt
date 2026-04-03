package dk.verzier.shoppingv9.ui.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dk.verzier.shoppingv9.domain.Theme
import dk.verzier.shoppingv9.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    val uiState: StateFlow<UiState> = userPreferencesRepository.theme
        .map { UiState(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = UiState()
        )

    val uiEvents: UiEvents = object : UiEvents {
        override fun onSetTheme(theme: Theme) {
            viewModelScope.launch {
                userPreferencesRepository.setTheme(theme)
            }
        }
    }

    data class UiState(
        val theme: Theme = Theme.SYSTEM
    )

    @Immutable
    interface UiEvents {
        fun onSetTheme(theme: Theme)
    }
}
