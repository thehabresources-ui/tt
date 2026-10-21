package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import com.example.engine.PlatformerEngine
import com.example.model.BlockType
import com.example.model.EnemyType
import com.example.model.WorldTheme
import kotlin.math.abs
import kotlin.math.sin

@Composable
fun RetroGameCanvas(
    engine: PlatformerEngine,
    gameTimeTicks: Long,
    modifier: Modifier = Modifier
) {
    val theme = engine.let {
        // Safe access to theme
        WorldTheme.JUNGLE
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // World coordinates scale: Base game height is 480f
        val scale = height / 480f
        val camX = engine.cameraX

        // 1. Draw Background Sky / Cavern / Castle
        drawGameBackground(theme, camX, width, height)

        // 2. Parallax scenery (clouds, hills, bushes)
        drawParallaxScenery(camX, scale, width, height)

        // 3. Castle at Flagpole area
        drawEndCastle(engine.flagpole.x + 120f, camX, scale)

        // 4. Draw Flagpole
        drawFlagpole(engine.flagpole.x, engine.flagpole.y, engine.flagpole.height, engine.flagpole.flagProgress, camX, scale)

        // 5. Draw Blocks
        for (block in engine.blocks) {
            val screenX = (block.x - camX) * scale
            val screenY = (block.y + block.bumpY) * scale
            val blockSize = block.width * scale

            // Frustum culling: only draw visible blocks
            if (screenX + blockSize >= -50f && screenX <= width + 50f) {
                drawBlock(block.type, screenX, screenY, blockSize, gameTimeTicks)
            }
        }

        // 6. Draw Collectible Coins
        for (coin in engine.coins) {
            if (!coin.collected) {
                val screenX = (coin.x - camX) * scale
                val screenY = coin.y * scale
                if (screenX >= -40f && screenX <= width + 40f) {
                    drawCoin(screenX, screenY, scale, gameTimeTicks)
                }
            }
        }

        // 7. Draw Letter Pickups (Huruf Bahasa)
        for (letter in engine.letters) {
            if (!letter.collected) {
                val screenX = (letter.x - camX) * scale
                val screenY = letter.y * scale
                if (screenX >= -40f && screenX <= width + 40f) {
                    drawLetterOrb(screenX, screenY, letter.char, scale, gameTimeTicks)
                }
            }
        }

        // 8. Draw Enemies
        for (enemy in engine.enemies) {
            val screenX = (enemy.x - camX) * scale
            val screenY = enemy.y * scale
            if (screenX >= -60f && screenX <= width + 60f) {
                if (enemy.isAlive || enemy.squashTimer > 0f) {
                    drawEnemy(enemy.type, screenX, screenY, scale, enemy.isAlive, enemy.vx > 0)
                }
            }
        }

        // 9. Draw Player (Wira Mario)
        val player = engine.player
        val pScreenX = (player.x - camX) * scale
        val pScreenY = player.y * scale

        // Invulnerability flicker
        val isVisible = (player.invulnerableTimer <= 0f) || ((gameTimeTicks / 4) % 2 == 0L)
        if (isVisible) {
            drawHero(
                x = pScreenX,
                y = pScreenY,
                scale = scale,
                facingRight = player.facingRight,
                isRunning = player.isRunning && player.isGrounded,
                isJumping = !player.isGrounded,
                isCelebrating = player.isCelebrating,
                gameTimeTicks = gameTimeTicks
            )
        }
    }
}

private fun DrawScope.drawGameBackground(theme: WorldTheme, camX: Float, width: Float, height: Float) {
    val topColor = when (theme) {
        WorldTheme.JUNGLE -> Color(0xFF5C94FC)
        WorldTheme.CAVERN -> Color(0xFF0F172A)
        WorldTheme.CASTLE -> Color(0xFF3B0764)
    }
    val bottomColor = when (theme) {
        WorldTheme.JUNGLE -> Color(0xFF93C5FD)
        WorldTheme.CAVERN -> Color(0xFF1E293B)
        WorldTheme.CASTLE -> Color(0xFF701A75)
    }

    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(topColor, bottomColor),
            startY = 0f,
            endY = height
        ),
        size = Size(width, height)
    )
}

