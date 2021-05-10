package com.fbuur.myhealthtracker.pages.data.statistics

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
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
        val yAxisTitles: List<String>
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
        _binding = ViewBarChartBinding.inflate(LayoutInflater.from(context))
    }

    private fun setupUI(chart: BarChart) {

        // setup x axis titles
        setupXAxisTitles(chart.xAxisTitles)

        // setup y axis titles
        setupYAxisTitles(chart.yAxisTitles)

        // for each bar chart group set up each group
        chart.barGroups.forEach {
            setupBarChartGroups(it)
        }


    }

    private fun setupXAxisTitles(titles: List<String>) {
        titles.forEach {
            binding.xAxisTitles.addView(
                TextView(context).apply {
                    text = it
                    gravity = Gravity.CENTER
                }
            )
        }
    }

    private fun setupYAxisTitles(titles: List<String>) {
        titles.forEach {
            binding.yAxisTitles.addView(
                TextView(context).apply {
                    text = it
                    gravity = Gravity.CENTER
                }
            )
        }
    }

    private fun setupBarChartGroups(barGroups: List<BarGroupEntity>) {

        // setup bar group container
        val barGroup = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
        }
        val layoutParams = barGroup.layoutParams
        layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        barGroup.layoutParams = layoutParams

        // populate bar group container
        barGroups.forEach { barGroupEntity ->

            val bar = FrameLayout(context).apply {
                background = ContextCompat.getDrawable(context, barGroupEntity.color)
            }
            val barLayoutParams = bar.layoutParams
            barLayoutParams.width = 8.dpToPx.toInt()
            barLayoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
            bar.layoutParams = barLayoutParams

        }

        // add bar group
        binding.barGroupsContainer.addView(
            barGroup
        )
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}