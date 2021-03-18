package com.jdwebservices.deepstudio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jdwebservices.deepstudio.adapter.category_adapter
import com.jdwebservices.deepstudio.data.category_data
import com.mycity.client_login_save
import kotlinx.android.synthetic.main.activity_gallery.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

class gallery : AppCompatActivity() {
    private lateinit var session: client_login_save
    private var userId = ""
    private var userName = ""
    private var userEmail = ""
    private var recyclerViewAdapter: category_adapter? = null
    private var postRespone: JSONArray? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        session = client_login_save(this)
        val receivedText = intent.getStringExtra("album_name")
        val albumId = intent.getIntExtra("album_id", 0)
        if (session.isLoggedIn()) {

            val user: HashMap<String, ArrayList<String>> = session.getUserDetails()
            val patient = user["user"]!!
            userId = patient[0]
            userName = patient[1]
            userEmail = patient[2]
        }
            // Log.d("user data", Patient.toString())
            if (receivedText != null || receivedText != "") {
                category_title.text = receivedText
            }
        albumData(userId, albumId.toString())
            //initScrollListener()

            // getOrd()
            //  repeatcall(userId, usertoken)
            //   userdata = //JsonObject(Patient.get(3))

        back.setOnClickListener {

            val i = Intent(this, home::class.java)
            startActivity(i)
            finish()

        }



    }
    fun albumData(user_id:String,album:String)
    {


        val personListsCompleted = ArrayList<category_data>()

        val sharedPreferencess = getSharedPreferences("gallery", MODE_PRIVATE)
        val gsons = Gson()
        val json = sharedPreferencess.getString("Set", "")
        if (json!!.isEmpty()) {
            //   Toast.makeText(this@SeeResult, "There is something error", Toast.LENGTH_LONG).show()
        } else {
            val type = object : TypeToken<ArrayList<category_data>>() {}.type
            val arrPackageData = gsons.fromJson<ArrayList<category_data>>(json, type)
            for (data in arrPackageData) {
                personListsCompleted.add(
                    category_data(
                        data.category_id,
                        data.categoryName,
                        data.categoryicon,
                        data.categoryDescription
                    )
                )

            }

            val catAdp = category_adapter(this, personListsCompleted)
            category_view_id.layoutManager = GridLayoutManager(this, 3)
            category_view_id.adapter = catAdp

            category_view_id.visibility = View.VISIBLE

        }
        val catUrl1 = "gallery.php?user=$user_id&album=$album"
        Log.d("url", catUrl1)
        val category = ArrayList<category_data>()

        val sharedPreferences = getSharedPreferences("gallery", MODE_PRIVATE)
        val gson = Gson()

        val catRq1: RequestQueue = Volley.newRequestQueue(this)
        val catJar1 = JsonArrayRequest(
            Request.Method.GET,
            resources.getString(R.string.main_link) + catUrl1,
            null,
            { response ->
                postRespone = response
                if (response.length() == 0) {
                    // no_result.visibility = View.VISIBLE
                    category_view_id.visibility = View.GONE
                    //  progress.visibility = View.GONE
                    //  nodata.visibility = View.VISIBLE
                } else {
                    category_view_id.visibility = View.VISIBLE
                    //   nodata.visibility = View.GONE
                }

                for (x in 0 until response.length())
                    category.add(
                        category_data(
                            response.getJSONObject(x).getInt("image_id"),
                            "",
                            response.getJSONObject(x).getString("image"),
                            response.getJSONObject(x).getString("created_at")
                        )
                    )


                val jsons = gson.toJson(category)
                //  Log.d("TAG", "jsonCars = " + jsons)
                val editor = sharedPreferences.edit()
                editor.putString("Set", jsons)
                editor.apply()
            //    val jsonCars = gson.toJson(category)
                // Log.d("TAG", "jsonCars = " + jsonCars)

                val catAdp = category_adapter(this, category)
                recyclerViewAdapter = catAdp
                category_view_id.layoutManager = GridLayoutManager(this, 3)
                category_view_id.adapter = catAdp

                category_view_id.visibility = View.VISIBLE
                //shimmers.visibility = View.GONE
                // progress.visibility = View.GONE

            },
            {
                category_view_id.visibility = View.VISIBLE
                //  progress.visibility = View.GONE

                /*val builder = AlertDialog.Builder(this)
                builder.setMessage("Please check Your Internet Connection\n")
                val dialog: AlertDialog = builder.create()
                dialog.show()*/
            })
        catJar1.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        catRq1.add(catJar1)

    }


//    private fun initScrollListener() {
//        category_view_id!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
//            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//                super.onScrollStateChanged(recyclerView, newState)
//            }
//
//            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                super.onScrolled(recyclerView, dx, dy)
//                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
//                if (!isLoading) {
//                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == list.size - 1) {
//                        //bottom of list!
//                        //    loadMore()
//                        //   isLoading = true
//                    }
//                }
//            }
//        })
//    }

    override fun onBackPressed() {

        val i = Intent(this, home::class.java)
        startActivity(i)
        finish()
    }
}
