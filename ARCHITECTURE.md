# Praxis Architecture Guide

## Overview

Praxis follows the **MVVM (Model-View-ViewModel)** architecture pattern with Jetpack Compose for UI. This document explains how all the pieces fit together.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────┐
│                         View Layer                      │
│                    (Jetpack Compose)                    │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌────────┐ │
│  │Onboarding│  │   Goal   │  │  Matches │  │ Profile│ │
│  │  Screen  │→ │Selection │→ │  Screen  │  │ Screen │ │
│  └──────────┘  └──────────┘  └──────────┘  └────────┘ │
│         ↓             ↓             ↓            ↓       │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                    ViewModel Layer                      │
│              ┌──────────────────────┐                   │
│              │   PraxisViewModel    │                   │
│              │  - UI State          │                   │
│              │  - Business Logic    │                   │
│              │  - Event Handling    │                   │
│              └──────────────────────┘                   │
└─────────────────────────────────────────────────────────┘
                         ↓
┌─────────────────────────────────────────────────────────┐
│                  Data/Domain Layer                      │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐ │
│  │   Models     │  │MatchingEngine│  │  Repository  │ │
│  │ - User       │  │ - SAB Score  │  │ - Mock Data  │ │
│  │ - GoalNode   │  │ - Algorithm  │  │ - CRUD Ops   │ │
│  │ - Match      │  │              │  │              │ │
│  └──────────────┘  └──────────────┘  └──────────────┘ │
└─────────────────────────────────────────────────────────┘
```

## Layer Breakdown

### 1. View Layer (UI)

**Location**: `app/src/main/java/com/praxis/app/ui/screens/`

**Purpose**: Display UI and handle user interactions

**Key Files**:
- `OnboardingScreen.kt` - Collects user info
- `GoalSelectionScreen.kt` - Builds goal tree
- `HomeScreen.kt` - Main container with bottom nav
- `MatchesScreen.kt` - Shows compatible users
- `ProfileScreen.kt` - Displays user's goal tree
- `ChatScreen.kt` - Goal-focused messaging
- `GradingScreen.kt` - Post-collaboration feedback

**Technology**: Jetpack Compose (declarative UI)

**How It Works**:
```kotlin
@Composable
fun MyScreen(viewModel: PraxisViewModel) {
    // Observe state from ViewModel
    val state by viewModel.uiState.collectAsState()
    
    // Render UI based on state
    when (state) {
        is Loading -> ShowLoadingSpinner()
        is Success -> ShowContent()
        is Error -> ShowError()
    }
    
    // User interaction triggers ViewModel method
    Button(onClick = { viewModel.doSomething() }) {
        Text("Click")
    }
}
```

**Principle**: Views are **dumb** - they just display data and forward events. No business logic here!

---

### 2. ViewModel Layer

**Location**: `app/src/main/java/com/praxis/app/ui/viewmodel/`

**Purpose**: Manage UI state and handle business logic

**Key File**: `PraxisViewModel.kt`

**Responsibilities**:
1. Hold UI state (`StateFlow`)
2. Expose data to UI
3. Handle user events
4. Call repository/domain methods
5. Transform data for UI

**State Management**:
```kotlin
class PraxisViewModel : ViewModel() {
    // Private mutable state
    private val _uiState = MutableStateFlow<PraxisUiState>(Onboarding)
    
    // Public read-only state for UI
    val uiState: StateFlow<PraxisUiState> = _uiState.asStateFlow()
    
    // Handle user action
    fun findMatches() {
        viewModelScope.launch {  // Coroutine for async work
            val user = currentUser.value ?: return@launch
            val matches = matchingEngine.findMatches(user, candidates)
            _uiState.value = Home  // Update UI state
        }
    }
}
```

**Why StateFlow?**
- Reactive: UI automatically updates when state changes
- Lifecycle-aware: Survives screen rotation
- Thread-safe: Works with coroutines

**Sealed Classes for State**:
```kotlin
sealed class PraxisUiState {
    object Onboarding : PraxisUiState()
    object GoalSelection : PraxisUiState()
    object Home : PraxisUiState()
    data class Chat(val matchId: String) : PraxisUiState()
}
```

This ensures only valid states exist - no invalid combinations!

---

### 3. Data/Domain Layer

**Location**: `app/src/main/java/com/praxis/app/data/`

**Purpose**: Manage data and implement business logic

#### 3A. Models

**Location**: `data/model/`

**Files**:
- `GoalNode.kt` - Core goal tree structure
- `User.kt` - User profile and goal tree
- `Match.kt` - Compatibility results
- `Collaboration.kt` - Session tracking

**Example**:
```kotlin
data class GoalNode(
    val id: String,
    val domain: Domain,
    val name: String,
    var weight: Double = 1.0,  // Mutable for autopoietic updates
    var progress: Int = 0,
    val subGoals: MutableList<GoalNode> = mutableListOf()
) {
    // Business logic lives with the data
    fun updateWeightFromGrade(grade: FeedbackGrade) {
        weight = when (grade) {
            FeedbackGrade.SUCCEEDED -> weight * 0.8  // Easier
            FeedbackGrade.DISTRACTED -> weight * 1.2 // Harder
            // ...
        }
    }
}
```

**Principle**: Data classes + behavior = domain models

#### 3B. Repository

**Location**: `data/repository/`

**File**: `MockRepository.kt`

**Purpose**: Abstract data access (currently in-memory, will become API)

**Pattern**:
```kotlin
class MockRepository {
    private var currentUser: User? = null
    private val allUsers = mutableListOf<User>()
    
