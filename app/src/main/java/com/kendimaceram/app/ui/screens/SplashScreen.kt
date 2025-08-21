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
    // Bu kod, ekran göründüğü anda sadece bir kere çalışır.
    LaunchedEffect(key1 = true) {
        // Estetik olarak daha hoş görünmesi için 1 saniye bekleyelim.
        delay(1000L)

        // Yönlendirme: popUpTo ile splash ekranını navigasyon geçmişinden siliyoruz
        // ki kullanıcı geri tuşuna basınca bu ekrana dönemesin.
        navController.navigate(
            if (viewModel.isUserAuthenticated) Screen.Home.route else Screen.Login.route
        ) {
            popUpTo(Screen.Splash.route) { inclusive = true }
        }
    }

    // Yönlendirme yapılırken ekranda bir yüklenme animasyonu göster.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}