package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kendimaceram.app.ui.components.MainScaffold

@Composable
fun AccountInfoScreen(navController: NavController) {
    // O anki giriş yapmış kullanıcının bilgisini Firebase'den alıyoruz
    val currentUser = Firebase.auth.currentUser

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text("Hesap Bilgilerin", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(24.dp))

            // E-posta Adresi (Değiştirilemez)
            Text("E-posta Adresi", style = MaterialTheme.typography.labelLarge)
            Text(
                text = currentUser?.email ?: "E-posta bulunamadı.",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold
            )

            Divider(modifier = Modifier.padding(vertical = 24.dp))

            // Şifre Değiştirme
            Text("Şifre Yönetimi", style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    // TODO: Şifre değiştirme mantığı eklenecek
                    // (Kullanıcıya şifre sıfırlama e-postası gönderilir)
                }
            ) {
                Text("Şifre Değiştirme E-postası Gönder")
            }
            Text(
                "Güvenlik nedeniyle, şifrenizi değiştirmek için e-posta adresinize bir sıfırlama bağlantısı göndereceğiz.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}