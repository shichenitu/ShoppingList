package dk.verzier.shoppingv6.ui.components

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SnackBarHandler @Inject constructor(
    private val scope: CoroutineScope,
    @param:ApplicationContext private val context: Context,
) {
    val snackBarHostState = SnackbarHostState()

    fun postMessage(
        @StringRes msgRes: Int,
        vararg arguments: Any,
        @StringRes actionLabelRes: Int? = null,
        onDismiss: (() -> Unit)? = null,
        onActionClick: (() -> Unit)? = null,
    ) {
        scope.launch {
            val result = snackBarHostState.showSnackbar(
                message = context.getString(/* resId = */ msgRes, /* ...formatArgs = */ *arguments),
                actionLabel = actionLabelRes?.let { context.getString(/* resId = */ it) },
                duration = if (actionLabelRes != null) SnackbarDuration.Long else SnackbarDuration.Short
            )
            when (result) {
                SnackbarResult.ActionPerformed -> onActionClick?.invoke()
                SnackbarResult.Dismissed -> onDismiss?.invoke()
            }
        }
    }
}