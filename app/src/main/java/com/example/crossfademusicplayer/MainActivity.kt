package com.example.crossfademusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*

private const val SONG1_REQUEST_CODE = 1
private const val SONG2_REQUEST_CODE = 2
private const val DEFAULT_VOLUME = 0.5f

class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private lateinit var mp1: MediaPlayer
    private var totalTime: Int = 0
    private var totalTime1: Int = 0

    private var volume = 0.5f
    private var volume1= 0f

    private var crossFade = 1000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        addBtn1.setOnClickListener { pickAudioFile(SONG1_REQUEST_CODE) }
        addBtn2.setOnClickListener { pickAudioFile(SONG2_REQUEST_CODE) }

        // initMediaPlayers with default songs
        mp = MediaPlayer.create(this, R.raw.music)
        mp1 = MediaPlayer.create(this, R.raw.music1)
        mp.isLooping = false
        mp1.isLooping = false
        mp.setVolume(DEFAULT_VOLUME, DEFAULT_VOLUME)
        mp1.setVolume(0f, 0f)
        totalTime = mp.duration
        totalTime1 = mp1.duration

        val fade = findViewById<SeekBar>(R.id.fadeSeekBar)
        fade.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        crossFade = progress*1000 + 2000 // Given the delay (2 seconds)
                        findViewById<TextView>(R.id.fadeValue).setText(progress.toString())
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )
        // Position Bar
        positionBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        positionBar1.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp1.seekTo(progress)
                    }
                }
                override fun onStartTrackingTouch(p0: SeekBar?) {
                }
                override fun onStopTrackingTouch(p0: SeekBar?) {
                }
            }
        )

        // Thread
        Thread(Runnable {
            while (true) {
                try {
                    val msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()


        // Thread
        Thread(Runnable {
            while (true) {
                try {
                    val msg = Message()
                    msg.what = mp1.currentPosition
                    handler1.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
            }
            }
        }).start()
    }

    private fun pickAudioFile(requestCode: Int) {
        val audioUri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        intent = Intent(Intent.ACTION_PICK, audioUri)
        intent.type = "*/*"
        startActivityForResult(intent, requestCode)
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val currentPosition = msg.what

            // Update positionBar
            positionBar.progress = currentPosition

            // Update Labels
            val elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime

            val remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"

            if (totalTime - currentPosition <= crossFade) {
                mp1.start()
                volume -= (volume/(crossFade/1000)*1f)

                volume1 += (volume/(crossFade/1000)*1f)
                mp.setVolume(volume, volume)
                mp1.setVolume(volume1, volume1)
            }
        }
    }

    @SuppressLint("HandlerLeak")
    var handler1 = object : Handler() {
        override fun handleMessage(msg: Message) {
            val currentPosition1 = msg.what

            // Update positionBar
            positionBar1.progress = currentPosition1

            // Update Labels
            val elapsedTime1 = createTimeLabel1(currentPosition1)
            elapsedTimeLabel1.text = elapsedTime1

            val remainingTime1 = createTimeLabel1(totalTime1 - currentPosition1)
            remainingTimeLabel1.text = "-$remainingTime1"

            if (totalTime1 - currentPosition1 <= crossFade) {
                mp.start()
                volume1 -= (volume/(crossFade/1000)*1f)
                volume += (volume1/(crossFade/1000)*1f)
                mp1.setVolume (volume1, volume1)
                mp.setVolume(volume, volume)
            }
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun createTimeLabel1(time: Int): String {
        var timeLabel1 = ""
        val min = time / 1000 / 60
        val sec = time / 1000 % 60

        timeLabel1 = "$min:"
        if (sec < 10) timeLabel1 += "0"
        timeLabel1 += sec

        return timeLabel1
    }

    fun addBtn1Click(v: View){

    }

    fun addBtn2Click(v: View){

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            SONG1_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    mp.reset()
                    mp = MediaPlayer.create(this, data!!.data)
                    mp.isLooping = false
                    mp.setVolume(volume, volume)
                    totalTime = mp.duration
                    positionBar.max = totalTime
                    mp.start()
                }
            }

            SONG2_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK){
                    mp1.reset()
                    mp1 = MediaPlayer.create(this, data!!.data)
                    mp1.isLooping = false
                    mp1.setVolume(volume1, volume1)
                    totalTime1 = mp1.duration
                    positionBar1.max = totalTime1
                }
            }
        }
    }
}


