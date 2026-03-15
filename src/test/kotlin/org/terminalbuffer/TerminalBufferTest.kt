package org.terminalbuffer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.terminalbuffer.grid.TerminalColor
import org.terminalbuffer.grid.TextAttributes


class TerminalBufferTest {
    private lateinit var buffer: TerminalBuffer

    @BeforeEach
    fun setup() {
        buffer = TerminalBuffer(80, 24)
    }

    @Nested
    inner class Init {
        @Test
        fun `init throws exception on invalid width`() {
            assertThrows(IllegalArgumentException::class.java) { TerminalBuffer(0, 24) }
            assertThrows(IllegalArgumentException::class.java) { TerminalBuffer(10001, 24) }
        }
        @Test
        fun `init throws exception on invalid height`() {
            assertThrows(IllegalArgumentException::class.java) { TerminalBuffer(80, 0) }
            assertThrows(IllegalArgumentException::class.java) { TerminalBuffer(80, 10001) }
        }

        @Test
        fun `init throws exception on invalid scrollback size`() {
            assertThrows(IllegalArgumentException::class.java) { TerminalBuffer(80, 24, 499) }
            assertThrows(IllegalArgumentException::class.java) { TerminalBuffer(80, 24, 2001) }
        }
    }

    @Nested
    inner class Cursor {
        @Test
        fun `cursor starts at position 0 0`() {
            assertEquals(0, buffer.getCursorX())
            assertEquals(0, buffer.getCursorY())
        }

        @Test
        fun `setCursorPosition moves cursor to valid position`() {
            buffer.setCursorPosition(10, 5)
            assertEquals(10, buffer.getCursorX())
            assertEquals(5, buffer.getCursorY())
        }

        @Test
        fun `setCursorPosition clamps to bounds`() {
            buffer.setCursorPosition(999, 999)
            assertEquals(79, buffer.getCursorX())
            assertEquals(23, buffer.getCursorY())
        }

        @Test
        fun `moveCursorRight moves cursor right`() {
            buffer.moveCursorRight(5)
            assertEquals(5, buffer.getCursorX())
        }

        @Test
        fun `moveCursorLeft moves cursor left`() {
            buffer.setCursorPosition(10, 0)
            buffer.moveCursorLeft(5)
            assertEquals(5, buffer.getCursorX())
        }

        @Test
        fun `moveCursorUp moves cursor up`() {
            buffer.setCursorPosition(0, 10)
            buffer.moveCursorUp(5)
            assertEquals(5, buffer.getCursorY())
        }

        @Test
        fun `moveCursorDown moves cursor down`() {
            buffer.moveCursorDown(5)
            assertEquals(5, buffer.getCursorY())
        }
    }

    @Nested
    inner class WriteText {
        @Test
        fun `writes character at cursor position`() {
            buffer.writeText("A")
            assertEquals('A', buffer.getChar(0, buffer.currentScrollBackSize))
        }

        @Test
        fun `advances cursor after writing`() {
            buffer.writeText("A")
            assertEquals(1, buffer.getCursorX())
        }

        @Test
        fun `writes multiple characters`() {
            buffer.writeText("Hello")
            assertEquals('H', buffer.getChar(0, buffer.currentScrollBackSize))
            assertEquals('o', buffer.getChar(4, buffer.currentScrollBackSize))
            assertEquals(5, buffer.getCursorX())
        }

        @Test
        fun `newline moves cursor to next line`() {
            buffer.writeText("Hello\n")
            assertEquals(0, buffer.getCursorX())
            assertEquals(1, buffer.getCursorY())
        }

        @Test
        fun `carriage return moves cursor to beginning of line`() {
            buffer.writeText("Hello\r")
            assertEquals(0, buffer.getCursorX())
            assertEquals(0, buffer.getCursorY())
        }

        @Test
        fun `text wraps to next line at right edge`() {
            buffer.writeText("A".repeat(80))
            assertEquals(0, buffer.getCursorX())
            assertEquals(1, buffer.getCursorY())
        }

        @Test
        fun `writing past last line triggers scroll`() {
            repeat(24) { buffer.writeText("A\n") }
            assertEquals(23, buffer.getCursorY())
            assertEquals(1, buffer.currentScrollBackSize)
        }

        @Test
        fun `applies current attributes to written characters`() {
            val attrs = TextAttributes(foregroundColor = TerminalColor.RED)
            buffer.setAttributes(attrs)
            buffer.writeText("A")
            assertEquals(TerminalColor.RED, buffer.getAttributes(0, buffer.currentScrollBackSize).foregroundColor)
        }
    }

