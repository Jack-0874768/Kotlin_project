package com.example.GreedySnake

import android.media.MediaPlayer

import android.os.Bundle
import android.view.KeyEvent
import android.view.WindowManager
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.example.GreedySnake.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //no sr toolbar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

    }

}


