package kr.co.characin.gemini_api_example

import android.graphics.Bitmap

data class Message(
    val type: Int,
    val text: String,
    val bitmap: Bitmap? = null,
) {
    companion object {
        const val TYPE_PROMPT = 0
        const val TYPE_RESPONSE = 1

        fun prompt(prompt: String, bitmap: Bitmap? = null) = Message(
            type = TYPE_PROMPT,
            text = prompt,
            bitmap = bitmap,
        )

        fun response(response: String, bitmap: Bitmap? = null) = Message(
            type = TYPE_RESPONSE,
            text = response,
            bitmap = bitmap,
        )
    }
}