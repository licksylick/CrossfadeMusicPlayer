package com.example.crossfademusicplayer

import androidx.appcompat.app.AppCompatActivity
import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private lateinit var mp: MediaPlayer
    private lateinit var mp1: MediaPlayer
    private var totalTime: Int = 0
    private var totalTime1: Int = 0

    var left = 0.5f
    var right = 0.5f
    var left1 = 0.5f
    var right1 = 0.5f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mp = MediaPlayer.create(this, R.raw.music)
        mp1 = MediaPlayer.create(this, R.raw.music1)
        mp.isLooping = false
        mp1.isLooping = false
        mp.setVolume(left1, right)
        mp1.setVolume(left1, right1)
        totalTime = mp.duration
        totalTime1 = mp1.duration

        // Position Bar
        positionBar.max = totalTime
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

        positionBar1.max = totalTime1
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
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                }
            }
        }).start()


        // Thread
        Thread(Runnable {
            while (mp1 != null) {
                try {
                    var msg = Message()
                    msg.what = mp1.currentPosition
                    handler1.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
            }
            }
        }).start()

        mp.start()
    }

    @SuppressLint("HandlerLeak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition = msg.what

            // Update positionBar
            positionBar.progress = currentPosition

            // Update Labels
            var elapsedTime = createTimeLabel(currentPosition)
            elapsedTimeLabel.text = elapsedTime

            var remainingTime = createTimeLabel(totalTime - currentPosition)
            remainingTimeLabel.text = "-$remainingTime"

            if (totalTime - currentPosition <= 10000)
                mp1.start()
        }
    }

    @SuppressLint("HandlerLeak")
    var handler1 = object : Handler() {
        override fun handleMessage(msg: Message) {
            var currentPosition1 = msg.what

            // Update positionBar
            positionBar1.progress = currentPosition1

            // Update Labels
            var elapsedTime1 = createTimeLabel1(currentPosition1)
            elapsedTimeLabel1.text = elapsedTime1

            var remainingTime1 = createTimeLabel1(totalTime1 - currentPosition1)
            remainingTimeLabel1.text = "-$remainingTime1"

            if (totalTime1 - currentPosition1 <= 10000)
                mp.start()
        }
    }

    fun createTimeLabel(time: Int): String {
        var timeLabel = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel = "$min:"
        if (sec < 10) timeLabel += "0"
        timeLabel += sec

        return timeLabel
    }

    fun createTimeLabel1(time: Int): String {
        var timeLabel1 = ""
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timeLabel1 = "$min:"
        if (sec < 10) timeLabel1 += "0"
        timeLabel1 += sec

        return timeLabel1
    }

    fun addBtn1(v: View){

    }

    fun addBtn2(v: View){

    }
}
