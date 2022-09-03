package com.example.campbell.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.campbell.R
import com.example.campbell.model.Comment

class CommentRcyclerAdapter(val commentList: ArrayList<Comment>) :
    RecyclerView.Adapter<CommentRcyclerAdapter.CommentHolder>() {

    class CommentHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_comment, parent, false)
        return CommentHolder(view)
    }

    override fun getItemCount(): Int {
        return commentList.size
    }

    override fun onBindViewHolder(holder: CommentHolder, position: Int) {

        var kullName = holder.itemView.findViewById<TextView>(R.id.kullName)
        var kullMail = holder.itemView.findViewById<TextView>(R.id.kullMail)
        var kullComment = holder.itemView.findViewById<TextView>(R.id.kullComment)
        var kullCommPuan = holder.itemView.findViewById<TextView>(R.id.kullCommPuan)


        kullName.text = commentList[position].kullaniciAdiSoyadi
        kullMail.text = commentList[position].kullaniciMail
        kullComment.text = commentList[position].kullaniciComment
        kullCommPuan.text = commentList[position].kullaniciBegeni


    }


}