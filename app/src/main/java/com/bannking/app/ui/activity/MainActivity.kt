package com.bannking.app.ui.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import com.bannking.app.R
import com.bannking.app.UiExtension.isDarkModeEnabled
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.databinding.ActivityMainBinding
import com.bannking.app.model.CommonResponseApi
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel
import com.bannking.app.model.viewModel.MainViewModel
import com.bannking.app.ui.fragment.AccountCreatedFragment
import com.bannking.app.ui.fragment.AccountsFragment
import com.bannking.app.utils.Constants
import com.bannking.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.play.core.review.ReviewInfo
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*


/**
 * Crated By AM on 23/09/2022
 */

class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>(MainViewModel::class.java) {
    lateinit var viewModel: MainViewModel
    lateinit var savedSessionManagerCurrency: SessionManager
    lateinit var savedSessionManagerLanguage: SessionManager
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001
    //Amit
    private lateinit var saveReviewManager: SessionManager
    private lateinit var reviewManager: ReviewManager
    private var reviewInfo: ReviewInfo? = null

    override fun getBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: MainViewModel) {
        this.viewModel = viewModel
    }

    override fun observe() {
        with(viewModel) {
            progressObservable.observe(this@MainActivity) { isProgress ->
                if (isProgress != null) {
                    if (isProgress)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
            deleteBankAccount.observe(this@MainActivity) { deleteAccount ->
                if (deleteAccount != null) {
                    if (deleteAccount.code in 199..299) {
                        if (deleteAccount.apiResponse != null) {
                            val mainModel = gson.fromJson(
                                deleteAccount.apiResponse,
                                CommonResponseApi::class.java
                            )
                            if (mainModel.status!! == 200) {
                                dialogClass.showSuccessfullyDialog(mainModel.message.toString()) {
                                    deleteBankAccount.value = null
                                    headerTitleList.value =
                                        CommonResponseModel(
                                            deleteAccount.apiResponse,
                                            deleteAccount.code
                                        )
                                    recreate()
                                }
                            } else {
                                dialogClass.showError(mainModel.message.toString())
                            }
                        }
                    } else if (deleteAccount.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }

        }
    }

    @SuppressLint("SetTextI18n")
    override fun initialize() {
        if (isDarkModeEnabled()) {
            binding!!.bottomNavigationBar.setBackgroundResource(R.drawable.nav_shape_night) // Dark mode background color
        } else {
            binding!!.bottomNavigationBar.setBackgroundResource(R.drawable.nav_shape) // Light mode background color
        }


        reviewManager = ReviewManagerFactory.create(this)
        saveReviewManager = SessionManager(this, SessionManager.REVIEWMANAGER)
        val saveReviewManager = saveReviewManager.getLong(SessionManager.REVIEWMANAGER)
        sessionManager.updateLastActiveTime()

        val twentyFourInMillis = 24 * 60 * 60 * 1000 // 24hr in milliseconds
        val currentTime = System.currentTimeMillis()
        // Calculate the time difference
        val timeDifference = currentTime - saveReviewManager
        if (timeDifference > twentyFourInMillis) {
            requestReviewFlow()
        }
//        if (utils.checkisFirstDayOfWeek()) {
//            val saveReviewManager = saveReviewManager.getBoolean(SessionManager.REVIEWMANAGER)
//            if (!saveReviewManager) {
//                requestReviewFlow()
//            }
////            if (!sessionManager.getBoolean(SessionManager.isRated)) {
////                RateDialog(this, false).show()
////            }
//        }
//        if (utils.checkIsLastDayOfWeek()) {
//            saveReviewManager.setBoolean(SessionManager.REVIEWMANAGER, false)
//        }

        //Amit
        if (Build.VERSION.SDK_INT > 32) {
            when {
                ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {}

                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    showForcePermissionDialog()
                }

                else -> {
                    // Request the permission
                    requestPermissions(
                        arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                        NOTIFICATION_PERMISSION_REQUEST_CODE
                    )
                }
            }
        }

    }
    //Amit
    private fun showForcePermissionDialog() {
        val forceDialog = AlertDialog.Builder(this).setTitle("Permission Required")
            .setMessage("To access notifications, you must grant the permission. Please go to Settings and grant the permission.")
            .setPositiveButton("Settings") { dialogInterface: DialogInterface, i: Int ->
                // Open the app settings when the user clicks "Open Settings"
                openAppSettings()
                dialogInterface.dismiss()
            }.setCancelable(false).create()

        forceDialog.show()
    }
    //Amit
    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", this.packageName, null)
        startActivity(intent)
    }

    //Amit
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                // Permission denied
                // Handle the denied permission case
                showForcePermissionDialog()
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    //Amit
    private fun requestReviewFlow() {
        val request = reviewManager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We can get the ReviewInfo object
                reviewInfo = task.result
                startReviewFlow()
            } else {
                Log.d("sdfsdfdsfsdf", task.toString())
                // There was some problem, log or handle the error code.
                // task.exception
            }
        }
    }
    //Amit
    private fun startReviewFlow() {
        reviewInfo?.let {
            val flow = reviewManager.launchReviewFlow(this, it)
            flow.addOnCompleteListener { _ ->
                // The flow has finished, update the last prompt time
                saveReviewManager.saveLong(SessionManager.REVIEWMANAGER, System.currentTimeMillis())
            }
        }
    }

    override fun setMethod() {
        setOnClickListener()
    }

    fun loadAccountsFragment() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_container,
            AccountsFragment()
        ).commit()
    }

    private fun setOnClickListener() {
        binding!!.bottomNavigationBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_account -> {
                    with(viewModel) {
                        headerTitleList.observe(this@MainActivity) { headerTitleCommonModel ->
                            if (lifecycle.currentState == Lifecycle.State.RESUMED) {
                                if (headerTitleCommonModel != null) {
                                    if (headerTitleCommonModel.code in 199..299) {
                                        val headerModel =
                                            gson.fromJson(
                                                headerTitleList.value?.apiResponse,
                                                HeaderModel::class.java
                                            )
                                        if (headerModel.data.isNotEmpty()) {
                                            supportFragmentManager.beginTransaction()
                                                .replace(
                                                    R.id.fragment_container,
                                                    AccountCreatedFragment()
                                                ).commit()
                                        } else {
                                            supportFragmentManager.beginTransaction()
                                                .replace(
                                                    R.id.fragment_container,
                                                    AccountsFragment()
                                                ).commit()
                                        }
                                    } else {
                                        supportFragmentManager.beginTransaction()
                                            .replace(
                                                R.id.fragment_container,
                                                AccountsFragment()
                                            ).commit()
                                        dialogClass.showServerErrorDialog()
                                    }
                                }
                            }
                        }
                    }
                }

                R.id.nav_header -> {
                    val accountListModel =
                        gson.fromJson(
                            viewModel.accountListData.value?.apiResponse,
                            AccountListModel::class.java
                        )
                    if (accountListModel.data.isNotEmpty() && accountListModel!= null ) {

                        val intent = Intent(this@MainActivity, AccountMenuNewActivity::class.java)
                        val model =
                            gson.fromJson(
                                viewModel.headerTitleList.value?.apiResponse,
                                HeaderModel::class.java
                            )
                        val accountList =
                            gson.fromJson(
                                viewModel.accountListData.value?.apiResponse,
                                AccountListModel::class.java
                            )
                        intent.putExtra("Headermodel", model)
                        intent.putExtra("accountList", accountList)
                        resultLauncher.launch(intent)

//                        val intent = Intent(this@MainActivity, AccountMenuActivity::class.java)
//                        intent.putExtra("ComeFrom", "Navigation")
//                        startActivityForResult(intent, Constants.MAIN_TO_MENU)
                    } else {
                        dialogClass.showError(getString(R.string.please_create_any_one_account))
                        return@setOnItemSelectedListener false
                    }

                }

                R.id.nav_spending_plan -> {
                    Toast.makeText(
                        this@MainActivity,
                        "This feature is currently under development and will be available in a future update. Thank you for your patience!",
                        Toast.LENGTH_SHORT
                    ).show()

                /*    val model =
                        gson.fromJson(
                            viewModel.headerTitleList.value?.apiResponse,
                            HeaderModel::class.java
                        )
                    val accountList =
                        gson.fromJson(
                            viewModel.accountListData.value?.apiResponse,
                            AccountListModel::class.java
                        )
                    startActivityForResult(
                        Intent(
                            this@MainActivity, SpendingPlanActivity::class.java
                        ).putExtra("ComeFrom", "Navigation")
                            .putExtra("Headermodel", model).putExtra("accountList", accountList), Constants.MAIN_TO_SPENDING
                    )*/
                }


                R.id.nav_explore -> startActivity(
                    Intent(
                        this@MainActivity,
                        ExploreExpensesActivity::class.java
                    )
                )

                R.id.nav_menu -> {
                    val intent = Intent(this@MainActivity, ProfileActivity::class.java)
                    startActivity(intent)
                }
            }
            true
        }
        binding!!.imgProfile.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    ProfileActivity::class.java
                )
            )
        }
    }

    override fun onBackPressed() {
        finishAndRemoveTask()
    }


    @SuppressLint("SetTextI18n")
    override fun onResume() {
        super.onResume()
        if (sessionManager.getBoolean(SessionManager.isDeleteORLogOut)) {
            finishAndRemoveTask()
        }

        val userToken = sessionManager.getString(SessionManager.USERTOKEN)

        viewModel.setDataInHeaderTitleList(userToken)

        Handler(Looper.getMainLooper()).postDelayed({
            if (userToken != null) {
                viewModel.setDataInAccountList(userToken)
            }
        }, 100)
        Handler(Looper.getMainLooper()).postDelayed({
            if (userToken != null) {
                viewModel.getUserProfileData(userToken)
            }
        }, 200)

        binding!!.bottomNavigationBar.selectedItemId = R.id.nav_account

        binding!!.txtDate.text = utils.getCurrentDateMain()


        //Amit
        if (userModel!!.name != null) {
            binding!!.txtGreeting.text = "${getGreetingMessage() + ","} ${userModel!!.name} "
        } else {
            binding!!.txtGreeting.text = "${getGreetingMessage() + ","} ${userModel!!.username} "
        }

        // Initialize ReviewManager
        reviewManager = ReviewManagerFactory.create(this)

        saveReviewManager = SessionManager(this, SessionManager.REVIEWMANAGER)
        savedSessionManagerCurrency = SessionManager(this, SessionManager.CURRENCY)
        savedSessionManagerLanguage = SessionManager(this, SessionManager.LANGUAGE)


        if (savedSessionManagerCurrency.getCurrency().equals("")) {
            savedSessionManagerCurrency.setCurrency(userModel!!.currencyId.toString())
        }

        if (savedSessionManagerLanguage.getLanguage().equals("")) {
            savedSessionManagerLanguage.setLanguage(userModel!!.language!!.name.toString())
        }

        Glide.with(this@MainActivity)
            .asBitmap()
            .load(Constants.IMG_BASE_URL+userModel!!.image)
            .placeholder(R.drawable.glide_dot) //<== will simply not work:
            .error(R.drawable.glide_warning) // <== is also useless
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    binding!!.imgProfile.setImageBitmap(resource)
                }
            })



        val menu = binding!!.bottomNavigationBar.menu
        binding!!.bottomNavigationBar.itemIconTintList = null
        val menuItem = menu.findItem(R.id.nav_menu)
        Glide.with(this)
            .asBitmap()
            .load(Constants.IMG_BASE_URL + userModel!!.image)
            .apply(
                RequestOptions
                    .circleCropTransform()
                    .override(100, 100)
                    .placeholder(R.drawable.sample_user)
            )
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    menuItem?.icon = BitmapDrawable(resources, resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {

                }
            })


    }

    private fun getGreetingMessage(): String {
        val c = Calendar.getInstance()
        return when (c.get(Calendar.HOUR_OF_DAY)) {
            in 0..11 -> resources.getString(R.string.str_good_morning)
            in 12..15 -> resources.getString(R.string.str_good_afternoon)
            in 16..20 -> resources.getString(R.string.str_good_evening)
            in 21..23 -> resources.getString(R.string.str_good_night)
            else -> resources.getString(R.string.str_hello)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && (requestCode == Constants.MAIN_TO_MENU || requestCode == Constants.MAIN_TO_SPENDING)) {
            if (data!!.hasExtra("MainToMenu")) {
                viewModel.setIdInFilterDatanull(null)
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
//                viewModel.setDataInAccountList(userToken!!)
//
                viewModel.setDataInHeaderTitleList(userToken)
            }
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val data: Intent? = result.data
            if (result.resultCode == 1011) {
                val intent = Intent(this@MainActivity, BudgetPlannerActivity::class.java)
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

}