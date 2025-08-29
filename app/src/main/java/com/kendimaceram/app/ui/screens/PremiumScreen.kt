package com.kendimaceram.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.kendimaceram.app.ui.components.MainScaffold

@Composable
fun PremiumScreen(navController: NavController) {
    val context = LocalContext.current
    var selectedPlan by remember { mutableStateOf("yearly") }

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Premium'a Yükselt",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tüm maceraların kilidini aç.",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PricingOptionCard(
                    title = "Aylık",
                    price = "9.99 TL",
                    billingCycle = "/ay",
                    isSelected = selectedPlan == "monthly",
                    onClick = { selectedPlan = "monthly" }
                )
                PricingOptionCard(
                    title = "Yıllık",
                    price = "99.99 TL",
                    billingCycle = "/yıl",
                    tag = "En Avantajlı",
                    isSelected = selectedPlan == "yearly",
                    onClick = { selectedPlan = "yearly" }
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PremiumFeature(text = "Reklamsız Deneyim")
                PremiumFeature(text = "Tüm Hikayelere Sınırsız Erişim")
                PremiumFeature(text = "Çevrimdışı Okuma")
            }
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                    val planText = if (selectedPlan == "yearly") "Yıllık" else "Aylık"
                    Toast.makeText(context, "$planText plan seçildi. Ödeme sistemi yakında!", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Text("Devam Et", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun PricingOptionCard(
    title: String,
    price: String,
    billingCycle: String,
    isSelected: Boolean,
    tag: String? = null,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onClick,
                colors = RadioButtonDefaults.colors(selectedColor = MaterialTheme.colorScheme.primary)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (tag != null) {
                    Text(
                        text = tag,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(text = title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            }
            Row {
                Text(text = price, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                Text(
                    text = billingCycle,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.Bottom).padding(start = 4.dp, bottom = 4.dp)
                )
            }
        }
    }
}

@Composable
fun PremiumFeature(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(text = text, fontSize = 16.sp)
    }
}