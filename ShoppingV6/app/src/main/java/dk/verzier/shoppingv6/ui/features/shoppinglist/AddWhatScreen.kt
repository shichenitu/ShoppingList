package dk.verzier.shoppingv6.ui.features.shoppinglist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.verzier.shoppingv6.R
import dk.verzier.shoppingv6.ui.components.DismissButton
import dk.verzier.shoppingv6.ui.components.ConfirmButton
import dk.verzier.shoppingv6.ui.components.NavigationType
import dk.verzier.shoppingv6.ui.components.ShoppingTextField
import dk.verzier.shoppingv6.ui.components.ShoppingTopAppBar
import dk.verzier.shoppingv6.ui.components.ThemedPreviews
import dk.verzier.shoppingv6.ui.navigation.AppRoute
import dk.verzier.shoppingv6.ui.theme.ShoppingV6Theme
import kotlinx.serialization.Serializable

@Serializable
object AddWhat : AppRoute

@Composable
fun AddWhatScreen(
    onNavigate: (AddWhatViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AddWhatViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collect {
            onNavigate(it)
        }
    }

    AddWhatScreen(
        modifier = modifier,
        uiState = uiState,
        uiEvents = viewModel.uiEvents
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddWhatScreen(
    uiState: AddWhatViewModel.UiState,
    uiEvents: AddWhatViewModel.UiEvents,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by remember { mutableStateOf(value = false) }
    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                ConfirmButton {
                    showDatePicker = false
                    uiEvents.onDeadlineChange(datePickerState.selectedDateMillis)
                }
            },
            dismissButton = {
                DismissButton { showDatePicker = false }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            ShoppingTopAppBar(
                titleRes = R.string.add_what_title,
                navigationType = NavigationType.BACK,
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
                value = uiState.what,
                onValueChange = uiEvents::onWhatChange,
                labelRes = R.string.what_label,
                focusManager = LocalFocusManager.current,
                isLastField = false,
                isError = uiState.isError
            )

            uiState.deadline?.let {
                Spacer(Modifier.height(height = 8.dp))
                Text(
                    text = stringResource(id = R.string.deadline_set, it),
                    color = Color.Gray
                )
            }

            Spacer(Modifier.height(height = 8.dp))

            Row(modifier = Modifier.padding(horizontal = 50.dp)) {
                Button(
                    onClick = { showDatePicker = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(text = stringResource(id = R.string.add_deadline_button_label))
                }
                Spacer(modifier = Modifier.weight(weight = 1f))
                Button(onClick = uiEvents::onNextClick) {
                    Text(text = stringResource(id = R.string.next_button_label))
                }
            }
        }
    }
}

@ThemedPreviews
@Composable
fun AddWhatScreenPreview() {
    ShoppingV6Theme {
        AddWhatScreen(
            modifier = Modifier,
            uiState = AddWhatViewModel.UiState(what = "Milk"),
            uiEvents = object : AddWhatViewModel.UiEvents {
                override fun onWhatChange(what: String) {}
                override fun onDeadlineChange(deadline: Long?) {}
                override fun onNextClick() {}
                override fun onUpClick() {}
            }
        )
    }
}
