package dk.verzier.shoppingv9.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.verzier.shoppingv9.domain.Theme
import dk.verzier.shoppingv9.domain.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferencesRepositoryImpl @Inject constructor(@param:ApplicationContext private val context: Context) :
    UserPreferencesRepository {

    private object PreferencesKeys {
        val THEME = stringPreferencesKey(name = "theme")
        val TARGET_TIME = longPreferencesKey("welcome_target_time")
    }

    override val theme: Flow<Theme> = context.dataStore.data
        .map { preferences ->
            when (preferences[PreferencesKeys.THEME]) {
                Theme.LIGHT.name.lowercase() -> Theme.LIGHT
                Theme.DARK.name.lowercase() -> Theme.DARK
                else -> Theme.SYSTEM
            }
        }

    override val targetTime: Flow<Long?> = context.dataStore.data
        .map { preferences ->
            preferences[PreferencesKeys.TARGET_TIME]
        }


    override suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME] = when (theme) {
                Theme.LIGHT -> Theme.LIGHT.name.lowercase()
                Theme.DARK -> Theme.DARK.name.lowercase()
                Theme.SYSTEM -> Theme.SYSTEM.name.lowercase()
            }
        }
    }

    override suspend fun setTargetTime(newTarget: Long) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TARGET_TIME] = newTarget
        }
    }
}
