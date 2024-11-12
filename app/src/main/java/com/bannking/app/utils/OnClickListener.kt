package com.bannking.app.utils

import com.bannking.app.model.retrofitResponseModel.accountListModel.AccountsData
import com.bannking.app.model.retrofitResponseModel.accountListModel.ExtraData
import com.bannking.app.model.retrofitResponseModel.accountListModel.HiddenData
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.Data
import com.bannking.app.model.retrofitResponseModel.budgetPlannerModel.SubBudgetPlanner

interface OnClickListener {
    fun clickLister(data: SubBudgetPlanner)
    fun createNewItem(position:Int)
}
interface OnClickListenerBudget {
    fun clickListerBudget(data: Data,subData: SubBudgetPlanner,clickedCreate:String)
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
    fun openDialogBox(
        list: com.bannking.app.model.retrofitResponseModel.accountListModel.Data,
        list1: ArrayList<com.bannking.app.model.retrofitResponseModel.accountListModel.Data>
    )
    fun openDialogBoxExtraData(
        currentExtraItem: ExtraData,
        extraData: ArrayList<AccountsData>,
        hiddenData: ArrayList<HiddenData>?
    )
    fun dotForChildren(
        accountId: String,
        instituteId: String,
        accountsList: ArrayList<AccountsData>,
        hiddenData: ArrayList<HiddenData>?,
        extraDataHideList: ArrayList<AccountsData>
    )
}

interface ItemClickListener {
    fun onClick(str: String?)
}

interface OnClickAnnouncementDialog {
    fun clickOnAnnouncementDialog(list: com.bannking.app.model.retrofitResponseModel.accountListModel.Data)
    fun clickOnAnnouncementDialogExtra(currentExtraItem: AccountsData)
}

interface OnClickAnnouncement {
    fun clickOnAnnouncement(list: com.bannking.app.model.retrofitResponseModel.accountListModel.Data)
    fun clickOnAnnouncementExtra(currentExtraItem: AccountsData)
}


