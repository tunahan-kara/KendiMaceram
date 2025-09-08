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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.PlatformTextStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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

    // Zaten girişliyse atla
    LaunchedEffect(Unit) {
        auth.currentUser?.let { onSignedIn(it) }
    }

    var loading by remember { mutableStateOf(false) }
    var errorText by remember { mutableStateOf<String?>(null) }

    // Terms dialog state
    var showPrivacy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }

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
    GradientBackground(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .padding(top = 64.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            GraffitiHeader(
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
                        haloSizeDp = haloSize,
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
                Spacer(Modifier.height(12.dp))
                Text(
                    text = errorText!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(12.dp))

            TermsFooter(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                onPrivacyClick = { showPrivacy = true },
                onTermsClick = { showTerms = true }
            )
        }
    }

    // Dialogs
    if (showPrivacy) {
        TermsDialog(
            title = "Gizlilik Politikası",
            body = PRIVACY_PLACEHOLDER,
            onDismiss = { showPrivacy = false }
        )
    }
    if (showTerms) {
        TermsDialog(
            title = "Kullanım Şartları",
            body = TERMS_PLACEHOLDER,
            onDismiss = { showTerms = false }
        )
    }
}

/* -------------------- Gradient: app paletine uyumlu -------------------- */

@Composable
private fun GradientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()

    // Üstte canlı primary, orta yüzey, altta arka plan — koyuya akış
    val c1 = if (isDark) scheme.primary.copy(alpha = 0.95f) else scheme.primary
    val c2 = if (isDark) scheme.surface.copy(alpha = 0.90f) else scheme.surface.copy(alpha = 0.94f)
    val c3 = if (isDark) scheme.background.copy(alpha = 0.92f) else scheme.background.copy(alpha = 0.98f)

    Box(
        modifier = modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(c1, c2, c3),
                start = Offset(0f, 0f),          // sol-üst
                end = Offset(1200f, 1700f)       // sağ-alt
            )
        )
    ) { content() }
}

/* -------------------- Başlık: tek görünümlü 3D/graffiti -------------------- */

@Composable
private fun GraffitiHeader(
    text: String,
    modifier: Modifier = Modifier
) {
    val graffitiFamily: FontFamily? = runCatching {
        // res/font/graffiti.ttf eklediğinde otomatik devreye girer
        FontFamily(Font(resId = R.font.graffiti))
    }.getOrNull()

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Arka katman (gölge / 3D)
        Text(
            text = text,
            color = Color.Black.copy(alpha = 0.45f),
            lineHeight = 38.sp,
            style = TextStyle(
                fontFamily = graffitiFamily ?: MaterialTheme.typography.headlineMedium.fontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.35f),
                    offset = Offset(0f, 6f),
                    blurRadius = 10f
                ),
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .offset(x = 3.dp, y = 3.dp)
        )

        // Ön katman (parlak)
        Text(
            text = text,
            color = Color.White,
            lineHeight = 38.sp,
            style = TextStyle(
                fontFamily = graffitiFamily ?: MaterialTheme.typography.headlineMedium.fontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 30.sp,
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.20f),
                    offset = Offset(0f, 3f),
                    blurRadius = 6f
                ),
                platformStyle = PlatformTextStyle(includeFontPadding = false)
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

