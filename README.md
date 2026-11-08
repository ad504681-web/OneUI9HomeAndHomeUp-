# One UI 9 / 8 Home Launcher & Companion Home Up (v18) for One UI 6 (Android 14)

This multi-module Android Studio project provides a complete implementation of the **[Samsung One UI Home](https://play.google.com/store/apps/details?id=com.sec.android.app.launcher&hl=en)** launcher (with One UI 8 and One UI 9 features) paired with a standalone **[Good Lock Home Up (v18.0.00.38)](https://samsung.gadgethacks.com/news/samsung-home-up-one-ui-9-home-screen-fix-who-needs-it/)** companion APK that communicates with the launcher seamlessly on **One UI 6.0 / 6.1 (Android 14, API 34)** without root or Samsung platform keys.

## Why Two Paired APKs Are Required on One UI 6

Official [One UI 8.5 and One UI 9 APKs](https://www.reddit.com/r/oneui/comments/1ruyye7/one_ui_85_all_apksnot_mine/) depend on private framework changes and Samsung platform-key signatures introduced in Android 16/17. By pairing `:oneuihome` (`com.clone.sec.android.app.launcher`) and `:homeup` (`com.clone.samsung.android.app.homestar`) over a shared IPC `ContentProvider` (`content://com.clone.sec.android.app.launcher.homeup.provider/config`) and real-time broadcasts (`com.clone.samsung.homestar.ACTION_CONFIG_UPDATED`), both apps install side-by-side on any **One UI 6** device and synchronize settings instantaneously.

## Included Modules

1. **`:shared-bridge` (`com.clone.samsung.homestar.bridge`)**
   - Shared data contract (`HomeUpConfig`), IPC `ContentProvider` authority constants, and `HomeUpBridgeClient` for real-time synchronization between the Home Up APK and the One UI Home APK.
2. **`:oneuihome` (`com.clone.sec.android.app.launcher` -> `OneUIHome-OneUI9-Clone.apk`)**
   - Full Android Launcher (`CATEGORY_HOME`) built with Kotlin & Jetpack Compose targeting API 34 (One UI 6).
   - Implements [DIY Home Screen freeform layout](https://samsung.gadgethacks.com/news/samsung-home-up-one-ui-9-home-screen-fix-who-needs-it/) (free X/Y drag, rotation, and scale for icons, widgets, and stickers).
   - Implements [One UI 9 Stack Widgets](https://samsung.gadgethacks.com/news/samsung-home-up-one-ui-9-home-screen-fix-who-needs-it/), enlarged 3x3 folders, and frosted-glass folder popups.
   - Implements the [One UI 8/9 Home Screen Settings Dialog](https://sammyguru.com/good-lock-home-up-guide/) accessible directly by long-pressing the home screen.
   - Implements the [One UI 9 Frosted-Glass Edge Panel](https://sammyguru.com/good-lock-home-up-guide/) with configurable blur, opacity, and corner radius.
   - Implements [One UI 9 Multi-Finger Gestures](https://www.engadget.com/2305270/samsung-one-ui-9-coolest-features/) (2-finger, 3-finger, and 4-finger swipe actions) and [Live Data Speed monitoring](https://www.engadget.com/2305270/samsung-one-ui-9-coolest-features/).
3. **`:homeup` (`com.clone.samsung.android.app.homestar` -> `HomeUp-v18-Clone.apk`)**
   - Standalone companion customization app modeled after [Good Lock Home Up v18.0.00.38](https://www.sammobile.com/news/new-good-lock-home-up-update-brings-two-changes/).
   - Modifies Home Screen grids (up to 7x7), DIY Home Screen toggle, Folder transparency, Edge Panel glass settings, Gesture Animation damping/stiffness/bezier curves, Task Changer layouts (Stack, Grid, List, Vertical List, [Slim List](https://www.reddit.com/r/Samsung_GoodLock/comments/1x8itr5/will_good_lock_work_on_one_ui_9/)), and backup/restore presets.

## Building Both APKs

1. Open this folder in **Android Studio Ladybug (or newer)** with Android SDK 34 installed.
2. Run Gradle sync and build both APKs:
   ```bash
   ./gradlew :oneuihome:assembleDebug :homeup:assembleDebug
   ```
3. Install both APKs onto your One UI 6 device via ADB:
   ```bash
   adb install oneuihome/build/outputs/apk/debug/oneuihome-debug.apk
   adb install homeup/build/outputs/apk/debug/homeup-debug.apk
   ```
4. Open **Settings > Apps > Choose default apps > Home app** on your One UI 6 phone and select **One UI 9 Home**.
