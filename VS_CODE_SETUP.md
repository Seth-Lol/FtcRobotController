# Run FTC Robot Controller from VS Code (Instead of Android Studio)

This guide explains how to build and deploy this FTC SDK project from VS Code on Windows.

## What this does

- Builds the Android app with Gradle from VS Code.
- Installs the app to a connected Control Hub / Robot Controller phone with ADB.
- Lets you keep writing OpModes in TeamCode while using VS Code as your main editor.

Note: The OpMode still runs on the Robot Controller device, not on your PC.

## Prerequisites

1. Git clone of the FTC repo (this project).
2. Java/JDK installed (compatible with this SDK and Android Gradle plugin).
3. Android platform tools installed (`adb` available in terminal PATH).
4. A connected Robot Controller device (Control Hub or phone), visible in `adb devices`.

## Open the project in VS Code

1. Open the folder root: `FtcRobotController`.
2. Confirm these files exist at root:
   - `gradlew.bat`
   - `settings.gradle`
   - `TeamCode/`
   - `FtcRobotController/`

## Build from VS Code terminal

Use the integrated terminal in the project root:

```powershell
.\gradlew.bat :TeamCode:assembleDebug
```

If successful, Gradle generates the debug APK for TeamCode.

## Install to device from VS Code terminal

```powershell
.\gradlew.bat :TeamCode:installDebug
```

If the device is connected and authorized, the app installs to the Robot Controller.

## Use the VS Code Tasks (recommended)

This repo includes [ .vscode/tasks.json ](./.vscode/tasks.json) with ready tasks:

- `FTC: Clean`
- `FTC: Assemble Debug (TeamCode)`
- `FTC: Install Debug (TeamCode)`
- `FTC: Build + Install Debug (TeamCode)`
- `FTC: ADB Devices`
- `FTC: Logcat (RobotController)`

Run them from:

1. `Ctrl+Shift+P`
2. `Tasks: Run Task`
3. Pick a task

Typical workflow:

1. Run `FTC: ADB Devices` (confirm your device appears)
2. Run `FTC: Build + Install Debug (TeamCode)`
3. Run `FTC: Logcat (RobotController)` when debugging

## Run your OpMode after deploy

1. Open Robot Controller app on the device.
2. Connect Driver Station.
3. Select your OpMode (for example `4M Tank Drive`).
4. Press `INIT`, then `START`.

## Common issues

### 1) Signature mismatch on install

Error example:

`INSTALL_FAILED_UPDATE_INCOMPATIBLE`

Cause: The device already has `com.qualcomm.ftcrobotcontroller` signed with a different key.

Fix:

```powershell
adb uninstall com.qualcomm.ftcrobotcontroller
.\gradlew.bat :TeamCode:installDebug
```

### 2) Device not found

Check:

```powershell
adb devices
```

If no device appears:

- Check USB cable/connection.
- Accept RSA authorization prompt on device.
- Restart ADB:

```powershell
adb kill-server
adb start-server
adb devices
```

### 3) Java source/target 8 warnings

You may see warnings about Java 8 being obsolete with newer JDKs. For FTC SDK this is usually expected and not a build blocker as long as build succeeds.

## Optional: Build all modules

```powershell
.\gradlew.bat build
```

This is slower than module-specific commands, but useful for full verification.

## Quick command reference

```powershell
# Build TeamCode debug APK
.\gradlew.bat :TeamCode:assembleDebug

# Install TeamCode debug APK
.\gradlew.bat :TeamCode:installDebug

# List connected devices
adb devices

# FTC-focused logs
adb logcat -s RobotCore:D UsbManager:D FtcRobotController:D *:S
```

## Summary

You can use VS Code as your day-to-day FTC development environment by building and deploying through Gradle + ADB. Android Studio is not required for normal code/build/deploy flow once your environment is set up.
