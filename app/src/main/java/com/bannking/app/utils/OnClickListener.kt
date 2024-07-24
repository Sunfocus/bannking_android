package com.bannking.app.utils

import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.Data

interface OnClickListener {
    fun clickLister(data: Data)
}

interface CheckBoxListener {
    fun onClickCheckBox(checkBoxID: String?, type: String, name: String? = "")
}

interface CreateOwnMenuTitle {
    fun clickOnOwnMenuTitle()
}

interface SpendAdapterClick {
    fun clickOnSpentTitle(strString: String)
}


interface OnClickListenerViewPager {
    fun onClickListener(purchaseId: String)
}

interface OnSubmitBtnClick {
    fun onClick(dialogString: String?)
}

interface GetTokenFromOverride {
    fun sendToString(strToken: String)
}

interface MoreDotClick {
    fun openDialogBox(list: com.bannking.app.model.retrofitResponseModel.accountListModel.Data)
}

interface ItemClickListener {
    fun onClick(str: String?)
}

interface OnClickAnnouncementDialog {
    fun clickOnAnnouncementDialog(list: com.bannking.app.model.retrofitResponseModel.accountListModel.Data)
}

interface OnClickAnnouncement {
    fun clickOnAnnouncement(list: com.bannking.app.model.retrofitResponseModel.accountListModel.Data)
}


