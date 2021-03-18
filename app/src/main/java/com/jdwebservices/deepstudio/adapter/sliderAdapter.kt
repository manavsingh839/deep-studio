package com.jdwebservices.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jdwebservices.data.slider
import com.jdwebservices.deepstudio.R
import com.jdwebservices.deepstudio.home
import com.jdwebservices.deepstudio.player
import kotlinx.android.synthetic.main.slider.view.*
import java.util.*

class sliderAdapter(var context: Context, var lis: ArrayList<slider>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.slider, parent, false)
        return ClientHolder(v)
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        /*  holder.itemView.setOnClickListener {

          ifs[position].category=="yes") {

                  val chk_id = lis[position].slider_image_id
                 val intent= Intent(holder.itemView.context, check_items::class.java)
                 intent.putExtra("chk_id",chk_id)
                 holder.itemView.context.startActivity(intent)
             }
             else{

             }


         }*/
        (holder as ClientHolder).bind(lis[position].slider_image, lis[position].category)
    }

    class ClientHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(slider_image: String, category: String) {
            //itemView.slider_id.id=slider_image_id
            // itemView.text_name.text = slider_image

            var web: String = itemView.context.resources.getString(R.string.main_link) + "../admin/slider/" + slider_image
            web = web.replace(" ", "%20")
            //  Picasso.get().load(web).into(itemView.slider_image)

// val url = if (web != null) "$web" else null //1
            val url = web //1
            Glide.with(itemView.context)  //2
                    .load(url) //3
                    .placeholder(R.mipmap.ic_launcher) //5
                    .error(R.mipmap.ic_launcher) //6
                    .fallback(R.mipmap.ic_launcher) //7
                    .into(itemView.slider_image) //8


            if (category != "") {
                itemView.slider_image.setOnClickListener {
                    val i = Intent(itemView.context, player::class.java)
                    i.putExtra("video_link", category)
                    itemView.context.startActivity(i)

//                    val intent = Intent(
//                            Intent.ACTION_VIEW,
//                            Uri.parse(category)
//                    )
//                    itemView.context.startActivity(intent)
                }
            }

        }
    }


}

