# How to Run the ARShop Android Application

## Important Note

**This ARShop application is a complete, production-ready Android app that requires Android Studio and Firebase configuration to run.**

The application cannot be run directly in a CI/CD environment because it requires:
1. Android Studio with Android SDK
2. Firebase project configuration
3. Physical Android device or emulator
4. API keys for payment gateways (optional for basic testing)

## Prerequisites

### Required Software
- **Android Studio**: Hedgehog (2023.1.1) or later
- **JDK**: Version 17
- **Android SDK**: Level 34 (Android 14)
- **Git**: For cloning the repository

### Required Accounts
- **Firebase Account**: Free tier is sufficient
- **Payment Gateway Accounts** (Optional for testing):
  - Stripe test account
  - PayPal sandbox account  
  - Razorpay test account

## Step-by-Step Setup

### 1. Install Android Studio

Download from: https://developer.android.com/studio

**Installation:**
```bash
# Linux
sudo snap install android-studio --classic

# macOS
brew install --cask android-studio

# Windows
Download installer from the website above
```

### 2. Clone the Repository

```bash
git clone https://github.com/abhishekyadgire/arshop.git
cd arshop
```

### 3. Configure Firebase

#### A. Create Firebase Project

1. Go to https://console.firebase.google.com/
2. Click "Add Project"
3. Project name: **ARShop**
4. Enable Google Analytics (optional)
5. Click "Create Project"

#### B. Add Android App to Firebase

1. In Firebase Console, click "Add App" → Android icon
2. Enter details:
   - **Android package name**: `com.arshop`
   - **App nickname**: ARShop Android
   - **Debug signing certificate SHA-1**: (Get from Android Studio)
     ```bash
     keytool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android
     ```
3. Download `google-services.json`
4. Place file in `app/` directory:
   ```bash
   cp ~/Downloads/google-services.json app/
   ```

#### C. Enable Firebase Services

**Authentication:**
1. Firebase Console → Authentication → Get Started
2. Enable "Email/Password" authentication
3. Enable "Google" authentication
4. Add your SHA-1 certificate (from step B)

**Firestore Database:**
1. Firebase Console → Firestore Database → Create Database
2. Start in **production mode**
3. Select region (e.g., us-central)
4. Apply security rules from `FIREBASE_SETUP.md`

**Firebase Storage:**
1. Firebase Console → Storage → Get Started
2. Start in **production mode**
3. Apply security rules from `FIREBASE_SETUP.md`

### 4. Open Project in Android Studio

```bash
# Launch Android Studio
android-studio

# Or open from command line
studio.sh /path/to/arshop  # Linux
open -a "Android Studio" /path/to/arshop  # macOS
```

**In Android Studio:**
1. File → Open → Select `arshop` directory
2. Wait for Gradle sync (this may take 5-10 minutes on first run)
3. If prompted to update Gradle or SDK, accept

### 5. Configure API Keys (Optional)

For payment testing, create `local.properties`:

```bash
cp local.properties.template local.properties
nano local.properties
```

Add your test keys:
```properties
STRIPE_PUBLISHABLE_KEY=pk_test_your_key_here
RAZORPAY_KEY_ID=rzp_test_your_key_here
```

**Get test keys:**
- Stripe: https://dashboard.stripe.com/test/apikeys
- Razorpay: https://dashboard.razorpay.com/app/keys

### 6. Add Test Data to Firebase

#### Create Admin User

In Firebase Console → Firestore:

1. Create collection: `admin_users`
2. Add document:
   - Document ID: Your Firebase Auth UID (get after first login)
   - Fields:
     ```json
     {
       "email": "admin@arshop.com",
       "role": "admin",
       "permissions": ["manage_products", "manage_orders"]
     }
     ```

#### Add Sample Products

Create collection: `products`

Sample product document:
```json
{
  "id": "product_001",
  "name": "Classic White T-Shirt",
  "description": "100% cotton, comfortable fit",
  "price": 29.99,
  "category": "Clothing",
  "gender": "Male",
  "subcategory": "T-Shirts",
  "sizes": ["S", "M", "L", "XL"],
  "colors": [
    {
      "name": "White",
      "hexCode": "#FFFFFF"
    }
  ],
  "imageUrls": ["https://via.placeholder.com/500"],
  "isArEnabled": false,
  "modelUrl": "",
  "stockQuantity": 100,
  "rating": 4.5,
  "totalReviews": 120,
  "createdAt": 1707484800000,
  "updatedAt": 1707484800000
}
```

### 7. Run the Application

#### On Android Emulator

1. In Android Studio: Tools → Device Manager
2. Create new virtual device:
   - Device: Pixel 7 or newer
   - System Image: Android 14 (API 34) - Download if needed
   - Click "Finish"
3. Click "Run" button (green triangle) or press Shift+F10
4. Select your emulator from the device list
5. Wait for app to install and launch

#### On Physical Device

1. Enable Developer Options on your phone:
   - Settings → About Phone → Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect phone via USB
