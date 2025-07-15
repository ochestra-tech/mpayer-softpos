// ===== CONTRIBUTING.md =====
# Contributing to Soft POS Application

We love your input! We want to make contributing to this project as easy and transparent as possible, whether it's:

- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

## Development Process

We use GitHub to host code, to track issues and feature requests, as well as accept pull requests.

### Pull Requests

1. Fork the repo and create your branch from `main`.
2. If you've added code that should be tested, add tests.
3. If you've changed APIs, update the documentation.
4. Ensure the test suite passes.
5. Make sure your code lints.
6. Issue that pull request!

## Code Style

### Kotlin Style Guide

We follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Use camelCase for function and variable names
- Use PascalCase for class names
- Use UPPER_SNAKE_CASE for constants
- Maximum line length is 120 characters

### Example

```kotlin
class TransactionManager(
    private val terminalId: String,
    private val merchantId: String
) {
    companion object {
        private const val MAX_RETRY_ATTEMPTS = 3
        private const val TAG = "TransactionManager"
    }
    
    fun processTransaction(amount: Double): TransactionResult {
        // Implementation here
    }
}
```

### Compose Guidelines

- Use meaningful composable names
- Keep composables small and focused
- Use state hoisting when appropriate
- Follow Material Design guidelines

```kotlin
@Composable
fun TransactionAmountInput(
    amount: String,
    onAmountChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = amount,
        onValueChange = onAmountChange,
        label = { Text("Transaction Amount") },
        modifier = modifier
    )
}
```

## Security Guidelines

### Data Handling
- Never log sensitive card data
- Always mask PAN numbers in logs and UI
- Use Android Keystore for sensitive data storage
- Validate all inputs before processing

### Example
```kotlin
// Good
Log.d(TAG, "Processing transaction for card: ${pan.maskCardNumber()}")

// Bad
Log.d(TAG, "Processing transaction for card: $pan")
```

### Network Security
- Use HTTPS in production
- Implement certificate pinning
- Validate all network responses
- Handle network errors gracefully

## Testing Guidelines

### Unit Tests
- Write tests for all business logic
- Use meaningful test names
- Follow AAA pattern (Arrange, Act, Assert)

```kotlin
@Test
fun `should create valid authorization request when card data is valid`() {
    // Arrange
    val cardData = createValidTestCard()
    val amount = 10.00
    
    // Act
    val request = transactionManager.createAuthorizationRequest(cardData, amount, "840")
    
    // Assert
    assertEquals("0100", request.getMTI())
    assertTrue(request.hasField(2))
    assertTrue(request.hasField(4))
}
```

### UI Tests
- Test user interactions
- Verify UI state changes
- Test error scenarios

```kotlin
@Test
fun should_display_error_when_amount_is_invalid() {
    composeTestRule.onNodeWithText("Transaction Amount").performTextInput("invalid")
    composeTestRule.onNodeWithText("Start Transaction").performClick()
    composeTestRule.onNodeWithText("Invalid amount").assertIsDisplayed()
}
```

## Documentation

### Code Documentation
- Document all public APIs
- Include parameter descriptions
- Provide usage examples

```kotlin
/**
 * Processes an EMV card transaction
 * 
 * @param cardData The EMV card data read from NFC
 * @param amount The transaction amount in dollars
 * @param currencyCode The ISO currency code (e.g., "840" for USD)
 * @return TransactionResponse containing the result
 * @throws TransactionException if the transaction fails
 */
fun processTransaction(
    cardData: EMVCardData,
    amount: Double,
    currencyCode: String
): TransactionResponse
```

### README Updates
- Update README for new features
- Include setup instructions
- Provide usage examples

## Commit Messages

Use clear and meaningful commit messages:

```
feat: add support for Mastercard contactless payments
fix: resolve NFC timeout issues on Samsung devices
docs: update API documentation for transaction manager
test: add unit tests for ISO8583 message parsing
refactor: simplify card data validation logic
```

### Commit Message Format
```
<type>(<scope>): <subject>

<body>

<footer>
```

Types:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes
- `refactor`: Code refactoring
- `test`: Adding or updating tests
- `chore`: Maintenance tasks

## Issue Reporting

### Bug Reports

Use the bug report template and include:
- Device information (model, Android version)
- Steps to reproduce
- Expected vs actual behavior
- Screenshots if applicable
- Relevant log output

### Feature Requests

Use the feature request template and include:
- Clear description of the feature
- Use case and benefits
- Possible implementation approach
- Any relevant examples

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

## Questions?

Feel free to contact the maintainers or open an issue for any questions about contributing.