# Money Factory Android Game - Development Instructions

## Project Overview
Create a simple Android idle clicker game called "Money Factory" that demonstrates payment SDK integration. The game should be built using **Kotlin** with modern Android development practices.

## Technical Requirements

### Development Stack
- **Language**: Kotlin (preferred for modern Android development)
- **Minimum SDK**: API 24 (Android 7.0)
- **Target SDK**: Latest stable Android API
- **Architecture**: MVVM with ViewBinding/DataBinding
- **Build System**: Gradle with Kotlin DSL

### Dependencies to Include
```kotlin
// Core Android
implementation 'androidx.core:core-ktx:1.12.0'
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0'

// Animation
implementation 'com.airbnb.android:lottie:6.1.0'

// Preferences for game state
implementation 'androidx.preference:preference-ktx:1.2.1'

// Your Payment SDK dependency here
// implementation 'com.yourcompany:payment-sdk:x.x.x'
```

## Game Design Specifications

### Core Game Mechanics

#### 1. Main Screen Layout
Create a single activity with the following components:
- **Factory Visual**: Central animated money printing factory
- **Stats Panel**: Display current cash, coins, money per second
- **Click Button**: Large, prominent button for manual money generation
- **Automation Panel**: Scrollable list of purchasable automation tools
- **Coin Shop Button**: Access to premium currency purchases

#### 2. Currency System
Implement two currencies:
- **Cash**: Earned through gameplay (clicking + automation)
- **Coins**: Premium currency purchased with real money
- **Conversion Rate**: 1 Coin = 100 Cash (adjustable)

#### 3. Click Mechanics
- Base click value: 1 cash per click
- Visual feedback: money animation, sound effect, vibration
- Implement click multipliers through upgrades

### Automation System

Create purchasable automation tools with exponential pricing:

```kotlin
data class AutomationTool(
    val id: String,
    val name: String,
    val description: String,
    val baseCost: Long,
    val baseProduction: Double,
    val costMultiplier: Double = 1.15,
    var owned: Int = 0
)
```

#### Automation Tools List
1. **Basic Printer** - 10 cash - 0.1/sec
2. **Enhanced Printer** - 100 cash - 1/sec  
3. **Money Press** - 1,000 cash - 8/sec
4. **Industrial Printer** - 12,000 cash - 47/sec
5. **Money Factory** - 130,000 cash - 260/sec
6. **Mega Factory** - 1,400,000 cash - 1,400/sec
7. **Corporate Empire** - 20,000,000 cash - 7,800/sec

### Visual Design Requirements

#### Factory Animation
Create SVG-based or programmatic animations:
- Idle state: Subtle machinery movement
- Active state: Papers flying, gears turning, money being printed
- Upgrade state: Visual improvements as automation increases

```kotlin
// Example animation code structure
class FactoryAnimationView : View {
    fun startPrintingAnimation() {
        // Implement money printing visual effect
    }
    
    fun updateFactoryLevel(automationLevel: Int) {
        // Change factory appearance based on upgrades
    }
}
```