    @Nested
    inner class InsertText {
        @Test
        fun `inserts character at cursor position`() {
            buffer.insertText("A")
            assertEquals('A', buffer.getChar(0, buffer.currentScrollBackSize))
        }

        @Test
        fun `advances cursor after inserting`() {
            buffer.insertText("A")
            assertEquals(1, buffer.getCursorX())
        }

        @Test
        fun `shifts existing characters to the right`() {
            buffer.writeText("BCD")
            buffer.setCursorPosition(0, 0)
            buffer.insertText("A")
            assertEquals('A', buffer.getChar(0, buffer.currentScrollBackSize))
            assertEquals('B', buffer.getChar(1, buffer.currentScrollBackSize))
            assertEquals('C', buffer.getChar(2, buffer.currentScrollBackSize))
            assertEquals('D', buffer.getChar(3, buffer.currentScrollBackSize))
        }

        @Test
        fun `character at right edge is lost after shift`() {
            buffer.writeText("A".repeat(79))
            buffer.writeText("Z")
            buffer.setCursorPosition(0, 0)
            buffer.insertText("X")
            assertEquals('X', buffer.getChar(0, buffer.currentScrollBackSize))
            assertEquals('A', buffer.getChar(79, buffer.currentScrollBackSize))
        }

        @Test
        fun `newline moves cursor to next line`() {
            buffer.insertText("Hello\n")
            assertEquals(0, buffer.getCursorX())
            assertEquals(1, buffer.getCursorY())
        }

        @Test
        fun `inserting past last line triggers scroll`() {
            repeat(24) { buffer.insertText("A\n") }
            assertEquals(23, buffer.getCursorY())
            assertEquals(1, buffer.currentScrollBackSize)
        }

        @Test
        fun `applies current attributes to inserted characters`() {
            val attrs = TextAttributes(foregroundColor = TerminalColor.BLUE)
            buffer.setAttributes(attrs)
            buffer.insertText("A")
            assertEquals(TerminalColor.BLUE, buffer.getAttributes(0, buffer.currentScrollBackSize).foregroundColor)
        }
    }

    @Nested
    inner class FillLine {
        @Test
        fun `fills current line with given character`() {
            buffer.fillLine('X')
            for (x in 0 until 80) {
                assertEquals('X', buffer.getChar(x, buffer.currentScrollBackSize))
            }
        }

        @Test
        fun `fills current line with space by default`() {
            buffer.writeText("Hello")
            buffer.setCursorPosition(0, 0)
            buffer.fillLine()
            for (x in 0 until 80) {
                assertEquals(' ', buffer.getChar(x, buffer.currentScrollBackSize))
            }
        }

        @Test
        fun `fills only current cursor line`() {
            buffer.writeText("Hello\n")
            buffer.writeText("World")
            buffer.setCursorPosition(0, 0)
            buffer.fillLine('X')
            assertEquals('X', buffer.getChar(0, buffer.currentScrollBackSize))
            assertEquals('W', buffer.getChar(0, buffer.currentScrollBackSize + 1))
        }

        @Test
        fun `applies current attributes when filling`() {
            val attrs = TextAttributes(foregroundColor = TerminalColor.GREEN)
            buffer.setAttributes(attrs)
            buffer.fillLine('X')
            assertEquals(TerminalColor.GREEN, buffer.getAttributes(0, buffer.currentScrollBackSize).foregroundColor)
        }

        @Test
        fun `does not move cursor`() {
            buffer.setCursorPosition(5, 3)
            buffer.fillLine('X')
            assertEquals(5, buffer.getCursorX())
            assertEquals(3, buffer.getCursorY())
        }
    }
    @Nested
    inner class ClearScreen {
        @Test
        fun `clears all characters from screen`() {
            buffer.writeText("Hello")
            buffer.clearScreen()
            for (y in 0 until 24) {
                for (x in 0 until 80) {
                    assertEquals(' ', buffer.getChar(x, buffer.currentScrollBackSize + y))
                }
            }
        }

        @Test
        fun `resets cursor to 0 0`() {
            buffer.setCursorPosition(10, 5)
            buffer.clearScreen()
            assertEquals(0, buffer.getCursorX())
            assertEquals(0, buffer.getCursorY())
        }

        @Test
        fun `does not clear scrollback`() {
            repeat(24) { buffer.writeText("A\n") }
            val scrollSizeBefore = buffer.currentScrollBackSize
            buffer.clearScreen()
            assertEquals(scrollSizeBefore, buffer.currentScrollBackSize)
        }
    }

