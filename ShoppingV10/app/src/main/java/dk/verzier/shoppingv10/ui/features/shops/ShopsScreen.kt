package dk.verzier.shoppingv10.ui.features.shops

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.HorizontalUncontainedCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import dk.verzier.shoppingv10.R
import dk.verzier.shoppingv10.domain.Shop
import dk.verzier.shoppingv10.ui.components.NavigationType
import dk.verzier.shoppingv10.ui.components.RequestBackgroundLocationPermission
import dk.verzier.shoppingv10.ui.components.RequestLocationPermission
import dk.verzier.shoppingv10.ui.components.ShopOrNullProvider
import dk.verzier.shoppingv10.ui.components.ShoppingTopAppBar
import dk.verzier.shoppingv10.ui.components.ThemedPreviews
import dk.verzier.shoppingv10.ui.components.previewShops
import dk.verzier.shoppingv10.ui.navigation.AppRoute
import dk.verzier.shoppingv10.ui.theme.ShoppingV10PreviewTheme
import kotlinx.serialization.Serializable

@Serializable
object Shops : AppRoute

@Composable
fun ShopsScreen(
    modifier: Modifier = Modifier,
    viewModel: ShopsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ShopsScreen(uiState = uiState, uiEvents = viewModel.uiEvents, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopsScreen(
    uiState: ShopsViewModel.UiState,
    uiEvents: ShopsViewModel.UiEvents,
    modifier: Modifier = Modifier
) {
    var showPermissionRequest by remember { mutableStateOf(value = true) }

    if (showPermissionRequest) {
        RequestLocationPermission(
            onPermissionGranted = {
                showPermissionRequest = false
                uiEvents.onLocationPermissionGranted()
            },
            onPermissionDenied = {
                showPermissionRequest = false
                // Optional: You could show a Snackbar here saying "Shops are not sorted by distance."
            }
        )
    }

    if (uiState.showBackgroundPermission) {
        RequestBackgroundLocationPermission(
            onPermissionGranted = {
                uiEvents.onBackgroundPermissionHandled(isGranted = true)
            },
            onPermissionDenied = {
                uiEvents.onBackgroundPermissionHandled(isGranted = false)
            }
        )
    }

    Scaffold(
        topBar = {
            ShoppingTopAppBar(
                titleRes = R.string.shops_near_you_title,
                navigationType = NavigationType.NONE
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, end = 8.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Button(onClick = uiEvents::onEnableGeofencingClick) {
                    Text(text = stringResource(id = R.string.enable_geofencing_button_label))
                }
            }
            HorizontalUncontainedCarousel(
                state = rememberCarouselState(itemCount = { uiState.shops.count() }),
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp, bottom = 16.dp),
                itemWidth = 186.dp,
                itemSpacing = 8.dp,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) { i ->
                val shop = uiState.shops[i]
                Card(
                    modifier = Modifier
                        .height(height = 205.dp)
                        .clip(shape = MaterialTheme.shapes.extraLarge)
                        .clickable { uiEvents.onShopSelected(shop = shop) }
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AsyncImage(
                                model = shop.imageUrl,
                                contentDescription = shop.name,
                                modifier = Modifier.weight(weight = 1f),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(height = 8.dp))
                            Text(
                                text = shop.name,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        if (!shop.isCurrentlyOpen()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.6f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(id = R.string.closed_label),
                                    color = androidx.compose.ui.graphics.Color.White,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    uiState.selectedShop?.let {
        ShopDetailsSheet(
            shop = it,
            onDismiss = uiEvents::onDismissShopDetails
        )
    }
}

@ThemedPreviews
@Composable
private fun ShopsScreenPreview(@PreviewParameter(provider = ShopOrNullProvider::class) shopOrNull: Shop?) {
    ShoppingV10PreviewTheme {
        ShopsScreen(
            uiState = ShopsViewModel.UiState(
                shops = previewShops(),
                selectedShop = shopOrNull
            ),
            uiEvents = object : ShopsViewModel.UiEvents {
                override fun onShopSelected(shop: Shop) {}
                override fun onDismissShopDetails() {}
                override fun onLocationPermissionGranted() {}
                override fun onEnableGeofencingClick() {}
                override fun onBackgroundPermissionHandled(isGranted: Boolean) {}
            }
        )
    }
}