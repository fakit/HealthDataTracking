package com.fakit.healthdatatracking

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.os.CountDownTimer
import android.util.Log
import java.util.*


/*
class SensorService:SensorEventListener {
    var sensorType: Int = 0
    var currentValue: Float = 0.0F
    override fun onSensorChanged(event: SensorEvent?) {


        val i = Intent(this, HelpSensorService::class.java)

        if (isRunning1) {
            isRunning1 = false;
            countdown_timer = object : CountDownTimer(2000, 1000) {
                override fun onFinish() {
                    isRunning1 = true;
                    mobileDeviceConnected=true
                    if (mobileDeviceConnected&& StartPressed) {
                        // The sensor type (as defined in the Sensor class).
                        try {
                            sensorType = event!!.sensor.getType()
                            currentValue = event!!.values.get(0)

                            i.putExtra("sensorType", sensorType)
                            i.putExtra("currentValue", currentValue)

                            i.putExtra("nodeId", mobileNodeUri)
                            startService(i)
                        }catch (e: Exception){

                        }
                    }
                }
                override fun onTick(p0: Long) {}
            }
            countdown_timer.start()
            //getSensorData(sensorType, currentValue)
        }

    }


    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        TODO("Not yet implemented")
    }



}

*/

class SensorService :  SensorEventListener {

    var isRunning =true
    var LastSensorData:String?=null

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    override fun onSensorChanged(event: SensorEvent?) {

        if (isRunning) {
            isRunning = false;
            var countdown_timer = object : CountDownTimer(10000, 1000) {
                override fun onFinish() {
                    Log.d(
                        "TAG_MESSAGE_RECEIVED",
                        "SEND SENSOR !!!!!!!!!!!!!!!" + Date()

                    )
                    isRunning = true;
                    mobileDeviceConnected = true
                    if (mobileDeviceConnected && MainActivity().getpressed()) {
                        // The sensor type (as defined in the Sensor class).
                        try {
                            val CurrentSensorData =
                                ByteString(getSensorData(event!!.sensor.getType(), event!!.values.get(0)))

                            if (CurrentSensorData != LastSensorData) {
                            MA.sendData(CurrentSensorData)
                            LastSensorData = CurrentSensorData
                            }
                        } catch (e: Exception) {}
                    }
                }

                override fun onTick(p0: Long) {}
            }
            countdown_timer.start()
        }
    }

    private fun ByteString(DataList: MutableMap<String, Float>): String {
        var outputString="\n"
        DataList.forEach{  entry->
            outputString+="${entry.key} : ${entry.value}\n"
        }
        return outputString
    }
    fun getSensorData(sensorType:Int , currentValue: Float ): MutableMap<String, Float> {

        // The new data value of the sensor.  Both the light and proximity
        // sensors report one value at a time, which is always the first
        // element in the values array.

        var CurrentSensorData = mutableMapOf<String, Float>()
        when (sensorType) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {

                CurrentSensorData["temp"] = currentValue

            }
            Sensor.TYPE_HEART_RATE -> {
                CurrentSensorData["heart"] = currentValue

            }
            Sensor.TYPE_PRESSURE -> {
                CurrentSensorData ["pressure"] = currentValue

            }
            Sensor.TYPE_LIGHT -> {
                CurrentSensorData ["light"] = currentValue

            }
            Sensor.TYPE_ACCELEROMETER -> {
                CurrentSensorData ["acceleration"] = currentValue

            }
            Sensor.TYPE_LINEAR_ACCELERATION -> {
                CurrentSensorData ["linearacceleration"] = currentValue

            }
            Sensor.TYPE_RELATIVE_HUMIDITY -> {
                CurrentSensorData ["rhumid"] = currentValue

            }

            else -> {
            }
        }
        return CurrentSensorData
    }

    /* private fun setSensorData(sbTemp: String) {
         if (StartPressed) {
             val sbTempArr =  sbTemp.split(":")
             if (sbTempArr[0].trim()=="temp"){
                 CurrentSensorData["temp"] = sbTempArr[1].toFloat()
             }
             else if (sbTempArr[0].trim()=="heart"){
                 CurrentSensorData["heart"]= sbTempArr[1].toFloat()
             }
             else if (sbTempArr[0].trim()=="pressure"){
                 CurrentSensorData ["pressure"] = sbTempArr[1].toFloat()
                 sendSensorData(nodeId)
             }
             else if (sbTempArr[0].trim()=="light"){
                 CurrentSensorData["light"] = sbTempArr[1].toFloat()
             }
             else if (sbTempArr[0].trim()=="acceleration"){
                 CurrentSensorData ["acceleration"] = sbTempArr[1].toFloat()
                 sendSensorData(nodeId)
             }
             else if (sbTempArr[0].trim()=="linearacceleration"){
                 CurrentSensorData ["linearacceleration"] = sbTempArr[1].toFloat()
                 sendSensorData(nodeId)
             }
             else if (sbTempArr[0].trim()=="rhumid"){
                 CurrentSensorData ["rhumid"] = sbTempArr[1].toFloat()
                 sendSensorData(nodeId)
             }


             sendSensorData(nodeId)
         }

     }*/

}


