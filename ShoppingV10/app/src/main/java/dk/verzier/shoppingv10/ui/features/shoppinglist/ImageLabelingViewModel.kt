package dk.verzier.shoppingv10.ui.features.shoppinglist

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.annotation.concurrent.Immutable
import javax.inject.Inject

@HiltViewModel
class ImageLabelingViewModel @Inject constructor(
    @param:ApplicationContext private val context: Context
) : ViewModel() {
    private val _uiState = MutableStateFlow(value = UiState())
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    // Initialize the ML Kit Image Labeler
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    val uiEvents: UiEvents = object : UiEvents {
        override fun onImageSelected(uri: Uri?) {
            _uiState.update { it.copy(isPhotoSaved = false) }
            uri?.let { processImage(uri = it) }
        }

        override fun onLabelConfirmed(label: String) {
            viewModelScope.launch {
                _navigationEvents.emit(value = NavigationEvent.NavigateUpWithResult(label))
            }
        }

        override fun onUpClick() {
            viewModelScope.launch {
                _navigationEvents.emit(value = NavigationEvent.NavigateUp)
            }
        }

        override fun clearLabel() {
            _uiState.update { it.copy(recognizedLabel = null, isError = false, isPhotoSaved = false) }
        }

        override fun saveImageToGallery(uri: Uri) {
            viewModelScope.launch(context = Dispatchers.IO) {
                try {
                    // TODO Save image to the gallery (MediaStore)
                    // HINT: Remember to update the _uiState so the UI knows it was successful

                    val contentResolver = context.contentResolver

                    val contentValues = ContentValues().apply {
                        put(MediaStore.Images.Media.DISPLAY_NAME, "Shopping_Image_${System.currentTimeMillis()}.jpg")
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/ShoppingApp")
                            put(MediaStore.Images.Media.IS_PENDING, 1)
                        }
                    }

                    val destinationUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                    if (destinationUri != null) {
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            contentResolver.openOutputStream(destinationUri)?.use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }

                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                            contentValues.clear()
                            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                            contentResolver.update(destinationUri, contentValues, null, null)
                        }

                        _uiState.update { it.copy(isPhotoSaved = true, isError = false) }

                    } else {
                        _uiState.update { it.copy(isPhotoSaved = false, isError = true) }
                    }
                } catch (e: Exception) {
                    // In a production app, you might want to show an error state here
                    e.printStackTrace()
                    _uiState.update { it.copy(isPhotoSaved = false, isError = true) }
                }
            }
        }
    }

    private fun processImage(uri: Uri) {
        _uiState.update { it.copy(isLoading = true, isError = false) }
        try {
            // 1. Prepare the input image using a file URI
            val image = InputImage.fromFilePath(/* context = */ context, /* imageUri = */ uri)

            // 2. Configure and run the image labeler
            labeler.process(/* p0 = */ image)
                .addOnSuccessListener { labels ->
                    // 3. Get information about labeled objects
                    // We take the highest-confidence label by grabbing the first item.
                    val bestLabel = labels.firstOrNull()?.text
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            recognizedLabel = bestLabel,
                            isError = bestLabel == null
                        )
                    }
                }
                .addOnFailureListener {
                    _uiState.update { it.copy(isLoading = false, isError = true) }
                }
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, isError = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        labeler.close() // Prevent memory leaks
    }

    data class UiState(
        val isLoading: Boolean = false,
        val recognizedLabel: String? = null,
        val isError: Boolean = false,
        val isPhotoSaved: Boolean = false,
    )

    @Immutable
    interface UiEvents {
        fun onImageSelected(uri: Uri?)
        fun onLabelConfirmed(label: String)
        fun onUpClick()
        fun clearLabel()
        fun saveImageToGallery(uri: Uri)
    }

    sealed class NavigationEvent {
        data object NavigateUp : NavigationEvent()
        data class NavigateUpWithResult(val label: String) : NavigationEvent()
    }
}