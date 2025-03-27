@file:OptIn(PublicPreviewAPI::class)

package kr.co.characin.gemini_api_example

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.vertexai.type.Content
import com.google.firebase.vertexai.type.PublicPreviewAPI
import com.google.firebase.vertexai.type.content
import com.google.firebase.vertexai.vertexAI
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    companion object {
        private const val MESSAGE_FAILED = "An unknown error occurred during prompt execution. Please run the prompt again"
        private const val MESSAGE_EXCEPTION = "The following error occurred during prompt execution. Please refer to the error log"
    }

    private val _generativeModel = Firebase.vertexAI.generativeModel("gemini-2.0-flash")
    private val _imagen3Model = Firebase.vertexAI.imagenModel("imagen-3.0-generate-002")

    private val _inProgressFlow = MutableStateFlow(value = false)
    val inProgressFlow: StateFlow<Boolean> get() = _inProgressFlow

    private val _message = mutableStateListOf<Message>()
    val message: List<Message> get() = _message

    fun clearData() {
        _message.clear()
    }

    fun sendPrompt(prompt: String, bitmap: Bitmap? = null) {
        if (prompt.isEmpty())
            return

        viewModelScope.launch {
            setInProgress(inProgress = true)
            addPrompt(prompt, bitmap)
            try {
                if (bitmap != null) {
                    // MultiModal.
                    val multiModalPrompt = content {
                        image(bitmap)
                        text(prompt)
                    }
                    val response = _generativeModel.generateContent(multiModalPrompt)
                    addTextResponse(response = response.text ?: MESSAGE_FAILED)

                } else {
                    // Text.
                    val response = _generativeModel.generateContent(prompt)
                    addTextResponse(response = response.text ?: MESSAGE_FAILED)
                }

            } catch (e: Exception) {
                addTextResponse(response = "$MESSAGE_EXCEPTION, ${e.message}")
            }

            setInProgress(inProgress = false)
        }
    }

    fun sendRequestImagePrompt(prompt: String) {
        if (prompt.isEmpty())
            return

        viewModelScope.launch {
            setInProgress(inProgress = true)
            addPrompt(prompt)
            try {
                val imageResponse = _imagen3Model.generateImages(prompt)
                val image = imageResponse.images.first()
                addImageResponse(bitmap = image.asBitmap())

            } catch (e: Exception) {
                addTextResponse(response = "$MESSAGE_EXCEPTION, ${e.message}")
            }
            setInProgress(inProgress = false)
        }
    }

    private fun setInProgress(inProgress: Boolean) {
        _inProgressFlow.value = inProgress
    }

    private fun addPrompt(prompt: String, bitmap: Bitmap? = null) {
        _message.add(element = Message.prompt(prompt, bitmap))
    }

    private fun addTextResponse(response: String) {
        _message.add(element = Message.response(response))
    }

    private fun addImageResponse(bitmap: Bitmap) {
        _message.add(element = Message.response(response = "", bitmap = bitmap))
    }
}