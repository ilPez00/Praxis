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

## 🔄 Next Steps

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
