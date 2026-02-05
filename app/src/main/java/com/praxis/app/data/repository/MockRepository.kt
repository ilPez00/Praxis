package com.praxis.app.data.repository

import com.praxis.app.data.model.*
import java.util.UUID

/**
 * Mock data repository for MVP
 * In production, this will connect to a real backend API
 * For now, it stores everything in memory
 */
class MockRepository {
    
    // Current logged-in user
    private var currentUser: User? = null
    
    // Mock database of other users
    private val allUsers = mutableListOf<User>()
    
    // User's matches
    private val matches = mutableListOf<Match>()
    
    // Active collaborations
    private val collaborations = mutableListOf<Collaboration>()
    
    init {
        // Create some mock users for testing
        createMockUsers()
    }
    
    /**
     * Creates mock users with various goals for testing
     */
    private fun createMockUsers() {
        // Mock User 1: Fitness + Career focus
        allUsers.add(User(
            name = "Alex",
            age = 28,
            bio = "Software engineer looking to get stronger and advance my career",
            isVerified = true,
            goalTree = mutableListOf(
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.FITNESS,
                    name = "Strength Training",
                    weight = 1.2,
                    progress = 60,
                    subGoals = mutableListOf(
                        GoalNode(UUID.randomUUID().toString(), Domain.FITNESS, "Squat 100kg", progress = 70),
                        GoalNode(UUID.randomUUID().toString(), Domain.FITNESS, "Bench 80kg", progress = 50)
                    )
                ),
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.CAREER,
                    name = "Senior Promotion",
                    weight = 1.0,
                    progress = 40
                )
            )
        ))
        
        // Mock User 2: Fitness + Mental Health
        allUsers.add(User(
            name = "Sam",
            age = 25,
            bio = "Yoga enthusiast and meditation practitioner",
            isVerified = true,
            goalTree = mutableListOf(
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.FITNESS,
                    name = "Cardio Training",
                    weight = 1.3,
                    progress = 55
                ),
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.MENTAL_HEALTH,
                    name = "Daily Meditation",
                    weight = 1.1,
                    progress = 80
                )
            )
        ))
        
        // Mock User 3: Career + Philosophy
        allUsers.add(User(
            name = "Jordan",
            age = 30,
            bio = "Startup founder exploring Stoicism",
            isVerified = false,
            goalTree = mutableListOf(
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.CAREER,
                    name = "Launch Startup",
                    weight = 1.5,
                    progress = 30
                ),
                GoalNode(
                    id = UUID.randomUUID().toString(),
                    domain = Domain.PHILOSOPHY,
                    name = "Study Stoicism",
                    weight = 0.9,
                    progress = 50
                )
            )
        ))
    }
    
    // User Management
    fun getCurrentUser(): User? = currentUser
    
    fun setCurrentUser(user: User) {
        currentUser = user
    }
    
    fun createUser(name: String, age: Int, bio: String): User {
        val user = User(name = name, age = age, bio = bio)
        currentUser = user
        return user
    }
    
    fun updateUserGoals(goals: MutableList<GoalNode>) {
        currentUser?.goalTree?.clear()
        currentUser?.goalTree?.addAll(goals)
    }
    
    // Matching
    fun getAllUsers(): List<User> = allUsers
    
    fun getMatches(): List<Match> = matches
    
    fun addMatch(match: Match) {
        matches.add(match)
    }
    
    fun clearMatches() {
        matches.clear()
    }
    
    // Collaborations
    fun getCollaborations(): List<Collaboration> = collaborations
    
    fun createCollaboration(matchId: String, goalId: String): Collaboration {
        val collab = Collaboration(matchId = matchId, goalId = goalId)
        collaborations.add(collab)
        return collab
    }
    
    fun completeCollaboration(collabId: String, myGrade: FeedbackGrade) {
        collaborations.find { it.id == collabId }?.let {
            it.completedAt = System.currentTimeMillis()
            it.myGrade = myGrade
            
            // Update goal weights based on feedback
            currentUser?.getAllGoals()?.find { goal -> goal.id == it.goalId }?.let { goal ->
                goal.updateWeightFromGrade(myGrade)
            }
        }
    }
    
    // Predefined goal templates for onboarding with 4-level hierarchy
    fun getGoalTemplates(): Map<Domain, List<GoalCategory>> = mapOf(
        Domain.FITNESS to listOf(
            GoalCategory("🏋️", "Strength & Muscle", listOf(
                GoalSubcategory("💪", "Weight Training", listOf(
                    SpecificGoal("🏋️‍♂️", "Powerlifting"),
                    SpecificGoal("🦾", "Bodybuilding"),
                    SpecificGoal("⚡", "CrossFit")
                )),
                GoalSubcategory("🤸", "Bodyweight", listOf(
                    SpecificGoal("🧘", "Calisthenics"),
                    SpecificGoal("🤸‍♀️", "Gymnastics")
                ))
            )),
            GoalCategory("🏃", "Cardio & Endurance", listOf(
                GoalSubcategory("🏃‍♂️", "Running", listOf(
                    SpecificGoal("🎽", "Marathon Training"),
                    SpecificGoal("🏅", "Sprint Training"),
                    SpecificGoal("⛰️", "Trail Running")
                )),
                GoalSubcategory("🚴", "Cycling", listOf(
                    SpecificGoal("🚵", "Mountain Biking"),
                    SpecificGoal("🚴‍♀️", "Road Cycling")
                ))
            )),
            GoalCategory("🧘", "Flexibility & Balance", listOf(
                GoalSubcategory("🧘‍♀️", "Yoga", listOf(
                    SpecificGoal("☮️", "Hatha Yoga"),
                    SpecificGoal("🔥", "Hot Yoga"),
                    SpecificGoal("🌊", "Flow Yoga")
                )),
                GoalSubcategory("🤸‍♂️", "Stretching", listOf(
                    SpecificGoal("🦵", "Leg Flexibility"),
                    SpecificGoal("🧘", "Full Body Mobility")
                ))
            ))
        ),
        
        Domain.CAREER to listOf(
            GoalCategory("📈", "Career Growth", listOf(
                GoalSubcategory("⬆️", "Advancement", listOf(
                    SpecificGoal("👔", "Get Promotion"),
                    SpecificGoal("💼", "Leadership Role"),
                    SpecificGoal("🎯", "Salary Increase")
                )),
                GoalSubcategory("🛠️", "Skills", listOf(
                    SpecificGoal("💻", "Technical Skills"),
                    SpecificGoal("🗣️", "Soft Skills"),
                    SpecificGoal("🎓", "Certifications")
                ))
            )),
            GoalCategory("🚀", "Entrepreneurship", listOf(
                GoalSubcategory("💡", "Startup", listOf(
                    SpecificGoal("🏢", "Launch Business"),
                    SpecificGoal("📱", "Build MVP"),
                    SpecificGoal("💰", "Raise Funding")
                )),
                GoalSubcategory("📊", "Side Business", listOf(
                    SpecificGoal("🛍️", "E-commerce"),
                    SpecificGoal("✍️", "Freelancing"),
                    SpecificGoal("📸", "Creative Services")
                ))
            )),
            GoalCategory("🌐", "Networking", listOf(
                GoalSubcategory("🤝", "Professional Network", listOf(
                    SpecificGoal("👥", "Industry Contacts"),
                    SpecificGoal("🎤", "Public Speaking"),
                    SpecificGoal("📣", "Personal Brand")
                ))
            ))
        ),
        
        Domain.MENTAL_HEALTH to listOf(
            GoalCategory("🧘", "Mindfulness", listOf(
                GoalSubcategory("🧘‍♀️", "Meditation", listOf(
                    SpecificGoal("⏰", "Daily Practice"),
                    SpecificGoal("🎯", "Focused Meditation"),
                    SpecificGoal("💭", "Transcendental")
                )),
                GoalSubcategory("🌅", "Breathing", listOf(
                    SpecificGoal("😮‍💨", "Pranayama"),
                    SpecificGoal("🧊", "Wim Hof Method")
                ))
            )),
            GoalCategory("💚", "Therapy & Support", listOf(
                GoalSubcategory("🗣️", "Therapy", listOf(
                    SpecificGoal("💬", "Talk Therapy"),
                    SpecificGoal("🧠", "CBT"),
                    SpecificGoal("👥", "Group Therapy")
                )),
                GoalSubcategory("🤝", "Support", listOf(
                    SpecificGoal("👨‍👩‍👧", "Support Groups"),
                    SpecificGoal("📱", "Mental Health Apps")
                ))
            )),
            GoalCategory("😴", "Sleep & Rest", listOf(
                GoalSubcategory("🛌", "Sleep Quality", listOf(
                    SpecificGoal("⏰", "Sleep Schedule"),
                    SpecificGoal("🌙", "Sleep Hygiene"),
                    SpecificGoal("📵", "Digital Detox")
                ))
            ))
        ),
        
        Domain.ACADEMICS to listOf(
            GoalCategory("🎓", "Formal Education", listOf(
                GoalSubcategory("📚", "Degree Programs", listOf(
                    SpecificGoal("🎓", "Bachelor's Degree"),
                    SpecificGoal("📖", "Master's Degree"),
                    SpecificGoal("🔬", "PhD")
                )),
                GoalSubcategory("📜", "Certifications", listOf(
                    SpecificGoal("🏅", "Professional Cert"),
                    SpecificGoal("💻", "Technical Cert")
                ))
            )),
            GoalCategory("💻", "Self-Learning", listOf(
                GoalSubcategory("🌐", "Online Courses", listOf(
                    SpecificGoal("🎥", "Video Courses"),
                    SpecificGoal("📱", "Mobile Learning"),
                    SpecificGoal("🎓", "MOOCs")
                )),
                GoalSubcategory("📖", "Reading", listOf(
                    SpecificGoal("📚", "Read More Books"),
                    SpecificGoal("📰", "Research Papers"),
                    SpecificGoal("✍️", "Take Notes")
                ))
            )),
            GoalCategory("🗣️", "Languages", listOf(
                GoalSubcategory("🌍", "Language Learning", listOf(
                    SpecificGoal("🇪🇸", "Spanish"),
                    SpecificGoal("🇫🇷", "French"),
                    SpecificGoal("🇨🇳", "Mandarin"),
                    SpecificGoal("🇯🇵", "Japanese")
                ))
            ))
        ),
        
        Domain.PHILOSOPHY to listOf(
            GoalCategory("📖", "Philosophical Schools", listOf(
                GoalSubcategory("🏛️", "Ancient Philosophy", listOf(
                    SpecificGoal("🗿", "Stoicism"),
                    SpecificGoal("🏺", "Epicureanism"),
                    SpecificGoal("💭", "Socratic Method")
                )),
                GoalSubcategory("🧠", "Modern Philosophy", listOf(
                    SpecificGoal("❓", "Existentialism"),
                    SpecificGoal("🌌", "Nihilism"),
                    SpecificGoal("🔮", "Phenomenology")
                ))
            )),
            GoalCategory("⚖️", "Ethics & Morality", listOf(
                GoalSubcategory("💡", "Ethical Systems", listOf(
                    SpecificGoal("⚖️", "Consequentialism"),
                    SpecificGoal("📜", "Deontology"),
                    SpecificGoal("💪", "Virtue Ethics")
                ))
            ))
        ),
        
        Domain.INVESTING to listOf(
            GoalCategory("💵", "Saving", listOf(
                GoalSubcategory("🏦", "Emergency Fund", listOf(
                    SpecificGoal("💰", "Build Emergency Fund"),
                    SpecificGoal("📊", "Budget Better"),
                    SpecificGoal("💳", "Reduce Debt")
                ))
            )),
            GoalCategory("📈", "Investing", listOf(
                GoalSubcategory("📊", "Stock Market", listOf(
                    SpecificGoal("📈", "Index Funds"),
                    SpecificGoal("🎯", "Individual Stocks"),
                    SpecificGoal("💹", "Options Trading")
                )),
                GoalSubcategory("🏠", "Real Estate", listOf(
                    SpecificGoal("🏡", "Buy Property"),
                    SpecificGoal("🏘️", "Rental Income"),
                    SpecificGoal("🏗️", "REITs")
                )),
                GoalSubcategory("₿", "Crypto", listOf(
                    SpecificGoal("💎", "Long-term Hold"),
                    SpecificGoal("📊", "Trading")
                ))
            )),
            GoalCategory("🎯", "Financial Goals", listOf(
                GoalSubcategory("🔥", "FIRE", listOf(
                    SpecificGoal("🌴", "Early Retirement"),
                    SpecificGoal("💰", "Financial Independence")
                ))
            ))
        ),
        
        Domain.CULTURE_HOBBIES to listOf(
            GoalCategory("🎵", "Music", listOf(
                GoalSubcategory("🎸", "Instruments", listOf(
                    SpecificGoal("🎹", "Piano"),
                    SpecificGoal("🎸", "Guitar"),
                    SpecificGoal("🥁", "Drums"),
                    SpecificGoal("🎺", "Wind Instruments")
                )),
                GoalSubcategory("🎤", "Vocals", listOf(
                    SpecificGoal("🎵", "Singing"),
                    SpecificGoal("🎭", "Performance")
                ))
            )),
            GoalCategory("🎨", "Visual Arts", listOf(
                GoalSubcategory("🖌️", "Painting", listOf(
                    SpecificGoal("🎨", "Oil Painting"),
                    SpecificGoal("🖼️", "Watercolor"),
                    SpecificGoal("✨", "Digital Art")
                )),
                GoalSubcategory("📸", "Photography", listOf(
                    SpecificGoal("📷", "Portrait"),
                    SpecificGoal("🌄", "Landscape"),
                    SpecificGoal("📱", "Mobile Photography")
                ))
            )),
            GoalCategory("✍️", "Writing", listOf(
                GoalSubcategory("📖", "Creative Writing", listOf(
                    SpecificGoal("📚", "Novel Writing"),
                    SpecificGoal("📝", "Poetry"),
                    SpecificGoal("📰", "Journalism")
                ))
            ))
        ),
        
        Domain.INTIMACY_ROMANCE to listOf(
            GoalCategory("💑", "Dating", listOf(
                GoalSubcategory("🔍", "Finding Partner", listOf(
                    SpecificGoal("📱", "Online Dating"),
                    SpecificGoal("🎉", "Social Events"),
                    SpecificGoal("👥", "Meetup Groups")
                )),
                GoalSubcategory("💬", "Communication", listOf(
                    SpecificGoal("🗣️", "Better Conversations"),
                    SpecificGoal("❤️", "Emotional Intelligence")
                ))
            )),
            GoalCategory("💕", "Relationship Growth", listOf(
                GoalSubcategory("🤝", "Partnership", listOf(
                    SpecificGoal("💑", "Strengthen Bond"),
                    SpecificGoal("🗣️", "Improve Communication"),
                    SpecificGoal("🎯", "Shared Goals")
                )),
                GoalSubcategory("🔥", "Intimacy", listOf(
                    SpecificGoal("💋", "Physical Intimacy"),
                    SpecificGoal("💭", "Emotional Intimacy")
                ))
            ))
        ),
        
        Domain.FRIENDSHIP_SOCIAL to listOf(
            GoalCategory("👥", "Making Friends", listOf(
                GoalSubcategory("🆕", "New Connections", listOf(
                    SpecificGoal("🎉", "Join Groups"),
                    SpecificGoal("🏃", "Sports Clubs"),
                    SpecificGoal("🎮", "Hobby Communities")
                )),
                GoalSubcategory("🔄", "Reconnecting", listOf(
                    SpecificGoal("📞", "Old Friends"),
                    SpecificGoal("🎓", "Alumni Networks")
                ))
            )),
            GoalCategory("🤝", "Social Skills", listOf(
                GoalSubcategory("💬", "Communication", listOf(
                    SpecificGoal("🗣️", "Conversation Skills"),
                    SpecificGoal("👂", "Active Listening"),
                    SpecificGoal("😊", "Social Confidence")
                ))
            )),
            GoalCategory("🎪", "Community", listOf(
                GoalSubcategory("🏘️", "Local Community", listOf(
                    SpecificGoal("🤲", "Volunteering"),
                    SpecificGoal("🎉", "Community Events"),
                    SpecificGoal("🏛️", "Local Groups")
                ))
            ))
        )
    )
}
