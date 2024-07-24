package com.bannking.app.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.bannking.app.utils.DialogClass
import com.bannking.app.utils.SessionManager
import com.bannking.app.utils.Utils
import com.google.gson.Gson

abstract class BaseFragment<VM : BaseViewModel, Binding : ViewBinding>(private val mViewModelClass: Class<VM>) :
    Fragment() {

    lateinit var viewModel: VM
    open lateinit var mBinding: Binding
    var gson: Gson = Gson()
    var utils: Utils = Utils()
    lateinit var sessionManager: SessionManager
    lateinit var dialogClass: DialogClass


    open fun init() {}


//    private fun getViewM(): VM = ViewModelProviders.of(requireActivity())[mViewModelClass]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        viewModel = getViewM()
        viewModel = getViewModelClass()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = getBinding(inflater, container!!)
        super.onCreateView(inflater, container, savedInstanceState)
        dialogClass = DialogClass(requireActivity())
        sessionManager = SessionManager(requireActivity(), SessionManager.mySharedPref)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCreated()
        observer()
    }

    protected abstract fun getBinding(inflater: LayoutInflater, container: ViewGroup): Binding

    protected abstract fun getViewModelClass(): VM

    abstract fun viewCreated()

    abstract fun observer()

    open fun refresh() {}

}