package dk.verzier.shoppingv6.ui.features.shops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import coil.compose.AsyncImage
import dk.verzier.shoppingv6.R
import dk.verzier.shoppingv6.domain.Shop
import dk.verzier.shoppingv6.ui.components.NavigationType
import dk.verzier.shoppingv6.ui.components.ShopOrNullProvider
import dk.verzier.shoppingv6.ui.components.ShoppingTopAppBar
import dk.verzier.shoppingv6.ui.components.ThemedPreviews
import dk.verzier.shoppingv6.ui.components.previewShops
import dk.verzier.shoppingv6.ui.navigation.AppRoute
import dk.verzier.shoppingv6.ui.theme.ShoppingV6Theme
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
    Scaffold(
        topBar = {
            ShoppingTopAppBar(
                titleRes = R.string.shops_near_you_title,
                navigationType = NavigationType.NONE
            )
        }
    ) { paddingValues ->
        HorizontalUncontainedCarousel(
            state = rememberCarouselState { uiState.shops.count() },
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(top = 64.dp, bottom = 16.dp),
            itemWidth = 186.dp,
            itemSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) { i ->
            val shop = uiState.shops[i]
            Card(
                modifier = Modifier
                    .height(height = 205.dp)
                    .clip(shape = MaterialTheme.shapes.extraLarge)
                    .clickable { uiEvents.onShopSelected(shop) }
            ) {
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
    ShoppingV6Theme {
        ShopsScreen(
            uiState = ShopsViewModel.UiState(
                shops = previewShops(),
                selectedShop = shopOrNull
            ),
            uiEvents = object : ShopsViewModel.UiEvents {
                override fun onShopSelected(shop: Shop) {}
                override fun onDismissShopDetails() {}
            }
        )
    }
}