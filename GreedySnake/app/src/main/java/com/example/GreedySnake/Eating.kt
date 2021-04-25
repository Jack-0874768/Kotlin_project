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
    //touch event to record the position on view
    private var x1 = 0
    private var y1 = 0
    private var x2 = 0
    private var y2 = 0

    //when moving distance is more than 100, trun around will be work
    private val MoveLength = 100

    //The cycle of the handle event can also be understood as the speed of the snake's movement
    private var time = 300

    //The width and height of the game area
    private var panWidth: Int = 0
    private var panHidth: Int = 0

    //The height and width of the grid
    private var panLineHeight: Float = 0.toFloat()
    private var panLineWidth: Float = 0.toFloat()

    //Bitmap size and grid size ratio
    private val rare = 3 * 1.0f / 4

    //Number of horizontal grids
    private val MAX_LINE = 14
    //Number of vertical grids
    private val MAXLONG_LINE = 25

    private val mSnakeArray = ArrayList<Point>()  //snake list

    private var SnakeHead: Bitmap? = null
    private var SnakeBody: Bitmap? = null

    //The four orientations of the snake
    private var SnakeBody_right: Bitmap? = null
    private var SnakeBody_left: Bitmap? = null
    private var SnakeBody_up: Bitmap? = null
    private var SnakeBody_down: Bitmap? = null

    private var Snakefood: Bitmap? = null //Pictures of food
    private var Director = DOWN  //The direction the snake head is heading
    private var food: Point? = null  //Food coordinates

    //Judge whether the game is over
    private var mIsGameover: Boolean = false

    private val paint = Paint() //paint

    //Refresh the interface
    private val handler = object : Handler() {

        override fun handleMessage(message: Message) {
            Moving()
        }
    }

//create snake move
    fun SnakeMove() {
        val snakehead = mSnakeArray[mSnakeArray.size - 1]
        var next: Point? = null
        val x = snakehead.x     //Two-dimensional plan coordinate
        val y = snakehead.y
        when (Director) {

            UP -> {
                next = Point(x, (y - 1 + MAXLONG_LINE) % MAXLONG_LINE)  //Get the upward position
            }
            DOWN -> {
                next = Point(x, (y + 1) % MAXLONG_LINE)   //Get down position
            }
            RIGHT -> {
                next = Point((x + 1) % MAX_LINE, y)   //Get the position to the right
            }
            LEFT -> {
                next = Point((x - 1 + MAX_LINE) % MAX_LINE, y)   ///Get the position to the left
            }
        }

        //The state has not changed and the game is over
        if (mSnakeArray.contains(next)) {
            mIsGameover = true
        }

        //Add the location obtained by next
        if (next != null) {
            mSnakeArray.add(next)
        }
        mSnakeArray.removeAt(0)

        //eat a food
    if (mSnakeArray.contains(food))
        {
            food?.let { mSnakeArray.add(0, it) }
            creatfood()
            if (time > 50) time -= 5   //speed will higher when eat a food
        }
        invalidate()
    }

    //Judge whether the game is over, if it is not over, let the snake move automatically
    private fun Moving() {
        SnakeMove()

        if (mIsGameover) {
            GameOver()
        } else {
            handler.sendEmptyMessageDelayed(1, time.toLong())  //Through the Handle cycle, let the snake move automatically
        }
    }

