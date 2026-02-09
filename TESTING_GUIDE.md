# ARShop Testing Guide

This guide provides comprehensive instructions for testing the ARShop Android e-commerce application.

## Table of Contents
1. [Test Environment Setup](#test-environment-setup)
2. [Running Unit Tests](#running-unit-tests)
3. [Running Instrumented Tests](#running-instrumented-tests)
4. [Manual Testing](#manual-testing)
5. [AR Testing](#ar-testing)
6. [Payment Testing](#payment-testing)
7. [Troubleshooting](#troubleshooting)

## Test Environment Setup

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- Android SDK 34
- ARCore-compatible device for AR testing
- Firebase project configured (see FIREBASE_SETUP.md)
- Test payment gateway accounts

### Initial Setup

1. **Clone and Build**
   ```bash
   git clone <repository-url>
   cd arshop
   ./gradlew clean build
   ```

2. **Configure Firebase**
   - Add `google-services.json` to `app/` directory
   - Enable Authentication (Email/Password, Google Sign-In)
   - Create Firestore database
   - Enable Firebase Storage

3. **Configure API Keys**
   - Copy `local.properties.template` to `local.properties`
   - Add test API keys:
     ```properties
     STRIPE_PUBLISHABLE_KEY=pk_test_...
     RAZORPAY_KEY_ID=rzp_test_...
     ```

4. **Create Test Data**
   - Add sample products to Firestore `products` collection
   - Create admin user in `admin_users` collection
   - Add test categories

## Running Unit Tests

### Repository Tests
```bash
./gradlew test
```

Tests cover:
- AuthRepository operations
- ProductRepository CRUD
- CartRepository sync
- OrderRepository creation
- Utility functions

### ViewModel Tests
```bash
./gradlew testDebugUnitTest
```

Tests cover:
- State management
- Error handling
- Loading states
- Navigation events

### Run Specific Test
```bash
./gradlew test --tests "AuthRepositoryTest"
```

## Running Instrumented Tests

### Database Tests
```bash
./gradlew connectedAndroidTest
```

Tests cover:
- Room DAO operations
- Database migrations
- Type converters
- Query performance

### UI Tests
```bash
./gradlew connectedDebugAndroidTest
```

Tests cover:
- Login flow
- Product browsing
- Cart operations
- Checkout flow

### Run on Specific Device
```bash
adb devices
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.deviceSerial=<device-id>
```

## Manual Testing

### Authentication Flow

#### Test Cases
1. **Sign Up**
   - Valid email and password → Success
   - Invalid email format → Error
   - Password < 8 chars → Error
   - Mismatched passwords → Error
   - Existing email → Firebase error

2. **Login**
   - Valid credentials → Navigate to Home
   - Invalid email → Error
   - Wrong password → Firebase error
   - Google Sign-In → OAuth flow

3. **Forgot Password**
   - Valid email → Reset email sent
   - Invalid email → Error
   - Non-existent email → Firebase error

### Product Browsing

#### Test Cases
1. **Home Screen**
   - Featured products load → Display grid
   - Categories load → Display chips
   - Pull to refresh → Reload data
   - Empty state → Show message

2. **Browse Screen**
   - Products grid loads → Display cards
   - Apply filters → Update results
   - Search products → Filter by query
   - Pagination → Load more on scroll
   - AR badge → Shows for AR-enabled products

3. **Product Detail**
   - Product loads → Display all info
   - Select size → Updates selection
   - Select color → Updates selection
   - Change quantity → Updates value
   - Add to cart → Cart updated
   - Try AR → Navigate to AR screen

### Shopping Cart

#### Test Cases
1. **Cart Operations**
   - View cart items → Display list
   - Increase quantity → Updates total
   - Decrease quantity → Updates total
   - Remove item → Item deleted
   - Empty cart → Show empty state
   - Proceed to checkout → Navigate

### Checkout Flow

#### Test Cases
1. **Multi-Step Checkout**
   - Select address → Proceed
   - New address → Add and select
   - Select payment → Choose method
   - Review order → Verify details
   - Place order → Create order
   - Confirmation → Show order number

### Profile & Orders

#### Test Cases
1. **Profile**
   - View profile → Display user info
   - Update profile → Save changes
   - View addresses → List addresses
   - Add address → Save new address
   - Delete address → Remove address
   - Logout → Clear auth state

2. **Order History**
   - View orders → Display list
   - Filter by status → Update list
   - View order detail → Show full info
   - Cancel order → Update status
   - Reorder → Add items to cart

### Admin Panel

#### Test Cases
1. **Admin Access**
   - Non-admin user → No access
   - Admin user → Show dashboard

2. **Product Management**
   - View products → List all
   - Search products → Filter results
   - Add product → Create new
   - Edit product → Update existing
   - Delete product → Remove (with confirm)
   - Upload images → Save to Storage
   - Upload 3D model → Save to Storage

3. **Order Management**
   - View all orders → Display list
   - Filter by status → Update list
   - Update order status → Save change
   - View order detail → Show full info

## AR Testing

### Requirements
- ARCore-compatible device (see: https://developers.google.com/ar/devices)
- Android 7.0 (API 24) or higher
- Camera permission granted
- Good lighting conditions
- Flat surface for footwear placement

### AR Test Cases

#### 1. Camera Permissions
- First launch → Request camera permission
- Permission denied → Show rationale
- Permission granted → Initialize AR

#### 2. AR Try-On (Clothing)
- Navigate to AR → Load product
- Body detection → Track body (if supported)
- Manual placement → Fallback mode
- Scale adjustment → Resize model
- Screenshot → Save to gallery
- Share → Share screenshot

#### 3. AR Try-On (Footwear)
- Navigate to AR → Load product
- Floor mode → Detect plane
- Tap to place → Place model
- Rotate → Adjust orientation
- Size selector → Scale model
- Foot tracking → Track foot (future)

#### 4. AR Performance
- Model loading → < 5 seconds
- Tracking stability → Smooth tracking
- Frame rate → > 30 FPS
- No crashes → Stable session

### AR Testing Checklist
- [ ] Camera permission flow works
- [ ] AR session initializes successfully
- [ ] 3D models load correctly
- [ ] Tracking is stable
- [ ] Screenshots save properly
- [ ] Share functionality works
- [ ] Back navigation cleans up session
- [ ] No memory leaks

## Payment Testing

### Test Mode Configuration
All payment SDKs support test mode. Use test credentials only.

### Stripe Testing

#### Test Cards
```
Success: 4242 4242 4242 4242
Declined: 4000 0000 0000 0002
Insufficient funds: 4000 0000 0000 9995
```

#### Test Cases
- Valid card → Payment success
- Invalid card number → Validation error
- Declined card → Payment failed
- Test 3D Secure → Additional verification

### PayPal Testing
- Use PayPal Sandbox account
- Test buyer credentials from PayPal Developer
- Test Cases:
  - Successful payment → Order created
  - Cancelled payment → Return to cart
  - Insufficient funds → Error message

### Razorpay Testing
- Use Razorpay Test Mode
- Test Cases:
  - Successful payment → Order created
  - Failed payment → Error handling
  - Payment timeout → Timeout message

### Google Pay Testing
- Configure test merchant account
- Use test credit cards
- Test Cases:
  - Payment sheet shows → Display options
  - Payment successful → Order created
  - Payment cancelled → Return to checkout

## Troubleshooting

### Build Issues

**Problem:** Dependencies not resolving
```bash
./gradlew clean
./gradlew --refresh-dependencies build
```

**Problem:** Gradle sync failed
- File → Invalidate Caches / Restart
- Delete `.gradle` folder
- Sync again

### Firebase Issues

**Problem:** google-services.json missing
- Download from Firebase Console
- Place in `app/` directory
- Sync project

**Problem:** Authentication not working
- Check Firebase Console → Authentication enabled
- Verify SHA-1 certificate registered
- Check package name matches

### AR Issues

**Problem:** ARCore not supported
- Device must be ARCore-compatible
- Check: https://developers.google.com/ar/devices

**Problem:** AR session fails to initialize
- Check camera permission granted
- Ensure ARCore app is installed
- Update ARCore from Play Store

**Problem:** Model not loading
- Check 3D model exists in Firebase Storage
- Verify model format (GLB/GLTF)
- Check internet connectivity

### Test Failures

**Problem:** Unit tests fail
- Check mock data setup
- Verify coroutine test rules
- Check assertions

**Problem:** Instrumented tests fail
- Ensure device/emulator running
- Check test APK installed
- Verify test data in database

## Test Data Setup

### Sample Products
```kotlin
Product(
    id = "test_tshirt_1",
    name = "Classic White T-Shirt",
    description = "100% cotton",
    price = 29.99,
    category = "Clothing",
    gender = "Male",
    sizes = listOf("S", "M", "L", "XL"),
    colors = listOf(ProductColor("White", "#FFFFFF")),
    isArEnabled = true,
    modelUrl = "https://...tshirt.glb"
)
```

### Sample User
```kotlin
User(
    uid = "test_user_123",
    email = "test@example.com",
    displayName = "Test User",
    addresses = listOf(
        Address(
            street = "123 Test St",
            city = "Test City",
            state = "CA",
            zipCode = "12345",
            country = "USA"
        )
    )
)
```

### Admin User
```kotlin
AdminUser(
    uid = "admin_user_123",
    email = "admin@arshop.com",
    role = "admin",
    permissions = listOf("manage_products", "manage_orders")
)
```

## Performance Testing

### Metrics to Monitor
- App startup time < 2 seconds
- Screen navigation < 500ms
- API response time < 3 seconds
- Image loading < 1 second
- AR initialization < 5 seconds
- Memory usage < 200MB

### Tools
- Android Profiler (CPU, Memory, Network)
- Layout Inspector (UI hierarchy)
- Database Inspector (Room data)
- Logcat (debugging)

## Continuous Integration

### GitHub Actions (Future)
```yaml
- Run unit tests
- Run lint checks
- Build APK
- Run instrumented tests
- Generate test report
```

## Test Coverage Goals

- Unit Tests: > 70%
- Integration Tests: > 50%
- UI Tests: Critical flows covered
- Manual Tests: All features tested

## Reporting Issues

When reporting bugs, include:
1. Steps to reproduce
2. Expected behavior
3. Actual behavior
4. Device/Android version
5. Logcat output
6. Screenshots/videos

---

**Note:** This is a comprehensive testing guide. Adapt based on your testing requirements and environment.
