package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold

@Composable
fun MyStoriesScreen(navController: NavController) {
    // Ekranı genel iskelemizle sarmalıyoruz
    MainScaffold(navController = navController) { innerPadding ->
        // Box'a padding ekleyerek içeriğin üst barın altına girmesini engelliyoruz
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding), // <-- ÖNEMLİ DEĞİŞİKLİK
            contentAlignment = Alignment.Center
        ) {
            Text(text = "İndirdiğim Hikayeler Ekranı")
        }
    }
}