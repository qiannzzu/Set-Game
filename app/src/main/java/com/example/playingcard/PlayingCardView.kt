package com.example.playingcard

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

/**
 * TODO: document your custom view class.
 */
class PlayingCardView : View {
    companion object ValidCards{
        val shapes = listOf("Diamond","Squiggle","Oval")
        val ranks = listOf("One","Two","Three")
        val shading = listOf("Solid","Striped","Open")
        val colors = listOf("Red","Green","Purple")
        val CARD_STD_HEIGHT = 240.00f
        val CORNER_RADIUS = 12.0f
    }

    private  val cornerScaleFactor: Float
        get() {return height / CARD_STD_HEIGHT}

    private val cornerRadius: Float
        get() { return  CORNER_RADIUS * cornerScaleFactor}

    var shape: String? = null
        set(value) {
            if (value in shapes){
                field = value
                invalidate()
                //Don't call onDraw()
            }
        }

    var rank:String? = null
        set(value) {
            if(value in ranks){
                field = value
                invalidate()
            }
        }

    var shade:String? = null
        set(value) {
            if(value in shading){
                field = value
                invalidate()
            }
        }

    var colorX:String? = null
        set(value) {
            if(value in colors){
                field = value
                invalidate()
            }
        }

    var choose = false
        set(value){
            field = value
            invalidate()
        }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    private val mPaint = Paint() //for drawing border and face

    private fun init(attrs: AttributeSet?) {
        // Load attributes
        val a = context.obtainStyledAttributes(attrs, R.styleable.PlayingCardView)

        rank = a.getString(R.styleable.PlayingCardView_rank)
        shape = a.getString(R.styleable.PlayingCardView_shape)
        shade = a.getString(R.styleable.PlayingCardView_shade)
        colorX = a.getString(R.styleable.PlayingCardView_cardColor)

        mPaint.isAntiAlias = true

        a.recycle()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val rect = setBackGroundCard(canvas)

        // Set up the paint style based on the shade value
        val patternPaint = Paint().apply {
            style = when (shade) {
                "Solid","Striped" -> Paint.Style.FILL
                "Open" -> Paint.Style.STROKE
                else -> Paint.Style.FILL
            }
            isAntiAlias = true
        }

        // Determine the number of shapes to draw based on the rank
        val numShapesToDraw = when (rank) {
            "One" -> 1
            "Two" -> 2
            "Three" -> 3
            else -> 0
        }

        // Set the solid color pattern
        patternPaint.color = when (colorX) {
            "Red" -> Color.parseColor("#EB6C93") // Pink
            "Green" -> Color.parseColor("#69CB5C") // Green
            "Purple" -> Color.parseColor("#D9B8F1") // Purple
            else -> Color.parseColor("#AB817B") // Rose, Default color if "color" has an unknown value
        }
        // Get the shape path and its dimensions
        val shapePathData = getShapePath(shape.toString())
        val shapePath = shapePathData.first
        val shapeWidth = shapePathData.second
        val shapeHeight = shapePathData.third

        val patternSpacing = 10f

        val (startX, startY) = setStartXY(rect, shapeHeight, patternSpacing, shapeWidth, numShapesToDraw)

        // Draw the pattern based on the shade value
        for (i in 0 until numShapesToDraw) {
            canvas.save()
            canvas.translate(startX, startY + i * (shapeHeight + patternSpacing))

            when (shade) {
                "Solid" -> canvas.drawPath(shapePath, patternPaint)
                "Open" -> {
                    patternPaint.strokeWidth = 7.0f
                    canvas.drawPath(shapePath, patternPaint)
                }
                "Striped" -> drawStripedPattern(canvas, shapePath, patternPaint)
            }

            canvas.restore()
        }

    }

