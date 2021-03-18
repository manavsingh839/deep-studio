package com.jdwebservices.deepstudio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jdwebservices.deepstudio.adapter.album_adapter
import com.jdwebservices.deepstudio.adapter.category_adapter
import com.jdwebservices.deepstudio.data.category_data
import com.mycity.client_login_save
import kotlinx.android.synthetic.main.activity_services.*
import org.json.JSONArray
import java.util.HashMap

class services : AppCompatActivity() {
    lateinit var session: client_login_save
    var userId = ""
    var userName = ""
    var userEmail = ""
    var drawerLayout: DrawerLayout? = null
    var navigationView: NavigationView? = null
    var recyclerViewAdapter: album_adapter? = null
    var post_respone: JSONArray? = null
    var isLoading = false
    var pkname = ""
    val list = ArrayList<category_data>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)
        session = client_login_save(this)
        if (session.isLoggedIn()) {

            val user: HashMap<String, ArrayList<String>> = session.getUserDetails()
            val Patient = user.get("user")!!
            userId = Patient.get(0)
            userName = Patient.get(1)
            userEmail = Patient.get(2)
        }
        // Log.d("user data", Patient.toString())
        album_data(userId)
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
    fun album_data(user_id:String)
    {


        val person_lists_completed = ArrayList<category_data>()

        val sharedPreferencess = getSharedPreferences("services", MODE_PRIVATE)
        val gsons = Gson()
        val json = sharedPreferencess.getString("Set", "")
        if (json!!.isEmpty()) {
            //   Toast.makeText(this@SeeResult, "There is something error", Toast.LENGTH_LONG).show()
        } else {
            val type = object : TypeToken<ArrayList<category_data>>() {}.getType()
            val arrPackageData = gsons.fromJson<ArrayList<category_data>>(json, type)
            for (data in arrPackageData) {
                person_lists_completed.add(
                    category_data(
                        data.category_id,
                        data.categoryName,
                        data.categoryicon,
                        data.categoryDescription
                    )
                )

            }

            val cat_adp = album_adapter(this, person_lists_completed)
            category_view_id.layoutManager = GridLayoutManager(this, 2)
            category_view_id.adapter = cat_adp

            category_view_id.visibility = View.VISIBLE

        }

        val cat_url1 = "services.php?user=$user_id"
        Log.d("url", cat_url1)
        var category = ArrayList<category_data>()

        val sharedPreferences = getSharedPreferences("services", MODE_PRIVATE)
        val gson = Gson()

        val cat_rq1: RequestQueue = Volley.newRequestQueue(this)
        val cat_jar1 = JsonArrayRequest(
            Request.Method.GET,
            resources.getString(R.string.main_link) + cat_url1,
            null,
            { response ->
                post_respone = response
                if (response.length() == 0) {
                    // no_result.visibility = View.VISIBLE
                    category_view_id.visibility = View.GONE
                    //  progress.visibility = View.GONE
                    //  nodata.visibility = View.VISIBLE
                } else {
                    category_view_id.visibility = View.VISIBLE
                    //   nodata.visibility = View.GONE
                }

                for (x in 0..response.length() - 1)
                    category.add(
                        category_data(
                            response.getJSONObject(x).getInt("album_id"),
                            response.getJSONObject(x).getString("name"),
                            response.getJSONObject(x).getString("image"),
                            "0"
                        )
                    )


                val jsons = gson.toJson(category)
                //  Log.d("TAG", "jsonCars = " + jsons)
                val editor = sharedPreferences.edit()
                editor.putString("Set", jsons)
                editor.apply()
                val jsonCars = gson.toJson(category)
                // Log.d("TAG", "jsonCars = " + jsonCars)

                val cat_adp = album_adapter(this, category)
                recyclerViewAdapter = cat_adp
                category_view_id.layoutManager = GridLayoutManager(this, 2)
                category_view_id.adapter = cat_adp

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
        cat_jar1.setRetryPolicy(DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        cat_rq1.add(cat_jar1)

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