package com.example.mindchess

import android.app.AlertDialog
import android.content.Context
import android.graphics.*
import android.os.Build
import android.util.Log
import android.view.Display
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.annotation.RequiresApi
import com.example.mindchess.common.*
import com.example.mindchess.common.toInt
import java.lang.Integer.min
import java.lang.Math.max


private val LOG_TAG = "ChessGameViewTest"


private data class BoardInfo(
    val size: Rect,
    val tiles: Int,
    val tileSize: Int,
    val topLeftPoint: Point,
    val lightSquarePaint: Paint,
    val darkSquarePaint: Paint,
    val selectedSquarePaint: Paint
)

class ChessGameView(context: Context) : SurfaceView(context), GameEventListener {

    private var viewListeners = arrayListOf<ChessGameViewListener>()
    private var gameViewModel: GameViewModel? = null
    private var pieceImageMap: Array<MutableMap<String, Bitmap>> = arrayOf(mutableMapOf(), mutableMapOf())

    private lateinit var boardInfo: BoardInfo

    init {
        
        pieceImageMap[0]["PAWN"] = BitmapFactory.decodeResource(resources, R.drawable.white_pawn)
        pieceImageMap[0]["KNIGHT"] = BitmapFactory.decodeResource(resources, R.drawable.white_knight)
        pieceImageMap[0]["BISHOP"] = BitmapFactory.decodeResource(resources, R.drawable.white_bishop)
        pieceImageMap[0]["ROOK"] = BitmapFactory.decodeResource(resources, R.drawable.white_rook)
        pieceImageMap[0]["QUEEN"] = BitmapFactory.decodeResource(resources, R.drawable.white_queen)
        pieceImageMap[0]["KING"] = BitmapFactory.decodeResource(resources, R.drawable.white_king)

        pieceImageMap[1]["PAWN"] = BitmapFactory.decodeResource(resources, R.drawable.black_pawn)
        pieceImageMap[1]["KNIGHT"] = BitmapFactory.decodeResource(resources, R.drawable.black_knight)
        pieceImageMap[1]["BISHOP"] = BitmapFactory.decodeResource(resources, R.drawable.black_bishop)
        pieceImageMap[1]["ROOK"] = BitmapFactory.decodeResource(resources, R.drawable.black_rook)
        pieceImageMap[1]["QUEEN"] = BitmapFactory.decodeResource(resources, R.drawable.black_queen)
        pieceImageMap[1]["KING"] = BitmapFactory.decodeResource(resources, R.drawable.black_king)
        
        
        holder.addCallback(object : SurfaceHolder.Callback {

            @RequiresApi(Build.VERSION_CODES.N)
            override fun surfaceCreated(holder: SurfaceHolder?) {
                boardInfo = getBoardInfo()
                drawBoard()


            }

            override fun surfaceChanged(
                holder: SurfaceHolder?,
                format: Int,
                width: Int,
                height: Int
            ) {
            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {
            }

        }

        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getBoardInfo() : BoardInfo {
        val size = Rect()
        display?.getRectSize(size)

        val tiles = BOARD_TILES
        val tileSize = min(size.width(), size.height()) / tiles

        val lightSquarePaint = Paint()
        lightSquarePaint.color = LIGHT_SQUARE_COLOR

        val darkSquarePaint = Paint()
        darkSquarePaint.color = DARK_SQUARE_COLOR

        val selectedSquarePaint = Paint()
        selectedSquarePaint.color = SELECTED_SQUARE_COLOR

        val topLeftPoint = Point()
        topLeftPoint.set(
            (size.width() > size.height()).toInt() * (size.width() - size.height()) / 2,
            (size.height() > size.width()).toInt() * (size.height() - size.width()) / 2
        )


        return BoardInfo(
            size = size,
            tiles = tiles,
            tileSize = tileSize,
            topLeftPoint = topLeftPoint,
            lightSquarePaint = lightSquarePaint,
            darkSquarePaint = darkSquarePaint,
            selectedSquarePaint = selectedSquarePaint
        )


    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun drawBoard() {
        if (holder.surface.isValid) {


            val canvas = holder.lockCanvas()

            canvas.drawRGB(120, 200, 120)


            for (file in 0..boardInfo.tiles - 1) {
                for (rank in 0..boardInfo.tiles - 1) {

                    val rect = Rect(boardInfo.topLeftPoint.x + file * boardInfo.tileSize,
                        boardInfo.topLeftPoint.y + rank * boardInfo.tileSize,
                        boardInfo.topLeftPoint.x + (file + 1) * boardInfo.tileSize,
                        boardInfo.topLeftPoint.y + (rank + 1) * boardInfo.tileSize)

                    if ((file + rank) % 2 == 0) {
                        canvas.drawRect(rect, boardInfo.darkSquarePaint)
                    } else {
                        canvas.drawRect(rect, boardInfo.lightSquarePaint)
                    }
                }
            }

            if (gameViewModel?.selected_coordinate != null) {
                val rect = Rect(
                    boardInfo.topLeftPoint.x + gameViewModel!!.selected_coordinate!!.x * boardInfo . tileSize,
                    boardInfo.topLeftPoint.y + (BOARD_TILES - 1 - gameViewModel!!.selected_coordinate!!.y) * boardInfo.tileSize,
                    boardInfo.topLeftPoint.x + (gameViewModel!!.selected_coordinate!!.x + 1) * boardInfo.tileSize,
                    boardInfo.topLeftPoint.y + (BOARD_TILES - gameViewModel!!.selected_coordinate!!.y) * boardInfo.tileSize
                )

                canvas.drawRect(rect, boardInfo.selectedSquarePaint)
            }


            gameViewModel?.pieces?.forEach {
                val team_index = (it.team < 0).toInt()

                canvas.drawBitmap(
                    pieceImageMap[team_index][it.name]!!,
                    null,
                    coordinateToRect(it.coordinate),
                    null
                )
            }


            holder.unlockCanvasAndPost(canvas)
        }
    }

    private fun coordinateToRect(coordinate: Coordinate) : Rect {

        val left = boardInfo.topLeftPoint.x + boardInfo.tileSize * coordinate.x
        val top = boardInfo.topLeftPoint.y + boardInfo.tileSize * (boardInfo.tiles - coordinate.y - 1)
        val right = left + boardInfo.tileSize
        val bottom = top + boardInfo.tileSize

        return Rect(left, top, right, bottom)
    }

    private fun touchToCoordinate(x: Int, y: Int) : Coordinate? { // Fix this, make Coordinate be null if outside a board

        return Coordinate((x - boardInfo.topLeftPoint.x) / boardInfo.tileSize , BOARD_TILES - 1 - (y - boardInfo.topLeftPoint.y) / boardInfo.tileSize)
    }


    fun addViewListener(listener: ChessGameViewListener) {
        viewListeners.add(listener)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewModelChange(viewModel: GameViewModel) {
        gameViewModel = viewModel
        drawBoard()
    }

    override fun onStalemate() {
        AlertDialog.Builder(context).setMessage("STALEMATE").setCancelable(true).create().show()
    }

    override fun onCheckmate() {
        AlertDialog.Builder(context).setMessage("CHECKMATE").setCancelable(true).create().show()
    }



    override fun performClick(): Boolean {
        super.performClick()
        return true

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        if (event != null) {
            when (event.action and MotionEvent.ACTION_MASK) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                    val selectedCoordinate = touchToCoordinate(event.x.toInt(), event.y.toInt())

                    if (selectedCoordinate != null) {
                        viewListeners.forEach {
                            it.onCoordinateSelected(selectedCoordinate)
                        }
                    }

                    performClick()
                }
            }
        }

        return true
    }





}