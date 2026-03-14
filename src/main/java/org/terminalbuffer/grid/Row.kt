package org.terminalbuffer.grid

class Row(val width: Int) {
    val cells: Array<Cell> = Array(width){ Cell() }
    var isWrapped = false

    fun clear() {
        cells.forEach{ cell -> cell.reset() }
        isWrapped = false
    }

    init {
        require(width in 1..10000) {
            "Width must be between 1 and 10000, got $width"
        }
    }
}