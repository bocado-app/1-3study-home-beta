package com.example.util

object HangulAutomaton {
    private val CHO = charArrayOf(
        'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ', 'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )
    private val JUNG = charArrayOf(
        'ㅏ', 'ㅐ', 'ㅑ', 'ㅒ', 'ㅓ', 'ㅔ', 'ㅕ', 'ㅖ', 'ㅗ', 'ㅘ', 'ㅙ', 'ㅚ', 'ㅛ', 'ㅜ', 'ㅝ', 'ㅞ', 'ㅟ', 'ㅠ', 'ㅡ', 'ㅢ', 'ㅣ'
    )
    private val JONG = charArrayOf(
        ' ', 'ㄱ', 'ㄲ', 'ㄳ', 'ㄴ', 'ㄵ', 'ㄶ', 'ㄷ', 'ㄹ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅁ', 'ㅂ', 'ㅄ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'
    )

    private val COMPLEX_JUNG = mapOf(
        "ㅗㅏ" to 'ㅘ', "ㅗㅐ" to 'ㅙ', "ㅗㅣ" to 'ㅚ',
        "ㅜㅓ" to 'ㅝ', "ㅜㅔ" to 'ㅞ', "ㅜㅣ" to 'ㅟ',
        "ㅡㅣ" to 'ㅢ'
    )

    private val COMPLEX_JONG = mapOf(
        "ㄱㅅ" to 'ㄳ', "ㄴㅈ" to 'ㄵ', "ㄴㅎ" to 'ㄶ',
        "ㄹㄱ" to 'ㄺ', "ㄹㅁ" to 'ㄻ', "ㄹㅂ" to 'ㄼ',
        "ㄹㅅ" to 'ㄽ', "ㄹㅌ" to 'ㄾ', "ㄹㅍ" to 'ㄿ',
        "ㄹㅎ" to 'ㅀ', "ㅂㅅ" to 'ㅄ'
    )

    private val DECOMP_JONG = mapOf(
        'ㄳ' to Pair('ㄱ', 'ㅅ'), 'ㄵ' to Pair('ㄴ', 'ㅈ'), 'ㄶ' to Pair('ㄴ', 'ㅎ'),
        'ㄺ' to Pair('ㄹ', 'ㄱ'), 'ㄻ' to Pair('ㄹ', 'ㅁ'), 'ㄼ' to Pair('ㄹ', 'ㅂ'),
        'ㄽ' to Pair('ㄹ', 'ㅅ'), 'ㄾ' to Pair('ㄹ', 'ㅌ'), 'ㄿ' to Pair('ㄹ', 'ㅍ'),
        'ㅀ' to Pair('ㄹ', 'ㅎ'), 'ㅄ' to Pair('ㅂ', 'ㅅ')
    )

    fun isHangulJamo(c: Char): Boolean {
        return c in CHO || c in JUNG || c in 'ㄱ'..'ㅎ' || c in 'ㅏ'..'ㅣ'
    }

    private fun isConsonant(c: Char): Boolean = CHO.contains(c) || c in listOf('ㄳ', 'ㄵ', 'ㄶ', 'ㄺ', 'ㄻ', 'ㄼ', 'ㄽ', 'ㄾ', 'ㄿ', 'ㅀ', 'ㅄ')
    private fun isVowel(c: Char): Boolean = JUNG.contains(c)

    private fun makeSyllable(cho: Char, jung: Char, jong: Char = ' '): Char {
        val choIdx = CHO.indexOf(cho)
        val jungIdx = JUNG.indexOf(jung)
        val jongIdx = JONG.indexOf(jong)
        if (choIdx < 0 || jungIdx < 0 || jongIdx < 0) return ' '
        return (0xAC00 + (choIdx * 21 + jungIdx) * 28 + jongIdx).toChar()
    }

    private fun decomposeSyllable(c: Char): Triple<Char, Char, Char>? {
        if (c !in '가'..'힣') return null
        val code = c.code - 0xAC00
        val jongIdx = code % 28
        val jungIdx = (code / 28) % 21
        val choIdx = code / (28 * 21)
        return Triple(CHO[choIdx], JUNG[jungIdx], JONG[jongIdx])
    }

    fun appendJamo(currentText: String, inputJamo: Char): String {
        if (currentText.isEmpty()) return inputJamo.toString()

        val lastChar = currentText.last()
        val prefix = currentText.dropLast(1)

        // Case 1: Last char is a Hangul Syllable ('가'..'힣')
        val decomp = decomposeSyllable(lastChar)
        if (decomp != null) {
            val (cho, jung, jong) = decomp

            // If input is a Vowel
            if (isVowel(inputJamo)) {
                if (jong == ' ') {
                    // Try combining compound vowel (e.g., ㅗ + ㅏ = ㅘ)
                    val combinedVowel = COMPLEX_JUNG["$jung$inputJamo"]
                    if (combinedVowel != null) {
                        return prefix + makeSyllable(cho, combinedVowel, ' ')
                    }
                } else {
                    // Jong exists. Move jong (or second part of compound jong) to next syllable cho!
                    val decompJong = DECOMP_JONG[jong]
                    return if (decompJong != null) {
                        // e.g., 닭 + ㅏ -> 달 + 가
                        val firstJong = decompJong.first
                        val nextCho = decompJong.second
                        prefix + makeSyllable(cho, jung, firstJong) + makeSyllable(nextCho, inputJamo, ' ')
                    } else {
                        // e.g., 각 + ㅏ -> 가 + 개
                        prefix + makeSyllable(cho, jung, ' ') + makeSyllable(jong, inputJamo, ' ')
                    }
                }
            } else if (isConsonant(inputJamo)) {
                // Input is a Consonant
                if (jong == ' ') {
                    // Try setting as jong
                    if (JONG.contains(inputJamo)) {
                        return prefix + makeSyllable(cho, jung, inputJamo)
                    }
                } else {
                    // Try making compound jong (e.g., ㄱ + ㅅ = ㄳ)
                    val combinedJong = COMPLEX_JONG["$jong$inputJamo"]
                    if (combinedJong != null) {
                        return prefix + makeSyllable(cho, jung, combinedJong)
                    }
                }
            }
        }

        // Case 2: Last char is a standalone Jamo
        if (isHangulJamo(lastChar)) {
            if (isConsonant(lastChar) && isVowel(inputJamo)) {
                // e.g., ㄱ + ㅏ = 가
                return prefix + makeSyllable(lastChar, inputJamo, ' ')
            }
            if (isVowel(lastChar) && isVowel(inputJamo)) {
                // e.g., ㅗ + ㅏ = ㅘ
                val combined = COMPLEX_JUNG["$lastChar$inputJamo"]
                if (combined != null) {
                    return prefix + combined
                }
            }
        }

        return currentText + inputJamo
    }

    fun deleteLast(currentText: String): String {
        if (currentText.isEmpty()) return ""
        val lastChar = currentText.last()
        val prefix = currentText.dropLast(1)

        val decomp = decomposeSyllable(lastChar)
        if (decomp != null) {
            val (cho, jung, jong) = decomp

            if (jong != ' ') {
                val decompJong = DECOMP_JONG[jong]
                return if (decompJong != null) {
                    // 닭 -> 달
                    prefix + makeSyllable(cho, jung, decompJong.first)
                } else {
                    // 각 -> 가
                    prefix + makeSyllable(cho, jung, ' ')
                }
            } else {
                // Check if jung is compound vowel
                val jungStr = jung.toString()
                for ((key, value) in COMPLEX_JUNG) {
                    if (value == jung) {
                        // ㅘ -> ㅗ
                        return prefix + makeSyllable(cho, key[0], ' ')
                    }
                }
                // 가 -> ㄱ
                return prefix + cho
            }
        }

        return prefix
    }

    private val QWERTY_TO_JAMO = mapOf(
        'q' to 'ㅂ', 'Q' to 'ㅃ', 'w' to 'ㅈ', 'W' to 'ㅉ',
        'e' to 'ㄷ', 'E' to 'ㄸ', 'r' to 'ㄱ', 'R' to 'ㄲ',
        't' to 'ㅅ', 'T' to 'ㅆ', 'y' to 'ㅛ', 'Y' to 'ㅛ',
        'u' to 'ㅕ', 'U' to 'ㅕ', 'i' to 'ㅑ', 'I' to 'ㅑ',
        'o' to 'ㅐ', 'O' to 'ㅒ', 'p' to 'ㅔ', 'P' to 'ㅖ',
        'a' to 'ㅁ', 'A' to 'ㅁ', 's' to 'ㄴ', 'S' to 'ㄴ',
        'd' to 'ㅇ', 'D' to 'ㅇ', 'f' to 'ㄹ', 'F' to 'ㄹ',
        'g' to 'ㅎ', 'G' to 'ㅎ', 'h' to 'ㅗ', 'H' to 'ㅗ',
        'j' to 'ㅓ', 'J' to 'ㅓ', 'k' to 'ㅏ', 'K' to 'ㅏ',
        'l' to 'ㅣ', 'L' to 'ㅣ', 'z' to 'ㅋ', 'Z' to 'ㅋ',
        'x' to 'ㅌ', 'X' to 'ㅌ', 'c' to 'ㅊ', 'C' to 'ㅊ',
        'v' to 'ㅍ', 'V' to 'ㅍ', 'b' to 'ㅠ', 'B' to 'ㅠ',
        'n' to 'ㅜ', 'N' to 'ㅜ', 'm' to 'ㅡ', 'M' to 'ㅡ'
    )

    fun englishToHangul(englishInput: String): String {
        var result = ""
        for (ch in englishInput) {
            val jamo = QWERTY_TO_JAMO[ch]
            if (jamo != null) {
                result = appendJamo(result, jamo)
            } else {
                result += ch
            }
        }
        return result
    }
}
