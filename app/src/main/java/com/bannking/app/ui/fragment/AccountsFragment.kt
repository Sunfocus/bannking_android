package com.bannking.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bannking.app.databinding.FragmentAccountsBinding
import com.bannking.app.ui.activity.AccountMenuNewActivity
import com.bannking.app.ui.activity.MainActivity
import com.bannking.app.utils.AdController
import com.bannking.app.utils.DialogClass
import com.bannking.app.utils.SessionManager

class AccountsFragment : Fragment() {
    private var mParam1: String? = null
    private var mParam2: String? = null
    var mBinding: FragmentAccountsBinding? = null
    lateinit var dialogClass: DialogClass
    private lateinit var savedAdTime: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = requireArguments().getString(ARG_PARAM1)
            mParam2 = requireArguments().getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentAccountsBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialogClass = DialogClass(requireActivity())
        savedAdTime = SessionManager(requireActivity(), SessionManager.ADTIME)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        val adTime = savedAdTime.getLong(SessionManager.ADTIME)
        val fiveMinutesInMillis = 5 * 60 * 1000 // 5 minutes in milliseconds
        val currentTime = System.currentTimeMillis()
        // Calculate the time difference
        val timeDifference = currentTime - adTime

        mBinding!!.btnCreateNewAccount.setOnClickListener {
            if (timeDifference > fiveMinutesInMillis) {
                AdController.showInterAd(requireActivity(), null, 0) {
                    val intent = Intent(requireActivity(), AccountMenuNewActivity::class.java)
                    (requireActivity() as MainActivity).resultLauncher.launch(intent)
                }
            }else{
                val intent = Intent(requireActivity(), AccountMenuNewActivity::class.java)
                (requireActivity() as MainActivity).resultLauncher.launch(intent)
            }


        }
        mBinding?.txtInformation?.setOnClickListener {
            dialogClass.showInformMotionDialog()
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"
    }
}