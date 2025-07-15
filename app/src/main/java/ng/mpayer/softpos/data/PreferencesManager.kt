package ng.mpayer.softpos.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import ng.mpayer.softpos.utils.Constants
import androidx.core.content.edit

class PreferencesManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    private val _terminalId = MutableStateFlow(getTerminalId())
    val terminalId: Flow<String> = _terminalId.asStateFlow()

    private val _merchantId = MutableStateFlow(getMerchantId())
    val merchantId: Flow<String> = _merchantId.asStateFlow()

    private val _hostIP = MutableStateFlow(getHostIP())
    val hostIP: Flow<String> = _hostIP.asStateFlow()

    private val _hostPort = MutableStateFlow(getHostPort())
    val hostPort: Flow<Int> = _hostPort.asStateFlow()

    fun getTerminalId(): String {
        return prefs.getString(Constants.PREF_TERMINAL_ID, Constants.DEFAULT_TERMINAL_ID)
            ?: Constants.DEFAULT_TERMINAL_ID
    }

    fun setTerminalId(terminalId: String) {
        prefs.edit { putString(Constants.PREF_TERMINAL_ID, terminalId) }
        _terminalId.value = terminalId
    }

    fun getMerchantId(): String {
        return prefs.getString(Constants.PREF_MERCHANT_ID, Constants.DEFAULT_MERCHANT_ID)
            ?: Constants.DEFAULT_MERCHANT_ID
    }

    fun setMerchantId(merchantId: String) {
        prefs.edit { putString(Constants.PREF_MERCHANT_ID, merchantId) }
        _merchantId.value = merchantId
    }

    fun getHostIP(): String {
        return prefs.getString(Constants.PREF_HOST_IP, Constants.DEFAULT_HOST_IP)
            ?: Constants.DEFAULT_HOST_IP
    }

    fun setHostIP(hostIP: String) {
        prefs.edit { putString(Constants.PREF_HOST_IP, hostIP) }
        _hostIP.value = hostIP
    }

    fun getHostPort(): Int {
        return prefs.getInt(Constants.PREF_HOST_PORT, Constants.DEFAULT_HOST_PORT)
    }

    fun setHostPort(hostPort: Int) {
        prefs.edit { putInt(Constants.PREF_HOST_PORT, hostPort) }
        _hostPort.value = hostPort
    }

    fun getLastSTAN(): Long {
        return prefs.getLong(Constants.PREF_LAST_STAN, 1L)
    }

    fun setLastSTAN(stan: Long) {
        prefs.edit { putLong(Constants.PREF_LAST_STAN, stan) }
    }

    fun clearAll() {
        prefs.edit { clear() }
        _terminalId.value = Constants.DEFAULT_TERMINAL_ID
        _merchantId.value = Constants.DEFAULT_MERCHANT_ID
        _hostIP.value = Constants.DEFAULT_HOST_IP
        _hostPort.value = Constants.DEFAULT_HOST_PORT
    }
}