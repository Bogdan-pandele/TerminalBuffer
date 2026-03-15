package org.terminalbuffer

import org.terminalbuffer.cursor.Cursor
import org.terminalbuffer.grid.Row
import org.terminalbuffer.grid.TerminalGrid
import org.terminalbuffer.grid.TextAttributes
import org.terminalbuffer.scrollbuffer.ScrollBuffer

class TerminalBuffer(
    val width: Int,
    val height: Int,
    maxScrollBackSize: Int = 500
) {
    init {
        require(width in 1..10000) {
            "Width must be between 1 and 10000, got $width"
        }
        require(height in 1..10000) {
            "Height must be between 1 and 10000, got $height"
        }
        require(maxScrollBackSize in 500..2000) {
            "Scrollback size must be between 500 and 2000, got $maxScrollBackSize"
        }
    }

    private val grid = TerminalGrid(width, height)
    private val scrollBuffer = ScrollBuffer(maxScrollBackSize)
    private val cursor = Cursor(width, height)
    private var currentAttributes = TextAttributes()

    val currentScrollBackSize: Int
        get() = scrollBuffer.size

    fun setAttributes(attributes: TextAttributes) {
        currentAttributes = attributes
    }

    fun getCursorX(): Int {
        return cursor.x
    }

    fun getCursorY(): Int {
        return cursor.y
    }

    fun setCursorPosition(x: Int, y: Int) {
        cursor.moveTo(x, y)
    }

    fun moveCursorLeft(n: Int) {
        cursor.moveLeft(n)
    }

    fun moveCursorRight(n: Int) {
        cursor.moveRight(n)
    }

    fun moveCursorUp(n: Int) {
        cursor.moveUp(n)
    }

    fun moveCursorDown(n: Int) {
        cursor.moveDown(n)
    }

    /**
     * Writes text at the current cursor position, overwriting existing content.
     * Handles newlines, carriage returns, line wrapping and scrolling
     */
    fun writeText(text: String) {
        for (ch in text) {
            when (ch) {
                '\n' -> {
                    newLine()
                }

                '\r' -> cursor.carriageReturn()
                else -> {
                    val row = grid.getRow(cursor.y)
                    row.cells[cursor.x].character = ch
                    row.cells[cursor.x].textAttributes = currentAttributes.copy()
                    if(!cursor.advanceRight()) {
                        newLine()
                    }
                }
            }
        }
    }

    /**
     * Inserts text at the current cursor position, shifting existing characters to the right
     * Characters that fall off the right edge are lost.
     * Handles newlines, carriage returns and scrolling.
     */
    fun insertText(text: String) {
        for(ch in text) {
            when (ch) {
                '\n' -> {
                    newLine()
                }
                '\r' -> cursor.carriageReturn()
                else -> {
                    val row = grid.getRow(cursor.y)
                    for(i in width - 1 downTo cursor.x + 1) {
                        row.cells[i].character = row.cells[i - 1].character
                        row.cells[i].textAttributes = row.cells[i - 1].textAttributes.copy()
                    }
                    row.cells[cursor.x].character = ch
                    row.cells[cursor.x].textAttributes = currentAttributes.copy()

                    if(!cursor.advanceRight()) {
                        newLine()
                    }
                }
            }
        }
    }

    fun fillLine(ch: Char = ' ') {
        val row = grid.getRow(cursor.y)
        for(i in 0..<width) {
            row.cells[i].character = ch
            row.cells[i].textAttributes = currentAttributes.copy()
        }
    }

    fun insertEmptyLineAtBottom() {
        scrollBuffer.addRow(grid.scrollUp())
    }

    fun clearScreen() {
        grid.clear()
        cursor.moveTo(0, 0)
    }

    fun clearScreenAndScrollback() {
        grid.clear()
        scrollBuffer.clear()
        cursor.moveTo(0, 0)
    }

    fun getChar(x: Int, y: Int): Char {
        require(x in 0 until width) { "X coordinate ($x) out of bounds" }
        return getRowFromTerminal(y).cells[x].character
    }

    fun getAttributes(x: Int, y: Int): TextAttributes {
        require(x in 0 until width) { "X coordinate ($x) out of bounds" }
        return getRowFromTerminal(y).cells[x].textAttributes
    }

    /**
     * Returns the row at the given y coordinate using unified indexing:
     * y = 0 is the oldest scrollback line
     * y = scrollBuffer.size is the first screen line
     */
    fun getRow(y: Int): String {
        val row = getRowFromTerminal(y)
        return buildString {
            for (cell in row.cells) {
                append(cell.character)
            }
        }
    }

    fun getScreenContent(): String {
        return buildString {
            for(y in 0..<height) {
                val row = grid.getRow(y)
                for(cell in row.cells) {
                    append(cell.character)
                }
                append('\n')
            }
        }.removeSuffix("\n")
    }

    fun getScreenAndScrollbackContent(): String {
        return buildString {
            for (y in 0 until scrollBuffer.size + height) {
                append(getRow(y))
                append('\n')
            }
        }
    }


    private fun newLine() {
        cursor.carriageReturn()
        if(!cursor.advanceDown()) {
            scrollBuffer.addRow(grid.scrollUp())
        }
    }

    /**
     * Returns a Row using unified indexing across scrollback and screen.
     * y = 0 -> oldest scrollback line
     * y = scrollBuffer.size - 1 -> most recent scrollback line
     * y = scrollBuffer.size -> first screen line(row 0)
     * y = scrollBuffer.size + height - 1 -> last screen line
     */
    private fun getRowFromTerminal(y: Int): Row {
        require(y in 0 until scrollBuffer.size + height) {
            "y coordinate ($y) out of bounds for total size ${scrollBuffer.size + height}"
        }

        return if(y < scrollBuffer.size) {
            scrollBuffer.getRow(y)
        } else {
            grid.getRow(y - scrollBuffer.size)
        }
    }
}