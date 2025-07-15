package ng.mpayer.softpos.data

data class EMVCardData(
    val pan: String,
    val expiryDate: String,
    val serviceCode: String,
    val track2Data: String,
    val emvData: String
) {
    override fun toString(): String {
        return "Card ****${if (pan.length >= 4) pan.takeLast(4) else "****"} " +
                "Exp: ${if (expiryDate.length >= 4) "${expiryDate.take(2)}/${expiryDate.drop(2)}" else "**/**"}"
    }

    fun getMaskedPAN(): String {
        return if (pan.length >= 8) {
            "${pan.take(4)}****${pan.takeLast(4)}"
        } else pan
    }
}