private fun DrawScope.drawParallaxScenery(camX: Float, scale: Float, width: Float, height: Float) {
    // Distant Clouds
    val cloudOffset1 = ((camX * 0.2f) % (width + 300f))
    drawCloud(120f * scale - cloudOffset1, 60f * scale, scale * 1.2f)
    drawCloud(450f * scale - cloudOffset1, 100f * scale, scale * 0.9f)
    drawCloud(800f * scale - cloudOffset1, 50f * scale, scale * 1.1f)

    // Distant Green Hills
    val hillOffset = (camX * 0.4f) % 600f
    for (i in -1..4) {
        val hillX = (i * 600f - hillOffset) * scale
        val hillY = 320f * scale
        drawArc(
            color = Color(0xFF16A34A).copy(alpha = 0.65f),
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = true,
            topLeft = Offset(hillX, hillY),
            size = Size(360f * scale, 220f * scale)
        )
    }

    // Bushes near ground
    val bushOffset = (camX * 0.8f) % 500f
    for (i in -1..4) {
        val bushX = (i * 500f - bushOffset) * scale
        val bushY = 370f * scale
        drawRoundRect(
            color = Color(0xFF15803D),
            topLeft = Offset(bushX, bushY),
            size = Size(90f * scale, 35f * scale),
            cornerRadius = CornerRadius(18f * scale, 18f * scale)
        )
    }
}

private fun DrawScope.drawCloud(x: Float, y: Float, scale: Float) {
    val cloudColor = Color.White.copy(alpha = 0.9f)
    drawCircle(cloudColor, radius = 22f * scale, center = Offset(x, y))
    drawCircle(cloudColor, radius = 28f * scale, center = Offset(x + 25f * scale, y - 8f * scale))
    drawCircle(cloudColor, radius = 24f * scale, center = Offset(x + 50f * scale, y))
    drawCircle(cloudColor, radius = 18f * scale, center = Offset(x + 70f * scale, y + 4f * scale))
    drawRoundRect(
        color = cloudColor,
        topLeft = Offset(x - 10f * scale, y),
        size = Size(90f * scale, 22f * scale),
        cornerRadius = CornerRadius(10f * scale, 10f * scale)
    )
}

