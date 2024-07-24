package com.bannking.app.uiUtil.viewPagerAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bannking.app.R
import com.bannking.app.model.UpgradeSliderModel
import com.bannking.app.uiUtil.viewPagerAdapter.SliderAdapter.SliderViewHolder
import com.bannking.app.utils.OnClickListenerViewPager

class SliderAdapter(
    upgradeSliderItems: MutableList<UpgradeSliderModel>,
    viewPager2: ViewPager2,
    private val listener: OnClickListenerViewPager
) : RecyclerView.Adapter<SliderViewHolder>() {
    private val upgradeSliderItems: List<UpgradeSliderModel>
    private val viewPager2: ViewPager2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        return SliderViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_upgrade, parent, false)
        )
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.setImage(upgradeSliderItems[position])

        holder.itemView.setOnClickListener {
            listener.onClickListener(upgradeSliderItems[position].purchaseId)
        }
        //        if (position == sliderItems.size()- 2){
//            viewPager2.post(runnable);
//        }
    }

    override fun getItemCount(): Int {
        return upgradeSliderItems.size
    }

    inner class SliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView
        fun setImage(upgradeSliderModel: UpgradeSliderModel) {
            imageView.setImageResource(upgradeSliderModel.image)
        }

        init {
            imageView = itemView.findViewById(R.id.imageSlide)
        }
    }

    init {
        this.upgradeSliderItems = upgradeSliderItems
        this.viewPager2 = viewPager2
    }
}