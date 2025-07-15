package ng.mpayer.softpos.app

class TransactionResponse {
    var mti: String = ""
    var responseCode: String = ""
    var authorizationCode: String = ""
    var approved: Boolean = false
    var errorMessage: String = ""

    override fun toString(): String {
        return "TransactionResponse(mti='$mti', responseCode='$responseCode', " +
                "authorizationCode='$authorizationCode', approved=$approved, errorMessage='$errorMessage')"
    }
}