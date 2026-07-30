package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Editorial Aesthetic Theme Palette
val EditorialPurple = Color(0xFF6750A4)
val EditorialDarkPurple = Color(0xFF21005D)
val EditorialLightPurple = Color(0xFFEADDFF)
val EditorialFabPurple = Color(0xFFD0BCFF)
val BackgroundEditorial = Color(0xFFFEF7FF)
val SurfaceEditorial = Color(0xFFF3EDF7)
val BorderEditorial = Color(0xFFCAC4D0)
val TextDarkEditorial = Color(0xFF1D1B20)
val TextMutedEditorial = Color(0xFF49454F)

// Subject Specific Colors tuned to Editorial Aesthetic
val SubjectKorean = Color(0xFFB71C1C)      // Deep Editorial Red (국어)
val SubjectKoreanBg = Color(0xFFFFEBEE)

val SubjectMath = Color(0xFF1565C0)        // Deep Editorial Blue (수학)
val SubjectMathBg = Color(0xFFE3F2FD)

val SubjectSocial = Color(0xFF2E7D32)      // Deep Editorial Green (사회)
val SubjectSocialBg = Color(0xFFE8F5E9)

val SubjectScience = Color(0xFF00838F)     // Editorial Teal (과학)
val SubjectScienceBg = Color(0xFFE0F7FA)

val SubjectEnglish = Color(0xFF6750A4)     // Editorial Purple (영어)
val SubjectEnglishBg = Color(0xFFEADDFF)

val SubjectHistory = Color(0xFFC62828)     // Editorial Terracotta (한국사)
val SubjectHistoryBg = Color(0xFFFBE9E7)

fun getSubjectColor(subject: String): Color {
    return when (subject) {
        "국어" -> SubjectKorean
        "수학" -> SubjectMath
        "사회" -> SubjectSocial
        "과학" -> SubjectScience
        "영어" -> SubjectEnglish
        "한국사" -> SubjectHistory
        else -> EditorialPurple
    }
}

fun getSubjectBgColor(subject: String): Color {
    return when (subject) {
        "국어" -> SubjectKoreanBg
        "수학" -> SubjectMathBg
        "사회" -> SubjectSocialBg
        "과학" -> SubjectScienceBg
        "영어" -> SubjectEnglishBg
        "한국사" -> SubjectHistoryBg
        else -> EditorialLightPurple
    }
}

fun getSubjectIcon(subject: String): String {
    return when (subject) {
        "국어" -> "📖"
        "수학" -> "📐"
        "사회" -> "🌏"
        "과학" -> "🧪"
        "영어" -> "🔤"
        "한국사" -> "🏯"
        else -> "📚"
    }
}
