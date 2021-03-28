package com.kmrite

import android.util.Log
import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.io.RandomAccessFile
import java.nio.ByteBuffer

data class Memory(val pkg: String) {
    var pid: Int = 0
    var sAddress = 0.toLong()
}

class Tools {
    companion object {
        private const val TAG = "TOOLS : "
        fun setCode(pkg: String, lib: String, offset: Int, hex: String): Boolean {
            val mem = Memory(pkg)
            getProcessID(mem)
            return if (mem.pid > 1 && mem.sAddress > 1) {
                memEdit(mem, offset, hex)
            } else {
                parseMap(mem, lib)
                memEdit(mem, offset, hex)
            }
        }

        private fun parseMap(nmax: Memory, lib_name: String) {
            File("/proc/${nmax.pid}/maps").useLines { liness ->
                liness.forEach {
                    if (it.contains(lib_name) && nmax.sAddress == 0L) {
                        val lines = it.replace("\\s+".toRegex(), " ") //Removing WhiteSpace
                        val regex = "\\p{XDigit}+".toRegex()
                        val result: String = regex.find(lines)?.value!!
                        nmax.sAddress = result.toLong(16)
                    }
                }
            }
        }

        //Credit Code ("https://github.com/jbro129")
        private fun hex2b(hexs: String): ByteArray {
            var hex: String? = hexs
            if (hex!!.contains(" ")) {
                hex = hex.replace(" ", "")
            }
            return if (hex.length % 2 != 0) {
                throw IllegalArgumentException("Unexpected hex string: $hex")
            } else {
                val result = ByteArray(hex.length / 2)
                for (i in result.indices) {
                    val d1 = decodeHexDigit(hex[i * 2]) shl 4
                    val d2 = decodeHexDigit(hex[i * 2 + 1])
                    result[i] = (d1 + d2).toByte()
                }
                result
            }
        }

        //Credit Code From ("https://github.com/jbro129")
        private fun decodeHexDigit(paramChar: Char): Int {
            if (paramChar in '0'..'9') {
                return paramChar - '0'
            }
            if (paramChar in 'a'..'f') {
                return paramChar - 'a' + 10
            }
            if (paramChar in 'A'..'F') {
                return paramChar - 'A' + 10
            }
            throw IllegalArgumentException("Unexpected hex digit: $paramChar")
        }

        private fun memEdit(nmax: Memory, offset: Int, hex_t: String): Boolean {
            return try {
                RandomAccessFile("/proc/${nmax.pid}/mem", "rw").use { channel ->
                    channel.channel.use { channels ->
                        val buff: ByteBuffer = ByteBuffer.wrap(hex2b(hex_t))
                        for (i in 0 until hex_t.length / 2) {
                            channels.write(buff, (nmax.sAddress + offset) + i)
                        }
                    }
                }
                true
            } catch (e: Exception) {
                Log.w(TAG, e)
                false
            }
        }

        private fun getProcessID(nmax: Memory) {
            val process = Runtime.getRuntime().exec(arrayOf("pidof", nmax.pkg))
            val reader = BufferedReader(
                InputStreamReader(process.inputStream)
            )
            val buff = reader.readLine()
            reader.close()
            process.waitFor()
            process.destroy()
            nmax.pid = if (buff != null && buff.isNotEmpty()) buff.toInt() else 0
        }
    }
}