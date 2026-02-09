# How to Run ARShop - Step by Step

**Total Time**: 30-45 minutes (first time setup)

---

## ⚡ Quick Overview

ARShop is an Android mobile app. You need:
1. A computer (Windows, Mac, or Linux)
2. Android Studio installed
3. A Firebase account (free)
4. An Android phone OR Android emulator

---

## 📋 Step-by-Step Instructions

### **STEP 1: Install Android Studio** (15-20 minutes)

#### On Windows:
1. Go to https://developer.android.com/studio
2. Click "Download Android Studio"
3. Run the installer (e.g., `android-studio-2023.1.1.26-windows.exe`)
4. Follow the wizard - accept all defaults
5. When it asks, also install Android Virtual Device (AVD)
6. Wait for it to download Android SDK (this is large, ~10 GB)

#### On Mac:
1. Go to https://developer.android.com/studio
2. Click "Download Android Studio"
3. Open the `.dmg` file
4. Drag Android Studio to Applications folder
5. Open Android Studio from Applications
6. Follow the setup wizard - accept all defaults
7. Wait for Android SDK download

#### On Linux (Ubuntu/Debian):
```bash
sudo snap install android-studio --classic
android-studio
# Follow the setup wizard
```

**✅ Verify**: Android Studio opens and shows welcome screen

---

### **STEP 2: Get the ARShop Code** (2 minutes)

1. Open a terminal/command prompt
2. Navigate to where you want the project:
   ```bash
   cd ~/Documents  # or wherever you want
   ```
3. Clone the repository:
   ```bash
   git clone https://github.com/abhishekyadgire/arshop.git
   cd arshop
   ```

**✅ Verify**: You see a folder called `arshop` with files inside

---

### **STEP 3: Create Firebase Project** (5 minutes)

1. **Go to Firebase Console**
   - Open browser: https://console.firebase.google.com/
   - Sign in with your Google account (or create one)

2. **Create New Project**
   - Click "Add project" or "Create a project"
   - Project name: Type **ARShop** (or any name you like)
   - Click Continue
   - Google Analytics: You can turn it off for testing
   - Click "Create project"
   - Wait ~30 seconds for it to be ready
   - Click "Continue"

3. **Add Android App**
   - On the project page, click the Android icon (robot)
   - Android package name: Type exactly `com.arshop`
   - App nickname: Type `ARShop Android`
   - Debug signing certificate: Leave blank for now
   - Click "Register app"

4. **Download Configuration File**
   - Click "Download google-services.json"
   - **Important**: Save this file - you'll need it in Step 5

5. **Enable Services** (click each and enable):
   - Click "Continue to console" to get to main page
   - In left sidebar, click **Authentication**
     - Click "Get started"
     - Click "Email/Password" → Enable it → Save
     - Click "Google" → Enable it → Save
   - In left sidebar, click **Firestore Database**
     - Click "Create database"
     - Start in **production mode** → Next
     - Select your region (e.g., us-central) → Enable
   - In left sidebar, click **Storage**
     - Click "Get started"
     - Start in **production mode** → Done

**✅ Verify**: You have `google-services.json` file downloaded

---

### **STEP 4: Add Firebase Config to Project** (1 minute)

1. Find the `google-services.json` file you downloaded
2. Copy it to your project's `app` folder:
   
   **Windows:**
   ```
   Copy the file to: C:\Users\YourName\Documents\arshop\app\
   ```
   
   **Mac:**
   ```bash
   cp ~/Downloads/google-services.json ~/Documents/arshop/app/
   ```
   
   **Linux:**
   ```bash
   cp ~/Downloads/google-services.json ~/Documents/arshop/app/
   ```

**✅ Verify**: File is at `arshop/app/google-services.json`

---

### **STEP 5: Open Project in Android Studio** (5 minutes)

1. **Open Android Studio**
2. Click **"Open"** (or File → Open)
3. Navigate to your `arshop` folder
4. Click **"OK"**
5. **Wait for Gradle Sync**
   - Bottom right: Shows "Gradle sync in progress..."
   - This takes 3-5 minutes first time
   - Downloads all dependencies
   - You'll see progress at the bottom
6. **If prompted** to update Gradle Plugin or SDK, click "Update" or "OK"

**✅ Verify**: No errors in bottom panel, status shows "Gradle build finished"

---

### **STEP 6: Set Up an Android Device** (Choose A or B)

#### **Option A: Use Your Physical Android Phone** (Recommended)

1. **Enable Developer Mode on Phone:**
   - Go to Settings → About Phone
   - Find "Build Number"
   - Tap it **7 times** rapidly
   - You'll see "You are now a developer!"

2. **Enable USB Debugging:**
   - Go to Settings → System → Developer Options
   - Turn on "USB Debugging"
   - Tap OK on any warnings

3. **Connect Phone to Computer:**
   - Use USB cable
   - On phone, if prompted "Allow USB debugging?" → Tap "Always allow" → OK
   - In Android Studio, top toolbar should show your device name

**✅ Verify**: Your phone appears in device dropdown in Android Studio

#### **Option B: Use Android Emulator**

1. In Android Studio: **Tools → Device Manager**
2. Click **"Create Device"**
3. Choose **Phone → Pixel 7** (or any device) → Next
4. Under "Recommended", select **UpsideDownCake** or latest → Download
5. Wait for system image to download (~2 GB)
6. Click Next → Finish
7. Click ▶️ (play button) next to your virtual device to start it
8. Wait for emulator to boot (1-2 minutes)

