package com.example.GreedySnake

import android.media.MediaPlayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.example.GreedySnake.R

class MainActivity : AppCompatActivity() {
    internal var mediaPlayer1: MediaPlayer? = null
    //re-click to exit
    private var exitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        //play bg music
        mediaPlayer1 = null
        mediaPlayer1 = MediaPlayer.create(this, R.raw.snake)
        mediaPlayer1!!.start()

        //repeat play
        mediaPlayer1!!.setOnCompletionListener {
            mediaPlayer1!!.start()
            mediaPlayer1!!.isLooping = true
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(applicationContext, "re-click to exit this app", Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            } else {
                finish()
                System.exit(0)
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}