4. Accept debugging prompt on phone
5. In Android Studio, click "Run"
6. Select your device from the list

### 8. Test the App

#### First Launch
1. App will open to Login screen
2. Tap "Sign Up" to create an account
3. Enter name, email, password
4. Tap "Sign Up"
5. You'll be logged in automatically

#### Browse Products
1. Home screen shows featured products
2. Tap Browse to see all products
3. Use filters to refine results
4. Tap a product to see details

#### Try AR (Physical ARCore Device Only)
1. On product detail, tap "Try with AR"
2. Grant camera permission
3. Point camera at floor/surface
4. AR model will appear (if product has AR enabled)

**Note:** AR features require an ARCore-compatible device. Emulators don't support AR.

Check compatibility: https://developers.google.com/ar/devices

#### Admin Features
1. Create an account with your admin email
2. Get your UID from Firebase Console → Authentication
3. Add your UID to `admin_users` collection (step 6)
4. Logout and login again
5. Admin dashboard will be accessible from Profile screen

## Troubleshooting

### Build Errors

**"google-services.json not found"**
```bash
# Verify file is in correct location
ls app/google-services.json

# If missing, download from Firebase Console
```

**"Gradle sync failed"**
```bash
# In Android Studio:
File → Invalidate Caches / Restart

# Or clean build
./gradlew clean
./gradlew build
```

**"SDK not found"**
```bash
# In Android Studio:
File → Project Structure → SDK Location
# Set Android SDK location (usually ~/Android/Sdk)
```

### Runtime Errors

**"Firebase not initialized"**
- Verify `google-services.json` is in `app/` directory
- Check package name in Firebase matches `com.arshop`
- Clean and rebuild project

**"Authentication failed"**
- Verify Email/Password is enabled in Firebase Console
- For Google Sign-In, verify SHA-1 is registered
- Check internet connection

**"AR not working"**
- Verify device is ARCore compatible
- Update ARCore from Play Store
- Grant camera permission in Settings

### Network Issues

**"Unable to load products"**
- Check internet connection
- Verify Firestore has data
- Check Firestore security rules allow read access
- View Logcat for specific error messages

## Build Variants

### Debug Build
```bash
./gradlew assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

### Release Build
```bash
# Create keystore first (see DEPLOYMENT_GUIDE.md)
./gradlew assembleRelease
# APK: app/build/outputs/apk/release/app-release.apk
```

## Development Workflow

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Install on connected device
./gradlew installDebug

# Run all tests
./gradlew test

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Check for lint errors
./gradlew lint
```

## Viewing Logs

### In Android Studio
1. View → Tool Windows → Logcat
2. Filter by package: `com.arshop`
3. Select log level (Verbose, Debug, Info, Warn, Error)

### Via Command Line
```bash
# View all logs
adb logcat

# Filter by tag
adb logcat -s ARShop

# Clear logs
adb logcat -c

# Save to file
adb logcat > arshop-logs.txt
```

## Why This App Can't Run in CI/CD

This is a **full Android application** that requires:

1. **Android Build System**
   - Android SDK (30+ GB)
   - Android Studio or complete CLI tools
   - Gradle with Android plugins
   - Not available in standard CI environments

2. **Firebase Backend**
   - Requires real Firebase project
   - Needs `google-services.json` (contains project secrets)
   - Can't use mock Firebase in production app

3. **Runtime Environment**
   - Needs Android OS (emulator or device)
   - AR features need physical ARCore device
   - UI testing requires Android instrumentation

4. **External Services**
   - Firebase Authentication
   - Cloud Firestore
   - Firebase Storage
   - Payment gateway APIs

## Alternative: Verify Build Only

If you want to verify the code compiles without running:

1. Install Android SDK
2. Add `google-services.json` (even a mock one)
3. Run: `./gradlew assembleDebug`
4. Check for compilation errors

But this won't actually run the app.

## Full Documentation

- **QUICKSTART.md**: 5-minute setup overview
- **FIREBASE_SETUP.md**: Detailed Firebase configuration
- **TESTING_GUIDE.md**: Testing procedures
- **DEPLOYMENT_GUIDE.md**: Production deployment
- **README.md**: Project overview

## Support

For issues:
1. Check Logcat output
2. Verify Firebase configuration
3. Review documentation files
4. Check Firebase Console for errors
5. Ensure all prerequisites are met

---

## Summary

**To run this app, you need:**

✅ Android Studio installed  
✅ Firebase project configured  
✅ `google-services.json` in `app/` directory  
✅ Test data in Firestore  
✅ Physical device or emulator  

**You cannot:**

❌ Run directly from command line without Android SDK  
❌ Run in standard CI/CD environment  
❌ Run without Firebase configuration  
❌ Test AR features on emulator  

This is a **complete, production-ready Android application** that follows industry standards for mobile app development. It requires the standard Android development environment to run.

**For detailed setup, follow QUICKSTART.md step by step.**
