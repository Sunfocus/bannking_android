package com.bannking.app.ui.activity

import com.bannking.app.R
import com.bannking.app.adapter.NotificationAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityNotificationBinding
import com.bannking.app.model.retrofitResponseModel.notificationModel.Data
import com.bannking.app.model.retrofitResponseModel.notificationModel.NotificationModel
import com.bannking.app.model.viewModel.NotificationViewModel

class NotificationActivity :
    BaseActivity<NotificationViewModel, ActivityNotificationBinding>(NotificationViewModel::class.java) {

    lateinit var viewModel: NotificationViewModel
    lateinit var adapter: NotificationAdapter
    private lateinit var listData: ArrayList<Data>

    override fun getBinding(): ActivityNotificationBinding {
        return ActivityNotificationBinding.inflate(layoutInflater)
    }

    override fun initViewModel(viewModel: NotificationViewModel) {
        this.viewModel = viewModel
        viewModel.setDataInNotificationList()
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
                            if (mainModel.data.size != 0) {
                                listData = mainModel.data
                                adapter.updateList(mainModel.data)
                            } else

                                dialogClass.showError(resources.getString(R.string.str_no_data_found))
//                                Toast.makeText(this@NotificationActivity, "No data found", Toast.LENGTH_SHORT).show()
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