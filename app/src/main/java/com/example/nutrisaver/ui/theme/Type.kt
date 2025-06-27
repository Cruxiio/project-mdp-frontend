package com.example.nutrisaver.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.nutrisaver.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)

val OpenSans = FontFamily(
    Font(R.font.open_sans_regular),
    Font(R.font.open_sans_bold, FontWeight.Bold),
    Font(R.font.open_sans_semi_bold, FontWeight.SemiBold),
    Font(R.font.open_sans_light, FontWeight.Light),
    Font(R.font.open_sans_extra_bold, FontWeight.ExtraBold)
)

val Roboto = FontFamily(
    Font(R.font.roboto_regular),
    Font(R.font.roboto_bold, FontWeight.Bold),
    Font(R.font.roboto_semi_bold, FontWeight.SemiBold),
    Font(R.font.roboto_extra_bold, FontWeight.ExtraBold),
    Font(R.font.roboto_light, FontWeight.Light),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_black, FontWeight.Black),
    Font(R.font.roboto_extra_light, FontWeight.ExtraLight),
    Font(R.font.roboto_thin, FontWeight.Thin),
)