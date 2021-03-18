@file:Suppress("DEPRECATION")

package com.jdwebservices.deepstudio.adapter

import android.Manifest
import android.app.Dialog
import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jdwebservices.deepstudio.R
import com.jdwebservices.deepstudio.data.category_data
import com.jdwebservices.deepstudio.gallery
import kotlinx.android.synthetic.main.category_cardview_row.view.*
import java.util.*


class category_adapter(var context: Context, var lis: ArrayList<category_data>): RecyclerView.Adapter<RecyclerView.ViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val v: View = LayoutInflater.from(context).inflate(R.layout.category_cardview_row, parent, false)
        return ClientHolder(v)
    }

    override fun getItemCount(): Int {
        return lis.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener {

          val categoryId = lis[position].category_id
          val categoryDescription = lis[position].categoryDescription
          val categoryName = lis[position].categoryName
            if(categoryDescription == "1") {
                val intent = Intent(holder.itemView.context, gallery::class.java)
                intent.putExtra("album_id", categoryId)
                intent.putExtra("album_name", categoryName)
                holder.itemView.context.startActivity(intent)
            }



        }
        (holder as ClientHolder).bind(
                lis[position].categoryName,
                lis[position].categoryicon,
                lis[position].categoryDescription
        )
    }

    class ClientHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    {
        fun bind(
                categoryName: String,
                categoryicon: String,
                categoryDescription: String
        )
        {
           // itemView.catgory_id.id=category_id
             itemView.category_title.text = categoryName
            var web: String = when (categoryDescription) {
                "1" -> {
                    itemView.context.resources.getString(R.string.main_link) + "../admin/album/" + categoryicon
                }
                "0" -> {
                    itemView.context.resources.getString(R.string.main_link) + "../admin/album/" + categoryicon
                }
                "" -> {
                    itemView.context.resources.getString(R.string.main_link) + "../admin/album/" + categoryicon
                }
                else -> {
                    itemView.context.resources.getString(R.string.main_link) + "../admin/uploads/" + categoryicon
                }
            }
            web=web.replace(" ", "%20")

            val url = web //1
            Glide.with(itemView.context)  //2
                .load(url) //3
                .placeholder(R.mipmap.ic_launcher) //5
                .error(R.mipmap.ic_launcher) //6
                .fallback(R.mipmap.ic_launcher) //7
                .into(itemView.catgory_img) //8

            itemView.shimmer.visibility = View.GONE
            itemView.main.visibility = View.VISIBLE

            if(categoryDescription != "" || categoryDescription != "0") {
                val nagDialog = Dialog(
                        itemView.context,
                        android.R.style.Theme_Translucent_NoTitleBar_Fullscreen
                )
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                nagDialog.setCancelable(false)
                nagDialog.setContentView(R.layout.preview_image)
                // val btnClose = nagDialog.findViewById(R.id.btnIvClose) as Button
                val ivPreview = nagDialog.findViewById(R.id.iv_preview_image) as ImageView
                val downloadtext = nagDialog.findViewById(R.id.download) as ImageButton
                val closetext = nagDialog.findViewById(R.id.close_btn) as ImageButton
                var ebss: String = if(categoryDescription != "" || categoryDescription != "0") {
                    itemView.context.resources.getString(R.string.main_link) + "../admin/uploads/" + categoryicon
                }else{
                    itemView.context.resources.getString(R.string.main_link) + "../admin/album/" + categoryicon
                }
                ebss = ebss.replace(" ", "%20")
                val urll = ebss //1
                Glide.with(itemView.context)  //2
                    .load(urll) //3
                    .placeholder(R.mipmap.ic_launcher) //5
                    .error(R.mipmap.ic_launcher) //6
                    .fallback(R.mipmap.ic_launcher) //7
                    .into(ivPreview)
                downloadtext.setOnClickListener {


                    if (ActivityCompat.checkSelfPermission(itemView.context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(itemView.context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    {
                        Toast.makeText(itemView.context,"Please Allow Permissions",Toast.LENGTH_LONG).show()
                    }
                    else {


                        // we are going to call Download async task to begin our download

                        val request = DownloadManager.Request(Uri.parse(ebss))
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                        request.setTitle(categoryDescription)
                        request.setDescription("The file is downloading...")

                        request.allowScanningByMediaScanner()
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        request.setDestinationInExternalPublicDir(
                                Environment.DIRECTORY_DOWNLOADS,
                                "${System.currentTimeMillis()}.jpg"
                        )

                        val manager = itemView.context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                        manager.enqueue(request)

                    }
                    // task.execute("http://alex.smola.org/drafts/thebook.pdf");
                    }

                itemView.catgory_img.setOnClickListener {

                    // Picasso.get().load(ebs).resize(250,250).centerInside().into(ivPreview)
                    //ivPreview.setBackgroundDrawable()
                    closetext.setOnClickListener {
                        nagDialog.dismiss()
                    }
                    nagDialog.show()
                }
            }
          /*  itemView.GlideApp.with(itemView.context)
                .load(web)
                .placeholder(R.mipmap.ic_launcher)
                .fitCenter()
                .into(itemView.catgory_img);

            val requestOptions = RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .signature(ObjectKey(signature))
                .override(100, 100) // resize does not respect aspect ratio

            Glide.with(context).load(url).apply(requestOptions).into(imageView)*/

          //  Picasso.get().load(web).resize(250,250).centerInside().into(itemView.catgory_img)

        }


    }



}

