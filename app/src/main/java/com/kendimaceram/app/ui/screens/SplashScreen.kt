package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.SplashViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: SplashViewModel = hiltViewModel()
) {
    LaunchedEffect(key1 = true) {
        delay(1000L)

        // ---- DEĞİŞİKLİK BURADA ----
        // Artık kullanıcı giriş yapmışsa onu "Home" yerine "NewStories" ekranına yönlendiriyoruz.
        val startDestination = if (viewModel.isUserAuthenticated) Screen.NewStories.route else Screen.Login.route

        navController.navigate(startDestination) {
            // Splash ekranını navigasyon geçmişinden siliyoruz.
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}