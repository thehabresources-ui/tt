package com.example.data

import com.example.model.BlockType
import com.example.model.CoinItem
import com.example.model.EnemyState
import com.example.model.EnemyType
import com.example.model.FlagpoleState
import com.example.model.GameBlock
import com.example.model.LetterItem
import com.example.model.LevelData
import com.example.model.WorldTheme

object LevelRepository {

    fun getAllLevels(): List<LevelData> {
        return listOf(
            createLevel1_1(),
            createLevel1_2(),
            createLevel2_1(),
            createLevel2_2(),
            createLevel3_1(),
            createLevel3_2()
        )
    }

    fun getLevelById(id: String): LevelData {
        return getAllLevels().find { it.id == id } ?: createLevel1_1()
    }

    private fun createLevel1_1(): LevelData {
        val blocks = mutableListOf<GameBlock>()
        var blockId = 1

        // Ground floor with gaps: 0 to 600, 720 to 1400, 1520 to 2600
        fun addGround(startX: Float, endX: Float) {
            var x = startX
            while (x <= endX) {
                // Top layer ground
                blocks.add(GameBlock(blockId++, x, 400f, 40f, 40f, BlockType.GROUND))
                // Sub-layer ground
                blocks.add(GameBlock(blockId++, x, 440f, 40f, 40f, BlockType.GROUND))
                x += 40f
            }
        }

        addGround(0f, 680f)
        addGround(800f, 1500f)
        addGround(1620f, 2600f)

        // Question mark Mystery blocks & Bricks
        // Cluster 1 (x: 240..360)
        blocks.add(GameBlock(blockId++, 240f, 280f, 40f, 40f, BlockType.BRICK))
        blocks.add(GameBlock(blockId++, 280f, 280f, 40f, 40f, BlockType.MYSTERY, questionId = 1)) // Soalan 1
        blocks.add(GameBlock(blockId++, 320f, 280f, 40f, 40f, BlockType.BRICK, hasCoin = true))
        blocks.add(GameBlock(blockId++, 360f, 280f, 40f, 40f, BlockType.MYSTERY, questionId = 2)) // Soalan 2

        // Warp Pipe at x: 480
        blocks.add(GameBlock(blockId++, 480f, 320f, 40f, 40f, BlockType.PIPE_TOP))
        blocks.add(GameBlock(blockId++, 480f, 360f, 40f, 40f, BlockType.PIPE_BODY))

        // High floating platform (x: 900..1060)
        blocks.add(GameBlock(blockId++, 900f, 260f, 40f, 40f, BlockType.WOOD_PLATFORM))
        blocks.add(GameBlock(blockId++, 940f, 260f, 40f, 40f, BlockType.MYSTERY, questionId = 3)) // Soalan 3
        blocks.add(GameBlock(blockId++, 980f, 260f, 40f, 40f, BlockType.BRICK, hasCoin = true))
        blocks.add(GameBlock(blockId++, 1020f, 260f, 40f, 40f, BlockType.WOOD_PLATFORM))

        // Pipe at x: 1200
        blocks.add(GameBlock(blockId++, 1200f, 280f, 40f, 40f, BlockType.PIPE_TOP))
        blocks.add(GameBlock(blockId++, 1200f, 320f, 40f, 40f, BlockType.PIPE_BODY))
        blocks.add(GameBlock(blockId++, 1200f, 360f, 40f, 40f, BlockType.PIPE_BODY))

        // Stairway to flagpole (x: 1900..2060)
        for (step in 0..3) {
            val stepX = 1900f + (step * 40f)
            val stepY = 360f - (step * 40f)
            var currentY = 360f
            while (currentY >= stepY) {
                blocks.add(GameBlock(blockId++, stepX, currentY, 40f, 40f, BlockType.BRICK))
                currentY -= 40f
            }
        }

        // Enemies
        val enemies = listOf(
            EnemyState(id = 1, x = 400f, y = 364f, startX = 350f, endX = 460f, type = EnemyType.GOOMBA),
            EnemyState(id = 2, x = 1000f, y = 364f, startX = 850f, endX = 1150f, type = EnemyType.GOOMBA),
            EnemyState(id = 3, x = 1350f, y = 364f, startX = 1260f, endX = 1450f, type = EnemyType.KOOPA, vx = -1.5f),
            EnemyState(id = 4, x = 1750f, y = 364f, startX = 1650f, endX = 1850f, type = EnemyType.GOOMBA)
        )

        // Target word letters: "P-A-N-D-A-I"
        val word = "PANDAI"
        val letterCoords = listOf(
            280f to 220f,  // P above block
            580f to 340f,  // A
            940f to 200f,  // N above wood platform
            1300f to 340f, // D
            1680f to 340f, // A
            2100f to 200f  // I near flag
        )
        val letters = word.mapIndexed { idx, char ->
            val coord = letterCoords.getOrElse(idx) { (200f + idx * 300f) to 300f }
            LetterItem(id = idx + 1, x = coord.first, y = coord.second, char = char, indexInWord = idx)
        }

        // Bonus coins
        val coins = listOf(
            CoinItem(1, 200f, 340f),
            CoinItem(2, 240f, 220f),
            CoinItem(3, 320f, 220f),
            CoinItem(4, 900f, 200f),
            CoinItem(5, 1020f, 200f),
            CoinItem(6, 1400f, 340f),
            CoinItem(7, 1440f, 340f),
            CoinItem(8, 1780f, 340f)
        )

        val questions = GrammarDatabase.allQuestions.filter { it.id in listOf(1, 2, 3) }

        return LevelData(
            id = "1-1",
            world = 1,
            stage = 1,
            title = "Rimba Golongan Kata",
            subtitle = "Kembara Pertama Si Wira",
            topic = "Kenali Kata Nama, Kata Kerja & Kata Adjektif",
            theme = WorldTheme.JUNGLE,
            targetWord = word,
            levelWidth = 2600f,
            blocks = blocks,
            enemies = enemies,
            coins = coins,
            letters = letters,
            flagpole = FlagpoleState(x = 2260f, y = 140f),
            questions = questions
        )
    }

