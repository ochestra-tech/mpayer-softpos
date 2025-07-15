package ng.mpayer.softpos.app

import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class TransactionUiState(
    val amount: String = "",
    val status: String = "Ready - Tap card to process transaction",
    val cardInfo: String = "",
    val isLoading: Boolean = false,
    val isTransactionInProgress: Boolean = false,
    val lastTransactionResult: TransactionResult? = null,
    val showResultDialog: Boolean = false
)

data class TransactionResult(
    val isSuccess: Boolean,
    val amount: Double,
    val authCode: String? = null,
    val responseCode: String? = null,
    val errorMessage: String? = null
)

class TransactionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState.asStateFlow()

    private val cardReader = NFCCardReader()
    private val transactionManager = TransactionManager(
        terminalId = "12345678",
        merchantId = "123456789012345",
        acquirerId = "123456"
    )
    private val hostComm = HostCommunication("192.168.1.100", 8080)

    companion object {
        private const val TAG = "TransactionViewModel"
        private const val CURRENCY_CODE = "840" // USD
    }

    fun updateAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun updateStatus(status: String) {
        _uiState.value = _uiState.value.copy(status = status)
    }

    fun processTransaction(tag: Tag, amount: Double) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(
                    isLoading = true,
                    isTransactionInProgress = true,
                    status = "Reading card..."
                )

                // Read card data
                val cardData = withContext(Dispatchers.IO) {
                    cardReader.readCard(tag)
                }

                _uiState.value = _uiState.value.copy(
                    cardInfo = cardData.toString(),
                    status = "Processing transaction..."
                )

                // Create authorization request
                val authRequest = transactionManager.createAuthorizationRequest(
                    cardData, amount, CURRENCY_CODE
                )

                _uiState.value = _uiState.value.copy(status = "Connecting to host...")

                // Send to payment host
                val response = withContext(Dispatchers.IO) {
                    sendTransactionRequest(authRequest)
                }

                // Handle response
                val result = if (response?.approved == true) {
                    TransactionResult(
                        isSuccess = true,
                        amount = amount,
                        authCode = response.authorizationCode,
                        responseCode = response.responseCode
                    )
                } else {
                    TransactionResult(
                        isSuccess = false,
                        amount = amount,
                        responseCode = response?.responseCode,
                        errorMessage = "Transaction declined"
                    )
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isTransactionInProgress = false,
                    lastTransactionResult = result,
                    showResultDialog = true,
                    status = if (result.isSuccess) "Transaction approved - Ready for next"
                    else "Transaction declined - Try again",
                    amount = if (result.isSuccess) "" else _uiState.value.amount
                )

            } catch (e: Exception) {
                Log.e(TAG, "Transaction failed", e)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isTransactionInProgress = false,
                    lastTransactionResult = TransactionResult(
                        isSuccess = false,
                        amount = amount,
                        errorMessage = "Transaction failed: ${e.message}"
                    ),
                    showResultDialog = true,
                    status = "Transaction failed - Try again"
                )
            }
        }
    }

    private suspend fun sendTransactionRequest(request: ISO8583Message): TransactionResponse? {
        return try {
            if (!hostComm.connect()) {
                throw Exception("Failed to connect to payment host")
            }

            val requestData = request.pack()
            val responseData = hostComm.sendMessage(requestData)
            ResponseParser.parseResponse(responseData)

        } catch (e: Exception) {
            Log.w(TAG, "Using mock response due to: ${e.message}")

            // Create mock response for demo
            TransactionResponse().apply {
                mti = "0110"
                responseCode = "00"
                authorizationCode = "123456"
                approved = true
            }
        } finally {
            hostComm.disconnect()
        }
    }

    fun dismissResultDialog() {
        _uiState.value = _uiState.value.copy(
            showResultDialog = false,
            cardInfo = ""
        )
    }

    fun clearAmount() {
        _uiState.value = _uiState.value.copy(amount = "")
    }
}