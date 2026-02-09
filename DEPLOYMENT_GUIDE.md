# ARShop Deployment Guide

This guide provides step-by-step instructions for deploying the ARShop Android e-commerce application to production.

## Table of Contents
1. [Pre-Deployment Checklist](#pre-deployment-checklist)
2. [Firebase Production Setup](#firebase-production-setup)
3. [Release Build Configuration](#release-build-configuration)
4. [Payment Gateway Production Keys](#payment-gateway-production-keys)
5. [Code Signing](#code-signing)
6. [Building Release APK/AAB](#building-release-apkaab)
7. [Play Store Submission](#play-store-submission)
8. [Post-Deployment](#post-deployment)
9. [Monitoring & Analytics](#monitoring--analytics)

## Pre-Deployment Checklist

### Code Quality
- [ ] All unit tests passing
- [ ] All instrumented tests passing
- [ ] Lint checks passing (no errors)
- [ ] Code review completed
- [ ] Security audit completed
- [ ] Performance testing completed
- [ ] No hardcoded credentials
- [ ] ProGuard rules verified

### Features
- [ ] All core features working
- [ ] Authentication flow tested
- [ ] Product browsing tested
- [ ] Cart and checkout tested
- [ ] AR features tested on real devices
- [ ] Payment integration tested
- [ ] Admin panel tested
- [ ] Offline mode tested

### Resources
- [ ] All strings externalized
- [ ] Images optimized
- [ ] 3D models optimized (<5MB each)
- [ ] App icons for all densities
- [ ] Privacy policy created
- [ ] Terms of service created

### Configuration
- [ ] Production Firebase project created
- [ ] Production API keys obtained
- [ ] Release keystore created
- [ ] Package name finalized
- [ ] Version code and name set

## Firebase Production Setup

### 1. Create Production Project

1. **Firebase Console** (https://console.firebase.google.com/)
   - Create new project: "ARShop-Production"
   - Enable Google Analytics
   - Select region

2. **Add Android App**
   - Package name: `com.arshop` (or your production package)
   - App nickname: "ARShop Android"
   - Download `google-services.json`
   - Place in `app/` directory

3. **Register SHA-1 Certificate**
   ```bash
   # Get SHA-1 from release keystore
   keytool -list -v -keystore release.keystore -alias arshop
   ```
   - Copy SHA-1 fingerprint
   - Add to Firebase Console → Project Settings → Your apps

### 2. Enable Firebase Services

#### Authentication
1. Enable Email/Password authentication
2. Enable Google Sign-In
   - Configure OAuth consent screen
   - Add authorized domains
3. Set up password policy (min 8 characters)

#### Firestore Database
1. Create database in production mode
2. Select region (same as Storage)
3. Set up Security Rules:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Helper functions
    function isSignedIn() {
      return request.auth != null;
    }
    
    function isOwner(userId) {
      return isSignedIn() && request.auth.uid == userId;
    }
    
    function isAdmin() {
      return isSignedIn() && 
        exists(/databases/$(database)/documents/admin_users/$(request.auth.uid));
    }
    
    // Products - Public read, admin write
    match /products/{productId} {
      allow read: if true;
      allow write: if isAdmin();
    }
    
    // Users - Owner read/write
    match /users/{userId} {
      allow read, write: if isOwner(userId);
    }
    
    // Cart - Owner read/write
    match /cart/{userId}/items/{itemId} {
      allow read, write: if isOwner(userId);
    }
    
    // Orders - Owner read, admin write status
    match /orders/{orderId} {
      allow read: if isSignedIn() && 
        (resource.data.userId == request.auth.uid || isAdmin());
      allow create: if isSignedIn();
      allow update: if isAdmin();
    }
    
    // Admin users - Admin read only
    match /admin_users/{userId} {
      allow read: if isAdmin();
    }
  }
}
```

#### Firebase Storage
1. Create default bucket
2. Set up Security Rules:

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    
    // Helper functions
    function isSignedIn() {
      return request.auth != null;
    }
    
    function isAdmin() {
      return firestore.get(/databases/(default)/documents/admin_users/$(request.auth.uid)).size() > 0;
    }
    
    // Product images and models - Public read, admin write
    match /products/{productId}/{allPaths=**} {
      allow read: if true;
      allow write: if isAdmin();
    }
    
    // User uploads - Owner read/write
    match /users/{userId}/{allPaths=**} {
      allow read, write: if request.auth.uid == userId;
    }
  }
}
```

### 3. Cloud Functions (Optional)

#### Install Firebase CLI
```bash
npm install -g firebase-tools
firebase login
```

#### Initialize Functions
```bash
firebase init functions
# Select TypeScript
# Install dependencies
```

#### Deploy Payment Processing Functions
```typescript
// functions/src/index.ts
import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import Stripe from 'stripe';

admin.initializeApp();

const stripe = new Stripe(functions.config().stripe.secret_key, {
  apiVersion: '2023-10-16',
});

export const processStripePayment = functions.https.onCall(async (data, context) => {
  // Verify authentication
  if (!context.auth) {
    throw new functions.https.HttpsError('unauthenticated', 'User must be authenticated');
  }

  const { amount, currency, paymentMethodId } = data;

  try {
    const paymentIntent = await stripe.paymentIntents.create({
      amount: amount * 100, // Convert to cents
      currency: currency || 'usd',
      payment_method: paymentMethodId,
      confirm: true,
    });

    return { success: true, paymentIntentId: paymentIntent.id };
  } catch (error) {
    throw new functions.https.HttpsError('internal', error.message);
  }
});

export const sendOrderConfirmation = functions.firestore
  .document('orders/{orderId}')
  .onCreate(async (snapshot, context) => {
    const order = snapshot.data();
    // Send confirmation email (implement with SendGrid, etc.)
    console.log('Order created:', context.params.orderId);
  });
```

#### Deploy
```bash
firebase use production
firebase deploy --only functions
```

### 4. Firebase Analytics & Crashlytics

Enable in Firebase Console:
- Google Analytics
- Firebase Crashlytics
- Performance Monitoring

## Release Build Configuration

### 1. Update build.gradle.kts

```kotlin
android {
    defaultConfig {
        versionCode = 1
        versionName = "1.0.0"
    }

    signingConfigs {
        create("release") {
            storeFile = file("../release.keystore")
            storePassword = System.getenv("KEYSTORE_PASSWORD")
            keyAlias = "arshop"
            keyPassword = System.getenv("KEY_PASSWORD")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }
    }
}
```

### 2. Verify ProGuard Rules

Ensure `app/proguard-rules.pro` includes all necessary rules (already configured).

### 3. Update local.properties

```properties
# Production API Keys
STRIPE_PUBLISHABLE_KEY=pk_live_...
RAZORPAY_KEY_ID=rzp_live_...
```

## Payment Gateway Production Keys

### Stripe

1. **Stripe Dashboard** (https://dashboard.stripe.com/)
   - Switch to Live mode
   - Developers → API keys
   - Copy Publishable key
   - Copy Secret key (for Cloud Functions)

2. **Configure Webhook** (for order updates)
   - Webhooks → Add endpoint
   - URL: Your Cloud Function URL
   - Events: payment_intent.succeeded, payment_intent.failed

3. **Add to Configuration**
   ```properties
   # local.properties
   STRIPE_PUBLISHABLE_KEY=pk_live_...
   ```
   ```bash
   # Cloud Functions
   firebase functions:config:set stripe.secret_key="sk_live_..."
   ```

### PayPal

1. **PayPal Developer** (https://developer.paypal.com/)
   - Switch to Live
   - Apps & Credentials → Create App
   - Copy Client ID and Secret

2. **Configure in App**
   ```kotlin
   // In PaymentScreen or ViewModel
   val paypalConfig = PayPalConfig(
       clientId = "YOUR_LIVE_CLIENT_ID",
       environment = PayPalEnvironment.LIVE
   )
   ```

### Razorpay

1. **Razorpay Dashboard** (https://dashboard.razorpay.com/)
   - Settings → API Keys
   - Generate Live Keys
   - Copy Key ID and Secret

2. **Add to Configuration**
   ```properties
   # local.properties
   RAZORPAY_KEY_ID=rzp_live_...
   ```

### Google Pay

1. **Google Pay Business Console** (https://pay.google.com/business/console/)
   - Create merchant account
   - Get Merchant ID

2. **Configure in App**
   - Use production environment
   - Add merchant ID to PaymentScreen

## Code Signing

### 1. Create Release Keystore

```bash
keytool -genkey -v -keystore release.keystore -alias arshop \
  -keyalg RSA -keysize 2048 -validity 10000
```

**Important:**
- Use strong password
- Store keystore securely (never commit to git)
- Backup keystore in secure location
- Document passwords in secure vault

### 2. Set Environment Variables

```bash
export KEYSTORE_PASSWORD="your_keystore_password"
export KEY_PASSWORD="your_key_password"
```

Or use CI/CD secrets in GitHub Actions, etc.

### 3. Verify Keystore

```bash
keytool -list -v -keystore release.keystore -alias arshop
```

## Building Release APK/AAB

### 1. Clean Build

```bash
./gradlew clean
```

### 2. Build App Bundle (Recommended)

```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

### 3. Build APK (Alternative)

```bash
./gradlew assembleRelease
```

Output: `app/build/outputs/apk/release/app-release.apk`

### 4. Test Release Build

```bash
# Install on device
adb install app/build/outputs/apk/release/app-release.apk

# Test thoroughly
# - Authentication
# - Product browsing
# - Cart and checkout
# - Payments (test mode first)
# - AR features
```

### 5. Verify APK

```bash
# Check signing
jarsigner -verify -verbose -certs app/build/outputs/apk/release/app-release.apk

# Analyze APK
./gradlew analyzeReleaseBundle
```

## Play Store Submission

### 1. Google Play Console Setup

1. **Create App** (https://play.google.com/console/)
   - App name: ARShop
   - Default language: English
   - App/Game: App
   - Free/Paid: Free

2. **Store Listing**
   - App name: ARShop - AR Shopping
   - Short description: Shop with AR virtual try-on
   - Full description: (See template below)
   - App icon: 512x512 PNG
   - Feature graphic: 1024x500 PNG
   - Screenshots: 
     - Phone: 2-8 screenshots
     - Tablet: 2-8 screenshots (optional)
   - Video: (optional)

3. **Content Rating**
   - Complete questionnaire
   - Select appropriate rating

4. **App Content**
   - Privacy policy URL: https://yoursite.com/privacy
   - Ads: No (unless using ads)
   - Data safety: Complete form
   - Target audience: 13+

5. **Pricing & Distribution**
   - Free
   - Countries: Select all or specific
   - Primarily for children: No

### 2. Upload App Bundle

1. **Production Track**
   - Create new release
   - Upload AAB file
   - Release name: v1.0.0
   - Release notes: Initial release

2. **Review Changes**
   - Verify all sections complete
   - Submit for review

### 3. Store Listing Description Template

```
ARShop - Shop Fashion with AR Virtual Try-On

Transform your shopping experience with augmented reality! Try on clothes and shoes virtually before you buy.

FEATURES:
• Virtual Try-On: Use AR to see how products look on you
• Browse Catalog: Thousands of fashion items
• Easy Checkout: Secure payment with multiple options
• Order Tracking: Track your orders in real-time
• Personalized: Save favorites and addresses

SUPPORTED PRODUCTS:
• Clothing: T-shirts, Dresses, Pants, Jackets
• Footwear: Sneakers, Boots, Sandals, Heels

AR TECHNOLOGY:
Powered by ARCore for realistic virtual try-ons

SECURE PAYMENTS:
• Stripe
• PayPal
• Razorpay
• Google Pay

Download now and shop with confidence!
```

### 4. Review Process

- Initial review: 3-7 days
- Updates: 1-3 days
- Address any issues raised by Google
- Monitor Google Play Console for messages

## Post-Deployment

### 1. Monitor Crashes

**Firebase Crashlytics:**
- Review crash reports daily
- Fix critical crashes immediately
- Release updates for stability

### 2. Monitor Analytics

**Firebase Analytics:**
- Track user engagement
- Monitor conversion rates
- Analyze user flows
- Identify drop-off points

### 3. Monitor Performance

**Firebase Performance:**
- App startup time
- Network requests
- Screen rendering
- Custom traces

### 4. User Feedback

**Play Store Reviews:**
- Respond to reviews
- Address common issues
- Implement feature requests

### 5. Updates

**Release Schedule:**
- Bug fixes: As needed
- Minor updates: Monthly
- Major updates: Quarterly

**Version Numbering:**
- Major.Minor.Patch (e.g., 1.0.0)
- Increment patch for bug fixes
- Increment minor for new features
- Increment major for major changes

## Monitoring & Analytics

### Key Metrics to Track

1. **User Acquisition**
   - Downloads
   - Install attribution
   - User demographics

2. **Engagement**
   - Daily/Monthly Active Users
   - Session duration
   - Session frequency
   - Screen views

3. **Conversion**
   - Add to cart rate
   - Checkout completion rate
   - Purchase conversion rate
   - Average order value

4. **AR Usage**
   - AR session starts
   - AR session duration
   - Products tried with AR
   - AR to purchase conversion

5. **Performance**
   - Crash-free rate (target: >99.5%)
   - App startup time
   - API response times
   - Screen load times

6. **Revenue**
   - Daily/Monthly revenue
   - Revenue per user
   - Product performance
   - Category performance

### Custom Events

Log important events:
```kotlin
// Product viewed
firebaseAnalytics.logEvent("product_view") {
    param("product_id", productId)
    param("category", category)
}

// AR session started
firebaseAnalytics.logEvent("ar_session_start") {
    param("product_id", productId)
    param("product_type", productType)
}

// Purchase completed
firebaseAnalytics.logEvent(FirebaseAnalytics.Event.PURCHASE) {
    param(FirebaseAnalytics.Param.CURRENCY, "USD")
    param(FirebaseAnalytics.Param.VALUE, totalAmount)
}
```

## Rollback Plan

If critical issues are discovered:

1. **Immediate:**
   - Disable new user signups (if needed)
   - Add banner about known issues

2. **Quick Fix:**
   - Create hotfix branch
   - Fix critical issue
   - Test thoroughly
   - Build and upload new AAB
   - Submit as emergency update

3. **Rollback:**
   - In Play Console: Halt rollout
   - Release previous stable version
   - Investigate and fix issue
   - Re-release when ready

## Security Checklist

- [ ] No API keys in code (use BuildConfig)
- [ ] HTTPS only (network security config)
- [ ] ProGuard enabled for release
- [ ] Certificate pinning (optional)
- [ ] Firebase security rules tested
- [ ] Input validation everywhere
- [ ] SQL injection prevention (Room handles)
- [ ] XSS prevention (no WebView user input)
- [ ] Secure storage for tokens
- [ ] Permission requests justified

## Legal Compliance

### Required Documents
- [ ] Privacy Policy (GDPR, CCPA compliant)
- [ ] Terms of Service
- [ ] Cookie Policy
- [ ] Refund Policy
- [ ] Shipping Policy

### Data Protection
- [ ] User data encrypted in transit
- [ ] User data encrypted at rest (if needed)
- [ ] Right to delete account
- [ ] Data export capability
- [ ] GDPR consent collection (EU users)

## Support Infrastructure

### Customer Support
- Support email: support@arshop.com
- FAQ page
- In-app help
- Chat support (optional)

### Bug Reporting
- Firebase Crashlytics (automatic)
- In-app feedback form
- GitHub issues (for developers)

---

**Congratulations!** Your app is now ready for production deployment. Monitor closely and iterate based on user feedback.