private fun DrawScope.drawBlock(type: BlockType, x: Float, y: Float, size: Float, ticks: Long) {
    when (type) {
        BlockType.GROUND -> {
            // Ground earth with green grass top
            drawRect(Color(0xFFC84C0C), Offset(x, y), Size(size, size))
            // Green grass border
            drawRect(Color(0xFF22C55E), Offset(x, y), Size(size, size * 0.22f))
            // Retro brick speckles
            drawRect(Color(0xFF9A3412), Offset(x + size * 0.2f, y + size * 0.4f), Size(size * 0.2f, size * 0.15f))
            drawRect(Color(0xFF9A3412), Offset(x + size * 0.6f, y + size * 0.7f), Size(size * 0.25f, size * 0.15f))
        }
        BlockType.BRICK -> {
            // Brown brick
            drawRect(Color(0xFFB84408), Offset(x, y), Size(size, size))
            // Mortar grid lines
            val stroke = Stroke(width = size * 0.08f)
            drawRect(Color(0xFF782800), Offset(x, y), Size(size, size), style = stroke)
            drawLine(Color(0xFF782800), Offset(x, y + size * 0.5f), Offset(x + size, y + size * 0.5f), strokeWidth = size * 0.08f)
            drawLine(Color(0xFF782800), Offset(x + size * 0.5f, y), Offset(x + size * 0.5f, y + size * 0.5f), strokeWidth = size * 0.08f)
            drawLine(Color(0xFF782800), Offset(x + size * 0.25f, y + size * 0.5f), Offset(x + size * 0.25f, y + size), strokeWidth = size * 0.08f)
            drawLine(Color(0xFF782800), Offset(x + size * 0.75f, y + size * 0.5f), Offset(x + size * 0.75f, y + size), strokeWidth = size * 0.08f)
        }
        BlockType.MYSTERY -> {
            // Glowing pulsing Question Box
            val pulse = (sin(ticks * 0.15) * 0.15 + 0.85).toFloat()
            val boxColor = Color(0xFFFFB800)
            drawRect(boxColor, Offset(x, y), Size(size, size))

            // 3D Bevel highlight
            drawLine(Color(0xFFFFEE55), Offset(x, y), Offset(x + size, y), strokeWidth = size * 0.1f)
            drawLine(Color(0xFFFFEE55), Offset(x, y), Offset(x, y + size), strokeWidth = size * 0.1f)
            drawLine(Color(0xFFB45309), Offset(x + size, y), Offset(x + size, y + size), strokeWidth = size * 0.1f)
            drawLine(Color(0xFFB45309), Offset(x, y + size), Offset(x + size, y + size), strokeWidth = size * 0.1f)

            // 4 Corner bolts
            val boltSize = size * 0.1f
            drawRect(Color(0xFF78350F), Offset(x + size * 0.1f, y + size * 0.1f), Size(boltSize, boltSize))
            drawRect(Color(0xFF78350F), Offset(x + size * 0.8f, y + size * 0.1f), Size(boltSize, boltSize))
            drawRect(Color(0xFF78350F), Offset(x + size * 0.1f, y + size * 0.8f), Size(boltSize, boltSize))
            drawRect(Color(0xFF78350F), Offset(x + size * 0.8f, y + size * 0.8f), Size(boltSize, boltSize))

            // "?" Question Mark in center
            drawIntoCanvasText("?", x + size * 0.5f, y + size * 0.75f, size * 0.65f * pulse, android.graphics.Color.WHITE)
        }
        BlockType.EMPTY_BOX -> {
            drawRect(Color(0xFF78350F), Offset(x, y), Size(size, size))
            drawRect(Color(0xFF451A03), Offset(x + size * 0.08f, y + size * 0.08f), Size(size * 0.84f, size * 0.84f))
            val boltSize = size * 0.1f
            drawRect(Color(0xFF92400E), Offset(x + size * 0.15f, y + size * 0.15f), Size(boltSize, boltSize))
            drawRect(Color(0xFF92400E), Offset(x + size * 0.75f, y + size * 0.15f), Size(boltSize, boltSize))
            drawRect(Color(0xFF92400E), Offset(x + size * 0.15f, y + size * 0.75f), Size(boltSize, boltSize))
            drawRect(Color(0xFF92400E), Offset(x + size * 0.75f, y + size * 0.75f), Size(boltSize, boltSize))
        }
        BlockType.PIPE_TOP -> {
            // Green Pipe Lip
            drawRoundRect(
                color = Color(0xFF16A34A),
                topLeft = Offset(x - size * 0.1f, y),
                size = Size(size * 1.2f, size),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Highlight strip
            drawRect(Color(0xFF4ADE80), Offset(x + size * 0.15f, y), Size(size * 0.2f, size))
            drawRect(Color(0xFF14532D), Offset(x + size * 0.8f, y), Size(size * 0.25f, size))
        }
        BlockType.PIPE_BODY -> {
            drawRect(Color(0xFF16A34A), Offset(x, y), Size(size, size))
            drawRect(Color(0xFF4ADE80), Offset(x + size * 0.2f, y), Size(size * 0.2f, size))
            drawRect(Color(0xFF14532D), Offset(x + size * 0.75f, y), Size(size * 0.25f, size))
        }
        BlockType.WOOD_PLATFORM -> {
            drawRoundRect(
                color = Color(0xFFD97706),
                topLeft = Offset(x, y + size * 0.2f),
                size = Size(size, size * 0.6f),
                cornerRadius = CornerRadius(6f, 6f)
            )
            drawRect(Color(0xFF78350F), Offset(x + size * 0.45f, y + size * 0.2f), Size(size * 0.1f, size * 0.6f))
        }
    }
}

private fun DrawScope.drawCoin(x: Float, y: Float, scale: Float, ticks: Long) {
    val coinW = 22f * scale * (abs(sin(ticks * 0.12f))).coerceAtLeast(0.25f)
    val coinH = 26f * scale
    val center = Offset(x + 12f * scale, y + 12f * scale)

    drawOval(
        color = Color(0xFFFFD700),
        topLeft = Offset(center.x - coinW / 2, center.y - coinH / 2),
        size = Size(coinW, coinH)
    )
    drawOval(
        color = Color(0xFFB45309),
        topLeft = Offset(center.x - (coinW * 0.65f) / 2, center.y - (coinH * 0.7f) / 2),
        size = Size(coinW * 0.65f, coinH * 0.7f),
        style = Stroke(width = 2f * scale)
    )
}

private fun DrawScope.drawLetterOrb(x: Float, y: Float, char: Char, scale: Float, ticks: Long) {
    val floatY = sin(ticks * 0.1 + char.code) * 6f * scale
    val radius = 18f * scale
    val center = Offset(x + 16f * scale, y + 16f * scale + floatY.toFloat())

    // Glowing aura
    drawCircle(
        color = Color(0xFF38BDF8).copy(alpha = 0.4f),
        radius = radius * 1.35f,
        center = center
    )
    // Orb body
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFF67E8F9), Color(0xFF0284C7)),
            center = center,
            radius = radius
        ),
        radius = radius,
        center = center
    )
    // White ring border
    drawCircle(
        color = Color.White,
        radius = radius,
        center = center,
        style = Stroke(width = 2.5f * scale)
    )

    // Letter text
    drawIntoCanvasText(char.toString(), center.x, center.y + 7f * scale, 20f * scale, android.graphics.Color.WHITE)
}

