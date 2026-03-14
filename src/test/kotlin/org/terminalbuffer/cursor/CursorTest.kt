package org.terminalbuffer.cursor
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Assertions.*

class CursorTest {
    private lateinit var cursor: Cursor

    @BeforeEach
    fun setup() {
        cursor = Cursor(30, 40)
    }
    @Test
    fun `init throws exception on invalid width`() {
        val exceptionMin = assertThrows(IllegalArgumentException::class.java) {
            Cursor(0, 24)
        }

        assertTrue(exceptionMin.message!!.contains("Width must be between 1 and 10000"))

        val exceptionMax = assertThrows(IllegalArgumentException::class.java) {
            Cursor(10001, 24)
        }

        assertTrue(exceptionMax.message!!.contains("Width must be between 1 and 10000"))
    }

    @Test
    fun `init throws exception on invalid height`() {
        val exceptionMin = assertThrows(IllegalArgumentException::class.java) {
            Cursor(10000, 0)
        }

        assertTrue(exceptionMin.message!!.contains("Height must be between 1 and 10000"))

        val exceptionMax = assertThrows(IllegalArgumentException::class.java) {
            Cursor(24, 10001)
        }

        assertTrue(exceptionMax.message!!.contains("Height must be between 1 and 10000"))
    }

    @Test
    fun `moveTo sets cursor to valid postion`() {
        cursor.moveTo(13, 5)
        assertEquals(13, cursor.x)
        assertEquals(5, cursor.y)
    }

    @Test
    fun `moveTo clamps to bounds when postion is out of range`() {
        cursor.moveTo(30, 40)

        assertEquals(29, cursor.x, "x should clamp to gridWidth - 1 when exceeding bounds")
        assertEquals(39, cursor.y, "y should clamp to gridHeight - 1 when exceeding bounds")

        cursor.moveTo(-1, -1)

        assertEquals(0, cursor.x, "x should clamp to 0 when negative")
        assertEquals(0, cursor.y, "y should clamp to 0 when negative")
    }

    @Test
    fun `moveUp decreases y given a valid n value`() {
        cursor.moveTo(0, 39)
        cursor.moveUp(39)

        assertEquals(0, cursor.y)
    }

    @Test
    fun `moveUp does not modify y when given a negative n value`() {
        cursor.moveUp(-1)
        assertEquals(0, cursor.y)
    }

    @Test
    fun `moveUp clamps to 0 when n value is too big`() {
        cursor.moveUp(40)
        assertEquals(0, cursor.y)
    }

    @Test
    fun `moveUp does not change y when given n = 0`() {
        cursor.moveTo(0, 5)
        cursor.moveUp(0)
        assertEquals(5, cursor.y)
    }

    @Test
    fun `moveDown increases y given a valid n value`() {
        cursor.moveDown(39)

        assertEquals(39, cursor.y)
    }

    @Test
    fun `moveDown does not modify y when given a negative n value`() {
        cursor.moveDown(-1)
        assertEquals(0, cursor.y)
    }

    @Test
    fun `moveDown clamps to gridHeight - 1 when n value is too big`() {
        cursor.moveDown(40)
        assertEquals(39, cursor.y)
    }

    @Test
    fun `moveDown does not change y when given n = 0`() {
        cursor.moveTo(0, 5)
        cursor.moveDown(0)
        assertEquals(5, cursor.y)
    }


    @Test
    fun `moveLeft decreases x given a valid n value`() {
        cursor.moveTo(29, 0)
        cursor.moveLeft(29)
        assertEquals(0, cursor.x)
    }

    @Test
    fun `moveLeft does not modify x when given a negative n value`() {
        cursor.moveLeft(-1)
        assertEquals(0, cursor.x)
    }

    @Test
    fun `moveLeft clamps to 0 when n value is too big`() {
        cursor.moveTo(29, 0)
        cursor.moveLeft(30)
        assertEquals(0, cursor.x)
    }

    @Test
    fun `moveLeft does not change x when given n = 0`() {
        cursor.moveTo(5, 0)
        cursor.moveLeft(0)
        assertEquals(5, cursor.x)
    }


    @Test
    fun `moveRight increases x given a valid n value`() {
        cursor.moveRight(29)
        assertEquals(29, cursor.x)
    }

    @Test
    fun `moveRight does not modify x when given a negative n value`() {
        cursor.moveRight(-1)
        assertEquals(0, cursor.x)
    }

    @Test
    fun `moveRight clamps to gridWidth - 1 when n value is too big`() {
        cursor.moveTo(0, 0)
        cursor.moveRight(30)
        assertEquals(29, cursor.x)
    }

    @Test
    fun `moveRight does not change x when given n = 0`() {
        cursor.moveTo(5, 0)
        cursor.moveRight(0)
        assertEquals(5, cursor.x)
    }

    @Test
    fun `carriageReturn resets x to 0`() {
        cursor.moveTo(5, 15)
        cursor.carriageReturn()
        assertEquals(0, cursor.x)
        assertEquals(15, cursor.y)
    }

    @Test
    fun `advanceRight increments x and returns true when not at edge`() {
        val result = cursor.advanceRight()
        assertEquals(1, cursor.x)
        assertTrue(result)
    }

    @Test
    fun `advanceRight returns false and does not increment x when at edge`() {
        cursor.moveTo(29, 0)
        val result = cursor.advanceRight()
        assertEquals(29, cursor.x)
        assertFalse(result)
    }

    @Test
    fun `advanceDown increments y and returns true when not at bottom`() {
        val result = cursor.advanceDown()
        assertEquals(1, cursor.y)
        assertTrue(result)
    }

    @Test
    fun `advanceDown returns false and does not increment y when at bottom`() {
        cursor.moveTo(0, 39)
        val result = cursor.advanceDown()
        assertEquals(39, cursor.y)
        assertFalse(result)
    }
}