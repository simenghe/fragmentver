package com.example.fragout

import android.app.Activity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Request
import com.android.volley.Response
import kotlinx.android.synthetic.main.content_make_booking.*
import kotlinx.android.synthetic.main.new_content_cooking.*
import kotlinx.android.synthetic.main.new_content_cooking.edit_name
import kotlinx.android.synthetic.main.new_content_cooking.edit_title
import kotlinx.android.synthetic.main.new_content_cooking.view.*
import org.json.JSONObject
import java.util.HashMap

const val url = "https://ratnuback.appspot.com/addBooking/Shaftesbury"
class BookingFragment : Fragment(){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.d("Does Exist?", dataList.isEmpty().toString())
        val startList =
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.new_content_cooking, container, false)
        rootView.btn_cancel.setOnClickListener{
            ShowFrag1()
            println("BUTTON CLICKED.")
        }
        //save the booking.
        rootView.btn_save.setOnClickListener{
            val params = HashMap<String,String>()
            //dependent on location.
            params["title"] = rootView.edit_title.text.toString()
            params["userName"] = rootView.edit_name.text.toString()
            params["description"] = "Booked from the Android APP!"

            val jsonObject = JSONObject(params)
            val request = CustomJsonObjectRequestBasicAuth(
                Request.Method.POST,url,jsonObject,
                Response.Listener { response ->
                    println("GOOD")
                    val dataObject = Data()
                    dataObject.title = params["title"]
//                    dataObject.endingTime = formattedEnd.toString()
//                    dataObject.startingTime = formattedStart.toString()
                    dataObject.userName = params["userName"]
                }, Response.ErrorListener{
                    println("BAD")
                    // Error in request
                    println("Volley error: $it")
                    val dataObject = Data()
                    dataObject.title = params["title"]
//                    dataObject.endingTime = formattedEnd.toString()
//                    dataObject.startingTime = formattedStart.toString()
                    dataObject.userName = params["userName"]
                }, credentials)
            request.retryPolicy = DefaultRetryPolicy(
                DefaultRetryPolicy.DEFAULT_TIMEOUT_MS,
                // 0 means no retry
                0, // DefaultRetryPolicy.DEFAULT_MAX_RETRIES = 2
                1f // DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
            activity?.applicationContext?.let { it1 -> VolleySingleton.getInstance(it1).addToRequestQueue(request) }
            //ShowFrag1()
        }
        return rootView
    }
    fun ShowFrag1(){
        val transaction = fragmentManager!!.beginTransaction()
        val fragment = ArticleFragment()
        transaction.setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out)
        transaction.replace(R.id.fragment_holder,fragment,"MainFrag")
        transaction.addToBackStack(null)
        transaction.commit()
    }
}