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
import java.math.RoundingMode
import java.text.DecimalFormat

class CompareGraphView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val emptyGraphData = CompareGraphData(
        scope = DataViewModel.DataScope.WEEK,
        graphs = Pair(
            first = CompareGraphEntity(
                0L,
                ContextCompat.getColor(context, android.R.color.transparent),
                "",
                emptyList()
            ),
            second = CompareGraphEntity(
                0L,
                ContextCompat.getColor(context, android.R.color.transparent),
                "",
                emptyList()
            )
        )
    )

    var compareGraphData: CompareGraphData = emptyGraphData
        set(value) {
            field = value
            this.invalidate()
        }

    // paints
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)
    private val tempPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG)

    // points
    private var pointBotL = PointF(0f, 0f)
    private var pointBotR = PointF(0f, 0f)
    private var pointTopL = PointF(0f, 0f)
    private var pointTopR = PointF(0f, 0f)
    private var graphHeight = 0f
    private var graphWidth = 0f

    private val lineMargin = 28.dpToPx

    private val yAxisLabelCount = 6
    private var xAxisLabelCount = 0


    init {
        setupPaint()
    }

    private fun setupPaint() {

        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 2.dpToPx
        paint.color = ContextCompat.getColor(context, R.color.mth_black)

        tempPaint.style = Paint.Style.FILL

        textPaint.style = Paint.Style.FILL
        textPaint.strokeWidth = 7.dpToPx
        textPaint.textSize = 10.spToPx
        textPaint.textAlign = Paint.Align.CENTER

        setWillNotDraw(false)
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.let {
            drawBaseElements(it)
            drawYAxisLabels(it)
            this.compareGraphData.graphs.first?.let { graphData ->
                drawGraph(it, graphData)
            }
            this.compareGraphData.graphs.second?.let { graphData ->
                drawGraph(it, graphData)
            }
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
        paint.color = ContextCompat.getColor(context, R.color.mth_black)
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
        xAxisLabelCount = xAxisValues.size
        val itemWidth = this.graphWidth / xAxisValues.size
        val yPos = pointBotL.y + 16.dpToPx

        textPaint.color = ContextCompat.getColor(context, R.color.mth_text_primary_onwhite)

        for (i in xAxisValues.indices) {
            val xPos = pointBotL.x + itemWidth * i + itemWidth / 2
            canvas.drawText(xAxisValues[i], xPos, yPos, textPaint)
        }
    }

    private fun drawYAxisLabels(canvas: Canvas) {
        val itemHeight = this.graphHeight / (yAxisLabelCount + 1)
        val xPosPrimary = lineMargin / 2
        val xPosSecondary = measuredWidth - lineMargin / 2

        val (maxPrimary, minPrimary) = getMinAndMaxByList(this.compareGraphData.graphs.first)
        val (maxSecondary, minSecondary) = getMinAndMaxByList(this.compareGraphData.graphs.second)

        val rangeHopPrimary = (maxPrimary - minPrimary).toFloat() / yAxisLabelCount
        val rangeHopSecondary = (maxSecondary - minSecondary).toFloat() / yAxisLabelCount

        for (i in 0..yAxisLabelCount) {
            val yPos =
                pointTopL.y + itemHeight * (yAxisLabelCount - i) + itemHeight / 2
            val formatter = DecimalFormat("#.#").apply {
                roundingMode = RoundingMode.FLOOR
            }
            val labelPrimary = formatter.format(minPrimary + rangeHopPrimary * i)
                .replace(',', '.')
            val labelSecondary = formatter.format(minSecondary + rangeHopSecondary * i)
                .replace(',', '.')

            // draw primary text
            this.compareGraphData.graphs.first?.let { graph ->
                textPaint.color = graph.color
                canvas.drawText(
                    labelPrimary,
                    xPosPrimary,
                    yPos,
                    textPaint
                )
            }
            // draw secondary text
            this.compareGraphData.graphs.second?.let { graph ->
                textPaint.color = graph.color
                canvas.drawText(
                    labelSecondary,
                    xPosSecondary,
                    yPos,
                    textPaint
                )
            }
        }

        // draw y-axis value of interest titles
        this.compareGraphData.graphs.first?.let { graph ->
            textPaint.color = graph.color
            textPaint.color = graph.color
            canvas.drawText(
                graph.valueOfInterestTitle,
                pointTopL.x,
                lineMargin / 2 + textPaint.textSize / 2,
                textPaint
            )
        }
        this.compareGraphData.graphs.second?.let { graph ->
            textPaint.color = graph.color
            canvas.drawText(
                graph.valueOfInterestTitle,
                pointTopR.x,
                lineMargin / 2 + textPaint.textSize / 2,
                textPaint
            )
        }

    }

    private fun drawGraph(canvas: Canvas, compareGraphEntity: CompareGraphEntity) {

        val itemHeight = this.graphHeight / (yAxisLabelCount + 1)
        val itemWidth = this.graphWidth / xAxisLabelCount

        val (maxValue, minValue) = getMinAndMaxByList(compareGraphEntity)

        paint.color = compareGraphEntity.color
        tempPaint.color = compareGraphEntity.color

        val points = arrayListOf<PointF>()

        // calc and set points
        compareGraphEntity.dataPoints.forEachIndexed { i, value ->
            value?.let {
                val xPos = pointBotL.x + itemWidth / 2 + itemWidth * i

                val temp1 = maxValue - minValue
                val temp2 = value - minValue
                val percentOfMaxHeight = temp2.toFloat() / temp1.toFloat()
                val yPos =
                    pointBotL.y - (graphHeight - itemHeight) * percentOfMaxHeight - itemHeight / 2 - textPaint.textSize / 3

                points.add(PointF(xPos, yPos))
                canvas.drawCircle(xPos, yPos, 4.dpToPx, tempPaint)
            }
        }

        // make lines between points
        points.forEachIndexed { i, p1 ->
            points.getOrNull(i + 1)?.let { p2 ->
                canvas.drawLine(p1.x, p1.y, p2.x, p2.y, paint)
            }
        }

    }

    private fun getMinAndMaxByList(compareGraphEntity: CompareGraphEntity?): Pair<Int, Int> {
        var maxValue = Int.MIN_VALUE
        compareGraphEntity?.dataPoints?.forEach { num ->
            num?.let {
                if (it > maxValue) maxValue = it
            }
        }
        var minValue = Int.MAX_VALUE
        compareGraphEntity?.dataPoints?.forEach { num ->
            num?.let {
                if (it < minValue) minValue = it
            }
        }
        if (minValue == Int.MAX_VALUE && maxValue == Int.MIN_VALUE) {
            maxValue = 2
            minValue = 1
        } else if (minValue == maxValue) {
            maxValue = minValue * 2
        }
        return Pair(maxValue, minValue)
    }

    fun changeScope(scope: DataViewModel.DataScope) {
        this.compareGraphData = this.compareGraphData.copy(scope = scope)
    }

}

data class CompareGraphData(
    val scope: DataViewModel.DataScope,
    val graphs: Pair<CompareGraphEntity?, CompareGraphEntity?>
)

data class CompareGraphEntity(
    val temId: Long,
    val color: Int,
    val valueOfInterestTitle: String,
    val dataPoints: List<Int?>
)