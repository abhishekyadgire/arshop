# ARShop - Quick Start Guide

Get your ARShop Android app running in minutes!

## Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 34
- Git

## Step 1: Clone the Repository

```bash
git clone <your-repo-url>
cd arshop
```

## Step 2: Firebase Setup (Required)

### Create Firebase Project

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Create a new project named "ARShop"
3. Add an Android app:
   - Package name: `com.arshop`
   - App nickname: ARShop Android
   - Download `google-services.json`

### Configure Firebase Services

**Authentication:**
- Enable Email/Password authentication
- Enable Google Sign-In

**Firestore Database:**
- Create database in production mode
- See `FIREBASE_SETUP.md` for security rules

**Firebase Storage:**
- Enable default storage bucket
- See `FIREBASE_SETUP.md` for security rules

### Add google-services.json

```bash
# Place the downloaded file in the app directory
cp ~/Downloads/google-services.json app/
```

## Step 3: Configure API Keys (Optional for Basic Testing)

### For Payment Testing

```bash
# Copy the template
cp local.properties.template local.properties

# Edit local.properties and add test keys
nano local.properties
```

Add your test keys:
```properties
STRIPE_PUBLISHABLE_KEY=pk_test_your_key_here
RAZORPAY_KEY_ID=rzp_test_your_key_here
```

> **Note:** You can skip this step initially and add later when testing payments.

## Step 4: Build the App

```bash
# Sync Gradle
./gradlew clean build

# Or in Android Studio:
# File → Sync Project with Gradle Files
```

## Step 5: Add Test Data

### Create Admin User

In Firebase Console → Firestore:

1. Create collection: `admin_users`
2. Add document with ID: `<your-user-id>`
3. Fields:
   ```
   email: "admin@arshop.com"
   role: "admin"
   permissions: ["manage_products", "manage_orders"]
   ```

### Add Sample Products

Create collection: `products`

Sample product:
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
  "imageUrls": ["<image-url>"],
  "isArEnabled": true,
  "modelUrl": "",
  "stockQuantity": 100,
  "rating": 4.5,
  "totalReviews": 120,
  "createdAt": <current-timestamp>,
  "updatedAt": <current-timestamp>
}
```

## Step 6: Run the App

### On Emulator

```bash
# Create emulator (if needed)
avdmanager create avd -n Pixel_7 -k "system-images;android-34;google_apis;x86_64"

# Start emulator
emulator -avd Pixel_7

# Install app
./gradlew installDebug
```

### On Physical Device

1. Enable Developer Options
2. Enable USB Debugging
3. Connect device
4. Run: `./gradlew installDebug`

## Step 7: Test the App

### Authentication Flow
1. Open app → Login screen
2. Tap "Sign Up"
3. Create account
4. Login with credentials

### Browse Products
1. Home screen shows featured products
2. Tap categories to filter
3. Tap product to view details

### Try AR (ARCore device required)
1. On product detail, tap "Try with AR"
2. Grant camera permission
3. Point at floor/wall
4. Model appears in AR view

### Test Admin Panel
1. Login with admin user
2. Access admin dashboard
3. Add/edit products
4. View orders

## Common Issues

### Build Errors

**Problem:** google-services.json not found
```bash
# Make sure file is in app/ directory
ls app/google-services.json
```

**Problem:** Gradle sync failed
```bash
./gradlew --refresh-dependencies
# Or in Android Studio: File → Invalidate Caches / Restart
```

### Runtime Errors

**Problem:** Firebase not initialized
- Verify google-services.json is correct
- Check package name matches Firebase config

**Problem:** AR not working
- Verify device is ARCore compatible
- Update ARCore from Play Store
- Grant camera permission

## Next Steps

1. **Add More Products**: Use admin panel or Firestore Console
2. **Test Payments**: Configure payment gateway test keys
3. **Test AR**: Add 3D models to Firebase Storage
4. **Customize**: Modify colors, strings, images
5. **Deploy**: Follow DEPLOYMENT_GUIDE.md for production

## Development Workflow

```bash
# Daily development
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Check logs
adb logcat | grep -i arshop

# Run tests
./gradlew test
./gradlew connectedAndroidTest
```

## Resources

- **FIREBASE_SETUP.md** - Detailed Firebase configuration
- **TESTING_GUIDE.md** - Testing procedures
- **DEPLOYMENT_GUIDE.md** - Production deployment
- **README.md** - Full documentation

## Support

For issues:
1. Check existing documentation
2. Review Firebase Console logs
3. Check Android Studio Logcat
4. Verify all prerequisites met

---

**You're all set!** Start building your AR shopping experience. 🚀