    @Nested
    inner class ClearScreenAndScrollback {
        @Test
        fun `clears all characters from screen`() {
            buffer.writeText("Hello")
            buffer.clearScreenAndScrollback()
            for (y in 0 until 24) {
                for (x in 0 until 80) {
                    assertEquals(' ', buffer.getChar(x, y))
                }
            }
        }

        @Test
        fun `clears scrollback`() {
            repeat(24) { buffer.writeText("A\n") }
            buffer.clearScreenAndScrollback()
            assertEquals(0, buffer.currentScrollBackSize)
        }

        @Test
        fun `resets cursor to 0 0`() {
            buffer.setCursorPosition(10, 5)
            buffer.clearScreenAndScrollback()
            assertEquals(0, buffer.getCursorX())
            assertEquals(0, buffer.getCursorY())
        }
    }

    @Nested
    inner class ContentAccess {
        @Test
        fun `getChar returns correct character from screen`() {
            buffer.writeText("A")
            assertEquals('A', buffer.getChar(0, buffer.currentScrollBackSize))
        }

        @Test
        fun `getChar returns correct character from scrollback`() {
            repeat(24) { buffer.writeText("A\n") }
            assertEquals('A', buffer.getChar(0, 0))
        }

        @Test
        fun `getChar throws exception for invalid x`() {
            assertThrows(IllegalArgumentException::class.java) { buffer.getChar(-1, 0) }
            assertThrows(IllegalArgumentException::class.java) { buffer.getChar(80, 0) }
        }

        @Test
        fun `getAttributes returns correct attributes from screen`() {
            val attrs = TextAttributes(foregroundColor = TerminalColor.RED)
            buffer.setAttributes(attrs)
            buffer.writeText("A")
            assertEquals(TerminalColor.RED, buffer.getAttributes(0, buffer.currentScrollBackSize).foregroundColor)
        }

        @Test
        fun `getAttributes returns correct attributes from scrollback`() {
            val attrs = TextAttributes(foregroundColor = TerminalColor.BLUE)
            buffer.setAttributes(attrs)
            repeat(24) { buffer.writeText("A\n") }
            assertEquals(TerminalColor.BLUE, buffer.getAttributes(0, 0).foregroundColor)
        }

        @Test
        fun `getRow returns correct line from screen`() {
            buffer.writeText("Hello")
            val line = buffer.getRow(buffer.currentScrollBackSize)
            assertTrue(line.startsWith("Hello"))
        }

        @Test
        fun `getRow returns correct line from scrollback`() {
            repeat(24) { buffer.writeText("A\n") }
            val line = buffer.getRow(0)
            assertTrue(line.startsWith("A"))
        }

        @Test
        fun `getScreenContent returns all screen lines`() {
            buffer.writeText("Hello")
            val content = buffer.getScreenContent()
            assertTrue(content.contains("Hello"))
        }

        @Test
        fun `getScreenContent has correct number of lines`() {
            val content = buffer.getScreenContent()
            assertEquals(24, content.lines().size)
        }

        @Test
        fun `getScreenAndScrollbackContent includes scrollback and screen`() {
            repeat(24) { buffer.writeText("A\n") }
            buffer.writeText("B")
            val content = buffer.getScreenAndScrollbackContent()
            assertTrue(content.contains("A"))
            assertTrue(content.contains("B"))
        }

        @Test
        fun `getScreenAndScrollbackContent includes scrollback lines`() {
            repeat(24) { buffer.writeText("A\n") }
            assertTrue(buffer.currentScrollBackSize > 0)
            val content = buffer.getScreenAndScrollbackContent()
            assertTrue(content.lines().size >= buffer.currentScrollBackSize + 24)
        }
    }

    @Nested
    inner class InsertEmptyLineAtBottom {
        @Test
        fun `pushes top line to scrollback`() {
            buffer.writeText("Hello")
            buffer.insertEmptyLineAtBottom()
            assertEquals(1, buffer.currentScrollBackSize)
        }

        @Test
        fun `new bottom line is empty`() {
            buffer.writeText("Hello")
            buffer.insertEmptyLineAtBottom()
            val bottomLine = buffer.getRow(buffer.currentScrollBackSize + 23)
            assertTrue(bottomLine.isBlank())
        }

        @Test
        fun `shifts all lines up`() {
            buffer.writeText("Hello")
            buffer.insertEmptyLineAtBottom()
            assertEquals('H', buffer.getChar(0, 0))
        }
    }
}