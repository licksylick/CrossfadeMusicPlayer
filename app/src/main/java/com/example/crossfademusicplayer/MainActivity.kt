package com.example.crossfademusicplayer

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val nextBtn = findViewById<Button>(R.id.nextBtn)
        nextBtn.setOnClickListener {
            val i = Intent(this, Main2Activity::class.java)
            startActivity(i)
        }
    }
}

