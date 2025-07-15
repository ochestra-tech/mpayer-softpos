package ng.mpayer.softpos.app

object ResponseParser {
    @Throws(Exception::class)
    fun parseResponse(responseData: ByteArray): TransactionResponse {
        val response = TransactionResponse()

        if (responseData.size < 4) {
            throw Exception("Invalid response format")
        }

        val mti = String(responseData, 0, 4)
        response.mti = mti

        // Simplified response parsing - in production, implement full ISO8583 parsing
        response.responseCode = extractFieldFromResponse(responseData, 39)

        if (response.responseCode == "00") {
            response.authorizationCode = extractFieldFromResponse(responseData, 38)
            response.approved = true
        } else {
            response.approved = false
        }

        return response
    }

    private fun extractFieldFromResponse(data: ByteArray, fieldNumber: Int): String {
        // Mock implementation - replace with actual field extraction logic
        return when (fieldNumber) {
            38 -> "123456" // Authorization code
            39 -> "00" // Response code - always approve for demo
            else -> ""
        }
    }
}