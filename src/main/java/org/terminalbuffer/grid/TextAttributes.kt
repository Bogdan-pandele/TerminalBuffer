package org.terminalbuffer.grid

data class TextAttributes (
    var foregroundColor: TerminalColor = TerminalColor.DEFAULT,
    var backgroundColor: TerminalColor = TerminalColor.DEFAULT,
    var flags: Int = 0,
)
{
    companion object {
        const val BOLD = 1 shl 0
        const val ITALIC = 1 shl 1
        const val UNDERLINE = 1 shl 2
        //Todo: add more flags
        private const val ALL_FLAGS = BOLD or ITALIC or UNDERLINE
    }


    init {
        require(flags and ALL_FLAGS.inv() == 0) {
            "Invalid flags: $flags"
        }
    }
}

