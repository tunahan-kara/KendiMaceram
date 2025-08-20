package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold
import com.kendimaceram.app.ui.navigation.Screen
import com.kendimaceram.app.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Kendi Maceram", fontSize = 32.sp)
            Spacer(modifier = Modifier.height(48.dp))

            // EKSİK OLAN BUTONLARI GERİ EKLİYORUZ
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

            Spacer(modifier = Modifier.height(32.dp))

            // ÇIKIŞ YAP BUTONU YERİNDE KALIYOR
            Button(
                onClick = {
                    viewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Çıkış Yap")
            }
        }
    }
}