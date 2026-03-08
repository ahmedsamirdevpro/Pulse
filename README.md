# Pulse — Offline-First Social Network

<div align="center">

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-FFCA28?style=for-the-badge&logo=firebase&logoColor=black)
![Clean Architecture](https://img.shields.io/badge/Clean%20Architecture-FF6B35?style=for-the-badge&logo=android&logoColor=white)
![Multi-Module](https://img.shields.io/badge/Multi--Module-0D47A1?style=for-the-badge&logo=android&logoColor=white)
![Paging3](https://img.shields.io/badge/Paging%203-RemoteMediator-6200EA?style=for-the-badge&logo=android&logoColor=white)
![Offline First](https://img.shields.io/badge/Offline--First-WorkManager-00C853?style=for-the-badge&logo=android&logoColor=white)

**Stay Connected. Even Offline.** ⚡

*A production-grade social network Android app built with Enterprise-level architecture — Multi-module, Offline-first, Real-time, and fully Scalable.*

</div>

---



## ✨ Features
### ⚡ Sync Engine (Offline-First)
- **Pending posts** saved to Room and synced when online
- **Pending likes** queued and processed on reconnection
- **Feed caching** — last 50 posts stored locally
- **WorkManager** periodic sync every hour
- Exponential backoff retry on failure
- `eventual consistency` pattern — same as production apps

### 🔔 Notifications
- Real-time notifications for likes, comments, follows, reposts
- Unread badge counter
- Mark all as read with batch Firestore write

### 📰 Feed
- Real-time post timeline with **Paging 3** + **RemoteMediator** (Room as Single Source of Truth)
- **Optimistic Like** — heart turns red instantly, reverts on failure
- **Pull-to-Refresh** — swipe down to reload latest posts
- **Feed Ranking Algorithm** — posts scored by likes, comments, reposts & recency
- **prefetchDistance = 5** — next page loads before user reaches end
- `@Immutable` models — zero unnecessary recompositions

### 🔐 Authentication
- Email & Password registration and login via **Firebase Auth**
- Persistent session with automatic auth state management
- Input validation with real-time error feedback


### ✍️ Create Post
- Rich text post creation (up to 280 characters)
- Image attachment support via **Firebase Storage**
- **Offline-first**: posts saved locally and synced automatically when internet returns
- Character counter with visual feedback

### 👤 Profile
- Real-time profile updates via **Firestore listeners**
- Follow / Unfollow system with live follower counts
- Profile image upload to Firebase Storage
- Posts, Followers, Following stats

### 🔍 Search
- Real-time user search with **400ms debounce**
- Search by username or display name
- "Who to follow" recommendations sorted by popularity

### 💬 Comments
- Real-time comments via **Firestore snapshot listeners**
- Add and delete comments
- Keyboard-aware layout with `imePadding`


---

## 🏗️ Architecture

Pulse is built with **Clean Architecture**, **MVVM**, and **Feature Modularization** — the same pattern used by Google's Now in Android app.

```
┌─────────────────────────────────────────────┐
│              Presentation Layer             │
│   Screens (Compose) + ViewModels + States   │
├─────────────────────────────────────────────┤
│               Domain Layer                  │
│     Use Cases + Repository Interfaces       │
│              + Models                       │
├─────────────────────────────────────────────┤
│                Data Layer                   │
│   Firebase DataSources + Room + Repos       │
└─────────────────────────────────────────────┘
```

---

## 📂 Multi-Module Structure

```
Pulse/
│
├── app/                          ← Entry point, DI, Navigation, SyncEngine
│
├── core/
│   ├── core-model/               ← Domain models: User, Post, Comment, Notification
│   ├── core-ui/                  ← Shared Compose components + PulseTheme
│   ├── core-network/             ← Retrofit, DTOs, AuthInterceptor
│   ├── core-database/            ← Room DB, DAOs, Entities, Mappers
│   └── core-common/              ← Extensions, Constants, NetworkMonitor, DispatcherProvider
│
├── feature/
│   ├── feature-auth/             ← Login, Register, Splash
│   ├── feature-feed/             ← Timeline, PostDetail, Paging
│   ├── feature-post/             ← Create Post with image upload
│   ├── feature-profile/          ← Profile, Follow/Unfollow, Edit
│   ├── feature-search/           ← User search + recommendations
│   ├── feature-comments/         ← Real-time comments
│   └── feature-notifications/    ← Notifications + mark as read
│
└── build-logic/
    └── convention/               ← Convention plugins (AndroidLibrary, Feature, Hilt)
```

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin |
| **UI** | Jetpack Compose + Material Design 3 |
| **Architecture** | MVVM + Clean Architecture |
| **Modularization** | Multi-module (Feature Modularization) |
| **DI** | Hilt |
| **Async** | Coroutines + Flow + StateFlow |
| **Networking** | Retrofit2 + OkHttp3 |
| **Authentication** | Firebase Auth |
| **Database** | Firebase Firestore (remote) + Room (local) |
| **Storage** | Firebase Storage |
| **Pagination** | Paging 3 + RemoteMediator |
| **Feed Ranking** | Custom Score Algorithm (likes + comments + recency) |
| **Background Sync** | WorkManager |
| **Image Loading** | Coil |
| **Build System** | Gradle Convention Plugins |

---

## ⚡ Offline-First Architecture

The core principle of Pulse:

```
Room = Single Source of Truth
Network = Sync Layer
```

### Flow for Creating a Post:
```
User taps "Post"
      ↓
Save to Room (pending_posts table)
      ↓
Is online?  →  Yes  →  SyncWorker uploads immediately
              No   →  WorkManager waits for connectivity
                              ↓
                    Network returns → Auto-sync
                              ↓
                    Post appears in feed ✅
```

### Sync Engine:
```kotlin
// WorkManager periodic sync — every 1 hour
syncManager.startPeriodicSync()

// Immediate sync on network return
syncManager.syncNow()

// SyncWorker handles:
// ✅ Pending posts upload
// ✅ Pending likes/unlikes
// ✅ Feed cache refresh (last 50 posts)
```

---

## 🔥 Firebase Setup

1. Create a project at [Firebase Console](https://console.firebase.google.com)
2. Enable:
   - **Authentication** → Email/Password
   - **Firestore Database** → Test Mode → `europe-west1`
   - **Storage** → Test Mode
3. Download `google-services.json` → place in `app/`
4. Add SHA-1:
```bash
./gradlew signingReport
```

### Firestore Structure:
```
users/{userId}
  → id, username, displayName, email, bio,
    profileImageUrl, followersCount, followingCount, postsCount

posts/{postId}
  → id, authorId, content, imageUrl,
    likesCount, commentsCount, repostsCount, createdAt
  → comments/{commentId}

likes/{userId_postId}
follows/{followerId_followingId}
notifications/{notificationId}
```

---

## ⚙️ Getting Started

### Prerequisites
- Android Studio Hedgehog or later
- Min SDK: API 26 (Android 8.0)
- Firebase project configured

### Installation

```bash
# Clone the repository
git clone https://github.com/ahmedsamirdevpro/Pulse.git

# Open in Android Studio
# Add google-services.json to app/

# Build & Run
./gradlew assembleDebug
```

---


---

## 📋 Screens Overview

| Screen | Description |
|---|---|
| **Splash** | Auth state check + animated logo |
| **Login** | Email/password sign-in |
| **Register** | Account creation with validation |
| **Feed** | Infinite scroll timeline with Paging 3 |
| **Post Detail** | Full post view with stats and actions |
| **Create Post** | Text + image post with offline support |
| **Profile** | User info, stats, follow/unfollow |
| **Search** | Real-time user search + recommendations |
| **Comments** | Live comments with keyboard handling |
| **Notifications** | Activity feed with unread badges |

---

## 🔮 Future Improvements

- [ ] Direct Messages (DM)
- [ ] Stories feature
- [ ] Push notifications (FCM)
- [ ] Video post support
- [ ] Trending topics
- [ ] Hashtag system
- [ ] End-to-end encryption for DMs
- [ ] Dark/Light theme toggle

---

## 👨‍💻 Author

**Ahmed Samir AbdElhamid**
- 📧 ahmedsamir.devpro@gmail.com
- 💼 [LinkedIn](https://www.linkedin.com/in/ahmedsamir-devpro)
- 🐙 [GitHub](https://github.com/ahmedsamirdevpro)

---

## 📄 License

```
Copyright 2026 Ahmed Samir AbdElhamid

Licensed under the Apache License, Version 2.0
```

---

<div align="center">
⭐ If you found this project useful, please give it a star!
</div>