package com.mycity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.SharedPreferences
import com.jdwebservices.deepstudio.login

class client_login_save {

    var pref: SharedPreferences
    var editor: SharedPreferences.Editor
    var con: Context
    var PRIVATE_MODE: Int = 0

    @SuppressLint("CommitPrefEdits")
    constructor(con: Context) {
        this.con = con
        pref = con.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
    }

    companion object {
        val PREF_NAME: String = "user"
        val IS_LOGIN: String = "isLoggedIn"
        val KEY_userId: String = "userId"
        val KEY_userName: String = "userName"
        val KEY_userEmail: String = "userEmail"
    }


    fun createLoginSession(
        userId: String,
        userName: String,
        userEmail: String,
    ) {
        editor.putBoolean(IS_LOGIN, true)
        editor.putString(KEY_userId, userId)
        editor.putString(KEY_userName, userName)
        editor.putString(KEY_userEmail, userEmail)
        editor.commit()
    }

    fun checkLogin() {
        if (!this.isLoggedIn()) {
            val i = Intent(con, login::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            i.flags = FLAG_ACTIVITY_CLEAR_TASK
            con.startActivity(i)
        }
    }

    fun isLoggedIn(): Boolean {
        return pref.getBoolean(IS_LOGIN, false)
    }

    fun getUserDetails(): HashMap<String, java.util.ArrayList<String>> {
        val hm = HashMap<String, java.util.ArrayList<String>>()
        val values = ArrayList<String>()
        values.add(pref.getString(KEY_userId, null)!!)
        values.add(pref.getString(KEY_userName, null)!!)
        values.add(pref.getString(KEY_userEmail, null)!!)
        hm.put("user", values)
        /* var user: Map<String,String> = HashMap<String,String>()
         (user as HashMap).put(KEY_blo_id,pref.getString(KEY_blo_id,null)!!)
         user.put(KEY_EMAIL,pref.getString(KEY_EMAIL,null)!!)*/
        return hm
    }

    fun LogoutUser() {
        editor.clear()
        editor.commit()
        val i = Intent(con, login::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        i.flags = FLAG_ACTIVITY_CLEAR_TASK
        con.startActivity(i)
    }
//    fun clearData() {
//        editor.clear()
//        editor.commit()
//    }
}