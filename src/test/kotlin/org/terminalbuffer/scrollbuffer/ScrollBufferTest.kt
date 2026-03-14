package org.terminalbuffer.scrollbuffer

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.terminalbuffer.grid.Row

class ScrollBufferTest {
    private lateinit var buffer: ScrollBuffer

    @BeforeEach
    fun setup() {
        buffer = ScrollBuffer(500)
    }

    @Test
    fun `init throws exception on invalid size argument`() {
        val exceptionMin = assertThrows(IllegalArgumentException::class.java) {
            ScrollBuffer(499)
        }

        assertTrue(exceptionMin.message!!.contains("Max size must be between 500 and 2000"))

        val exceptionMax = assertThrows(IllegalArgumentException::class.java) {
            ScrollBuffer(2001)
        }

        assertTrue(exceptionMax.message!!.contains("Max size must be between 500 and 2000"))
    }

    @Test
    fun `constructor creates empty buffer`() {
        assertEquals(0, buffer.size)
    }

    @Test
    fun `addRow increases size`() {
        buffer.addRow(Row(80))
        assertEquals(1, buffer.size)
    }

    @Test
    fun `addRow deletes oldest row when maxSize is reached`() {
        val firstRow = Row(80).apply { cells[0].character = 'A' }
        buffer.addRow(firstRow)
        repeat(499) {buffer.addRow(Row(80))}

        buffer.addRow(Row(80))
        assertEquals(500, buffer.size)
        assertNotEquals('A', buffer.getRow(0).cells[0].character)
    }

    @Test
    fun `getRow returns correct row by index`() {
        val row = Row(80).apply { cells[0].character = 'Z' }
        buffer.addRow(row)
        assertEquals('Z', buffer.getRow(0).cells[0].character)
    }

    @Test
    fun `getRow throws exception when buffer is empty`() {
        assertThrows(IllegalArgumentException::class.java) {
            buffer.getRow(0)
        }
    }

    @Test
    fun `getRow throws exception for negative index`() {
        buffer.addRow(Row(80))
        assertThrows(IllegalArgumentException::class.java) {
            buffer.getRow(-1)
        }
    }

    @Test
    fun `getRow throws exception when index exceeds size`() {
        buffer.addRow(Row(80))
        assertThrows(IllegalArgumentException::class.java) {
            buffer.getRow(1)
        }
    }

    @Test
    fun `clear removes all rows`() {
        repeat(10) {buffer.addRow(Row(80))}
        buffer.clear()
        assertEquals(0, buffer.size)
    }
}