package com.example.fragout

import android.os.Build
import android.support.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class DateFunctions{
    //Creates a string of the current time/date of the desired pattern.
    public fun getCurrentTimeUsingDate(pattern:String = datePat):String {
        val date = Date()
        val strDateFormat = "hh:mm:ss a"
        val dateFormat = SimpleDateFormat(pattern)
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }
    fun formatTimeUsingDate(date:Date,pattern:String = datePat):String {
        val strDateFormat = "hh:mm:ss a"
        val dateFormat = SimpleDateFormat(pattern)
        val formattedDate = dateFormat.format(date)
        return formattedDate
    }
    fun strToDate(date:String?,pattern:String? = pattern1): Date { //Changes the string to a date object.
        return SimpleDateFormat(pattern).parse(date)

    }
    fun isToday(curDate:Date,Booking:Data):Boolean{
        val pattern2 = "dd MMMMM yyyy"
        val dateFormat = SimpleDateFormat(pattern2)
        val todayStart = dateFormat.parse(getCurrentTimeUsingDate())
        val todayEnd = Date(todayStart.time + TimeUnit.DAYS.toMillis(1))
        println("THE DATE IS :"+todayStart)
        println("THE LATE IS "+todayEnd)
        val startTime = strToDate(Booking.startingTime)
        if(startTime>=todayStart&&startTime<=todayEnd){
            println("Within TODAY")
            return true
        }
        return false

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun sortByTime(dataList:ArrayList<Data>):ArrayList<Data>{
        //also remove entries that are not in the day...
        val pattern2 = "dd MMMMM yyyy"
        val dateFormat = SimpleDateFormat(pattern2)
        val todayStart = dateFormat.parse(getCurrentTimeUsingDate())
        val todayEnd = Date(todayStart.time + TimeUnit.DAYS.toMillis(1))
        println("THE DATE IS :"+todayStart)
        println("THE LATE IS "+todayEnd)
        val newData = ArrayList<Data>()
        dataList.sortBy { strToDate(it.startingTime) }
        dataList.forEach {
            println(it.startingTime)
            val startTime = strToDate(it.startingTime)
            if(startTime>=todayStart&&startTime<=todayEnd){
                println("ALLOWED")
                newData.add(it)
            }
        }
        newData.forEach {
            println("AYY")
            println(it.startingTime)
        }
        return newData
    }
    fun sortTime(dataList: ArrayList<Data>){
        dataList.sortBy { strToDate(it.startingTime) }
    }
    fun getOccupied(dataList:ArrayList<Data>, curDate: Date): Data? {
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
    fun isOccupied(bookDate:Data,curDate: Date = Calendar.getInstance().time): Boolean {
        val startTime = SimpleDateFormat(pattern1).parse(bookDate.startingTime)
        val endTime = SimpleDateFormat(pattern1).parse(bookDate.endingTime)
        if (startTime <= curDate && endTime >= curDate) {
            return true
        }
        return false
    }
    fun isFine(booking:Data,startTime:Date,endTime:Date):Boolean{ //checks if the time overlaps...
        val bookStart = DateFunctions().strToDate(booking.startingTime)
        val bookEnd = DateFunctions().strToDate(booking.endingTime)
//    val case1 = startTime>bookStart && endTime>bookEnd && endTime>bookStart
//    val case2  = startTime>bookStart && endTime>bookEnd&&startTime>bookEnd
//    val case3 = startTime == bookStart && endTime == bookEnd
//    val case4 = startTime<bookStart && endTime>bookEnd
        val case1 = startTime<bookStart && endTime<bookEnd
        val case2 = startTime<bookStart && endTime == bookEnd
        val case3 = startTime == bookEnd && endTime>bookEnd
        val case4 = startTime>bookEnd && endTime>bookEnd
        val netCase = case1 || case2 ||case3||case4
        if(netCase){
            println("YUP ITS OCCUPIED...")
            return true
        }
        println("THE BOOKSTART :$bookStart BOOKEND: $bookEnd")
        println("STARTY: $startTime ENDY: $endTime")
        return false
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun removeOptions(dataList: ArrayList<Data>): ArrayList<Date> {
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
    fun isEnd(bookDate: Data,curDate: Date = Calendar.getInstance().time): Boolean{
        val endDate = strToDate(bookDate.endingTime)
        if(curDate>endDate){
            return true
        }
        return false
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

}
