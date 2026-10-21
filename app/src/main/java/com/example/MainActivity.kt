package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.audio.RetroAudio
import com.example.model.LevelResult
import com.example.ui.screens.ArcadeQuizScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.KamusScreen
import com.example.ui.screens.WorldSelectScreen
import com.example.ui.theme.MyApplicationTheme

enum class AppScreen {
    HOME,
    GAME,
    WORLD_SELECT,
    KAMUS,
    ARCADE
}

class MainActivity : ComponentActivity() {
    private lateinit var audio: RetroAudio

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        audio = RetroAudio(this)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                WiraBahasaApp(audio = audio)
            }
        }
    }
}

@Composable
fun WiraBahasaApp(audio: RetroAudio) {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedLevelId by remember { mutableStateOf("1-1") }
    var unlockedLevels by remember { mutableStateOf(setOf("1-1")) }
    val starsMap = remember { mutableStateMapOf("1-1" to 0) }
    var totalCoins by remember { mutableIntStateOf(0) }

    val totalStars = starsMap.values.sum()

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        when (currentScreen) {
            AppScreen.HOME -> {
                HomeScreen(
                    audio = audio,
                    totalStars = totalStars,
                    totalCoins = totalCoins,
                    onStartGame = {
                        selectedLevelId = "1-1"
                        currentScreen = AppScreen.GAME
                    },
                    onOpenWorldSelect = {
                        currentScreen = AppScreen.WORLD_SELECT
                    },
                    onOpenKamus = {
                        currentScreen = AppScreen.KAMUS
                    },
                    onOpenArcade = {
                        currentScreen = AppScreen.ARCADE
                    }
                )
            }

            AppScreen.GAME -> {
                GameScreen(
                    levelId = selectedLevelId,
                    audio = audio,
                    onBackToMap = {
                        currentScreen = AppScreen.WORLD_SELECT
                    },
                    onLevelCompleted = { result: LevelResult ->
                        // Save stars
                        val prevStars = starsMap[result.levelId] ?: 0
                        if (result.stars > prevStars) {
                            starsMap[result.levelId] = result.stars
                        }
                        totalCoins += result.coinsEarned

                        // Unlock next level
                        val nextId = when (result.levelId) {
                            "1-1" -> "1-2"
                            "1-2" -> "2-1"
                            "2-1" -> "2-2"
                            "2-2" -> "3-1"
                            "3-1" -> "3-2"
                            else -> null
                        }
                        if (nextId != null) {
                            unlockedLevels = unlockedLevels + nextId
                        }
                    },
                    onSelectNextLevel = { nextId ->
                        selectedLevelId = nextId
                        unlockedLevels = unlockedLevels + nextId
                    }
                )
            }

            AppScreen.WORLD_SELECT -> {
                WorldSelectScreen(
                    unlockedLevels = unlockedLevels,
                    starsMap = starsMap,
                    onSelectLevel = { levelId ->
                        selectedLevelId = levelId
                        currentScreen = AppScreen.GAME
                    },
                    onBack = {
                        currentScreen = AppScreen.HOME
                    }
                )
            }

            AppScreen.KAMUS -> {
                KamusScreen(
                    onBack = {
                        currentScreen = AppScreen.HOME
                    }
                )
            }

            AppScreen.ARCADE -> {
                ArcadeQuizScreen(
                    audio = audio,
                    onBack = {
                        currentScreen = AppScreen.HOME
                    }
                )
            }
        }
    }
}