    fun getCurrentUser(): User? = currentUser
    fun setCurrentUser(user: User) { currentUser = user }
    
    // In production:
    // suspend fun getCurrentUser(): User = api.getUser()
    // suspend fun updateUser(user: User) = api.updateUser(user)
}
```

**Why Repository Pattern?**
- Single source of truth
- Easy to swap implementations (mock → real API)
- Testable (can inject fake repository)
- Clean separation of concerns

#### 3C. Matching Engine

**Location**: `data/matching/`

**File**: `MatchingEngine.kt`

**Purpose**: Implement the SAB compatibility algorithm from whitepaper

**Algorithm**:
```kotlin
SAB = Σ Σ δ(gi, gj) · sim(i, j) · Wi · Wj
      i j
─────────────────────────────────────────
         Σ Wi · Σ Wj
         i      j

Where:
- δ = 1 if domains match, 0 otherwise
- sim(i, j) = progress similarity (0-1)
- Wi, Wj = goal weights
```

**Implementation**:
```kotlin
fun computeMatch(userA: User, userB: User): Match? {
    var totalScore = 0.0
    
    // Nested loop over all goals
    for (nodeA in userA.getAllGoals()) {
        for (nodeB in userB.getAllGoals()) {
            if (nodeA.domain == nodeB.domain) {  // δ
                val similarity = computeSimilarity(nodeA, nodeB)  // sim
                totalScore += similarity * nodeA.weight * nodeB.weight
            }
        }
    }
    
    // Normalize
    val normalizedScore = totalScore / (sumWeightsA * sumWeightsB)
    return Match(userB.id, normalizedScore, sharedGoals)
}
```

---

## Data Flow Examples

### Example 1: Finding Matches

```
1. User taps search icon
   ↓
2. MatchesScreen calls viewModel.findMatches()
   ↓
3. ViewModel:
   - Gets current user
   - Gets candidates from repository
   - Calls matchingEngine.findMatches()
   ↓
4. MatchingEngine:
   - Computes SAB score for each candidate
   - Returns sorted list of matches
   ↓
5. ViewModel:
   - Stores matches in repository
   - Updates _matches StateFlow
   ↓
6. MatchesScreen:
   - Observes matches StateFlow
   - UI automatically re-renders with new matches
```

### Example 2: Grading Collaboration

```
1. User selects grade in GradingDialog
   ↓
2. Dialog calls viewModel.gradeCollaboration(id, grade)
   ↓
3. ViewModel:
   - Calls repository.completeCollaboration()
   ↓
4. Repository:
   - Finds the collaboration
   - Finds the goal node
   - Calls goalNode.updateWeightFromGrade(grade)
   ↓
5. GoalNode:
   - Adjusts weight based on grade
   - Weight *= factor (0.8 for success, 1.5 for noob)
   ↓
6. ViewModel:
   - Fetches updated user
   - Updates _currentUser StateFlow
   ↓
7. ProfileScreen:
   - Observes currentUser
   - Shows updated weight in goal tree
```

---

## Key Design Patterns

### 1. Observer Pattern (via StateFlow)

```kotlin
// ViewModel exposes data
val currentUser: StateFlow<User?>

// UI observes data
val user by viewModel.currentUser.collectAsState()

// When data changes, UI updates automatically
```

### 2. Repository Pattern

```kotlin
interface UserRepository {
    suspend fun getUser(id: String): User
    suspend fun updateUser(user: User)
}

// Mock for testing
class MockRepository : UserRepository { ... }

// Real for production
class ApiRepository : UserRepository { ... }
```

### 3. Sealed Classes for State

```kotlin
sealed class Result<T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error<T>(val message: String) : Result<T>()
    object Loading : Result<Nothing>()
}
```

Impossible to have invalid states!

### 4. Single Responsibility

Each class has ONE job:
- `GoalNode` = represent a goal
- `MatchingEngine` = compute compatibility
- `PraxisViewModel` = manage UI state
- `MockRepository` = provide data

### 5. Dependency Injection (Manual for MVP)

```kotlin
class PraxisViewModel {
    private val repository = MockRepository()  // ← Could be injected
    private val matchingEngine = MatchingEngine()
}

