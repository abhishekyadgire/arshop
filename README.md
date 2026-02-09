# ARShop - AR E-Commerce Android Application

ARShop is a native Android e-commerce application with integrated Augmented Reality (AR) virtual try-on capabilities for clothing and footwear. Built with Kotlin and Jetpack Compose, the app allows users to visualize how products look on them before purchase, enhancing shopping confidence and reducing uncertainty.

## 🚀 **Want to Run This App?**

### 👉 **[START HERE: Step-by-Step Guide](HOW_TO_RUN.md)** 👈

Complete beginner-friendly instructions to get the app running in 30-45 minutes.

**Quick Links:**
- 📖 [HOW_TO_RUN.md](HOW_TO_RUN.md) - Full step-by-step guide (recommended)
- 📋 [START_HERE.md](START_HERE.md) - Quick reference and overview
- ⚡ [QUICKSTART.md](QUICKSTART.md) - 5-minute summary

## Features

### Core Features
- **AR Virtual Try-On**: Try clothes and shoes virtually using ARCore
  - Clothing: 3D garment overlay on body detection
  - Footwear: Dual mode (floor placement & foot tracking)
  - Screenshot and share AR sessions
- **E-Commerce Platform**: Complete shopping experience
  - Product browsing with filters (category, gender, price, AR-enabled)
  - Search functionality
  - Shopping cart with real-time sync
  - Multi-payment gateway support (Stripe, PayPal, Razorpay, Google Pay)
  - Order tracking and history
- **User Authentication**: Secure sign-in
  - Email/password authentication
  - Google Sign-In integration
  - Password reset functionality
- **Admin Panel**: Product and order management
  - Add/edit/delete products
  - Upload product images and 3D models
  - Update order statuses
  - View analytics

## Technology Stack

- **Platform**: Native Android (Kotlin)
- **UI Framework**: Jetpack Compose with Material Design 3
- **Architecture**: MVVM with Repository pattern
- **Dependency Injection**: Hilt
- **AR Framework**: Google ARCore SDK with Sceneview
- **Backend**: Firebase
  - Authentication (Email/Password, Google Sign-In)
  - Firestore (database)
  - Storage (images, 3D models)
  - Cloud Functions (payment processing)
  - Cloud Messaging (push notifications)
- **Local Database**: Room (caching)
- **Image Loading**: Coil
- **Networking**: Retrofit + OkHttp
- **Payment SDKs**: Stripe, PayPal, Razorpay
- **Async**: Kotlin Coroutines + Flow

## Requirements

