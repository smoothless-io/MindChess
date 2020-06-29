package com.example.mindchess

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.mindchess.common.startWithFade
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        (wheelchair_man.drawable as AnimationDrawable).start()

        // startActivity(Intent(this, ChessActivity::class.java))

        // Menu, choose chess or poker..
    }

    infix fun Int.greaterThan(other : Int) : Int {
        var result = this

        if (this < other) {
            result = other
        }
        return result
    }

    override fun onPause() {
        super.onPause()
        (wheelchair_man.drawable as AnimationDrawable).stop()
    }

}
