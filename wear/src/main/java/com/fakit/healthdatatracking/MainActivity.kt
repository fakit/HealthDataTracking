package com.fakit.healthdatatracking

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

import androidx.core.app.ActivityCompat
import com.google.android.gms.wearable.*
import kotlinx.android.synthetic.main.activity_main.*

var MA = MainActivity()
var StartPressed: Boolean=false
var mobileDeviceConnected: Boolean = false
 var mWearBodySensorsPermissionApproved = false
 var newMessageReceiver: MessageService?=null
 val PERMISSION_REQUEST_READ_BODY_SENSORS = 1
 val MESSAGE_ITEM_RECEIVED_PATH: String = "/message-item-received"

var activityContext: Context? = null


var messageEvent: MessageEvent? = null
var mobileNodeUri: String? = null
//Timer Check
lateinit var countdown_timer: CountDownTimer
var isRunning: Boolean = true;
 lateinit var mSensorManager: SensorManager

// Proximity and light sensors, as retrieved from the sensor manager.
private var mSensorTemp: Sensor? = null
private var mSensorHeart: Sensor? = null
private var mSensorLight: Sensor? = null
private var mSensorAccel: Sensor? = null
private var mSensorPres: Sensor? = null
private var mSensorHumidity: Sensor? = null
private var mSensorLinAccel: Sensor? = null


class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener,
    CapabilityClient.OnCapabilityChangedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if(!mWearBodySensorsPermissionApproved){
            try {
                permissionRequest()
            }

            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mobileNodeUri = messageEvent?.sourceNodeId
        //btn_sending.setOnClickListener();
        val btn_sending = findViewById(R.id.btn_sending) as Button
        // set on-click listener
        btn_sending.setOnClickListener {
            // your code to perform when the user clicks on the button
            if(  btn_sending.text=="Start Job")
            {
                onResume()
                StartPressed  = true
                btn_sending.text="Stop Job"
                //btn_sending.setBackgroundColor(Color.RED)
                deviceconnectionStatusTv.text = "Running..."
            }
            else{
                onPause()
                StartPressed  = false
                btn_sending.text="Start Job"
                deviceconnectionStatusTv.text = "Job stoped"
            }
        }

        activityContext = this

        newMessageReceiver= MessageService()
        // start your next activity

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager

        mSensorTemp = mSensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorHeart = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorLight = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorPres = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        mSensorHumidity = mSensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        mSensorLinAccel = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        onPause()
        MA = this@MainActivity
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun currentTimeStr(): String? {
        val c: Calendar = Calendar.getInstance()
        val df = SimpleDateFormat("HH:mm:ss")
        return df.format(c.getTime())
    }
         fun getpressed():Boolean{
                return StartPressed
            }
     fun sendData(CurrentDataString: String) {
        if (StartPressed && isRunning) {
            isRunning = false;
            countdown_timer = object : CountDownTimer(3000, 1000) {
                override fun onFinish() {
                    isRunning = true
                try {

                    // Set the data of the message to be the bytes of the Uri.
                    val payload: ByteArray = CurrentDataString.toByteArray()
                    // Send the rpc
                    // Instantiates clients without member variable
                    val sendMessageTask =
                        Wearable.getMessageClient(activityContext!!)
                            .sendMessage(mobileNodeUri.toString(), MESSAGE_ITEM_RECEIVED_PATH, payload)
                    sendMessageTask.addOnCompleteListener { if (it.isSuccessful) {
                        deviceconnectionStatusTv.text = "Running..."
                    }
                    }
                } catch (e: Exception) {                        }
                }
                override fun onTick(p0: Long) {}
            }
            countdown_timer.start()
        }
    }

    fun permissionRequest() {
        mWearBodySensorsPermissionApproved =
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                    === PackageManager.PERMISSION_GRANTED)
        if (!mWearBodySensorsPermissionApproved) {
            // On devices, BODY SENSORS permission not granted. Request permission.
            ActivityCompat.requestPermissions(
                this@MainActivity, arrayOf(Manifest.permission.BODY_SENSORS),
                PERMISSION_REQUEST_READ_BODY_SENSORS
            )
        }
    }
        override fun onPause() {
        super.onPause()
       try {
               Wearable.getDataClient(activityContext!!).removeListener(this)
               Wearable.getCapabilityClient(activityContext!!).removeListener(this)
               Wearable.getMessageClient(activityContext!!).removeListener(newMessageReceiver!!)
               mSensorManager.unregisterListener(SensorService())
           } catch (e: Exception) {
               e.printStackTrace()
           }
    }

    override fun onResume() {
        super.onResume()
        try {
            Wearable.getDataClient(activityContext!!).addListener(this)
            Wearable.getMessageClient(activityContext!!).addListener(newMessageReceiver!!)
            Wearable.getCapabilityClient(activityContext!!)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
            mSensorManager.registerListener(SensorService(), mSensorTemp, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(SensorService(), mSensorHeart, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(SensorService(), mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(SensorService(), mSensorAccel, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(SensorService(), mSensorPres, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(SensorService(), mSensorHumidity, SensorManager.SENSOR_DELAY_NORMAL);
            mSensorManager.registerListener(SensorService(), mSensorLinAccel, SensorManager.SENSOR_DELAY_NORMAL);
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDataChanged(p0: DataEventBuffer) {    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {    }
}

