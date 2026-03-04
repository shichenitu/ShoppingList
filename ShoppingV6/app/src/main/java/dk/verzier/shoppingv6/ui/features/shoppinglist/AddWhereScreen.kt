package dk.verzier.shoppingv6.ui.features.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.verzier.shoppingv6.R
import dk.verzier.shoppingv6.ui.components.NavigationType
import dk.verzier.shoppingv6.ui.components.ShoppingTextField
import dk.verzier.shoppingv6.ui.components.ShoppingTopAppBar
import dk.verzier.shoppingv6.ui.components.ThemedPreviews
import dk.verzier.shoppingv6.ui.navigation.AppRoute
import dk.verzier.shoppingv6.ui.theme.ShoppingV6Theme
import kotlinx.serialization.Serializable

@Serializable
data class AddWhere(val what: String, val deadline: String?) : AppRoute

@Composable
fun AddWhereScreen(
    onNavigate: (AddWhereViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddWhereViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collect {
            onNavigate(it)
        }
    }

    AddWhereScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvents = viewModel.uiEvents
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWhereScreen(
    uiState: AddWhereViewModel.UiState,
    uiEvents: AddWhereViewModel.UiEvents,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingTopAppBar(
                titleRes = R.string.add_where_title,
                navigationType = NavigationType.CLOSE,
                onUpClick = uiEvents::onUpClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShoppingTextField(
                value = uiState.where,
                onValueChange = uiEvents::onWhereChange,
                labelRes = R.string.where_label,
                focusManager = LocalFocusManager.current,
                isLastField = true,
                isError = uiState.isError
            )

            Spacer(Modifier.height(height = 8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Button(onClick = uiEvents::onCancelClick) {
                    Text(text = stringResource(R.string.cancel_addition_button_label))
                }
                Button(onClick = uiEvents::onDoneClick) {
                    Text(text = stringResource(R.string.done_button_label))
                }
            }
        }
    }
}

@ThemedPreviews
@Composable
fun AddWhereScreenPreview() {
    ShoppingV6Theme {
        AddWhereScreen(
            modifier = Modifier,
            uiState = AddWhereViewModel.UiState(where = "Dairy"),
            uiEvents = object : AddWhereViewModel.UiEvents {
                override fun onWhereChange(where: String) {}
                override fun onDoneClick() {}
                override fun onUpClick() {}
                override fun onCancelClick() {}
            }
        )
    }
}