package com.bannking.app.ui.activity

import com.bannking.app.R
import com.bannking.app.adapter.NotificationAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityNotificationBinding
import com.bannking.app.model.retrofitResponseModel.notificationModel.Data
import com.bannking.app.model.retrofitResponseModel.notificationModel.Notification
import com.bannking.app.model.retrofitResponseModel.notificationModel.NotificationModel
import com.bannking.app.model.viewModel.NotificationViewModel
import com.bannking.app.utils.SessionManager

class NotificationActivity :
    BaseActivity<NotificationViewModel, ActivityNotificationBinding>(NotificationViewModel::class.java) {

    lateinit var viewModel: NotificationViewModel
    lateinit var adapter: NotificationAdapter
    private lateinit var listData: ArrayList<Notification>

    override fun getBinding(): ActivityNotificationBinding {
        return ActivityNotificationBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: NotificationViewModel) {
        this.viewModel = viewModel
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.setDataInNotificationList(userToken)
    }

    override fun initialize() {
        adapter = NotificationAdapter()
        listData = arrayListOf()

        adapter = NotificationAdapter(this@NotificationActivity, listData)
        binding!!.rvNotification.adapter = adapter
    }

    override fun setMethod() {
        setOnClickListener()
    }


    override fun observe() {
        with(viewModel) {
            notificationListData.observe(this@NotificationActivity) { accountList ->
                if (accountList != null) {
                    if (accountList.code in 199..299) {
                        if (accountList.apiResponse != null) {
                            val mainModel = gson.fromJson(
                                accountList.apiResponse,
                                NotificationModel::class.java
                            )
                            if (mainModel.data.notifications != null){
                                if (mainModel.data.notifications!!.size != 0) {
                                    listData = mainModel.data.notifications!!
                                    adapter.updateList(mainModel.data.notifications!!)
                                } else
                                    dialogClass.showError(resources.getString(R.string.str_no_data_found))

                            }else dialogClass.showError(resources.getString(R.string.str_no_data_found))

                        }
                    } else if (accountList.code in 400..500) {
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
        }
    }
}