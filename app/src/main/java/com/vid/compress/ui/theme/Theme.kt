package com.vid.compress.ui.theme


import android.app.Activity
import android.view.Window
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import com.vid.compress.R

private val DarkColorPalette = darkColors(
        primary = Background3,
        primaryVariant = LightGray,
        secondary = Background2,
        onSecondary = LightGray
)

private val LightColorPalette = lightColors(
        primary = LightBlue,
        primaryVariant = LighterBlue,
        secondary = Teal200,
        onSecondary = textColorTitleLight


        /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun VideoCompressorTheme(darkTheme: Boolean = isSystemInDarkTheme(),activity:Activity, content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette

    } else {
        LightColorPalette
    }
    activity.window.statusBarColor=activity.getColor(if(darkTheme)R.color.DarkStatusBar else R.color.LighterBlue)


    MaterialTheme(
            colors = colors,
            typography = Typography,
            shapes = Shapes,
            content = content

    )
}