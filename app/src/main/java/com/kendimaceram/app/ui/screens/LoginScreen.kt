package com.kendimaceram.app.ui.screens

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow // ✅ DOĞRU import
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.kendimaceram.app.R

@Composable
fun LoginScreen(
    onSignedIn: (FirebaseUser) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val activity = context as Activity
    val auth = remember { FirebaseAuth.getInstance() }

    // Eğer kullanıcı zaten girişliyse login'i atla
    LaunchedEffect(Unit) {
        auth.currentUser?.let { onSignedIn(it) }
    }

    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    // Google Sign-In launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        loading = false
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            try {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) {
                    errorText = "Google idToken alınamadı."
                    return@rememberLauncherForActivityResult
                }
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(credential)
                    .addOnSuccessListener { auth.currentUser?.let(onSignedIn) }
                    .addOnFailureListener { e ->
                        errorText = e.localizedMessage ?: "Giriş başarısız."
                    }
            } catch (e: ApiException) {
                errorText = "Google Sign-In iptal/başarısız: ${e.statusCode}"
            } catch (t: Throwable) {
                errorText = t.localizedMessage ?: "Beklenmeyen hata."
            }
        } else {
            errorText = "Giriş iptal edildi."
        }
    }

    fun startGoogleSignIn() {
        errorText = null
        loading = true
        val client = googleClient(context)
        signInLauncher.launch(client.signInIntent)
    }

    // --- UI ---
    GradientBackground(
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp, bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FancyHeader(
                text = "Kendi Maceranı yazmaya hazır mısın?",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
            )

            Spacer(Modifier.weight(1f))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                val haloSize = 220.dp
                if (loading) {
                    FaceIdHalo(
                        haloSizeDp = haloSize,         // ✅ isim değişti
                        ringCount = 3,
                        baseColor = Color.White.copy(alpha = 0.85f),
                        strokeWidth = 3.dp
                    )
                }

                GoogleSignInButton(
                    text = if (loading) "Bağlanılıyor..." else "Google ile Giriş Yap",
                    enabled = !loading,
                    onClick = { startGoogleSignIn() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
            }

            if (errorText != null) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = errorText!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.weight(1.2f))
        }
    }
}

/* -------------------- Helpers & UI Pieces -------------------- */

@Composable
private fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val c1 = MaterialTheme.colorScheme.primary
    val c2 = MaterialTheme.colorScheme.secondary
    val c3 = MaterialTheme.colorScheme.tertiary

    Box(
        modifier = modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    c1.copy(alpha = 0.95f),
                    c2.copy(alpha = 0.90f),
                    c3.copy(alpha = 0.85f)
                )
            )
        )
    ) { content() }
}

@Composable
private fun FancyHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier,
        color = Color.White,
        lineHeight = 36.sp,
        style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            shadow = Shadow( // ✅ graphics.Shadow
                color = Color.Black.copy(alpha = 0.25f),
                offset = Offset(0f, 4f),
                blurRadius = 8f
            )
        ),
        textAlign = TextAlign.Center
    )
}

@Composable
private fun GoogleSignInButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp), clip = false),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black.copy(alpha = 0.9f),
            disabledContainerColor = Color.White.copy(alpha = 0.85f),
            disabledContentColor = Color.Black.copy(alpha = 0.5f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Google simgesi — drawable yoksa kırılsa bile UI çalışır
            runCatching {
                Image(
                    painter = painterResource(id = R.drawable.ic_google),
                    contentDescription = "Google",
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                )
            }

            Spacer(Modifier.width(12.dp))
            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1
            )
        }
    }
}

/**
 * Samsung Face ID hissi: butonun etrafında genişleyip sönümlenen halkalar.
 * DİKKAT: Parametre adı `haloSizeDp`, Canvas içindeki `this.size` ile karışmasın.
 */
@Composable
private fun FaceIdHalo(
    haloSizeDp: Dp,
    ringCount: Int = 3,
    baseColor: Color = Color.White,
    strokeWidth: Dp = 3.dp
) {
    val transition = rememberInfiniteTransition(label = "halo")
    val phase by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = TweenSpec(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val strokePx = with(LocalContext.current.resources.displayMetrics) {
        strokeWidth.value * density
    }

    Canvas(
        modifier = Modifier.size(haloSizeDp)
    ) {
        val maxR = this.size.minDimension / 2f // ✅ DrawScope.size.minDimension

        for (i in 0 until ringCount) {
            val offset = i.toFloat() / ringCount
            val t = ((phase + offset) % 1f)

            val radius = lerpFloat(0.40f * maxR, 0.95f * maxR, t)
            val alpha = (1f - t).coerceIn(0f, 1f)
            val color = baseColor.copy(alpha = 0.35f * alpha)

            drawCircle(
                color = color,
                radius = radius,
                center = this.center,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
    }
}

/* -------------------- Google client helper -------------------- */

private fun googleClient(context: android.content.Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

/* -------------------- Small util -------------------- */

private fun lerpFloat(start: Float, end: Float, t: Float): Float {
    return start + (end - start) * t
}
