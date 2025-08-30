package com.kendimaceram.app.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = GPGreen,
    onPrimary = GPDarkCharcoal,
    secondary = GPGray,
    background = GPDarkCharcoal,
    surface = GPCardSurface,
    onBackground = Color.White,
    onSurface = GPLightGray
)

private val LightColorScheme = lightColorScheme(
    primary = GPGreen,
    onPrimary = Color.White,
    secondary = GPGray,
    background = Color.White,
    surface = Color(0xFFF0F2F5),
    onBackground = GPDarkCharcoal,
    onSurface = GPDarkCharcoal
)

@Composable
fun KendiMaceramTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor = false
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalView.current.context
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // Üst ve alt barları şeffaf yapıyoruz
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            // Bu komut, sisteme "benim uygulamam sistem barlarını kendisi yönetecek" der.
            WindowCompat.setDecorFitsSystemWindows(window, false)

            val insetsController = WindowCompat.getInsetsController(window, view)

            // Barlar şeffaf olduğu için, üzerindeki ikonların (saat, pil, geri tuşu)
            // temanın aydınlık veya karanlık olmasına göre rengini ayarla.
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}