- **Minimum SDK**: API 24 (Android 7.0) - Required for ARCore
- **Target SDK**: API 34 (Android 14)
- **JDK**: Version 17
- **Android Studio**: Arctic Fox or later
- **ARCore Support**: Device must support ARCore (check compatibility: https://developers.google.com/ar/devices)

## Project Structure

```
arshop/
├── app/
│   ├── src/main/
│   │   ├── java/com/arshop/
│   │   │   ├── ui/                    # Compose UI screens
│   │   │   │   ├── screens/           # Screen composables
│   │   │   │   │   ├── auth/          # Login, SignUp
│   │   │   │   │   ├── home/          # Home screen
│   │   │   │   │   ├── browse/        # Product browsing
│   │   │   │   │   ├── product/       # Product detail
│   │   │   │   │   ├── ar/            # AR try-on
│   │   │   │   │   ├── cart/          # Shopping cart
│   │   │   │   │   ├── checkout/      # Checkout flow
│   │   │   │   │   ├── profile/       # User profile
│   │   │   │   │   └── admin/         # Admin panel
│   │   │   │   ├── components/        # Reusable UI components
│   │   │   │   ├── theme/             # Material 3 theme
│   │   │   │   ├── navigation/        # Navigation setup
│   │   │   │   └── MainActivity.kt
│   │   │   ├── viewmodel/             # ViewModels
│   │   │   ├── repository/            # Repository layer
│   │   │   ├── data/
│   │   │   │   ├── model/             # Data models
│   │   │   │   ├── remote/            # Firestore helpers
│   │   │   │   └── local/             # Room database
│   │   │   │       ├── entity/        # Room entities
│   │   │   │       └── dao/           # Data access objects
│   │   │   ├── di/                    # Hilt modules
│   │   │   ├── ar/                    # AR-specific code
│   │   │   ├── util/                  # Utilities
│   │   │   └── ARShopApplication.kt
│   │   ├── res/                       # Resources
│   │   │   ├── values/                # Strings, colors, themes
│   │   │   ├── drawable/              # Icons, images
│   │   │   └── xml/                   # XML configs
│   │   ├── assets/                    # Placeholder 3D models
│   │   └── AndroidManifest.xml
│   ├── build.gradle.kts               # App-level Gradle
│   ├── proguard-rules.pro             # ProGuard configuration
│   └── google-services.json           # Firebase config (YOU MUST ADD)
├── build.gradle.kts                   # Project-level Gradle
├── settings.gradle.kts                # Gradle settings
├── gradle.properties                  # Gradle properties
├── README.md                          # This file
└── FIREBASE_SETUP.md                  # Firebase configuration guide
```

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd arshop
```

### 2. Configure Firebase

**This is the most critical step.** The app will not work without Firebase configuration.

1. Follow the detailed guide in [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)
2. Create a Firebase project named "ARShop"
3. Add Android app with package name: `com.arshop`
4. Download `google-services.json` and place it in `arshop/app/`
5. Enable Firebase services:
   - Authentication (Email/Password, Google Sign-In)
   - Firestore Database
   - Storage
   - Cloud Functions
   - Cloud Messaging

### 3. Configure API Keys

Create `local.properties` in the root directory:

```properties
# Firebase is configured via google-services.json

# Stripe (get from https://dashboard.stripe.com/)
STRIPE_PUBLISHABLE_KEY=pk_test_your_key_here

# Razorpay (get from https://dashboard.razorpay.com/)
RAZORPAY_KEY_ID=rzp_test_your_key_here

# Google Sign-In is configured via google-services.json
```

### 4. Build and Run

1. Open the project in Android Studio
2. Wait for Gradle sync to complete
3. Connect an ARCore-compatible device or use an emulator
4. Build and run:

```bash
./gradlew assembleDebug
./gradlew installDebug
```

Or click the "Run" button in Android Studio.

### 5. Create Admin Account

To access the admin panel:

1. Sign up through the app
2. Go to Firebase Console > Firestore Database
3. Create collection `admin_users`
4. Add a document with your email as the document ID:
   ```json
   {
     "email": "your-email@example.com",
     "role": "admin",
     "permissions": ["manage_products", "manage_orders"]
   }
   ```
5. Restart the app and sign in

## Usage

### For Users

1. **Browse Products**: Open the app and browse the catalog
2. **Filter Products**: Use filters (category, gender, price, AR-enabled)
3. **View Product Details**: Tap on any product to see details
4. **Try with AR**: Tap "Launch AR Try-On" to see the product virtually
5. **Add to Cart**: Select size/color and add to cart
6. **Checkout**: Proceed to checkout and complete payment
7. **Track Orders**: View order history in your profile

### For Admins

1. Sign in with admin account
2. Navigate to Profile > Admin Panel
3. **Manage Products**:
   - Add new products with images and 3D models
   - Edit existing products
   - Manage inventory
4. **Manage Orders**:
   - View all orders
   - Update order statuses
   - Add tracking numbers

## Testing

### Test Accounts

Create test accounts using the Sign Up feature in the app.

### Test Payments

Use test credentials:
- **Stripe**: Card `4242 4242 4242 4242`, any future date, any CVC
- **PayPal**: Use PayPal sandbox account
- **Razorpay**: Use Razorpay test mode cards
- **Google Pay**: Configured via Stripe test mode

### Test AR

1. Ensure your device supports ARCore
2. Grant camera permission
3. Select an AR-enabled product
4. Follow on-screen instructions for body/floor detection

## Known Limitations

1. **3D Models**: Project uses placeholder models. In production, you need actual GLB/GLTF files for products.
2. **Payment Processing**: Cloud Functions for payment processing need to be deployed separately.
3. **Device Compatibility**: AR features require ARCore-compatible devices (API 24+).
4. **Firebase Configuration**: App requires valid Firebase setup - it will not work without it.

## Next Steps / Future Enhancements

- [ ] Add product reviews and ratings
- [ ] Implement wishlist functionality
- [ ] Add push notifications for order updates
- [ ] Implement advanced search (Algolia integration)
- [ ] Add multi-language support
- [ ] Implement dark mode
- [ ] Add video product demos
- [ ] Social sharing integration
- [ ] Referral system
- [ ] Advanced analytics dashboard for admins

## Troubleshooting

### App crashes on launch
- Verify `google-services.json` is in `arshop/app/` directory
- Check Firebase project is properly configured
- Review Android Studio Logcat for specific errors

### Google Sign-In fails
- Add SHA-1 certificate to Firebase project
- Enable Google Sign-In in Firebase Authentication
- Download updated `google-services.json`

### AR not working
- Confirm device supports ARCore
- Grant camera permission
- Check API level is >= 24

### Build errors
- Clean and rebuild: `./gradlew clean build`
- Invalidate caches: Android Studio > File > Invalidate Caches > Invalidate and Restart
- Check all dependencies are downloaded

### Firebase permission errors
- Review Firestore security rules in Firebase Console
- Ensure user is authenticated
- For admin operations, verify `admin_users` collection entry

## Contributing

This is a demonstration project. For production use:
1. Implement comprehensive error handling
2. Add extensive unit and integration tests
3. Optimize AR rendering performance
4. Implement proper 3D model management
5. Add analytics and monitoring
6. Conduct security audit
7. Implement rate limiting
8. Add comprehensive logging

## License

[Specify your license here]

## Support

For detailed Firebase setup, see [FIREBASE_SETUP.md](./FIREBASE_SETUP.md)

For issues:
1. Check Firebase Console logs
2. Review Android Studio Logcat
3. Consult ARCore documentation: https://developers.google.com/ar
4. Firebase documentation: https://firebase.google.com/docs

## Acknowledgments

- Google ARCore for AR capabilities
- Firebase for backend infrastructure
- Material Design 3 for UI components
- Jetpack Compose for modern Android UI
- Payment gateway providers (Stripe, PayPal, Razorpay)