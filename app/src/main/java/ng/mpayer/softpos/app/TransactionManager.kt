package ng.mpayer.softpos.app

import ng.mpayer.softpos.data.EMVCardData
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicLong

class TransactionManager(
    private val terminalId: String,
    private val merchantId: String,
    private val acquirerId: String
) {
    private val stanSequence = AtomicLong(1)

    fun createAuthorizationRequest(
        cardData: EMVCardData,
        amount: Double,
        currencyCode: String
    ): ISO8583Message {
        val message = ISO8583Message("0100") // Authorization Request

        // Mandatory fields
        message.setField(2, extractPAN(cardData.pan))
        message.setField(3, "000000") // Processing code (purchase)
        message.setField(4, formatAmount(amount))
        message.setField(11, String.format(Locale.US, "%06d", stanSequence.getAndIncrement()))
        message.setField(12, getCurrentTime())
        message.setField(13, getCurrentDate())
        message.setField(22, "051") // POS Entry Mode (chip card)
        message.setField(25, "00") // POS Condition Code (normal presentment)
        message.setField(32, acquirerId)
        message.setField(35, cardData.track2Data)
        message.setField(37, generateRRN())
        message.setField(41, terminalId)
        message.setField(42, merchantId)
        message.setField(49, currencyCode)

        // EMV specific fields
        if (cardData.emvData.isNotEmpty()) {
            message.setField(55, cardData.emvData)
        }

        return message
    }

    private fun extractPAN(panData: String): String {
        return panData.replace(Regex("[^0-9]"), "")
    }

    private fun formatAmount(amount: Double): String {
        val amountCents = (amount * 100).toLong()
        return String.format(Locale.US, "%012d", amountCents)
    }

    private fun getCurrentTime(): String {
        return SimpleDateFormat("HHmmss", Locale.US).format(Date())
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("MMdd", Locale.US).format(Date())
    }

    private fun generateRRN(): String {
        return String.format(Locale.US, "%012d", kotlin.math.abs(SecureRandom().nextLong()) % 1000000000000L)
    }
}