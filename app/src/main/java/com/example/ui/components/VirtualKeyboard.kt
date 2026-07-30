package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import com.example.ui.theme.EditorialDarkPurple
import com.example.ui.theme.EditorialPurple
import com.example.util.HangulAutomaton

// Dark Theme Colors matching Samsung / OneUI Dark Keyboard
private val KeyboardDarkBg = Color(0xFF141518)
private val KeyStandardBg = Color(0xFF282A2F)
private val KeyFunctionBg = Color(0xFF1E2024)
private val KeyActiveBg = Color(0xFFE2E3E8)
private val KeyTextColor = Color(0xFFFFFFFF)
private val KeySubTextColor = Color(0xFFA0A3AB)
private val SearchAccentColor = Color(0xFF8AB4F8)

private object ScreenBottomPositionProvider : PopupPositionProvider {
    override fun calculatePosition(
        anchorBounds: IntRect,
        windowSize: IntSize,
        layoutDirection: LayoutDirection,
        popupContentSize: IntSize
    ): IntOffset {
        return IntOffset(
            x = (windowSize.width - popupContentSize.width) / 2,
            y = windowSize.height - popupContentSize.height
        )
    }
}

enum class KeyboardMode {
    KOREAN, ENGLISH, SYMBOLS
}

@Composable
fun VirtualKeyboardToggleBar(
    isOpen: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        color = if (isOpen) KeyboardDarkBg else Color(0xFFF3EDF7),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .testTag("virtual_keyboard_toggle_bar")
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isOpen) Icons.Default.KeyboardHide else Icons.Default.Keyboard,
                    contentDescription = null,
                    tint = if (isOpen) Color.White else EditorialDarkPurple,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isOpen) "⌨️ 자체 한글 가상 자판 닫기" else "⌨️ 터치식 한글/영문 가상 자판 열기",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isOpen) Color.White else EditorialDarkPurple
                    )
                )
            }
            Text(
                text = if (isOpen) "접기 ▲" else "펼치기 ▼",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = if (isOpen) Color.White.copy(alpha = 0.8f) else Color(0xFF616161)
                )
            )
        }
    }
}

