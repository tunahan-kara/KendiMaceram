package com.kendimaceram.app.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// YAZILARIMIZA EKLEYECEĞİMİZ GÖLGE EFEKTİ
// Offset(x, y) gölgenin ne kadar sağa/aşağı kayacağını, blurRadius ise ne kadar bulanık olacağını belirler.
val textShadow = Shadow(
    color = Color.Black.copy(alpha = 0.7f),
    offset = Offset(x = 2f, y = 4f),
    blurRadius = 4f
)

// Typography, uygulamadaki tüm standart metin stillerini tanımlar.
// Her bir stile 'shadow' parametresini ekliyoruz.
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        shadow = textShadow // Gölgeyi ekledik
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        shadow = textShadow // Gölgeyi ekledik
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        shadow = textShadow // Gölgeyi ekledik
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        shadow = textShadow // Gölgeyi ekledik
    )
)