package dk.verzier.shoppingv10.ui.features.shoppinglist

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dk.verzier.shoppingv10.R
import dk.verzier.shoppingv10.ui.components.NavigationType
import dk.verzier.shoppingv10.ui.components.ShoppingTopAppBar
import dk.verzier.shoppingv10.ui.components.ThemedPreviews
import dk.verzier.shoppingv10.ui.navigation.AppRoute
import dk.verzier.shoppingv10.ui.theme.ShoppingV10PreviewTheme
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
object ImageLabeling : AppRoute

@Composable
fun ImageLabelingScreen(
    onNavigate: (ImageLabelingViewModel.NavigationEvent) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ImageLabelingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = Unit) {
        viewModel.navigationEvents.collect {
            onNavigate(it)
        }
    }

    ImageLabelingScreen(
        uiState = uiState,
        uiEvents = viewModel.uiEvents,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageLabelingScreen(
    uiState: ImageLabelingViewModel.UiState,
    uiEvents: ImageLabelingViewModel.UiEvents,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var cameraUri by remember { mutableStateOf<Uri?>(value = null) }

    var isFromCamera by remember { mutableStateOf(false) }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        isFromCamera = false
        uiEvents.onImageSelected(uri)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success: Boolean ->
        if (success) {
            isFromCamera = true
            uiEvents.onImageSelected(cameraUri)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val file = File(/* parent = */ context.cacheDir, /* child = */ "temp_image.jpg")

            val uri = FileProvider.getUriForFile(
                /* context = */ context,
                /* authority = */ "dk.verzier.shoppinglist.provider",
                /* file = */ file
            )

            cameraUri = uri
            cameraLauncher.launch(input = uri)
        }
    }

    Scaffold(
        topBar = {
            ShoppingTopAppBar(
                titleRes = R.string.image_labeling_title,
                navigationType = NavigationType.BACK,
                onUpClick = uiEvents::onUpClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(all = 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(height = 16.dp))
                Text(text = stringResource(id = R.string.analyzing_image))
            } else if (uiState.recognizedLabel != null) {
                Text(
                    text = stringResource(id = R.string.looks_like_a),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = uiState.recognizedLabel,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(height = 32.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(space = 16.dp)) {
                    OutlinedButton(onClick = {
                        isFromCamera = false
                        uiEvents.clearLabel()
                    }) {
                        Text(text = stringResource(id = R.string.try_again))
                    }
                    Button(onClick = { uiEvents.onLabelConfirmed(uiState.recognizedLabel) }) {
                        Text(text = stringResource(id = R.string.use_label))
                    }
                }

                if (isFromCamera && cameraUri != null) {
                    Spacer(modifier = Modifier.height(height = 16.dp))
                    TextButton(
                        onClick = { uiEvents.saveImageToGallery(uri = cameraUri!!) },
                        enabled = !uiState.isPhotoSaved
                    ) {
                        Text(text = stringResource(id = if (uiState.isPhotoSaved) R.string.save_to_gallery_success else R.string.save_to_gallery_action))
                    }
                }
            } else {
                if (uiState.isError) {
                    Text(
                        text = stringResource(id = R.string.could_not_identify),
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(height = 16.dp))
                }

                Button(
                    onClick = { permissionLauncher.launch(input = Manifest.permission.CAMERA) },
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f)
                ) {
                    Text(text = stringResource(id = R.string.take_a_photo))
                }
                Spacer(modifier = Modifier.height(height = 16.dp))
                OutlinedButton(
                    onClick = {
                        galleryLauncher.launch(input = PickVisualMediaRequest(mediaType = ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    modifier = Modifier.fillMaxWidth(fraction = 0.8f)
                ) {
                    Text(text = stringResource(id = R.string.choose_from_gallery))
                }
            }
        }
    }
}

@ThemedPreviews
@Composable
fun ImageLabelingScreenPreview() {
    ShoppingV10PreviewTheme {
        ImageLabelingScreen(
            modifier = Modifier,
            uiState = ImageLabelingViewModel.UiState(),
            uiEvents = object : ImageLabelingViewModel.UiEvents {
                override fun onImageSelected(uri: Uri?) {}
                override fun onUpClick() {}
                override fun clearLabel() {}
                override fun onLabelConfirmed(label: String) {}
                override fun saveImageToGallery(uri: Uri) {}
            }
        )
    }
}