    private fun setStartXY(
        rect: RectF,
        shapeHeight: Float,
        patternSpacing: Float,
        shapeWidth: Float,
        numShapesToDraw: Int
    ): Pair<Float, Float> {
        val centerX = rect.centerX()
        val centerY = rect.centerY()

        // Calculate the number of shapes that can fit in the card's height
        val numShapesHeight = (rect.height() / (shapeHeight + patternSpacing)).toInt()

        // Calculate the total height occupied by the shapes
        val totalShapesHeight = numShapesHeight * (shapeHeight + patternSpacing) - patternSpacing

        // Calculate the starting position of the pattern to align it at the center
        val startX = centerX - shapeWidth / 2
        val startY =
            centerY - totalShapesHeight / 2 + (totalShapesHeight - numShapesToDraw * (shapeHeight + patternSpacing)) / 2
        return Pair(startX, startY)
    }

    private fun setBackGroundCard(canvas: Canvas): RectF {
        val path = Path()
        val rect = RectF(0f, 0f, width.toFloat(), height.toFloat())
        path.addRoundRect(rect, cornerRadius, cornerRadius, Path.Direction.CW)
        canvas.clipPath(path)

        // Draw card background
        mPaint.style = Paint.Style.FILL
        mPaint.color = Color.WHITE
        canvas.drawPath(path, mPaint)

        mPaint.style = Paint.Style.STROKE

        if (choose) {
            mPaint.strokeWidth = 20.0f
            mPaint.color = Color.RED
        } else {
            mPaint.strokeWidth = 3.0f
            mPaint.color = Color.BLACK
        }
        canvas.drawPath(path, mPaint)
        return rect
    }

    private fun drawStripedPattern(canvas: Canvas, shapePath: Path, patternPaint: Paint) {
        val bounds = RectF()
        shapePath.computeBounds(bounds, true)

        val stripeSpacing = 15f // Adjust the spacing between stripes
        val stripePaint = Paint().apply {
            style = Paint.Style.FILL_AND_STROKE
            color = patternPaint.color
            strokeWidth = 10f // Set the stroke width for the stripes
            isAntiAlias = true
        }

        val stripePath = Path()

        var x = bounds.left
        while (x < bounds.right) {
            stripePath.moveTo(x, bounds.top)
            stripePath.lineTo(x, bounds.bottom)
            x += stripeSpacing
        }

        // Draw the frame line
        val frameLinePaint = Paint().apply {
            style = Paint.Style.STROKE
            color = patternPaint.color
            strokeWidth = 5f // Set the stroke width for the frame line
            isAntiAlias = true
        }
        canvas.drawPath(shapePath, frameLinePaint)

        // Fill the shape with stripes
        canvas.save()
        canvas.clipPath(shapePath) // Clip the canvas to the shape path
        canvas.drawPath(stripePath, stripePaint) // Draw the stripes
        canvas.restore()
    }

    private fun getShapePath(shape: String): Triple<Path, Float, Float> {
        val path = Path()
        val bounds = RectF()

        when (shape) {
            "Diamond" -> {
                // Define the diamond shape path
                path.moveTo(0f, 0f)
                path.lineTo(30f, 30f)
                path.lineTo(60f, 0f)
                path.lineTo(30f, -30f)
                path.close()
            }
            "Squiggle" -> {
                // Define the squiggle shape path
                path.moveTo(104f, 15f)
                path.cubicTo(112.4f, 36.9f, 89.7f, 60.8f, 63f, 54f)
                path.cubicTo(52.3f, 51.3f, 42.2f, 42f, 27f, 53f)
                path.cubicTo(9.6f, 65.6f, 5.4f, 58.3f, 5f, 40f)
                path.cubicTo(4.6f, 22f, 19.1f, 9.7f, 36f, 12f)
                path.cubicTo(59.2f, 15.2f, 61.9f, 31.5f, 89f, 14f)
                path.cubicTo(95.3f, 10f, 100.9f, 6.9f, 104f, 15f)
                path.close()
            }
            "Oval" -> {
                // Define the oval shape path
                val ovalRect = RectF(0f, -20f, 100f, 20f) // Adjust the width to elongate the oval
                val cornerRadius = 20f // Adjust the corner radius to make the ends more rounded
                path.addRoundRect(ovalRect, cornerRadius, cornerRadius, Path.Direction.CW)
            }
            else -> {
                path.reset()
            }
        }

        // Calculate the bounds of the shape path
        path.computeBounds(bounds, true)

        val width = bounds.width()
        val height = bounds.height()

        return Triple(path, width, height)
    }

}