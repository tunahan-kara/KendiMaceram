// Screen.kt
package com.kendimaceram.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home_screen")
    data object MyStories : Screen("my_stories_screen")
    data object NewStories : Screen("new_stories_screen")
    data object Premium : Screen("premium_screen")
    data object StoryReader : Screen("story_reader_screen/{storyId}") {
        fun createRoute(storyId: String) = "story_reader_screen/$storyId"
    }
}