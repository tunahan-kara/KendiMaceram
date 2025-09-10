package com.kendimaceram.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kendimaceram.app.data.SettingsRepository
import com.kendimaceram.app.data.ThemeSetting
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.ui.screens.*
import com.kendimaceram.app.ui.theme.KendiMaceramTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.kendimaceram.app.ui.screens.LoginScreen
import com.kendimaceram.app.ui.screens.SplashScreen
import com.kendimaceram.app.ui.theme.GPDarkGreen
import com.kendimaceram.app.ui.theme.GPDarkPurple

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var settingsRepository: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge açık kalsın
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // SADECE alt navigation bar'ı gizle
        val ic = WindowCompat.getInsetsController(window, window.decorView)
        ic.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        ic.hide(WindowInsetsCompat.Type.navigationBars())

        setContent {
            val themeSetting by settingsRepository.themeSettingFlow.collectAsState(initial = ThemeSetting.SYSTEM)
            val useDarkTheme = when (themeSetting) {
                ThemeSetting.SYSTEM -> isSystemInDarkTheme()
                ThemeSetting.LIGHT -> false
                ThemeSetting.DARK -> true
            }

            KendiMaceramTheme(darkTheme = useDarkTheme) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(GPDarkPurple, GPDarkGreen, MaterialTheme.colorScheme.background)
                            )
                        )
                ) {
                    Surface(modifier = Modifier.fillMaxSize(), color = Color.Transparent) {
                        val navController = rememberNavController()
                        NavHost(navController = navController, startDestination = Screen.Splash.route) {
                            composable(Screen.Splash.route) { SplashScreen(navController) }
                            composable(
                                Screen.Login.route,
                                enterTransition = { fadeIn(tween(700)) },
                                exitTransition = { fadeOut(tween(700)) }
                            ) {
                                LoginScreen(
                                    onSignedIn = {
                                        navController.navigate(Screen.NewStories.route) {
                                            popUpTo(Screen.Login.route) { inclusive = true }
                                            launchSingleTop = true
                                        }
                                    }
                                )
                            }
                            composable(
                                Screen.Register.route,
                                enterTransition = { slideInHorizontally { 1000 } },
                                exitTransition = { slideOutHorizontally { -1000 } }
                            ) { RegisterScreen(navController) }

                            composable(Screen.MyStories.route,
                                enterTransition = { fadeIn(tween(300)) },
                                exitTransition = { fadeOut(tween(300)) }
                            ) { MyStoriesScreen(navController) }

                            composable(Screen.NewStories.route,
                                enterTransition = { fadeIn(tween(300)) },
                                exitTransition = { fadeOut(tween(300)) }
                            ) { NewStoriesScreen(navController) }

                            composable(Screen.Premium.route,
                                enterTransition = { fadeIn(tween(300)) },
                                exitTransition = { fadeOut(tween(300)) }
                            ) { PremiumScreen(navController) }

                            composable(Screen.Profile.route,
                                enterTransition = { fadeIn(tween(300)) },
                                exitTransition = { fadeOut(tween(300)) }
                            ) { ProfileScreen(navController) }

                            composable(Screen.AccountInfo.route,
                                enterTransition = { slideInHorizontally { 1000 } }
                            ) { AccountInfoScreen(navController) }

                            composable(Screen.NotificationSettings.route,
                                enterTransition = { slideInHorizontally { 1000 } }
                            ) { NotificationSettingsScreen(navController) }

                            composable(Screen.ThemeSettings.route,
                                enterTransition = { slideInHorizontally { 1000 } }
                            ) { ThemeSettingsScreen(navController) }

                            composable(Screen.Help.route,
                                enterTransition = { slideInHorizontally { 1000 } }
                            ) { HelpScreen(navController) }

                            composable(
                                Screen.StoryDetail.route,
                                arguments = listOf(navArgument("storyId") { type = NavType.StringType })
                            ) {
                                val storyId = it.arguments?.getString("storyId") ?: ""
                                StoryDetailScreen(navController, storyId)
                            }
                            composable(
                                Screen.StoryReader.route,
                                arguments = listOf(navArgument("storyId") { type = NavType.StringType })
                            ) {
                                val storyId = it.arguments?.getString("storyId") ?: ""
                                StoryReaderScreen(navController, storyId)
                            }
                        }
                    }
                }
            }
        }
    }

    // Odak geri geldiğinde bazı OEM'ler barı gösterir; tekrar gizle
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            WindowCompat.getInsetsController(window, window.decorView)
                .hide(WindowInsetsCompat.Type.navigationBars())
        }
    }
}
