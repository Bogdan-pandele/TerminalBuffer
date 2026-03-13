package org.terminalbuffer.grid

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*

class TerminalGridTest {
    @Test
    fun `init throws exception on invalid width`() {
        val exceptionMin = assertThrows(IllegalArgumentException::class.java) {
            TerminalGrid(0, 24)
        }

        assertTrue(exceptionMin.message!!.contains("Width must be between 1 and 10000"))

        val exceptionMax = assertThrows(IllegalArgumentException::class.java) {
            TerminalGrid(10001, 24)
        }

        assertTrue(exceptionMax.message!!.contains("Width must be between 1 and 10000"))
    }

    @Test
    fun `init throws exception on invalid height`() {
        val exceptionMin = assertThrows(IllegalArgumentException::class.java) {
            TerminalGrid(10000, 0)
        }

        assertTrue(exceptionMin.message!!.contains("Height must be between 1 and 10000"))

        val exceptionMax = assertThrows(IllegalArgumentException::class.java) {
            TerminalGrid(24, 10001)
        }

        assertTrue(exceptionMax.message!!.contains("Height must be between 1 and 10000"))
    }

    @Test
    fun `getRow returns correct row for valid boundaries`() {
        val grid = TerminalGrid(30, 24)
        val topRow = grid.getRow(0)
        val bottomRow = grid.getRow(23)

        assertNotNull(topRow, "Top row must not be null")
        assertNotNull(bottomRow, "Bottom row must not be null")
        assertNotSame(bottomRow, topRow, "Top row and bottom row must be different objects")
    }

    @Test
    fun `getRow throws exception for index out of bounds`() {
        val grid = TerminalGrid(30, 24)
        val exceptionMin = assertThrows(IllegalArgumentException::class.java) {
            grid.getRow(-1)
        }

        assertTrue(exceptionMin.message!!.contains("out of bounds"))

        val exceptionMax = assertThrows(IllegalArgumentException::class.java) {
            grid.getRow(24)
        }

        assertTrue(exceptionMax.message!!.contains("out of bounds"))
    }

    @Test
    fun `scrollUp returns old top row and pushes a new default row to bottom`() {
        val grid = TerminalGrid(10, 5)
        grid.getRow(0).cells[0].character = 'A'
        grid.getRow(1).cells[0].character = 'B'
        grid.getRow(2).cells[0].character = 'C'
        grid.getRow(3).cells[0].character = 'D'
        grid.getRow(4).cells[0].character = 'E'

        val oldRow = grid.scrollUp()
        assertEquals('A', oldRow.cells[0].character, "scrollUp must return the row that left the screen")

        val newBottomRow = grid.getRow(4)
        assertEquals(' ', newBottomRow.cells[0].character, "New row must have empty character")
    }

    @Test
    fun `scrollUp handles cyclic wrapping without memory corruption`() {
        val grid = TerminalGrid(10, 5)
        grid.getRow(0).cells[0].character = 'A'
        grid.getRow(1).cells[0].character = 'B'


        repeat(10) { grid.scrollUp() }

        for(y in 0 until 5) {
            assertEquals(' ', grid.getRow(y).cells[0].character, "Row $y should be empty")
        }

        assertEquals(10, grid.getRow(0).width)
    }

    @Test
    fun `clear method resets all rows and bottomUp member`() {
        val grid = TerminalGrid(10, 5)
        grid.getRow(0).cells[0].character = 'A'
        grid.getRow(1).cells[0].character = 'B'
        grid.getRow(2).cells[0].character = 'C'
        grid.getRow(3).cells[0].character = 'D'
        grid.getRow(4).cells[0].character = 'E'

        repeat(2) {grid.scrollUp()}

        grid.clear()

        for(y in 0 until 5) {
            assertEquals(' ', grid.getRow(y).cells[0].character, "Row $y should be empty")
        }

        grid.getRow(0).cells[0].character = 'A'
        val oldTop = grid.scrollUp()
        assertEquals('A', oldTop.cells[0].character, "After clear, grid must behave as freshly initialized")
    }
}