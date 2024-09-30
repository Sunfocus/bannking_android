package com.bannking.app.ui.activity

import android.media.MediaPlayer
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Toast
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
import com.bannking.app.model.retrofitResponseModel.soundModel.Voices
import com.bannking.app.model.viewModel.SoundViewModel
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

    private lateinit var allVoices: ArrayList<Voices>
    private lateinit var childVoices: ArrayList<Voices>
    private lateinit var maleVoices: ArrayList<Voices>
    private lateinit var femaleVoices: ArrayList<Voices>
    private lateinit var audioTypeAdapter: AudioTypeAdapter
    private lateinit var viewModel: SoundViewModel
    private var mediaPlayer: MediaPlayer? = null
    override fun getBinding(): ActivitySoundBinding {
        return ActivitySoundBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: SoundViewModel) {
        viewModel.setDataInLanguageList("en-US")
        this.viewModel = viewModel
    }

    override fun initialize() {
        allVoices = ArrayList()
        maleVoices = ArrayList()
        femaleVoices = ArrayList()
        childVoices = ArrayList()

        setUIColor()

    }

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun setMethod() {
        setOnClickListener()
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

                            femaleVoices =
                                model.data.voices_list.filter { it.VoiceGender == "Female" } as ArrayList
                            maleVoices =
                                model.data.voices_list.filter { it.VoiceGender == "Male" } as ArrayList

                            allVoices = model.data.voices_list

                            childVoices =
                                model.data.voices_list.filter { it.VoiceGender == "Male (Child)" || it.VoiceGender == "Female (Child)" } as ArrayList
                            setAdapter(childVoices)
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

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
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
        val bottom_bar = bottomSheet.findViewById<ConstraintLayout>(R.id.bottom_bar)
        val adapter = LanguageRegionAdapter(object : LanguageRegionAdapter.ClickLanguageRegion {
            override fun onItemRegionClick(position: Int, languageCode: String) {
                binding!!.tvCountryRegion.text = languageCode
                viewModel.setDataInLanguageListWithLiveData(languageCode)
                    .observe(this@SoundActivity) { it ->
                        val response = it as SoundResponse
                        if (response.success) {
                            femaleVoices.clear()
                            maleVoices.clear()
                            allVoices.clear()
                            childVoices.clear()

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
            bottom_bar!!.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
        }else{
            bottom_bar!!.setBackgroundResource(R.drawable.bg_corner)
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
            binding!!.ivSelectCountryDropDown.setColorFilter(ContextCompat.getColor(this, R.color.white))
            binding!!.tvAudioIfo.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvSelectionType.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.clAudioType.setBackgroundResource(R.drawable.edittext_stroke_blue)
            binding!!.spinnerCL.setBackgroundResource(R.drawable.edittext_stroke_blue)

            binding!!.tvAll.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvMale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvFemale.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.black))

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
            binding!!.tvChild.setBackgroundResource(R.drawable.drawable_unselected)
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
            binding!!.ivSelectCountryDropDown.setColorFilter(ContextCompat.getColor(this, R.color.black))
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
            binding!!.tvChild.setTextColor(ContextCompat.getColor(this, R.color.black))


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
            binding!!.tvChild.setBackgroundResource(R.drawable.drawable_unselected)
        }
    }

    private fun setOnClickListener() {
        binding!!.imgBack.setOnClickListener {
            finish()
        }
        binding!!.tvAll.setOnClickListener {
            allSelect()
            setAdapter(allVoices)
        }
        binding!!.tvMale.setOnClickListener {
            maleSelect()
            setAdapter(maleVoices)
        }
        binding!!.tvFemale.setOnClickListener {
            femaleSelect()
            setAdapter(femaleVoices)
        }
        binding!!.tvChild.setOnClickListener {
            childSelect()
            setAdapter(childVoices)
        }
        binding!!.spinnerCL.setOnClickListener {
            openBottomSheet()
        }

        binding!!.btnSaveAudio.setOnClickListener {
            Toast.makeText(
                this@SoundActivity,
                "This feature is currently under development and will be available in a future update. Thank you for your patience!",
                Toast.LENGTH_SHORT
            ).show()
        }
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
        Log.d("VoicesParameter", VoicesParameter.toString())
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

    override fun clickedItem(position: Int, voices: Voices) {
        viewModel.postVoiceInLanguageList(voices.Engine, voices.VoiceId, voices.Language)
    }

}