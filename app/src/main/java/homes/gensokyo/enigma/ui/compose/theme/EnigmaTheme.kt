package homes.gensokyo.enigma.ui.compose.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import homes.gensokyo.enigma.R

//品牌点缀金（与启动图标的金点同源），仅作小面积强调，不进 colorScheme
val AccentGold = Color(0xFFFFD66B)

private val LightColors
    @Composable get() = lightColorScheme(
        primary = colorResource(R.color.md_theme_primary),
        onPrimary = colorResource(R.color.md_theme_on_primary),
        primaryContainer = colorResource(R.color.md_theme_primary_container),
        onPrimaryContainer = colorResource(R.color.md_theme_on_primary_container),
        secondary = colorResource(R.color.md_theme_secondary),
        onSecondary = colorResource(R.color.md_theme_on_secondary),
        secondaryContainer = colorResource(R.color.md_theme_secondary_container),
        onSecondaryContainer = colorResource(R.color.md_theme_on_secondary_container),
        tertiary = colorResource(R.color.md_theme_tertiary),
        onTertiary = colorResource(R.color.md_theme_on_tertiary),
        tertiaryContainer = colorResource(R.color.md_theme_tertiary_container),
        onTertiaryContainer = colorResource(R.color.md_theme_on_tertiary_container),
        background = colorResource(R.color.md_theme_background),
        onBackground = colorResource(R.color.md_theme_on_background),
        surface = colorResource(R.color.md_theme_surface),
        onSurface = colorResource(R.color.md_theme_on_surface),
        surfaceVariant = colorResource(R.color.md_theme_surface_variant),
        onSurfaceVariant = colorResource(R.color.md_theme_on_surface_variant),
        surfaceContainerHigh = colorResource(R.color.md_theme_surface_container_high),
        outline = colorResource(R.color.md_theme_outline),
    )

private val DarkColors
    @Composable get() = darkColorScheme(
        primary = colorResource(R.color.md_theme_primary),
        onPrimary = colorResource(R.color.md_theme_on_primary),
        primaryContainer = colorResource(R.color.md_theme_primary_container),
        onPrimaryContainer = colorResource(R.color.md_theme_on_primary_container),
        secondary = colorResource(R.color.md_theme_secondary),
        onSecondary = colorResource(R.color.md_theme_on_secondary),
        secondaryContainer = colorResource(R.color.md_theme_secondary_container),
        onSecondaryContainer = colorResource(R.color.md_theme_on_secondary_container),
        tertiary = colorResource(R.color.md_theme_tertiary),
        onTertiary = colorResource(R.color.md_theme_on_tertiary),
        tertiaryContainer = colorResource(R.color.md_theme_tertiary_container),
        onTertiaryContainer = colorResource(R.color.md_theme_on_tertiary_container),
        background = colorResource(R.color.md_theme_background),
        onBackground = colorResource(R.color.md_theme_on_background),
        surface = colorResource(R.color.md_theme_surface),
        onSurface = colorResource(R.color.md_theme_on_surface),
        surfaceVariant = colorResource(R.color.md_theme_surface_variant),
        onSurfaceVariant = colorResource(R.color.md_theme_on_surface_variant),
        surfaceContainerHigh = colorResource(R.color.md_theme_surface_container_high),
        outline = colorResource(R.color.md_theme_outline),
    )

@Composable
fun EnigmaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, content = content)
}
