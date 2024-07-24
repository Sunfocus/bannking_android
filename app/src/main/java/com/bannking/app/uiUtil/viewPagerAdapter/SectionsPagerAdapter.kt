package com.bannking.app.uiUtil.viewPagerAdapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.bannking.app.model.retrofitResponseModel.headerModel.Data
import com.bannking.app.ui.fragment.tabFragment.TabOneFragment
import com.bannking.app.ui.fragment.tabFragment.TabTwoFragment

class SectionsPagerAdapter(
    fm: FragmentManager?,
    private val dataList: ArrayList<Data>
) : FragmentPagerAdapter(fm!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {


    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> return TabOneFragment()

            1 -> return TabTwoFragment()
        }
        return TabOneFragment()
    }

    override fun getPageTitle(position: Int): CharSequence {
        return if (dataList.size == 0) {
            ""
        } else {
            dataList[position].name.toString()
        }
    }

    override fun getCount(): Int {
        return if (dataList.size == 0) {
            0
        } else if (dataList.size > 2) {
            2
        } else
            dataList.size
    }
}