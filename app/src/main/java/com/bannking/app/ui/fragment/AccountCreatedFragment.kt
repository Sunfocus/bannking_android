package com.bannking.app.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.bannking.app.R
import com.bannking.app.UiExtension.isDarkModeEnabled
import com.bannking.app.adapter.FilterTabAdapter
import com.bannking.app.core.BaseFragment
import com.bannking.app.databinding.FragmentAccountCreatedBinding
import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountListModel
import com.bannking.app.model.retrofitResponseModel.headerModel.HeaderModel
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.model.viewModel.MainViewModel
import com.bannking.app.ui.activity.AccountMenuNewActivity
import com.bannking.app.ui.activity.MainActivity
import com.bannking.app.uiUtil.viewPagerAdapter.SectionsPagerAdapter
import com.bannking.app.utils.AdController
import com.bannking.app.utils.ItemClickListener
import com.bannking.app.utils.SessionManager
import com.google.android.material.bottomsheet.BottomSheetDialog


class AccountCreatedFragment :
    BaseFragment<MainViewModel, FragmentAccountCreatedBinding>(MainViewModel::class.java) {

    private var mParam1: String? = null
    private var mParam2: String? = null
    private lateinit var savedSessionManagerTab1: SessionManager
    private lateinit var savedSessionManagerTab2: SessionManager
    private lateinit var currentTab: SessionManager
    private lateinit var savedAdTime: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
        Log.d("sdfdsfdsfs", "yes")
    }


    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }

    override fun getBinding(
        inflater: LayoutInflater,
        container: ViewGroup
    ): FragmentAccountCreatedBinding {
        return FragmentAccountCreatedBinding.inflate(inflater, container, false)
    }

    override fun getViewModelClass(): MainViewModel {
        return ViewModelProvider(requireActivity())[MainViewModel::class.java]
    }

    override fun viewCreated() {
        if (isDarkModeEnabled()) {
            mBinding.LLVp.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireActivity(),
                    R.color.dark_mode
                ) // Dark mode background color
            mBinding.txtInformation.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        } else {
            mBinding.LLVp.backgroundTintList =
                ContextCompat.getColorStateList(
                    requireActivity(),
                    R.color.clr_wild_sand
                ) // Light mode background color
            mBinding.txtInformation.setTextColor(ContextCompat.getColor(requireActivity(), R.color.clr_text))
        }
        savedSessionManagerTab1 = SessionManager(requireActivity(), SessionManager.TAB1)
        savedSessionManagerTab2 = SessionManager(requireActivity(), SessionManager.TAB2)
        currentTab = SessionManager(requireActivity(), SessionManager.currentTab)
        savedAdTime = SessionManager(requireActivity(), SessionManager.ADTIME)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        val adTime = savedAdTime.getLong(SessionManager.ADTIME)
        val fiveMinutesInMillis = 5 * 60 * 1000 // 5 minutes in milliseconds
        val currentTime = System.currentTimeMillis()
        // Calculate the time difference
        val timeDifference = currentTime - adTime


        mBinding.imgAccountCreate.setOnClickListener {
            if (timeDifference > fiveMinutesInMillis) {
                AdController.showInterAd(requireActivity(), null, 0) {
                    val intent = Intent(requireActivity(), AccountMenuNewActivity::class.java)
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
                    (requireActivity() as MainActivity).resultLauncher.launch(intent)
                }
            } else {
                val intent = Intent(requireActivity(), AccountMenuNewActivity::class.java)
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
                (requireActivity() as MainActivity).resultLauncher.launch(intent)
            }


        }
        mBinding.txtInformation.setOnClickListener { dialogClass.showInformMotionDialog() }

        mBinding.imgFilter.setOnClickListener {
            openFilterDialog()
        }

    }


    override fun observer() {
        with(viewModel) {
            getProfileData.observe(this@AccountCreatedFragment) { apiResponseData ->
                if (apiResponseData != null) {
                    if (apiResponseData.code in 199..299) {
                        if (apiResponseData.apiResponse != null) {
                            val mainModel =
                                gson.fromJson(apiResponseData.apiResponse, UserModel::class.java)
                            if (mainModel.status == 200) {
                                if (mainModel.data!!.subscriptionStatus == 1) {
                                    inAppPurchaseSM.setBoolean(SessionManager.isPremium, true)
                                } else {
                                    inAppPurchaseSM.setBoolean(SessionManager.isPremium, false)
                                }
                            }
                        }
                    } else if (apiResponseData.code in 400..500) {
                        dialogClass.showServerErrorDialog()
                    }
                }
            }


            headerTitleList.observe(requireActivity()) { header ->
                if (isVisible) {
                    if (header != null) {
                        if (header.code in 199..299) {
                            if (header.apiResponse != null) {
                                val model =
                                    gson.fromJson(header.apiResponse, HeaderModel::class.java)
                                if (model.status == 200) {
                                    mBinding.tablayout.setupWithViewPager(mBinding.viewPager)
                                    val sectionsPagerAdapter = SectionsPagerAdapter(
                                        childFragmentManager,
                                        model.data
                                    )
//                                    filterTabDataList.value?.get(0).name

                                    filterTabDataList.value?.size
                                    mBinding.txtInformation.text = if (model.data.size == 1)
                                        "${model.data[0].name.toString()} Account"
                                    else if (model.data.size == 2)
                                        "${model.data[0].name.toString()} & ${model.data[1].name.toString()} Accounts"
                                    else ""

                                    if (model.data.size == 1) {
                                        savedSessionManagerTab1.setTab1(model.data[0].name.toString())
                                    } else if (model.data.size == 2) {
                                        savedSessionManagerTab1.setTab1(model.data[0].name.toString())
                                        savedSessionManagerTab2.setTab2(model.data[1].name.toString())
                                    }

                                    mBinding.viewPager.adapter = sectionsPagerAdapter
                                    //Amit
                                    val selectedTabIndex =
                                        currentTab.getInt(SessionManager.currentTab)
                                    mBinding.viewPager.setCurrentItem(selectedTabIndex, false)
                                    setCurrentTab()
                                } else {
                                    Log.e("sdfkhsdjfds", "yess")
                                    dialogClass.showServerErrorDialog()
                                }
                            }
                        }
                    }
                }
            }

            filterTabDataList.observe(requireActivity()) { header ->
                if (isVisible) {
                    if (header != null) {
                        mBinding.txtInformation.text = if (header.size == 1)
                            "${header[0].name.toString()} Account"
                        else if (filterTabDataList.value?.size == 3)
                            "${header[0].name.toString()} & ${header[1].name.toString()} Accounts"
                        else ""

                        Log.e("TAG_headerSize", "observer: ${header.size}")
                        if (header.size == 1) {
                            savedSessionManagerTab1.setTab1(header[0].name.toString())
                        } else if (header.size == 2) {
                            savedSessionManagerTab1.setTab1(header[0].name.toString())
                            savedSessionManagerTab2.setTab1(header[1].name.toString())
                        }
                        mBinding.tablayout.setupWithViewPager(mBinding.viewPager)
                        val sectionsPagerAdapter =
                            SectionsPagerAdapter(childFragmentManager, header)
                        mBinding.viewPager.adapter = sectionsPagerAdapter

                        //Amit
                        val selectedTabIndex = currentTab.getInt(SessionManager.currentTab)
                        mBinding.viewPager.setCurrentItem(selectedTabIndex, false)
                        setCurrentTab()
                    }
                }
            }
        }
    }

    //Amit
    private fun setCurrentTab() {
        val mPageChangeListener = object : OnPageChangeListener {
            override fun onPageScrollStateChanged(arg0: Int) {
            }

            override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {
            }

            override fun onPageSelected(pos: Int) {
                currentTab.saveInt(SessionManager.currentTab, pos)
            }
        }

        mBinding.viewPager.addOnPageChangeListener(mPageChangeListener)
    }

    private fun openFilterDialog() {
        val bottomSheetDialog: BottomSheetDialog?
        val itemClickListener: ItemClickListener
        bottomSheetDialog =
            BottomSheetDialog(requireActivity(), R.style.NoBackgroundDialogTheme)
        val view = LayoutInflater.from(requireActivity()).inflate(
            R.layout.bottomshit_filter_header,
            requireActivity().findViewById(R.id.linearLayout)
        )
        val rvFilter = view.findViewById(R.id.rv_filter) as RecyclerView
        val btnSubmit = view.findViewById(R.id.btn_submit) as Button
        bottomSheetDialog.setContentView(view)
        var adapter = FilterTabAdapter()
        val model =
            gson.fromJson(
                viewModel.headerTitleList.value!!.apiResponse,
                HeaderModel::class.java
            )

        itemClickListener = object : ItemClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onClick(str: String?) {
                rvFilter.post { adapter.notifyDataSetChanged() }
            }
        }

        if (model.data.isNotEmpty()) {
            if (model.data.size > 1) {
                bottomSheetDialog.show()
                val tempModel = model.data
                tempModel.add(
                    com.bannking.app.model.retrofitResponseModel.headerModel.Data(
                        "${model.data[0].id},${model.data[1].id}",
                        "Both"
                    )
                )
                adapter = FilterTabAdapter(
                    tempModel,
                    itemClickListener,
                    viewModel.filterTabDataList.value
                )
                rvFilter.layoutManager = LinearLayoutManager(requireActivity())
                rvFilter.adapter = adapter
            } else {
                dialogClass.showError(resources.getString(R.string.str_you_have_only_tab_available))

            }
        }

        btnSubmit.setOnClickListener {
            if (adapter.getSelected() != null) {
                if (adapter.getSelected()!![0].name.equals("Both")) {
                    viewModel.setIdInFilterData(model.data)
                } else {
                    viewModel.setIdInFilterData(adapter.getSelected()!!)
                }
                bottomSheetDialog.dismiss()
            }
        }
    }
}