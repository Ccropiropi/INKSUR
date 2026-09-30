package com.example.model

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.CombinedVibration
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin
import kotlin.random.Random

class SoundManager(context: Context) {
    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isSoundEnabled: Boolean = true
    var isHapticEnabled: Boolean = true

    private val scope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 22050

    fun playSwoosh() {
        if (!isSoundEnabled) return
        scope.launch {
            generateNoiseSweep(
                startFreq = 480f,
                endFreq = 160f,
                durationMs = 90,
                volume = 0.28f
            )
        }
    }

    fun playSplatter() {
        if (!isSoundEnabled) return
        scope.launch {
            generateSplatterSound()
        }
        triggerHaptic(VibrationType.MEDIUM)
    }

    fun playReactionBoom() {
        if (!isSoundEnabled) return
        scope.launch {
            generateReactionBoom()
        }
        triggerHaptic(VibrationType.HEAVY)
    }

    fun playHit() {
        triggerHaptic(VibrationType.LIGHT)
    }

    fun playLevelUp() {
        if (!isSoundEnabled) return
        scope.launch {
            playChord(listOf(440f, 554.37f, 659.25f, 880f), 320)
        }
        triggerHaptic(VibrationType.HEAVY)
    }

    enum class VibrationType { LIGHT, MEDIUM, HEAVY }

    fun triggerHaptic(type: VibrationType) {
        if (!isHapticEnabled || vibrator == null || !vibrator.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val effect = when (type) {
                    VibrationType.LIGHT -> VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE)
                    VibrationType.MEDIUM -> VibrationEffect.createOneShot(38, 180)
                    VibrationType.HEAVY -> VibrationEffect.createWaveform(longArrayOf(0, 45, 30, 60), intArrayOf(0, 200, 0, 255), -1)
                }
                vibrator.vibrate(effect)
            } else {
                @Suppress("DEPRECATION")
                val duration = when (type) {
                    VibrationType.LIGHT -> 20L
                    VibrationType.MEDIUM -> 40L
                    VibrationType.HEAVY -> 80L
                }
                vibrator.vibrate(duration)
            }
        } catch (_: Exception) {}
    }

    private fun generateNoiseSweep(startFreq: Float, endFreq: Float, durationMs: Int, volume: Float) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toFloat() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                val envelope = sin(t * PI.toFloat())
                phase += 2.0 * PI * currentFreq / sampleRate
                val tone = sin(phase) * 0.4f
                val noise = (Random.nextFloat() * 2f - 1f) * 0.6f
                val sample = (tone + noise) * envelope * volume
                buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        } catch (_: Exception) {}
    }

    private fun generateSplatterSound() {
        try {
            val numSamples = (sampleRate * 0.14f).toInt()
            val buffer = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toFloat() / numSamples
                val env = exp(-t * 12.0).toFloat()
                val freq = 320f * (1f - t * 0.7f)
                phase += 2.0 * PI * freq / sampleRate
                val pop = sin(phase) * 0.7f + (Random.nextFloat() * 2f - 1f) * 0.3f
                val sample = pop * env * 0.35f
                buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        } catch (_: Exception) {}
    }

    private fun generateReactionBoom() {
        try {
            val numSamples = (sampleRate * 0.28f).toInt()
            val buffer = ShortArray(numSamples)
            var phase = 0.0

            for (i in 0 until numSamples) {
                val t = i.toFloat() / numSamples
                val env = exp(-t * 6.0).toFloat()
                val freq = 160f * (1f - t * 0.6f)
                phase += 2.0 * PI * freq / sampleRate
                val bass = sin(phase) * 0.8f + (Random.nextFloat() * 2f - 1f) * 0.2f
                val sample = bass * env * 0.45f
                buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        } catch (_: Exception) {}
    }

    private fun playChord(freqs: List<Float>, durationMs: Int) {
        try {
            val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
            val buffer = ShortArray(numSamples)
            val phases = DoubleArray(freqs.size)

            for (i in 0 until numSamples) {
                val t = i.toFloat() / numSamples
                val env = (1f - t) * sin((t * 0.5f + 0.1f) * PI.toFloat())
                var combined = 0f
                for (j in freqs.indices) {
                    phases[j] += 2.0 * PI * freqs[j] / sampleRate
                    combined += sin(phases[j]).toFloat() / freqs.size
                }
                val sample = combined * env * 0.3f
                buffer[i] = (sample * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }
            playPcmBuffer(buffer)
        } catch (_: Exception) {}
    }

    private fun playPcmBuffer(buffer: ShortArray) {
        val audioTrack = AudioTrack.Builder()
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

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        scope.launch {
            kotlinx.coroutines.delay(buffer.size * 1000L / sampleRate + 50)
            try {
                audioTrack.stop()
                audioTrack.release()
            } catch (_: Exception) {}
        }
    }
}
