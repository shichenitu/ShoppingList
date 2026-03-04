package dk.verzier.shoppingv6.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.verzier.shoppingv6.domain.Theme
import dk.verzier.shoppingv6.domain.UserPreferencesRepository
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
    }

    override val theme: Flow<Theme> = context.dataStore.data
        .map { preferences ->
            when (preferences[PreferencesKeys.THEME]) {
                Theme.LIGHT.name.lowercase() -> Theme.LIGHT
                Theme.DARK.name.lowercase() -> Theme.DARK
                else -> Theme.SYSTEM
            }
        }

    override suspend fun setTheme(theme: Theme) {
        context.dataStore.edit { preferences ->
            /*preferences[PreferencesKeys.THEME] = when (theme) {
                // TODO: Map preferences keys to values
            }*/
        }
    }
}
