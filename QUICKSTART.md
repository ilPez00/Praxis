# 🚀 Praxis Quick Start Guide

## For Complete Beginners (Zero Kotlin/Android Experience)

### Prerequisites Check
```bash
# Check if you have Java
java -version  # Should show version 8 or higher

# Check if you have Android Studio
# If not, install it first (see main README)
```

## 5-Minute Setup

### Step 1: Copy Project to Your Machine
```bash
# The project is in /home/claude/Praxis
# Copy it to your home directory
cp -r /home/claude/Praxis ~/Praxis
cd ~/Praxis
```

### Step 2: Open in Android Studio
```bash
# Option 1: Command line
/opt/android-studio/bin/studio.sh ~/Praxis

# Option 2: GUI
# Open Android Studio → File → Open → Select ~/Praxis
```

### Step 3: Wait for Gradle Sync
- Android Studio will automatically download dependencies
- This takes 5-10 minutes the first time
- You'll see a progress bar at the bottom
- ☕ Grab coffee while it works!

### Step 4: Set Up a Device

**Easiest: Use Emulator**
1. Click the phone icon (📱) in the toolbar
2. Click "Create Device"
3. Select "Pixel 5"
4. Click "Next"
5. Select "Tiramisu" (API 33)
6. Click "Finish"

**Alternative: Use Your Phone**
1. On your phone: Settings → About → Tap "Build Number" 7 times
2. Settings → Developer Options → Enable "USB Debugging"
3. Connect phone to computer with USB cable
4. Phone will ask "Allow USB Debugging?" → Tap "Allow"

### Step 5: Run!
1. Select your device from the dropdown (top of Android Studio)
2. Click the green play button ▶️
3. Wait for build (2-5 minutes first time)
4. App launches! 🎉

## What You Should See

### Screen 1: Onboarding
- Enter your name (e.g., "Alex")
- Enter your age (e.g., "25")
- Enter a bio (optional)
- Click "Continue to Goal Selection"

### Screen 2: Goal Selection
- You'll see 9 life domains
- Tap one (e.g., "Fitness")
- Select a specific goal (e.g., "Strength Training")
- Repeat for 2 more goals
- Click "Start Matching"

### Screen 3: Home - Matches Tab
- Initially empty
- Tap the search icon 🔍 at top right
- Algorithm runs and shows 3 mock users
- Each shows compatibility % and shared goals
- Tap a match to open chat

### Screen 4: Chat
- Goal-focused messaging interface
- Type a message, tap send
- Tap "Complete & Grade" when done
- Rate your collaboration partner

### Screen 5: Home - Profile Tab
- See your goal tree
- Each goal shows:
  - Domain badge
  - Progress bar (initially 0%)
  - Priority weight (starts at 1.0x)
- Stats section at bottom

## Common First-Time Issues

### "Gradle Sync Failed"
**Fix**: Click "Try Again" button. If that doesn't work:
```bash
# In Android Studio menu:
File → Invalidate Caches → Invalidate and Restart
```

### "SDK Not Found"
**Fix**: 
```bash
# Create local.properties file in project root
echo "sdk.dir=/home/$USER/Android/Sdk" > ~/Praxis/local.properties
```

### "Cannot Run on Emulator"
**Fix**: Make sure virtualization is enabled:
```bash
# Check if KVM is installed
ls /dev/kvm  # Should exist

# If not:
sudo apt install qemu-kvm
sudo usermod -aG kvm $USER
# Then reboot
```

### "Build Takes Forever"
**First build is always slow!**
- 5-10 minutes is normal
- Subsequent builds: 30 seconds - 2 minutes
- Be patient the first time

## Understanding the Code (Baby Steps)

### Files to Read First (in order):

1. **MainActivity.kt** (Line 20-50)
   - Entry point of the app
   - Sets up the UI
   - Just 70 lines!

2. **GoalNode.kt** (Line 1-80)
   - Defines what a "goal" is
   - Has weight, progress, sub-goals
   - Core data structure

3. **OnboardingScreen.kt** (Line 1-100)
   - First screen users see
   - Shows how Compose UI works
   - Very readable!

4. **MockRepository.kt** (Line 1-150)
   - Fake database for testing
   - Creates 3 mock users
   - Will be replaced with real API later

### Key Concepts (Don't Worry If Confusing at First)

**Data Class**: A container for data
```kotlin
data class User(val name: String, val age: Int)
```

**@Composable**: A function that draws UI
```kotlin
@Composable
fun MyButton() {
    Button(onClick = { }) { Text("Click me") }
}
```

**StateFlow**: Live-updating data
```kotlin
val userName = MutableStateFlow("Alex")  // Can change
userName.value = "Sam"  // UI auto-updates!
```

**ViewModel**: Manages app state and logic
- Holds data that survives screen rotation
- Separates UI from business logic
- One source of truth

## Making Your First Change

### Change 1: Modify App Name
```kotlin
// File: app/src/main/res/values/strings.xml
<string name="app_name">My Praxis</string>
```
Run app → See new name!

### Change 2: Add a New Goal Template
```kotlin
// File: MockRepository.kt (Line ~150)
// Find the goalTemplates map and add:
Domain.FITNESS to listOf(
    "Strength Training", 
    "Cardio", 
    "Yoga",
    "Swimming"  // ← Add this!
)
```
Run app → See "Swimming" in goal selection!

### Change 3: Change Theme Color
```kotlin
// File: ui/theme/Theme.kt (Line 8)
private val PraxisPrimary = Color(0xFF6750A4)  // Purple
// Change to:
private val PraxisPrimary = Color(0xFF2196F3)  // Blue
```
Run app → Everything is blue now!

## Next Steps

### Today:
- ✅ Get app running
- ✅ Navigate through all screens
- ✅ Make one small change

### This Week:
- Read through MainActivity.kt fully
- Understand how navigation works
- Study one screen's code deeply

### Next Week:
- Modify the matching algorithm
- Add a new feature (e.g., goal deletion)
- Learn about Compose UI more

### This Month:
- Plan backend integration
- Research Firebase/Supabase
- Start building real API

## Getting Help

### When Stuck:
1. Read the error message carefully
2. Google: "android studio [error message]"
3. Check Stack Overflow
4. Review the main README.md

### Learning Resources:
- [Kotlin Koans](https://kotlinlang.org/docs/koans.html) - Interactive tutorial
- [Compose Basics](https://developer.android.com/jetpack/compose/tutorial) - Official guide
- YouTube: Search "Android Studio tutorial for beginners"

## You Got This! 💪

Don't be overwhelmed. This is a complete, working app that implements a sophisticated algorithm from your whitepaper. It's OKAY if you don't understand everything immediately.

**The secret**: Focus on ONE thing at a time. Run it, break it, fix it, learn. That's how everyone learns to code.

**Remember**: This app works RIGHT NOW. You can show it to people, test the concept, get feedback. That's huge!

Start with just getting it running. Everything else comes naturally with time and curiosity.

**Welcome to Android development! 🎉**
