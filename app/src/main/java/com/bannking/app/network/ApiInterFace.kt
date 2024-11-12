package com.bannking.app.network

import com.bannking.app.model.*
import com.bannking.app.model.retrofitResponseModel.soundModel.SoundResponse
import com.bannking.app.model.retrofitResponseModel.tranSectionListModel.TranSectionListModel
import com.bannking.app.model.retrofitResponseModel.userModel.UserModel
import com.bannking.app.utils.Constants
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*


interface ApiInterFace {

    @Headers("Content-Type: application/json")
    @POST("user/appversion")
    fun appDetails(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/login")
    fun userLogin(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/sendotp")
    fun sendOtp(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/loginWithSocialMedia")
    fun loginWithSocial(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/sendOtpForForgotPassword")
    fun sendOtpForget(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("user/updateExtraUserFieldData")
    fun changeLanguage(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("user/update_currency")
    fun changeCurrency(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("/user/updateUserVoiceData")
    fun updateVoiceMakerData(
        @Body body: JsonObject, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/register")
    fun registerUser(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/change_notification")
    fun changeNotification(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/changePassword")
    fun changePassword(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    //Na
    @Headers("Content-Type: application/json")
    @POST("user/forgot")
    fun forgetUserNameAndPassword(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/listLanguage")
    fun getLanguageList(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/listCurrency")
    fun getCurrencyList(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/privacy")
    fun getPrivacyText(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/listBudgetPlanner")
    fun getBudgetPlannerList(
        @Query("acc_title_id") SelectedItemMenu: String,
        @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/listAccountTitles")
    fun getAccountMenuTitle(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @DELETE("user/deleteUserAccount")
    fun deleteAccount(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/createAccountTitle")
    fun createOwnAccountTitle(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/explore_expenses")
    fun exploreExpenses(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/account_list")
    fun accountList(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/transaction_list")
    fun transactionList(
        @Query("account_id") accountID: String,
        @Query("searchText") filters: String,
        @Query("dateFilter") sortDate: String,
        @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/transaction_list")
    fun transactionListLive(
        @Query("account_id") accountID: String,
        @Query("searchText") filters: String,
        @Query("dateFilter") sortDate: String,
        @Header(Constants.AUTHORIZATION) token: String
    ): Call<TranSectionListModel>

    //na
    @Headers("Content-Type: application/json")
    @POST("user/transection_type")
    fun transactionType(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/create_transaction_detail")
    fun createTransactionDetail(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/create_account")
    fun createAccount(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/createSubBudgetPlanner")
    fun createSubBudgetAccount(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/profile")
    fun getProfile(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/profile")
    fun getProfileLive(@Header(Constants.AUTHORIZATION) token: String): Call<UserModel>


    @Multipart
    @PATCH("user/update_profile")
    fun updateProfile(
        @Part("security") security: RequestBody?,
        @Part("username") username: RequestBody?,
        @Part("email") email: RequestBody?,
        @Part("id") id: RequestBody?,
        @Part("token") token: RequestBody?,
        @Part("name") name: RequestBody?,
        @Part("face_id_status") faceIdStatus: RequestBody?,
        @Part("deleteOldImageUrl") oldImageUrl: RequestBody?,
        @Part image: MultipartBody.Part,
        @Header(Constants.AUTHORIZATION) tokens: String
    ): Call<JsonObject>


    @Multipart
    @PATCH("user/update_profile")
    fun updateNotification(
        @Part("notification_status") notificationStatus: RequestBody?,
        @Header(Constants.AUTHORIZATION) tokens: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @GET("user/listNotification")
    fun notification(@Header(Constants.AUTHORIZATION) tokens: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/schedule_transfer")
    fun scheduleTransfer(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/resentVerificationToken")
    fun postEmailResend(@Body body: String): Call<JsonObject>


    @Headers("Content-Type: application/json")
    @POST("user/schedule_transfer")
    fun pay(@Body body: String, @Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @DELETE("user/delete_account")
    fun deleteBankAccount(
        @Query("accountId") accountID: String,
        @Query("type") type: String,
        @Query("budgetId") budget_id: String,
        @Query("accountTitleId") accountTitleId: String,
        @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    //Na
    @Headers("Content-Type: application/json")
    @POST("user/remove_header")
    fun removeHeader(@Body body: String): Call<CommonResponseApi>

    @Headers("Content-Type: application/json")
    @GET("user/header_title_list")
    fun headerTitleList(@Header(Constants.AUTHORIZATION) token: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @PATCH("user/update_account")
    fun updateAccount(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    //Na
    @Headers("Content-Type: application/json")
    @POST("user/save_header")
    fun saveHeader(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/premium")
    fun getPremiumStatus(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("subs/subscription")
    fun purchaseSubscription(@Body body: String): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("subs/androidInitialPurchase")
    fun purchaseSubscriptionNew(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("subs/purchaseLifeTimePlan")
    fun purchaseSubscriptionLifeTime(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/createBudgetPlanner")
    fun createOwnBudgetPlanner(
        @Body body: String, @Header(Constants.AUTHORIZATION) token: String
    ): Call<JsonObject>

    @Headers("Content-Type: application/json")
    @POST("user/forgot_password")
    fun forgotPassword(@Body body: String): Call<JsonObject>


    @POST("/voice/list")
    fun getVoiceMaker(@Body body: JsonObject): Call<JsonObject>

    @POST("/voice/list")
    fun getVoiceMakerWithLive(@Body body: JsonObject): Call<SoundResponse>

    @POST("/voice/api")
    fun postVoiceMaker(@Body body: JsonObject): Call<JsonObject>

    @POST("/voice/api")
    fun postVoiceMakerLiveOb(@Body body: JsonObject): Call<PostVoiceResponse>

    @GET("/api/user/accounts")
    fun getBankDetails(@Header(Constants.AUTHORIZATION) token: String): Call<GetBankListResponse>

    @POST("/api/link/token/create")
    fun getTokenForAddBank(@Header(Constants.AUTHORIZATION) token: String): Call<GetBankLinkTokenResponse>

    @PATCH("/user/updateAccountTitle")
    fun updateHeaderTitle(
        @Header(Constants.AUTHORIZATION) token: String,
        @Body jsonData: JsonObject
    ): Call<CustomHeaderResponse>

    @DELETE("/user/deleteAccountTitle")
    fun deleteHeaderTitle(
        @Header(Constants.AUTHORIZATION) token: String,
        @Query("accountTitleId")accountTitleId:String
    ): Call<DeleteBankResponse>

    @POST("/api/exchange_token")
    fun exchangeTokenForAddBank(
        @Header(Constants.AUTHORIZATION) token: String, @Body exchangeToken: JsonObject
    ): Call<ExchangeTokenResponse>

    @POST("api/hide-plaid-bank-account")
    fun hideBankAccount(
        @Header(Constants.AUTHORIZATION) token: String, @Body hideBankObject: JsonObject
    ): Call<HideBankResponse>

 @POST("api/unhide-all-plaid-bank-account")
    fun showAllBankAccount(
        @Header(Constants.AUTHORIZATION) token: String, @Body hideBankObject: JsonObject
    ): Call<HideBankResponse>


    @GET("/api/get-user-month-spending")
    fun getBankBalanceAndSpend(
        @Query("account_id") account_id: String,
        @Query("institutionId") institutionId: String,
        @Header(Constants.AUTHORIZATION) token: String
    ): Call<GetBankBalanceSpendResponse>

    @DELETE("/api/unlink-bank-account")
    fun removeBankPlaid(
        @Query("institution_id") institutionId: String,
        @Header(Constants.AUTHORIZATION) token: String
    ): Call<DeleteBankResponse>

    @GET("/api/get-user-transactions-with-filters")
    fun getTransactionFilter(
        @Query("searchText") filters: String,
        @Query("sortDate") sortDate: String,
        @Query("page") page: String,
        @Query("limit") limit: String,
        @Query("account_id") account_id: String,
        @Query("institution_id") institutionId: String,
        @Header(Constants.AUTHORIZATION) token: String
    ): Call<GetTransactionResponse>
}