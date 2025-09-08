package com.kendimaceram.app.ui.screens

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.kendimaceram.app.R
import com.kendimaceram.app.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    navController: NavController
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val activity = context as Activity
    val auth = remember { FirebaseAuth.getInstance() }
    val user = auth.currentUser

    var showPrivacy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Hesap Bilgileri") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    actionIconContentColor = MaterialTheme.colorScheme.onSurface
                ),
                windowInsets = androidx.compose.foundation.layout.WindowInsets(0)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar + temel bilgiler
            Avatar(user?.photoUrl)

            Spacer(Modifier.height(12.dp))

            Text(text = user?.displayName.orEmpty(), style = MaterialTheme.typography.titleLarge)
            Text(
                text = user?.email.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )

            Spacer(Modifier.height(20.dp))

            // Hesap/ güvenlik işlemleri
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(Modifier.padding(12.dp)) {
                    ListItem(
                        headlineContent = { Text("Çıkış yap") },
                        leadingContent = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (busy) return@clickable
                                busy = true
                                FirebaseAuth.getInstance().signOut()
                                googleClient(context).signOut().addOnCompleteListener {
                                    busy = false
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                        launchSingleTop = true
                                    }
                                }
                            }
                    )
                    Divider()
                    ListItem(
                        headlineContent = { Text("Google hesabı erişimini iptal et") },
                        supportingContent = { Text("Bir sonraki girişte yeniden izin istenir") },
                        leadingContent = { Icon(Icons.Default.LinkOff, contentDescription = null) },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (busy) return@clickable
                                busy = true
                                googleClient(context).revokeAccess().addOnCompleteListener {
                                    busy = false
                                }
                            }
                    )
                    Divider()
                    ListItem(
                        headlineContent = {
                            Text("Hesabı Sil", color = MaterialTheme.colorScheme.error)
                        },
                        supportingContent = {
                            Text(
                                "Kalıcıdır. Yakın zamanda giriş yaptıysanız yeniden doğrulama istenebilir.",
                                color = MaterialTheme.colorScheme.error
                            )
                        },
                        leadingContent = {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error
                            )
                        },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable {
                                if (busy) return@clickable
                                busy = true
                                auth.currentUser?.delete()
                                    ?.addOnSuccessListener {
                                        busy = false
                                        navController.navigate(Screen.Login.route) {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                    ?.addOnFailureListener {
                                        busy = false
                                    }
                            }
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Yasal metinler
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(onClick = { showPrivacy = true }) { Text("Gizlilik Politikası") }
                Text(" • ", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                TextButton(onClick = { showTerms = true }) { Text("Kullanım Şartları") }
            }
        }
    }

    if (showPrivacy) {
        TermsDialog(
            title = "Gizlilik Politikası",
            body = PRIVACY_LONG,
            onDismiss = { showPrivacy = false }
        )
    }
    if (showTerms) {
        TermsDialog(
            title = "Kullanım Şartları",
            body = TERMS_LONG,
            onDismiss = { showTerms = false }
        )
    }
}

/* -------------------- UI Parçaları -------------------- */

@Composable
private fun Avatar(photoUrl: Uri?) {
    val placeholder = androidx.compose.ui.res.painterResource(id = R.drawable.ic_google)
    Box(contentAlignment = Alignment.Center) {
        if (photoUrl != null) {
            AsyncImage(
                model = ImageRequest.Builder(androidx.compose.ui.platform.LocalContext.current)
                    .data(photoUrl)
                    .crossfade(true)
                    .build(),
                placeholder = placeholder,
                error = placeholder,
                contentDescription = "Profil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                painter = placeholder,
                contentDescription = "Profil",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
            )
        }
    }
}

@Composable
private fun TermsDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
        ) {
            Column(Modifier.padding(16.dp)) {
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Kapat")
                    }
                }
                Divider(Modifier.padding(vertical = 8.dp))
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .heightIn(min = 220.dp, max = 460.dp)
                        .verticalScroll(rememberScrollState())
                )
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Kapat") }
                }
            }
        }
    }
}

/* -------------------- Helpers -------------------- */

private fun googleClient(context: android.content.Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

/* -------------------- Uzun/resmi metinler -------------------- */

private const val PRIVACY_LONG = """
Kendi Maceram Gizlilik Politikası
(uzun/resmî metnini buraya yapıştırdın)
"""

private const val TERMS_LONG = """
Kendi Maceram Kullanım Şartları
(uzun/resmî metnini buraya yapıştırdın)
"""