    private fun createLevel1_2(): LevelData {
        val blocks = mutableListOf<GameBlock>()
        var blockId = 100

        fun addGround(startX: Float, endX: Float) {
            var x = startX
            while (x <= endX) {
                blocks.add(GameBlock(blockId++, x, 400f, 40f, 40f, BlockType.GROUND))
                blocks.add(GameBlock(blockId++, x, 440f, 40f, 40f, BlockType.GROUND))
                x += 40f
            }
        }

        addGround(0f, 750f)
        addGround(880f, 1600f)
        addGround(1720f, 2600f)

        // Floating blocks & mystery questions 4, 5, 6
        blocks.add(GameBlock(blockId++, 280f, 270f, 40f, 40f, BlockType.MYSTERY, questionId = 4))
        blocks.add(GameBlock(blockId++, 320f, 270f, 40f, 40f, BlockType.BRICK, hasCoin = true))
        blocks.add(GameBlock(blockId++, 360f, 270f, 40f, 40f, BlockType.MYSTERY, questionId = 5))

        // Pipe
        blocks.add(GameBlock(blockId++, 540f, 320f, 40f, 40f, BlockType.PIPE_TOP))
        blocks.add(GameBlock(blockId++, 540f, 360f, 40f, 40f, BlockType.PIPE_BODY))

        // Elevated structure
        blocks.add(GameBlock(blockId++, 1050f, 260f, 40f, 40f, BlockType.WOOD_PLATFORM))
        blocks.add(GameBlock(blockId++, 1090f, 260f, 40f, 40f, BlockType.MYSTERY, questionId = 6))
        blocks.add(GameBlock(blockId++, 1130f, 260f, 40f, 40f, BlockType.WOOD_PLATFORM))

        val enemies = listOf(
            EnemyState(id = 11, x = 450f, y = 364f, startX = 380f, endX = 520f, type = EnemyType.GOOMBA),
            EnemyState(id = 12, x = 1150f, y = 364f, startX = 950f, endX = 1350f, type = EnemyType.KOOPA),
            EnemyState(id = 13, x = 1850f, y = 364f, startX = 1750f, endX = 2000f, type = EnemyType.GOOMBA)
        )

        val word = "CERDAS"
        val letterCoords = listOf(
            280f to 210f, 650f to 340f, 1090f to 200f, 1380f to 340f, 1800f to 340f, 2150f to 220f
        )
        val letters = word.mapIndexed { idx, char ->
            LetterItem(id = idx + 10, x = letterCoords[idx].first, y = letterCoords[idx].second, char = char, indexInWord = idx)
        }

        val coins = listOf(
            CoinItem(11, 200f, 340f),
            CoinItem(12, 400f, 340f),
            CoinItem(13, 1050f, 200f),
            CoinItem(14, 1130f, 200f),
            CoinItem(15, 1450f, 340f),
            CoinItem(16, 1900f, 340f)
        )

        return LevelData(
            id = "1-2",
            world = 1,
            stage = 2,
            title = "Bukit Penjodoh Bilangan",
            subtitle = "Lembah Buah & Barangan",
            topic = "Kuasai Penjodoh Bilangan & Adjektif",
            theme = WorldTheme.JUNGLE,
            targetWord = word,
            levelWidth = 2600f,
            blocks = blocks,
            enemies = enemies,
            coins = coins,
            letters = letters,
            flagpole = FlagpoleState(x = 2300f, y = 140f),
            questions = GrammarDatabase.allQuestions.filter { it.id in listOf(4, 5, 6) }
        )
    }

