package com.bannking.app.ui.activity

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.bannking.app.BuildConfig
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityProfileUpdateBinding
import com.bannking.app.model.retrofitResponseModel.userModel.Data
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.ProfileUpdateViewModel
import com.bannking.app.utils.AdController
import com.bannking.app.utils.Constants
import com.bannking.app.utils.FileUtil
import com.bannking.app.utils.SessionManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.messaging.FirebaseMessaging
import java.io.IOException

class ProfileUpdateActivity :
    BaseActivity<ProfileUpdateViewModel, ActivityProfileUpdateBinding>(ProfileUpdateViewModel::class.java) {
    private var switchFaceVerificationOFF: Boolean = false
    lateinit var viewModel: ProfileUpdateViewModel
    private var bottomSheetDialog: BottomSheetDialog? = null

    private val IMAGE_GALLERY_REQUEST_CODE_INTENT = 200
    private val IMAGE_CAMERA_REQUEST_CODE_INTENT = 100
    private var imgPath = ""
    var data: Data? = null

    override fun getBinding(): ActivityProfileUpdateBinding {
        return ActivityProfileUpdateBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: ProfileUpdateViewModel) {
        this.viewModel = viewModel
    }

    private fun uiColor(){
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.tvUpdatePP.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.llUpdateProfile.setBackgroundColor(ContextCompat.getColor(this, R.color.dark_mode))
            binding!!.tvFirstName.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvUserName.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvChangeEmail.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvSecurity.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvRefrain.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvVersion.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.etFirstNameUpdate.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.txtFragHeader.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.edtUserEmail.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.edtUserName.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvFaceIdEP.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvChangePassEP.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding!!.llUpdateProfile.setBackgroundColor(ContextCompat.getColor(this, R.color.white))
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
            binding!!.tvUpdatePP.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvFirstName.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvUserName.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvChangeEmail.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvSecurity.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvRefrain.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvFaceIdEP.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding!!.txtFragHeader.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding!!.tvChangePassEP.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding!!.tvVersion.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.edtUserName.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.etFirstNameUpdate.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.edtUserEmail.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.edtUserName.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.etFirstNameUpdate.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.edtUserEmail.setHintTextColor(ContextCompat.getColor(this, R.color.grey))
        }
    }

    override fun initialize() {
        data = Data()
        uiColor()
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataProfileDataList(userToken)

    }

    override fun setMethod() {
        setOnClickListener()
        val versionName: String = BuildConfig.VERSION_NAME
        binding!!.tvVersion.text = "$versionName"

    }


    override fun observe() {
        with(viewModel) {
            profileUpdateData.observe(this@ProfileUpdateActivity) { updateResponse ->
                if (updateResponse != null) {
                    if (updateResponse.code in 199..299) {
                        val mainModel =
                            gson.fromJson(updateResponse.apiResponse, UserModel::class.java)
                        if (mainModel != null) {
                            if (mainModel.status == 200) {
                                data = mainModel.data
                                setUserDataInSession(mainModel.data!!)
                                dialogClass.showSuccessfullyDialog(mainModel.message.toString())
//                                Toast.makeText(this@ProfileUpdateActivity, mainModel.message, Toast.LENGTH_SHORT).show()
                            } else
                                dialogClass.showError(mainModel.message.toString())
                        }
                    } else
                        dialogClass.showServerErrorDialog()
                }
            }

            //Delete Account Response
            deleteAccountData.observe(this@ProfileUpdateActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val mainModel =
                            gson.fromJson(apiResponseData.apiResponse, UserModel::class.java)
                        if (mainModel.status == 200) {
                            FirebaseMessaging.getInstance()
                                .unsubscribeFromTopic("user_" + userModel!!.id)
                                .addOnCompleteListener { task ->
                                    Log.d("token====", userModel!!.id.toString())
                                }
                            sessionManager.setString(
                                SessionManager.UserId, ""
                            )
                            sessionManager.setString(
                                SessionManager.Password, ""
                            )
                            inAppPurchaseSM.logOut()
                            sessionManager.logOut()
                            sessionManager.setBoolean(SessionManager.isLogin, false)

//                            startActivity(Intent(this@ProfileActivity, SplashActivity::class.java).setFlags(
//                                FLAG_ACTIVITY_CLEAR_TASK))
//                            finishAffinity()
                            val intent = Intent(this@ProfileUpdateActivity, SplashActivity::class.java)
                            val cn = intent.component
                            val mainIntent = Intent.makeRestartActivityTask(cn)
                            startActivity(mainIntent)
                        }
                    } else dialogClass.showServerErrorDialog()
                }
            }

            progressObservable.observe(this@ProfileUpdateActivity) {
                if (it != null) {
                    if (it)
                        dialogClass.showLoadingDialog()
                    else
                        dialogClass.hideLoadingDialog()
                }
            }
            //Change Language Response
            profileData.observe(this@ProfileUpdateActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.code in 199..299) {
                        if (apiResponseData.apiResponse != null) {
                            val mainModel =
                                gson.fromJson(apiResponseData.apiResponse, UserModel::class.java)
                            if (mainModel.status == 200) {
                                data = mainModel.data
                                setUserDataInSession((mainModel.data!!))
                                updateUi(mainModel.data)

                                if (mainModel.data!!.subscriptionStatus == 1) {
                                    inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
                                } else  {
                                    inAppPurchaseSM.setBoolean(SessionManager.isPremium, false)
                                }
                            }
                        }
                    } else if (apiResponseData.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }
        }
    }


    private fun setOnClickListener() {
        with(binding!!) {
            imgBack.setOnClickListener {
                finish()
            }
            llChangePasswordPf.setOnClickListener {
                startActivity(Intent(this@ProfileUpdateActivity, ChangePasswordActivity::class.java))
            }

            rlEditProfile.setOnClickListener {
                openBottomSuitorUpdateProfile()
            }

            btnSubmit.setOnClickListener {
                onSubmitClick()
            }
            switchFaceVerification.setOnCheckedChangeListener { _, isChecked ->
//                sessionManager.setBoolean(SessionManager.isNotification, isChecked)
                switchFaceVerificationOFF = isChecked
            }
            cardDelete.setOnClickListener {
                deleteUserAccount()
            }
        }
    }
    private fun deleteUserAccount() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@ProfileUpdateActivity)

        builder.setMessage(resources.getString(R.string.str_are_you_sure_you_want_to_delete_this_user_account))

        builder.setTitle(resources.getString(R.string.str_alert))
        builder.setIcon(R.drawable.ic_warning)
        builder.setCancelable(false)
        builder.setPositiveButton(
            resources.getString(R.string.str_confirm)
        ) { dialog: DialogInterface?, _: Int ->
            AdController.showInterAd(this@ProfileUpdateActivity, null, 0)
            val userToken = sessionManager.getString(SessionManager.USERTOKEN)
            viewModel.setDataDeleteAccountDataList(userToken)
            dialog?.dismiss()
        }
        builder.setNegativeButton(
            resources.getString(R.string.str_cancel)
        ) { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }

    private fun openBottomSuitorUpdateProfile() {

        bottomSheetDialog = BottomSheetDialog(this@ProfileUpdateActivity)
        val view: View = LayoutInflater.from(this@ProfileUpdateActivity).inflate(
            R.layout.bottom_sheet_add_picture_modal,
            this@ProfileUpdateActivity.findViewById(R.id.linearLayout)
        )
        bottomSheetDialog!!.setContentView(view)

        val llPickImageCamera: LinearLayout = view.findViewById(R.id.line_add_picture_camera)
        val lineAddPictureGallery: LinearLayout = view.findViewById(R.id.line_add_picture_gallery)
        bottomSheetDialog!!.show()


        llPickImageCamera.setOnClickListener {
            if (utils.checkPermissions(this@ProfileUpdateActivity)) {
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, IMAGE_CAMERA_REQUEST_CODE_INTENT)
            }

        }
        lineAddPictureGallery.setOnClickListener {
            if (utils.checkPermissions(this@ProfileUpdateActivity)) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(
                    Intent.createChooser(intent, resources.getString(R.string.str_select_picture)),
                    IMAGE_GALLERY_REQUEST_CODE_INTENT
                )
            }
        }
    }

    private fun onSubmitClick() {
        with(binding!!) {
            if (etFirstNameUpdate.text.isNotEmpty()){
            if (edtUserName.text.isNotEmpty()) {
                if (edtUserEmail.text.toString().isNotEmpty()) {
                    val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                    if (utils.isValidEmailId(edtUserEmail.text.toString().trim())) {
                        switchFaceVerification.isChecked
                        viewModel.setDataUpdateDataList(
                            edtUserName.text.toString(),
                            edtUserEmail.text.toString(),
                            imgPath,etFirstNameUpdate.text.toString(),userToken,switchFaceVerification.isChecked
                        )
                    } else
                        edtUserEmail.error =
                            resources.getString(R.string.str_please_enter_valid_email_id)
                } else
                    edtUserEmail.error = resources.getString(R.string.str_enter_email_address)
            } else
                edtUserName.error = resources.getString(R.string.enter_your_username)
            }else
                etFirstNameUpdate.error = "Enter your First name"
        }
    }

    private fun updateUi(data: Data?) {
        val setdata: Data = data ?: userModel!!
        Glide.with(this@ProfileUpdateActivity)
            .asBitmap()
            .load(Constants.IMG_BASE_URL+setdata.image)
            .placeholder(R.drawable.glide_dot) //<== will simply not work:
            .error(R.drawable.glide_warning) // <== is also useless
            .into(object : SimpleTarget<Bitmap?>() {
                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap?>?
                ) {
                    binding!!.imgUserPhoto.setImageBitmap(resource)
                }

            })

        binding!!.switchFaceVerification.isChecked = setdata.face_id_status!!

        binding!!.edtUserName.setText(setdata.username.toString())
        binding!!.edtUserEmail.setText(setdata.email.toString())
        binding!!.etFirstNameUpdate.setText(setdata.name.toString())

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == IMAGE_GALLERY_REQUEST_CODE_INTENT) {
                if (data != null) {
                    if (null != data.data) {
                        try {
                            val uri = data.data
                            val file = uri?.let { FileUtil.from(this@ProfileUpdateActivity, it) }
                            imgPath = file.toString()
                            binding!!.imgUserPhoto.setImageURI(uri)
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
            }
            //Get Image Frim camera
            try {
                if (requestCode == IMAGE_CAMERA_REQUEST_CODE_INTENT) {
                    val photo = data?.extras!!["data"] as Bitmap?
                    imgPath = utils.getImageUri(photo!!, this@ProfileUpdateActivity)
                    binding!!.imgUserPhoto.setImageBitmap(photo)
                }
            } catch (exception: Exception) {
                exception.message
            }
        }
        bottomSheetDialog?.dismiss()
    }
}