// In production:
class PraxisViewModel(
    private val repository: UserRepository,  // Interface!
    private val matchingEngine: MatchingEngine
)
```

---

## File Dependencies

### Who Depends on Whom?

```
MainActivity
    ↓ uses
PraxisViewModel
    ↓ uses
MockRepository, MatchingEngine
    ↓ use
User, GoalNode, Match
```

**Rule**: Dependencies point downward (high-level → low-level)

**No circular dependencies**: If A uses B, B cannot use A

---

## Migration Path: Mock → Real Backend

### Current (MVP):
```kotlin
class MockRepository {
    private val users = mutableListOf<User>()
    
    fun getUser(id: String): User? = users.find { it.id == id }
}
```

### Future (Production):
```kotlin
class ApiRepository(private val api: PraxisApi) {
    suspend fun getUser(id: String): User {
        return api.getUser(id)  // Network call
    }
}

interface PraxisApi {
    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: String): User
}
```

### ViewModel doesn't change!
```kotlin
// Works with both Mock and Api repositories
class PraxisViewModel(private val repo: Repository) {
    fun loadUser() {
        viewModelScope.launch {
            val user = repo.getUser("123")
            _currentUser.value = user
        }
    }
}
```

This is the power of abstraction!

---

## Testing Strategy

### Unit Tests (Fast)
Test logic in isolation:
```kotlin
@Test
fun `matching algorithm returns highest compatibility first`() {
    val userA = createUser(goals = listOf("Fitness"))
    val userB = createUser(goals = listOf("Fitness"))  // High match
    val userC = createUser(goals = listOf("Career"))   // No match
    
    val matches = engine.findMatches(userA, listOf(userB, userC))
    
    assertEquals(userB.id, matches.first().userId)
}
```

### Integration Tests
Test component interactions:
```kotlin
@Test
fun `viewmodel updates matches after finding them`() {
    val viewModel = PraxisViewModel()
    viewModel.createUser("Test", 25, "Bio")
    
    viewModel.findMatches()
    
    assertTrue(viewModel.matches.value.isNotEmpty())
}
```

### UI Tests (Slow)
Test full user flows:
```kotlin
@Test
fun onboarding_to_matches_flow() {
    composeTestRule.onNodeWithText("Continue").performClick()
    composeTestRule.onNodeWithText("Fitness").performClick()
    // ...
}
```

---

## Performance Considerations

### Current Bottlenecks:
1. **Matching Algorithm**: O(n²) complexity
   - For 1000 users: 1,000,000 comparisons
   - Solution: Backend filtering, caching

2. **Goal Tree Traversal**: Recursive
   - Deep trees = slow
   - Solution: Limit depth, memoization

3. **No Pagination**: Loads all matches
   - Solution: Lazy loading, virtual lists

### Optimizations for Production:
1. Compute matches on backend
2. Cache match results
3. Paginate large lists
4. Use background workers for updates
5. Implement proper loading states

---

## Security Notes

### Current State (MVP):
- ⚠️ No authentication
- ⚠️ No encryption
- ⚠️ Data in memory only
- ⚠️ No input validation

### Production Requirements:
1. **Auth**: Firebase Auth, OAuth
2. **Encryption**: HTTPS, encrypted storage
3. **Validation**: Server-side input checks
4. **Privacy**: Anonymous grading, data deletion
5. **Rate Limiting**: Prevent spam/abuse

---

## Scalability Plan

### MVP (Current):
- In-memory storage
- Single-threaded operations
- Mock users only

### Phase 1 (100 users):
- Firebase Realtime DB
- Simple API calls
- Manual matching

### Phase 2 (10,000 users):
- PostgreSQL database
- REST API with caching
- Background match computation
- Redis for sessions

### Phase 3 (100,000+ users):
- Microservices architecture
- GraphQL for flexible queries
- ML-enhanced matching
- Distributed caching
- Load balancing

---

## Summary

**Praxis Architecture** = Clean, Scalable, Testable

**Key Principles**:
1. Separation of concerns (View / ViewModel / Data)
2. Single responsibility per class
3. Reactive state management
4. Repository abstraction for easy migration
5. Domain logic in models

**Why This Matters**:
- Easy to understand (even for beginners)
- Easy to test (mock dependencies)
- Easy to extend (add features without breaking)
- Easy to scale (swap implementations)

**Next Steps**:
1. Study one flow end-to-end (e.g., matching)
2. Trace code from UI → ViewModel → Repository
3. Modify something small
4. See how changes propagate

You now understand the entire system! 🎉
