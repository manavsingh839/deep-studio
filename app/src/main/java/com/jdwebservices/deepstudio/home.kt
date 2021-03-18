@file:Suppress("DEPRECATION")

package com.jdwebservices.deepstudio

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jdwebservices.adapter.sliderAdapter
import com.jdwebservices.data.slider
import com.jdwebservices.deepstudio.adapter.album_adapter
import com.jdwebservices.deepstudio.adapter.category_adapter
import com.jdwebservices.deepstudio.data.category_data
import com.mycity.client_login_save
import kotlinx.android.synthetic.main.content_home.*
import kotlinx.coroutines.*
import org.json.JSONArray
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList


class home : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var session: client_login_save
    private var userId = ""
    private var userName = ""
    private var userEmail = ""
    private var drawerLayout: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var recyclerViewAdapter: sliderAdapter? = null
    private var post_respone: JSONArray? = null
    private var isLoading = false
    private var pkname = ""
    private val list = ArrayList<slider>()

    //  var userdata:JsonObject?  = null


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        title = resources.getString(R.string.app_name)
        drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout!!.setDrawerListener(toggle)
        toggle.syncState()

        val manager = this.packageManager
        val info = manager.getPackageInfo(this.packageName, PackageManager.GET_ACTIVITIES)
        pkname = info.packageName
        val vert = info.versionCode
        // Toast("PackageName = " + info.packageName + "\nVersionCode = "+ info.versionCode + "\nVersionName = " + info.versionName + "\nPermissions = " + info.permissions)
        //ver.text = "Version : " + info.versionCode.toString()

        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        navigationView!!.setNavigationItemSelectedListener(this)
        val nav_Menu = navigationView!!.menu
        val headerView = navigationView!!.getHeaderView(0)
        val navUsername = headerView.findViewById<View>(R.id.ver) as TextView
        navUsername.text = "Version - "+info.versionName.toString()


        // Internet checking //

        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if(networkInfo != null && networkInfo.isConnected){

            var urdl = "update_msg.php?version=$vert&update=submit"
            urdl = urdl.replace(" ", "%20")
            val rdq: RequestQueue = Volley.newRequestQueue(this)
            val sdr = StringRequest(Request.Method.GET, resources.getString(R.string.main_link) + urdl, { response ->


                when (response) {
                    "" -> {
                    }
                    else -> {
                        val dislof = AlertDialog.Builder(this)
                        dislof.setIcon(R.mipmap.ic_launcher)
                        dislof.setCancelable(false)
                        dislof.setTitle("Update APP")
                        dislof.setMessage(response)
                        dislof.setPositiveButton("Update") { _, _ ->
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://play.google.com/store/apps/details?id=$pkname")
                            )
                            startActivity(intent)
                        }
                        dislof.setNegativeButton("Cancel") { _, _ ->

                            val start = Intent(Intent.ACTION_MAIN)
                            start.addCategory(Intent.CATEGORY_HOME)
                            start.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(start)

                        }
                        dislof.show()
                    }
                }
            }, {

                /* val builder = AlertDialog.Builder(this)
                 builder.setMessage("Please check Your Internet Connection\n")
                 val dialog: AlertDialog = builder.create()
                 dialog.show()*/
            })
            rdq.add(sdr)
        }
        else{
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Please check Your Internet Connection\n")
            val dialog: AlertDialog = builder.create()
            dialog.show()
            dialog.setOnDismissListener {
                this.finish()
            }
        }
        // Internet checking //

        //  val vert = info.versionName.toString()




        /*    val actionbar: ActionBar? = supportActionBar
            actionbar?.setDisplayHomeAsUpEnabled(true)
            actionbar?.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp)

            drawerLayout = findViewById<View>(R.id.drawer_layout) as DrawerLayout
            val actionBarDrawerToggle = ActionBarDrawerToggle(
                this, drawerLayout, R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout?.addDrawerListener(actionBarDrawerToggle)
            actionBarDrawerToggle.syncState()*/

        session = client_login_save(this)
        /*    val prhelper = AutoStartPermissionHelper.getInstance().isAutoStartPermissionAvailable(this)
            if(prhelper) {
                AutoStartPermissionHelper.getInstance().getAutoStartPermission(this)
            }*/

        if (session.isLoggedIn()) {

            val user: HashMap<String, ArrayList<String>> = session.getUserDetails()
            val aUser = user["user"]!!
            userId = aUser[0]
            userName = aUser[1]
            userEmail = aUser[2]
//            nav_Menu.findItem(R.id.nav_logout).isVisible = true
            nav_Menu.findItem(R.id.nav_logout).title = "Logout"
            albm_ttl.text = "Albums"
            album_data(userId)
            album_view.setOnClickListener {
                val i = Intent(this, Album::class.java)
                startActivity(i)
                finish()
            }
        }else{
            albm_ttl.text = "Services"
//            nav_Menu.findItem(R.id.nav_logout).isVisible = false
            nav_Menu.findItem(R.id.nav_logout).title = "Login"
            service_data(userId)
            album_view.setOnClickListener {
                val i = Intent(this, services::class.java)
                startActivity(i)
                finish()
            }
        }
            // Log.d("user data", Patient.toString())
            slider_data()
            post_data(userId)
          //  initScrollListener()




            // getOrd()
            //  repeatcall(userId, usertoken)
            //   userdata = //JsonObject(Patient.get(3))

    }
    private fun slider_data()
    {
        val ur = "slider.php"
        val lis = ArrayList<slider>()

        val r: RequestQueue = Volley.newRequestQueue(this)
        val ja = JsonArrayRequest(
            Request.Method.POST,
            resources.getString(R.string.main_link) + ur,
            null,
            { response ->

                for (x in 0 until response.length())
                    lis.add(
                        slider(
                            response.getJSONObject(x).getInt("slider_id"),
                            response.getJSONObject(x).getString("image"),
                                ""
                        )
                    )

                val ad = sliderAdapter(this, lis)
                slider_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
                // Scroll
                val job = GlobalScope.launch(Dispatchers.Main, start = CoroutineStart.LAZY) {
                    val MINIMUM_POSITION = 1
                    val AUTO_SCROLL_REPEATING_TIMES = 15000
                    val SCROLL_DELAY = 3000.toLong()
                    var position = MINIMUM_POSITION
                    repeat(AUTO_SCROLL_REPEATING_TIMES) {
                        delay(SCROLL_DELAY)
                        slider_rv.smoothScrollToPosition(position)
                        when {
                            position + 1 == lis.size -> position = 0
                            position == 0 -> position = MINIMUM_POSITION
                            else -> position++
                        }
                    }
                }
                job.start()
                // Scroll


                slider_rv.adapter = ad
                slider_rv.visibility = View.VISIBLE
                // shimmer.visibility = View.GONE

            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        ja.setRetryPolicy(DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        r.add(ja)
        //   slider()
    }
    fun service_data(user_id:String)
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

            val cat_adp = category_adapter(this, person_lists_completed)
            album_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            //  album_rv.layoutManager = GridLayoutManager(this, 3)
            album_rv.adapter = cat_adp

            album_rv.visibility = View.VISIBLE

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
                if (response.length() == 0) {
                    // no_result.visibility = View.VISIBLE
                    album_rv.visibility = View.GONE
                    //  progress.visibility = View.GONE
                    //  nodata.visibility = View.VISIBLE
                } else {
                    album_rv.visibility = View.VISIBLE
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
                album_rv.layoutManager = GridLayoutManager(this, 3)
                album_rv.adapter = cat_adp

                album_rv.visibility = View.VISIBLE
                //shimmers.visibility = View.GONE
                // progress.visibility = View.GONE

            },
            {
                album_rv.visibility = View.VISIBLE
                //  progress.visibility = View.GONE

                /*val builder = AlertDialog.Builder(this)
                builder.setMessage("Please check Your Internet Connection\n")
                val dialog: AlertDialog = builder.create()
                dialog.show()*/
            })
        cat_jar1.setRetryPolicy(DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        cat_rq1.add(cat_jar1)

    }
    fun album_data(user_id:String)
    {


        val person_lists_completed = ArrayList<category_data>()

        val sharedPreferencess = getSharedPreferences("USER", MODE_PRIVATE)
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
            album_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
          //  album_rv.layoutManager = GridLayoutManager(this, 3)
            album_rv.adapter = cat_adp

            album_rv.visibility = View.VISIBLE

        }

        val cat_url1 = "album.php?user=$user_id"
        Log.d("url", cat_url1)
        var category = ArrayList<category_data>()

        val sharedPreferences = getSharedPreferences("USER", MODE_PRIVATE)
        val gson = Gson()

        val cat_rq1: RequestQueue = Volley.newRequestQueue(this)
        val cat_jar1 = JsonArrayRequest(
            Request.Method.GET,
            resources.getString(R.string.main_link) + cat_url1,
            null,
            { response ->
                if (response.length() == 0) {
                    // no_result.visibility = View.VISIBLE
                    album_rv.visibility = View.GONE
                    //  progress.visibility = View.GONE
                  //  nodata.visibility = View.VISIBLE
                } else {
                    album_rv.visibility = View.VISIBLE
                 //   nodata.visibility = View.GONE
                }

                for (x in 0..response.length() - 1)
                    category.add(
                        category_data(
                            response.getJSONObject(x).getInt("album_id"),
                            response.getJSONObject(x).getString("name"),
                            response.getJSONObject(x).getString("image"),
                            "1"
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
                album_rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
//                album_rv.layoutManager = GridLayoutManager(this, 3)
                album_rv.adapter = cat_adp

                album_rv.visibility = View.VISIBLE
                //shimmers.visibility = View.GONE
                // progress.visibility = View.GONE

            },
            {
                album_rv.visibility = View.VISIBLE
                //  progress.visibility = View.GONE

                /*val builder = AlertDialog.Builder(this)
                builder.setMessage("Please check Your Internet Connection\n")
                val dialog: AlertDialog = builder.create()
                dialog.show()*/
            })
        cat_jar1.setRetryPolicy(DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        cat_rq1.add(cat_jar1)

    }
    fun post_data(user_id:String)
    {
        val ur = "highlight.php?user=$user_id"

        val r: RequestQueue = Volley.newRequestQueue(this)
        val ja = JsonArrayRequest(
            Request.Method.POST,
            resources.getString(R.string.main_link) + ur,
            null,
            { response ->
                post_respone = response
                for (x in 0..response.length() - 1)
                    list.add(
                        slider(
                            response.getJSONObject(x).getInt("highlight_id"),
                            response.getJSONObject(x).getString("image"),
                            response.getJSONObject(x).getString("link"),
                        )
                    )
                val ad = sliderAdapter(this, list)
                post_rv.layoutManager =
                    LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerViewAdapter = ad
                post_rv.adapter = ad
                post_rv.visibility = View.VISIBLE
                // shimmer.visibility = View.GONE

            },
            { error ->
                Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
            })
        ja.setRetryPolicy(DefaultRetryPolicy(0, -1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        r.add(ja)
        //   slider()
    }

    private fun initScrollListener() {
        post_rv!!.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager?
                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == list.size - 1) {
                        //bottom of list!
                    //    loadMore()
                     //   isLoading = true
                    }
                }
            }
        })
    }

    private fun loadMore() {
        list.add(slider(0, "", ""))
        recyclerViewAdapter!!.notifyItemInserted(list.size - 1)
        val handler = Handler()
        handler.postDelayed({
            list.removeAt(list.size - 1)
            val scrollPosition: Int = list.size
            recyclerViewAdapter!!.notifyItemRemoved(scrollPosition)
            var currentSize = scrollPosition
            val nextLimit = currentSize + 10
            while (currentSize - 1 < nextLimit) {
                for (x in 0..post_respone!!.length() - 1)
                    list.add(
                        slider(
                            post_respone!!.getJSONObject(x).getInt("highlight_id"),
                            post_respone!!.getJSONObject(x).getString("image"),
                            post_respone!!.getJSONObject(x).getString("link")
                        )
                    )
                currentSize++
            }
            recyclerViewAdapter!!.notifyDataSetChanged()
            isLoading = false
        }, 2000)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.home, menu)
        val searchItem = menu.findItem(R.id.LogOutMenu)
        searchItem.isVisible = false
        //    onOptionsItemSelected(searchItem)
        /*    if(role_id == "2")
            {
                nav_employee_lis.isVisible = false
            }else{
                nav_employee_lis.isVisible = true
            }*/


        return true


        //return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.LogOutMenu -> {
                session.LogoutUser()
                return true

            }
            else -> super.onOptionsItemSelected(item)
        }
        // return false
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_home -> {
                val i = Intent(this, home::class.java)
                startActivity(i)
                finish()
            }
            R.id.nav_services -> {
                val i = Intent(this, services::class.java)
                startActivity(i)
                finish()
            }
            R.id.nav_highlight -> {
                val i = Intent(this, highlight::class.java)
                startActivity(i)
                finish()
            }
            R.id.nav_direction -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://www.google.com/maps/dir//Deep+Digital+Studio,+Naka+No+1,+Near+Darbar+Sahib,+Sri+Muktsar+Sahib,+SahibPunjab,+152026/@30.4735282,74.5112675,17z/data=!4m9!4m8!1m0!1m5!1m1!1s0x391768da53c09297:0x1cf9ac30ae01fde9!2m2!1d74.5134562!2d30.4735282!3e0")
                )
                startActivity(intent)
                /* val gmmIntentUri = Uri.parse(rest_geo)
                 val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                 mapIntent.setPackage("com.google.android.apps.maps")
                 mapIntent.resolveActivity(packageManager)?.let {
                     startActivity(mapIntent)
                 }*/
            }
            R.id.nav_call -> {
                val q = Intent(Intent.ACTION_DIAL)
                q.data = Uri.parse("tel:+919988627977")
                startActivity(q)
                finish()
            }
            R.id.nav_logout -> {
                if(userId == "") {
                    val i = Intent(this, login::class.java)
                    startActivity(i)
                    finish()

                }else{
                    session.LogoutUser()
                }
            }
            R.id.nav_share -> {
                val i = Intent(Intent.ACTION_SEND)
                i.type = "text/plain"
                i.putExtra(Intent.EXTRA_SUBJECT, "Deep Studio")
                var sAux = "\nDownload Deep Studio app\n\n"
                sAux += "https://play.google.com/store/apps/details?id=$pkname\n"
                i.putExtra(Intent.EXTRA_TEXT, sAux)
                startActivity(Intent.createChooser(i, "Share app link"))

            }
        }
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun printKeyHash(context: Activity): String {
        val packageInfo: PackageInfo
        var key = ""
        try {
            //getting application package name, as defined in manifest
            val packageName = context.getApplicationContext().getPackageName()
            //Retriving package info
            packageInfo = context.packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            //   Log.e("Package Name=", context.getApplicationContext().getPackageName())
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                // key = String(Base64.encode(md.digest(), 0))
                // String key = new String(Base64.encodeBytes(md.digest()));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    key = Base64.getEncoder().encodeToString(md.digest())
                }
                //    Log.e("Key Hash=", key)
            }
        } catch (e1: PackageManager.NameNotFoundException) {
            //   Log.e("Name not found", e1.toString())
        } catch (e: NoSuchAlgorithmException) {
            //    Log.e("No such an algorithm", e.toString())
        } catch (e: Exception) {
            //    Log.e("Exception", e.toString())
        }
        return key
    }

    override fun onBackPressed() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {

            val dislof = AlertDialog.Builder(this)
            dislof.setIcon(R.mipmap.ic_launcher)
            dislof.setTitle("Closing APP")
            dislof.setMessage("Are you sure you want to close this App?")
            dislof.setPositiveButton("Yes") { _, _ ->
                val start = Intent(Intent.ACTION_MAIN)
                start.addCategory(Intent.CATEGORY_HOME)
                start.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(start)
            }
            dislof.setNegativeButton("No", null)
            dislof.show()
        }
    }
}