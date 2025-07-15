package ng.mpayer.softpos.app

import java.io.ByteArrayOutputStream
import java.util.*

class ISO8583Message(private val mti: String) {
    private val fields = mutableMapOf<Int, String>()
    private val bitmap = BitSet(128)

    fun setField(fieldNumber: Int, value: String) {
        fields[fieldNumber] = value
        bitmap.set(fieldNumber)
    }

    fun getField(fieldNumber: Int): String? = fields[fieldNumber]

    @Throws(Exception::class)
    fun pack(): ByteArray {
        val baos = ByteArrayOutputStream()

        // MTI (4 bytes)
        baos.write(mti.toByteArray())

        // Primary bitmap (8 bytes)
        val primaryBitmap = ByteArray(8)
        for (i in 1..64) {
            if (bitmap.get(i)) {
                val byteIndex = (i - 1) / 8
                val bitIndex = 7 - ((i - 1) % 8)
                primaryBitmap[byteIndex] = (primaryBitmap[byteIndex].toInt() or (1 shl bitIndex)).toByte()
            }
        }
        baos.write(primaryBitmap)

        // Secondary bitmap if needed
        val hasSecondaryBitmap = (65..128).any { bitmap.get(it) }

        if (hasSecondaryBitmap) {
            bitmap.set(1) // Set bit 1 to indicate secondary bitmap
            primaryBitmap[0] = (primaryBitmap[0].toInt() or 0x80).toByte()

            val secondaryBitmap = ByteArray(8)
            for (i in 65..128) {
                if (bitmap.get(i)) {
                    val byteIndex = (i - 65) / 8
                    val bitIndex = 7 - ((i - 65) % 8)
                    secondaryBitmap[byteIndex] = (secondaryBitmap[byteIndex].toInt() or (1 shl bitIndex)).toByte()
                }
            }
            baos.write(secondaryBitmap)
        }

        // Data fields
        for (i in 1..128) {
            if (bitmap.get(i) && i != 1) { // Skip bitmap field itself
                fields[i]?.let { fieldData ->
                    val fieldBytes = packField(i, fieldData)
                    baos.write(fieldBytes)
                }
            }
        }

        return baos.toByteArray()
    }

    @Throws(Exception::class)
    private fun packField(fieldNumber: Int, data: String): ByteArray {
        return when (fieldNumber) {
            2, 32, 35 -> packLLVAR(data) // PAN, Acquiring Institution ID, Track 2 - LLVAR
            55 -> packLLLVAR(data) // EMV Data - LLLVAR
            else -> data.toByteArray()
        }
    }

    private fun packLLVAR(data: String): ByteArray {
        val dataBytes = data.toByteArray()
        val result = ByteArray(dataBytes.size + 1)
        result[0] = dataBytes.size.toByte()
        System.arraycopy(dataBytes, 0, result, 1, dataBytes.size)
        return result
    }

    private fun packLLLVAR(data: String): ByteArray {
        val dataBytes = data.toByteArray()
        val result = ByteArray(dataBytes.size + 2)
        result[0] = (dataBytes.size / 256).toByte()
        result[1] = (dataBytes.size % 256).toByte()
        System.arraycopy(dataBytes, 0, result, 2, dataBytes.size)
        return result
    }
}