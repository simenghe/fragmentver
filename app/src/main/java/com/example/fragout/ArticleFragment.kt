package com.example.fragout

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.content_make_booking.*
import kotlinx.android.synthetic.main.recycler_fragment.*
import kotlinx.android.synthetic.main.recycler_fragment.view.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

var dataList = ArrayList<Data>()
val adapter = MainAdapter(dataList)
class ArticleFragment : Fragment(),Runnable {
    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onResume() {
        super.onResume()
        println("FRAGMENT RESUMED")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.recycler_fragment, container, false)
        val recycleMain = rootView.recycle_main
        val swiper = rootView.swiper
        val title = MainActivity().getBookingTitle()
        val next = MainActivity().getNext()
        val user = MainActivity().getUser()
        println("TITLE IS: $title, NEXT IS: $next, USER IS $user")
        rootView.txt_bookedTitle.text = title
        rootView.txt_nextBooking.text = next
        rootView.txt_bookingUser.text = user
        if(title=="FREE"){
            rootView?.txt_bookedTitle?.setTextSize(TypedValue.COMPLEX_UNIT_DIP,70f)
        }
        recycleMain.layoutManager = LinearLayoutManager(this.activity)
        recycleMain.adapter = adapter
        recycleMain.setHasFixedSize(true)
        swiper.setOnRefreshListener {
            swiper.isRefreshing = false
        }
        rootView.fab.setOnClickListener {
            println("STOP REPEATING")
            ShowFrag2()
        }

        return rootView
    }
    fun ShowFrag2(){
        val transaction = fragmentManager!!.beginTransaction()
        val fragment = BookingFragment()
        transaction.setCustomAnimations(R.anim.abc_fade_in,R.anim.abc_fade_out)
        transaction.replace(R.id.fragment_holder,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    fun strToDate(date:String?,pattern:String? = pattern1): Date { //Changes the string to a date object.
        return SimpleDateFormat(pattern).parse(date)

    }
    fun isEnd(bookDate: Data,curDate: Date = Calendar.getInstance().time): Boolean{
        val endDate = strToDate(bookDate.endingTime)
        if(curDate>endDate){
            return true
        }
        return false
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun doOnListUpdate() {
        //dataList = DateFunctions().sortByTime(dataList)
        //val s= MainAdapter(dataList).removeOptions()
        if(!dataList.isEmpty()){
            println("SORTING THE LIST")
            dataList.sortBy { strToDate(it.startingTime) }
        }
        adapter.notifyDataSetChanged()
    }
}