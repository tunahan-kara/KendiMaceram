package com.kendimaceram.app.ui.screens

import android.app.Activity
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.kendimaceram.app.R
import com.kendimaceram.app.ui.navigation.Screen
import kotlinx.coroutines.tasks.await
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountInfoScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val activity = context as Activity
    val auth = remember { FirebaseAuth.getInstance() }
    val firestore = remember { FirebaseFirestore.getInstance() }
    val user = auth.currentUser

    var showPrivacy by remember { mutableStateOf(false) }
    var showTerms by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var busy by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = Color.Transparent,
        // Bilerek system TopAppBar kullanmıyoruz; başlığı aşağıya özel bir alanla koyacağız.
        topBar = {},
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { inner ->

        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ------- Özel Başlık Alanı (geri + başlık) -------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri"
                    )
                }
                Spacer(Modifier.width(6.dp))
                Text(
                    text = "Hesap Bilgileri",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(8.dp))
            // --------------------------------------------------

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

                    // ÇIKIŞ
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

                    // GOOGLE ERİŞİMİNİ İPTAL
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
                                    Toast
                                        .makeText(context, "Google erişimi iptal edildi.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                    )

                    Divider()

                    // HESABI SİL (Soft Delete)
                    ListItem(
                        headlineContent = {
                            Text("Hesabı Sil", color = MaterialTheme.colorScheme.error)
                        },
                        supportingContent = {
                            Text(
                                "Hesabın 30 gün içinde silinmek üzere işaretlenecek.",
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
                                if (!busy) showDeleteConfirm = true
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

    // ----- Silme Onay Diyaloğu (Soft Delete) -----
    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { if (!busy) showDeleteConfirm = false },
            title = { Text("Hesap Silme Talebi") },
            text = {
                Text(
                    "Hesabın 30 gün içinde silinmek üzere işaretlenecek. " +
                            "Bu süre içinde fikrini değiştirirsen tekrar giriş yaparak talebi iptal edebilirsin. " +
                            "Devam etmek istiyor musun?"
                )
            },
            confirmButton = {
                TextButton(
                    enabled = !busy,
                    onClick = {
                        if (busy) return@TextButton
                        val u = FirebaseAuth.getInstance().currentUser
                        if (u == null) {
                            Toast.makeText(context, "Kullanıcı bulunamadı.", Toast.LENGTH_LONG).show()
                            showDeleteConfirm = false
                            return@TextButton
                        }
                        busy = true
                        // Firestore: delete/{uid} ve users/{uid} bayrağı
                        val uid = u.uid
                        val nowMs = System.currentTimeMillis()
                        val expiresMs = nowMs + 30L * 24 * 60 * 60 * 1000 // 30 gün

                        val data = hashMapOf(
                            "uid" to uid,
                            "email" to (u.email ?: ""),
                            "displayName" to (u.displayName ?: ""),
                            "photoUrl" to (u.photoUrl?.toString() ?: ""),
                            "requestedAt" to FieldValue.serverTimestamp(),
                            "expiresAt" to Timestamp(Date(expiresMs)),
                            "status" to "pending",
                            "note" to ""
                        )

                        val fs = FirebaseFirestore.getInstance()
                        val deleteRef = fs.collection("delete").document(uid)
                        val userRef = fs.collection("users").document(uid)

                        // zincir: delete doc -> users flag -> signOut -> login'e git
                        deleteRef.set(data, SetOptions.merge())
                            .continueWithTask { userRef.set(
                                mapOf(
                                    "pendingDeletion" to true,
                                    "deletionExpiresAt" to Timestamp(Date(expiresMs))
                                ),
                                SetOptions.merge()
                            ) }
                            .addOnSuccessListener {
                                busy = false
                                showDeleteConfirm = false
                                Toast.makeText(context, "Silme talebin alındı.", Toast.LENGTH_LONG).show()
                                // Oturumu kapatıp login ekranına dön
                                FirebaseAuth.getInstance().signOut()
                                googleClient(context).signOut().addOnCompleteListener {
                                    navController.navigate(Screen.Login.route) {
                                        popUpTo(0) { inclusive = true }
                                    }
                                }
                            }
                            .addOnFailureListener {
                                busy = false
                                Toast.makeText(
                                    context,
                                    it.localizedMessage ?: "Bir hata oluştu",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                    }
                ) { Text(if (busy) "Gönderiliyor..." else "Evet, talebi ilet") }
            },
            dismissButton = {
                TextButton(onClick = { if (!busy) showDeleteConfirm = false }) { Text("Vazgeç") }
            }
        )
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
                model = ImageRequest.Builder(LocalContext.current)
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

private const val TERMS_LONG = """
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
