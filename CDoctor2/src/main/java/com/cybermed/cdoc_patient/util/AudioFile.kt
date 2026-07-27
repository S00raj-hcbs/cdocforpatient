package com.cybermed.cdoc_patient.util

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.os.Build
import android.util.Base64

object AudioFile {

    fun decodeBase64File(base64: String): ByteArray {
        return Base64.decode(base64, 0)
    }

    fun playAudioFile(source: ByteArray): AudioTrack {
        val audioTrack = AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_CONFIGURATION_MONO, AudioFormat.ENCODING_PCM_16BIT, 500000, AudioTrack.MODE_STATIC)
        audioTrack.write(source, 0, source.size)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            audioTrack.setVolume(AudioTrack.getMaxVolume())
        }
        audioTrack.play()
        return audioTrack
    }

    fun decodeAndPlayAudioFile(base64: String) : AudioTrack{
        return playAudioFile(decodeBase64File(base64))
    }
}