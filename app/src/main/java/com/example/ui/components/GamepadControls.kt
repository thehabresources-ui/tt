package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.North
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MarioRed
import com.example.ui.theme.OverallsBlue

@Composable
fun GamepadControls(
    onLeftPress: (Boolean) -> Unit,
    onRightPress: (Boolean) -> Unit,
    onJumpPress: (Boolean) -> Unit,
    onDashPress: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0xDD0F172A), Color(0xFF0F172A))
                )
            )
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // D-PAD: Left and Right
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DirectionalTouchButton(
                testTag = "dpad_left",
                label = "Kiri",
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Bergerak ke kiri",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                },
                onPressChange = onLeftPress
            )

            DirectionalTouchButton(
                testTag = "dpad_right",
                label = "Kanan",
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Bergerak ke kanan",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                },
                onPressChange = onRightPress
            )
        }

        // ACTION BUTTONS: [B] Lari / Dash, [A] Lompat / Jump
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // [B] Dash / Lari
            ActionTouchButton(
                testTag = "action_dash",
                letter = "B",
                subLabel = "LARI",
                color = OverallsBlue,
                onPressChange = onDashPress
            )

            // [A] Jump / Lompat
            ActionTouchButton(
                testTag = "action_jump",
                letter = "A",
                subLabel = "LOMPAT",
                color = MarioRed,
                onPressChange = onJumpPress
            )
        }
    }
}

@Composable
private fun DirectionalTouchButton(
    testTag: String,
    label: String,
    icon: @Composable () -> Unit,
    onPressChange: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .testTag(testTag)
            .size(58.dp)
            .shadow(if (isPressed) 2.dp else 6.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isPressed) Color(0xFF334155) else Color(0xFF1E293B)
            )
            .border(2.dp, if (isPressed) Color(0xFF94A3B8) else Color(0xFF475569), RoundedCornerShape(14.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressChange(true)
                        tryAwaitRelease()
                        isPressed = false
                        onPressChange(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        icon()
    }
}

@Composable
private fun ActionTouchButton(
    testTag: String,
    letter: String,
    subLabel: String,
    color: Color,
    onPressChange: (Boolean) -> Unit
) {
    var isPressed by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .testTag(testTag)
            .size(64.dp)
            .shadow(if (isPressed) 2.dp else 8.dp, CircleShape)
            .clip(CircleShape)
            .background(if (isPressed) color.copy(alpha = 0.8f) else color)
            .border(2.5.dp, Color.White.copy(alpha = 0.85f), CircleShape)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressChange(true)
                        tryAwaitRelease()
                        isPressed = false
                        onPressChange(false)
                    }
                )
            },
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = letter,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}