/* -------------------- Google Button -------------------- */

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
            .shadow(10.dp, shape = RoundedCornerShape(18.dp), clip = false),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.elevatedButtonColors(
            containerColor = Color.White,
            contentColor = Color.Black.copy(alpha = 0.92f),
            disabledContainerColor = Color.White.copy(alpha = 0.85f),
            disabledContentColor = Color.Black.copy(alpha = 0.55f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
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

/* -------------------- Halo (Samsung Face ID hissi) -------------------- */

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

    Canvas(modifier = Modifier.size(haloSizeDp)) {
        val maxR = this.size.minDimension / 2f

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

/* -------------------- Terms: linkli dipnot & dialog -------------------- */

@Composable
private fun TermsFooter(
    modifier: Modifier = Modifier,
    onPrivacyClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    val text = buildAnnotatedString {
        append("Giriş yaparak ")

        pushStringAnnotation(tag = "PRIVACY", annotation = "privacy")
        withStyle(
            style = androidx.compose.ui.text.SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Gizlilik Politikası") }
        pop()

        append(" ve ")

        pushStringAnnotation(tag = "TERMS", annotation = "terms")
        withStyle(
            style = androidx.compose.ui.text.SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                textDecoration = TextDecoration.Underline
            )
        ) { append("Kullanım Şartları") }
        pop()

        append("’nı kabul etmiş olursun.")
    }

    androidx.compose.foundation.text.ClickableText(
        text = text,
        style = MaterialTheme.typography.labelSmall.copy(
            fontSize = 11.sp,
            lineHeight = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        ),
        modifier = modifier,
        onClick = { offset ->
            text.getStringAnnotations("PRIVACY", offset, offset).firstOrNull()?.let { onPrivacyClick() }
            text.getStringAnnotations("TERMS", offset, offset).firstOrNull()?.let { onTermsClick() }
        }
    )
}

@Composable
private fun TermsDialog(
    title: String,
    body: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false) // genişliği biz ayarlayalım
    ) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            tonalElevation = 6.dp,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ) {
                Box(Modifier.fillMaxWidth()) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterEnd)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Kapat")
                    }
                }

                Spacer(Modifier.height(8.dp))
                Divider()
                Spacer(Modifier.height(8.dp))

                // Kaydırılabilir içerik
                val scroll = rememberScrollState()
                Text(
                    text = body,
                    style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 20.sp),
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 180.dp, max = 420.dp)
                        .verticalScroll(scroll)
                )
                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Kapat")
                    }
                }
            }
        }
    }
}

/* -------------------- Placeholder içerikler -------------------- */

private const val PRIVACY_PLACEHOLDER = """
Gizlilik Politikası

Kendi Maceram uygulaması (“Uygulama”), kullanıcılarının gizliliğini ve kişisel verilerinin korunmasını son derece önemsemektedir. Bu Gizlilik Politikası, Uygulama üzerinden toplanan kişisel verilerin hangi amaçlarla işlendiğini, nasıl saklandığını, kimlerle paylaşıldığını ve kullanıcıların bu verilere ilişkin haklarını açıklamaktadır.

1. Toplanan Veriler
- Kimlik Verileri: Google hesabı ile giriş yapıldığında ad, soyad, e-posta adresi ve profil fotoğrafı.
- Kullanım Verileri: Uygulama içerisinde ziyaret edilen sayfalar, okunan hikâyeler, yapılan seçimler ve kullanım istatistikleri.
- Cihaz Verileri: Uygulamaya erişimde kullanılan cihaz türü, işletim sistemi ve sürümü.

2. Verilerin İşlenme Amaçları
- Kullanıcı kimliğinin doğrulanması ve giriş işlemlerinin sağlanması.
- Uygulamanın güvenliğinin sağlanması ve kötüye kullanımın önlenmesi.
- Kullanıcı deneyiminin geliştirilmesi, içeriklerin kişiselleştirilmesi ve yeni özelliklerin sunulması.
- Yasal yükümlülüklerin yerine getirilmesi.

3. Verilerin Saklanması ve Güvenliği
- Kişisel veriler, güvenli sunucularda şifreleme yöntemleriyle korunur.
- Yetkisiz erişimi önlemek amacıyla gerekli teknik ve idari tedbirler alınmıştır.
- Veriler, yalnızca hizmetin sağlanması için gerekli süre boyunca saklanır.

4. Verilerin Üçüncü Kişilerle Paylaşımı
- Kişisel veriler, yasal bir zorunluluk olmadıkça üçüncü kişilerle paylaşılmaz.
- Google Firebase gibi hizmet sağlayıcıları yalnızca kimlik doğrulama ve veri saklama amacıyla kullanılmaktadır.

5. Kullanıcı Hakları
Kullanıcılar aşağıdaki haklara sahiptir:
- Kişisel verilerinin işlenip işlenmediğini öğrenme.
- İşlenen verilere erişme ve bunların düzeltilmesini talep etme.
- Verilerin silinmesini veya anonim hale getirilmesini isteme.
- İşlemeye itiraz etme veya kısıtlama talep etme.
- İlgili veri koruma otoritelerine şikâyette bulunma.

6. Çocukların Gizliliği
Uygulama 13 yaş altı çocuklara yönelik değildir. 13 yaş altındaki çocuklardan bilerek kişisel veri toplanmamaktadır.

7. Değişiklikler
Bu Gizlilik Politikası zaman zaman güncellenebilir. Güncellenen politika, Uygulama üzerinden kullanıcılara sunulur.

İletişim
Her türlü soru veya talebiniz için bizimle şu adresten iletişime geçebilirsiniz:
E-posta: orcadev2025@gmail.com

"""

