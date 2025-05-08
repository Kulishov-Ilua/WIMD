package ru.kulishov.wimd

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.ComposeUIViewController

fun MainViewController() = ComposeUIViewController {
    val db = getRoomDatabase(getDatabaseBuilder())
    AppTheme {
        App(
            db,
            MaterialTheme.colors.primary,
            MaterialTheme.colors.background,
            MaterialTheme.typography.h2,
            MaterialTheme.typography.body1,
            MaterialTheme.typography.h1
        )

    }
}
@Composable
fun AppTheme(content:@Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
            lightColors(
                background = Color.White,//Color(22, 22, 22),
                primary = Color(63, 89, 156),
                surface = Color(22, 22, 22)
            )
        } else {
            darkColors(
                background =Color(255, 245, 225),
                primary = Color(63, 89, 156),
                surface = Color(255, 245, 225),
            )
        },
        content = content,
        typography = Typography(
            h2 = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
            h1 = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 40.sp
            ),
            body1 = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    )
}