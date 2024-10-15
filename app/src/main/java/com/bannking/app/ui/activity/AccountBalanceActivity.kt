package com.bannking.app.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.adapter.BankTranAdapter
import com.bannking.app.core.BaseActivity
import com.bannking.app.databinding.ActivityAccountBalanceBinding
import com.bannking.app.model.GetBankBalanceSpendResponse
import com.bannking.app.model.GetTransactionResponse
import com.bannking.app.model.NewTransaction
import com.bannking.app.model.viewModel.BankBalanceViewModel
import com.bannking.app.utils.SessionManager
import com.bannking.app.utils.SharedPref
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AccountBalanceActivity :
    BaseActivity<BankBalanceViewModel, ActivityAccountBalanceBinding?>(BankBalanceViewModel::class.java) {

    private lateinit var bankTranAdapter: BankTranAdapter
    private var institutionId = ""
    private var accountNumber = ""
    private var accountId = ""
    private var balance = 0
    private var accountName = ""
    private lateinit var viewModel: BankBalanceViewModel
    private lateinit var pref: SharedPref
    private var isLoading = false
    private var pageCount = 1

    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    private val debounceDelay: Long = 1200 // 1200 milliseconds delay

    override fun getBinding(): ActivityAccountBalanceBinding {
        return ActivityAccountBalanceBinding.inflate(layoutInflater)
    }

    override fun initialize() {
        institutionId = intent.getStringExtra("institutionId").toString()
        accountNumber = intent.getStringExtra("accountNumber").toString()
        accountId = intent.getStringExtra("accountId").toString()
        balance = intent.getIntExtra("balance", 0)
        accountName = intent.getStringExtra("accountName").toString()

    }

    override fun setMethod() {
        binding!!.apply {
            imgBack.setOnClickListener {
                finish()
            }
            ivSort.setOnClickListener {
                openDatePicker()
            }
            ivSyncTotalAcc.setOnClickListener {
                syncImageViewAnimation()
                hitApiToCheckAccount()
            }
            switchHide.setOnCheckedChangeListener { _, isChecked ->
                pref.saveBoolean("HideTransactions", isChecked)
                if (isChecked) {
                    tvHideTransactionMessage.visibility = View.GONE
                    rvBankTransaction.visibility = View.VISIBLE
                    hitApiForTransactionFilter("", pageCount, "")
                } else {
                    tvHideTransactionMessage.visibility = View.VISIBLE
                    rvBankTransaction.visibility = View.GONE
                }
            }


            svTransactionManual.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    searchRunnable?.let { handler.removeCallbacks(it) }

                    if (!newText.isNullOrEmpty()) {
                        searchRunnable = Runnable {
                            pageCount = 1
                            hitApiForTransactionFilter("", pageCount, newText)
                        }
                        handler.postDelayed(searchRunnable!!, debounceDelay)
                    }
                    return false
                }
            })

        }
        val hideTransactions = pref.getBoolean("HideTransactions")
        binding!!.switchHide.isChecked = hideTransactions

    }

    private fun syncImageViewAnimation() {
        val scaleX = ObjectAnimator.ofFloat(binding!!.ivSyncTotalAcc, "scaleX", 1f, 1.5f, 1f)
        val scaleY = ObjectAnimator.ofFloat(binding!!.ivSyncTotalAcc, "scaleY", 1f, 1.5f, 1f)

        val rotation = ObjectAnimator.ofFloat(binding!!.ivSyncTotalAcc, "rotation", 0f, 360f)

        scaleX.duration = 1000
        scaleY.duration = 1000
        rotation.duration = 1000

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(scaleX, scaleY, rotation)
        animatorSet.start()
    }

    private fun hitApiToCheckAccount() {
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)
        viewModel.getBankBalanceSpentApi(userToken, accountId, institutionId).observe(this) {
            val response = it as GetBankBalanceSpendResponse
            if (response.status == 200) {
                updateUi(
                    response.data.institutionName,
                    response.data.month,
                    response.data.currentMonthTotalSpending.toString()
                )
            }

        }
    }

    override fun observe() {
        viewModel.apply {
            progressObservable.observe(this@AccountBalanceActivity) {
                if (it != null) {
                    if (it) dialogClass.showLoadingDialog()
                    else dialogClass.hideLoadingDialog()
                }
            }
        }
    }

    override fun initViewModel(viewModel: BankBalanceViewModel) {
        this.viewModel = viewModel
        pref = SharedPref(this)
        institutionId = intent.getStringExtra("institutionId").toString()
        accountNumber = intent.getStringExtra("accountNumber").toString()
        accountId = intent.getStringExtra("accountId").toString()
        balance = intent.getIntExtra("balance", 0)
        accountName = intent.getStringExtra("accountName").toString()
        hitApiToCheckAccount()
        hitApiForTransactionFilter("", pageCount, "")


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun hitApiForTransactionFilter(formattedDate: String, pageCount: Int, filter: String) {
        val userToken = sessionManager.getString(SessionManager.USERTOKEN)

        viewModel.getBankTransactionFilterApi(
            userToken, accountId, institutionId, filter, formattedDate, pageCount.toString(), "10"
        ).observe(this) {
            val response = it as GetTransactionResponse
            if (response.status == 200) {
                if (pageCount == 1) {
                    isLoading = false
                    setAdapter(response.data.new_transactions)
                } else {
                    if (::bankTranAdapter.isInitialized) {
                        isLoading = false
                        bankTranAdapter.getAddList().addAll(response.data.new_transactions)
                        bankTranAdapter.notifyDataSetChanged()

                    } else {
                        isLoading = false
                        setAdapter(response.data.new_transactions)
                    }
                }
            }

        }
    }


    private fun openDatePicker() {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->

                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                setCurrentTime(calendar)

                val formattedDate = formatDateToISO8601(calendar.time)

                Log.e("formattedDate", formattedDate)

                pageCount = 1
                hitApiForTransactionFilter(formattedDate, pageCount, "")

            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.show()
    }


    private fun setCurrentTime(calendar: Calendar) {
        val currentTime = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY))
        calendar.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE))
        calendar.set(Calendar.SECOND, currentTime.get(Calendar.SECOND))
        calendar.set(Calendar.MILLISECOND, currentTime.get(Calendar.MILLISECOND))
    }

    private fun formatDateToISO8601(date: Date): String {
        val iso8601Format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        iso8601Format.timeZone = TimeZone.getTimeZone("UTC")
        return iso8601Format.format(date)
    }


    @SuppressLint("SetTextI18n")
    private fun updateUi(
        institutionName: String, month: String, currentMonthTotalSpending: String
    ) {
        binding!!.apply {
            tvBankNameData.text = institutionName
            tvSubBankTypeBalance.text = "$$balance"
            tvSpent.text = "Spent($month)"
            tvSpentBalance.text = "$$currentMonthTotalSpending"
            tvSccountTypeData.text = accountName
            tvSubBankType.text = "Total $accountName"
            tvAccountNumberValue.text = "****$accountNumber"
        }

    }

    @SuppressLint("SetTextI18n")
    private fun setAdapter(newTransactions: ArrayList<NewTransaction>) {
        val hideTransactions = pref.getBoolean("HideTransactions")
        Log.e("newTransactions", newTransactions.size.toString())
        if (newTransactions.size > 0) {
            if (hideTransactions) {
                binding!!.tvHideTransactionMessage.visibility = View.GONE
                binding!!.rvBankTransaction.visibility = View.VISIBLE
                bankTranAdapter = BankTranAdapter(this, newTransactions)
                binding!!.rvBankTransaction.adapter = bankTranAdapter

                // Setup scroll listener for pagination
                binding!!.rvBankTransaction.addOnScrollListener(object :
                    RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                        if (!isLoading && totalItemCount > visibleItemCount && (visibleItemCount + firstVisibleItemPosition >= totalItemCount && firstVisibleItemPosition >= 0)) {
                            loadMoreTransactions()
                        }
                    }
                })

            } else {
                binding!!.tvHideTransactionMessage.visibility = View.VISIBLE
                binding!!.rvBankTransaction.visibility = View.GONE
            }

        } else {
            binding!!.tvHideTransactionMessage.visibility = View.VISIBLE
            binding!!.tvHideTransactionMessage.text = "No transactions available for this date!"
            binding!!.rvBankTransaction.visibility = View.GONE
        }
    }

    private fun loadMoreTransactions() {
        isLoading = true
        // Simulate a network call or database fetch
        pageCount++
        Handler(Looper.getMainLooper()).postDelayed({
            hitApiForTransactionFilter("", pageCount, "")
        }, 1000)
    }

}