package com.bannking.app.ui.activity

import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.adapter.AudioTypeAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivitySoundBinding
import com.bannking.app.model.AudioPlayListener
import com.bannking.app.model.viewModel.ProfileViewModel

class SoundActivity :
    BaseActivity<ProfileViewModel, ActivitySoundBinding?>(ProfileViewModel::class.java),
    AudioPlayListener {

    private lateinit var audioTypeAdapter: AudioTypeAdapter
    private lateinit var spinnerListAudio: ArrayList<String>
    override fun getBinding(): ActivitySoundBinding {
        return ActivitySoundBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        setUIColor()
        setAdapter()
        setSpinnerList()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun setSpinnerList() {
        spinnerListAudio = arrayListOf()
        spinnerListAudio.add("English,US")
        spinnerListAudio.add("English,UK")
        binding!!.spinnerLang.apply {
            adapter = ArrayAdapter(
                this@SoundActivity, android.R.layout.simple_spinner_item, spinnerListAudio
            ).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
            setSelection(0)
        }
    }

    override fun setMethod() {
        setOnClickListener()
    }

    override fun observe() {

    }

    override fun initViewModel(viewModel: ProfileViewModel) {

    }

    private fun setUIColor() {
        if (UiExtension.isDarkModeEnabled()) {
            binding!!.clTopSound.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.dark_mode
                )
            )
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.white))
            binding!!.tvAudioIfo.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.tvSelectionType.setTextColor(ContextCompat.getColor(this, R.color.white))
            binding!!.clAudioType.setBackgroundResource(R.drawable.edittext_stroke_blue)

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

        } else {
            binding!!.clTopSound.setBackgroundColor(
                ContextCompat.getColor(
                    this, R.color.white
                )
            )
            binding!!.clAudioType.setBackgroundResource(R.drawable.bg_gender_audio)
            binding!!.imgBack.setColorFilter(this.resources.getColor(R.color.black))
            binding!!.tvAudioIfo.setTextColor(ContextCompat.getColor(this, R.color.clr_text_blu))
            binding!!.tvSelectionType.setTextColor(
                ContextCompat.getColor(
                    this, R.color.clr_text_blu
                )
            )

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
        }
        binding!!.tvMale.setOnClickListener {
            maleSelect()
        }
        binding!!.tvFemale.setOnClickListener {
            femaleSelect()
        }
        binding!!.tvChild.setOnClickListener {
            childSelect()
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

    private fun setAdapter() {
        audioTypeAdapter = AudioTypeAdapter(this, this)
        binding!!.rvAudioTpe.adapter = audioTypeAdapter
    }

    override fun clickedItem(position: Int) {
        Toast.makeText(
            this@SoundActivity,
            "This feature is currently under development and will be available in a future update. Thank you for your patience!",
            Toast.LENGTH_SHORT
        ).show()
    }


}