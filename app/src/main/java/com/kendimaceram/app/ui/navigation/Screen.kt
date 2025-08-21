package com.kendimaceram.app.ui.navigation

sealed class Screen(val route: String) {
    data object Splash : Screen("splash_screen")
    data object Home : Screen("home_screen")
    data object MyStories : Screen("my_stories_screen")
    data object NewStories : Screen("new_stories_screen")
    data object Premium : Screen("premium_screen")
    data object StoryReader : Screen("story_reader_screen/{storyId}") {
        fun createRoute(storyId: String) = "story_reader_screen/$storyId"
    }
    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")
}