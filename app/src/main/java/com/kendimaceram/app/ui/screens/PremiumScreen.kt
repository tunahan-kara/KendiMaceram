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
import androidx.compose.ui.draw.alpha
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

    val onBg = MaterialTheme.colorScheme.onBackground

    MainScaffold(navController = navController) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Başlık
            Text(
                text = "Premium'a Yükselt",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = onBg
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tüm maceraların kilidini aç.",
                style = MaterialTheme.typography.titleMedium,
                color = onBg.copy(alpha = 0.75f)
            )

            Spacer(Modifier.height(28.dp))

            // Planlar
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
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

            Spacer(Modifier.height(24.dp))

            // Özellikler (kart içinde daha okunaklı)
            FeaturesCard(
                features = listOf(
                    "Reklamsız Deneyim",
                    "Tüm Hikayelere Sınırsız Erişim",
                    "Çevrimdışı Okuma"
                )
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick = {
                    val planText = if (selectedPlan == "yearly") "Yıllık" else "Aylık"
                    Toast.makeText(
                        context,
                        "$planText plan seçildi. Ödeme sistemi yakında!",
                        Toast.LENGTH_SHORT
                    ).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .padding(bottom = 16.dp)
            ) {
                Text("Devam Et", fontSize = 16.sp)
            }
        }
    }
}

@Composable
private fun PricingOptionCard(
    title: String,
    price: String,
    billingCycle: String,
    isSelected: Boolean,
    tag: String? = null,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(16.dp)

    val border = if (isSelected)
        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    else
        BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)

    val container = if (isSelected)
        MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
    else
        MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = shape,
        border = border,
        colors = CardDefaults.cardColors(containerColor = container),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
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
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (tag != null) {
                    AssistChip(
                        onClick = {},
                        label = { Text(tag) },
                        leadingIcon = null,
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                            labelColor = MaterialTheme.colorScheme.primary
                        ),
                        border = AssistChipDefaults.assistChipBorder(
                            borderColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(Modifier.height(6.dp))
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Fiyat – alt satırla hizalı dursun
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = price,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = billingCycle,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 3.dp),
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                )
            }
        }
    }
}

fun AssistChip(onClick: () -> Unit, label: @Composable () -> Unit, leadingIcon: Nothing?, colors: ChipColors, border: ChipBorder) {

}

@Composable
private fun FeaturesCard(features: List<String>) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            features.forEachIndexed { i, text ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        text = text,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                if (i != features.lastIndex) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .alpha(0.5f)
                    )
                }
            }
        }
    }
}
