package com.bannking.app.ui.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.media.MediaPlayer
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.adapter.AudioTypeAdapter
import com.bannking.app.adapter.LanguageRegionAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivitySoundBinding
import com.bannking.app.model.AudioPlayListener
import com.bannking.app.model.LanguageDataResponse
import com.bannking.app.model.PostVoiceResponse
import com.bannking.app.model.retrofitResponseModel.soundModel.SoundResponse
import com.bannking.app.model.retrofitResponseModel.soundModel.UpdateSoundResponse
import com.bannking.app.model.retrofitResponseModel.soundModel.Voices
import com.bannking.app.model.viewModel.SoundViewModel
import com.bannking.app.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader
import java.io.Reader
import java.io.UnsupportedEncodingException

class SoundActivity :
    BaseActivity<SoundViewModel, ActivitySoundBinding?>(SoundViewModel::class.java),
    AudioPlayListener {
    private var premiumFeatures: Boolean = false
    private var voiceForApi = ""
    private var engineForApi = ""
    private var voiceGender = ""
    private var languageCodeForAPI = "en-US"
    private var languageNameForAPI = "English, US"
    private lateinit var voiceMakerList: ArrayList<Voices>
    private lateinit var allVoices: ArrayList<Voices>
    private lateinit var childVoices: ArrayList<Voices>
    private lateinit var maleVoices: ArrayList<Voices>
    private lateinit var femaleVoices: ArrayList<Voices>
    private lateinit var audioTypeAdapter: AudioTypeAdapter
    private lateinit var viewModel: SoundViewModel
    private var mediaPlayer: MediaPlayer? = null
    private lateinit var pref: SharedPref
    private lateinit var savedSessionManagerVoice: SessionManager
    var util: Utils = Utils()
    override fun getBinding(): ActivitySoundBinding {
        return ActivitySoundBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: SoundViewModel) {
        pref = SharedPref(this)

        savedSessionManagerVoice = SessionManager(this@SoundActivity, SessionManager.VOICE)
        voiceMakerList = pref.getListOfVoiceMaker(SessionManager.VOICEMAKERLIST)
        if (voiceMakerList.isEmpty()) {
            viewModel.setDataInLanguageList("en-US")
        }
        allVoices = ArrayList()
        maleVoices = ArrayList()
        femaleVoices = ArrayList()
        childVoices = ArrayList()

        if (voiceMakerList.isNotEmpty()) {
            allVoices = voiceMakerList
            maleVoices = voiceMakerList.filter { it.VoiceGender == "Male" } as ArrayList
            femaleVoices = voiceMakerList.filter { it.VoiceGender == "Female" } as ArrayList
            childVoices =
                voiceMakerList.filter { it.VoiceGender == "Male (Child)" || it.VoiceGender == "Female (Child)" } as ArrayList

        }
        this.viewModel = viewModel
    }

    override fun initialize() {
        setUIColor()
        val voiceGenderName = pref.getString(SessionManager.VOICEGENDER)
        premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)
        if (voiceGenderName.isNotEmpty()) {
            when (voiceGenderName) {
                "Child" -> {
                    childSelect()
                    setAdapter(childVoices)
                }

                "All" -> {
                    allSelect()
                    setAdapter(allVoices)
                }

                "Female" -> {
                    femaleSelect()
                    setAdapter(femaleVoices)
                }

                "Male" -> {
                    maleSelect()
                    setAdapter(maleVoices)
                }

                else -> {
                    maleSelect()
                    setAdapter(maleVoices)
                }
            }
        }


    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun setMethod() {
        setOnClickListener()
        updateUi()
    }

    private fun updateUi() {
        val languageRegionList = getLanguageRegion()
        val languageCode = pref.getString(SessionManager.LANGUAGECODEFORAPI)
        val voiceGenderPref = pref.getString(SessionManager.VOICEGENDER)
        val engineForApiPref = pref.getString(SessionManager.ENGINEFORAPI)
        val languageNameForApiPref = pref.getString(SessionManager.LANGUAGENAMEFORAPI)
        val voiceForApiPref = pref.getString(SessionManager.VOICEFORAPI)

        voiceGender = voiceGenderPref
        engineForApi = engineForApiPref
        voiceForApi = voiceForApiPref
        languageCodeForAPI = languageCode
        languageNameForAPI = languageNameForApiPref

        if (languageCode.isNotEmpty()) {
            val findSelectedObject = languageRegionList.find { it.languageCode == languageCode }
            if (findSelectedObject != null) {
                binding!!.tvCountryRegion.text = findSelectedObject.languageName
            }
        }

    }

    override fun observe() {
        with(viewModel) {
            soundMakerList.observe(this@SoundActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val model = gson.fromJson(
                            apiResponseData.apiResponse, SoundResponse::class.java
                        )
                        if (model.success) {
                            femaleVoices.clear()
                            maleVoices.clear()
                            allVoices.clear()
                            childVoices.clear()
                            pref.saveListOfVoiceMaker(
                                SessionManager.VOICEMAKERLIST, model.data.voices_list
                            )

                            femaleVoices =
                                model.data.voices_list.filter { it.VoiceGender == "Female" } as ArrayList
                            maleVoices =
                                model.data.voices_list.filter { it.VoiceGender == "Male" } as ArrayList

                            allVoices = model.data.voices_list

                            childVoices =
                                model.data.voices_list.filter { it.VoiceGender == "Male (Child)" || it.VoiceGender == "Female (Child)" } as ArrayList
                            maleSelect()
                            setAdapter(maleVoices)
                        }

                    } else dialogClass.showError("Something went wrong")
                }
            }

            updateVoiceMakerData.observe(this@SoundActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val model = gson.fromJson(
                            apiResponseData.apiResponse, UpdateSoundResponse::class.java
                        )
                        if (model.status == 200) {
                            pref.saveString(SessionManager.VOICEFORAPI, model.data.voice_id)
                            pref.saveString(SessionManager.ENGINEFORAPI, model.data.engine)
                            pref.saveString(SessionManager.VOICEGENDER, model.data.voice_gender)
                            pref.saveString(
                                SessionManager.LANGUAGECODEFORAPI, model.data.language_code
                            )
                            pref.saveString(
                                SessionManager.LANGUAGENAMEFORAPI, model.data.language_region
                            )
                            finish()
                        }

                    } else dialogClass.showError("Something went wrong")
                }
            }

            postSoundMakerList.observe(this@SoundActivity) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.apiResponse != null) {
                        val model = gson.fromJson(
                            apiResponseData.apiResponse, PostVoiceResponse::class.java
                        )
                        if (model.success) {
                            playAudio(model.path)
                        }

                    } else dialogClass.showError("Something went wrong")
                }
            }

            errorResponse.observe(this@SoundActivity) { message ->
                if (message != null) {
                    dialogClass.showErrorMessageDialog(message)
                }
            }

            progressObservable.observe(this@SoundActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }

        }
    }

    override fun onStop() {
        super.onStop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    private fun playAudio(url: String) {
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer()

        try {
            mediaPlayer?.apply {
                setDataSource(url)
                setOnPreparedListener {
                    start()
                }
                setOnCompletionListener {
                    release()
                }
                setOnErrorListener { _, _, _ ->
                    false
                }
                prepareAsync()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("catchError", "Error initializing media player: ${e.message}")
        }
    }

    private fun getLanguageRegion(): ArrayList<LanguageDataResponse> {
        var countries: ArrayList<LanguageDataResponse>? = null
        val inputStream: InputStream = this.resources.openRawResource(R.raw.language_region)
        try {
            val reader: Reader = InputStreamReader(inputStream, "UTF-8")
            val gson = Gson()
            countries =
                gson.fromJson(reader, object : TypeToken<ArrayList<LanguageDataResponse>>() {}.type)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        return countries!!
    }

    @Suppress("DEPRECATION")
    private fun openBottomSheet() {
        val languageRegionList = getLanguageRegion()
        val bottomSheet = BottomSheetDialog(this, R.style.SheetDialog)
        bottomSheet.setContentView(R.layout.bottom_sheet_language_region)
        val lp = WindowManager.LayoutParams()
        lp.alpha = 1.0f
        val metrics = DisplayMetrics()
        this.windowManager.defaultDisplay.getMetrics(metrics)
        lp.copyFrom(bottomSheet.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = (metrics.heightPixels * 1)
        lp.gravity = Gravity.CENTER
        bottomSheet.window!!.attributes = lp
        bottomSheet.behavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheet.behavior.peekHeight = metrics.heightPixels
        bottomSheet.show()
        val rcCountryName = bottomSheet.findViewById<RecyclerView>(R.id.rcCountryName)
        val bottomBar = bottomSheet.findViewById<ConstraintLayout>(R.id.bottom_bar)
        val adapter = LanguageRegionAdapter(object : LanguageRegionAdapter.ClickLanguageRegion {
            override fun onItemRegionClick(
                position: Int, languageCode: String, languageName: String
            ) {
                languageCodeForAPI = languageCode
                languageNameForAPI = languageName
                binding!!.tvCountryRegion.text = languageName
                viewModel.setDataInLanguageListWithLiveData(languageCode)
                    .observe(this@SoundActivity) { it ->
                        val response = it as SoundResponse
                        if (response.success) {
                            femaleVoices.clear()
                            maleVoices.clear()
                            allVoices.clear()
                            childVoices.clear()

                            pref.saveListOfVoiceMaker(
                                SessionManager.VOICEMAKERLIST, response.data.voices_list
                            )

                            femaleVoices =
                                response.data.voices_list.filter { it.VoiceGender == "Female" } as ArrayList
                            maleVoices =
                                response.data.voices_list.filter { it.VoiceGender == "Male" } as ArrayList

                            allVoices = response.data.voices_list

                            childVoices =
                                response.data.voices_list.filter { it.VoiceGender == "Male (Child)" || it.VoiceGender == "Female (Child)" } as ArrayList

                            setAdapter(childVoices)
                        }
                    }
                bottomSheet.dismiss()
            }

        }, this, languageRegionList)
        rcCountryName!!.adapter = adapter
        if (UiExtension.isDarkModeEnabled()) {
            bottomBar!!.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
        } else {
            bottomBar!!.setBackgroundResource(R.drawable.bg_corner)
        }
    }

    private fun setUIColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.clTopSound.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.white))
            binding!!.ivSelectCountryDropDown.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.white
                )
            )
            binding!!.tvAudioIfo.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvSelectionType.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.clAudioType.setBackgroundResource(R.drawable.edittext_stroke_blue)
            binding!!.spinnerCL.setBackgroundResource(R.drawable.edittext_stroke_blue)

            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.white))

            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvCountryRegion.setTextColor(ContextCompat.getColor(this, R.color.white))
        } else {
            binding!!.clTopSound.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.white
                )
            )
            binding!!.spinnerCL.setBackgroundResource(R.drawable.bg_gender_audio)
            binding!!.clAudioType.setBackgroundResource(R.drawable.bg_gender_audio)
            binding!!.imgBack.setColorFilter(ContextCompat.getColor(this, R.color.black))
            binding!!.ivSelectCountryDropDown.setColorFilter(
                ContextCompat.getColor(
                    this, R.color.black
                )
            )
            binding!!.tvAudioIfo.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding!!.tvSelectionType.setTextColor(
                ContextCompat.getColor(
                    this, R.color.clr_text_blu
                )
            )
            binding!!.tvCountryRegion.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.grey))


            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
        }
    }

    private fun setOnClickListener() {
        binding!!.imgBack.setOnClickListener {
            finish()
        }

        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            binding!!.btnMaleVoiceFree.isChecked = true
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {
            binding!!.btnFemaleVoiceFree.isChecked = true
        }

        var voice1 = ""
        binding!!.radiogrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId -> // find which radio button is selected
            when (checkedId) {
                R.id.btn_maleVoiceFree -> {
                    voice1 = util.getGenderDescription(Gender.MALE)
                    savedSessionManagerVoice.setAnnouncementVoice(voice1)
                }

                R.id.btn_femaleVoiceFree -> {
                    voice1 = util.getGenderDescription(Gender.FEMALE)
                    savedSessionManagerVoice.setAnnouncementVoice(voice1)
                }

                R.id.btn_otherVoiceFree -> {
                    voice1 = util.getGenderDescription(Gender.OTHER)
                    savedSessionManagerVoice.setAnnouncementVoice(voice1)
                }
            }
        })





        binding!!.tvAll.setOnClickListener {
            voiceGender = "All"
            allSelect()
            setAdapter(allVoices)
        }
        binding!!.tvMale.setOnClickListener {
            voiceGender = "Male"
            maleSelect()
            setAdapter(maleVoices)
        }
        binding!!.tvFemale.setOnClickListener {
            voiceGender = "Female"
            femaleSelect()
            setAdapter(femaleVoices)
        }
        binding!!.tvChild.setOnClickListener {
            voiceGender = "Child"
            childSelect()
            setAdapter(childVoices)
        }
        binding!!.spinnerCL.setOnClickListener {
            openBottomSheet()
        }
        premiumFeatures = inAppPurchaseSM.getBoolean(SessionManager.isPremium)
        binding!!.btnSaveAudio.setOnClickListener {
            if (premiumFeatures) {
                val userToken = sessionManager.getString(SessionManager.USERTOKEN)
                if (voiceForApi.isEmpty() && engineForApi.isEmpty() && languageCodeForAPI.isEmpty()) {
                    Toast.makeText(
                        this@SoundActivity, "Please select any one voice maker!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    viewModel.updateVoiceMakerApi(
                        userToken,
                        languageCodeForAPI,
                        languageNameForAPI,
                        voiceForApi,
                        engineForApi,
                        voiceGender
                    )
                }
            } else {
                val intent = Intent(this, UpgradeActivity::class.java)
                startActivity(intent)
//                showMembershipDialog()
            }
        }

        if (premiumFeatures) {
            binding!!.RlFree.visibility = View.GONE
            binding!!.tvFreeVoice.visibility = View.GONE
        } else {
            binding!!.RlFree.visibility = View.VISIBLE
            binding!!.tvFreeVoice.visibility = View.VISIBLE
        }
    }

    private fun showMembershipDialog() {

        val builder = AlertDialog.Builder(this)


        builder.setTitle("Upgrade to Premium")
        builder.setMessage("You are currently not a premium member. Choose an option below:")


        builder.setPositiveButton("Premium") { dialog, _ ->
            val intent = Intent(this, UpgradeActivity::class.java)
            startActivity(intent)
            dialog.dismiss()
        }

        builder.setNegativeButton("Free") { dialog, _ ->
            showAnnouncementDialog()
            dialog.dismiss()
        }


        val dialog = builder.create()
        dialog.show()
    }

    private fun showAnnouncementDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_announcement)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val imgClose: ImageView = dialog.findViewById(R.id.img_close)
        val radiogrp: RadioGroup = dialog.findViewById(R.id.radiogrp)
        val btn_maleVoice: RadioButton = dialog.findViewById(R.id.btn_maleVoice)
        val btn_femaleVoice: RadioButton = dialog.findViewById(R.id.btn_femaleVoice)
        val btn_otherVoice: RadioButton = dialog.findViewById(R.id.btn_otherVoice)
        val btnScheduleTransfer: Button = dialog.findViewById(R.id.btn_schedule_transfer)

        if (savedSessionManagerVoice.getAnnouncementVoice() == "Male") {
            btn_maleVoice.isChecked = true
        } else if (savedSessionManagerVoice.getAnnouncementVoice() == "Female") {
            btn_femaleVoice.isChecked = true
        }

        imgClose.setOnClickListener {
            dialog.dismiss()

        }
        var voice1 = ""
        radiogrp.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId -> // find which radio button is selected
            when (checkedId) {
                R.id.btn_maleVoice -> {
                    voice1 = util.getGenderDescription(Gender.MALE)
                    savedSessionManagerVoice.setAnnouncementVoice(voice1)
                }

                R.id.btn_femaleVoice -> {
                    voice1 = util.getGenderDescription(Gender.FEMALE)
                    savedSessionManagerVoice.setAnnouncementVoice(voice1)
                }

                R.id.btn_otherVoice -> {
                    voice1 = util.getGenderDescription(Gender.OTHER)
                    savedSessionManagerVoice.setAnnouncementVoice(voice1)
                }
            }
        })

        var voice = ""
        btnScheduleTransfer.setOnClickListener {
            if (btn_maleVoice.isChecked) {
                voice = util.getGenderDescription(Gender.MALE)
            } else if (btn_femaleVoice.isChecked) {
                voice = util.getGenderDescription(Gender.FEMALE)
            } else if (btn_otherVoice.isChecked) {
                voice = util.getGenderDescription(Gender.OTHER)
            }
            savedSessionManagerVoice.setAnnouncementVoice(voice)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun allSelect() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvAll.setBackgroundResource(R.drawable.drawable_unselected)

        } else {
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.grey))

            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvAll.setBackgroundResource(R.drawable.drawable_unselected)

        }
    }

    private fun maleSelect() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvMale.setBackgroundResource(R.drawable.drawable_unselected)
        } else {
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.grey))

            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvMale.setBackgroundResource(R.drawable.drawable_unselected)

        }

    }

    private fun femaleSelect() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvFemale.setBackgroundResource(R.drawable.drawable_unselected)


        } else {
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.black))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.grey))

            binding!!.tvChild.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvFemale.setBackgroundResource(R.drawable.drawable_unselected)
        }
    }

    private fun childSelect() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.tvChild.setBackgroundResource(R.drawable.drawable_unselected)

        } else {
            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.grey))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.black))

            binding!!.tvFemale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvMale.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvAll.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.clr_wild_sand
                )
            )
            binding!!.tvChild.setBackgroundResource(R.drawable.drawable_unselected)
        }
    }

    private fun setAdapter(VoicesParameter: ArrayList<Voices>) {
        val getWhichVoice = pref.getString(SessionManager.VOICEFORAPI)

        if (getWhichVoice.isNotEmpty()) {
            val checkedPosition = VoicesParameter.indexOfFirst { it.VoiceId == getWhichVoice }
            if (checkedPosition != -1) {
                VoicesParameter[checkedPosition].checkValue = true
                VoicesParameter.sortedBy { it.checkValue }
            }
        }
        if (VoicesParameter.isEmpty()) {
            binding!!.tvItemsNotFound.visibility = View.VISIBLE
            binding!!.rvAudioTpe.visibility = View.GONE

        } else {

            binding!!.rvAudioTpe.visibility = View.VISIBLE
            binding!!.tvItemsNotFound.visibility = View.GONE
            audioTypeAdapter = AudioTypeAdapter(this, VoicesParameter, this)
            binding!!.rvAudioTpe.adapter = audioTypeAdapter
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun clickedItem(position: Int, voices: Voices) {
        engineForApi = voices.Engine
        voiceForApi = voices.VoiceId
        languageCodeForAPI = voices.Language
        audioTypeAdapter.notifyDataSetChanged()
        if (premiumFeatures) {
            viewModel.postVoiceInLanguageList(voices.Engine, voices.VoiceId, voices.Language)
        } else {
            AdController.showInterAd(this@SoundActivity, null, 0) {
                viewModel.postVoiceInLanguageList(voices.Engine, voices.VoiceId, voices.Language)
            }
        }
    }

}