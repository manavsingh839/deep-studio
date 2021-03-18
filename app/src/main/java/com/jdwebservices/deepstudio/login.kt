package com.jdwebservices.deepstudio

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.mycity.client_login_save
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONObject

class login : AppCompatActivity() {
    private lateinit var session: client_login_save

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        session = client_login_save(applicationContext)
        //  var pass = this.findViewById(R.id.password) as EditText


        if (session.isLoggedIn()) {
            val i: Intent = Intent(applicationContext, home::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(i)
            finish()
        }


        // Internet checking //

        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if(networkInfo != null && networkInfo.isConnected){ }
        else{
            val builder = android.app.AlertDialog.Builder(this)
            builder.setMessage("Please check Your Internet Connection\n")
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
            dialog.setOnDismissListener {
                this.finish()
            }
        }
        // Internet checking //


        mobile.text.clear()
        password.text.clear()


        forget.setOnClickListener {
            login.text = "Submit"
            password.visibility = View.GONE
            forget.visibility = View.GONE
        }



        login.setOnClickListener {


            val Mobile = mobile.text.toString()
            var Password = password.text.toString()
//            if (Password.equals("")) {
//                Toast.makeText(this, "Password incorrect", Toast.LENGTH_LONG).show()
//            } else {
            if (mobile.length() > 0 && Mobile.contains(" ")) {
                Toast.makeText(this, "Space Not Allowed", Toast.LENGTH_LONG).show()
            } else {
                if (login.text.equals("Submit")) {
                    Password = ""
                }
                login_form.visibility = View.GONE
                progress.visibility = View.VISIBLE

                var url = "login.php?user_login=submit&user_id=$Mobile&password=$Password"
                url = url.replace(" ", "%20")

                val rq: RequestQueue = Volley.newRequestQueue(this)
                val cat_jar =
                    JsonObjectRequest(
                        Request.Method.GET,
                        resources.getString(R.string.main_link) + url,
                        null,
                        {


                            val status = it.getBoolean("success")
                            val message = it.getString("message")
                            val show = it.getBoolean("show")
                            if(show)
                            {
                                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                            }
                            if (status) {
                                val stringJson = it.toString()
                                val obj = JSONObject(stringJson)
                               // val sessionArray: JSONArray = obj.optJSONArray("data")
                                val detail: JSONObject = obj.optJSONObject("detail")
                                val id =  detail.getString("id")
                                val name =  detail.getString("name")
                                val email =  detail.getString("email")
                                session.createLoginSession(id, name, email)
                                val a = Intent(this, home::class.java)
                                startActivity(a)
                                finish()
                            }else{
                                login_form.visibility = View.VISIBLE
                                progress.visibility = View.GONE
                            }



                        },
                        {
                            login_form.visibility = View.VISIBLE
                            progress.visibility = View.GONE


                            // val builder = AlertDialog.Builder(this)
                            //  builder.setMessage("Please check Your Internet Connection\n")
                            //  val dialog: AlertDialog = builder.create()
                            //   dialog.show()
                        })

                rq.add(cat_jar)
            }

            // }
        }


//        register.setOnClickListener {
//            val b = Intent(this, Register::class.java)
//            startActivity(b)
//            finish()
//
//        }


    }

    override fun onBackPressed() {
        val i = Intent(this, front_screen::class.java)
        startActivity(i)
        finish()

//        val dislof = android.app.AlertDialog.Builder(this)
//        dislof.setIcon(R.mipmap.ic_launcher)
//        dislof.setTitle("Closing APP")
//        dislof.setMessage("Are you sure you want to close this App?")
//        dislof.setPositiveButton("Yes") { _, _ ->
//            val start = Intent(Intent.ACTION_MAIN)
//            start.addCategory(Intent.CATEGORY_HOME)
//            start.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            startActivity(start)
//        }
//        dislof.setNegativeButton("No", null)
//        dislof.show()
    }
}