package com.jdwebservices.deepstudio

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT = 4000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val tv1 = findViewById(R.id.iv1) as ImageView
//        val tv2 = findViewById(R.id.iv2) as TextView
        val tv3 = findViewById(R.id.iv3) as TextView
        //TextView tv2 = (TextView) findViewById(R.id.tv2);
        val mani = AnimationUtils.loadAnimation(this, R.anim.trans)
        tv1.startAnimation(mani)
        //tv2.startAnimation(mani)
        //tv3.startAnimation(mani)

        tv3.setOnClickListener {
            val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://jdwebservices.com/")
                    )
                    startActivity(intent)
        }


        // Internet checking //

        val cm = baseContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = cm.activeNetworkInfo
        if(networkInfo != null && networkInfo.isConnected){
            // Internet checking //
            Handler().postDelayed(Runnable {
                val i = Intent(this, front_screen::class.java)
                startActivity(i)
                finish()
            }, SPLASH_TIME_OUT.toLong())

        }
        else{
            val builder = android.app.AlertDialog.Builder(this)
            builder.setMessage("Please check Your Internet Connection\n")
            val dialog: android.app.AlertDialog = builder.create()
            dialog.show()
            dialog.setOnDismissListener {
                this.finish()
            }
        }

    }
}