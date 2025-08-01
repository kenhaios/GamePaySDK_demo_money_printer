# Money Factory Android Game - Claude Instructions

## Project Overview
This is an Android idle clicker game called "Money Factory" built in **Kotlin** that demonstrates payment SDK integration. The game features money printing mechanics, automation systems, and premium currency purchases.

## Key Project Information

### Tech Stack
- **Language**: Kotlin (required)
- **Architecture**: MVVM with ViewBinding/DataBinding  
- **Min SDK**: API 24 (Android 7.0)
- **Build**: Gradle with Kotlin DSL

### Core Dependencies
- AndroidX Core KTX, AppCompat, ConstraintLayout
- Lifecycle ViewModel KTX
- Lottie for animations
- Preference KTX for game state
- Payment SDK integration (to be specified)

### Game Structure
- **Main Screen**: Factory animation, stats panel, click button, automation panel, coin shop
- **Currencies**: Cash (gameplay) and Coins (premium)
- **Automation**: 7 purchasable tools with exponential pricing
- **Payment Integration**: Coin packages from $0.99 to $39.99

## Development Commands

### Testing
```bash
# Run unit tests
./gradlew test

# Run instrumented tests  
./gradlew connectedAndroidTest
```

### Build Commands
```bash
# Debug build
./gradlew assembleDebug

# Release build
./gradlew assembleRelease

# Clean build
./gradlew clean
```

### Code Quality
```bash
# Lint check
./gradlew lint

# Kotlin static analysis (if configured)
./gradlew detekt
```

## Important Implementation Notes

### Architecture Patterns
- Use MVVM architecture with ViewModels
- Implement Repository pattern for data management
- Use LiveData/StateFlow for UI updates
- Persist game state in SharedPreferences or Room

### Payment Integration Priority
- Focus on secure payment flow implementation
- Validate all transactions server-side
- Implement proper error handling for payment failures
- Test payment scenarios thoroughly

### Performance Requirements
- Optimize for low-end Android devices
- Limit money calculation updates to 10 FPS
- Use object pooling for animation effects
- Implement efficient number formatting for large values

### Security Considerations
- Obfuscate sensitive game values
- Add basic anti-cheat measures
- Client-side purchase verification
- Server-side transaction validation

## File Structure
```
app/src/main/java/com/yourcompany/moneyfactory/
├── ui/main/          # Main game screen
├── ui/shop/          # Coin shop  
├── data/models/      # Data classes
├── game/engine/      # Game logic
├── payment/          # Payment SDK integration
└── utils/            # Helper classes
```

## Testing Checklist
- [ ] Click mechanics work smoothly
- [ ] Payment flow completes successfully  
- [ ] Offline earnings calculate correctly
- [ ] Game state persists across restarts
- [ ] Animations perform well on low-end devices

## When Working on This Project
1. Always test payment integration thoroughly
2. Follow Kotlin coding conventions
3. Implement proper error handling
4. Optimize for performance on older devices
5. Validate all monetary calculations
6. Use appropriate data types (Long for money, Double for rates)