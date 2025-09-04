package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.kendimaceram.app.ui.components.MainScaffold
import kotlinx.coroutines.launch

@Composable
fun AccountInfoScreen(navController: NavController) {
    val user = Firebase.auth.currentUser
    val email = user?.email
    val isVerified = user?.isEmailVerified == true

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    MainScaffold(
        navController = navController,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // --- HEADER / AVATAR ---
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                val avatarLetter = (email?.firstOrNull()?.uppercaseChar() ?: 'U').toString()
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarLetter,
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Hesap Bilgilerin",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = email ?: "E-posta bulunamadı",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.75f)
                    )
                }
            }

            // --- HESAP KARTI ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Hesap",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    // E-posta (salt okunur)
                    Text(
                        text = "E-posta Adresi",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = email ?: "—",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(16.dp))
                    // Doğrulama durumu
                    AssistChip(
                        onClick = {},
                        label = { Text(if (isVerified) "Doğrulandı" else "Doğrulanmadı") },
                        enabled = false,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = if (isVerified)
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            else
                                MaterialTheme.colorScheme.error.copy(alpha = 0.12f),
                            labelColor = if (isVerified)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.error
                        )
                    )

                    Spacer(Modifier.height(12.dp))
                    OutlinedButton(
                        onClick = {
                            if (user != null) {
                                user.sendEmailVerification()
                                    .addOnSuccessListener {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Doğrulama e-postası gönderildi.")
                                        }
                                    }
                                    .addOnFailureListener {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Gönderilemedi: ${it.message ?: "Bilinmeyen hata"}")
                                        }
                                    }
                            }
                        },
                        enabled = user != null && !isVerified,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Doğrulama Maili Gönder")
                    }
                }
            }

            // --- GÜVENLİK KARTI ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Güvenlik",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(12.dp))

                    Text(
                        "Şifre Yönetimi",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Güvenlik nedeniyle, şifrenizi değiştirmek için e-posta adresinize bir sıfırlama bağlantısı göndereceğiz.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (email != null) {
                                Firebase.auth.sendPasswordResetEmail(email)
                                    .addOnSuccessListener {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Şifre sıfırlama e-postası gönderildi.")
                                        }
                                    }
                                    .addOnFailureListener {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Gönderilemedi: ${it.message ?: "Bilinmeyen hata"}")
                                        }
                                    }
                            }
                        },
                        enabled = email != null,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Şifre Sıfırlama Maili Gönder")
                    }
                }
            }
        }
    }
}
