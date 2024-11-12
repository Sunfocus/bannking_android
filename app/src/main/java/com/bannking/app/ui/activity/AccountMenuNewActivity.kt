package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.adapter.AccountMenuNewAdapter
import com.bannking.app.adapter.AccountMenuWithSavedAdapterNew
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityAccountMenuNewBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.EditHeaderTitle
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.AccountTitleModel
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.Data
import com.bannking.app.model.viewModel.AccountMenuViewModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class AccountMenuNewActivity :
    BaseActivity<AccountMenuViewModel, ActivityAccountMenuNewBinding>(AccountMenuViewModel::class.java),
    EditHeaderTitle {

    private lateinit var accountList: AccountListModel
    var adapter: AccountMenuNewAdapter? = null
    private var adapterNew: AccountMenuWithSavedAdapterNew? = null
    private lateinit var list: ArrayList<Data>
    private lateinit var savedHeaderlist: ArrayList<Data>
    private lateinit var viewModel: AccountMenuViewModel
    private var bottomSheetDialog: BottomSheetDialog? = null
    private lateinit var currentTab: SessionManager
    override fun getBinding(): ActivityAccountMenuNewBinding {
        return ActivityAccountMenuNewBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: AccountMenuViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataInAccountTitleList(userToken)
    }


    private fun uiChangeColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.cvAccountMenu.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))

        } else {
            binding!!.cvAccountMenu.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_card_background
                )
            )
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
        }
    }

    override fun initialize() {
        uiChangeColor()

        currentTab = SessionManager(this, SessionManager.currentTab)
        savedHeaderlist = ArrayList()
        adapter = AccountMenuNewAdapter()
        list = arrayListOf()

        if (intent.hasExtra("Headermodel")) {
            val listModel =
                intent.getSerializableExtra("Headermodel") as com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel

            listModel.data.forEach { listObject ->
                savedHeaderlist.add(
                    Data(
                        id = listObject.id,
                        name = listObject.name?.uppercase(),
                        type = "1",
                        isTitleMenuHasAccount = true,
                        isAccountCreated = 1
                    )
                )
            }
        }

        if (intent.hasExtra("accountList")) {
            accountList = intent.getSerializableExtra("accountList") as AccountListModel
        }

        adapterNew = AccountMenuWithSavedAdapterNew(
            this@AccountMenuNewActivity, savedHeaderlist
        ) { postion ->

            val finalList =
                accountList.data.filter {
                    it.userAccountTitle!!.name!!.lowercase(Locale.getDefault()) == savedHeaderlist[postion].name!!.lowercase(
                        Locale.getDefault()
                    )
                }
            if (finalList.size != 0) {
                val headerTitleId = savedHeaderlist[postion].id!!.toString()
                deleteConfirmationDialog(
                    finalList[0], getString(R.string.str_remove_header), "3", "", headerTitleId
                ) {
                    adapter?.removeMenuTitle(savedHeaderlist[postion].name.toString())
                    savedHeaderlist.removeAt(postion)
                    adapterNew?.notifyItemRemoved(postion)
                    checkSavedHeaderList()
                }
            } else {
                val data = com.bannking.app.model.retrofitResponseModel.accountListModel.Data()
                val headerTitle = savedHeaderlist[postion].name!!.toString()
                val headerTitleId = savedHeaderlist[postion].id!!.toString()

                deleteConfirmationDialog(
                    data, getString(R.string.str_remove_header), "3", headerTitle,headerTitleId
                ) {
                    adapter?.removeMenuTitle(savedHeaderlist[postion].name.toString())
                    savedHeaderlist.removeAt(postion)
                    adapterNew?.notifyItemRemoved(postion)
                    checkSavedHeaderList()
                }

            }

        }

        binding!!.rvSelectedMenu.apply {
            adapter = adapterNew

        }
        checkSavedHeaderList()

    }

    fun checkSavedHeaderList() {
//        binding?.txtNoDataFount?.isVisible = savedHeaderlist.isEmpty()
        Log.d("dsfdfsdfdsfds", savedHeaderlist.size.toString())
        if (savedHeaderlist.size == 1) {
            binding?.txtRemainingDescription?.text = "One Account type is remaining"
        } else if (savedHeaderlist.size > 1) {
            binding?.txtRemainingDescription?.text = "0 Account type is remaining"
        }
//        else if (savedHeaderlist.size <= 0){
//            binding?.txtRemainingDescription?.text = "Remaining Menu Title"
//        }
    }

    override fun setMethod() {
        setOnClickListener()
        setAdapter()
    }

    @SuppressLint("NotifyDataSetChanged")
    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (data?.hasExtra("BudgetPlannerData") == true) {
                val BudgetModel =
                    data.getSerializableExtra("BudgetPlannerData") as com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.Data
                if (data.hasExtra("position")) {
                    savedHeaderlist[data.getStringExtra("position").toString()
                        .toInt()].accountName = data.getStringExtra("edtAccountName")
                    savedHeaderlist[data.getStringExtra("position").toString()
                        .toInt()].account_code = data.getStringExtra("edtAccountCode")
                    savedHeaderlist[data.getStringExtra("position").toString()
                        .toInt()].accountAmount = data.getStringExtra("edtAccountAmount")
                    savedHeaderlist[data.getStringExtra("position").toString().toInt()].budgetName =
                        BudgetModel.name
                    savedHeaderlist[data.getStringExtra("position").toString().toInt()].budgetId =
                        BudgetModel.id
                    savedHeaderlist[data.getStringExtra("position").toString().toInt()].isFillUp =
                        true
                    savedHeaderlist[data.getStringExtra("position").toString()
                        .toInt()].isAccountCreated = 1
                    savedHeaderlist[data.getStringExtra("position").toString()
                        .toInt()].isTitleMenuHasAccount = true
                    adapterNew?.notifyDataSetChanged()
                }
            }
        }

    override fun observe() {
        with(viewModel) {
            accountTitleList.observe(this@AccountMenuNewActivity) {
                if (it != null) {
                    if (it.code in 199..299) {
                        val model = gson.fromJson(it.apiResponse, AccountTitleModel::class.java)
                        if (model.status == 200) {
                            list = model.data

                            val removedDuplicateValueList: ArrayList<Data> = ArrayList()
                            for (item in list) {
                                if (savedHeaderlist.size == 1) {

                                    if (!savedHeaderlist[0].name.equals(item.name, true)) {
                                        removedDuplicateValueList.add(item.apply {
                                            isSelected = false
                                        })
                                    } else {
                                        removedDuplicateValueList.add(item.apply {
                                            isSelected = true
                                        })
                                    }
                                } else if (savedHeaderlist.size == 2) {
                                    if (!savedHeaderlist[0].name.equals(
                                            item.name, true
                                        ) && !savedHeaderlist[1].name.equals(item.name, true)
                                    ) {
                                        removedDuplicateValueList.add(item.apply {
                                            isSelected = false
                                        })
                                    } else {
                                        removedDuplicateValueList.add(item.apply {
                                            isSelected = true
                                        })
                                    }

                                }
                            }
                            if (removedDuplicateValueList.isEmpty()) {
                                adapter?.updateList(list)
                            } else {
                                adapter?.updateList(removedDuplicateValueList)
                            }

                        } else {
                            dialogClass.showServerErrorDialog()
                        }

                    } else if (it.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

            createOwnMenuTitleDataList.observe(this@AccountMenuNewActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.code in 199..299) {
                        if (apiResponseData.apiResponse != null) {
                            val model = gson.fromJson(
                                apiResponseData.apiResponse, CommonResponseApi::class.java
                            )
                            if (model.status == 200) {
                                dialogClass.showSuccessfullyDialog(model.message.toString())
                                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                                viewModel.setDataInAccountTitleList(userToken)
                            } else {
                                dialogClass.showError(model.message.toString())
                            }
                        } else dialogClass.showError(resources.getString(R.string.str_failed_to_create_menu_title_request))
                    } else if (apiResponseData.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }

                }
            }

            saveHeaderDataList.observe(this@AccountMenuNewActivity) { saveHeaderDataList ->
                if (saveHeaderDataList != null) {
                    if (saveHeaderDataList.code in 199..299) {
                        if (saveHeaderDataList.apiResponse != null) {
                            val model = gson.fromJson(
                                saveHeaderDataList.apiResponse, CommonResponseApi::class.java
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
                        } else dialogClass.showError(resources.getString(R.string.str_failed_to_save_menu_title_request))

                    } else if (saveHeaderDataList.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }

                }
            }

            progressObservable.observe(this@AccountMenuNewActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }
        }
    }


    private fun setAdapter() {
        binding!!.rvMenu.setHasFixedSize(true)
        val manager: LayoutManager = GridLayoutManager(this, 2, RecyclerView.VERTICAL, false)
        binding!!.rvMenu.layoutManager = manager
        adapter = AccountMenuNewAdapter(
            this@AccountMenuNewActivity, list, dialogClass, savedHeaderlist, adapterNew, this
        )
        binding!!.rvMenu.adapter = adapter
    }


    private fun setOnClickListener() {
        with(binding!!) {

            imgFloating.setOnClickListener {
                clickOnOwnMenuTitle(1, "", "")
            }

            imgBack.setOnClickListener { finish() }

            txtUpgrade.setOnClickListener {
                val selectedTabIndex = currentTab.getInt(SessionManager.currentTab)
//                finish()
//                startActivity(Intent(this@AccountMenuNewActivity, UpgradeActivity::class.java))
                if (savedHeaderlist.size == 1) {
                    finish()
                    val intent =
                        Intent(this@AccountMenuNewActivity, BudgetPlannerNewActivity::class.java)
                    intent.putExtra("SelectedItemMenu", savedHeaderlist[selectedTabIndex].id)
                    intent.putExtra("position", selectedTabIndex.toString())
                    startActivity(intent)
                } else if (savedHeaderlist.size == 2) {
                    val clickAccountType = currentTab.getBoolean(SessionManager.clickAccountType)
                    if (clickAccountType) {
                        currentTab.setBoolean(SessionManager.clickAccountType, false)
                        val intent = Intent(
                            this@AccountMenuNewActivity, BudgetPlannerNewActivity::class.java
                        )
                        intent.putExtra("SelectedItemMenu", savedHeaderlist[1].id)
                        intent.putExtra("position", "1")
                        startActivity(intent)
                        finish()
                    } else {
                        currentTab.setBoolean(SessionManager.clickAccountType, false)
                        val intent = Intent(
                            this@AccountMenuNewActivity, BudgetPlannerNewActivity::class.java
                        )
                        intent.putExtra("SelectedItemMenu", savedHeaderlist[selectedTabIndex].id)
                        intent.putExtra("position", selectedTabIndex.toString())
                        startActivity(intent)
                        finish()
                    }

                } else {
                    Toast.makeText(
                        this@AccountMenuNewActivity, "Select Account type!", Toast.LENGTH_SHORT
                    ).show()
                }
            }

        }

    }

    private fun clickOnOwnMenuTitle(type: Int, name: String, id: String) {
        bottomSheetDialog =
            BottomSheetDialog(this@AccountMenuNewActivity, R.style.NoBackgroundDialogTheme)
        val view = LayoutInflater.from(this@AccountMenuNewActivity)
            .inflate(R.layout.bottomshit_create_menu_title, findViewById(R.id.linearLayoutMenu))
        bottomSheetDialog!!.setContentView(view)
        bottomSheetDialog!!.show()

        val btnSubmit = view.findViewById<Button>(R.id.btn_submit)
        val edtCreateTitle = view.findViewById<EditText>(R.id.edt_create_title)
        val tvCreateOwn = view.findViewById<TextView>(R.id.tvCreateOwn)
        val btnDeleteTitle = view.findViewById<AppCompatButton>(R.id.btnDeleteTitle)

        if (UiExtension.isDarkModeEnabled()) {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.dark_mode)
            edtCreateTitle.setHintTextColor(ContextCompat.getColor(this, R.color.white))
            edtCreateTitle.setTextColor(ContextCompat.getColor(this, R.color.white))
            tvCreateOwn.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            view.backgroundTintList = ContextCompat.getColorStateList(this, R.color.white)
            edtCreateTitle.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
            edtCreateTitle.setTextColor(ContextCompat.getColor(this, R.color.black))
            tvCreateOwn.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
        }
        if (type == 2) {
            edtCreateTitle.setText(name)
            btnDeleteTitle.visibility = View.VISIBLE
            tvCreateOwn.text = "UPDATE OWN TITLE"
        } else {
            btnDeleteTitle.visibility = View.GONE
            tvCreateOwn.text = "CREATE OWN TITLE"
        }

        btnDeleteTitle.setOnClickListener {
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            viewModel.deleteCustomHeader(userToken, id).observe(this) {
                viewModel.setDataInAccountTitleList(userToken)
                bottomSheetDialog!!.dismiss()
            }
        }

        btnSubmit.setOnClickListener {
            if (type == 2) {
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                viewModel.updateCustomHeader(userToken, edtCreateTitle.text.toString(), id)
                    .observe(this) {
                        viewModel.setDataInAccountTitleList(userToken)
                        bottomSheetDialog!!.dismiss()
                    }
            } else {
                if (edtCreateTitle.text.toString().isNotEmpty()) {
                    val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                    viewModel.setDataInCreateOwnMenuTitleList(
                        edtCreateTitle.text.toString(), userToken
                    )
                    bottomSheetDialog!!.dismiss()
                } else edtCreateTitle.error =
                    resources.getString(R.string.str_please_enter_your_title)
            }

        }

    }


    private fun deleteConfirmationDialog(
        list: com.bannking.app.model.retrofitResponseModel.accountListModel.Data,
        alertMsg: String,
        deleteType: String,
        headerTitle: String,
        headerTitleId: String,
        callbacks: (() -> Unit)? = null
    ) {
        val headerTitleName = if (list.userAccountTitle != null) {
            list.userAccountTitle!!.name
        } else {
            headerTitle
        }
        val finalMessage =
            "Are you sure you want to delete all created accounts related to ${headerTitleName}?"
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@AccountMenuNewActivity)
        builder.setMessage(finalMessage)
        builder.setTitle(resources.getString(R.string.str_alert))
        builder.setIcon(R.drawable.ic_warning)
        builder.setCancelable(false)
        builder.setPositiveButton(
            resources.getString(R.string.str_confirm)
        ) { _: DialogInterface?, _: Int ->
            val accId = if (list.id.isNullOrEmpty()) {
                ""
            } else {
                list.id.toString()
            }
            setDataInDeleteBankAccount(accId, type = deleteType,headerTitleId) {
                callbacks?.invoke()
            }
        }
        builder.setNegativeButton(
            resources.getString(R.string.str_cancel)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()

        }
        Log.e("sdkjhfsdjfhds", "${list.id}")
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    fun setDataInDeleteBankAccount(
        headerTitleID: String, type: String,headerTitleId:String, callbacks: (() -> Unit)? = null
    ) {
        viewModel.progressObservable.value = true
        FCM_TOKEN.let {
            val apiBody = JsonObject()
            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("accountId", "")
                apiBody.addProperty("header_title_id", headerTitleID)
                apiBody.addProperty("type", type)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            val call = RetrofitClient.instance?.myApi?.deleteBankAccount(
                headerTitleID, type, "",headerTitleId, userToken!!
            )

            call?.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    viewModel.progressObservable.value = false
                    if (response.isSuccessful) {
                        val model = gson.fromJson(response.body(), CommonResponseApi::class.java)
                        if (model.status == 200) {
                            dialogClass.showSuccessfullyDialog(model.message.toString()) {
                                callbacks?.invoke()
                            }
                        } else {
                            dialogClass.showError(model.message.toString())
                        }
                    }
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    viewModel.progressObservable.value = false
                    dialogClass.showServerErrorDialog()
                }
            })
        }
    }


    override fun onResume() {
        super.onResume()
//        binding!!.txtUpgrade.isVisible = !isPremium

        binding!!.bottomNavHeader.selectedItemId = R.id.nav_header

        binding!!.bottomNavHeader.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_account -> {
                    val intent = Intent(this@AccountMenuNewActivity, MainActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }

                R.id.nav_header -> {

                }

                R.id.nav_spending_plan -> {
                    val intent =
                        Intent(this@AccountMenuNewActivity, HeaderForBankActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }

                R.id.nav_explore -> {
                    startActivity(
                        Intent(
                            this@AccountMenuNewActivity, ExploreExpensesActivity::class.java
                        )
                    )
                    overridePendingTransition(0, 0)
                    finish()
                }

                R.id.nav_menu -> {
                    val intent = Intent(this@AccountMenuNewActivity, ProfileActivity::class.java)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                }

            }
            true
        }

        val menu = binding!!.bottomNavHeader.menu
        binding!!.bottomNavHeader.itemIconTintList = null
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

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("MainToMenu", "True")
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun editTitleOnClick(name: String?, id: String?) {
        if (name != null && id != null) {
            clickOnOwnMenuTitle(2, name, id)
        }

    }
}
