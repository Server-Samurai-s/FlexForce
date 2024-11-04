# 🏋️ FlexForce – Gym Application and Fitness Tracker

**FlexForce** is a comprehensive fitness application designed to offer a customized training experience that assists users in achieving their fitness goals. From personalized routines to progress tracking and workout challenges, FlexForce makes training engaging and effective.

## 👥 Members
* **Aidan Keith Naidoo** - ST10109482
* **David Roy Mellors** - ST10241466
* **Nilay Ramjee** - ST10082679
* **Dylan Miller** - ST10029256

## 📱 App Details
* **App Name**: FlexForce
* **App Type**: Gym Application / Fitness Tracker

## 🎯 Purpose
FlexForce aims to provide an accessible and user-friendly fitness tracking solution to support a healthy lifestyle. Users can create custom training routines, track their progress, and participate in workout challenges. Predefined workouts are also available for quick access.

## 🚀 Key Innovative Features
* **Filter Exercises by Muscle Group Anatomy**: Select body parts on an interactive anatomy figure to find targeted exercises.
* **Workout Streaks and High Score**: Keeps users motivated by rewarding consistent progress.
* **Workout Challenges**: Join challenges to push fitness limits.
* **Google Login**: Login with Google for convenience.
* **Weight Tracker**: Track weight, body fat percentage, and BMI for insights into the fitness journey.

## 📦 Release Notes

### New Features
1. **Google Login**: Seamless login with Google.
2. **Weight Tracker**: Track weight, body fat %, and BMI.

### Updates
* **Single Sign-On** and **Biometric Authentication** for secure logins.
* **Settings** page for personal customization.
* **Offline Mode**: Save workouts and settings offline.
* **Real-time Notifications** to keep users engaged.
* **Multi-language Support**: Choose from English, Afrikaans, or Zulu.

## 🎨 Design Considerations
FlexForce's design was influenced by a review of top fitness apps, with a focus on combining their best features into a visually appealing and easy-to-use interface for enhanced user engagement.

## 🎨 Colour Palette
* **Primary Colour**: #1B1C1E
* **Secondary Colour**: #FFFFFF
* **Background Colour**: #303030
* **Text Colour**: #FFFFFF

## 📱 Screens Overview
1. **Welcome Screen**: Introduction to the app with options to register or log in.
2. **Registration Screen**: Create a new account.
3. **Login Screen**: Login with credentials.
4. **Home Screen**: View workout streaks, weight, favorite exercises, and set fitness goals.
5. **Settings Page**: Customize profile and app settings.
6. **Workout Screen**: Browse exercises by category.
7. **Create Workout Screen**: Design custom workout routines.
8. **Challenges Screen**: Access and join fitness challenges.
9. **Statistics Screen**: Track fitness progress, streaks, and BMI.

## 🗺️ Application Navigation
FlexForce utilizes a bottom navigation bar for seamless access to profile, workouts, statistics, challenges, and settings.

## 📡 API Design
The REST API facilitates data flow between the Android app and Firebase:
* **Endpoints**: Synchronization, user data, and exercise retrieval.
* **Tech Stack**: Built with Node.js and Express, hosted on Vercel.
* **Methods**: Includes GET, POST, and DELETE.

### Key API Functions
* **Workout Retrieval**: Fetch workouts based on type.
* **Preset Workouts**: Access predefined workouts based on user preferences.

## 🔄 GitHub and GitHub Actions
The FlexForce project leverages GitHub for version control and GitHub Actions for CI/CD:
* **Automated Testing**: Each push triggers unit tests.
* **Automated Build**: Builds APK and AAB files for distribution.
* **Artifact Upload**: APK and AAB files are stored as GitHub artifacts.
* **Triggers**: Actions run on each push to `master` or `release/**`.

## 🔔 Real-time Notifications Setup
FlexForce uses Firebase Cloud Messaging (FCM) to provide real-time notifications:

1. **Open Firebase Console**:
   * Navigate to **Cloud Messaging** in Firebase Console and create a new notification.

2. **Enter Notification Details**:
   * Define the title and body, configure targeting, and set scheduling options.

3. **Retrieve FCM Token**:
   * Obtain the FCM token from the Android Studio logs to target specific devices.

4. **Implement Firebase Messaging Service**:
   * Create `FirebaseMessagingService.kt` in Android Studio, extending `FirebaseMessagingService`.
   * Use `onMessageReceived` to display the notification with content specified in Firebase.

5. **Test Notification Delivery**:
   * Use the **Test on device** feature in Firebase Console, pasting the FCM token to deliver the notification to the target device.

6. **Verify Notification**:
   * Confirm the notification appears on the device with the correct title and body.

## 🏃 Getting Started
1. Clone the repository from GitHub: 
git clone  https://github.com/Server-Samurai-s/FlexForce.git
2. Open the project in Android studio
3. Build and run the project on an Android device or emulator. 
API repository: git clone https://github.com/davidrmellors/flexforce-api.git

## ⚙️ **Continuous Integration with GitHub Actions**

**GitHub Actions** is used in this project to automate testing and building processes. It ensures that each code push is tested and the app is built correctly across environments, reducing manual work and enhancing consistency.

**Automated Testing**: Every push to the `master` or `release/**` branches triggers automated unit tests using Gradle (`./gradlew test`). This ensures that the main functionality of the app is verified with every code update.

**Automated Build**: After successful testing, the workflow automatically builds APK and AAB files (`./gradlew assembleDebug` and `./gradlew bundleRelease`). These files are used for distribution and testing.

**Artefact Upload**: The APK and AAB files are uploaded as artefacts, making them available for download directly from GitHub.

**Triggers**: The workflow is automatically triggered on every push to `master` or `release/**` branches, ensuring continuous integration and consistent validation of code changes.

## 🧪 Technologies Used
1. Firebase: Cloud firestore is used for storing user workouts and data.
2. Vercel: Used to host the API.
3. Node.js and Express: Used to build a custom REST API that connects to firebase. 
4. Android Development: Developed using Kotlin programming language for Android platform compatibility.
5. Database: RoomDB for offline mode.
 
## 📚 Reference / Bibliography
· GeeksforGeeks. 2024. How to send different types of requests (GET, POST, PUT, DELETE) in Postman, 1 January 2024. [Online]. Available at: https://www.geeksforgeeks.org/how-to-send-different-types-of-requests-get-post-put-delete-in-postman/ [Accessed 23 September 2024].

## 📜 License
This project is licensed under the MIT License. See the LICENSE file for more information.
