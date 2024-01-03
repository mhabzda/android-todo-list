# Android Todo List

This sample application contains the paged todo list with the possibility to create, edit, and remove items.
Data is stored in the Firebase Cloud Firestore.

To build the project you need a `google-services.json` file in the `app/` directory.  
You can follow these instructions to get it for your account -
[firestore doc](https://firebase.google.com/docs/firestore/quickstart).

### Demo

<div align="center">
  <video src="https://github.com/mhabzda/android-todo-list/assets/26122834/d7b2e2a6-b15f-4d33-83c1-78d216e3e6e6" width="400" />
</div>

### Architecture

Clean Architecture has been used in the project. There are multiple Gradle modules containing
specific layers in the Architecture:

- App (Android module)
- Domain (Java module)
- Data (Android module)

Particular screens have been created using the MVI pattern.

### Technology stack

- Android Jetpack Compose (Material3)
- AndroidX Paging library v3
- Hilt
- Kotlin Coroutines
- Firestore Android SDK
- Coil
- JUnit 5
- Mockito Kotlin
- KtLint

### Gradle Versions Plugin

In order to automate dependencies version updates, the Gradle Versions plugin alongside the Version catalog
update plugin has been introduced.
There are two tasks that can help with automatic dependency updates:

- `./gradlew dependencyUpdate` - shows a report of possible updates. It takes only stable (not RC)
  versions into account.
- `./gradlew versionCatalogUpdate` - automatically updates the `libs.versions.toml` file with the
  latest stable versions.
