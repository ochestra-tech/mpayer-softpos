package ng.mpayer.softpos.data

import ng.mpayer.softpos.app.TransactionResponse
import java.util.*

object MockDataProvider {

    fun createTestVisaCard(): EMVCardData {
        return EMVCardData(
            pan = "4111111111111111",
            expiryDate = "2512",
            serviceCode = "201",
            track2Data = "4111111111111111=25122010000000000000",
            emvData = createMockEMVData("VISA")
        )
    }

    fun createTestMastercardCard(): EMVCardData {
        return EMVCardData(
            pan = "5555555555554444",
            expiryDate = "2512",
            serviceCode = "201",
            track2Data = "5555555555554444=25122010000000000000",
            emvData = createMockEMVData("MASTERCARD")
        )
    }

    fun createTestAmexCard(): EMVCardData {
        return EMVCardData(
            pan = "378282246310005",
            expiryDate = "2512",
            serviceCode = "201",
            track2Data = "378282246310005=25122010000000000000",
            emvData = createMockEMVData("AMEX")
        )
    }

    private fun createMockEMVData(cardBrand: String): String {
        val builder = StringBuilder()

        // Application Identifier (AID)
        when (cardBrand) {
            "VISA" -> {
                builder.append("84") // Tag
                builder.append("07") // Length
                builder.append("A0000000031010") // Visa AID
            }
            "MASTERCARD" -> {
                builder.append("84") // Tag
                builder.append("07") // Length
                builder.append("A0000000041010") // Mastercard AID
            }
            "AMEX" -> {
                builder.append("84") // Tag
                builder.append("07") // Length
                builder.append("A000000025010801") // Amex AID
            }
        }

        // Application Interchange Profile (AIP)
        builder.append("82") // Tag
        builder.append("02") // Length
        builder.append("0000") // Value

        // Application Usage Control (AUC)
        builder.append("9F07") // Tag
        builder.append("02") // Length
        builder.append("FF00") // Value

        // Transaction Amount
        builder.append("9F02") // Tag
        builder.append("06") // Length
        builder.append("000000001000") // $10.00

        // Terminal Country Code
        builder.append("9F1A") // Tag
        builder.append("02") // Length
        builder.append("0840") // USA

        // Transaction Date
        builder.append("9A") // Tag
        builder.append("03") // Length
        builder.append("241201") // December 1, 2024

        // Transaction Time
        builder.append("9F21") // Tag
        builder.append("03") // Length
        builder.append("120000") // 12:00:00

        // Random Number
        builder.append("9F37") // Tag
        builder.append("04") // Length
        builder.append("12345678") // Mock value

        return builder.toString()
    }

    fun createMockApprovedResponse(amount: Double): TransactionResponse {
        return TransactionResponse(

        )
    }

    fun createMockDeclinedResponse(amount: Double, reason: String = "51"): TransactionResponse {
        return TransactionResponse(

        )
    }

    private fun generateRandomAuthCode(): String {
        return String.format("%06d", Random().nextInt(999999))
    }

    private fun generateRandomRRN(): String {
        return String.format("%012d", kotlin.math.abs(Random().nextLong()) % 1000000000000L)
    }

    private fun generateRandomTransactionId(): String {
        return "TXN${System.currentTimeMillis()}"
    }

    private fun getDeclineReason(code: String): String {
        return when (code) {
            "51" -> "Insufficient funds"
            "54" -> "Expired card"
            "55" -> "Incorrect PIN"
            "57" -> "Transaction not permitted"
            "61" -> "Exceeds withdrawal limit"
            "62" -> "Restricted card"
            "96" -> "System error"
            else -> "Transaction declined"
        }
    }

    // Sample transaction data for testing
    fun getSampleTransactions(): List<SampleTransaction> {
        return listOf(
            SampleTransaction("Sale", 25.00, "Approved", "123456"),
            SampleTransaction("Sale", 50.75, "Declined", ""),
            SampleTransaction("Refund", 15.25, "Approved", "789012"),
            SampleTransaction("Sale", 100.00, "Approved", "345678"),
            SampleTransaction("Sale", 5.99, "Approved", "901234")
        )
    }

    data class SampleTransaction(
        val type: String,
        val amount: Double,
        val status: String,
        val authCode: String
    )
}