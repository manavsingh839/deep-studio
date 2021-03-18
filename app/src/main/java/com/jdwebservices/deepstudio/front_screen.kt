@file:Suppress("ControlFlowWithEmptyBody", "DEPRECATION")

package com.jdwebservices.deepstudio

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mycity.client_login_save
import kotlinx.android.synthetic.main.activity_front_screen.*

class front_screen : AppCompatActivity() {
    private lateinit var session: client_login_save

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front_screen)

        session = client_login_save(applicationContext)
        //  var pass = this.findViewById(R.id.password) as EditText


        if (session.isLoggedIn()) {
            val i = Intent(applicationContext, home::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
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

        login.setOnClickListener {
            val a = Intent(this, com.jdwebservices.deepstudio.login::class.java)
            startActivity(a)
        }

        skip_btn.setOnClickListener {
            val a = Intent(this, home::class.java)
            startActivity(a)
        }


    }

    override fun onBackPressed() {

        val dislof = android.app.AlertDialog.Builder(this)
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