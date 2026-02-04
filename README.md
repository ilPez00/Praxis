# Praxis - A Goal-Aligned Social Operating System

**"Goals unite us, rigor guides us, collaboration transforms us."**

Praxis is a revolutionary social platform that connects people through shared ambitions and measurable progress. Unlike traditional social networks, Praxis creates bonds based on aligned goals, disciplined engagement, and authentic development.

## 🎯 What Makes Praxis Different?

- **Goal-Centric Matching**: AI algorithm matches users based on goal alignment, not superficial attributes
- **Dynamic Goal Trees**: Self-evolving priority system that adapts to your progress
- **Autopoietic Feedback**: Post-collaboration grading recalibrates goal weights automatically
- **Focused Collaboration**: Direct messaging channels dedicated to specific goals (no feeds, no distractions)
- **Multi-Domain Support**: Career, Fitness, Mental Health, Philosophy, and more

## 📱 Current MVP Features

### ✅ Implemented
- **Onboarding Flow**: Name, age, bio collection
- **Goal Selection**: Guided tree-based selection (up to 3 goals free tier)
- **Matching Algorithm**: SAB compatibility scoring from whitepaper
- **Matches View**: See compatible users with shared goals
- **Profile View**: Visual goal tree with progress tracking
- **Direct Messaging**: Goal-focused chat interface
- **Grading System**: Post-collaboration feedback to update weights

### 🚧 Coming Soon (Post-MVP)
- Identity verification (facial scanning)
- Real-time progress tracking
- Backend API integration
- Advanced analytics dashboard
- Premium features (unlimited goals, AI coaching)
- Job marketplace integration

## 🛠️ Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture**: MVVM with StateFlow
- **Current Storage**: In-memory (MockRepository)
- **Future Backend**: REST API (to be implemented)

## 📋 Prerequisites

Before you begin, make sure you have:

1. **Android Studio** (Latest version recommended - Electric Eel or newer)
   - Download from: https://developer.android.com/studio

2. **JDK 8 or higher**
   - Usually comes with Android Studio
   - Check: `java -version`

3. **Android SDK**
   - Will be installed via Android Studio
   - Minimum SDK: 26 (Android 8.0)
   - Target SDK: 34 (Android 14)

4. **Git** (for version control)
   - Check: `git --version`

## 🚀 Setup Instructions (Debian 13)

### Step 1: Install Android Studio

```bash
# Download Android Studio
cd ~/Downloads
wget https://redirector.gvt1.com/edgedl/android/studio/ide-zips/2023.1.1.26/android-studio-2023.1.1.26-linux.tar.gz

# Extract
sudo tar -xzf android-studio-*.tar.gz -C /opt/

# Run Android Studio
/opt/android-studio/bin/studio.sh
```

During first launch:
- Select "Standard" installation
- Accept all SDK licenses
- Let it download necessary SDK components

### Step 2: Set Up Environment Variables

```bash
# Edit your .bashrc or .zshrc
nano ~/.bashrc

# Add these lines (adjust paths as needed)
export ANDROID_HOME=$HOME/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/emulator
export PATH=$PATH:$ANDROID_HOME/platform-tools

# Reload
source ~/.bashrc
```

### Step 3: Open Project in Android Studio

```bash
# Navigate to project directory
cd /path/to/Praxis

# Open Android Studio
/opt/android-studio/bin/studio.sh .
```

OR:
1. Open Android Studio
2. Click "Open" 
3. Navigate to `/home/claude/Praxis`
4. Click "OK"

### Step 4: Gradle Sync

Android Studio will automatically:
- Download required Gradle version
- Download all dependencies (this takes a few minutes first time)
- Index the project

If sync fails:
- Click "Try Again"
- Check "File → Project Structure" - SDK should be set to API 34
- Click "Tools → SDK Manager" - ensure Android 13 (API 34) is installed

### Step 5: Set Up Emulator or Device

**Option A: Use Android Emulator**
1. Click "Device Manager" in Android Studio (phone icon in top toolbar)
2. Click "Create Device"
3. Select "Pixel 5" (or any modern device)
4. Click "Next"
5. Download "Tiramisu" (API 33) or "UpsideDownCake" (API 34)
6. Click "Next" → "Finish"

**Option B: Use Physical Device**
1. Enable Developer Options on your phone:
   - Go to Settings → About Phone
   - Tap "Build Number" 7 times
2. Enable USB Debugging:
   - Settings → Developer Options → USB Debugging
3. Connect phone via USB
4. Accept "Allow USB Debugging" prompt

### Step 6: Run the App!

1. Select your device/emulator from the device dropdown (top toolbar)
2. Click the green "Run" button (▶️) or press Shift+F10
3. Wait for build to complete
4. App will launch on device!

## 📁 Project Structure

```
Praxis/
├── app/
│   ├── src/main/
│   │   ├── java/com/praxis/app/
│   │   │   ├── data/
│   │   │   │   ├── model/         # Data classes (User, GoalNode, Match)
│   │   │   │   ├── repository/    # MockRepository (will become API)
│   │   │   │   └── matching/      # Matching algorithm
│   │   │   ├── ui/
│   │   │   │   ├── screens/       # All UI screens
│   │   │   │   ├── theme/         # Material 3 theme
│   │   │   │   ├── navigation/    # Navigation setup
│   │   │   │   └── viewmodel/     # PraxisViewModel
│   │   │   └── MainActivity.kt    # Entry point
│   │   ├── res/                   # Resources (strings, themes)
│   │   └── AndroidManifest.xml    # App configuration
│   └── build.gradle.kts           # App dependencies
├── build.gradle.kts               # Project Gradle config
├── settings.gradle.kts
└── README.md
```