//create after the game is over
    private fun GameOver() {
        val builder = AlertDialog.Builder(this.context)

    //Generate a dialog box to let the user choose to exit the game or start another round
        builder.setTitle(resources.getString(R.string.G_O))
        builder.setMessage(resources.getString(R.string.Y_s_i) + mSnakeArray.size + resources.getString(R.string.d_y_W))
        builder.setNegativeButton(resources.getString(R.string.yes)) { dialog, which -> GameAgain() }
        builder.setPositiveButton(resources.getString(R.string.exit)) { dialog, which -> System.exit(0) }
        val modalDialog = builder.create()
        modalDialog.show()
    }

    //initialization
    init {
        setBackgroundColor(0x22000000)
        init()
        handler.sendEmptyMessageDelayed(1, time.toLong())
    }

    private fun init() {
       //customize paint
        paint.color = 0x40000000
        paint.isAntiAlias = true
        paint.isDither = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2f

        //initial snake length
        mSnakeArray.clear()
        mSnakeArray.add(Point(MAX_LINE - 2, 1))
        mSnakeArray.add(Point(MAX_LINE - 2, 2))
        mSnakeArray.add(Point(MAX_LINE - 2, 3))

        //initial picture
        SnakeBody_right = BitmapFactory.decodeResource(resources, R.drawable.shi1)
        SnakeBody_down = BitmapFactory.decodeResource(resources, R.drawable.shi2)
        SnakeBody_up = BitmapFactory.decodeResource(resources, R.drawable.shi3)
        SnakeBody_left = BitmapFactory.decodeResource(resources, R.drawable.shi4)
        SnakeBody = SnakeBody_down
        SnakeHead = SnakeBody_down
        Snakefood = BitmapFactory.decodeResource(resources, R.drawable.food)
    }

    //Set to full screen
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        setMeasuredDimension(widthSize, heightSize)

    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //Set the width of the board and grid
        panWidth = w
        panHidth = h
        panLineWidth = panWidth * 1.0f / MAX_LINE
        panLineHeight = panHidth * 1.0f / MAXLONG_LINE
        creatfood()   //when game starts, produce first food

        //Set the size of Bitmap
        SnakeBody = Bitmap.createScaledBitmap(SnakeBody!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_left = Bitmap.createScaledBitmap(SnakeBody_left!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_right = Bitmap.createScaledBitmap(SnakeBody_right!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_up = Bitmap.createScaledBitmap(SnakeBody_up!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeBody_down = Bitmap.createScaledBitmap(SnakeBody_down!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
        SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
        Snakefood = Bitmap.createScaledBitmap(Snakefood!!, (panLineWidth * rare * 1.0f).toInt(), (panLineHeight * rare * 1.0f).toInt(), false)
    }

    //touch event
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (mIsGameover) return false  //Judge whether it is over
        val action = event.action
        //Get coordinates from swipe the screen
        if (action == MotionEvent.ACTION_DOWN) {
            x1 = event.x.toInt()
            y1 = event.y.toInt()
        }
        //Finger let go of the screen
        else if (action == MotionEvent.ACTION_UP) {
            x2 = event.x.toInt()
            y2 = event.y.toInt()

            //After judging the direction, adjust the direction of the snake and then move
            if (Director == RIGHT || Director == LEFT) {
                if (y2 - y1 >= MoveLength) //down
                {
                    Director = DOWN
                    SnakeHead = SnakeBody_down
                    SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
                    SnakeMove()
                }
                else if (y1 - y2 >= MoveLength) //up
                {
                    Director = UP
                    SnakeHead = SnakeBody_up
                    SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
                    SnakeMove()
                }
            } else if (Director == UP || Director == DOWN) {
                if (x2 - x1 >= MoveLength) //right
                {
                    Director = RIGHT
                    SnakeHead = SnakeBody_right
                    SnakeHead = Bitmap.createScaledBitmap(SnakeHead!!, panLineWidth.toInt(), panLineHeight.toInt(), false)
                    SnakeMove()
                } else if (x1 - x2 >= MoveLength) //left
                {
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

   //Drawing
    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSnake(canvas)
    }

   // game again
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
        Toast.makeText(context, resources.getString(R.string.Again), Toast.LENGTH_SHORT).show()
    }


    private fun drawSnake(canvas: Canvas) {
       //draw food
        canvas.drawBitmap(Snakefood!!, food!!.x * panLineWidth + (1 - rare) / 2 * panLineWidth, food!!.y * panLineHeight + (1 - rare) / 2 * panLineWidth, null)

        //Draw a snake body
        var i = 0
        val n = mSnakeArray.size
        while (i < n - 1) {
            val snakepoint = mSnakeArray[i]
            val next = mSnakeArray[i + 1]
          //Up or down
            if (snakepoint.x == next.x) {
                if ((snakepoint.y + 1) % MAXLONG_LINE == next.y)
                    SnakeBody = SnakeBody_down
                else
                    SnakeBody = SnakeBody_up
            }
            //To the right or to the left
            else if (snakepoint.y == next.y) {
                if ((snakepoint.x + 1) % MAX_LINE == next.x)
                    SnakeBody = SnakeBody_right
                else
                    SnakeBody = SnakeBody_left
            }
            canvas.drawBitmap(SnakeBody!!, snakepoint.x * panLineWidth + (1 - rare) / 2 * panLineWidth, snakepoint.y * panLineHeight + (1 - rare) / 2 * panLineHeight, null)
            i++
        }

        //Draw a snake head
        val snakepoint = mSnakeArray[mSnakeArray.size - 1]
        canvas.drawBitmap(SnakeHead!!, snakepoint.x * panLineWidth, snakepoint.y * panLineHeight, null)
    }


    private fun creatfood() {
        //Randomly generate a food
        food = Point(Random().nextInt(MAX_LINE), Random().nextInt(MAXLONG_LINE))

        //Not on the snake
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


