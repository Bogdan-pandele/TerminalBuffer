package org.terminalbuffer.grid

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class RowTest {
    @Test
    fun `clear resets all cells and wrap flag`() {
        val row = Row(10)
        row.isWrapped = true
        row.cells[5].character = 'A'
        row.cells[3].character = 'C'

        row.clear()

        assertTrue(row.cells.all { cell -> cell.character == ' ' })
        assertFalse(row.isWrapped)
    }

    @Test
    fun `init throws exception on invalid length argument`() {
        val exceptionMin = assertThrows(IllegalArgumentException::class.java) {
            Row(0)
        }

        assertTrue(exceptionMin.message!!.contains("Width must be between 1 and 10000"))

        val exceptionMax = assertThrows(IllegalArgumentException::class.java) {
            Row(10001)
        }

        assertTrue(exceptionMax.message!!.contains("Width must be between 1 and 10000"))
    }

    @Test
    fun `constructor accepts valid length argument`() {
        val row = Row(width = 20)

        assertEquals(20, row.width)
        assertFalse(row.isWrapped)
    }

}