    private fun createLevel2_1(): LevelData {
        val blocks = mutableListOf<GameBlock>()
        var blockId = 200

        fun addGround(startX: Float, endX: Float) {
            var x = startX
            while (x <= endX) {
                blocks.add(GameBlock(blockId++, x, 400f, 40f, 40f, BlockType.GROUND))
                blocks.add(GameBlock(blockId++, x, 440f, 40f, 40f, BlockType.GROUND))
                x += 40f
            }
        }

        addGround(0f, 600f)
        addGround(700f, 1400f)
        addGround(1550f, 2600f)

        // Cavern mystery blocks (questions 7, 8, 9)
        blocks.add(GameBlock(blockId++, 260f, 280f, 40f, 40f, BlockType.BRICK))
        blocks.add(GameBlock(blockId++, 300f, 280f, 40f, 40f, BlockType.MYSTERY, questionId = 7))
        blocks.add(GameBlock(blockId++, 340f, 280f, 40f, 40f, BlockType.BRICK, hasCoin = true))

        blocks.add(GameBlock(blockId++, 850f, 270f, 40f, 40f, BlockType.MYSTERY, questionId = 8))
        blocks.add(GameBlock(blockId++, 1100f, 250f, 40f, 40f, BlockType.MYSTERY, questionId = 9))

        val enemies = listOf(
            EnemyState(id = 21, x = 450f, y = 364f, startX = 350f, endX = 550f, type = EnemyType.GOOMBA, vx = -1.4f),
            EnemyState(id = 22, x = 950f, y = 364f, startX = 800f, endX = 1250f, type = EnemyType.KOOPA, vx = -1.6f),
            EnemyState(id = 23, x = 1800f, y = 364f, startX = 1650f, endX = 2000f, type = EnemyType.GOOMBA, vx = -1.5f)
        )

        val word = "BELAJAR"
        val letterCoords = listOf(
            150f to 340f, 300f to 210f, 650f to 310f, 850f to 200f, 1100f to 180f, 1700f to 340f, 2100f to 250f
        )
        val letters = word.mapIndexed { idx, char ->
            LetterItem(id = idx + 20, x = letterCoords[idx].first, y = letterCoords[idx].second, char = char, indexInWord = idx)
        }

        val coins = listOf(
            CoinItem(21, 260f, 210f),
            CoinItem(22, 340f, 210f),
            CoinItem(23, 750f, 340f),
            CoinItem(24, 1200f, 340f),
            CoinItem(25, 1950f, 340f)
        )

        return LevelData(
            id = "2-1",
            world = 2,
            stage = 1,
            title = "Gua Imbuhan Awalan",
            subtitle = "Misteri Rahsia meN- & peN-",
            topic = "Kuasai Formula Leburan Imbuhan meN-",
            theme = WorldTheme.CAVERN,
            targetWord = word,
            levelWidth = 2600f,
            blocks = blocks,
            enemies = enemies,
            coins = coins,
            letters = letters,
            flagpole = FlagpoleState(x = 2280f, y = 140f),
            questions = GrammarDatabase.allQuestions.filter { it.id in listOf(7, 8, 9) }
        )
    }

