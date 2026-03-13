package org.terminalbuffer.grid
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TextAttributesTest {
    @Test
    fun `constructor throws exception on invalid flags`() {
        val exceptionMsg = assertThrows(IllegalArgumentException::class.java) {
            TextAttributes(flags = (1 shl 3))
        }

        assertTrue(exceptionMsg.message!!.contains("Invalid flags:"))
    }

    @Test
    fun `constructor accepts valid combined flags without throwing an exception`() {
        val validAttrs = TextAttributes(flags = TextAttributes.BOLD or TextAttributes.UNDERLINE)
        assertEquals(5, validAttrs.flags)
    }
}