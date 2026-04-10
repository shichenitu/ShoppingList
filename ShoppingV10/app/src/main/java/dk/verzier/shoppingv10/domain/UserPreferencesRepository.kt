package dk.verzier.shoppingv10.domain

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    val theme: Flow<Theme>
    val targetTime: Flow<Long?>
    suspend fun setTheme(theme: Theme)
    suspend fun setTargetTime(newTarget: Long)
}