@Composable
fun OnScreenVirtualKeyboard(
    currentText: String,
    onTextChange: (String) -> Unit,
    isVisible: Boolean,
    onClose: (() -> Unit)? = null,
    headerContent: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(KeyboardMode.KOREAN) }
    var isShifted by remember { mutableStateOf(false) }

    if (isVisible) {
        Popup(
            popupPositionProvider = ScreenBottomPositionProvider,
            onDismissRequest = { onClose?.invoke() },
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = false
            )
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Column(
                    modifier = modifier.fillMaxWidth()
                ) {
                    if (headerContent != null) {
                        headerContent()
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("on_screen_virtual_keyboard"),
                        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = KeyboardDarkBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 6.dp, vertical = 6.dp)
                        ) {
                            // Top Bar: Toolbar items matching screenshot (Emoji, AI, Layout, Clipboard, Settings, More)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🙂", fontSize = 16.sp)
                                    Text("✨", fontSize = 16.sp)
                                    Text("⌨️", fontSize = 16.sp)
                                    Text("📋", fontSize = 16.sp)
                                    Text("⚙️", fontSize = 16.sp)
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Quick English to Korean converter
                                    Surface(
                                        color = KeyFunctionBg,
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier.clickable {
                                            val converted = HangulAutomaton.englishToHangul(currentText)
                                            onTextChange(converted)
                                        }
                                    ) {
                                        Text(
                                            text = "영타➔한글",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Medium,
                                                color = KeySubTextColor,
                                                fontSize = 11.sp
                                            ),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                        )
                                    }

                                    if (onClose != null) {
                                        Text(
                                            text = "✕",
                                            color = KeySubTextColor,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .clickable { onClose() }
                                                .padding(horizontal = 4.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Top Number Row (Always present like Samsung Keyboard)
                            val numbersRow = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")
                            KeyRow(numbersRow, keyBg = KeyStandardBg) { num ->
                                onTextChange(currentText + num)
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            // Character Layout Rows based on Mode
                            when (mode) {
                                KeyboardMode.KOREAN -> {
                                    val row1 = if (isShifted) listOf("ㅃ","ㅉ","ㄸ","ㄲ","ㅆ","ㅛ","ㅕ","ㅑ","ㅒ","ㅖ")
                                    else listOf("ㅂ","ㅈ","ㄷ","ㄱ","ㅅ","ㅛ","ㅕ","ㅑ","ㅐ","ㅔ")

                                    val row2 = listOf("ㅁ","ㄴ","ㅇ","ㄹ","ㅎ","ㅗ","ㅓ","ㅏ","ㅣ")
                                    val row3 = listOf("ㅋ","ㅌ","ㅊ","ㅍ","ㅠ","ㅜ","ㅡ")

                                    KeyRow(row1, keyBg = KeyStandardBg) { char ->
                                        onTextChange(HangulAutomaton.appendJamo(currentText, char[0]))
                                        if (isShifted) isShifted = false
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))

                                    KeyRow(row2, keyBg = KeyStandardBg) { char ->
                                        onTextChange(HangulAutomaton.appendJamo(currentText, char[0]))
                                        if (isShifted) isShifted = false
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))

                                    // Row 3 with Shift on Left and Delete on Right
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        // Shift Key
                                        KeyButton(
                                            label = "⇧",
                                            bgColor = if (isShifted) KeyActiveBg else KeyFunctionBg,
                                            textColor = if (isShifted) Color.Black else KeyTextColor,
                                            modifier = Modifier.weight(1.3f)
                                        ) {
                                            isShifted = !isShifted
                                        }

                                        row3.forEach { char ->
                                            KeyButton(
                                                label = char,
                                                bgColor = KeyStandardBg,
                                                textColor = KeyTextColor,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                onTextChange(HangulAutomaton.appendJamo(currentText, char[0]))
                                                if (isShifted) isShifted = false
                                            }
                                        }

                                        // Backspace Key
                                        KeyButton(
                                            label = "⌫",
                                            bgColor = KeyFunctionBg,
                                            textColor = KeyTextColor,
                                            modifier = Modifier.weight(1.3f)
                                        ) {
                                            onTextChange(HangulAutomaton.deleteLast(currentText))
                                        }
                                    }
                                }

                                KeyboardMode.ENGLISH -> {
                                    val row1 = if (isShifted) listOf("Q","W","E","R","T","Y","U","I","O","P")
                                    else listOf("q","w","e","r","t","y","u","i","o","p")

                                    val row2 = if (isShifted) listOf("A","S","D","F","G","H","J","K","L")
                                    else listOf("a","s","d","f","g","h","j","k","l")

                                    val row3 = if (isShifted) listOf("Z","X","C","V","B","N","M")
                                    else listOf("z","x","c","v","b","n","m")

                                    KeyRow(row1, keyBg = KeyStandardBg) { char ->
                                        onTextChange(currentText + char)
                                        if (isShifted) isShifted = false
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))

                                    KeyRow(row2, keyBg = KeyStandardBg) { char ->
                                        onTextChange(currentText + char)
                                        if (isShifted) isShifted = false
                                    }
                                    Spacer(modifier = Modifier.height(3.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        KeyButton(
                                            label = "⇧",
                                            bgColor = if (isShifted) KeyActiveBg else KeyFunctionBg,
                                            textColor = if (isShifted) Color.Black else KeyTextColor,
                                            modifier = Modifier.weight(1.3f)
                                        ) {
                                            isShifted = !isShifted
                                        }

                                        row3.forEach { char ->
                                            KeyButton(
                                                label = char,
                                                bgColor = KeyStandardBg,
                                                textColor = KeyTextColor,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                onTextChange(currentText + char)
                                                if (isShifted) isShifted = false
                                            }
                                        }

                                        KeyButton(
                                            label = "⌫",
                                            bgColor = KeyFunctionBg,
                                            textColor = KeyTextColor,
                                            modifier = Modifier.weight(1.3f)
                                        ) {
                                            if (currentText.isNotEmpty()) onTextChange(currentText.dropLast(1))
                                        }
                                    }
                                }

                                KeyboardMode.SYMBOLS -> {
                                    val row1 = listOf("!","@","#","$","%","^","&","*","(",")")
                                    val row2 = listOf("-","=","+","?",";",":","'","\"","<",">")
                                    val row3 = listOf("[","]","{","}","\\","|","~","`")

                                    KeyRow(row1, keyBg = KeyStandardBg) { onTextChange(currentText + it) }
                                    Spacer(modifier = Modifier.height(3.dp))

                                    KeyRow(row2, keyBg = KeyStandardBg) { onTextChange(currentText + it) }
                                    Spacer(modifier = Modifier.height(3.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        KeyButton(
                                            label = "1/2",
                                            bgColor = KeyFunctionBg,
                                            textColor = KeySubTextColor,
                                            modifier = Modifier.weight(1.2f)
                                        ) {}

                                        row3.forEach { char ->
                                            KeyButton(
                                                label = char,
                                                bgColor = KeyStandardBg,
                                                textColor = KeyTextColor,
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                onTextChange(currentText + char)
                                            }
                                        }

                                        KeyButton(
                                            label = "⌫",
                                            bgColor = KeyFunctionBg,
                                            textColor = KeyTextColor,
                                            modifier = Modifier.weight(1.2f)
                                        ) {
                                            if (currentText.isNotEmpty()) onTextChange(currentText.dropLast(1))
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(3.dp))

                            // Bottom Function Row: !#1 | 한/영 | , | Spacebar (—) | . | 🔍 (Search/Enter)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Mode Button !#1
                                KeyButton(
                                    label = if (mode == KeyboardMode.SYMBOLS) "가" else "!#1",
                                    bgColor = KeyFunctionBg,
                                    textColor = KeyTextColor,
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    mode = if (mode == KeyboardMode.SYMBOLS) KeyboardMode.KOREAN else KeyboardMode.SYMBOLS
                                }

                                // Lang Switch 한/영
                                KeyButton(
                                    label = if (mode == KeyboardMode.KOREAN) "한/영" else "한/영",
                                    bgColor = KeyFunctionBg,
                                    textColor = KeyTextColor,
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    mode = if (mode == KeyboardMode.KOREAN) KeyboardMode.ENGLISH else KeyboardMode.KOREAN
                                }

                                // Comma
                                KeyButton(
                                    label = ",",
                                    bgColor = KeyFunctionBg,
                                    textColor = KeyTextColor,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onTextChange(currentText + ",")
                                }

                                // Space Bar with pill symbol —
                                KeyButton(
                                    label = "—",
                                    bgColor = KeyStandardBg,
                                    textColor = KeySubTextColor,
                                    modifier = Modifier.weight(3.5f)
                                ) {
                                    onTextChange(currentText + " ")
                                }

                                // Period
                                KeyButton(
                                    label = ".",
                                    bgColor = KeyFunctionBg,
                                    textColor = KeyTextColor,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onTextChange(currentText + ".")
                                }

                                // Search / Enter Action
                                KeyButton(
                                    label = "🔍",
                                    bgColor = KeyFunctionBg,
                                    textColor = SearchAccentColor,
                                    modifier = Modifier.weight(1.2f)
                                ) {
                                    onClose?.invoke()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun KeyRow(
    keys: List<String>,
    keyBg: Color = KeyStandardBg,
    onKeyClick: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        keys.forEach { key ->
            KeyButton(
                label = key,
                bgColor = keyBg,
                textColor = KeyTextColor,
                modifier = Modifier.weight(1f)
            ) {
                onKeyClick(key)
            }
        }
    }
}

@Composable
private fun KeyButton(
    label: String,
    bgColor: Color = KeyStandardBg,
    textColor: Color = KeyTextColor,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
            .height(44.dp)
            .clickable { onClick() }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.padding(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    fontSize = 17.sp
                )
            )
        }
    }
}
