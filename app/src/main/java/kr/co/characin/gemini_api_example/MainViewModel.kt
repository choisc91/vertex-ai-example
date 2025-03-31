@file:OptIn(PublicPreviewAPI::class)

package kr.co.characin.gemini_api_example

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
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

    // 지금까지의 Prompt 및 reponse를 제거합니다.
    fun clearData() {
        _message.clear()
    }

    // Multi modal & text prompt에 대한 response를 요청합니다.
    fun sendPrompt(prompt: String, bitmap: Bitmap? = null) {
        if (prompt.isEmpty())
            return

        viewModelScope.launch {
            setInProgress(inProgress = true)
            addPrompt(prompt, bitmap)

            try {
                val response = if (bitmap != null)
                    _generativeModel.generateContent(buildMultiModealContent(bitmap, prompt))
                else
                    _generativeModel.generateContent(prompt)

                addTextResponse(response = response.text ?: MESSAGE_FAILED)

            } catch (e: Exception) {
                addTextResponse(response = "$MESSAGE_EXCEPTION, ${e.message}")
            }

            setInProgress(inProgress = false)
        }
    }

    // 이미지 생성에 대한 response를 요청합니다.
    // 현재 prompt는 English only 입니다.
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

    // Progress 상태를 업데이트 합니다.
    private fun setInProgress(inProgress: Boolean) {
        _inProgressFlow.value = inProgress
    }

    // 프롬프트 상태를 업데이트합니다.
    private fun addPrompt(prompt: String, bitmap: Bitmap? = null) {
        _message.add(element = Message.prompt(prompt, bitmap))
    }

    // 리스폰스 상태를 업데이트합니다. text only.
    private fun addTextResponse(response: String) {
        _message.add(element = Message.response(response))
    }

    // 리스폰스 상태를 업데이트 합니다. multi modal.
    private fun addImageResponse(bitmap: Bitmap) {
        _message.add(element = Message.response(response = "", bitmap = bitmap))
    }

    // Multi modal request content를 반환합니다.
    private fun buildMultiModealContent(bitmap: Bitmap, prompt: String) = content {
        image(bitmap)
        text(prompt)
    }
}