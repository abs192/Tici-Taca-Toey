package com.abs192.ticitacatoey.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import com.abs192.ticitacatoey.R


class AudioManager(context: Context) {

    private val attributes: AudioAttributes = AudioAttributes.Builder()
        .setUsage(AudioAttributes.USAGE_GAME)
        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
        .build()
    var soundPool: SoundPool? = SoundPool.Builder()
        .setAudioAttributes(attributes)
        .build()

    private val click = soundPool?.load(context, R.raw.click1, 1)
    private val ping = soundPool?.load(context, R.raw.ping1, 1)

    fun playClick() {
        click?.let { soundPool?.play(it, 1F, 1F, 0, 0, 1F) }
    }

    fun playPing() {
        ping?.let { soundPool?.play(it, 1F, 1F, 0, 0, 1F) }
    }


    fun destroy() {
        soundPool?.release()
    }

}