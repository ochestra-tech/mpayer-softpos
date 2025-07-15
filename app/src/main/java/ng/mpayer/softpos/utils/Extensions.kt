package ng.mpayer.softpos.utils

import android.content.Context
import android.nfc.NfcManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

// Context Extensions
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Context.isNFCAvailable(): Boolean {
    val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
    val nfcAdapter = nfcManager.defaultAdapter
    return nfcAdapter != null
}

fun Context.isNFCEnabled(): Boolean {
    val nfcManager = getSystemService(Context.NFC_SERVICE) as NfcManager
    val nfcAdapter = nfcManager.defaultAdapter
    return nfcAdapter?.isEnabled ?: false
}

// String Extensions
fun String.maskCardNumber(): String {
    return if (length >= 8) {
        "${take(4)}${"*".repeat(length - 8)}${takeLast(4)}"
    } else this
}

fun String.isValidPAN(): Boolean {
    return matches(Regex("^[0-9]{13,19}$"))
}

fun String.isValidAmount(): Boolean {
    return try {
        val amount = toDoubleOrNull()
        amount != null && amount > 0 && amount <= 999999.99
    } catch (e: Exception) {
        false
    }
}

fun String.formatCurrency(currencyCode: String = "USD"): String {
    return try {
        val amount = toDoubleOrNull() ?: return this
        val format = NumberFormat.getCurrencyInstance(Locale.US)
        format.currency = Currency.getInstance(currencyCode)
        format.format(amount)
    } catch (e: Exception) {
        this
    }
}

// ByteArray Extensions
fun ByteArray.toHexString(): String {
    return joinToString("") { "%02X".format(it) }
}

fun String.fromHexString(): ByteArray {
    require(length % 2 == 0) { "Hex string must have even length" }
    return chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray()
}

// Date Extensions
fun Date.formatForTransaction(): String {
    val format = SimpleDateFormat("MMddHHmmss", Locale.US)
    return format.format(this)
}

fun Date.formatReadable(): String {
    val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    return format.format(this)
}

// Double Extensions
fun Double.toCents(): Long {
    return (this * 100).toLong()
}

fun Long.fromCents(): Double {
    return this / 100.0
}

fun Double.formatAmount(): String {
    return String.format(Locale.US, "%.2f", this)
}

// Compose Extensions
@Composable
fun rememberNFCStatus(): NFCStatus {
    val context = LocalContext.current
    return when {
        !context.isNFCAvailable() -> NFCStatus.NOT_SUPPORTED
        !context.isNFCEnabled() -> NFCStatus.DISABLED
        else -> NFCStatus.ENABLED
    }
}

enum class NFCStatus {
    NOT_SUPPORTED,
    DISABLED,
    ENABLED
}
