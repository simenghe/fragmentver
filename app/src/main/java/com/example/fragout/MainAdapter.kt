package com.example.fragout

import android.os.Build
import android.os.UserManager
import android.support.annotation.RequiresApi
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.bookingitem.view.*
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.*
import kotlin.collections.ArrayList

class MainAdapter(val dataList: ArrayList<Data>) : RecyclerView.Adapter<MainAdapter.CustomViewHolder>() {
    //trying a get request....
    override fun getItemCount(): Int { //probably determined by a count through the database...
        return dataList.count()
    }
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        if(DateFunctions().isOccupied(dataList[p1])){
            val cellForRow = layoutInflater.inflate(R.layout.booked_card, p0, false)
            return CustomViewHolder(cellForRow)
        }else{
            val cellForRow = layoutInflater.inflate(R.layout.booked_card, p0, false)
            return CustomViewHolder(cellForRow)
        }
    }
    override fun onBindViewHolder(p0: CustomViewHolder, pos: Int) {
        //example date: 5/30/2019, 16:00:00
        val booking = dataList[pos]
        val pattern = "MM/dd/yyyy, HH:mm:ss"
        p0.itemView.txt_title?.text =booking.title
        p0.itemView.txt_NAME?.text = "Organizer : " + booking.userName
        //should format the date using some java library.
        val startTime = SimpleDateFormat(pattern).parse(booking.startingTime)
        val endTime = SimpleDateFormat(pattern).parse(booking.endingTime)
        val startString = DateFunctions().formatTimeUsingDate(startTime, custSpinnerPat)
        val endString = DateFunctions().formatTimeUsingDate(endTime, custSpinnerPat)
        p0.itemView.txt_duration?.text = "$startString - $endString"
    }
    fun getOccupied(dataList: ArrayList<Data>, curDate: Date): Data? {
        dataList.forEach {
            val startTime = SimpleDateFormat(pattern1).parse(it.startingTime)
            val endTime = SimpleDateFormat(pattern1).parse(it.endingTime)
            val Occupied = (startTime <= curDate && endTime >= curDate)
            if (Occupied) {
                return it
            }
        }
        return null
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun removeOptions(): ArrayList<Date> {
        //val formattedDate = DateFunctions().getCurrentTimeUsingDate(pattern1)
        val zeroDate = DateFunctions().getCurrentTimeUsingDate(datePat)
        val occupiedDates = ArrayList<Date>()
        //println("THIS IS THE CURRENT DATE : " + formattedDate)
        val curDate = Calendar.getInstance().time
        val occupied = getOccupied(dataList,curDate)
        val todayZero = SimpleDateFormat(datePat).parse(zeroDate)
        if (occupied != null) {
            println("RUNNING NON NULL")
            val occStart = SimpleDateFormat(pattern1).parse(occupied?.startingTime.toString())
            val occEnd = SimpleDateFormat(pattern1).parse(occupied?.endingTime.toString())
            //println("OCC "+occStart.toString())
            val c = Calendar.getInstance()
            c.time = todayZero
            for (i in 0..48) { //range from 00:00 -> 24:00 create the list.
                //println(c.time.toString())
                val prev = c.time
                val curTime = Calendar.getInstance().time
                println("CUR TIME TO REMOVE $curTime")
                if(prev < curTime){
                    println("curtime is greater")
                }
                if (prev < occEnd) {
                    println("bookStart is greater")
                } else {
                    occupiedDates.add(c.time)
                }
                c.add(Calendar.MINUTE, 30)
            }
            //now filter the list of available times using some nasty algorithm
        }else if(occupied==null){
            println("its null")
            val c = Calendar.getInstance()
            c.time = todayZero
            var state = true
            for (i in 0..48) { //range from 00:00 -> 24:00 create the list.
                val prev = c.time
                val curTime = Calendar.getInstance().time
                println("CUR TIME TO FILTER $curTime")
                if(prev < curTime) {
                    c.add(Calendar.MINUTE, 30)
                    val after = c.time
                    if(curTime<after){
                        println("Permissible.")
                        occupiedDates.add(prev)
                    }
                    c.add(Calendar.MINUTE, -30)
                    println("curtime is greater")
                } else{
                    occupiedDates.add(c.time)
                }
                c.add(Calendar.MINUTE, 30)
            }
        }else{
            println("Error has occured....")
        }
        return occupiedDates
    }
    class CustomViewHolder(v: View) : RecyclerView.ViewHolder(v) {
    }
}