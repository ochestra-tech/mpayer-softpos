package ng.mpayer.softpos.utils

object Constants {
    // Application Constants
    const val APP_VERSION = "1.0.0"
    const val APP_NAME = "Soft POS"

    // Network Configuration
    const val DEFAULT_HOST_IP = "192.168.1.100"
    const val DEFAULT_HOST_PORT = 8080
    const val CONNECTION_TIMEOUT = 10000
    const val READ_TIMEOUT = 30000
    const val MAX_RETRY_ATTEMPTS = 3

    // Transaction Constants
    const val DEFAULT_CURRENCY_CODE = "840" // USD
    const val MAX_TRANSACTION_AMOUNT = 999999.99
    const val MIN_TRANSACTION_AMOUNT = 0.01

    // Terminal Configuration
    const val DEFAULT_TERMINAL_ID = "12345678"
    const val DEFAULT_MERCHANT_ID = "123456789012345"
    const val DEFAULT_ACQUIRER_ID = "123456"
    const val MERCHANT_CATEGORY_CODE = "6011"

    // ISO8583 Message Types
    const val MTI_AUTH_REQUEST = "0100"
    const val MTI_AUTH_RESPONSE = "0110"
    const val MTI_REVERSAL_REQUEST = "0400"
    const val MTI_REVERSAL_RESPONSE = "0410"
    const val MTI_SETTLEMENT_REQUEST = "0500"
    const val MTI_SETTLEMENT_RESPONSE = "0510"
    const val MTI_NETWORK_MGMT_REQUEST = "0800"
    const val MTI_NETWORK_MGMT_RESPONSE = "0810"

    // Processing Codes
    const val PROC_CODE_SALE = "000000"
    const val PROC_CODE_REFUND = "200000"
    const val PROC_CODE_VOID = "020000"
    const val PROC_CODE_INQUIRY = "300000"
    const val PROC_CODE_SETTLEMENT = "920000"
    const val PROC_CODE_NETWORK_MGMT = "990000"

    // POS Entry Modes
    const val POS_ENTRY_CHIP = "051"
    const val POS_ENTRY_SWIPE = "021"
    const val POS_ENTRY_MANUAL = "011"
    const val POS_ENTRY_CONTACTLESS = "071"
    const val POS_ENTRY_FALLBACK = "801"

    // Response Codes
    const val RESPONSE_APPROVED = "00"
    const val RESPONSE_REFER_TO_ISSUER = "01"
    const val RESPONSE_INVALID_MERCHANT = "03"
    const val RESPONSE_DO_NOT_HONOR = "05"
    const val RESPONSE_INVALID_TRANSACTION = "12"
    const val RESPONSE_INVALID_AMOUNT = "13"
    const val RESPONSE_INVALID_CARD = "14"
    const val RESPONSE_INSUFFICIENT_FUNDS = "51"
    const val RESPONSE_EXPIRED_CARD = "54"
    const val RESPONSE_INCORRECT_PIN = "55"
    const val RESPONSE_SYSTEM_ERROR = "96"

    // EMV Tags
    const val EMV_TAG_AID = "84"
    const val EMV_TAG_APP_LABEL = "50"
    const val EMV_TAG_PAN = "5A"
    const val EMV_TAG_EXPIRY_DATE = "5F24"
    const val EMV_TAG_CARDHOLDER_NAME = "5F20"
    const val EMV_TAG_TRACK2_DATA = "57"
    const val EMV_TAG_SERVICE_CODE = "5F30"
    const val EMV_TAG_AMOUNT = "9F02"
    const val EMV_TAG_COUNTRY_CODE = "9F1A"
    const val EMV_TAG_TERMINAL_ID = "9F1C"
    const val EMV_TAG_TRANSACTION_DATE = "9A"
    const val EMV_TAG_TRANSACTION_TIME = "9F21"
    const val EMV_TAG_CRYPTOGRAM = "9F26"
    const val EMV_TAG_CRYPTOGRAM_INFO = "9F27"

    // NFC Constants
    const val NFC_TIMEOUT = 5000
    const val ISO_DEP_TIMEOUT = 5000

    // Shared Preferences Keys
    const val PREFS_NAME = "soft_pos_prefs"
    const val PREF_TERMINAL_ID = "terminal_id"
    const val PREF_MERCHANT_ID = "merchant_id"
    const val PREF_HOST_IP = "host_ip"
    const val PREF_HOST_PORT = "host_port"
    const val PREF_LAST_STAN = "last_stan"

    // UI Constants
    const val ANIMATION_DURATION = 300
    const val SPLASH_DELAY = 2000
    const val TOAST_DURATION_SHORT = 2000
    const val TOAST_DURATION_LONG = 3500
}