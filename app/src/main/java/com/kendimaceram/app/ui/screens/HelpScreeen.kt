package com.kendimaceram.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold

@Composable
fun HelpScreen(navController: NavController) {
    MainScaffold(navController = navController) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            item {
                Text("Sıkça Sorulan Sorular", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                HelpItem("Hikayeleri nasıl indiririm?", "Keşfet sekmesine giderek, indirmek istediğiniz hikayenin yanındaki 'İndir' butonuna basmanız yeterlidir.")
                HelpItem("İndirdiğim hikayeleri nerede bulabilirim?", "Alt navigasyon çubuğundaki 'Hikayelerim' sekmesinde, telefonunuza indirilmiş olan tüm hikayeleri bulabilirsiniz.")
                HelpItem("Bir hikayeyi nasıl silerim?", "'Hikayelerim' sekmesinde, silmek istediğiniz hikayenin sağındaki çöp kutusu ikonuna basarak silebilirsiniz.")
                HelpItem("Premium üyelik ne işe yarar?", "Premium üyelik, tüm hikayelere reklamsız ve sınırsız erişim gibi birçok avantaj sunar. 'Premium' sekmesinden detayları inceleyebilirsiniz.")

                Spacer(modifier = Modifier.height(24.dp))
                Text("Destek", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Daha fazla sorun için destek@kendimaceram.com adresine e-posta gönderebilirsiniz.")
            }
        }
    }
}

@Composable
private fun HelpItem(question: String, answer: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(text = question, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        Text(text = answer, style = MaterialTheme.typography.bodyMedium)
    }
}