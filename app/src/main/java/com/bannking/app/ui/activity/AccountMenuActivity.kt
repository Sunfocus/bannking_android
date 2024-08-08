package com.bannking.app.ui.activity

import android.content.Intent
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bannking.app.R
import com.bannking.app.adapter.AccountMenuOldAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityAccountMenuBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.AccountTitleModel
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.Data
import com.bannking.app.model.viewModel.AccountMenuViewModel
import com.bannking.app.utils.AdController
import com.bannking.app.utils.CreateOwnMenuTitle
import com.bannking.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog

class AccountMenuActivity :
    BaseActivity<AccountMenuViewModel, ActivityAccountMenuBinding>(AccountMenuViewModel::class.java),
    CreateOwnMenuTitle {

    var adapter: AccountMenuOldAdapter? = null
    lateinit var list: ArrayList<Data>
    lateinit var viewModel: AccountMenuViewModel
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var savedAdTime: SessionManager
    override fun getBinding(): ActivityAccountMenuBinding {
        return ActivityAccountMenuBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: AccountMenuViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataInAccountTitleList(userToken)
    }

    override fun initialize() {
        if (intent != null) {
            if (intent.getStringExtra("ComeFrom").equals("Navigation", true)) {
                binding!!.txtDone.isVisible = true
                binding!!.txtNext.isVisible = false
            } else {
                binding!!.txtDone.isVisible = false
                binding!!.txtNext.isVisible = true
            }
        }
        adapter = AccountMenuOldAdapter()
        list = arrayListOf()
        savedAdTime = SessionManager(this, SessionManager.ADTIME)
    }

    override fun setMethod() {
        setOnClickListener()
        setAdapter()


    }

    override fun observe() {
        with(viewModel) {
            accountTitleList.observe(this@AccountMenuActivity) {
                if (it != null) {
                    if (it.code in 199..299) {
                        val model = gson.fromJson(it.apiResponse, AccountTitleModel::class.java)
                        if (model.status == 200) {
                            list = model.data
//                            if (intent.getStringExtra("ComeFrom").equals("Navigation", true)) {
//                            list.add(
//                                Data(
//                                    "-1",
//                                    resources.getString(R.string.str_create_own_title),
//                                    "-1",
//                                    false,
//                                    Constants.ACCOUNT_CUSTOME
//                                )
//                            )
//                            }
                            adapter?.updateList(list)
                        } else {
                            dialogClass.showServerErrorDialog()
                        }
                    } else if (it.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            createOwnMenuTitleDataList.observe(this@AccountMenuActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.code in 199..299) {
                        if (apiResponseData.apiResponse != null) {
                            val model = gson.fromJson(
                                apiResponseData.apiResponse,
                                CommonResponseApi::class.java
                            )
                            if (model.status == 200) {
                                dialogClass.showSuccessfullyDialog(model.message.toString())
                                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                                viewModel.setDataInAccountTitleList(userToken)
                            } else {
                                dialogClass.showError(model.message.toString())
                            }
                        } else
                            dialogClass.showError(resources.getString(R.string.str_failed_to_create_menu_title_request))
                    } else if (apiResponseData.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }

                }
            }

            saveHeaderDataList.observe(this@AccountMenuActivity) { saveHeaderDataList ->
                if (saveHeaderDataList != null) {
                    if (saveHeaderDataList.code in 199..299) {
                        if (saveHeaderDataList.apiResponse != null) {
                            val model = gson.fromJson(
                                saveHeaderDataList.apiResponse,
                                CommonResponseApi::class.java
                            )
                            if (model.status == 200) {
                                dialogClass.showAccountCreateSuccessfullyDialog(getString(R.string.str_account_header_switch)) {
                                    val intent = Intent()
                                    intent.putExtra("MainToMenu", "MainToMenu")
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                            } else {
                                dialogClass.showError(model.message.toString())

                            }

                        } else
                            dialogClass.showError(resources.getString(R.string.str_failed_to_save_menu_title_request))

                    } else if (saveHeaderDataList.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }

                }
            }

            progressObservable.observe(this@AccountMenuActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
        }
    }


    private fun setAdapter() {
        binding!!.rvMenu.setHasFixedSize(true)
        val manager: LayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding!!.rvMenu.layoutManager = manager
        adapter = AccountMenuOldAdapter(this@AccountMenuActivity, list, this, dialogClass)
        binding!!.rvMenu.adapter = adapter
    }

    private fun setOnClickListener() {
        with(binding!!) {

            imgFloating.setOnClickListener {
                clickOnOwnMenuTitle()
            }

            imgBack.setOnClickListener { finish() }
            txtNext.setOnClickListener {
                if (adapter!!.selectedItem.isNotEmpty()) {
                    val stringConcat = if (adapter!!.selectedItem.size == 1) {
                        adapter!!.selectedItem[0].id.toString()
                    } else {
                        adapter!!.selectedItem[0].id.toString() + "," + adapter!!.selectedItem[1].id
                    }
                    val intent = Intent()
                    intent.putExtra("SelectedMenu", stringConcat)
                    setResult(1011, intent)
                    finish()
//                    val intent = Intent(this@AccountMenuActivity, BudgetPlannerActivity::class.java)
//                    intent.putExtra("SelectedMenu", stringConcat)
//                    startActivity(intent)
//                    finish()
                } else

                    dialogClass.showError(resources.getString(R.string.str_please_select_any_one_menu))
//                    Toast.makeText(this@AccountMenuActivity, "not selected", Toast.LENGTH_SHORT).show()
            }
            txtUpgrade.setOnClickListener {
                finish()
                startActivity(Intent(this@AccountMenuActivity, UpgradeActivity::class.java))
            }

            val adTime = savedAdTime.getLong(SessionManager.ADTIME)
            val fiveMinutesInMillis = 5 * 60 * 1000 // 5 minutes in milliseconds
            val currentTime = System.currentTimeMillis()
            // Calculate the time difference
            val timeDifference = currentTime - adTime

            txtDone.setOnClickListener {
                if (timeDifference > fiveMinutesInMillis) {
                    AdController.showInterAd(this@AccountMenuActivity, null, 0)
                }
                if (adapter!!.selectedItem.isNotEmpty()) {
                    val stringConcat = if (adapter!!.selectedItem.size == 1) {
                        adapter!!.selectedItem[0].id.toString()
                    } else {
                        adapter!!.selectedItem[0].id.toString() + "," + adapter!!.selectedItem[1].id
                    }
                    viewModel.setDataInSaveHeaderDataList(stringConcat)
                } else
                    dialogClass.showError(resources.getString(R.string.str_please_select_any_one_menu))
//                    Toast.makeText(this@AccountMenuActivity, "not selected", Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun clickOnOwnMenuTitle() {
        bottomSheetDialog =
            BottomSheetDialog(this@AccountMenuActivity, R.style.NoBackgroundDialogTheme)
        val view = LayoutInflater.from(this@AccountMenuActivity)
            .inflate(R.layout.bottomshit_create_menu_title, findViewById(R.id.linearLayoutMenu))
        bottomSheetDialog!!.setContentView(view)
        bottomSheetDialog!!.show()

        val btnSubmit = view.findViewById<Button>(R.id.btn_submit)
        val edtCreateTitle = view.findViewById<EditText>(R.id.edt_create_title)

        btnSubmit.setOnClickListener {
            if (edtCreateTitle.text.toString().isNotEmpty()) {
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.setDataInCreateOwnMenuTitleList(edtCreateTitle.text.toString(), userToken)
                bottomSheetDialog!!.dismiss()
            } else

                edtCreateTitle.error = resources.getString(R.string.str_please_enter_your_title)
        }
    }

    override fun onResume() {
        super.onResume()
        binding!!.txtUpgrade.isVisible = !isPremium
    }

}
