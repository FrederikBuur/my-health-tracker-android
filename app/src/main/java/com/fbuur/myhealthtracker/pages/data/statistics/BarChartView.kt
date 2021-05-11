package com.fbuur.myhealthtracker.pages.data.statistics

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.children
import com.fbuur.myhealthtracker.databinding.ViewBarChartBinding
import com.fbuur.myhealthtracker.util.dpToPx

class BarChartView : FrameLayout {

    private var _binding: ViewBarChartBinding? = null
    private val binding get() = _binding!!

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    class BarChart(
        val barGroups: List<List<BarGroupEntity>>,
        val xAxisTitles: List<String>,
        val yAxisTitles: List<Int>
    )

    class BarGroupEntity(
        val temId: Long,
        val color: Int,
        val value: Int
    )

    var barChartData: BarChart = BarChart(emptyList(), emptyList(), emptyList())
        set(value) {
            field = value
            setupUI(value)
        }

    init {
        _binding = ViewBarChartBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun setupUI(chart: BarChart) {

        // setup x axis titles
        setupXAxisTitles(chart.xAxisTitles)

        // setup y axis titles
        setupYAxisTitles(chart.yAxisTitles)

        // for each bar chart group set up each group
        binding.barGroupsContainer.removeAllViews()
        chart.barGroups.forEachIndexed { i, item ->
            setupBarChartGroups(item, i)
        }

    }

    private fun setupXAxisTitles(titles: List<String>) {
        binding.xAxisTitles.removeAllViews()
        titles.forEach {

            val tv = TextView(context).apply {
                text = it
                textAlignment = TEXT_ALIGNMENT_CENTER
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // width
                    ViewGroup.LayoutParams.WRAP_CONTENT, // height
                    1f // wight
                )
            }

            binding.xAxisTitles.addView(tv)
        }
    }

    private fun setupYAxisTitles(titles: List<Int>) {
        binding.yAxisTitles.removeAllViews()
        titles.forEach {
            binding.yAxisTitles.addView(
                TextView(context).apply {
                    text = it.toString()
                    gravity = Gravity.CENTER
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, // width
                        ViewGroup.LayoutParams.WRAP_CONTENT, // height
                        1f // wight
                    )
                }
            )
        }
    }

    private fun setupBarChartGroups(barGroups: List<BarGroupEntity>, i: Int) {

        i
        val maxWidth = binding.xAxisTitles.measuredWidth.toFloat()
        val itemWidth = maxWidth / binding.xAxisTitles.childCount
        itemWidth

        // setup bar group container
        val barGroup = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, // width
                ViewGroup.LayoutParams.MATCH_PARENT, // height
                1.0f // weight
            )
        }

        // populate bar group container
        barGroups.forEach { barGroupEntity ->
            val maxHeight = binding.yAxisTitles.measuredHeight.toFloat()
            val itemHeight = maxHeight / binding.yAxisTitles.childCount

            val max = this.barChartData.yAxisTitles.maxOf { it }
            val heightPercentage: Float = barGroupEntity.value.toFloat() / max.toFloat()
            val actualHeight = maxHeight * heightPercentage - itemHeight.toFloat() / 2

            val bar = View(context).apply {
                setBackgroundColor(barGroupEntity.color)
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // width
                    ViewGroup.LayoutParams.MATCH_PARENT // height
                ).apply {
                    width = 8.dpToPx.toInt()
                    height = actualHeight.toInt()
                }
            }
            // add bar to bar group
            barGroup.addView(bar)
        }

        // add bar group to bar group container
        binding.barGroupsContainer.addView(
            barGroup
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}