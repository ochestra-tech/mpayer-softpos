package ng.mpayer.softpos.app

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier

import ng.mpayer.softpos.ui.theme.SoftPOSTheme

class MainActivity : ComponentActivity() {
    private val viewModel: TransactionViewModel by viewModels()

    private var nfcAdapter: NfcAdapter? = null
    private var pendingIntent: PendingIntent? = null
    private var intentFilters: Array<IntentFilter>? = null
    private var techLists: Array<Array<String>>? = null

    companion object {
        private const val TAG = "SoftPOS"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeNFC()

        setContent {
            SoftPOSTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TransactionScreen(
                        viewModel = viewModel,
                        onNFCStatusUpdate = { message ->
                            viewModel.updateStatus(message)
                        }
                    )
                }
            }
        }
    }

    private fun initializeNFC() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        if (nfcAdapter == null) {
            viewModel.updateStatus("NFC not supported on this device")
            return
        }

        if (nfcAdapter?.isEnabled != true) {
            viewModel.updateStatus("NFC is disabled. Please enable NFC in settings.")
            return
        }

        // Create pending intent for NFC
        val intent = Intent(this, javaClass).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_MUTABLE)

        // Setup intent filters and tech lists
        val isoDepFilter = IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED)
        intentFilters = arrayOf(isoDepFilter)
        techLists = arrayOf(arrayOf(IsoDep::class.java.name))

        Log.d(TAG, "NFC initialized successfully")
    }

    override fun onResume() {
        super.onResume()
        nfcAdapter?.enableForegroundDispatch(this, pendingIntent, intentFilters, techLists)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter?.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        if (NfcAdapter.ACTION_TECH_DISCOVERED == intent.action) {
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let { handleCardDetected(it) }
        }
    }

    private fun handleCardDetected(tag: Tag) {
        if (viewModel.uiState.value.amount.isEmpty()) {
            viewModel.updateStatus("Please enter transaction amount first")
            return
        }

        val amount = viewModel.uiState.value.amount.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            viewModel.updateStatus("Invalid amount")
            return
        }

        viewModel.processTransaction(tag, amount)
    }
}
