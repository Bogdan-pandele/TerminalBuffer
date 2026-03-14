package org.terminalbuffer.scrollbuffer

import org.terminalbuffer.grid.Row

class ScrollBuffer(private val maxSize: Int) {
    init {
        require(maxSize in 500..2000) {
            "Max size must be between 500 and 2000, got $maxSize"
        }
    }

    private val rows = ArrayDeque<Row>(maxSize)
    val size get() = rows.size

    fun addRow(row: Row) {
        if(rows.size >= maxSize) {
            rows.removeFirst()
        }
        rows.addLast(row)
    }

    fun getRow(index: Int): Row {
        require(index in 0 until rows.size) {
            "Index $index out of bounds for scrollback size ${rows.size}"
        }

        return rows[index]
    }

    fun clear() = rows.clear()
}