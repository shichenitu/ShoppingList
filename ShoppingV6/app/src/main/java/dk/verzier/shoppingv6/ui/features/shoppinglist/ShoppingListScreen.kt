package dk.verzier.shoppingv6.ui.features.shoppinglist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import dk.verzier.shoppingv6.R
import dk.verzier.shoppingv6.domain.Item
import dk.verzier.shoppingv6.domain.fullDescription
import dk.verzier.shoppingv6.ui.components.ItemOrNullProvider
import dk.verzier.shoppingv6.ui.components.NavigationType
import dk.verzier.shoppingv6.ui.components.ShoppingTopAppBar
import dk.verzier.shoppingv6.ui.components.ThemedPreviews
import dk.verzier.shoppingv6.ui.components.previewShoppingList
import dk.verzier.shoppingv6.ui.components.previewShops
import dk.verzier.shoppingv6.ui.navigation.AppRoute
import dk.verzier.shoppingv6.ui.navigation.NestedGraph
import dk.verzier.shoppingv6.ui.theme.ShoppingV6Theme
import kotlinx.serialization.Serializable

@Serializable
object ShoppingGraph : NestedGraph {
    override val startDestination: AppRoute
        get() = ShoppingList()
}

@Serializable
data class ShoppingList(val itemId: String? = null) : AppRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingListScreen(
    onNavigate: (ShoppingListViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ShoppingListViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collect {
            onNavigate(it)
        }
    }

    ShoppingListScreen(
        uiState = uiState,
        uiEvents = viewModel.uiEvents,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShoppingListScreen(
    uiState: ShoppingListViewModel.UiState,
    uiEvents: ShoppingListViewModel.UiEvents,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            ShoppingTopAppBar(
                titleRes = R.string.app_name,
                navigationType = NavigationType.NONE
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = uiEvents::onAddItemClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(id = R.string.add_button_label)
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            items(items = uiState.shoppingList) { item ->
                val shop = uiState.shops.find { it.name == item.where }
                ListItem(
                    item = item,
                    imageUrl = shop?.imageUrl,
                    brandColor = shop?.brandColor,
                    onItemClick = { uiEvents.onEditItemClick(item) }
                )
            }
        }
    }

    uiState.selectedItem?.let {
        DetailsSheet(
            item = it,
            isWhatError = uiState.isWhatError,
            isWhereError = uiState.isWhereError,
            showDeleteConfirmation = uiState.showDeleteConfirmation,
            uiEvents = uiEvents
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ListItem(item: Item, imageUrl: String?, brandColor: Color?, onItemClick: () -> Unit) {
    Card(
        onClick = onItemClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.padding(all = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(weight = 1f)) {
                Text(
                    text = item.what,
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    color = brandColor ?: Color.Unspecified
                )
                Text(
                    text = item.fullDescription(context = LocalContext.current),
                    style = MaterialTheme.typography.bodyMedium,
                    color = brandColor ?: Color.Unspecified
                )
            }
            imageUrl?.let {
                Spacer(modifier = Modifier.width(width = 8.dp))
                AsyncImage(
                    model = it,
                    contentDescription = item.where,
                    modifier = Modifier.size(size = 40.dp)
                )
            }
            IconButton(onClick = onItemClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.edit_item_title)
                )
            }
        }
    }
}

@ThemedPreviews
@Composable
private fun ShoppingListScreenPreview(@PreviewParameter(provider = ItemOrNullProvider::class) itemOrNull: Item?) {
    ShoppingV6Theme {
        ShoppingListScreen(
            uiState = ShoppingListViewModel.UiState(
                shoppingList = previewShoppingList(),
                shops = previewShops(),
                selectedItem = itemOrNull,
                isWhatError = false,
                isWhereError = false,
                showDeleteConfirmation = false,
            ),
            uiEvents = object : ShoppingListViewModel.UiEvents {
                override fun onWhatChange(what: String) {}
                override fun onWhereChange(where: String) {}
                override fun onDescriptionChange(description: String) {}
                override fun onDeadlineChange(deadline: Long?) {}
                override fun onSaveClick(): Boolean {
                    return true
                }
                override fun onDeleteClick() {}
                override fun onConfirmDelete() {}
                override fun onDismissDeleteConfirmation() {}
                override fun onDismissDetails() {}
                override fun onAddItemClick() {}
                override fun onEditItemClick(item: Item) {}
            }
        )
    }
}
