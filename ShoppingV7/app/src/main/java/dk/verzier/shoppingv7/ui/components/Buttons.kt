package dk.verzier.shoppingv7.ui.components

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import dk.verzier.shoppingv7.R

@Composable
fun ConfirmButton(onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Text(text = stringResource(R.string.ok_button_label))
    }
}

@Composable
fun DismissButton(onClick: () -> Unit){
    TextButton(onClick = onClick) {
        Text(text = stringResource(R.string.cancel_button_label))
    }
}