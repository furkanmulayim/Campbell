package com.campbell.campbell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.campbell.campbell.R
import com.campbell.campbell.model.Camp
import com.campbell.campbell.util.ProgressBarr
import com.campbell.campbell.util.downFromUrl
import com.campbell.campbell.view.FeedFragmentDirections

class CampAdapter(val campList: ArrayList<Camp>) :
    RecyclerView.Adapter<CampAdapter.CampViewHolder>() {

    class CampViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CampViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_camp, parent, false)
        return CampViewHolder(view)
    }

    override fun onBindViewHolder(holder: CampViewHolder, position: Int) {

        val textview4 = holder.itemView.findViewById<TextView>(R.id.textView4)
        val textview2 = holder.itemView.findViewById<TextView>(R.id.sehName)
        val imageView = holder.itemView.findViewById<ImageView>(R.id.imageView)

        textview2.text = campList[position].campCountryName
        textview4.text = campList[position].campAciklamasi

        holder.view.setOnClickListener {
            val act = FeedFragmentDirections.actionFeedFragmentToCampFragment(campList[position])
            Navigation.findNavController(it).navigate(act)
        }

        imageView.downFromUrl(campList[position].pngUrl,ProgressBarr(holder.view.context))
    }

    override fun getItemCount(): Int {
        return campList.size
    }

    fun updateCampList(newCampList: List<Camp>) {
        campList.clear()
        campList.addAll(newCampList)
        notifyDataSetChanged()
    }

}