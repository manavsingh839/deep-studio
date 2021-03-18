package com.jdwebservices.deepstudio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.jdwebservices.adapter.sliderAdapter
import com.jdwebservices.data.slider
import com.mycity.client_login_save
import kotlinx.android.synthetic.main.activity_services.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList

class highlight : AppCompatActivity() {
    private lateinit var session: client_login_save
    private var userId = ""
    private var userName = ""
    private var userEmail = ""
    private var recyclerViewAdapter: sliderAdapter? = null
    private var postRespone: JSONArray? = null
    private val list = ArrayList<slider>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_highlight)
        session = client_login_save(this)
        if (session.isLoggedIn()) {

            val user: HashMap<String, ArrayList<String>> = session.getUserDetails()
            val aUser = user["user"]!!
            userId = aUser[0]
            userName = aUser[1]
            userEmail = aUser[2]
        }
        // Log.d("user data", Patient.toString())
        postData(userId)
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
    private fun postData(user_id:String)
    {
        val ur = "highlight.php?user=$user_id"

        val r: RequestQueue = Volley.newRequestQueue(this)
        val ja = JsonArrayRequest(
            Request.Method.POST,
            resources.getString(R.string.main_link) + ur,
            null,
            { response ->
                postRespone = response
                for (x in 0 until response.length())
                    list.add(
                        slider(
                            response.getJSONObject(x).getInt("highlight_id"),
                            response.getJSONObject(x).getString("image"),
                            response.getJSONObject(x).getString("link"),
                        )
                    )
                val ad = sliderAdapter(this, list)
                category_view_id.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerViewAdapter = ad
                category_view_id.adapter = ad
                category_view_id.visibility = View.VISIBLE
                // shimmer.visibility = View.GONE

            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        ja.retryPolicy = DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT)
        r.add(ja)
        //   slider()
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