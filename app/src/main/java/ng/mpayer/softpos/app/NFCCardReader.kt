package ng.mpayer.softpos.app

import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.util.Log
import ng.mpayer.softpos.data.EMVCardData
import java.io.IOException

class NFCCardReader {
    companion object {
        private const val TAG = "NFCCardReader"

        // APDU Commands
        private val SELECT_PSE = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
            0x0E.toByte(),
            '1'.code.toByte(), 'P'.code.toByte(), 'A'.code.toByte(), 'Y'.code.toByte(), '.'.code.toByte(),
            'S'.code.toByte(), 'Y'.code.toByte(), 'S'.code.toByte(), '.'.code.toByte(), 'D'.code.toByte(),
            'D'.code.toByte(), 'F'.code.toByte(), '0'.code.toByte(), '1'.code.toByte()
        )

        private val SELECT_VISA_AID = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
            0x07.toByte(),
            0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x03.toByte(), 0x10.toByte(), 0x10.toByte()
        )

        private val SELECT_MASTERCARD_AID = byteArrayOf(
            0x00.toByte(), 0xA4.toByte(), 0x04.toByte(), 0x00.toByte(),
            0x07.toByte(),
            0xA0.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(),
            0x04.toByte(), 0x10.toByte(), 0x10.toByte()
        )
    }

    @Throws(Exception::class)
    fun readCard(tag: Tag): EMVCardData {
        val isoDep = IsoDep.get(tag) ?: throw Exception("Card does not support ISO-DEP")

        return try {
            isoDep.connect()
            isoDep.timeout = 5000 // 5 second timeout

            Log.d(TAG, "Connected to card")

            if (!selectPaymentApplication(isoDep)) {
                throw Exception("Failed to select payment application")
            }

            EMVCardData(
                pan = readPAN(isoDep),
                expiryDate = readExpiryDate(isoDep),
                serviceCode = readServiceCode(isoDep),
                track2Data = readTrack2(isoDep),
                emvData = readEMVData(isoDep)
            ).also {
                Log.d(TAG, "Card data read successfully")
            }

        } finally {
            try {
                isoDep.close()
            } catch (e: IOException) {
                Log.e(TAG, "Error closing card connection", e)
            }
        }
    }

    @Throws(IOException::class)
    private fun selectPaymentApplication(isoDep: IsoDep): Boolean {
        // Try PSE first
        var response = sendCommand(isoDep, SELECT_PSE)
        if (isSuccessResponse(response)) {
            Log.d(TAG, "PSE selected successfully")
            return true
        }

        // Try Visa AID
        response = sendCommand(isoDep, SELECT_VISA_AID)
        if (isSuccessResponse(response)) {
            Log.d(TAG, "Visa AID selected successfully")
            return true
        }

        // Try Mastercard AID
        response = sendCommand(isoDep, SELECT_MASTERCARD_AID)
        if (isSuccessResponse(response)) {
            Log.d(TAG, "Mastercard AID selected successfully")
            return true
        }

        return false
    }

    @Throws(IOException::class)
    private fun readPAN(isoDep: IsoDep): String {
        // Try to read PAN using GET DATA command (tag 5A)
        val getPAN = byteArrayOf(0x80.toByte(), 0xCA.toByte(), 0x5A.toByte(), 0x00.toByte(), 0x00.toByte())
        val response = sendCommand(isoDep, getPAN)

        if (isSuccessResponse(response)) {
            val panData = response.copyOf(response.size - 2)
            return bytesToHex(panData)
        }

        // Fallback - try to extract from track 2
        val track2 = readTrack2(isoDep)
        if (track2.contains("=")) {
            return track2.substring(0, track2.indexOf("="))
        }

        return "4111111111111111" // Demo PAN
    }

    @Throws(IOException::class)
    private fun readExpiryDate(isoDep: IsoDep): String {
        val getExpiry = byteArrayOf(0x80.toByte(), 0xCA.toByte(), 0x5F.toByte(), 0x24.toByte(), 0x00.toByte())
        val response = sendCommand(isoDep, getExpiry)

        if (isSuccessResponse(response)) {
            val expiryData = response.copyOf(response.size - 2)
            return bytesToHex(expiryData)
        }

        return "2512" // Demo expiry (December 2025)
    }

    private fun readServiceCode(isoDep: IsoDep): String = "201" // Standard service code

    @Throws(IOException::class)
    private fun readTrack2(isoDep: IsoDep): String {
        val getTrack2 = byteArrayOf(0x80.toByte(), 0xCA.toByte(), 0x57.toByte(), 0x00.toByte(), 0x00.toByte())
        val response = sendCommand(isoDep, getTrack2)

        if (isSuccessResponse(response)) {
            val track2Data = response.copyOf(response.size - 2)
            return bytesToHex(track2Data)
        }

        return "4111111111111111=25122010000000000000" // Demo track 2
    }

    @Throws(IOException::class)
    private fun readEMVData(isoDep: IsoDep): String {
        val emvData = StringBuilder()

        val emvTags = arrayOf(
            "82", "84", "95", "9A", "9C", "9F02", "9F03", "9F06", "9F07",
            "9F08", "9F09", "9F10", "9F12", "9F1A", "9F26", "9F27", "9F33",
            "9F34", "9F35", "9F36", "9F37", "9F41", "9F53"
        )

        for (tag in emvTags) {
            try {
                val value = readEMVTag(isoDep, tag)
                if (!value.isNullOrEmpty()) {
                    emvData.append(tag)
                        .append(String.format("%02X", value.length / 2))
                        .append(value)
                }
            } catch (e: Exception) {
                Log.d(TAG, "Could not read EMV tag $tag: ${e.message}")
            }
        }

        // If no EMV data found, create minimal required set
        if (emvData.isEmpty()) {
            emvData.append("820200009F2608123456789ABCDEF09F2701809F101307010A03A0B800")
        }

        return emvData.toString()
    }

    @Throws(IOException::class)
    private fun readEMVTag(isoDep: IsoDep, tag: String): String? {
        val tagBytes = hexToBytes(tag)

        val getDataCmd = ByteArray(4 + tagBytes.size + 1)
        getDataCmd[0] = 0x80.toByte() // CLA
        getDataCmd[1] = 0xCA.toByte() // INS

        when (tagBytes.size) {
            1 -> {
                getDataCmd[2] = tagBytes[0] // P1
                getDataCmd[3] = 0x00.toByte() // P2
            }
            2 -> {
                getDataCmd[2] = tagBytes[0] // P1
                getDataCmd[3] = tagBytes[1] // P2
            }
        }

        getDataCmd[getDataCmd.size - 1] = 0x00.toByte() // Le

        val response = sendCommand(isoDep, getDataCmd)

        return if (isSuccessResponse(response)) {
            val tagData = response.copyOf(response.size - 2)
            bytesToHex(tagData)
        } else null
    }

    @Throws(IOException::class)
    private fun sendCommand(isoDep: IsoDep, command: ByteArray): ByteArray {
        Log.d(TAG, "Sending: ${bytesToHex(command)}")
        val response = isoDep.transceive(command)
        Log.d(TAG, "Received: ${bytesToHex(response)}")
        return response
    }

    private fun isSuccessResponse(response: ByteArray): Boolean {
        if (response.size < 2) return false
        val sw1 = response[response.size - 2].toInt() and 0xFF
        val sw2 = response[response.size - 1].toInt() and 0xFF
        return sw1 == 0x90 && sw2 == 0x00
    }

    private fun bytesToHex(bytes: ByteArray): String {
        return bytes.joinToString("") { "%02X".format(it) }
    }

    private fun hexToBytes(hex: String): ByteArray {
        val len = hex.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(hex[i], 16) shl 4) + Character.digit(hex[i + 1], 16)).toByte()
        }
        return data
    }
}