package org.terminalbuffer.grid


data class Cell(
    var character: Char = ' ',
    var textAttributes: TextAttributes = TextAttributes()
)
{
    fun reset() {
        this.character = ' ';
        this.textAttributes = TextAttributes()
    }
}