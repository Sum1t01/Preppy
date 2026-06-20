package com.sum1t.preppy.common.utils

data class StudyNotification(
    val title: String,
    val message: String
)

object StudyNotificationSpinner {

    private val notifications = listOf(

        // 📚 Study / Learning core
        StudyNotification(
            title = "📚 Study Time!",
            message = "Let’s build strong concepts with a quick quiz session."
        ),
        StudyNotification(
            title = "📖 Learning Moment!",
            message = "A small step today leads to big academic gains."
        ),
        StudyNotification(
            title = "✏️ Revision Time!",
            message = "Refresh your memory with a quick practice round."
        ),
        StudyNotification(
            title = "📊 Concept Check!",
            message = "Let’s see how well you understand this topic."
        ),

        // 🧠 Brain training
        StudyNotification(
            title = "🧠 Brain Workout!",
            message = "Sharpen your mind with a few smart questions."
        ),
        StudyNotification(
            title = "🧩 Thinking Mode!",
            message = "Solve, analyze, and level up your reasoning skills."
        ),
        StudyNotification(
            title = "⚡ Mental Boost!",
            message = "A quick challenge keeps your brain active and alert."
        ),
        StudyNotification(
            title = "🔍 Focus Drill!",
            message = "Train your attention with a short quiz burst."
        ),

        // 🎯 Challenge / competition
        StudyNotification(
            title = "🎯 Focus Challenge!",
            message = "Stay locked in and complete this mini challenge."
        ),
        StudyNotification(
            title = "🏆 Quiz Battle!",
            message = "Can you beat your previous high score?"
        ),
        StudyNotification(
            title = "🔥 Challenge Mode!",
            message = "Push your limits with this fast quiz round."
        ),
        StudyNotification(
            title = "🥇 Level Up Opportunity!",
            message = "Earn your next improvement with just a few questions."
        ),

        // 🎮 Gamified fun
        StudyNotification(
            title = "🎮 Quiz Time!",
            message = "Let’s turn learning into a fun game!"
        ),
        StudyNotification(
            title = "⚡ XP Boost!",
            message = "Quick quiz = instant knowledge upgrade!"
        ),
        StudyNotification(
            title = "🎉 Mini Game Time!",
            message = "Play a round and boost your skills."
        ),
        StudyNotification(
            title = "💡 Knowledge Quest!",
            message = "Explore new questions and expand your understanding."
        ),

        // 😄 Friendly / light tone
        StudyNotification(
            title = "😄 Quick Break!",
            message = "Let’s turn your break into a learning win."
        ),
        StudyNotification(
            title = "😎 Smart Move!",
            message = "A few questions now = less stress later."
        ),
        StudyNotification(
            title = "🤓 Nerd Mode ON!",
            message = "Time to show those subjects who's boss."
        ),
        StudyNotification(
            title = "🎈 Light Practice!",
            message = "No pressure—just a quick, easy session."
        ),

        // ⏰ Reminder / habit building
        StudyNotification(
            title = "⏰ Quick Reminder!",
            message = "Consistency beats intensity—try a short quiz."
        ),
        StudyNotification(
            title = "📌 Stay Consistent!",
            message = "Even 2 minutes today keeps your streak alive."
        ),
        StudyNotification(
            title = "🕒 Short Session!",
            message = "Just a quick round to keep your momentum going."
        ),
        StudyNotification(
            title = "⚡ Don’t Break the Flow!",
            message = "One small quiz keeps your learning habit strong."
        ),

        // 📈 Progress / motivation
        StudyNotification(
            title = "📈 Progress Check!",
            message = "See how much you’ve improved today."
        ),
        StudyNotification(
            title = "🚀 Growth Time!",
            message = "Every question brings you closer to mastery."
        ),
        StudyNotification(
            title = "📊 Track Your Skills!",
            message = "Let’s measure your learning progress."
        ),
        StudyNotification(
            title = "🏅 Earn Your Progress!",
            message = "Complete a quick set and improve your score."
        )
    )

    fun getRandom(): StudyNotification {
        return notifications.random()
    }
}