private fun DrawScope.drawEnemy(type: EnemyType, x: Float, y: Float, scale: Float, isAlive: Boolean, facingRight: Boolean) {
    if (!isAlive) {
        // Squashed Goomba/Koopa pancake
        drawOval(
            color = Color(0xFF854D0E),
            topLeft = Offset(x, y + 24f * scale),
            size = Size(36f * scale, 12f * scale)
        )
        return
    }

    when (type) {
        EnemyType.GOOMBA -> {
            // Mushroom head
            val headPath = Path().apply {
                moveTo(x + 18f * scale, y)
                cubicTo(
                    x + 36f * scale, y,
                    x + 36f * scale, y + 26f * scale,
                    x + 28f * scale, y + 26f * scale
                )
                lineTo(x + 8f * scale, y + 26f * scale)
                cubicTo(
                    x, y + 26f * scale,
                    x, y,
                    x + 18f * scale, y
                )
                close()
            }
            drawPath(headPath, Color(0xFF9A3412))

            // Body / Stem
            drawRoundRect(
                color = Color(0xFFFED7AA),
                topLeft = Offset(x + 9f * scale, y + 22f * scale),
                size = Size(18f * scale, 10f * scale),
                cornerRadius = CornerRadius(4f * scale, 4f * scale)
            )

            // Feet
            drawOval(Color(0xFF1E293B), Offset(x + 2f * scale, y + 28f * scale), Size(14f * scale, 8f * scale))
            drawOval(Color(0xFF1E293B), Offset(x + 20f * scale, y + 28f * scale), Size(14f * scale, 8f * scale))

            // Eyes
            drawOval(Color.White, Offset(x + 10f * scale, y + 10f * scale), Size(5f * scale, 8f * scale))
            drawOval(Color.White, Offset(x + 21f * scale, y + 10f * scale), Size(5f * scale, 8f * scale))
            drawCircle(Color.Black, radius = 2f * scale, center = Offset(x + 12f * scale, y + 14f * scale))
            drawCircle(Color.Black, radius = 2f * scale, center = Offset(x + 23f * scale, y + 14f * scale))

            // Angry eyebrows
            drawLine(Color.Black, Offset(x + 8f * scale, y + 8f * scale), Offset(x + 15f * scale, y + 11f * scale), strokeWidth = 2f * scale)
            drawLine(Color.Black, Offset(x + 28f * scale, y + 8f * scale), Offset(x + 21f * scale, y + 11f * scale), strokeWidth = 2f * scale)
        }
        EnemyType.KOOPA -> {
            // Green Shell
            drawArc(
                color = Color(0xFF16A34A),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(x + 4f * scale, y + 8f * scale),
                size = Size(28f * scale, 24f * scale)
            )
            drawArc(
                color = Color(0xFFFEF08A),
                startAngle = 0f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(x + 4f * scale, y + 20f * scale),
                size = Size(28f * scale, 10f * scale)
            )
            // Head
            val headX = if (facingRight) x + 26f * scale else x + 2f * scale
            drawCircle(Color(0xFFFACC15), radius = 7f * scale, center = Offset(headX, y + 12f * scale))
            drawCircle(Color.Black, radius = 1.8f * scale, center = Offset(headX + (if (facingRight) 2f else -2f) * scale, y + 11f * scale))

            // Feet
            drawOval(Color(0xFFEAB308), Offset(x + 6f * scale, y + 28f * scale), Size(10f * scale, 6f * scale))
            drawOval(Color(0xFFEAB308), Offset(x + 20f * scale, y + 28f * scale), Size(10f * scale, 6f * scale))
        }
    }
}

