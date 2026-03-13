package org.terminalbuffer

import org.terminalbuffer.grid.TerminalColor


class TerminalBuffer {

}



object Main {
    @JvmStatic
    fun main(args: Array<String>) {

        println(TerminalColor.values().size)
        for (i in 1..5) {
            println("i = " + i)
        }
    }
}