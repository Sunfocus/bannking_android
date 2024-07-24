package com.bannking.app.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.bannking.app.R
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityProfileUpdateBinding
import com.bannking.app.model.retrofitResponseModel.userModel.Data
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.ProfileUpdateViewModel
import com.bannking.app.utils.Constants
import com.bannking.app.utils.FileUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.io.IOException

class ProfileUpdateActivity :
    BaseActivity<ProfileUpdateViewModel, ActivityProfileUpdateBinding>(ProfileUpdateViewModel::class.java) {

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

    override fun initialize() {
        data = Data()
        viewModel.setDataProfileDataList()

    }

    override fun setMethod() {
        setOnClickListener()
    }


    override fun observe() {
        with(viewModel) {
            profileUpdateData.observe(this@ProfileUpdateActivity) { updateResponse ->
                if (updateResponse != null) {
                    if (updateResponse.code in 199..299) {
                        val mainModel =
                            gson.fromJson(updateResponse.apiResponse, UserModel::class.java)
                        if (mainModel != null) {
                            if (mainModel.status.equals(Constants.STATUSSUCCESS)) {
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
                            if (mainModel.status.equals(Constants.STATUSSUCCESS)) {
                                data = mainModel.data
                                setUserDataInSession((mainModel.data!!))
                                updateUi(mainModel.data)
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

            rlEditProfile.setOnClickListener {
                openBottomSuitorUpdateProfile()
            }

            btnSubmit.setOnClickListener {
                onSubmitClick()
            }
        }
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
                    if (utils.isValidEmailId(edtUserEmail.text.toString().trim())) {
                        viewModel.setDataUpdateDataList(
                            edtUserName.text.toString(),
                            edtUserEmail.text.toString(),
                            imgPath,etFirstNameUpdate.text.toString()
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
            .load(setdata.image)
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