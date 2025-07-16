# Soft POS Application - Android Kotlin/Jetpack Compose

A complete Point of Sale (POS) application for Android devices with NFC support, built using Kotlin and Jetpack Compose. This application enables contactless EMV chip card transactions with ISO8583 message processing.

## Features

### 🚀 Core Functionality
- **NFC EMV Card Reading** - Contactless chip card processing using Android NFC APIs
- **ISO8583 Message Processing** - Complete message formatting and parsing
- **Transaction Management** - Authorization, reversal, and settlement requests
- **Host Communication** - TCP/IP and SSL/TLS support for payment processors
- **Real-time UI Updates** - Reactive interface with Jetpack Compose

### 🛡️ Security Features
- **Android Keystore Integration** - Secure key management
- **Data Encryption** - Sensitive data protection
- **Network Security** - SSL/TLS communication with certificate pinning
- **Card Data Masking** - PCI DSS compliance considerations

### 📱 Modern Android Architecture
- **MVVM Pattern** - Clean architecture with ViewModel and StateFlow
- **Jetpack Compose** - Modern declarative UI
- **Kotlin Coroutines** - Async operations and structured concurrency
- **Material Design 3** - Contemporary UI components

## Prerequisites

### Hardware Requirements
- **Android device with NFC** (API level 23+)
- **EMV chip cards** for testing
- **Network connectivity** for payment host communication

### Software Requirements
- **Android Studio** Electric Eel (2022.1.1) or newer
- **Android SDK** API 23-34
- **Kotlin** 1.9.22+
- **Gradle** 8.0+

## Installation

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/soft-pos-android.git
cd soft-pos-android
```

### 2. Open in Android Studio
- Open Android Studio
- Select "Open an existing project"
- Navigate to the cloned directory
- Wait for Gradle sync to complete

### 3. Configure the Application
Update configuration in `Constants.kt`:
```kotlin
const val DEFAULT_HOST_IP = "your-payment-host.com"
const val DEFAULT_HOST_PORT = 443
const val DEFAULT_TERMINAL_ID = "your-terminal-id"
const val DEFAULT_MERCHANT_ID = "your-merchant-id"
```

### 4. Build and Install
```bash
./gradlew assembleDebug
./gradlew installDebug
```

## Project Structure

```
app/src/main/kotlin/com/softpos/app/
├── MainActivity.kt                 # Main entry point with NFC handling
├── TransactionViewModel.kt         # Business logic and state management
├── TransactionScreen.kt           # Jetpack Compose UI
├── NFCCardReader.kt              # EMV chip card reading
├── ISO8583Message.kt             # Message formatting
├── TransactionManager.kt         # Transaction processing
├── HostCommunication.kt          # Network communication
├── EMVCardData.kt               # Data classes
├── TransactionResponse.kt        # Response handling
├── ResponseParser.kt            # Response parsing
├── data/
│   └── PreferencesManager.kt    # Settings management
├── utils/
│   ├── Constants.kt             # Application constants
│   ├── Extensions.kt            # Kotlin extensions
│   ├── SecurityUtils.kt         # Security utilities
│   └── ValidationUtils.kt       # Input validation
└── ui/theme/
    ├── Theme.kt                 # Material Design theme
    ├── Color.kt                 # Color definitions
    └── Type.kt                  # Typography
```

## Usage

### Basic Transaction Flow

1. **Launch Application**
    - Ensure NFC is enabled on device
    - Application displays transaction screen

2. **Enter Transaction Amount**
    - Input amount in decimal format (e.g., 10.00)
    - Tap "Start Transaction" button

3. **Tap Card**
    - Hold EMV chip card near device NFC antenna
    - Application reads card data automatically

4. **Process Transaction**
    - App sends authorization request to payment host
    - Displays approval/decline result

### Configuration

Access settings through `PreferencesManager`:
```kotlin
val prefsManager = PreferencesManager(context)
prefsManager.setHostIP("192.168.1.100")
prefsManager.setHostPort(8080)
prefsManager.setTerminalId("12345678")
```

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

### Manual Testing
1. Use demo mode with mock card data
2. Test with EMV test cards
3. Verify network communication with test host

## Security Considerations

### Development Environment
- Uses HTTP for localhost testing
- Mock responses for offline development
- Debug logging enabled

### Production Environment
- **Enable HTTPS only** in network security config
- **Implement certificate pinning** for payment hosts
- **Add proper key management** for sensitive data
- **Follow PCI DSS requirements** for card data handling
- **Enable ProGuard** for code obfuscation

### PCI DSS Compliance
```kotlin
// Example: Secure card data handling
val encryptedPAN = SecurityUtils.encryptData(cardData.pan)
val maskedPAN = cardData.getMaskedPAN() // For display only
```

## API Documentation

### EMV Card Reading
```kotlin
val cardReader = NFCCardReader()
val cardData: EMVCardData = cardReader.readCard(nfcTag)
```

### ISO8583 Message Creation
```kotlin
val message = ISO8583Message("0100") // Authorization request
message.setField(2, pan)
message.setField(4, amount)
val packedMessage = message.pack()
```

### Host Communication
```kotlin
val hostComm = HostCommunication(hostIP, hostPort)
hostComm.connect()
val response = hostComm.sendMessage(requestData)
```

## Troubleshooting

### Common Issues

**NFC Not Working**
- Verify NFC is enabled in device settings
- Check if device supports ISO-DEP
- Ensure proper card positioning

**Network Connection Failed**
- Verify host IP and port configuration
- Check network connectivity
- Confirm firewall settings

**Card Reading Errors**
- Try different EMV cards
- Clean card contacts
- Adjust card positioning

### Debug Logging
Enable verbose logging in debug builds:
```kotlin
if (BuildConfig.DEBUG) {
    Log.d(TAG, "Debug information")
}
```

## Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/new-feature`)
3. Commit changes (`git commit -am 'Add new feature'`)
4. Push to branch (`git push origin feature/new-feature`)
5. Create Pull Request

### Code Standards
- Follow Kotlin coding conventions
- Use meaningful variable names
- Add documentation for public APIs
- Include unit tests for new features

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support and questions:
- Create an issue on GitHub
- Email: support@softpos.com
- Documentation: https://docs.softpos.com

## Acknowledgments

- Android NFC documentation and samples
- EMVCo specifications
- ISO8583 standard documentation
- Jetpack Compose development guides

---

**⚠️ Important:** This is a development/educational implementation. For production use, ensure proper security audits, PCI DSS compliance, and certification from payment networks.#moayer-softpos