**✅ Verify**: Emulator shows Android home screen

---

### **STEP 7: Run the App!** 🚀

1. **In Android Studio:**
   - Make sure your device is selected in top toolbar
   - Click the green **▶️ Run** button (or press Shift+F10)
   - Or go to Run → Run 'app'

2. **Wait for Build:**
   - Bottom panel shows "Building..."
   - Takes 2-3 minutes first time
   - Compiling code, packaging app

3. **App Installs and Launches:**
   - You'll see "Installing APK"
   - App opens automatically on your device/emulator

**✅ Verify**: ARShop app opens and shows the login screen!

---

### **STEP 8: Use the App**

#### **First Time - Create Account:**
1. On login screen, tap **"Sign Up"**
2. Enter:
   - Name: Your name
   - Email: Any email (e.g., test@example.com)
   - Password: At least 8 characters
   - Confirm Password: Same password
3. Tap **"Sign Up"**
4. You're now logged in! 🎉

#### **Browse Products:**
1. You'll see Home screen (might be empty - no products yet)
2. Tap **Browse** at bottom
3. Products list may be empty - that's okay!

#### **Add Test Products (Optional):**
1. Go back to Firebase Console (https://console.firebase.google.com/)
2. Select your ARShop project
3. Click **Firestore Database** in left sidebar
4. Click **"Start collection"**
5. Collection ID: Type `products`
6. Click Next
7. Add a document:
   - Document ID: Type `product_001`
   - Add fields (click "Add field" for each):
     - `name` (string): `Test T-Shirt`
     - `price` (number): `29.99`
     - `description` (string): `A nice t-shirt`
     - `category` (string): `Clothing`
     - `imageUrls` (array): Click ➕, add string: `https://via.placeholder.com/500`
     - `isArEnabled` (boolean): `false`
     - `stockQuantity` (number): `100`
     - `sizes` (array): Add strings: `S`, `M`, `L`, `XL`
8. Click **"Save"**
9. In the app, pull down to refresh
10. You should see your product!

---

## 🎯 What to Do Next

### **Test Features:**
- ✅ **Sign Up/Login**: Create account, login, logout
- ✅ **Browse Products**: View product list (after adding to Firestore)
- ✅ **Product Details**: Tap a product to see details
- ✅ **Shopping Cart**: Add items to cart
- ✅ **Profile**: View your profile
- ⚠️ **AR Try-On**: Requires physical ARCore device (not emulator)
- ⚠️ **Payments**: Requires payment gateway setup (optional)

### **Add Admin Access (Optional):**
1. Create an account in the app first
2. In Firebase Console → Authentication
3. Find your user, copy the UID
4. Go to Firestore Database
5. Create collection `admin_users`
6. Add document with your UID as document ID
7. Add fields:
   - `email` (string): your email
   - `role` (string): `admin`
8. Logout and login again in the app
9. You'll now see Admin menu in Profile!

---

## ❓ Troubleshooting

### "App won't build"
- ✅ Make sure `google-services.json` is in `app/` folder
- ✅ Try: File → Invalidate Caches / Restart
- ✅ Try: Build → Clean Project, then Build → Rebuild Project

### "No products showing"
- ✅ Add products to Firestore (see Step 8)
- ✅ Check internet connection
- ✅ Pull down to refresh in the app

### "Can't connect to Firebase"
- ✅ Check `google-services.json` is correct file
- ✅ Package name in Firebase must be `com.arshop`
- ✅ Check internet connection
- ✅ Enable Authentication, Firestore, Storage in Firebase Console

### "Emulator won't start"
- ✅ Check virtualization is enabled in BIOS
- ✅ On Windows: Disable Hyper-V if using Intel HAXM
- ✅ Allocate more RAM to emulator (2GB minimum)

### "AR features don't work"
- ⚠️ AR requires a physical Android device with ARCore support
- ⚠️ Emulators do NOT support AR
- ✅ Check device compatibility: https://developers.google.com/ar/devices

---

## 📚 Additional Resources

- **Detailed Guide**: See `RUN_INSTRUCTIONS.md` for advanced setup
- **Firebase Setup**: See `FIREBASE_SETUP.md` for security rules
- **Testing**: See `TESTING_GUIDE.md` for testing procedures
- **Deployment**: See `DEPLOYMENT_GUIDE.md` for Play Store release

---

## ✅ Success Checklist

Did everything work? You should have:
- [x] Android Studio installed and running
- [x] ARShop code downloaded
- [x] Firebase project created
- [x] google-services.json in app folder
- [x] Project opens in Android Studio without errors
- [x] App builds successfully
- [x] App runs on device/emulator
- [x] Can create an account
- [x] Can see the app interface

---

## 🎉 You're Done!

You're now running the ARShop Android app! 

**What you've accomplished:**
- Set up a complete Android development environment
- Configured Firebase backend services
- Built and deployed an Android app
- Created a working e-commerce app with AR capabilities

**Next steps:**
- Explore the app features
- Add more products via Firebase Console
- Try different screens (Browse, Cart, Profile)
- Customize the app (change colors, add features)

---

**Need Help?** 
- Check the troubleshooting section above
- Review `RUN_INSTRUCTIONS.md` for detailed explanations
- Check Firebase Console logs for backend errors
- View Android Studio Logcat for app errors

**Enjoy your ARShop app!** 🛍️📱
