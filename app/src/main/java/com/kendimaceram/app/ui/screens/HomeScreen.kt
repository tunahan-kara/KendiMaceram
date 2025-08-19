package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen

@Composable
fun HomeScreen(navController: NavController) {
    // Ekranın içeriğini MainScaffold ile sarmalıyoruz
    MainScaffold(navController = navController) { innerPadding ->
        // Column'a innerPadding'i uyguluyoruz ki içerik üst barın altına girmesin
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // <-- ÖNEMLİ DEĞİŞİKLİK
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Kendi Maceram", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(48.dp))

            Button(onClick = { navController.navigate(Screen.MyStories.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("İndirdiğim Hikayeler")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(Screen.NewStories.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Yeni Hikayeler Keşfet")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { navController.navigate(Screen.Premium.route) }, modifier = Modifier.fillMaxWidth()) {
                Text("Premium Üye Ol")
            }
        }
    }
}