private fun DrawScope.drawHero(
    x: Float,
    y: Float,
    scale: Float,
    facingRight: Boolean,
    isRunning: Boolean,
    isJumping: Boolean,
    isCelebrating: Boolean,
    gameTimeTicks: Long
) {
    val dir = if (facingRight) 1f else -1f
    val centerX = x + 16f * scale

    // 1. Red Cap
    val capY = y + (if (isCelebrating) sin(gameTimeTicks * 0.2).toFloat() * 4f * scale else 0f)
    drawRoundRect(
        color = Color(0xFFE52521),
        topLeft = Offset(centerX - 13f * scale, capY),
        size = Size(26f * scale, 12f * scale),
        cornerRadius = CornerRadius(6f * scale, 6f * scale)
    )
    // Cap Visor
    val visorX = if (facingRight) centerX - 2f * scale else centerX - 18f * scale
    drawRoundRect(
        color = Color(0xFFB91C1C),
        topLeft = Offset(visorX, capY + 8f * scale),
        size = Size(20f * scale, 5f * scale),
        cornerRadius = CornerRadius(2f * scale, 2f * scale)
    )
    // Cap Badge "B" (Bahasa)
    drawIntoCanvasText("B", centerX - 1f * scale, capY + 8f * scale, 8f * scale, android.graphics.Color.WHITE)

    // 2. Head & Face
    drawCircle(
        color = Color(0xFFFFD2A6),
        radius = 9f * scale,
        center = Offset(centerX, capY + 16f * scale)
    )
    // Eye
    val eyeX = if (facingRight) centerX + 4f * scale else centerX - 4f * scale
    drawCircle(Color.Black, radius = 1.8f * scale, center = Offset(eyeX, capY + 15f * scale))
    // Big Mario Mustache
    val stacheX = if (facingRight) centerX + 1f * scale else centerX - 9f * scale
    drawRoundRect(
        color = Color(0xFF451A03),
        topLeft = Offset(stacheX, capY + 18f * scale),
        size = Size(9f * scale, 4f * scale),
        cornerRadius = CornerRadius(2f * scale, 2f * scale)
    )
    // Big Round Nose
    drawCircle(
        color = Color(0xFFFFBF86),
        radius = 3.5f * scale,
        center = Offset(if (facingRight) centerX + 7f * scale else centerX - 7f * scale, capY + 17f * scale)
    )

    // 3. Body: Red Shirt & Blue Overalls
    val bodyY = capY + 23f * scale
    // Red Shirt
    drawRect(
        color = Color(0xFFE52521),
        topLeft = Offset(centerX - 10f * scale, bodyY),
        size = Size(20f * scale, 12f * scale)
    )
    // Blue Overalls
    drawRoundRect(
        color = Color(0xFF1D4ED8),
        topLeft = Offset(centerX - 9f * scale, bodyY + 3f * scale),
        size = Size(18f * scale, 14f * scale),
        cornerRadius = CornerRadius(3f * scale, 3f * scale)
    )
    // Overall Straps
    drawRect(Color(0xFF1E40AF), Offset(centerX - 8f * scale, bodyY), Size(4f * scale, 8f * scale))
    drawRect(Color(0xFF1E40AF), Offset(centerX + 4f * scale, bodyY), Size(4f * scale, 8f * scale))
    // Yellow Buttons
    drawCircle(Color(0xFFFBBF24), radius = 1.5f * scale, center = Offset(centerX - 6f * scale, bodyY + 7f * scale))
    drawCircle(Color(0xFFFBBF24), radius = 1.5f * scale, center = Offset(centerX + 6f * scale, bodyY + 7f * scale))

    // 4. Arms & Hands
    if (isCelebrating) {
        // Both hands up in triumph!
        drawRect(Color(0xFFE52521), Offset(centerX - 14f * scale, bodyY - 4f * scale), Size(5f * scale, 10f * scale))
        drawRect(Color(0xFFE52521), Offset(centerX + 9f * scale, bodyY - 4f * scale), Size(5f * scale, 10f * scale))
        drawCircle(Color.White, radius = 3.5f * scale, center = Offset(centerX - 12f * scale, bodyY - 6f * scale))
        drawCircle(Color.White, radius = 3.5f * scale, center = Offset(centerX + 12f * scale, bodyY - 6f * scale))
    } else if (isJumping) {
        // Classic jumping pose: one fist in the air
        val armUpX = if (facingRight) centerX + 8f * scale else centerX - 12f * scale
        drawRect(Color(0xFFE52521), Offset(armUpX, bodyY - 5f * scale), Size(5f * scale, 11f * scale))
        drawCircle(Color.White, radius = 3.5f * scale, center = Offset(armUpX + 2.5f * scale, bodyY - 7f * scale))
    } else {
        // Walking arm swing
        val armSwing = if (isRunning) sin(gameTimeTicks * 0.35f).toFloat() * 4f * scale else 0f
        drawRect(Color(0xFFE52521), Offset(centerX - 13f * scale + armSwing, bodyY + 3f * scale), Size(5f * scale, 8f * scale))
        drawCircle(Color.White, radius = 3f * scale, center = Offset(centerX - 11f * scale + armSwing, bodyY + 11f * scale))
    }

    // 5. Legs & Brown Shoes
    val legY = bodyY + 14f * scale
    val legSpread = if (isRunning) sin(gameTimeTicks * 0.4f).toFloat() * 6f * scale else 0f

    // Left leg
    drawRect(Color(0xFF1D4ED8), Offset(centerX - 8f * scale - legSpread, legY), Size(6f * scale, 7f * scale))
    drawRoundRect(
        color = Color(0xFF78350F),
        topLeft = Offset(centerX - 10f * scale - legSpread, legY + 5f * scale),
        size = Size(8f * scale, 6f * scale),
        cornerRadius = CornerRadius(2f * scale, 2f * scale)
    )

    // Right leg
    drawRect(Color(0xFF1D4ED8), Offset(centerX + 2f * scale + legSpread, legY), Size(6f * scale, 7f * scale))
    drawRoundRect(
        color = Color(0xFF78350F),
        topLeft = Offset(centerX + 1f * scale + legSpread, legY + 5f * scale),
        size = Size(8f * scale, 6f * scale),
        cornerRadius = CornerRadius(2f * scale, 2f * scale)
    )
}

