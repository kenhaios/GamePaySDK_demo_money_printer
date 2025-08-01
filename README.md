# Money Factory - Android Idle Clicker Game

A Kotlin-based Android idle clicker game demonstrating payment SDK integration. Players click to print money, purchase automation tools, and buy premium currency through in-app purchases.

## 🎮 Game Features

### Core Gameplay
- **Money Printing**: Click the factory button to generate cash
- **Automation System**: 7 purchasable automation tools with exponential pricing
- **Offline Earnings**: Earn money while away (up to 4 hours)
- **Persistent Progress**: Game state saved automatically

### Automation Tools
1. **Basic Printer** - 10 cash - 0.1/sec
2. **Enhanced Printer** - 100 cash - 1/sec  
3. **Money Press** - 1,000 cash - 8/sec
4. **Industrial Printer** - 12,000 cash - 47/sec
5. **Money Factory** - 130,000 cash - 260/sec
6. **Mega Factory** - 1,400,000 cash - 1,400/sec
7. **Corporate Empire** - 20,000,000 cash - 7,800/sec

### Premium Features
- **Coin Shop**: Purchase premium currency with real money
- **5 Coin Packages**: From $0.99 to $39.99 with bonus coins
- **Payment Integration**: Mock payment system ready for real SDK integration

## 🛠 Technical Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM with StateFlow
- **Min SDK**: API 24 (Android 7.0)
- **Target SDK**: API 36

## 🏗 Project Structure

```
app/src/main/java/com/terminal3/demo/money_printer/
├── data/
│   ├── models/          # Game data classes
│   └── repositories/    # Game state persistence
├── game/
│   └── engine/         # Core game logic
├── payment/            # Payment SDK integration
├── ui/
│   ├── main/          # Main game screen
│   ├── shop/          # Coin shop
│   ├── navigation/    # App navigation
│   └── theme/         # UI theming
└── utils/             # Helper utilities
```

## 🚀 Getting Started

### Prerequisites
- Android Studio Giraffe or later
- JDK 11 or later
- Android SDK 24+

### Building the Project
```bash
# Build debug APK
./gradlew assembleDebug

# Run tests
./gradlew test

# Install on device/emulator
./gradlew installDebug
```

## 💰 Payment Integration

The game includes a complete payment integration structure with:

- **PaymentManager**: Interface for payment SDK integration
- **Mock Implementation**: Simulates payment flow for testing
- **Coin Shop UI**: Complete purchase flow with loading states
- **Transaction Validation**: Ready for server-side validation

### Integrating Your Payment SDK

1. Replace mock implementation in `PaymentManager.kt`
2. Add your SDK dependency to `build.gradle.kts`
3. Initialize SDK in `PaymentManager.initializeSDK()`
4. Implement actual payment processing in `purchaseCoins()`

## 🎨 Game Design

### Color Scheme
- **Primary**: Green (#4CAF50) - money theme
- **Secondary**: Gold (#FFD700) - premium elements  
- **Background**: Dark gray (#2E2E2E)
- **Text**: White (#FFFFFF)
- **Accent**: Orange (#FF9800) - buttons

### Visual Features
- **Click Animation**: Factory button scales on tap
- **Money Formatting**: Large numbers formatted with suffixes (K, M, B, T)
- **Progress Tracking**: Real-time income display
- **Offline Rewards**: Welcome back dialog with earnings

## 🧪 Testing

The project includes:
- Unit test structure for game calculations
- Mock payment integration for testing
- Automated game state persistence
- Error handling for payment failures

## 🔧 Configuration

Key game parameters can be adjusted in:
- `AutomationTools.kt`: Tool costs and production rates
- `CoinPackages.kt`: Premium currency packages
- `GameEngine.kt`: Core game mechanics
