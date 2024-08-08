package com.bannking.app.ui.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.adapter.ExploreExpensesAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityExploreExpensesBinding
import com.bannking.app.model.retrofitResponseModel.exploreExpensesModel.Data
import com.bannking.app.model.retrofitResponseModel.exploreExpensesModel.ExploreExpensesModel
import com.bannking.app.model.viewModel.ExploreExpensesViewModel
import com.bannking.app.utils.SessionManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.github.mikephil.charting.utils.MPPointF
import com.github.mikephil.charting.utils.Utils


class ExploreExpensesActivity :
    BaseActivity<ExploreExpensesViewModel, ActivityExploreExpensesBinding>(ExploreExpensesViewModel::class.java) {

    var adapter: ExploreExpensesAdapter? = null
    lateinit var viewModel: ExploreExpensesViewModel
    private var list: ArrayList<Data> = arrayListOf()

    override fun getBinding(): ActivityExploreExpensesBinding {
        return ActivityExploreExpensesBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: ExploreExpensesViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataInExploreExpensesDataList(userToken)
        adapter = ExploreExpensesAdapter()
    }

    override fun initialize() {
//        adapter = ExploreExpensesAdapter(this@ExploreExpensesActivity, list)
//        binding!!.rvExploreExpenses.adapter = adapter
    }

    override fun setMethod() {
        setOnClickListener()


    }


    override fun observe() {
        with(viewModel) {
            exploreExpensesDataList.observe(this@ExploreExpensesActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.code in 199..299) {
                        updateExpensesDataList(
                            gson.fromJson(
                                apiResponseData.apiResponse,
                                ExploreExpensesModel::class.java
                            ), apiResponseData.code
                        )
                    } else if (apiResponseData.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }
            progressObservable.observe(this@ExploreExpensesActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
        }

    }

    private fun updateExpensesDataList(model: ExploreExpensesModel, code: Int) {
        if (code in 199..299) {
            if (model.status == 200) {
                list = model.data
                if (model.data.isEmpty()) {
                    binding!!.txtNoDataFound.isVisible = true
                    binding!!.chart1.isVisible = false
                } else {
                    binding!!.txtNoDataFound.isVisible = false
                    binding!!.chart1.isVisible = true
                    showPieChart(model.data)
                }
                adapter = ExploreExpensesAdapter(this@ExploreExpensesActivity, model.data)
                binding?.rvExploreExpenses?.adapter = adapter
//                adapter?.updateList(model.data)
            }
        } else if (code in 400..500) {
            dialogClass.showServerErrorDialog()
        }
    }


    private fun setOnClickListener() {

        with(binding!!) {
            imgBack.setOnClickListener { finish() }

            txtUpgrade.setOnClickListener {
                startActivity(Intent(this@ExploreExpensesActivity, UpgradeActivity::class.java))
            }

        }
    }


    private fun showPieChart(data: ArrayList<Data>) {

        binding?.chart1?.description?.isEnabled = false
        binding?.chart1?.setExtraOffsets(5f, 10f, 5f, 5f)

        // on below line we are setting drag for our pie chart
        binding?.chart1?.dragDecelerationFrictionCoef = 0.95f

        // on below line we are setting hole
        // and hole color for pie chart
        binding?.chart1?.isDrawHoleEnabled = true
        binding?.chart1?.setHoleColor(Color.WHITE)

        // on below line we are setting circle color and alpha
        binding?.chart1?.setTransparentCircleColor(Color.WHITE)
        binding?.chart1?.setTransparentCircleAlpha(50)

        // on  below line we are setting hole radius
        binding?.chart1?.holeRadius = 20f
        binding?.chart1?.transparentCircleRadius = 30f

        // on below line we are setting center text
        binding?.chart1?.setDrawCenterText(true)

        // on below line we are setting
        // rotation for our pie chart
        binding?.chart1?.rotationAngle = 0f

        // enable rotation of the binding?.chart1?.by touch
        binding?.chart1?.isRotationEnabled = true
        binding?.chart1?.isHighlightPerTapEnabled = true

        // on below line we are setting animation for our pie chart
        binding?.chart1?.animateY(1400, Easing.EaseInOutQuad)

        // on below line we are disabling our legend for pie chart
        binding?.chart1?.legend?.isEnabled = false

        if (UiExtension.isDarkModeEnabled()) {
            binding?.chart1?.setEntryLabelColor(Color.WHITE)
        } else {
            binding?.chart1?.setEntryLabelColor(Color.BLACK)
        }

        binding?.chart1?.setEntryLabelTextSize(12f)

        // on below line we are creating array list and
        // adding data to it to display in pie chart
        val entries: ArrayList<PieEntry> = ArrayList()
        val colors: ArrayList<Int> = ArrayList()
        data.forEach { objectData ->

            entries.add(
                PieEntry(
                    String.format("%.1f", objectData.value?.toFloat())
                        .toFloat(), "${objectData.value} %"
                )
            )
            colors.add(Color.parseColor(objectData.color))

        }


        // on below line we are setting pie data set
        val dataSet = PieDataSet(entries, "Bannking")
        // on below line we are setting icons.
        dataSet.setDrawIcons(false)

        // on below line we are setting slice for pie
        dataSet.sliceSpace = 3f
        dataSet.iconsOffset = MPPointF(0f, 40f)
        dataSet.selectionShift = 5f

        // add a lot of colors to list
        // on below line we are setting colors.
        dataSet.colors = colors

        // on below line we are setting pie data set
        val data = PieData(dataSet)

        data.setValueTextSize(10f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)

        if (UiExtension.isDarkModeEnabled()) {
            data.setValueTextColor(Color.WHITE)
        } else {
            data.setValueTextColor(Color.BLACK)
        }
        binding?.chart1?.setData(data)

        // undo all highlights
//        binding?.chart1?.highlightValues(null)

        // loading chart
        data.setValueFormatter(PercentFormatter())
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE)
        dataSet.setValueLinePart1OffsetPercentage(100f)
        dataSet.setValueLinePart1Length(0.6f)
        dataSet.setValueLinePart2Length(0.6f)
        dataSet.setSelectionShift(5f)
        binding!!.chart1.getData().setDrawValues(false)
        binding?.chart1?.setUsePercentValues(true);
        binding?.chart1?.invalidate()

    }


    override fun onResume() {
        super.onResume()
        binding!!.txtUpgrade.isVisible = !isPremium
    }
    fun ellipse(angle: Float): Float{
        return kotlin.math.tan(
            (
                    if (angle % 180f in 0.0..90.0) (angle % 180f) / 2
                    else 90f - (angle % 180f) / 2
                    ) * Utils.FDEG2RAD.toDouble()
        ).toFloat()
    }
}