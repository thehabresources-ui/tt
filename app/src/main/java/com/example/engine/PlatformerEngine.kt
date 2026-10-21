package com.example.engine

import com.example.audio.RetroAudio
import com.example.model.BlockType
import com.example.model.CoinItem
import com.example.model.EnemyState
import com.example.model.GameBlock
import com.example.model.GamePlayStatus
import com.example.model.LetterItem
import com.example.model.LevelData
import com.example.model.PlayerState
import com.example.model.QuizQuestion
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

class PlatformerEngine(
    private var levelData: LevelData,
    private val audio: RetroAudio
) {
    var player = PlayerState()
        private set

    var status: GamePlayStatus = GamePlayStatus.PLAYING
        private set

    var activeQuestion: QuizQuestion? = null
        private set

    var activeBlockId: Int? = null
        private set

    val blocks = mutableListOf<GameBlock>()
    val enemies = mutableListOf<EnemyState>()
    val coins = mutableListOf<CoinItem>()
    val letters = mutableListOf<LetterItem>()
    val flagpole = levelData.flagpole.copy()

    var cameraX: Float = 0f
        private set

    // Control inputs
    var inputLeft: Boolean = false
    var inputRight: Boolean = false
    var inputJump: Boolean = false
    var inputDash: Boolean = false

    private val gravity = 0.65f
    private val jumpImpulse = -13.5f
    private val maxRunSpeed = 4.8f
    private val maxDashSpeed = 6.2f
    private val acceleration = 0.5f
    private val friction = 0.82f

    // Stats for level evaluation
    var totalQuestionsAnswered: Int = 0
        private set
    var totalQuestionsCorrect: Int = 0
        private set

    init {
        resetLevel(levelData)
    }

    fun resetLevel(newLevel: LevelData = levelData) {
        levelData = newLevel
        blocks.clear()
        blocks.addAll(newLevel.blocks.map { it.copy() })

        enemies.clear()
        enemies.addAll(newLevel.enemies.map { it.copy() })

        coins.clear()
        coins.addAll(newLevel.coins.map { it.copy() })

        letters.clear()
        letters.addAll(newLevel.letters.map { it.copy() })

        flagpole.flagProgress = 0f
        flagpole.isTriggered = false
        flagpole.isCompleted = false

        player = PlayerState(
            x = 80f,
            y = 340f,
            vx = 0f,
            vy = 0f,
            lives = 3,
            score = 0,
            coins = 0,
            timeLeft = newLevel.timeLimit,
            collectedLetters = emptyList()
        )

        status = GamePlayStatus.PLAYING
        activeQuestion = null
        activeBlockId = null
        cameraX = 0f
        totalQuestionsAnswered = 0
        totalQuestionsCorrect = 0
    }

    fun update(deltaTimeSeconds: Float) {
        if (status != GamePlayStatus.PLAYING) {
            // Update flagpole cutscene if triggered
            if (flagpole.isTriggered && !flagpole.isCompleted) {
                updateFlagpoleCutscene(deltaTimeSeconds)
            }
            return
        }

        // Timer countdown
        player.timeLeft = max(0, player.timeLeft - (deltaTimeSeconds * 0.5f).toInt())
        if (player.timeLeft <= 0) {
            status = GamePlayStatus.GAME_OVER
            audio.playHurt()
            return
        }

        // Invulnerability timer
        if (player.invulnerableTimer > 0f) {
            player.invulnerableTimer = max(0f, player.invulnerableTimer - deltaTimeSeconds)
        }

        // Horizontal Movement
        val targetMaxSpeed = if (inputDash) maxDashSpeed else maxRunSpeed
        if (inputLeft) {
            player.vx -= acceleration
            player.facingRight = false
            player.isRunning = true
        } else if (inputRight) {
            player.vx += acceleration
            player.facingRight = true
            player.isRunning = true
        } else {
            player.vx *= friction
            if (abs(player.vx) < 0.1f) player.vx = 0f
            player.isRunning = false
        }
        player.vx = player.vx.coerceIn(-targetMaxSpeed, targetMaxSpeed)

        // Jump Handling
        if (inputJump) {
            if (player.isGrounded && !player.isJumping) {
                player.vy = jumpImpulse
                player.isGrounded = false
                player.isJumping = true
                audio.playJump()
            }
        } else {
            // Early jump release variable height (Super Mario feel)
            if (player.vy < -4.0f) {
                player.vy *= 0.55f
            }
        }

        // Apply Gravity
        player.vy += gravity
        if (player.vy > 12f) player.vy = 12f

        // Horizontal displacement and collisions
        player.x += player.vx
        checkHorizontalBlockCollisions()

        // Vertical displacement and collisions
        player.y += player.vy
        player.isGrounded = false
        checkVerticalBlockCollisions()

        // Block bump animation decay
        for (block in blocks) {
            if (block.bumpY < 0f) {
                block.bumpY = min(0f, block.bumpY + 2.5f)
            }
        }

        // Update Enemies
        updateEnemies(deltaTimeSeconds)

        // Collect Coins
        checkCoinPickups()

        // Collect Letters
        checkLetterPickups()

        // Check Flagpole
        checkFlagpoleTrigger()

        // Pit Fall Check
        if (player.y > 480f) {
            player.lives -= 1
            audio.playHurt()
            if (player.lives <= 0) {
                status = GamePlayStatus.GAME_OVER
            } else {
                // Respawn at last ground
                player.x = max(80f, player.x - 200f)
                player.y = 200f
                player.vy = 0f
                player.vx = 0f
            }
        }

        // Camera Tracking
        val targetCamX = player.x - 200f
        cameraX = targetCamX.coerceIn(0f, max(0f, levelData.levelWidth - 600f))
    }

    private fun checkHorizontalBlockCollisions() {
        val playerLeft = player.x
        val playerRight = player.x + player.width
        val playerTop = player.y
        val playerBottom = player.y + player.height

        for (block in blocks) {
            val bLeft = block.x
            val bRight = block.x + block.width
            val bTop = block.y
            val bBottom = block.y + block.height

            // Check AABB overlap
            if (playerRight > bLeft && playerLeft < bRight && playerBottom > bTop + 4f && playerTop < bBottom - 4f) {
                if (player.vx > 0) {
                    player.x = bLeft - player.width
                    player.vx = 0f
                } else if (player.vx < 0) {
                    player.x = bRight
                    player.vx = 0f
                }
            }
        }
    }

    private fun checkVerticalBlockCollisions() {
        val playerLeft = player.x + 3f
        val playerRight = player.x + player.width - 3f

        for (block in blocks) {
            val bLeft = block.x
            val bRight = block.x + block.width
            val bTop = block.y
            val bBottom = block.y + block.height

            if (playerRight > bLeft && playerLeft < bRight) {
                // Landing on top of block
                if (player.vy >= 0 && (player.y + player.height) >= bTop && (player.y + player.height) <= bTop + 14f) {
                    player.y = bTop - player.height
                    player.vy = 0f
                    player.isGrounded = true
                    player.isJumping = false
                }
                // Hitting block from below (Mario Head Bump!)
                else if (player.vy < 0 && player.y <= bBottom && player.y >= bBottom - 14f) {
                    player.y = bBottom
                    player.vy = 1.0f // Bounce down
                    block.bumpY = -12f

                    if (!block.hit) {
                        block.hit = true
                        handleBlockHit(block)
                    } else {
                        audio.playBlockBump()
                    }
                }
            }
        }
    }

    private fun handleBlockHit(block: GameBlock) {
        if (block.type == BlockType.MYSTERY) {
            audio.playBlockBump()
            block.type = BlockType.EMPTY_BOX
            val qId = block.questionId
            val q = levelData.questions.find { it.id == qId }
                ?: levelData.questions.firstOrNull()

            if (q != null) {
                activeQuestion = q
                activeBlockId = block.id
                status = GamePlayStatus.QUIZ_POPUP
                audio.playCoin()
            }
        } else if (block.hasCoin) {
            audio.playCoin()
            player.coins += 1
            player.score += 100
        } else {
            audio.playBlockBump()
        }
    }

    fun answerQuiz(selectedIndex: Int) {
        val q = activeQuestion ?: return
        totalQuestionsAnswered += 1

        if (selectedIndex == q.correctIndex) {
            totalQuestionsCorrect += 1
            player.score += 300
            player.coins += 2
            audio.playQuizSuccess()
        } else {
            audio.playQuizWrong()
        }
    }

    fun resumeAfterQuiz() {
        activeQuestion = null
        activeBlockId = null
        status = GamePlayStatus.PLAYING
    }

    private fun updateEnemies(deltaTimeSeconds: Float) {
        val playerLeft = player.x
        val playerRight = player.x + player.width
        val playerTop = player.y
        val playerBottom = player.y + player.height

        for (enemy in enemies) {
            if (!enemy.isAlive) {
                if (enemy.squashTimer > 0f) {
                    enemy.squashTimer -= deltaTimeSeconds
                }
                continue
            }

            // Patrol movement
            enemy.x += enemy.vx
            if (enemy.x <= enemy.startX) {
                enemy.x = enemy.startX
                enemy.vx = abs(enemy.vx)
            } else if (enemy.x >= enemy.endX) {
                enemy.x = enemy.endX
                enemy.vx = -abs(enemy.vx)
            }

            // Collision with Player
            val eLeft = enemy.x
            val eRight = enemy.x + enemy.width
            val eTop = enemy.y
            val eBottom = enemy.y + enemy.height

            if (playerRight > eLeft && playerLeft < eRight && playerBottom > eTop && playerTop < eBottom) {
                // Mario Stomp on top of enemy!
                if (player.vy > 0 && playerBottom <= eTop + 16f) {
                    enemy.isAlive = false
                    enemy.squashTimer = 0.5f
                    player.vy = -8.5f // small bounce
                    player.score += 200
                    audio.playEnemyStomp()
                } else if (player.invulnerableTimer <= 0f) {
                    // Player gets hurt
                    player.lives -= 1
                    player.invulnerableTimer = 2.0f
                    player.vy = -5f
                    player.vx = if (player.x < enemy.x) -3f else 3f
                    audio.playHurt()

                    if (player.lives <= 0) {
                        status = GamePlayStatus.GAME_OVER
                    }
                }
            }
        }
    }

    private fun checkCoinPickups() {
        val pCx = player.x + player.width / 2
        val pCy = player.y + player.height / 2

        for (coin in coins) {
            if (!coin.collected) {
                val dx = abs(pCx - (coin.x + 12f))
                val dy = abs(pCy - (coin.y + 12f))
                if (dx < 26f && dy < 26f) {
                    coin.collected = true
                    player.coins += 1
                    player.score += 50
                    audio.playCoin()
                }
            }
        }
    }

    private fun checkLetterPickups() {
        val pCx = player.x + player.width / 2
        val pCy = player.y + player.height / 2

        for (letter in letters) {
            if (!letter.collected) {
                val dx = abs(pCx - (letter.x + 16f))
                val dy = abs(pCy - (letter.y + 16f))
                if (dx < 28f && dy < 28f) {
                    letter.collected = true
                    val currentList = player.collectedLetters.toMutableList()
                    currentList.add(letter.char)
                    player.collectedLetters = currentList
                    player.score += 150
                    audio.playCoin()

                    // Check if full target word collected
                    val wordSpelled = levelData.targetWord.all { char ->
                        player.collectedLetters.contains(char)
                    }
                    if (wordSpelled && player.collectedLetters.size == levelData.targetWord.length) {
                        player.score += 500
                        player.coins += 5
                    }
                }
            }
        }
    }

    private fun checkFlagpoleTrigger() {
        if (!flagpole.isTriggered && player.x >= flagpole.x - 10f) {
            flagpole.isTriggered = true
            player.vx = 0f
            player.vy = 0f
            player.isCelebrating = true
            status = GamePlayStatus.LEVEL_CLEARED
            audio.playVictory()
        }
    }

    private fun updateFlagpoleCutscene(deltaTimeSeconds: Float) {
        if (flagpole.flagProgress < 1.0f) {
            flagpole.flagProgress = min(1.0f, flagpole.flagProgress + deltaTimeSeconds * 0.8f)
            player.y = min(360f, player.y + deltaTimeSeconds * 120f)
        } else {
            flagpole.isCompleted = true
        }
    }

    fun calculateStars(): Int {
        var stars = 1
        if (player.lives >= 2 && totalQuestionsCorrect >= 2) stars = 2
        if (player.lives == 3 && totalQuestionsCorrect >= 3 && letters.all { it.collected }) stars = 3
        return stars
    }
}