    private fun createLevel2_2(): LevelData {
        val blocks = mutableListOf<GameBlock>()
        var blockId = 300

        fun addGround(startX: Float, endX: Float) {
            var x = startX
            while (x <= endX) {
                blocks.add(GameBlock(blockId++, x, 400f, 40f, 40f, BlockType.GROUND))
                blocks.add(GameBlock(blockId++, x, 440f, 40f, 40f, BlockType.GROUND))
                x += 40f
            }
        }

        addGround(0f, 700f)
        addGround(820f, 1500f)
        addGround(1620f, 2600f)

        // Blocks for questions 10, 11, 12
        blocks.add(GameBlock(blockId++, 280f, 270f, 40f, 40f, BlockType.MYSTERY, questionId = 10))
        blocks.add(GameBlock(blockId++, 950f, 260f, 40f, 40f, BlockType.MYSTERY, questionId = 11))
        blocks.add(GameBlock(blockId++, 1350f, 270f, 40f, 40f, BlockType.MYSTERY, questionId = 12))

        val enemies = listOf(
            EnemyState(id = 31, x = 400f, y = 364f, startX = 300f, endX = 550f, type = EnemyType.KOOPA, vx = -1.6f),
            EnemyState(id = 32, x = 1100f, y = 364f, startX = 900f, endX = 1300f, type = EnemyType.GOOMBA, vx = -1.5f),
            EnemyState(id = 33, x = 1850f, y = 364f, startX = 1700f, endX = 2050f, type = EnemyType.KOOPA, vx = -1.7f)
        )

        val word = "MENULIS"
        val letterCoords = listOf(
            200f to 340f, 280f to 200f, 550f to 340f, 950f to 190f, 1350f to 200f, 1750f to 340f, 2100f to 220f
        )
        val letters = word.mapIndexed { idx, char ->
            LetterItem(id = idx + 30, x = letterCoords[idx].first, y = letterCoords[idx].second, char = char, indexInWord = idx)
        }

        val coins = listOf(
            CoinItem(31, 240f, 270f),
            CoinItem(32, 320f, 270f),
            CoinItem(33, 900f, 340f),
            CoinItem(34, 1400f, 340f),
            CoinItem(35, 1900f, 340f)
        )

        return LevelData(
            id = "2-2",
            world = 2,
            stage = 2,
            title = "Lembah Imbuhan & Ejaan",
            subtitle = "Kembara Ejaan Mantap",
            topic = "Bezakan Ejaan Betul & Imbuhan Awalan",
            theme = WorldTheme.CAVERN,
            targetWord = word,
            levelWidth = 2600f,
            blocks = blocks,
            enemies = enemies,
            coins = coins,
            letters = letters,
            flagpole = FlagpoleState(x = 2280f, y = 140f),
            questions = GrammarDatabase.allQuestions.filter { it.id in listOf(10, 11, 12) }
        )
    }

