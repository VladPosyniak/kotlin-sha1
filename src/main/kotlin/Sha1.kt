package main

import java.util.*

class Sha1 {
    private var h1 = 0x67452301
    private var h2 = -0x10325477
    private var h3 = -0x67452302
    private var h4 = 0x10325476
    private var h5 = -0x3c2d1e10

    private val k1 = 0x5A827999
    private val k2 = 0x6ED9EBA1
    private val k3 = -0x70e44324
    private val k4 = -0x359d3e2a

    private var messLength = 0

    public fun getHash(phrase: String): String
    {
        val binary = convertToBinary(phrase)
        messLength = binary.length
        return calculateMod(phrase, binary)
    }

    private fun convertToBinary(word: String): String {
        val bytes = word.toByteArray()
        val binary = StringBuilder()
        for (b in bytes) {
            var `val` = b.toInt()
            for (i in 0..7) {
                binary.append(if (`val` and 128 == 0) 0 else 1)
                `val` = `val` shl 1
            }
            binary.append(' ')
        }
        return binary.toString()
    }

    private fun calculateMod(word: String, binary: String): String
    {
        var binary = binary
        val binaryMessageLength = word.length * 8 - 8
        val endBitLength = calculateMessageLength(binaryMessageLength + 8)
        val subMod = endBitLength.length
        var temp = binaryMessageLength % 512
        temp = if (432 - temp < 0) {
            val x = 512 - temp
            x + 440 + temp + 64
        } else {
            432 - temp
        }
        val binaryZeros = temp
        val onePadded = "10000000" //add back the removed 8
        binary = binary.replace("\\s+".toRegex(), "") //видаляємо пробіли
        return createMessageLength(binary, onePadded, binaryZeros, endBitLength) //створюємо 512-бітове повідомлення
    }

    private fun calculateMessageLength(bitLength: Int): String {
        val tempBitsLength = Integer.toBinaryString(bitLength)
        val sb = StringBuilder(tempBitsLength)
        var temp = 64 - tempBitsLength.length
        while (temp > 0) {
            sb.insert(0, 0)
            temp--
        }
        return sb.toString()
    }

    private fun createMessageLength(message: String?, paddedOne: String?, zeros: Int, endLength: String?): String {
        var zeros = zeros
        val messageBinary = StringBuilder(message)
        messageBinary.insert(messageBinary.toString().length, paddedOne)
        while (zeros > 0) {
            messageBinary.insert(messageBinary.toString().length, 0)
            zeros--
        }
        messageBinary.insert(messageBinary.toString().length, endLength)
        var m = printMessage(messageBinary.toString())
        m = m.replace("\\s+".toRegex(), "")
        val mArray = IntArray(m.length / 32)
        var i = 0
        while (i < m.length) {
            mArray[i / 32] = Integer.valueOf(m.substring(i + 1, i + 32), 2)
            if (m[i] == '1') {
                mArray[i / 32] = mArray[i / 32] or -0x80000000
            }
            i += 32
        }
        return hash(mArray)
    }

    private fun printMessage(message: String): String {
        val sb = StringBuilder(message)
        var num = message.length
        while (num > 0) {
            if (num % 32 == 0) {
                sb.insert(num, " ")
            }
            num--
        }
        return sb.toString()
    }

    private fun leftrotate(x: Int, shift: Int): Int {
        return x shl shift or (x ushr 32 - shift)
    }

    private fun hash(z: IntArray): String {
        val integer_count = z.size
        val intArray = IntArray(80)
        var j = 0
        var i = 0
        while (i < integer_count) {
            j = 0
            while (j <= 15) {
                intArray[j] = z[j + i]
                j++
            }
            j = 16
            while (j <= 79) {
                intArray[j] =
                    leftrotate(intArray[j - 3] xor intArray[j - 8] xor intArray[j - 14] xor intArray[j - 16], 1)
                j++
            }

            //  розраховуємо A,B,C,D,E:
            var A = h1
            var B = h2
            var C = h3
            var D = h4
            var E = h5
            var t = 0
            for (x in 0..19) {
                t = leftrotate(A, 5) + (B and C or (B.inv() and D)) + E + intArray[x] + k1
                E = D
                D = C
                C = leftrotate(B, 30)
                B = A
                A = t
            }
            for (b in 20..39) {
                t = leftrotate(A, 5) + (B xor C xor D) + E + intArray[b] + k2
                E = D
                D = C
                C = leftrotate(B, 30)
                B = A
                A = t
            }
            for (c in 40..59) {
                t = leftrotate(A, 5) + (B and C or (B and D) or (C and D)) + E + intArray[c] + k3
                E = D
                D = C
                C = leftrotate(B, 30)
                B = A
                A = t
            }
            for (d in 60..79) {
                t = leftrotate(A, 5) + (B xor C xor D) + E + intArray[d] + k4
                E = D
                D = C
                C = leftrotate(B, 30)
                B = A
                A = t
            }
            h1 += A
            h2 += B
            h3 += C
            h4 += D
            h5 += E
            i += 16
        }
        var h1Length = Integer.toHexString(h1)
        var h2Length = Integer.toHexString(h2)
        var h3Length = Integer.toHexString(h3)
        var h4Length = Integer.toHexString(h4)
        var h5Length = Integer.toHexString(h5)

        if (h1Length.length < 8) {
            val h1L = StringBuilder(h1Length)
            h1L.insert(0, 0)
            h1Length = h1L.toString()
        } else if (h2Length.length < 8) {
            val h2L = StringBuilder(h2Length)
            h2L.insert(0, 0)
            h2Length = h2L.toString()
        } else if (h3Length.length < 8) {
            val h3L = StringBuilder(h3Length)
            h3L.insert(0, 0)
            h3Length = h3L.toString()
        } else if (h4Length.length < 8) {
            val h4L = StringBuilder(h4Length)
            h4L.insert(0, 0)
            h4Length = h4L.toString()
        } else if (h5Length.length < 8) {
            val h5L = StringBuilder(h5Length)
            h5L.insert(0, 0)
            h5Length = h5L.toString()
        }

        return h1Length + h2Length + h3Length + h4Length + h5Length
    }
}