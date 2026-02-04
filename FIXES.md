# Common Build Errors and Fixes

## ✅ FIXED: Launcher Icon Error

**Error:**
```
AAPT: error: resource mipmap/ic_launcher not found
```

**Solution Applied:**
- Created launcher icons for all screen densities (mdpi, hdpi, xhdpi, xxhdpi, xxxhdpi)
- Added both PNG icons and vector drawables
- Simplified AndroidManifest.xml

**This is now fixed in the latest archive!**

---

## Other Common Errors You Might See

### 1. "SDK location not found"

**Error:**
```
SDK location not found. Define location with an ANDROID_SDK_ROOT environment variable or by setting the sdk.dir path in your project's local properties file
```

**Fix:**
Create a file called `local.properties` in the project root:
```bash
cd ~/AndroidStudioProjects/Praxis
echo "sdk.dir=/home/$USER/Android/Sdk" > local.properties
```

Or find your SDK path in Android Studio:
- File → Project Structure → SDK Location
- Copy that path and create the file manually

---

### 2. "Gradle sync failed"

**Error:**
```
Could not resolve all dependencies for configuration ':app:debugCompileClasspath'
```

**Fix:**
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/

# In Android Studio:
File → Invalidate Caches → Invalidate and Restart
```

Then sync again (File → Sync Project with Gradle Files)

---

### 3. "Compilation failed; see the compiler error output for details"

**Error:**
Various Kotlin compilation errors

**Possible Fixes:**

**A. Version mismatch:**
Check `app/build.gradle.kts`:
```kotlin
// Make sure these versions match:
composeOptions {
    kotlinCompilerExtensionVersion = "1.5.4"
}

// And in project build.gradle.kts:
kotlin("android") version "1.9.20"
```

**B. Clean and rebuild:**
```
Build → Clean Project
Build → Rebuild Project
```

**C. Invalidate caches:**
```
File → Invalidate Caches → Invalidate and Restart
```

---

### 4. "Manifest merger failed"

**Error:**
```
Manifest merger failed with multiple errors
```

**Fix:**
Check `AndroidManifest.xml` for:
- Missing closing tags
- Duplicate attributes
- Invalid XML structure

The provided manifest is clean, but if you modify it, ensure valid XML.

---

### 5. "Cannot resolve symbol" errors in code

**Error:**
Red underlines in code, "Unresolved reference"

**Fix:**
```bash
# Force Gradle sync
File → Sync Project with Gradle Files

# If that doesn't work:
File → Invalidate Caches → Invalidate and Restart

# Nuclear option:
rm -rf .gradle/
rm -rf .idea/
# Then reopen project in Android Studio
```

---

### 6. Emulator won't start

**Error:**
```
The emulator process for AVD was killed
```

**Fix:**

**A. Enable virtualization (KVM):**
```bash
# Check if KVM is available
egrep -c '(vmx|svm)' /proc/cpuinfo
# Should return a number > 0

# Install KVM
sudo apt install qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils

# Add yourself to kvm group
sudo usermod -aG kvm $USER

# Restart computer
sudo reboot
```

**B. Increase emulator RAM:**
- In Device Manager, edit your AVD
- Reduce RAM to 2048 MB or 1536 MB
- Enable "Use Host GPU"

**C. Try a different system image:**
- Use x86_64 (not ARM)
- Use API 30 or 31 (instead of 33/34)

---

### 7. "Out of memory" during build

**Error:**
```
Expiring Daemon because JVM heap space is exhausted
```

**Fix:**
Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -Dfile.encoding=UTF-8
```

Or if that's too much:
```properties
org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
```

---

### 8. App crashes immediately on launch

**Check:**

**A. Logcat for errors:**
- Open Logcat in Android Studio (bottom tab)
- Look for red error messages
- Look for "FATAL EXCEPTION"

**B. Common causes:**
- Missing permissions in manifest
- Null pointer exceptions
- Resource not found

**C. Debug:**
- Add breakpoint in `MainActivity.onCreate()`
- Run in debug mode (bug icon)
- Step through code

---

## Building From Scratch

If you want to start fresh:

```bash
# 1. Delete build artifacts
rm -rf app/build/
rm -rf build/
rm -rf .gradle/

# 2. Clean Gradle cache
rm -rf ~/.gradle/caches/

# 3. Reopen in Android Studio
File → Sync Project with Gradle Files

# 4. Rebuild
Build → Rebuild Project
```

---

## Getting More Help

### Check Logcat
Always check Android Studio's Logcat (bottom panel) for detailed errors.

### Error Messages
Copy the full error message and Google it with "android studio"

### Stack Overflow
Most Android build errors have been solved on Stack Overflow

### Community
- r/androiddev on Reddit
- Android Developers Discord
- Stack Overflow (android tag)

---

## Quick Checklist Before Asking for Help

Before asking for help, try these:

- [ ] File → Sync Project with Gradle Files
- [ ] Build → Clean Project
- [ ] Build → Rebuild Project  
- [ ] File → Invalidate Caches → Restart
- [ ] Check Logcat for actual error message
- [ ] Google the exact error message
- [ ] Check SDK path in Project Structure
- [ ] Verify internet connection (for dependencies)
- [ ] Try on a different emulator/device
- [ ] Restart Android Studio
- [ ] Restart computer (seriously, sometimes it helps)

---

## Prevention Tips

1. **Always use Gradle sync** after changing dependencies
2. **Don't ignore warnings** - they often predict errors
3. **Keep Android Studio updated** to latest stable version
4. **Use version control** (git) so you can revert changes
5. **Make small changes** and test frequently
6. **Read error messages carefully** - they usually tell you what's wrong

---

## Working Configuration (Reference)

This is the working setup:

**Environment:**
- OS: Debian 13 / Ubuntu / Linux
- Android Studio: Electric Eel or newer
- JDK: 11 or higher
- Gradle: 8.2
- Kotlin: 1.9.20
- Min SDK: 26 (Android 8.0)
- Target SDK: 34 (Android 14)

**Dependencies versions that work:**
```kotlin
compose-bom: 2023.10.01
kotlin: 1.9.20
compose-compiler: 1.5.4
```

If you're getting errors, verify your versions match these.

---

## Still Stuck?

If none of these fixes work:

1. Copy the **exact error message** from Logcat or Build output
2. Copy the **last 50 lines** of the stack trace
3. Note which **step** you're at (Gradle sync? Build? Run?)
4. Post on r/androiddev with:
   - Error message
   - What you've tried
   - Your Android Studio version
   - Your OS

Good luck! The app works - we just need to get your environment set up correctly! 💪
