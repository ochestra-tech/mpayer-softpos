package ng.mpayer.softpos.utils

import java.util.Calendar
import java.util.regex.Pattern

object ValidationUtils {

    // Luhn algorithm for PAN validation
    fun validatePAN(pan: String): Boolean {
        if (pan.length < 13 || pan.length > 19) return false
        if (!pan.matches(Regex("^[0-9]+$"))) return false

        var sum = 0
        var alternate = false

        for (i in pan.length - 1 downTo 0) {
            var n = pan[i].toString().toInt()

            if (alternate) {
                n *= 2
                if (n > 9) {
                    n = (n % 10) + 1
                }
            }

            sum += n
            alternate = !alternate
        }

        return sum % 10 == 0
    }

    fun validateExpiryDate(expiry: String): Boolean {
        if (expiry.length != 4) return false
        if (!expiry.matches(Regex("^[0-9]{4}$"))) return false

        val month = expiry.substring(0, 2).toIntOrNull() ?: return false
        val year = expiry.substring(2, 4).toIntOrNull() ?: return false

        if (month < 1 || month > 12) return false

        // Check if card is not expired (basic check)
        val currentYear = Calendar.getInstance().get(Calendar.YEAR) % 100
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1

        return when {
            year > currentYear -> true
            year == currentYear -> month >= currentMonth
            else -> false
        }
    }

    fun validateCVV(cvv: String, cardType: CardType = CardType.UNKNOWN): Boolean {
        return when (cardType) {
            CardType.AMEX -> cvv.matches(Regex("^[0-9]{4}$"))
            else -> cvv.matches(Regex("^[0-9]{3}$"))
        }
    }

    fun validateAmount(amount: String): Boolean {
        return try {
            val value = amount.toDoubleOrNull()
            value != null && value > 0 && value <= 999999.99
        } catch (e: Exception) {
            false
        }
    }

    fun validateTerminalId(terminalId: String): Boolean {
        return terminalId.matches(Regex("^[A-Za-z0-9]{8}$"))
    }

    fun validateMerchantId(merchantId: String): Boolean {
        return merchantId.matches(Regex("^[A-Za-z0-9]{1,15}$"))
    }

    fun getCardType(pan: String): CardType {
        return when {
            pan.matches(Regex("^4[0-9]{12}(?:[0-9]{3})?$")) -> CardType.VISA
            pan.matches(Regex("^5[1-5][0-9]{14}$")) -> CardType.MASTERCARD
            pan.matches(Regex("^3[47][0-9]{13}$")) -> CardType.AMEX
            pan.matches(Regex("^6(?:011|5[0-9]{2})[0-9]{12}$")) -> CardType.DISCOVER
            else -> CardType.UNKNOWN
        }
    }

    enum class CardType {
        VISA, MASTERCARD, AMEX, DISCOVER, UNKNOWN
    }
}