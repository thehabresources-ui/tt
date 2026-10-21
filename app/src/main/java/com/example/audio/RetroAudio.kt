package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

class RetroAudio(private val context: Context) {
    private val scope = CoroutineScope(Dispatchers.Default)
    private var soundEnabled: Boolean = true
    private var hapticsEnabled: Boolean = true

    private val vibrator: Vibrator? by lazy {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            null
        }
    }

    fun toggleSound(): Boolean {
        soundEnabled = !soundEnabled
        return soundEnabled
    }

    fun isSoundEnabled(): Boolean = soundEnabled

    fun playJump() {
        if (!soundEnabled) return
        scope.launch {
            playSweepTone(startFreq = 260f, endFreq = 620f, durationMs = 120, isSquare = true)
        }
        vibrate(30, 80)
    }

    fun playCoin() {
        if (!soundEnabled) return
        scope.launch {
            playTone(987.77f, 60, isSquare = true)
            playTone(1318.51f, 150, isSquare = true)
        }
        vibrate(40, 150)
    }

    fun playBlockBump() {
        if (!soundEnabled) return
        scope.launch {
            playSweepTone(startFreq = 220f, endFreq = 120f, durationMs = 80, isSquare = true)
        }
        vibrate(50, 200)
    }

    fun playEnemyStomp() {
        if (!soundEnabled) return
        scope.launch {
            playSweepTone(startFreq = 350f, endFreq = 110f, durationMs = 90, isSquare = true)
        }
        vibrate(60, 220)
    }

    fun playQuizSuccess() {
        if (!soundEnabled) return
        scope.launch {
            // C5, E5, G5, C6 triumphant arpeggio
            val notes = listOf(523.25f, 659.25f, 783.99f, 1046.50f)
            for (freq in notes) {
                playTone(freq, 70, isSquare = true)
            }
        }
        vibratePattern(longArrayOf(0, 50, 40, 70), intArrayOf(0, 150, 0, 255))
    }

    fun playQuizWrong() {
        if (!soundEnabled) return
        scope.launch {
            playTone(180f, 120, isSquare = true)
            playTone(140f, 180, isSquare = true)
        }
        vibrate(100, 180)
    }

    fun playHurt() {
        if (!soundEnabled) return
        scope.launch {
            playSweepTone(startFreq = 400f, endFreq = 90f, durationMs = 200, isSquare = true)
        }
        vibrate(120, 255)
    }

    fun playVictory() {
        if (!soundEnabled) return
        scope.launch {
            // Classic fanfare notes: G4, C5, E5, G5, C6
            val fanfare = listOf(
                392.00f to 100,
                523.25f to 100,
                659.25f to 100,
                783.99f to 120,
                1046.50f to 300
            )
            for ((freq, dur) in fanfare) {
                playTone(freq, dur, isSquare = true)
            }
        }
        vibratePattern(longArrayOf(0, 80, 50, 80, 50, 200), intArrayOf(0, 180, 0, 200, 0, 255))
    }

    private fun playTone(freq: Float, durationMs: Int, isSquare: Boolean = false) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            if (numSamples <= 0) return
            val buffer = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val time = i.toFloat() / sampleRate
                val raw = sin(2.0 * Math.PI * freq * time).toFloat()
                val wave = if (isSquare) {
                    if (raw >= 0) 0.35f else -0.35f
                } else {
                    raw * 0.4f
                }
                // Quick envelope decay at end to avoid pops
                val fadeSamples = (sampleRate * 0.005f).toInt()
                val envelope = when {
                    i < fadeSamples -> i.toFloat() / fadeSamples
                    i > numSamples - fadeSamples -> (numSamples - i).toFloat() / fadeSamples
                    else -> 1.0f
                }
                buffer[i] = (wave * envelope * Short.MAX_VALUE).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 20)
            track.release()
        } catch (_: Exception) {
            // AudioTrack might fail gracefully on some container restrictions
        }
    }

    private fun playSweepTone(startFreq: Float, endFreq: Float, durationMs: Int, isSquare: Boolean = false) {
        try {
            val sampleRate = 22050
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            if (numSamples <= 0) return
            val buffer = ShortArray(numSamples)

            var phase = 0.0
            for (i in 0 until numSamples) {
                val t = i.toFloat() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                phase += 2.0 * Math.PI * currentFreq / sampleRate

                val raw = sin(phase).toFloat()
                val wave = if (isSquare) {
                    if (raw >= 0) 0.35f else -0.35f
                } else {
                    raw * 0.4f
                }
                val envelope = 1.0f - (i.toFloat() / numSamples) * 0.3f
                buffer[i] = (wave * envelope * Short.MAX_VALUE).toInt().toShort()
            }

            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            Thread.sleep(durationMs.toLong() + 20)
            track.release()
        } catch (_: Exception) {
            // Ignore safe audio errors
        }
    }

    private fun vibrate(durationMs: Long, amplitude: Int = 128) {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(
                    VibrationEffect.createOneShot(
                        durationMs,
                        amplitude.coerceIn(1, 255)
                    )
                )
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
        }
    }

    private fun vibratePattern(timings: LongArray, amplitudes: IntArray) {
        if (!hapticsEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(timings, -1)
            }
        } catch (_: Exception) {
        }
    }
}
