package com.kendimaceram.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    data object Splash : Screen("splash_screen", "Yükleniyor", null)
    data object Login : Screen("login_screen", "Giriş Yap", null)
    data object Register : Screen("register_screen", "Kayıt Ol", null)

    // 4 ana sekme
    data object NewStories : Screen("new_stories_screen", "Keşfet", Icons.Default.Explore)
    data object MyStories : Screen("my_stories_screen", "Hikayelerim", Icons.Default.LibraryBooks)
    data object Premium : Screen("premium_screen", "Premium", Icons.Default.Star)
    data object Profile : Screen("profile_screen", "Hesabım", Icons.Default.Person) // YENİ EKRAN

    data object StoryReader : Screen("story_reader_screen/{storyId}", "Hikaye Okuyucu", null) {
        fun createRoute(storyId: String) = "story_reader_screen/$storyId"
    }
}