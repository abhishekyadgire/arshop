# ARShop Resources & Configuration Guide

This guide covers all resource files and configuration templates for the ARShop Android application.

## 📋 Table of Contents

1. [String Resources](#string-resources)
2. [Drawable Resources](#drawable-resources)
3. [Asset Files](#asset-files)
4. [Configuration Files](#configuration-files)
5. [Security Configuration](#security-configuration)
6. [Setup Instructions](#setup-instructions)

---

## 📝 String Resources

**Location:** `app/src/main/res/values/strings.xml`

**Total Strings:** 346 comprehensive string resources

### Categories Covered

#### Authentication (20+ strings)
- Login/signup flows
- Password management
- Social sign-in
- Terms and privacy

#### Navigation (10 strings)
- Bottom navigation
- Screen titles
- Menu items

#### Products (40+ strings)
- Product details
- Categories
- Specifications
- Reviews and ratings
- Stock status

#### Shopping Cart (15+ strings)
- Cart management
- Item quantities
- Pricing components
- Coupon codes

#### Checkout (30+ strings)
- Address fields
- Payment methods
- Order summary
- Confirmation

#### AR Features (25+ strings)
- AR initialization
- Camera guidance
- Interaction instructions
- Error handling
- AR controls

#### Orders (20+ strings)
- Order status
- Order tracking
- Order actions
- Order history

#### Admin Panel (20+ strings)
- Product management
- Order management
- Analytics
- Admin actions

#### Error Messages (30+ strings)
- Validation errors
- Network errors
- Authentication errors
- AR errors
- Payment errors

#### Success Messages (15+ strings)
- Action confirmations
- Operation success

#### Dialogs & Confirmations (10+ strings)
- Confirmation dialogs
- Alert messages

#### Empty States (10+ strings)
- Empty cart
- No orders
- No products
- No results

#### Accessibility (20+ strings)
- Content descriptions
- Screen reader support

#### Common Actions (30+ strings)
- Buttons and actions
- Sort options
- Filter options

### Usage Example

```kotlin
// In Activity or Fragment
binding.addToCartButton.text = getString(R.string.add_to_cart)

// With arguments
val itemsLeft = getString(R.string.items_left, 5) // "5 items left"

// In ViewModel or Repository
context.getString(R.string.error_network)
```

---

## 🎨 Drawable Resources

**Location:** `app/src/main/res/drawable/`

### Vector Drawables (XML)

#### 1. ic_ar_badge.xml
- **Purpose:** AR enabled indicator for product cards
- **Size:** 24dp × 24dp
- **Color:** Purple (#9C27B0)
- **Usage:** Show on products with AR support

```xml
<ImageView
    android:src="@drawable/ic_ar_badge"
    android:contentDescription="@string/content_description_ar_badge" />
```

#### 2. ic_empty_cart.xml
- **Purpose:** Empty cart state illustration
- **Size:** 120dp × 120dp
- **Usage:** Display when cart is empty

#### 3. ic_empty_orders.xml
- **Purpose:** Empty orders state illustration
- **Size:** 120dp × 120dp
- **Usage:** Display when no orders exist

#### 4. ic_empty_products.xml
- **Purpose:** Empty products/search state
- **Size:** 120dp × 120dp
- **Usage:** Display when no products found

#### 5. ic_admin.xml
- **Purpose:** Admin panel settings icon
- **Size:** 24dp × 24dp
- **Usage:** Admin menu, settings

#### 6. ic_analytics.xml
- **Purpose:** Analytics/charts icon
- **Size:** 24dp × 24dp
- **Usage:** Analytics screen, dashboard

#### 7. placeholder_product.xml
- **Purpose:** Product image placeholder
- **Size:** 200dp × 200dp
- **Usage:** Loading state, missing images

```xml
<ImageView
    android:src="@drawable/placeholder_product"
    android:scaleType="centerCrop" />
```

### Customization

All drawables are vector XML files and can be easily customized:
- Change colors in `android:fillColor`
- Resize by modifying `android:width` and `android:height`
- Modify paths for different shapes

---

## 📦 Asset Files

**Location:** `app/src/main/assets/`

### 3D Models Directory

**Path:** `app/src/main/assets/models/`

Contains 3D model files for AR product visualization.

#### Supported Formats
- **GLB** (GL Transmission Format Binary) - ✅ Recommended
- **GLTF** (GL Transmission Format)

#### Naming Convention
```
product_[product_id].glb
```

Examples:
- `product_12345.glb`
- `product_shoe_nike_001.glb`
- `product_furniture_chair_01.glb`

#### Requirements
- File size: Under 10MB
- Polygon count: 5,000 - 50,000
- Texture resolution: 2048×2048 or lower
- PBR materials
- Proper UV unwrapping

#### Documentation
See `app/src/main/assets/models/README.md` for:
- Detailed specifications
- Optimization guidelines
- Testing procedures
- Orientation standards
- Tool recommendations

---

## ⚙️ Configuration Files

### 1. local.properties.template

**Location:** Root directory

Template for API keys and secrets.

**Setup:**
```bash
cp local.properties.template local.properties
```

Then edit `local.properties` with your actual keys:

```properties
# Stripe
STRIPE_PUBLISHABLE_KEY=pk_test_your_actual_key_here

# Razorpay
RAZORPAY_KEY_ID=rzp_test_your_actual_key_here
```

**Keys Required:**
- Stripe Publishable Key (from [Stripe Dashboard](https://dashboard.stripe.com/apikeys))
- Razorpay Key ID (from [Razorpay Dashboard](https://dashboard.razorpay.com/app/keys))

**Security:**
- ✅ File is in `.gitignore`
- ❌ Never commit with real keys
- ✅ Use test keys for development

### 2. google-services.json.template

**Location:** Root directory

Template for Firebase configuration.

**Setup:**

1. Go to [Firebase Console](https://console.firebase.google.com/)
2. Select your project or create new
3. Go to Project Settings
4. Scroll to "Your apps" section
5. Click "Add app" → Select Android
6. Enter package name: `com.arshop`
7. Download `google-services.json`
8. Place in `app/` directory (NOT root)

**Firebase Services to Enable:**
- Authentication (Email/Password, Google Sign-In)
- Firestore Database
- Cloud Storage
- Cloud Messaging (optional)
- Analytics (optional)

**Security:**
- ✅ File is in `.gitignore`
- ❌ Never commit to public repos
- ✅ Use different projects for dev/prod

### 3. ProGuard Rules

**Location:** `app/proguard-rules.pro`

Comprehensive ProGuard/R8 rules for code minification and obfuscation.

**Covers:**
- Firebase (Auth, Firestore, Storage, Analytics, Messaging)
- ARCore and Sceneview
- Payment SDKs (Stripe, PayPal, Razorpay)
- Networking (Retrofit, OkHttp)
- Serialization (Gson, Kotlin Serialization)
- Kotlin Coroutines
- Dagger Hilt
- Room Database
- AndroidX libraries
- Coil image loading
- DataStore

**Build Types:**

```gradle
buildTypes {
    release {
        minifyEnabled true
        shrinkResources true
        proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
    }
}
```

---

## 🔒 Security Configuration

### Network Security Config

**Location:** `app/src/main/res/xml/network_security_config.xml`

**Features:**
- ✅ Enforces HTTPS for all network traffic
- ✅ Allows localhost for development/testing
- ✅ Blocks cleartext traffic in production
- ⚠️ Supports emulator localhost (10.0.2.2, 10.0.3.2)

**Referenced in AndroidManifest:**
```xml
<application
    android:networkSecurityConfig="@xml/network_security_config"
    ...>
```

### .gitignore

**Location:** Root directory

**Excludes:**
- `google-services.json` (Firebase config)
- `local.properties` (API keys)
- `*.keystore`, `*.jks` (Signing keys)
- Build artifacts (`*.apk`, `*.aab`)
- IDE files (`.idea/`, `*.iml`)
- NDK files
- Large model files (optional)

**Never Commit:**
- API keys
- Firebase config
- Signing keystores
- Local properties
- Sensitive credentials

---

## 🚀 Setup Instructions

### First-Time Setup

#### 1. Clone Repository
```bash
git clone <repository-url>
cd arshop
```

#### 2. Configure API Keys

```bash
# Copy template
cp local.properties.template local.properties

# Edit with your keys
nano local.properties
```

Add your keys:
```properties
STRIPE_PUBLISHABLE_KEY=pk_test_51xxxxx
RAZORPAY_KEY_ID=rzp_test_xxxxx
```

#### 3. Configure Firebase

1. Create Firebase project
2. Download `google-services.json`
3. Place in `app/` directory:
```bash
mv ~/Downloads/google-services.json app/
```

#### 4. Enable Firebase Services

In Firebase Console:
- Authentication → Enable Email/Password
- Authentication → Enable Google Sign-In
- Firestore Database → Create database
- Storage → Get started
- (Optional) Analytics → Enable

#### 5. Configure OAuth

For Google Sign-In:
```bash
# Get SHA-1 fingerprint
./gradlew signingReport

# Add to Firebase Console:
# Project Settings > Your apps > Add fingerprint
```

#### 6. Build Project

```bash
./gradlew clean build
```

### Adding 3D Models

```bash
# Place GLB files in assets
cp your_model.glb app/src/main/assets/models/product_001.glb

# Reference in Firestore product document
{
  "id": "001",
  "name": "Product Name",
  "modelUrl": "models/product_001.glb",
  "arEnabled": true
}
```

### Verification

#### Check Configuration
```bash
# Verify files exist
ls -la app/google-services.json
ls -la local.properties

# Verify ignored
git status  # Should not show google-services.json or local.properties
```

#### Test Build
```bash
# Debug build
./gradlew assembleDebug

# Release build (with ProGuard)
./gradlew assembleRelease
```

---

## 📚 Additional Resources

### Firebase
- [Firebase Console](https://console.firebase.google.com/)
- [Firebase Android Setup](https://firebase.google.com/docs/android/setup)
- [Firestore Security Rules](https://firebase.google.com/docs/firestore/security/get-started)

### Payment Gateways
- [Stripe Dashboard](https://dashboard.stripe.com/)
- [Stripe Android SDK](https://stripe.com/docs/mobile/android)
- [Razorpay Dashboard](https://dashboard.razorpay.com/)
- [Razorpay Android SDK](https://razorpay.com/docs/payment-gateway/android-integration/)

### AR Development
- [ARCore Overview](https://developers.google.com/ar)
- [Sceneview Library](https://github.com/SceneView/sceneview-android)
- [glTF Format](https://www.khronos.org/gltf/)

### Android Development
- [Android Developers](https://developer.android.com/)
- [Material Design](https://material.io/develop/android)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)

---

## 🔧 Troubleshooting

### Common Issues

#### Firebase Initialization Failed
- ✅ Check `google-services.json` is in `app/` directory
- ✅ Verify package name matches (`com.arshop`)
- ✅ Clean and rebuild project

#### Google Sign-In Failed
- ✅ Add SHA-1 fingerprint to Firebase Console
- ✅ Enable Google Sign-In in Firebase Authentication
- ✅ Configure OAuth consent screen

#### Payment SDK Issues
- ✅ Verify API keys in `local.properties`
- ✅ Check internet permission in manifest
- ✅ Use test keys for development

#### AR Not Working
- ✅ Check ARCore is installed on device
- ✅ Verify camera permission granted
- ✅ Test on ARCore supported device
- ✅ Check 3D model format and size

#### ProGuard Issues
- ✅ Check `proguard-rules.pro` has all needed rules
- ✅ Test release build thoroughly
- ✅ Check logcat for missing class errors
- ✅ Add `-keep` rules for affected classes

---

## 📝 Maintenance

### Updating Dependencies

When updating libraries, verify ProGuard rules:

```bash
# After dependency update
./gradlew assembleRelease

# Check for ProGuard warnings
# Add new -keep rules if needed
```

### Rotating API Keys

```bash
# Update in Stripe/Razorpay dashboard
# Update local.properties
# Test payment flows
```

### Updating Firebase Config

```bash
# Download new google-services.json from Firebase Console
# Replace app/google-services.json
# Clean and rebuild
./gradlew clean build
```

---

## ✅ Checklist for Production

- [ ] All API keys configured (production keys)
- [ ] Firebase production project configured
- [ ] ProGuard rules tested
- [ ] Network security config reviewed
- [ ] No debug logging in release builds
- [ ] Signing keystore configured
- [ ] App bundle built and tested
- [ ] Payment flows tested with live keys
- [ ] AR features tested on multiple devices
- [ ] All permissions properly requested
- [ ] Privacy policy and terms configured
- [ ] Analytics tracking implemented
- [ ] Crash reporting configured
- [ ] Performance monitoring enabled

---

**Version:** 1.0  
**Last Updated:** 2024  
**Maintainer:** ARShop Development Team
