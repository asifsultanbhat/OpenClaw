# ClawController - OpenClaw Automator for Android

An Android application that automates the Termux CLI environment to run the OpenClaw AI agent with a beautiful, user-friendly interface.

## Features

✅ **Fresh Install Macro** - One-click cleanup and reinstallation of OpenClaw with all dependencies  
✅ **Safe Runtime Engine** - Automated startup with Android-specific patches (chroot, TMPDIR)  
✅ **Token Sniffer** - Automatically extracts and displays the gateway auth token  
✅ **Wake-Lock Persistence** - Keeps OpenClaw running in the background  
✅ **Configuration Manager** - Secure storage for API keys (Gemini, Telegram)  
✅ **Embedded Dashboard** - WebView for accessing the OpenClaw dashboard at localhost:18789

## Prerequisites

Before using ClawController, ensure you have the following installed on your Android device:

1. **Termux** - Main terminal emulator ([Download from F-Droid](https://f-droid.org/en/packages/com.termux/))
2. **Termux:API** - Required for wake-lock and system integration ([Download](https://f-droid.org/en/packages/com.termux.api/))
3. **Termux:Tasker** - Required for sending commands from ClawController to Termux ([Download](https://f-droid.org/en/packages/com.termux.tasker/))

> **Important**: Install all three apps from F-Droid, NOT from Google Play Store, as the Play Store versions are outdated.

## Building the App

Since this project was generated without Android Studio on your system, follow these steps:

### 1. Open in Android Studio

```bash
# Navigate to the project directory
cd C:\Users\asifs\.gemini\antigravity\scratch\ClawController

# Open Android Studio and select "Open an Existing Project"
# Point it to the ClawController folder
```

### 2. Sync Gradle

- Android Studio will automatically prompt you to sync Gradle dependencies
- Click **"Sync Now"** and wait for the process to complete
- If there are any SDK version mismatches, Android Studio will offer to install them

### 3. Build the APK

```
Build → Build Bundle(s) / APK(s) → Build APK(s)
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

### 4. Install on Android Device

**Option A: USB Debugging**
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Option B: Manual Transfer**
- Copy the APK to your phone
- Open the APK file and install it
- You may need to enable "Install from Unknown Sources" in Settings

## Usage

### Initial Setup

1. **Install Dependencies in Termux** first:
   ```bash
   pkg update
   pkg install termux-api
   ```

2. **Open ClawController** and configure your API keys:
   - Tap the settings icon (⚙️) in the toolbar
   - Enter your Gemini API Key
   - Enter your Telegram Bot Token
   - Enter your Telegram ID
   - Tap **Save Configuration**

### Fresh Install

Tap the **"Fresh Install"** button to:
- Clean old OpenClaw installation
- Update Termux packages
- Install all dependencies (Node.js, Python, libvips, xorgproto, etc.)
- Create the Gyp dummy config (Android NDK bypass)
- Install the latest OpenClaw version

This process takes 5-10 minutes depending on your connection speed.

### Starting OpenClaw

1. Tap **"Start Runtime"**
2. The app will:
   - Execute `termux-chroot` for proper environment
   - Set `TMPDIR=$PREFIX/tmp` (Android-specific fix)
   - Launch `openclaw gateway run`
3. Watch the **Terminal Output** section for progress
4. When you see **"Gateway auth token:"**, it will be automatically extracted and displayed
5. The **WebView** will automatically load the dashboard at `http://127.0.0.1:18789`

### Background Operation

- ClawController uses a **Foreground Service** with a persistent notification
- This prevents Android from killing the OpenClaw process
- The app requests **"Ignore Battery Optimization"** for maximum reliability
- You can switch to other apps - OpenClaw will keep running

### Stopping OpenClaw

Tap the **"Stop"** button to gracefully terminate the gateway.

## Architecture

### Components

- **MainActivity** - Dashboard UI with status, token display, controls, WebView, and logs
- **ConfigActivity** - Secure configuration screen for API keys
- **TermuxCommandService** - Foreground service that communicates with Termux
- **FreshInstallWorker** - Background job for the Fresh Install Macro
- **RuntimeEngineManager** - Pre-flight checks and gateway status monitoring
- **ConfigManager** - Encrypted storage using Android Keystore
- **TokenExtractor** - Regex-based token detection from terminal output

### Communication Flow

```
ClawController → Termux:Tasker Intent → Termux → OpenClaw
```

The app sends commands via the Termux:Tasker plugin, which executes them in Termux using the `RunCommandService` Intent API.

## Troubleshooting

### "Termux not installed" error
- Install Termux from F-Droid (not Google Play)
- Make sure Termux:API and Termux:Tasker are also installed

### Gateway fails to start
- Open Termux manually and check if OpenClaw is installed: `openclaw --version`
- Run the Fresh Install Macro to ensure all dependencies are present
- Check Termux logs: `logcat -s AndroidRuntime Termux`

### Token not detected
- The token appears in the terminal output as: `Gateway auth token: <TOKEN>`
- Check the **Terminal Output** section - if you see the token line, it should be extracted
- If not, copy it manually and paste into the dashboard login

### WebView shows blank page
- Ensure the gateway is running (status = ONLINE)
- Check if port 18789 is listening: `curl http://127.0.0.1:18789` in Termux
- Try manually opening `http://127.0.0.1:18789` in Chrome to verify the gateway is accessible

## Tech Stack

- **Language**: Kotlin
- **UI**: Material Design 3
- **Min SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 14 (API 34)
- **Architecture**: Service-based with WorkManager for background tasks
- **Security**: EncryptedSharedPreferences for credential storage

## License

This app is a custom automation tool for OpenClaw. Refer to the OpenClaw project for licensing information.

## Credits

Built for automating the OpenClaw AI agent in the Termux environment on Android.
