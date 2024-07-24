package com.bannking.app.network

import com.bannking.app.model.CommonResponseApi
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterFace {

    @Headers("Content-Type: application/json")
    @POST("appversion")
    fun appDetails(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("login")
    fun userLogin(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("sendotp")
    fun sendOtp(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("change_language")
    fun changeLanguage(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("change_currency")
    fun changeCurrency(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("register")
    fun registerUser(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("change_notification")
    fun changeNotification(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("change_password")
    fun changePassword(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("forgot")
    fun forgetUserNameAndPassword(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("language")
    fun getLanguageList(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("currency")
    fun getCurrencyList(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("privacy")
    fun getPrivacyText(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("budget_planner_list")
    fun getBudgetPlannerList(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("account_title")
    fun getAccountMenuTitle(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("delete_account")
    fun deleteAccount(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("create_own_account_title")
    fun createOwnAccountTitle(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("explore_expenses")
    fun exploreExpenses(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("account_list")
    fun accountList(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("transaction_list")
    fun transactionList(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("transection_type")
    fun transactionType(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("create_transaction_detail")
    fun createTransactionDetail(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("create_account")
    fun createAccount(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("profile")
    fun getProfile(@Body body: String): Call<JsonObject>


    @Multipart
    @POST("update_profile")
    fun updateProfile(
        @Part("security") security: RequestBody?,
        @Part("username") username: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("id") id: RequestBody?,
        @Part("token") token: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part image: MultipartBody.Part
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("notification")
    fun notification(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("schedule_transfer")
    fun scheduleTransfer(@Body body: String): Call<JsonObject>


    @Headers("Content-Type: application/json")
    @POST("pay")
    fun pay(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("delete_bank_account")
    fun deleteBankAccount(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("remove_header")
    fun removeHeader(@Body body: String): Call<CommonResponseApi>

    @Headers("Content-Type: application/json")
    @POST("header_title_list")
    fun headerTitleList(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("update_account")
    fun updateAccount(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("save_header")
    fun saveHeader(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("premium")
    fun getPremiumStatus(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("subscription")
    fun purchaseSubscription(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("androidSubscription")
    fun purchaseSubscriptionNew(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("create_own_budget_planner")
    fun createOwnBudgetPlanner(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("forgot_password")
    fun forgotPassword(@Body body: String): Call<JsonObject>
}