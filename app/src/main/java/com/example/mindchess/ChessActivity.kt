package com.example.mindchess


import android.os.Bundle
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import com.example.mindchess.chess_mechanics.ChessMatch

class ChessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chess)
        setSupportActionBar(findViewById(R.id.toolbar))

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // Start the chess game (AI?, sides?)
        // Listen for audio input, send it to be processed

        var match = ChessMatch(whiteAI=false, blackAI=false)
        var move_success = match.makeMove()
        if (move_success) {
            Toast.makeText(this, "Illegal move", Toast.LENGTH_LONG).show()
        }

//        setOnAudioListener...
//
//        if piece dropped on a square or recognized word is in move list (say "knight C 4") {
//            success = match.makeMove(white or black, that piece, that square) //either player or ai
//            if success {
//                match.ai_make_move()
//            }
//        }
    }

    //onPieceDropped makeMove(whatPiece, whichSquare) .. maybe some restrictions, maybe return true/false based on whether move was legal
    // ... and right then make AI's move


}