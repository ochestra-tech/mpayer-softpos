package ng.mpayer.softpos.app

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket

class HostCommunication(
    private val hostIP: String,
    private val hostPort: Int
) {
    private var socket: Socket? = null
    private var outputStream: DataOutputStream? = null
    private var inputStream: DataInputStream? = null

    companion object {
        private const val TAG = "HostComm"
    }

    fun connect(): Boolean {
        return try {
            socket = Socket(hostIP, hostPort).apply {
                soTimeout = 30000 // 30 second timeout
            }
            outputStream = DataOutputStream(socket?.getOutputStream())
            inputStream = DataInputStream(socket?.getInputStream())
            Log.d(TAG, "Connected to host: $hostIP:$hostPort")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to host: ${e.message}")
            false
        }
    }

    @Throws(Exception::class)
    fun sendMessage(message: ByteArray): ByteArray {
        val currentSocket = socket
        val currentOutputStream = outputStream
        val currentInputStream = inputStream

        if (currentSocket?.isConnected != true) {
            throw Exception("Not connected to host")
        }

        // Send message length first (2 bytes)
        currentOutputStream?.writeShort(message.size)
        currentOutputStream?.write(message)
        currentOutputStream?.flush()

        Log.d(TAG, "Sent message to host (${message.size} bytes)")

        // Read response
        val responseLength = currentInputStream?.readShort()?.toInt() ?: 0
        val response = ByteArray(responseLength)
        currentInputStream?.readFully(response)

        Log.d(TAG, "Received response from host ($responseLength bytes)")
        return response
    }

    fun disconnect() {
        try {
            socket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error disconnecting: ${e.message}")
        }
    }
}