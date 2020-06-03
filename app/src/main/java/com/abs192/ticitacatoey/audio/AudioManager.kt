package com.abs192.ticitacatoey.audio

import android.app.Activity
import android.media.MediaPlayer

class AudioManager(var activity: Activity) {

    private var assetFileNameClick = "click1.mp3"
    private var assetFileNamePing = "ping1.mp3"

    fun playClick() {
        play(assetFileNameClick)
    }

    fun playPing() {
        play(assetFileNamePing)
    }

    private fun play(s: String) {
        try {
            val mp = MediaPlayer()
            val afd = activity.assets.openFd(s)
            mp.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            mp.prepare()
            mp.start()
            mp.setOnCompletionListener {
                it.stop()
                it.release()
            }
        } catch (e: Exception) {
        }
    }

}