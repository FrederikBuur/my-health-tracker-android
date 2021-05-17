package com.fbuur.myhealthtracker.pages.data.compare

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import com.fbuur.myhealthtracker.R
import com.fbuur.myhealthtracker.pages.data.DataViewModel
import com.fbuur.myhealthtracker.util.dpToPx
import com.fbuur.myhealthtracker.util.spToPx

class CompareGraphView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    var compareGraphData: CompareGraphData = CompareGraphData(
        scope = DataViewModel.DataScope.WEEK,
        graphs = Pair(
            first = CompareGraphEntity(0L, 0, ""),
            second = CompareGraphEntity(0L, 0, "")
        )
    )
        set(value) {
            field = value
            reset()
        }

    // paints
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)

    // points
    private var pointBotL = PointF(0f, 0f)
    private var pointBotR = PointF(0f, 0f)
    private var pointTopL = PointF(0f, 0f)
    private var pointTopR = PointF(0f, 0f)
    private var graphHeight = 0f
    private var graphWidth = 0f


    init {
        setupPaint()
    }

    private fun setupPaint() {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 4.dpToPx
        paint.color = ContextCompat.getColor(context, R.color.mth_black)

        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 7.dpToPx
        textPaint.textSize = 12.spToPx
        textPaint.textAlign = Paint.Align.CENTER

        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {

        canvas?.let {

            drawBaseElements(it)
            drawGraphs(it)

        }

    }

    private fun drawBaseElements(canvas: Canvas) {

        val margin = 24.dpToPx

        // set axis points
        pointBotL = PointF(margin, measuredHeight - margin)
        pointBotR = PointF(measuredWidth - margin, measuredHeight - margin)
        pointTopL = PointF(margin, margin)
        pointTopR = PointF(measuredWidth - margin, margin)
        graphHeight = pointBotL.y - pointTopL.y
        graphWidth = pointBotR.x - pointBotL.x

        // draw axis lines
        paint.strokeCap = Paint.Cap.ROUND
        canvas.drawLine(pointTopL.x, pointTopL.y, pointBotL.x, pointBotL.y, paint)
        canvas.drawLine(pointBotL.x, pointBotL.y, pointBotR.x, pointBotR.y, paint)
        canvas.drawLine(pointBotR.x, pointBotR.y, pointTopR.x, pointTopR.y, paint)

        // draw x-axis values
        when (this.compareGraphData.scope) {
            DataViewModel.DataScope.DAY -> {
                drawXAxisValues(
                    canvas = canvas,
                    xAxisValues = listOf(
                        "1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                        "11", "12", "13", "14", "15", "16", "17", "18", "19",
                        "20", "21", "22", "23", "24"
                    )
                )
            }
            DataViewModel.DataScope.WEEK -> {
                drawXAxisValues(
                    canvas = canvas,
                    xAxisValues = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                )
            }
            else -> throw Exception("Not supported scope")
        }

    }

    private fun drawXAxisValues(canvas: Canvas, xAxisValues: List<String>) {
        val itemWidth = this.graphWidth / xAxisValues.size
        val yPos = pointBotL.y + 16.dpToPx

        for (i in xAxisValues.indices) {
            val xPos = pointBotL.x + itemWidth * i + itemWidth / 2
            canvas.drawText(xAxisValues[i], xPos, yPos, textPaint)
        }
    }

    private fun drawGraphs(canvas: Canvas) {

        // draw primary graph
        // todo

        // draw secondary graph
        // todo

    }

    private fun reset() {
        this.invalidate()
    }

}

class CompareGraphData(
    val scope: DataViewModel.DataScope,
    val graphs: Pair<CompareGraphEntity, CompareGraphEntity>
)

class CompareGraphEntity(
    val temId: Long,
    val color: Int,
    val valueOfInterestTitle: String,

    )