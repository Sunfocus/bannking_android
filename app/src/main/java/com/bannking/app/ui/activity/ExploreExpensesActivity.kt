package com.bannking.app.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.adapter.ExploreExpensesAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityExploreExpensesBinding
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.exploreExpensesModel.Data
import com.bannking.app.model.retrofitResponseModel.exploreExpensesModel.ExploreExpensesModel
import com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel
import com.bannking.app.model.viewModel.ExploreExpensesViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
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
        uiColor()
//        adapter = ExploreExpensesAdapter(this@ExploreExpensesActivity, list)
//        binding!!.rvExploreExpenses.adapter = adapter
    }

    override fun setMethod() {
        setOnClickListener()


    }

    private fun uiColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.white))
            binding!!.tvExplore.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.rlExplore.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
        } else {
            binding!!.rlExplore.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.black))
            binding!!.tvExplore.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))

        }
    }


    override fun observe() {
        with(viewModel) {
            exploreExpensesDataList.observe(this@ExploreExpensesActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.code in 199..299) {
                        updateExpensesDataList(
                            gson.fromJson(
                                apiResponseData.apiResponse, ExploreExpensesModel::class.java
                            ), apiResponseData.code
                        )
                    } else if (apiResponseData.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }
            progressObservable.observe(this@ExploreExpensesActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
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
                    String.format("%.1f", objectData.value?.toFloat()).toFloat(),
                    "${objectData.value} %"
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
        binding?.chart1?.data = data

        // undo all highlights
//        binding?.chart1?.highlightValues(null)

        // loading chart
        data.setValueFormatter(PercentFormatter())
        dataSet.xValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.yValuePosition = PieDataSet.ValuePosition.OUTSIDE_SLICE
        dataSet.valueLinePart1OffsetPercentage = 100f
        dataSet.valueLinePart1Length = 0.6f
        dataSet.valueLinePart2Length = 0.6f
        dataSet.selectionShift = 5f
        binding!!.chart1.data.setDrawValues(false)
        binding?.chart1?.setUsePercentValues(true)
        binding?.chart1?.invalidate()

    }


    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1011) {
                val intent = Intent(this@ExploreExpensesActivity, BudgetPlannerActivity::class.java)
                intent.putExtra("SelectedMenu", data?.getStringExtra("SelectedMenu"))
                resultLauncher2.launch(intent)
            }
        }

    private var resultLauncher2 =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                viewModel.setIdInFilterDatanull(null)
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
//                viewModel.setDataInAccountList(userToken!!)
                viewModel.setDataInHeaderTitleList(userToken)
            }
        }


    override fun onResume() {
        super.onResume()
        binding!!.txtUpgrade.isVisible = !isPremium

        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataInHeaderTitleList(userToken)

        Handler(Looper.getMainLooper()).postDelayed({
            if (userToken != null) {
                viewModel.setDataInAccountList(userToken)
            }
        }, 100)

        binding!!.bottomNavExpense.selectedItemId = R.id.nav_explore

        binding!!.bottomNavExpense.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_account -> {
                    val intent = Intent(this@ExploreExpensesActivity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }

                R.id.nav_header -> {
                    val accountListModel = gson.fromJson(
                        viewModel.accountListData.value?.apiResponse, AccountListModel::class.java
                    )
                    if (accountListModel.data != null && accountListModel.data.isNotEmpty() && accountListModel != null) {

                        val intent = Intent(this@ExploreExpensesActivity, AccountMenuNewActivity::class.java)
                        val model = gson.fromJson(
                            viewModel.headerTitleList.value?.apiResponse, HeaderModel::class.java
                        )
                        val accountList = gson.fromJson(
                            viewModel.accountListData.value?.apiResponse,
                            AccountListModel::class.java
                        )
                        intent.putExtra("Headermodel", model)
                        intent.putExtra("accountList", accountList)
                        resultLauncher.launch(intent)
                    }

                }

                R.id.nav_spending_plan -> {
                    val intent = Intent(this@ExploreExpensesActivity, HeaderForBankActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)

                }

                R.id.nav_explore -> {}

                R.id.nav_menu -> {
                    val intent = Intent(this@ExploreExpensesActivity, ProfileActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }

            }
            true
        }

        val menu = binding!!.bottomNavExpense.menu
        binding!!.bottomNavExpense.itemIconTintList = null
        val menuItem = menu.findItem(R.id.nav_menu)
        Glide.with(this).asBitmap().load(Constants.IMG_BASE_URL + userModel!!.image).apply(
            RequestOptions.circleCropTransform().override(100, 100)
                .placeholder(R.drawable.sample_user)
        ).into(object : CustomTarget<Bitmap>() {
            override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                menuItem?.icon = BitmapDrawable(resources, resource)
            }
            override fun onLoadCleared(placeholder: Drawable?) {

            }

        })

    }

    fun ellipse(angle: Float): Float {
        return kotlin.math.tan(
            (if (angle % 180f in 0.0..90.0) (angle % 180f) / 2
            else 90f - (angle % 180f) / 2) * Utils.FDEG2RAD.toDouble()
        ).toFloat()
    }
}