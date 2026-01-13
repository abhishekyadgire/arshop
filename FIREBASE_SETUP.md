# Firebase Setup Instructions for ARShop

This document explains how to configure Firebase for the ARShop application.

## Prerequisites
- Android Studio Arctic Fox or later
- JDK 17
- Android SDK API 24+

## Step 1: Create Firebase Project

1. Go to the [Firebase Console](https://console.firebase.google.com/)
2. Click "Add Project"
3. Enter project name: **ARShop**
4. Enable Google Analytics (recommended)
5. Complete the project creation

## Step 2: Add Android App to Firebase

1. In Firebase Console, click the Android icon to add an Android app
2. Enter the following details:
   - **Package name**: `com.arshop`
   - **App nickname**: ARShop (optional)
   - **Debug signing certificate SHA-1**: (optional, but required for Google Sign-In)

### Getting SHA-1 Certificate

Run this command in your project directory:
```bash
cd android
./gradlew signingReport
```

Copy the SHA-1 from the debug keystore and paste it in Firebase.

## Step 3: Download google-services.json

1. Click "Download google-services.json"
2. Place the file in: `arshop/app/google-services.json`
3. **Important**: This file contains sensitive keys. Never commit it to version control (already in .gitignore)

## Step 4: Enable Firebase Services

### 4.1 Firebase Authentication
1. Go to **Authentication** > **Sign-in method**
2. Enable **Email/Password**
3. Enable **Google** provider:
   - Add your app's package name
   - Download and configure OAuth client ID

### 4.2 Cloud Firestore
1. Go to **Firestore Database**
2. Click "Create database"
3. Choose **Start in test mode** (for development)
4. Select your region (choose closest to your users)

#### Required Firestore Indexes

Create composite indexes for:
- **products** collection:
  - category (Ascending) + rating (Descending)
  - gender (Ascending) + price (Ascending)
  - isArEnabled (Ascending) + createdAt (Descending)

- **cart_items** collection:
  - userId (Ascending) + addedAt (Descending)

- **orders** collection:
  - userId (Ascending) + createdAt (Descending)
  - orderStatus (Ascending) + createdAt (Descending)

#### Firestore Security Rules

Replace the default rules with:
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
    }

    match /products/{productId} {
      allow read: if true;
      allow write: if request.auth != null &&
                     exists(/databases/$(database)/documents/admin_users/$(request.auth.token.email));
    }

    match /cart_items/{itemId} {
      allow read, write: if request.auth != null &&
                           resource.data.userId == request.auth.uid;
    }

    match /orders/{orderId} {
      allow read: if request.auth != null &&
                    resource.data.userId == request.auth.uid;
      allow create: if request.auth != null;
      allow update: if request.auth != null &&
                      exists(/databases/$(database)/documents/admin_users/$(request.auth.token.email));
    }

    match /admin_users/{email} {
      allow read: if request.auth != null;
      allow write: if false;
    }
  }
}
```

### 4.3 Firebase Storage
1. Go to **Storage**
2. Click "Get started"
3. Choose **Start in test mode** (for development)

#### Storage Security Rules

```javascript
rules_version = '2';
service firebase.storage {
  match /b/{bucket}/o {
    match /products/{productId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null &&
                     firestore.exists(/databases/(default)/documents/admin_users/$(request.auth.token.email));
    }

    match /users/{userId}/{allPaths=**} {
      allow read: if true;
      allow write: if request.auth != null && request.auth.uid == userId;
    }
  }
}
```

### 4.4 Firebase Cloud Functions (For Payment Processing)
1. Install Firebase CLI:
```bash
npm install -g firebase-tools
```

2. Login and initialize:
```bash
firebase login
firebase init functions
```

3. Select your ARShop project
4. Choose JavaScript or TypeScript
5. Install dependencies

### 4.5 Firebase Cloud Messaging (Push Notifications)
1. Go to **Cloud Messaging**
2. Note the **Server key** (needed for backend)
3. Configure in Android app (already done in code)

## Step 5: Configure API Keys for Payments

### 5.1 Stripe
1. Go to [Stripe Dashboard](https://dashboard.stripe.com/)
2. Get your **Publishable key** and **Secret key**
3. Add to `local.properties`:
```
STRIPE_PUBLISHABLE_KEY=pk_test_...
```

4. Add Secret key to Firebase Functions config:
```bash
firebase functions:config:set stripe.secret_key="sk_test_..."
```

### 5.2 PayPal
1. Go to [PayPal Developer](https://developer.paypal.com/)
2. Create a sandbox app
3. Get **Client ID** and **Secret**
4. Add to Firebase Functions config:
```bash
firebase functions:config:set paypal.client_id="..." paypal.client_secret="..."
```

### 5.3 Razorpay
1. Go to [Razorpay Dashboard](https://dashboard.razorpay.com/)
2. Get **Test Key ID** and **Test Key Secret**
3. Add to `local.properties`:
```
RAZORPAY_KEY_ID=rzp_test_...
```

4. Add to Firebase Functions config:
```bash
firebase functions:config:set razorpay.key_secret="..."
```

### 5.4 Google Pay
1. Configured through Stripe (no separate setup needed)
2. Test with Stripe test cards

## Step 6: Create Admin User

To access the admin panel, add your email to the `admin_users` collection:

1. Go to Firestore Database
2. Create collection: `admin_users`
3. Add document with ID as your email (e.g., `admin@example.com`)
4. Add fields:
   - `email`: your email
   - `role`: "admin"
   - `permissions`: ["manage_products", "manage_orders"]

## Step 7: Build and Run

1. Open project in Android Studio
2. Sync Gradle files
3. Build the project:
```bash
./gradlew build
```

4. Run on device or emulator:
```bash
./gradlew installDebug
```

## Testing

### Test User Accounts
Create test accounts using the app's Sign Up feature.

### Test Products
You'll need to add products through the admin panel. For testing:
1. Sign in with your admin account
2. Go to Profile > Admin Panel > Manage Products
3. Add test products with:
   - Name, description, price
   - Category and gender
   - Images (upload from device)
   - Optional: 3D model (GLB file)
   - Enable AR if 3D model provided

### Test Payments
Use test cards:
- **Stripe**: 4242 4242 4242 4242 (any future date, any CVC)
- **PayPal**: Use sandbox test account
- **Razorpay**: Use test mode cards from Razorpay docs

## Troubleshooting

### Issue: "google-services.json not found"
- Ensure the file is in `arshop/app/` directory
- Re-sync Gradle files

### Issue: Google Sign-In fails
- Check SHA-1 certificate is added to Firebase
- Enable Google Sign-In in Firebase Authentication
- Download updated google-services.json

### Issue: Firestore permission denied
- Check security rules are correctly set
- Ensure user is authenticated
- For admin operations, verify admin_users collection

### Issue: ARCore not working
- Verify device supports ARCore
- Check camera permission granted
- Ensure API level >= 24

## Production Deployment

Before releasing to production:

1. **Change Firestore rules** to production rules (remove test mode)
2. **Change Storage rules** to production rules
3. **Use production API keys** for payment gateways
4. **Enable Firebase Crashlytics** for error reporting
5. **Configure ProGuard** (already set up)
6. **Generate release keystore**:
```bash
keytool -genkey -v -keystore arshop-release.jks -keyalg RSA -keysize 2048 -validity 10000 -alias arshop
```

7. **Create keystore.properties**:
```
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=arshop
storeFile=arshop-release.jks
```

8. **Build release APK**:
```bash
./gradlew assembleRelease
```

## Support

For issues or questions:
- Check Firebase Console for error logs
- Review Android Studio Logcat
- Consult Firebase documentation: https://firebase.google.com/docs
