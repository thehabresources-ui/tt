package com.example

import com.example.data.GrammarDatabase
import com.example.data.LevelRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testLevelRepositoryHasLevels() {
        val levels = LevelRepository.getAllLevels()
        assertTrue("Levels should not be empty", levels.isNotEmpty())
        assertEquals(6, levels.size)

        val firstLevel = LevelRepository.getLevelById("1-1")
        assertNotNull(firstLevel)
        assertEquals("PANDAI", firstLevel.targetWord)
        assertTrue(firstLevel.blocks.isNotEmpty())
        assertTrue(firstLevel.enemies.isNotEmpty())
        assertTrue(firstLevel.letters.isNotEmpty())
    }

    @Test
    fun testGrammarDatabaseQuestions() {
        val questions = GrammarDatabase.allQuestions
        assertTrue(questions.isNotEmpty())
        for (q in questions) {
            assertTrue("Options should have at least 2 choices", q.options.size >= 2)
            assertTrue("Correct index should be valid", q.correctIndex in q.options.indices)
            assertTrue("Explanation should be provided", q.explanation.isNotBlank())
        }
    }

    @Test
    fun testGrammarConcepts() {
        val concepts = GrammarDatabase.concepts
        assertTrue(concepts.isNotEmpty())
        for (c in concepts) {
            assertTrue(c.title.isNotBlank())
            assertTrue(c.definition.isNotBlank())
            assertTrue(c.examples.isNotEmpty())
        }
    }
}
