package com.bannking.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bannking.app.R
import com.bannking.app.UiExtension
import com.bannking.app.UiExtension.drawable
import com.bannking.app.model.retrofitResponseModel.accountMenuTitleModel.Data
import com.bannking.app.ui.activity.AccountMenuNewActivity
import com.bannking.app.utils.Constant
import com.bannking.app.utils.DialogClass
import com.bannking.app.utils.SessionManager

class AccountMenuNewAdapter : RecyclerView.Adapter<AccountMenuNewAdapter.ViewHolder> {
    private var context: Context? = null
    private var list: ArrayList<Data>? = null

    //    private var mBinding: ItemAccountMenuBinding? = null
    private var dialogClass: DialogClass? = null
    private var savedHeaderList: ArrayList<Data>? = null
    private var adapterNew: AccountMenuWithSavedAdapterNew? = null
    private var secondCardPosition = -1

    private lateinit var currentTab: SessionManager
    constructor()

    constructor(
        context: Context?,
        list: ArrayList<Data>?,
        dialogClass: DialogClass,
        savedHeaderlist: ArrayList<Data>,
        adapterNew: AccountMenuWithSavedAdapterNew?
    ) {
        this.context = context
        this.dialogClass = dialogClass
        this.list = list
        this.savedHeaderList = savedHeaderlist
        this.adapterNew = adapterNew

    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateList(list: ArrayList<Data>) {
        this.list?.clear()
        this.list = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountMenuNewAdapter.ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_account_menu, parent, false)
        )
    }
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
//
//        return ViewHolder(
//            ItemAccountMenuBinding.inflate(
//                LayoutInflater.from(context),
//                parent,
//                false
//            )
//        )
//    }

    @SuppressLint("UseCompatLoadingForDrawables", "NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val model = list!![position]

        currentTab = SessionManager(context!!, SessionManager.currentTab)
        Log.d("sdfdsfsdfsd",savedHeaderList.toString())

        holder.txtTitle.text = list!![position].name

        if (UiExtension.isDarkModeEnabled()) {
            holder.txtTitle.setTextColor(ContextCompat.getColor(context!!, R.color.white))
            if (model.isSelected) {
                holder.tvSelected.visibility = View.VISIBLE
                holder.cvMenu.background = context!!.drawable(R.drawable.drawable_selected_night)
            } else {
                holder.tvSelected.visibility = View.INVISIBLE
                holder.cvMenu.background = context!!.drawable(
                    R.drawable.drawable_unselected_night
                )
            }
        } else {
            holder.txtTitle.setTextColor(ContextCompat.getColor(context!!, R.color.clr_text_blu))
            if (model.isSelected) {
                holder.tvSelected.visibility = View.VISIBLE
                holder.cvMenu.background = context!!.drawable(R.drawable.drawable_selected)
            } else {
                holder.tvSelected.visibility = View.INVISIBLE
                holder.cvMenu.background = context!!.drawable(
                    R.drawable.drawable_unselected
                )
            }
        }



        holder.cvMenu.setOnClickListener {
            currentTab.setBoolean(SessionManager.clickAccountType,true)
            if (savedHeaderList!!.size < 2) {
                if (model.isSelected) {
                    val name = model.name
                    val item = savedHeaderList?.find { it.name == name }
                    val index = savedHeaderList?.indexOf(item)
                    if (savedHeaderList?.get(index!!)?.isAccountCreated == 0) {
/*                        model.isSelected = false
                        index?.let {
                            when (it) {
                                0 -> {
                                    savedHeaderlist?.remove(savedHeaderlist?.get(0))
                                    adapterNew?.notifyItemRemoved(0)
                                }
                                1 -> {
                                    savedHeaderlist?.remove(savedHeaderlist?.get(1))
                                    adapterNew?.notifyItemRemoved(1)
                                }
                                else -> {}
                            }
                        }*/
                    } else {
                        (context as AccountMenuNewActivity).dialogClass.showError((context as AccountMenuNewActivity).getString(R.string.str_account_created))
                    }

                } else {
                    if (savedHeaderList.isNullOrEmpty()) {
                        model.isSelected = true
                        savedHeaderList!!.add(Data(id = model.id, name = model.name?.uppercase(), type = "1", isTitleMenuHasAccount = model.isTitleMenuHasAccount))
                        adapterNew?.notifyItemInserted(adapterNew!!.itemCount - 1)
                    } else {
                        if (savedHeaderList!![0].isAccountCreated == 0) {
                            clearAllSelection()
                            savedHeaderList!!.clear()
                            savedHeaderList!!.add(Data(id = model.id, name = model.name?.uppercase(), type = "1", isTitleMenuHasAccount = model.isTitleMenuHasAccount))
                            adapterNew?.notifyDataSetChanged()
                        } else {
                            secondCardPosition = position
                            savedHeaderList!!.add(Data(id = model.id, name = model.name?.uppercase(), type = "1", isTitleMenuHasAccount = model.isTitleMenuHasAccount))
                            adapterNew?.notifyItemInserted(adapterNew!!.itemCount - 1)
                        }
                        model.isSelected = true
                    }
                }
                notifyDataSetChanged()
                (context as AccountMenuNewActivity).checkSavedHeaderList()
            } else {
                if (savedHeaderList!![0].isAccountCreated ==1  && savedHeaderList!![1].isAccountCreated ==0 && !model.isSelected) {
                    savedHeaderList?.removeAt(1)
                    savedHeaderList!!.add(1, Data(id = model.id, name = model.name?.uppercase(), type = "1", isTitleMenuHasAccount = model.isTitleMenuHasAccount))
                    list!![secondCardPosition].isSelected = false
                    model.isSelected = true
                    adapterNew?.notifyItemChanged(1)
                    notifyDataSetChanged()
                    secondCardPosition = position
                } else {
                    if (model.isSelected) {
                        val name = model.name
                        val item = savedHeaderList?.find { it.name!!.equals(name!!, ignoreCase = true) }
                        val index = savedHeaderList?.indexOf(item)
                        if (savedHeaderList?.get(index!!)?.isAccountCreated == 1) {
                            (context as AccountMenuNewActivity).dialogClass.showError((context as AccountMenuNewActivity).getString(R.string.str_account_created))
                        }
                    } else {
                        currentTab.setBoolean(SessionManager.clickAccountType,false)
                        /*                     if (!savedHeaderlist!![0].isAccountCreated && !savedHeaderlist!![1].isAccountCreated && !model.isSelected) {
                                                 clearAllSelection()
                                                 savedHeaderlist!!.clear()
                                                 savedHeaderlist!!.add(Data(id = model.id, name = model.name?.uppercase(), type = "1", isTitleMenuHasAccount = model.isTitleMenuHasAccount))
                                                 adapterNew?.notifyDataSetChanged()
                                                 model.isSelected = true
                                                 notifyDataSetChanged()
                                             } else {
                                                 (context as AccountMenuNewActivity).dialogClass.showError((context as AccountMenuNewActivity).getString(R.string.str_select_up_to_two))

                                             }*/
                        (context as AccountMenuNewActivity).dialogClass.showError((context as AccountMenuNewActivity).getString(R.string.str_select_up_to_two))
                    }
                }


            }

/*                model.isSelected = false
                val name = model.name
                val item = savedHeaderlist?.find { it.name == name }
                val index = savedHeaderlist?.indexOf(item)
                index?.let {
                    when (it) {
                        0 -> {
                            savedHeaderlist?.remove(savedHeaderlist?.get(0))
                            adapterNew?.notifyItemRemoved(0)
                        }
                        1 -> {
                            savedHeaderlist?.remove(savedHeaderlist?.get(1))
                            adapterNew?.notifyItemRemoved(1)
                        }
                        else -> {}
                    }
                }*/

        }
    }

    //            if (model.isSelected) {