## 🧪 Testing the App

### Test Flow:
1. **Onboarding**: Enter your name, age, and bio
2. **Goal Selection**: Pick 3 goals from different domains
3. **Home Screen**: You'll see the Matches and Profile tabs
4. **Find Matches**: Tap the search icon to run the matching algorithm
5. **View Matches**: See mock users with compatibility scores
6. **Open Chat**: Tap a match to open goal-focused DM
7. **Grade Collaboration**: Tap "Complete & Grade" to provide feedback
8. **Check Profile**: View your goal tree and updated weights

## 🐛 Common Issues & Solutions

### Issue: Gradle sync fails
**Solution**: 
```bash
# Clear Gradle cache
rm -rf ~/.gradle/caches/
# In Android Studio: File → Invalidate Caches → Invalidate and Restart
```

### Issue: "SDK location not found"
**Solution**:
Create `local.properties` in project root:
```
sdk.dir=/home/YOUR_USERNAME/Android/Sdk
```

### Issue: Build errors about Kotlin version
**Solution**: 
Check that `build.gradle.kts` has matching Kotlin/Compose versions:
```kotlin
kotlin("android") version "1.9.20"
kotlinCompilerExtensionVersion = "1.5.4"
```

### Issue: Emulator won't start
**Solution**:
```bash
# Check virtualization
egrep -c '(vmx|svm)' /proc/cpuinfo  # Should be > 0

# Install KVM
sudo apt install qemu-kvm libvirt-daemon-system libvirt-clients bridge-utils
sudo usermod -aG kvm $USER
# Reboot
```

## 🎓 Learning Kotlin/Android

Since you're new to Kotlin/Android, here are key concepts:

### Kotlin Basics (used in Praxis)
- **Data classes**: Immutable data holders (e.g., `User`, `GoalNode`)
- **Sealed classes**: Restricted class hierarchies (e.g., `PraxisUiState`)
- **StateFlow**: Reactive state management
- **Coroutines**: Async operations (`viewModelScope.launch`)

### Jetpack Compose Basics
- **@Composable**: Functions that describe UI
- **State**: `remember`, `mutableStateOf` for UI state
- **Modifiers**: `.fillMaxSize()`, `.padding()` for styling
- **Material 3**: Pre-built components (`Button`, `Card`, `TextField`)

### Files to Study First:
1. `GoalNode.kt` - Understand the core data structure
2. `MatchingEngine.kt` - See the algorithm in action
3. `OnboardingScreen.kt` - Simple Compose UI example
4. `PraxisViewModel.kt` - How app state is managed

## 🔄 Next Steps (Your Roadmap)

### Phase 1: Familiarize (Week 1)
- [ ] Run the app successfully
- [ ] Navigate through all screens
- [ ] Understand the data flow (User → Goals → Matches)
- [ ] Read through code comments

### Phase 2: Customize (Week 2)
- [ ] Change app colors in `Theme.kt`
- [ ] Add more goal templates in `MockRepository.kt`
- [ ] Modify grading descriptions in `GradingScreen.kt`
- [ ] Tweak matching algorithm parameters

### Phase 3: Enhance (Week 3-4)
- [ ] Add progress update feature
- [ ] Implement goal deletion
- [ ] Add search/filter to matches
- [ ] Create streak tracking system

### Phase 4: Backend (Month 2)
- [ ] Set up Firebase/Supabase
- [ ] Replace MockRepository with real API calls
- [ ] Implement user authentication
- [ ] Add cloud storage for user data

## 📚 Resources

### Official Docs
- [Kotlin Basics](https://kotlinlang.org/docs/basic-syntax.html)
- [Jetpack Compose Tutorial](https://developer.android.com/jetpack/compose/tutorial)
- [Android Basics with Compose](https://developer.android.com/courses/android-basics-compose/course)

### Recommended YouTube Channels
- Philipp Lackner (Android/Kotlin)
- CodingWithMitch
- Stevdza-San

### Communities
- r/androiddev
- r/Kotlin
- Kotlin Slack

## 🤝 Contributing (Future)

Once MVP is stable:
1. Fork the repository
2. Create feature branch
3. Make changes
4. Test thoroughly
5. Submit pull request

## 📄 License

[To be determined]

## 📞 Support

For issues or questions:
- Check "Common Issues" section above
- Review code comments
- Study the whitepaper (`Praxis_Whitepaper.pdf`)

## 🎉 You Did It!

You now have a working Praxis MVP! This is a complete, functional Android app that implements the core concepts from your whitepaper. Every feature is documented and ready for you to learn from and build upon.

**Remember**: You don't need to understand everything at once. Start by running it, playing with it, and gradually diving deeper into the code. The architecture is solid and ready for expansion.

**Welcome to the journey of building Praxis! 🚀**