private fun DrawScope.drawFlagpole(x: Float, y: Float, height: Float, progress: Float, camX: Float, scale: Float) {
    val screenX = (x - camX) * scale
    val screenY = y * scale
    val poleH = height * scale

    // Green base hillock
    drawRoundRect(
        color = Color(0xFF15803D),
        topLeft = Offset(screenX - 16f * scale, screenY + poleH - 12f * scale),
        size = Size(36f * scale, 18f * scale),
        cornerRadius = CornerRadius(6f * scale, 6f * scale)
    )

    // Silver/Bronze Pole
    drawRect(
        color = Color(0xFFE2E8F0),
        topLeft = Offset(screenX, screenY),
        size = Size(5f * scale, poleH)
    )

    // Golden Ball on top
    drawCircle(Color(0xFFFFD700), radius = 7f * scale, center = Offset(screenX + 2.5f * scale, screenY))

    // Descending Flag
    val flagY = screenY + (poleH - 50f * scale) * progress
    val flagPath = Path().apply {
        moveTo(screenX + 5f * scale, flagY)
        lineTo(screenX + 45f * scale, flagY + 16f * scale)
        lineTo(screenX + 5f * scale, flagY + 32f * scale)
        close()
    }
    drawPath(flagPath, Color(0xFFE52521))
    // Flag star or icon
    drawCircle(Color(0xFFFFD700), radius = 4f * scale, center = Offset(screenX + 18f * scale, flagY + 16f * scale))
}

