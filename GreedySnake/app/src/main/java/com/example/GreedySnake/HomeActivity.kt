package com.example.GreedySnake

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import java.io.IOException

class HomeActivity : AppCompatActivity() {
    internal var mediaPlayer1: MediaPlayer? = null
    //re-click to exit
    private var exitTime: Long = 0
    internal lateinit var btnStart: Button
    internal lateinit var btnMusic: Button
    internal var isMusicOpen = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        //play bg music
        mediaPlayer1 = null
        mediaPlayer1 = MediaPlayer.create(this@HomeActivity, R.raw.snake)
        //repeat play
        mediaPlayer1!!.setOnCompletionListener {
            mediaPlayer1!!.start()
            mediaPlayer1!!.isLooping = true
        }
        //Binding layout  control switch
        btnMusic = findViewById(R.id.btn_music)
        btnMusic.setOnClickListener {
            if (!isMusicOpen) {
                btnMusic.text = resources.getString(R.string.C_M)
                playMusic(true)
                Toast.makeText(this, resources.getString(R.string.T_O), Toast.LENGTH_SHORT).show()
            } else {
                btnMusic.text =resources.getString(R.string.O_M)
                playMusic(false)
                Toast.makeText(this, resources.getString(R.string.T_C), Toast.LENGTH_SHORT).show()
            }
            isMusicOpen = !isMusicOpen
        }
        btnStart = findViewById(R.id.btn_start)
        btnStart.setOnClickListener { startActivity(Intent(this@HomeActivity, MainActivity::class.java)) }
    }


    private fun playMusic(status: Boolean) {
        if (status) {
            //Judging whether it has started
            if (!mediaPlayer1!!.isPlaying) {
                mediaPlayer1!!.start()
            }
        } else {
            if (mediaPlayer1!!.isPlaying) {
                mediaPlayer1!!.stop()
                //Prepare for the next digitization
                try{
                    mediaPlayer1!!.prepare()
                }catch (e: IOException) {
                    println(resources.getString(R.string.P_F))
                }
                
            }
        }
    }

    //Determine whether to exit the app
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
               //Press once to exit send a reminder.
                   // You can quit the app by pressing quit twice within two seconds
            if (System.currentTimeMillis() - exitTime > 2000) {
                Toast.makeText(applicationContext, resources.getString(R.string.R_C_E), Toast.LENGTH_SHORT).show()
                exitTime = System.currentTimeMillis()
            } else {
                finish()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    //Destroy
    public override fun onDestroy() {
        super.onDestroy()
        if (mediaPlayer1!!.isPlaying) {
            mediaPlayer1!!.stop()
        }
    }
}


