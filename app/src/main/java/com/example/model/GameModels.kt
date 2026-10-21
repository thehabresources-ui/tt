package com.example.model

enum class WorldTheme {
    JUNGLE,
    CAVERN,
    CASTLE
}

enum class BlockType {
    GROUND,
    BRICK,
    MYSTERY,
    EMPTY_BOX,
    PIPE_TOP,
    PIPE_BODY,
    WOOD_PLATFORM
}

enum class EnemyType {
    GOOMBA, // Kumbang Ejaan
    KOOPA   // Kura Tatabahasa
}

data class GameBlock(
    val id: Int,
    var x: Float,
    var y: Float,
    val width: Float = 40f,
    val height: Float = 40f,
    var type: BlockType,
    var hit: Boolean = false,
    var bumpY: Float = 0f,
    val questionId: Int? = null,
    val hasCoin: Boolean = false
)

data class EnemyState(
    val id: Int,
    var x: Float,
    var y: Float,
    val startX: Float,
    val endX: Float,
    val width: Float = 36f,
    val height: Float = 36f,
    val type: EnemyType = EnemyType.GOOMBA,
    var vx: Float = -1.2f,
    var isAlive: Boolean = true,
    var squashTimer: Float = 0f
)

data class CoinItem(
    val id: Int,
    val x: Float,
    val y: Float,
    var collected: Boolean = false
)

data class LetterItem(
    val id: Int,
    val x: Float,
    val y: Float,
    val char: Char,
    val indexInWord: Int,
    var collected: Boolean = false
)

data class FlagpoleState(
    val x: Float,
    val y: Float = 140f,
    val height: Float = 260f,
    var flagProgress: Float = 0f,
    var isTriggered: Boolean = false,
    var isCompleted: Boolean = false
)

data class QuizQuestion(
    val id: Int,
    val category: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String,
    val hint: String
)

data class LevelData(
    val id: String,
    val world: Int,
    val stage: Int,
    val title: String,
    val subtitle: String,
    val topic: String,
    val theme: WorldTheme,
    val targetWord: String,
    val levelWidth: Float = 2800f,
    val blocks: List<GameBlock>,
    val enemies: List<EnemyState>,
    val coins: List<CoinItem>,
    val letters: List<LetterItem>,
    val flagpole: FlagpoleState,
    val questions: List<QuizQuestion>,
    val timeLimit: Int = 180
)

data class PlayerState(
    var x: Float = 80f,
    var y: Float = 340f,
    var vx: Float = 0f,
    var vy: Float = 0f,
    val width: Float = 32f,
    val height: Float = 44f,
    var facingRight: Boolean = true,
    var isGrounded: Boolean = false,
    var isJumping: Boolean = false,
    var isRunning: Boolean = false,
    var invulnerableTimer: Float = 0f,
    var lives: Int = 3,
    var score: Int = 0,
    var coins: Int = 0,
    var timeLeft: Int = 180,
    var collectedLetters: List<Char> = emptyList(),
    var isCelebrating: Boolean = false,
    var isDead: Boolean = false
)

enum class GamePlayStatus {
    PLAYING,
    QUIZ_POPUP,
    LEVEL_CLEARED,
    GAME_OVER,
    PAUSED
}

data class LearnedConcept(
    val id: String,
    val category: String,
    val title: String,
    val definition: String,
    val examples: List<String>,
    val tip: String
)

data class LevelResult(
    val levelId: String,
    val stars: Int,
    val score: Int,
    val coinsEarned: Int,
    val questionsAnswered: Int,
    val questionsCorrect: Int,
    val wordCompleted: Boolean
)
