package com.fbuur.myhealthtracker.pages.data.statistics

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.animation.doOnStart
import com.fbuur.myhealthtracker.databinding.ViewBarChartBinding
import com.fbuur.myhealthtracker.pages.data.DataViewModel
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
        val yAxisTitles: List<Int>,
        val scope: DataViewModel.DataScope
    )

    class BarGroupEntity(
        val temId: Long,
        val color: Int,
        val value: Int
    )

    var barChartData: BarChart = BarChart(
        emptyList(),
        emptyList(),
        emptyList(),
        DataViewModel.DataScope.DAY
    )
        set(value) {
            field = value
            setupUI(value)
        }

    init {
        _binding = ViewBarChartBinding.inflate(LayoutInflater.from(context), this, true)
    }

    private fun setupUI(chart: BarChart) {

        // empty text visibility
        if (chart.yAxisTitles.isEmpty()) {
            binding.emptyBarChartText.visibility = View.VISIBLE
        } else {
            binding.emptyBarChartText.visibility = View.GONE
        }

        // setup x axis titles
        setupXAxisTitles(chart.xAxisTitles)

        // setup y axis titles
        setupYAxisTitles(chart.yAxisTitles)

        // for each bar chart group set up each group
        binding.barGroupsContainer.removeAllViews()
        chart.barGroups.forEach { item ->
            setupBarChartGroups(item)
        }

    }

    private fun setupXAxisTitles(titles: List<String>) {
        binding.xAxisTitles.removeAllViews()
        titles.forEach {

            val tv = TextView(context).apply {
                text = it
                textSize = 9f
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
                    textSize = 9f
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

    private fun setupBarChartGroups(barGroups: List<BarGroupEntity>) {

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
        barGroups.forEachIndexed { i, barGroupEntity ->
            val maxHeight = binding.yAxisTitles.measuredHeight.toFloat()
            val itemHeight = maxHeight / binding.yAxisTitles.childCount

            val max = this.barChartData.yAxisTitles.maxOf { it }
            val heightPercentage: Float = barGroupEntity.value.toFloat() / max.toFloat()
            val actualHeight = maxHeight * heightPercentage - itemHeight.toFloat() / 2

            val bar = View(context).apply {
                layoutParams = LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, // width
                    ViewGroup.LayoutParams.MATCH_PARENT // height
                )
                layoutParams.width = when (this@BarChartView.barChartData.scope) {
                    DataViewModel.DataScope.DAY -> {
                        2.dpToPx.toInt()
                    }
                    DataViewModel.DataScope.WEEK -> {
                        8.dpToPx.toInt()
                    }
                    else -> {
                        8.dpToPx.toInt()
                    }
                }

                // animate bar height
                val anim = ValueAnimator
                    .ofInt(0, actualHeight.toInt())
                    .setDuration(375)
                anim.startDelay = i * 75L
                anim.addUpdateListener { animator ->
                    (animator.animatedValue as? Int)?.let {
                        this.layoutParams.height = it
                        this.requestLayout()
                    }
                }
                anim.doOnStart {
                    setBackgroundColor(barGroupEntity.color)
                }
                val animSet = AnimatorSet()
                animSet.interpolator = AccelerateDecelerateInterpolator()
                animSet.play(anim)
                animSet.start()

            }
            // add bar to bar group
            barGroup.addView(bar)
        }

        // add bar group to bar group container
        binding.barGroupsContainer.addView(
            barGroup
        )
    }

}

class ResizeAnimation(
    var view: View,
    private val targetHeight: Int,
) : Animation() {

    private val startHeight: Int = view.measuredHeight

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val newHeight = (startHeight + (targetHeight - startHeight) * interpolatedTime).toInt()
        view.layoutParams.height = newHeight
        view.requestLayout()
    }


    override fun willChangeBounds(): Boolean {
        return true
    }

}