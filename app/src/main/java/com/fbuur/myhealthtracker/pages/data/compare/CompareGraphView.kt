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
            first = CompareGraphEntity(
                0L,
                ContextCompat.getColor(context, R.color.test1),
                "test1",
                listOf(200, 350, 143, 160, 420)
            ),
            second = CompareGraphEntity(
                0L,
                ContextCompat.getColor(context, R.color.test2),
                "test2",
                listOf(3, 2, 8, 4, 9, 1, 6, 5, 10, 1)
            )
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

    private val lineMargin = 24.dpToPx

    private val yAxisTitleCount = 5


    init {
        setupPaint()
    }

    private fun setupPaint() {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.dpToPx
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
            drawYAxisValues(it)
            drawGraph(it, this.compareGraphData.graphs.first)
            drawGraph(it, this.compareGraphData.graphs.second)
        }
    }

    private fun drawBaseElements(canvas: Canvas) {

        // set axis points
        pointBotL = PointF(lineMargin, measuredHeight - lineMargin)
        pointBotR = PointF(measuredWidth - lineMargin, measuredHeight - lineMargin)
        pointTopL = PointF(lineMargin, lineMargin)
        pointTopR = PointF(measuredWidth - lineMargin, lineMargin)
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

        textPaint.color = ContextCompat.getColor(context, R.color.mth_text_primary_onwhite)

        for (i in xAxisValues.indices) {
            val xPos = pointBotL.x + itemWidth * i + itemWidth / 2
            canvas.drawText(xAxisValues[i], xPos, yPos, textPaint)
        }
    }

    private fun drawYAxisValues(canvas: Canvas) {
        val itemHeight = this.graphHeight / yAxisTitleCount
        val xPosPrimary = lineMargin / 2
        val xPosSecondary = measuredWidth - lineMargin / 2

        val maxPrimary =
            this.compareGraphData.graphs.first.dataPoints.maxByOrNull { it ?: 0 } ?: 0
        val minPrimary =
            this.compareGraphData.graphs.first.dataPoints.minByOrNull { it ?: 0 } ?: 0
        val maxSecondary =
            this.compareGraphData.graphs.second.dataPoints.maxByOrNull { it ?: 0 } ?: 0
        val minSecondary =
            this.compareGraphData.graphs.second.dataPoints.minByOrNull { it ?: 0 } ?: 0

        val rangeHopPrimary = (maxPrimary - minPrimary) / (yAxisTitleCount - 1)
        val rangeHopSecondary = (maxSecondary - minSecondary) / (yAxisTitleCount - 1)

        for (i in 1..yAxisTitleCount) {

            val yPos = pointTopL.y + itemHeight * i - itemHeight / 2 + textPaint.textSize / 4

            // draw primary text
            textPaint.color = this.compareGraphData.graphs.first.color
            canvas.drawText(
                "${minPrimary + rangeHopPrimary * (yAxisTitleCount - i)}",
                xPosPrimary,
                yPos,
                textPaint
            )

            // draw secondary text
            textPaint.color = this.compareGraphData.graphs.second.color
            canvas.drawText(
                "${minSecondary + rangeHopSecondary * (yAxisTitleCount - i)}",
                xPosSecondary,
                yPos,
                textPaint
            )

        }

        // draw y-axis value of interest titles
        with(this.compareGraphData.graphs.first) {
            textPaint.color = color
            canvas.drawText(
                valueOfInterestTitle,
                pointTopL.x,
                lineMargin / 2 + textPaint.textSize / 2,
                textPaint
            )
        }
        with(this.compareGraphData.graphs.second) {
            textPaint.color = color
            canvas.drawText(
                valueOfInterestTitle,
                pointTopR.x,
                lineMargin / 2 + textPaint.textSize / 2,
                textPaint
            )
        }

    }

    private fun drawGraph(canvas: Canvas, compareGraphEntity: CompareGraphEntity) {

        val itemHeight = this.graphHeight / yAxisTitleCount
        val xPosPrimary = lineMargin / 2
        val xPosSecondary = measuredWidth - lineMargin / 2

        val maxPrimary =
            this.compareGraphData.graphs.first.dataPoints.maxByOrNull { it ?: 0 } ?: 0
        val minPrimary =
            this.compareGraphData.graphs.first.dataPoints.minByOrNull { it ?: 0 } ?: 0
        val maxSecondary =
            this.compareGraphData.graphs.second.dataPoints.maxByOrNull { it ?: 0 } ?: 0
        val minSecondary =
            this.compareGraphData.graphs.second.dataPoints.minByOrNull { it ?: 0 } ?: 0


        // draw graph
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
    val dataPoints: List<Int?>
)