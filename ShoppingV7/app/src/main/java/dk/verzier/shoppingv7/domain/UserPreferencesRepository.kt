package dk.verzier.shoppingv7.domain

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val theme: Flow<Theme>
    suspend fun setTheme(theme: Theme)
}