//                model.isSelected = false
//                savedHeaderlist?.clear()
//                adapterNew?.notifyDataSetChanged()
//                notifyDataSetChanged()
//            } else {
//                clearAllSelection()
//                model.isSelected = true
//
//                savedHeaderlist?.clear()
//                savedHeaderlist!!.add(0, Data(model.id, model.name?.uppercase(), "1"))
//                adapterNew?.notifyDataSetChanged()
//                notifyDataSetChanged()
//            }

    private fun clearAllSelection() {
        list?.forEach {
            if (it.isSelected) {
                it.isSelected = false
            }
        }
    }

    fun removeMenuTitle(menuName: String) {
        list?.forEach {
            if (it.isSelected && it.name.equals(menuName, true)) {
                it.isSelected = false
                notifyDataSetChanged()
            }
        }
    }


    private val selectedItem: List<Data>
        get() {
            val returnList: MutableList<Data> = ArrayList()
            for (data in list!!) {
                if (data.isSelected) {
                    returnList.add(data)
                }
            }
            return returnList
        }

    override fun getItemCount(): Int {
        return if (list == null) 0 else list!!.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

/*    inner class ViewHolder(itemView: ItemAccountMenuBinding) :
        RecyclerView.ViewHolder(itemView.root) {
        init {
            setIsRecyclable(false)
            mBinding = itemView
        }
    }*/

    inner class ViewHolder(ItemView: View) : RecyclerView.ViewHolder(ItemView) {
        val txtTitle: TextView = itemView.findViewById(R.id.txt_title)
        val tvSelected: TextView = itemView.findViewById(R.id.tvSelected)
        val cvMenu: CardView = itemView.findViewById(R.id.cvMenu)

//        val btnCreate: AppCompatButton = itemView.findViewById(R.id.btn_create)
    }

//    @SuppressLint("NotifyDataSetChanged")
//    private fun addItem() {
//        list!!.add(Data("-1", "Create Own", "-1", false))
//        notifyDataSetChanged()
//    }

}