    private fun createLevel3_1(): LevelData {
        val blocks = mutableListOf<GameBlock>()
        var blockId = 400

        fun addGround(startX: Float, endX: Float) {
            var x = startX
            while (x <= endX) {
                blocks.add(GameBlock(blockId++, x, 400f, 40f, 40f, BlockType.GROUND))
                blocks.add(GameBlock(blockId++, x, 440f, 40f, 40f, BlockType.GROUND))
                x += 40f
            }
        }

        addGround(0f, 650f)
        addGround(780f, 1550f)
        addGround(1680f, 2600f)

        // Castle platforming blocks & questions 13, 14, 15
        blocks.add(GameBlock(blockId++, 300f, 280f, 40f, 40f, BlockType.MYSTERY, questionId = 13))
        blocks.add(GameBlock(blockId++, 1000f, 260f, 40f, 40f, BlockType.MYSTERY, questionId = 14))
        blocks.add(GameBlock(blockId++, 1400f, 270f, 40f, 40f, BlockType.MYSTERY, questionId = 15))

        val enemies = listOf(
            EnemyState(id = 41, x = 450f, y = 364f, startX = 350f, endX = 600f, type = EnemyType.GOOMBA, vx = -1.6f),
            EnemyState(id = 42, x = 1150f, y = 364f, startX = 950f, endX = 1350f, type = EnemyType.KOOPA, vx = -1.8f),
            EnemyState(id = 43, x = 1850f, y = 364f, startX = 1750f, endX = 2050f, type = EnemyType.KOOPA, vx = -1.8f)
        )

        val word = "JUARA"
        val letterCoords = listOf(
            180f to 340f, 300f to 200f, 1000f to 180f, 1400f to 190f, 2100f to 220f
        )
        val letters = word.mapIndexed { idx, char ->
            LetterItem(id = idx + 40, x = letterCoords[idx].first, y = letterCoords[idx].second, char = char, indexInWord = idx)
        }

        val coins = listOf(
            CoinItem(41, 250f, 280f),
            CoinItem(42, 350f, 280f),
            CoinItem(43, 900f, 340f),
            CoinItem(44, 1300f, 340f),
            CoinItem(45, 1950f, 340f)
        )

        return LevelData(
            id = "3-1",
            world = 3,
            stage = 1,
            title = "Istana Raja Peribahasa",
            subtitle = "Pertarungan Minda Bahasa",
            topic = "Kuasai Simpulan Bahasa & Peribahasa",
            theme = WorldTheme.CASTLE,
            targetWord = word,
            levelWidth = 2600f,
            blocks = blocks,
            enemies = enemies,
            coins = coins,
            letters = letters,
            flagpole = FlagpoleState(x = 2280f, y = 140f),
            questions = GrammarDatabase.allQuestions.filter { it.id in listOf(13, 14, 15) }
        )
    }

    private fun createLevel3_2(): LevelData {
        val blocks = mutableListOf<GameBlock>()
        var blockId = 500

        fun addGround(startX: Float, endX: Float) {
            var x = startX
            while (x <= endX) {
                blocks.add(GameBlock(blockId++, x, 400f, 40f, 40f, BlockType.GROUND))
                blocks.add(GameBlock(blockId++, x, 440f, 40f, 40f, BlockType.GROUND))
                x += 40f
            }
        }

        addGround(0f, 750f)
        addGround(880f, 1600f)
        addGround(1750f, 2600f)

        // Castle pinnacle questions 16, 17, 18
        blocks.add(GameBlock(blockId++, 320f, 270f, 40f, 40f, BlockType.MYSTERY, questionId = 16))
        blocks.add(GameBlock(blockId++, 1050f, 260f, 40f, 40f, BlockType.MYSTERY, questionId = 17))
        blocks.add(GameBlock(blockId++, 1450f, 260f, 40f, 40f, BlockType.MYSTERY, questionId = 18))

        val enemies = listOf(
            EnemyState(id = 51, x = 450f, y = 364f, startX = 350f, endX = 650f, type = EnemyType.KOOPA, vx = -1.9f),
            EnemyState(id = 52, x = 1150f, y = 364f, startX = 950f, endX = 1400f, type = EnemyType.GOOMBA, vx = -1.7f),
            EnemyState(id = 53, x = 1900f, y = 364f, startX = 1800f, endX = 2100f, type = EnemyType.KOOPA, vx = -2.0f)
        )

        val word = "WIRA"
        val letterCoords = listOf(
            200f to 340f, 320f to 190f, 1050f to 180f, 2100f to 220f
        )
        val letters = word.mapIndexed { idx, char ->
            LetterItem(id = idx + 50, x = letterCoords[idx].first, y = letterCoords[idx].second, char = char, indexInWord = idx)
        }

        val coins = listOf(
            CoinItem(51, 280f, 270f),
            CoinItem(52, 360f, 270f),
            CoinItem(53, 950f, 340f),
            CoinItem(54, 1500f, 340f),
            CoinItem(55, 1850f, 340f)
        )

        return LevelData(
            id = "3-2",
            world = 3,
            stage = 2,
            title = "Puncak Mahkota Bahasa",
            subtitle = "Ujian Terunggul Si Wira",
            topic = "Gabungan Tatabahasa Mantap",
            theme = WorldTheme.CASTLE,
            targetWord = word,
            levelWidth = 2600f,
            blocks = blocks,
            enemies = enemies,
            coins = coins,
            letters = letters,
            flagpole = FlagpoleState(x = 2280f, y = 140f),
            questions = GrammarDatabase.allQuestions.filter { it.id in listOf(16, 17, 18) }
        )
    }
}
