package kr.co.characin.gemini_api_example

import org.junit.Test

import org.junit.Assert.*

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
    fun messagePrompt_shouldCreatePromptMessage() {
        val msg = Message.prompt("test")
        assertEquals(Message.TYPE_PROMPT, msg.type)
        assertEquals("test", msg.text)
        assertNull(msg.bitmap)
    }

    @Test
    fun messageResponse_shouldCreateResponseMessage() {
        val msg = Message.response("response")
        assertEquals(Message.TYPE_RESPONSE, msg.type)
        assertEquals("response", msg.text)
        assertNull(msg.bitmap)
    }
}