private fun DrawScope.drawEndCastle(x: Float, camX: Float, scale: Float) {
    val screenX = (x - camX) * scale
    val screenY = 250f * scale
    val castleW = 140f * scale
    val castleH = 150f * scale

    // Castle Main Wall (Retro Red Brick)
    drawRect(Color(0xFF9A3412), Offset(screenX, screenY), Size(castleW, castleH))

    // Battlements on top
    for (i in 0..4) {
        val bX = screenX + i * 28f * scale
        drawRect(Color(0xFF9A3412), Offset(bX, screenY - 18f * scale), Size(18f * scale, 18f * scale))
    }

    // Doorway
    drawRoundRect(
        color = Color(0xFF1E293B),
        topLeft = Offset(screenX + castleW * 0.35f, screenY + castleH * 0.5f),
        size = Size(castleW * 0.3f, castleH * 0.5f),
        cornerRadius = CornerRadius(16f * scale, 16f * scale)
    )

    // Small Castle Windows
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(screenX + 20f * scale, screenY + 30f * scale),
        size = Size(16f * scale, 24f * scale),
        cornerRadius = CornerRadius(8f * scale, 8f * scale)
    )
    drawRoundRect(
        color = Color(0xFF0F172A),
        topLeft = Offset(screenX + castleW - 36f * scale, screenY + 30f * scale),
        size = Size(16f * scale, 24f * scale),
        cornerRadius = CornerRadius(8f * scale, 8f * scale)
    )

    // Castle Banner "BAHASA"
    drawIntoCanvasText("ISTANA BAHASA", screenX + castleW * 0.5f, screenY - 26f * scale, 12f * scale, android.graphics.Color.YELLOW)
}

private fun DrawScope.drawIntoCanvasText(text: String, x: Float, y: Float, textSize: Float, colorInt: Int) {
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = colorInt
            this.textSize = textSize
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.DEFAULT_BOLD
            isAntiAlias = true
        }
        drawText(text, x, y, paint)
    }
}
