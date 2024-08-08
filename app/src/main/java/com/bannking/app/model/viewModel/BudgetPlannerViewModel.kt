package com.bannking.app.model.viewModel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.bannking.app.UiExtension.FCM_TOKEN
import com.bannking.app.core.BaseActivity
import com.bannking.app.core.BaseViewModel
import com.bannking.app.core.CommonResponseModel
import com.bannking.app.network.RetrofitClient
import com.bannking.app.utils.Constants
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BudgetPlannerViewModel(val App: Application) : BaseViewModel(App) {

    var budgetPlannerList: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var createAccountListData: MutableLiveData<CommonResponseModel> = MutableLiveData(null)
    var progressObservable: MutableLiveData<Boolean> = MutableLiveData(null)

    fun setDataInBudgetPlannerList(id: Int?, SelectedItemMenu: String,userToken:String) {
        progressObservable.value = true
        val apiBody = JsonObject()

        try {
            apiBody.addProperty("security", Constants.SECURITY_0)
            apiBody.addProperty("token", App.FCM_TOKEN)
            apiBody.addProperty("menuTitleId", SelectedItemMenu)
            apiBody.addProperty("id", id)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val call = RetrofitClient.instance!!.myApi.getBudgetPlannerList(SelectedItemMenu,userToken)
        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                progressObservable.value = false
                if (response.isSuccessful) {
                    if (response.code() in 199..299) {
                        budgetPlannerList.value =
                            CommonResponseModel(response.body(), response.code())
                    } else if (response.code() in 400..500) {
                        assert(response.errorBody() != null)
                        val errorBody = response.errorBody().toString()
                        val jsonObject: JsonObject = JsonParser.parseString(errorBody).asJsonObject
                        budgetPlannerList.value = CommonResponseModel(jsonObject, response.code())
                    }
                } else budgetPlannerList.value = CommonResponseModel(null, 500)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                progressObservable.value = false
                budgetPlannerList.value = CommonResponseModel(null, 500)
            }
        })
    }


    fun setDataInCreateAccountListData(
        accMenuId: String, budgetId: String, account: String, accountCode: String, amount: String,userToke:String
    ) {
        App.FCM_TOKEN.let {
            progressObservable.value = true
            val apiBody = JsonObject()

            try {
                apiBody.addProperty("security", Constants.SECURITY_0)
                apiBody.addProperty("id", BaseActivity.userModel!!.id)
                apiBody.addProperty("token", it)
                apiBody.addProperty("acc_menu_id", accMenuId)
                apiBody.addProperty("budget_id", budgetId)
                apiBody.addProperty("account", account)
                apiBody.addProperty("account_code", accountCode)
                apiBody.addProperty("amount", amount)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val call = RetrofitClient.instance!!.myApi.createAccount(apiBody.toString(),userToke)
            call.enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    progressObservable.value = false
                    if (response.isSuccessful) {
                        if (response.code() in 199..299) {
                            createAccountListData.value =
                                CommonResponseModel(response.body(), response.code())
                        } else if (response.code() in 400..500) {
                            assert(response.errorBody() != null)
                            val errorBody = response.errorBody().toString()
                            val jsonObject: JsonObject =
                                JsonParser.parseString(errorBody).asJsonObject
                            createAccountListData.value =
                                CommonResponseModel(jsonObject, response.code())
                        }
                    } else createAccountListData.value = CommonResponseModel(null, 500)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    progressObservable.value = false
                    createAccountListData.value = CommonResponseModel(null, 500)
                }
            })
        }
    }
}