private const val TERMS_PLACEHOLDER = """
Kullanım Şartları

Kendi Maceram uygulamasını (“Uygulama”) indirerek ve kullanarak aşağıdaki şartları kabul etmiş olursunuz. Bu şartları kabul etmiyorsanız lütfen Uygulamayı kullanmayınız.

1. Hizmetin Tanımı
Uygulama, kullanıcılara etkileşimli hikâye deneyimi sunmayı amaçlayan bir mobil platformdur. Uygulama ücretsiz olarak sunulabilir, ancak ileride ek özellikler veya ücretli hizmetler devreye alınabilir.

2. Kullanım Koşulları
- Kullanıcı, Uygulama’yı yalnızca kişisel ve hukuka uygun amaçlarla kullanabilir.
- Uygulama’nın herhangi bir bölümünün kopyalanması, değiştirilmesi, satılması veya ticari amaçlarla kullanılması yasaktır.
- Uygulama’yı kullanırken yürürlükteki tüm yasalara uymak kullanıcıların sorumluluğundadır.

3. Hesap ve Güvenlik
- Google hesabı ile giriş yaparak Uygulama’ya erişim sağlanır.
- Hesap bilgilerinin gizliliği kullanıcının sorumluluğundadır.
- Şüpheli veya yetkisiz bir kullanım durumunda Uygulama, kullanıcıya haber vermeden erişimi askıya alabilir.

4. İçerikler
- Uygulama içerisindeki hikâyeler, metinler, grafikler ve diğer materyaller yalnızca bilgilendirme ve eğlence amaçlıdır.
- Uygulama, kullanıcı tarafından oluşturulan içeriklerden sorumlu tutulamaz.
- İçeriklerin doğruluğu ve sürekliliği garanti edilmez.

5. Sorumluluk Reddi
- Uygulama’nın kesintisiz, hatasız veya virüssüz çalışacağı garanti edilmez.
- Uygulama’nın kullanımı sonucunda doğabilecek doğrudan veya dolaylı zararlardan geliştirici sorumlu tutulamaz.

6. Değişiklik Hakkı
Uygulama, önceden bildirimde bulunmaksızın hizmetin kapsamını, içeriklerini ve kullanım koşullarını değiştirme hakkını saklı tutar. Güncellenen şartlar Uygulama’da yayımlandığı andan itibaren geçerli olur.

7. Fesih
Kullanıcı, bu şartlara aykırı davranması halinde Uygulama’yı kullanma hakkını kaybeder ve hesabı kapatılabilir.

8. Uygulanacak Hukuk
Bu şartlar, yürürlükteki Türkiye Cumhuriyeti yasalarına tabidir. Her türlü uyuşmazlıkta İstanbul Mahkemeleri ve İcra Daireleri yetkilidir.

İletişim
Kullanım şartlarıyla ilgili her türlü sorunuz için bizimle iletişime geçebilirsiniz:
E-posta: orcadev2025@gmail.com

"""

/* -------------------- Google client helper & util -------------------- */

private fun googleClient(context: android.content.Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

private fun lerpFloat(start: Float, end: Float, t: Float): Float {
    return start + (end - start) * t
}
