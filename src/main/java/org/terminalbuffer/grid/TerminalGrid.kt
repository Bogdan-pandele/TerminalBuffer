package org.terminalbuffer.grid

/**
 * This class is the visual memory representation of the terminal,
 * implemented as a Ring Buffer data structure (Circular Array).
 * @param width Number of columns the user can see in the terminal.
 * @param height Number of rows the user can see in the terminal.
 */
class TerminalGrid(val width: Int, val height: Int) {

    init {
        require(width in 1..10000) {
            "Width must be between 1 and 10000, got $width"
        }

        require(height in 1..10000) {
            "Height must be between 1 and 10000, got $height"
        }
    }

    private val rows: Array<Row> = Array(height) { Row(width) }
    private var bottomUp = height - 1

    fun getRow(y: Int): Row {
        require(y in 0..<height) {"Y coordinate ($y) is out of bounds for grid height $height"}
        val topRowIndex : Int = (bottomUp + 1) % height
        val realIndex = (topRowIndex + y) % height

        return rows[realIndex]
    }

    /**
     * Rotates the buffer up by one row, making room for the text at the bottom.
     * The old top row is returned so it can be saved into the scrollback history
     * by the TerminalBuffer.
     * @return The [Row] that was pushed off the screen
     */
    fun scrollUp() : Row {
        val topIndex = (bottomUp  + 1) % height
        val oldTopRow = rows[topIndex]
        rows[topIndex] = Row(width)
        bottomUp = (bottomUp + 1) % height
        return oldTopRow
    }

    fun clear() {
        rows.forEach { r -> r.clear() }
        bottomUp = height - 1
    }

}