#### UI Color Scheme
- Primary: Green (#4CAF50) - money theme
- Secondary: Gold (#FFD700) - premium elements  
- Background: Dark gray (#2E2E2E)
- Text: White (#FFFFFF)
- Accent: Orange (#FF9800) - buttons

### Game State Management

#### Data Persistence
Use SharedPreferences or Room database to save:
- Current cash amount
- Current coins amount
- Automation tools owned
- Total money earned
- Game start time for offline earnings

```kotlin
data class GameState(
    val cash: Long = 0,
    val coins: Int = 0,
    val automationTools: Map<String, Int> = emptyMap(),
    val totalEarned: Long = 0,
    val lastSaveTime: Long = System.currentTimeMillis()
)
```

#### Offline Earnings
Calculate money earned while app was closed:
- Maximum offline time: 4 hours
- Show popup with earnings when returning
- Option to double offline earnings with coin purchase

## Payment SDK Integration

### Coin Shop Implementation

#### Shop Items Structure
```kotlin
data class CoinPackage(
    val id: String,
    val coinAmount: Int,
    val priceUSD: String,
    val bonus: String = "",
    val isPopular: Boolean = false
)

val coinPackages = listOf(
    CoinPackage("small", 100, "$0.99", "Starter Pack"),
    CoinPackage("medium", 500, "$4.99", "+50 Bonus", true),
    CoinPackage("large", 1200, "$9.99", "+200 Bonus"),
    CoinPackage("mega", 2800, "$19.99", "+500 Bonus"),
    CoinPackage("ultimate", 6000, "$39.99", "+1000 Bonus")
)
```

#### Payment Integration Points
1. **Purchase Trigger**: When user taps any coin package
2. **SDK Initialization**: Initialize your payment SDK in Application class
3. **Transaction Handling**: Process payment and award coins on success
4. **Error Handling**: Show appropriate messages for failed transactions

```kotlin
class CoinShopActivity : AppCompatActivity() {
    
    private fun purchaseCoins(packageId: String) {
        // Show loading state
        showLoadingDialog()
        
        // Initialize your payment SDK
        PaymentSDK.processPurchase(
            packageId = packageId,
            onSuccess = { transactionId ->
                // Award coins to user
                awardCoins(packageId)
                hideLoadingDialog()
                showSuccessMessage()
            },
            onError = { error ->
                hideLoadingDialog()
                showErrorMessage(error)
            }
        )
    }
}
```

### Unique Items Shop
Implement special items purchasable with coins:
- **2x Click Multiplier** - 50 coins - Doubles click value
- **Golden Printer** - 200 coins - Unique automation tool
- **Money Magnet** - 100 coins - Attracts falling money automatically
- **Time Accelerator** - 300 coins - 2x production speed for 1 hour

## Code Structure

### Project Package Structure
```
com.yourcompany.moneyfactory/
├── ui/
│   ├── main/          # Main game screen
│   ├── shop/          # Coin shop
│   └── common/        # Shared UI components
├── data/
│   ├── models/        # Data classes
│   ├── repositories/  # Data management
│   └── preferences/   # SharedPreferences wrapper
├── game/
│   ├── engine/        # Game logic
│   ├── automation/    # Automation calculations
│   └── animations/    # Custom views and animations
├── payment/
│   ├── PaymentManager # Payment SDK wrapper
│   └── models/        # Payment-related data classes
└── utils/             # Helper classes
```

### Key Classes to Implement

#### 1. GameEngine.kt
```kotlin
class GameEngine {
    fun calculateMoneyPerSecond(): Double
    fun processClick(): Long
    fun calculateOfflineEarnings(offlineTime: Long): Long
    fun purchaseAutomation(toolId: String): Boolean
}
```

#### 2. AnimationManager.kt
```kotlin
class AnimationManager {
    fun playClickAnimation()
    fun updateFactoryAnimation(level: Int)
    fun showMoneyFloatingEffect(amount: Long)
}
```

#### 3. PaymentManager.kt
```kotlin
class PaymentManager {
    fun initializeSDK()
    fun purchaseCoins(packageId: String, callback: PaymentCallback)
    fun validatePurchase(transactionId: String): Boolean
}
```

## Implementation Steps

### Phase 1: Core Game
1. Set up project structure and dependencies
2. Create main game screen layout
3. Implement basic clicking mechanics
4. Add simple factory animation
5. Create automation system
6. Implement game state persistence

### Phase 2: Visual Polish
1. Create SVG animations or custom drawn elements
2. Add sound effects and haptic feedback
3. Implement money floating animations
4. Polish UI with proper styling

### Phase 3: Payment Integration
1. Integrate your payment SDK
2. Create coin shop UI
3. Implement purchase flow
4. Add unique items system
5. Test payment scenarios

### Phase 4: Testing & Polish
1. Test offline earnings calculation
2. Verify payment integration thoroughly
3. Add analytics tracking
4. Performance optimization
5. Final UI/UX polish

## Testing Requirements

### Unit Tests
- Game calculation logic
- Automation pricing formulas
- Offline earnings calculation

### Integration Tests  
- Payment flow end-to-end
- Game state persistence
- SDK integration points

### Manual Testing Checklist
- [ ] Click mechanics work smoothly
- [ ] Animations perform well on low-end devices
- [ ] Payment flow completes successfully
- [ ] Offline earnings calculate correctly
- [ ] Game state persists across app restarts
- [ ] All automation tools function properly

## Performance Considerations
- Use object pooling for animation effects
- Optimize money calculation updates (limit to 10 FPS)
- Implement efficient number formatting for large values
- Lazy load shop images and animations
- Use appropriate data types (Long for money, Double for rates)

## Security Notes
- Validate all purchase transactions server-side
- Implement client-side purchase verification
- Use obfuscation for sensitive game values
- Add basic anti-cheat measures for money values

## Additional Features (Optional)
- Achievement system
- Daily bonuses
- Prestige system for long-term progression  
- Social features (leaderboards)
- Push notifications for offline earnings

This specification provides a complete roadmap for building your Money Factory game with payment SDK integration. Adjust the payment integration sections based on your specific SDK requirements.
