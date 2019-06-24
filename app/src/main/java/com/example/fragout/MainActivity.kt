package com.example.fragout

import android.annotation.TargetApi
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.util.Log
import android.util.TypedValue
import android.view.View
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.recycler_fragment.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

//declare all big deal constants
const val datePat = "dd MMMM yyyy"
const val timePat = "HH:mm"
const val spinnerPat = "hh:mm a"
const val username = "ratnu"
const val password = "mandem"
const val delay: Long = 10000
const val credentials = "ratnu:mandem"
const val dataPattern = "M/d/yyyy, HH:mm:ss"
const val selectPattern = spinnerPat + " " + datePat
const val custSpinnerPat = "h:mm a"
var location = "Shaftesbury"
const val pattern1 = "MM/dd/yyyy, HH:mm:ss"
var txtNextBooking:String = ""
var txtBookedTitle:String = ""
var txtBookingUser:String = ""
class MainActivity : AppCompatActivity(),Runnable {
    override fun run() {}
    override fun onResume() {
        super.onResume()
        println("RESUMING COde")
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun doOnListUpdate() {
        //dataList = DateFunctions().sortByTime(dataList)
        //val s= MainAdapter(dataList).removeOptions()
        if(!dataList.isEmpty()){
            println("SORTING THE LIST")
            dataList.sortBy { DateFunctions().strToDate(it.startingTime) }
        }
        runOnUiThread{
            adapter.notifyDataSetChanged()
        }
    }
    var isFragmentOneLoaded = true
    val manager = supportFragmentManager
    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.statusBarColor = ContextCompat.getColor(this, R.color.blackdu)
        txt_room.text = Html.fromHtml("Room: <b>$location</b>")
        refreshList()
        ShowFrag1()
        startRepeating()
        btn_change.setOnClickListener {
            refreshList()
            restartFragment1()
        }
    }

    fun ShowFrag1(showAnimation:Boolean = true) {
        val transaction = manager.beginTransaction()
        val fragment = ArticleFragment()
        if(showAnimation){
            transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
        }
        transaction.replace(R.id.fragment_holder, fragment,"MainFrag")
        transaction.addToBackStack(null)
        transaction.commit()
    }

    fun ShowFrag2() {
        val transaction = manager.beginTransaction()
        val fragment = BookingFragment()
        transaction.setCustomAnimations(R.anim.abc_fade_in, R.anim.abc_fade_out)
        transaction.replace(R.id.fragment_holder, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
    val handler = Handler()
    fun startRepeating(){
        runnable.run()
    }
    fun stopRepeating(){
        handler.removeCallbacks(runnable)
    }
    val runnable = object: Runnable { //Refreshes every .. seconds
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            val startDate = Calendar.getInstance().time
            //check if the current time is occupied....
            println("INCREMENTING TIME, $startDate")
            refreshList()
            restartFragment1()
            handler.postDelayed(this, delay)
        }
    }
    fun fetchJson(){ //function to fetch json from the web.
        var keys:Iterator<String>
        println("FETCHING JSON")
        //val url = "https://api.myjson.com/bins/9wzmf"
        var url ="https://ratnuback.appspot.com/getBooking/"+ location
        val credential = Credentials.basic(username, password);
        val request = Request.Builder().url(url).header("Authorization",credential).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object:Callback{
            override fun onFailure(call: Call, e: IOException) {
                println("fail")
            }
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onResponse(call: Call, response: Response) {
                val body = response.body?.string()
                try {
                    val jsonObject = JSONObject(body)
                    val gson = GsonBuilder().create()
                    jsonObject.keys().forEach {
                        // println("key: $it")
                        //println(jsonObject[it].toString())
                        //we can remove the options that are not in the day here.
                        val curDate = Calendar.getInstance().time
                        val booking = gson.fromJson(jsonObject[it].toString(), Data::class.java)
                        if(DateFunctions().isEnd(booking,curDate)){
                            println("This booking has already ended")
                        }
                        else if(DateFunctions().isToday(curDate,booking)){
                            println("Adding entry since it is today!")
                            dataList.add(booking)
                        }
                        //dataList.add(gson.fromJson(jsonObject[it].toString(), Data::class.java))
                    }
                } catch (err: JSONException) {
                    Log.d("Error", err.toString())
                }
                dataList.sortBy { DateFunctions().strToDate(it.startingTime) }
                val occ = DateFunctions().getOccupied(dataList,Calendar.getInstance().time)
                if(occ!=null){
                    runOnUiThread{
                        println("OCCUPIED : TRUE")
                        //changeToRed()
                        txt_bookedTitle?.setTextSize(TypedValue.COMPLEX_UNIT_DIP,50f)
                        val endingString = DateFunctions().
                            formatTimeUsingDate(DateFunctions().strToDate(occ.endingTime), custSpinnerPat)
                        txtNextBooking = "Booked until ${endingString}"
                        txtBookedTitle = occ.title.toString()
                        txtBookingUser = occ.userName.toString()
                      /*  txt_nextBooking.text = txtNextBooking
                        txt_bookedTitle.text = txtBookedTitle
                        txt_bookingUser.text = txtBookingUser
*/
                    }
                }else{
                    runOnUiThread{
                        //changeToGreen()
                        println("VACANT")
                        //txt_bookedTitle?.setTextSize(TypedValue.COMPLEX_UNIT_DIP,70f)
                        txt_bookedTitle?.text = "FREE"
                        txtBookedTitle = "FREE"
                        if(!dataList.isEmpty()){
                            //try to get the smallest entry.
                            val nextBooking = dataList[0]
                            val startingString = DateFunctions().
                                formatTimeUsingDate(DateFunctions().strToDate(nextBooking.startingTime), custSpinnerPat)
                            txtNextBooking = "Next booking: ${startingString}"
                            txtBookingUser = ""
                            txt_nextBooking?.text = txtNextBooking
                            txt_bookingUser?.text = txtBookingUser

                        }else{
                            txtNextBooking = "No more bookings today!"
                            txtBookingUser = ""
                            txt_nextBooking?.text = txtNextBooking
                            txt_bookingUser?.text = txtBookingUser
                        }
                    }
                }
                dataList.forEach {
                    println("title : ${it.title}")
                }
                //dataList = sortByTime(dataList)
                doOnListUpdate()
            }
        })
    }
    fun restartFragment1(){
        //hopefully restart the fragment so that the layout gets updated with the right information.
        println("restarting fragment")
        val fragment = supportFragmentManager.findFragmentByTag("MainFrag")
        val trans = supportFragmentManager.beginTransaction()
        //ShowFrag1()
        Handler().postDelayed({
            //ShowFrag1(false)
            if (fragment != null) {
                trans.detach(fragment).addToBackStack(null).attach(fragment).commit()
            }
        }, 400)

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun refreshList(){
        dataList.clear()
        fetchJson()
    }
    public fun getNext(): String? {
        return txtNextBooking
    }
    public fun getBookingTitle():String?{
        return txtBookedTitle
    }
    public fun getUser():String?{
        return txtBookingUser
    }
}