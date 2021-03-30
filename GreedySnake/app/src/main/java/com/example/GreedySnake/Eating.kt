package com.example.GreedySnake


import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Point
import android.os.Handler
import android.os.Message

import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

import java.util.ArrayList
import java.util.Random


class Eating//init
(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var x1 = 0
    private var y1 = 0
    private var x2 = 0
    private var y2 = 0    //touch event to record the postion on view
    private val MoveLength = 100      //when moving distance is more than 100, trun around will be work
    private var time = 300       //handle event will work in the time

    private var panWidth: Int = 0
    private var panHidth: Int = 0
    private var panLineHeight: Float = 0.toFloat()
    private var panLineWidth: Float = 0.toFloat()
    private val rare = 3 * 1.0f / 4

    private val MAX_LINE = 14
    private val MAXLONG_LINE = 25

    private val mSnakeArray = ArrayList<Point>()  //snake list

    private var SnakeHead: Bitmap? = null
    private var SnakeBody: Bitmap? = null
    private var SnakeBody_rirht: Bitmap? = null
    private var SnakeBody_left: Bitmap? = null
    private var SnakeBody_up: Bitmap? = null
    private var SnakeBody_down: Bitmap? = null

    private var Snakefood: Bitmap? = null
    private var Director = DOWN
    private var food: Point? = null


    private var mIsGameover: Boolean = false

    private val paint = Paint()

    private val handler = object : Handler() {

        override fun handleMessage(message: Message) {
            Moving()
        }
    }


    fun SnakeMove() {
        val snakehead = mSnakeArray[mSnakeArray.size - 1]
        var next: Point? = null
        val x = snakehead.x
        val y = snakehead.y
        when (Director) {

            UP -> {
                next = Point(x, (y - 1 + MAXLONG_LINE) % MAXLONG_LINE)
            }
            DOWN -> {
                next = Point(x, (y + 1) % MAXLONG_LINE)
            }
            RIGHT -> {
                next = Point((x + 1) % MAX_LINE, y)
            }
            LEFT -> {
                next = Point((x - 1 + MAX_LINE) % MAX_LINE, y)
            }
        }

        if (mSnakeArray.contains(next)) {
            mIsGameover = true
        }
        if (next != null) {
            mSnakeArray.add(next)
        }
        mSnakeArray.removeAt(0)
        if (mSnakeArray.contains(food)) {
            food?.let { mSnakeArray.add(0, it) }
            creatfood()
            if (time > 50) time -= 5   //speed will higher when eat a food
        }
        invalidate()
    }

    private fun Moving() {
        SnakeMove()
        if (mIsGameover) {
            GameOver()
        } else {
            handler.sendEmptyMessageDelayed(1, time.toLong())
        }
    }


    private fun GameOver() {
        val builder = AlertDialog.Builder(this.context)

        builder.setTitle("GAME OVER")
        builder.setMessage("your size is  " + mSnakeArray.size + " !  do you want start again?")
        builder.setNegativeButton("yes") { dialog, which -> GameAgain() }
        builder.setPositiveButton("exit") { dialog, which -> System.exit(0) }
        val modalDialog = builder.create()
        modalDialog.show()
    }


    init {
        setBackgroundColor(0x22000000)
        init()
        handler.sendEmptyMessageDelayed(1, time.toLong())
    }

    private fun init() {
        paint.color = 0x40000000
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f

        mSnakeArray.clear()
        mSnakeArray.add(Point(MAX_LINE - 2, 1))
        mSnakeArray.add(Point(MAX_LINE - 2, 2))
        mSnakeArray.add(Point(MAX_LINE - 2, 3))

        SnakeBody_rirht = BitmapFactory.decodeResource(resources, R.drawable.shi1)
        SnakeBody_down = BitmapFactory.decodeResource(resources, R.drawable.shi2)
        SnakeBody_up = BitmapFactory.decodeResource(resources, R.drawable.shi3)
        SnakeBody_left = BitmapFactory.decodeResource(resources, R.drawable.shi4)
        SnakeBody = SnakeBody_down
        SnakeHead = SnakeBody_down
        Snakefood = BitmapFactory.decodeResource(resources, R.drawable.food)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        panWidth = w
        panHidth = h
        panLineWidth = panWidth * 1.0f / MAX_LINE
        panLineHeight = panHidth * 1.0f / MAXLONG_LINE
        creatfood()   //when game begining , creat first food

        SnakeBody = Bitmap.createScaledBitmap(SnakeBody!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_left = Bitmap.createScaledBitmap(SnakeBody_left!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_rirht = Bitmap.createScaledBitmap(SnakeBody_rirht!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_up = Bitmap.createScaledBitmap(SnakeBody_up!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_down = Bitmap.createScaledBitmap(SnakeBody_down!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
        Snakefood = Bitmap.createScaledBitmap(Snakefood!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
    }

    //touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mIsGameover) return false
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            x1 = event.x.toInt()
            y1 = event.y.toInt()
        } else if (action == MotionEvent.ACTION_UP) {
            x2 = event.x.toInt()
            y2 = event.y.toInt()

            if (Director == RIGHT || Director == LEFT) {
                if (y2 - y1 >= MoveLength) {
                    Director = DOWN
                    SnakeHead = SnakeBody_down
                    SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
                    SnakeMove()
                } else if (y1 - y2 >= MoveLength) {
                    Director = UP
                    SnakeHead = SnakeBody_up
                    SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
                    SnakeMove()
                }
            } else if (Director == UP || Director == DOWN) {
                if (x2 - x1 >= MoveLength) {
                    Director = RIGHT
                    SnakeHead = SnakeBody_rirht
                    SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
                    SnakeMove()
                } else if (x1 - x2 >= MoveLength) {
                    Director = LEFT
                    SnakeHead = SnakeBody_left
                    SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
                    SnakeMove()
                }
            }
            invalidate()
        }
        return true
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //drawBoard(canvas);
        drawSnake(canvas)
    }


    fun GameAgain() {
        mIsGameover = false
        mSnakeArray.clear()
        mSnakeArray.add(Point(MAX_LINE - 2, 1))
        mSnakeArray.add(Point(MAX_LINE - 2, 2))
        mSnakeArray.add(Point(MAX_LINE - 2, 3))
        Director = DOWN
        time = 300
        SnakeHead = SnakeBody_down
        SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
        handler.sendEmptyMessageDelayed(1, time.toLong())
        invalidate()
        Toast.makeText(context, "start game again", Toast.LENGTH_SHORT).show()
    }


    private fun drawBoard(canvas: Canvas) {
        val w = panWidth
        val h = panHidth
        val lineheight = panLineHeight
        val linewidth = panLineWidth
        for (i in 0 until MAXLONG_LINE) {
            val startx = 0
            val y = (i * lineheight).toInt()
            canvas.drawLine(startx.toFloat(), y.toFloat(), w.toFloat(), y.toFloat(), paint)
        }
        for (i in 0 until MAXLONG_LINE) {
            val starty = 0
            val y = (i * linewidth).toInt()
            canvas.drawLine(y.toFloat(), starty.toFloat(), y.toFloat(), h.toFloat(), paint)
        }
    }


    private fun drawSnake(canvas: Canvas) {
        canvas.drawBitmap(Snakefood!!, food!!.x * panLineWidth + (1 - rare) / 2 * panLineWidth, food!!.y * panLineHeight + (1 - rare) / 2 * panLineWidth, null)
        var i = 0
        val n = mSnakeArray.size
        while (i < n - 1) {
            val snakepoint = mSnakeArray[i]
            val next = mSnakeArray[i + 1]
            if (snakepoint.x == next.x) {
                if ((snakepoint.y + 1) % MAXLONG_LINE == next.y)
                    SnakeBody = SnakeBody_down
                else
                    SnakeBody = SnakeBody_up
            } else if (snakepoint.y == next.y) {
                if ((snakepoint.x + 1) % MAX_LINE == next.x)
                    SnakeBody = SnakeBody_rirht
                else
                    SnakeBody = SnakeBody_left
            }
            canvas.drawBitmap(SnakeBody!!, snakepoint.x * panLineWidth + (1 - rare) / 2 * panLineWidth, snakepoint.y * panLineHeight + (1 - rare) / 2 * panLineHeight, null)
            i++
        }
        val snakepoint = mSnakeArray[mSnakeArray.size - 1]
        canvas.drawBitmap(SnakeHead!!, snakepoint.x * panLineWidth, snakepoint.y * panLineHeight, null)
    }


    private fun creatfood() {
        food = Point(Random().nextInt(MAX_LINE), Random().nextInt(MAXLONG_LINE))
        while (mSnakeArray.contains(food!!)) {
            food = Point(Random().nextInt(MAX_LINE), Random().nextInt(MAXLONG_LINE))
        }
    }

    companion object {

        private val UP = 2
        private val DOWN = 3
        private val LEFT = 1
        private val RIGHT = 0
    }

}


