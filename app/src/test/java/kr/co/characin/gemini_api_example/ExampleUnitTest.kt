package kr.co.characin.gemini_api_example

import org.junit.Test
import org.junit.Assert.*

import kr.co.characin.gemini_api_example.Message

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun prompt_returnsPromptMessage() {
        val text = "prompt text"
        val message = Message.prompt(text, null)

        assertEquals(Message.TYPE_PROMPT, message.type)
        assertEquals(text, message.text)
        assertNull(message.bitmap)
    }

    @Test
    fun response_returnsResponseMessage() {
        val text = "response text"
        val message = Message.response(text, null)

        assertEquals(Message.TYPE_RESPONSE, message.type)
        assertEquals(text, message.text)
        assertNull(message.bitmap)
    }
}