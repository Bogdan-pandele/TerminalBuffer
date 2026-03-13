package org.terminalbuffer.grid
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*


class CellTest {
    @Test
    fun `constructor sets default values correctly`() {
        val cell = Cell()
        assertEquals(' ', cell.character, "Default character must be empty")
        assertEquals(TextAttributes(), cell.textAttributes, "Attributes must have default values")
    }

    @Test
    fun `reset resets cell values to default`() {
        val cell = Cell(character = 'a', textAttributes = TextAttributes(foregroundColor = TerminalColor.BLACK))
        cell.reset()

        assertEquals(' ', cell.character, "Cell character must be empty after reset")
        assertEquals(TerminalColor.DEFAULT, cell.textAttributes.foregroundColor, "Cell foreground color must be default after reset")
    }
}