#  Android App: Auth, PDF Viewer, Offline Storage, Notifications & Image Handling

##  Project Overview

This Android application demonstrates the integration of modern Android development features, including user authentication, offline data management with Room DB, push notifications via Firebase Cloud Messaging (FCM), image capture & selection, and an in-app PDF viewer.

---

##  Features

###  1. User Authentication
- Implemented **Google Sign-In** using **Firebase Authentication**.
- User details are saved securely in **Room Database** for offline access.

###  2. Report Viewer (PDF)
- Embedded PDF viewer to show reports from a given URL:
    - [Balance Sheet PDF](https://fssservices.bookxpert.co/GeneratedPDF/Companies/nadc/2024-2025/BalanceSheet.pdf)
- Integrated using a **third-party library** such as [AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer).

###  3. Image Capture & Selection
- Capture images via **device camera**.
- Pick images from **gallery**.
- Display selected image in an `ImageView`.

### ️ 4. Room Database & API Integration
- Fetched data from: `https://api.restful-api.dev/objects`
- Stored API data in **Room DB** for offline access.
- Supported **CRUD operations**: Create, Update, and Delete.
- Implemented proper **error handling** and **validations**.

### 5. Push Notifications
- Integrated **Firebase Cloud Messaging (FCM)**.
- Sent **real-time notifications** when an item is deleted.
- Notifications include item details.
- Users can **enable/disable notifications** using **SharedPreferences** or **Preference DataStore**.

---

##  Technical Stack

- **Language:** Kotlin
- **Architecture:** MVVM (Model-View-ViewModel)
- **UI:** Modern Material Design (Light & Dark Theme Support)
- **Local Storage:** Room Database
- **Networking:** Retrofit with Coroutines
- **Authentication:** Firebase Auth (Google Sign-In)
- **Push Notifications:** Firebase Cloud Messaging
- **Image Handling:** Camera & Gallery with Runtime Permissions
- **Preferences:** SharedPreferences / DataStore
- **PDF Viewer:** AndroidPdfViewer (or similar)

---

## 🔧 Permissions Required

- Camera Permission
- Read/Write External Storage
- Internet Access

> All permissions are handled at runtime for a secure user experience.

---

## Installation

1.  Clone the repository to your local machine.
2.  Open the project in Android Studio.
3.  Build and run the application on an Android emulator or physical device.


## Apk

* Debug APK: [Download](app-debug.apk)
