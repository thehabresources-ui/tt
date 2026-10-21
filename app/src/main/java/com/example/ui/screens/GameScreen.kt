package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.audio.RetroAudio
import com.example.data.LevelRepository
import com.example.engine.PlatformerEngine
import com.example.model.GamePlayStatus
import com.example.model.LevelData
import com.example.model.LevelResult
import com.example.ui.components.GamepadControls
import com.example.ui.components.LevelClearModal
import com.example.ui.components.QuizChallengeModal
import com.example.ui.components.RetroGameCanvas
import com.example.ui.theme.CoinGold
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.MarioRed
import com.example.ui.theme.SuccessGreen

@Composable
fun GameScreen(
    levelId: String,
    audio: RetroAudio,
    onBackToMap: () -> Unit,
    onLevelCompleted: (LevelResult) -> Unit,
    onSelectNextLevel: (String) -> Unit
) {
    val levelData = remember(levelId) { LevelRepository.getLevelById(levelId) }
    val engine = remember(levelData) { PlatformerEngine(levelData, audio) }

    var gameTicks by remember { mutableLongStateOf(0L) }
    var isPaused by remember { mutableStateOf(false) }
    var isSoundOn by remember { mutableStateOf(audio.isSoundEnabled()) }

    BackHandler {
        onBackToMap()
    }

    // Main 60 FPS Game Loop
    LaunchedEffect(engine, isPaused) {
        var lastTime = 0L
        while (true) {
            withFrameMillis { frameTime ->
                if (lastTime != 0L && !isPaused) {
                    val dt = (frameTime - lastTime) / 1000f
                    val clampedDt = dt.coerceIn(0.005f, 0.05f)
                    engine.update(clampedDt)
                    gameTicks++
                }
                lastTime = frameTime
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // 1. Retro HUD Bar
            GameHudBar(
                engine = engine,
                levelData = levelData,
                isSoundOn = isSoundOn,
                onToggleSound = {
                    isSoundOn = audio.toggleSound()
                },
                onPauseToggle = {
                    isPaused = !isPaused
                }
            )

            // 2. Platformer Canvas Viewport
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                RetroGameCanvas(
                    engine = engine,
                    gameTimeTicks = gameTicks
                )
            }

            // 3. Gamepad Virtual Controls
            GamepadControls(
                onLeftPress = { engine.inputLeft = it },
                onRightPress = { engine.inputRight = it },
                onJumpPress = { engine.inputJump = it },
                onDashPress = { engine.inputDash = it }
            )
        }

        // Quiz Question Dialog (when hitting ? block)
        if (engine.status == GamePlayStatus.QUIZ_POPUP && engine.activeQuestion != null) {
            QuizChallengeModal(
                question = engine.activeQuestion!!,
                onAnswerSelected = { optionIndex ->
                    engine.answerQuiz(optionIndex)
                },
                onResume = {
                    engine.resumeAfterQuiz()
                }
            )
        }

        // Level Cleared Modal (reached Flagpole)
        if (engine.status == GamePlayStatus.LEVEL_CLEARED) {
            val result = LevelResult(
                levelId = levelData.id,
                stars = engine.calculateStars(),
                score = engine.player.score,
                coinsEarned = engine.player.coins,
                questionsAnswered = engine.totalQuestionsAnswered,
                questionsCorrect = engine.totalQuestionsCorrect,
                wordCompleted = engine.letters.all { it.collected }
            )
            LevelClearModal(
                levelData = levelData,
                result = result,
                onNextLevel = {
                    onLevelCompleted(result)
                    val nextId = getNextLevelId(levelData.id)
                    onSelectNextLevel(nextId)
                },
                onRetry = {
                    engine.resetLevel(levelData)
                },
                onWorldMap = {
                    onLevelCompleted(result)
                    onBackToMap()
                }
            )
        }

        // Game Over Dialog (lives ran out or time expired)
        if (engine.status == GamePlayStatus.GAME_OVER) {
            GameOverModal(
                score = engine.player.score,
                onRetry = {
                    engine.resetLevel(levelData)
                },
                onExit = onBackToMap
            )
        }

        // Pause Overlay
        if (isPaused) {
            PauseModal(
                onResume = { isPaused = false },
                onRestart = {
                    isPaused = false
                    engine.resetLevel(levelData)
                },
                onExit = onBackToMap
            )
        }
    }
}

@Composable
private fun GameHudBar(
    engine: PlatformerEngine,
    levelData: LevelData,
    isSoundOn: Boolean,
    onToggleSound: () -> Unit,
    onPauseToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp),
        color = Color(0xFF0F172A),
        tonalElevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lives & Coins
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Lives
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Nyawa",
                        tint = ErrorRed,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "x${engine.player.lives}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                // Coins
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Syiling",
                        tint = CoinGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "${engine.player.coins}",
                        color = CoinGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Word Quest Tiles (e.g. P A N D A I)
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                levelData.targetWord.forEach { char ->
                    val isFound = engine.player.collectedLetters.contains(char)
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isFound) SuccessGreen else Color(0xFF334155))
                            .border(1.dp, if (isFound) Color.White else Color(0xFF475569), RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isFound) char.toString() else "_",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Level ID & Actions
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "${engine.player.score}",
                    color = Color.White,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp
                )

                IconButton(
                    onClick = onToggleSound,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isSoundOn) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "Bunyi",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                IconButton(
                    onClick = onPauseToggle,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Pause,
                        contentDescription = "Jeda",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun GameOverModal(score: Int, onRetry: () -> Unit, onExit: () -> Unit) {
    Dialog(onDismissRequest = {}) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.border(2.dp, ErrorRed, RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PERMAINAN TAMAT",
                    color = ErrorRed,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Jangan putus asa! Cuba lagi untuk kuasai tatabahasa.",
                    color = Color(0xFF94A3B8),
                    fontSize = 13.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Skor Akhir: $score",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = MarioRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("CUBA SEMULA")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onExit,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("KEMBALI KE PETA")
                }
            }
        }
    }
}

@Composable
private fun PauseModal(onResume: () -> Unit, onRestart: () -> Unit, onExit: () -> Unit) {
    Dialog(onDismissRequest = onResume) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFF0F172A),
            modifier = Modifier.border(2.dp, Color(0xFF38BDF8), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "PERMAINAN DIJEDA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = onResume,
                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("SAMBUNG BERMAIN")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRestart,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("MULAKAN SEMULA TAHAP")
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onExit,
                    colors = ButtonDefaults.buttonColors(containerColor = MarioRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("KELUAR KE PETA")
                }
            }
        }
    }
}

private fun getNextLevelId(currentId: String): String {
    return when (currentId) {
        "1-1" -> "1-2"
        "1-2" -> "2-1"
        "2-1" -> "2-2"
        "2-2" -> "3-1"
        "3-1" -> "3-2"
        else -> "1-1"
    }
}
