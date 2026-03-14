package org.terminalbuffer.cursor

class Cursor(private val gridWidth: Int, private val gridHeight: Int) {
    init {
        require(gridWidth in 1..10000){
            "Width must be between 1 and 10000, got $gridWidth"
        }

        require(gridHeight in 1..10000) {
            "Height must be between 1 and 10000, got $gridHeight"
        }
    }

    var x: Int = 0
        private set
    var y: Int = 0
        private set

    var isVisible = true

    fun moveTo(newX: Int, newY: Int) {
        x = newX.coerceIn(0, gridWidth -1)
        y = newY.coerceIn(0, gridHeight - 1)
    }

    fun moveUp(n: Int) {
        if(n <= 0) return

        y = (y - n).coerceAtLeast(0)
    }

    fun moveDown(n: Int) {
        if(n <= 0) return

        y = (y + n).coerceAtMost(gridHeight - 1)
    }

    fun moveLeft(n: Int) {
        if(n <= 0) return

        x = (x - n).coerceAtLeast(0)
    }

    fun moveRight(n: Int) {
        if(n <= 0) return

        x = (x + n).coerceAtMost(gridWidth - 1)
    }

    /**
     * Moves the cursor one step to the right.
     * @return true if the cursor advanced, false if it was already at the right edge.
     */
    fun advanceRight(): Boolean {
        if(x < gridWidth - 1) {
            x++
            return true
        }
        return false
    }

    /**
     * Moves the cursor one step down
     * @return true if the cursor advanced, false if it was already at the bottom edge.
     */
    fun advanceDown(): Boolean {
        if(y < gridHeight - 1) {
            y++
            return true
        }
        return false
    }

    fun carriageReturn() {
        x = 0
    }
}