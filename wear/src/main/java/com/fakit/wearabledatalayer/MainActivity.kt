package com.fakit.wearabledatalayer

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.wearable.*
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.StandardCharsets


var StartPressed: Boolean=false

class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener, SensorEventListener {
    var activityContext: Context? = null

    private val TAG_MESSAGE_RECEIVED = "receive1"
    private val APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD"

    private var mobileDeviceConnected: Boolean = false
    private var CurrentSensorData = arrayOf("0.0", "0.0", "0.0")
    private var LastSensorData=""
    private var mWearBodySensorsPermissionApproved = false
    private var mSensorManager: SensorManager? = null

    // Proximity and light sensors, as retrieved from the sensor manager.
    private var mSensorTemp: Sensor? = null
    private var mSensorHeart: Sensor? = null
    private var mSensorLight: Sensor? = null

    // Payload string items
    private val wearableAppCheckPayloadReturnACK = "AppOpenWearableACK"
    private val MESSAGE_ITEM_RECEIVED_PATH: String = "/message-item-received"
    private var first=true
    private val PERMISSION_REQUEST_READ_BODY_SENSORS = 1

    private var messageEvent: MessageEvent? = null
    private var mobileNodeUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn_sending.setOnClickListener();
        val btn_sending = findViewById(R.id.btn_sending) as Button
        // set on-click listener
        btn_sending.setOnClickListener {
            // your code to perform when the user clicks on the button
            if(  btn_sending.text=="Start Job")
            { StartPressed  = true
                btn_sending.text="Stop Job"
                btn_sending.setBackgroundColor(Color.RED)
                deviceconnectionStatusTv.text = "Running..."
            }
            else{
                StartPressed  = false
                btn_sending.text="Start Job"

                val sensorList = mSensorManager!!.getSensorList(Sensor.TYPE_ALL)
                // Iterate through the list of sensors, get the sensor name,
                // append it to the string.
                val sensorText = java.lang.StringBuilder()
                //String sensorText = "";
                //String sensorText = "";
                for (currentSensor in sensorList) {
                    sensorText.append(currentSensor.name).append(
                        System.getProperty("line.separator")
                    )
                }
                val nodeId = messageEvent?.sourceNodeId!!
                // Set the data of the message to be the bytes of the Uri.
                val payload: ByteArray = sensorText.toString().toByteArray()

                // Send the rpc
                // Instantiates clients without member variables, as clients are inexpensive to
                // create. (They are cached and shared between GoogleApi instances.)
                val sendMessageTask =
                    Wearable.getMessageClient(activityContext!!)
                        .sendMessage(nodeId, MESSAGE_ITEM_RECEIVED_PATH, payload)


                deviceconnectionStatusTv.text = "Job stoped"
            }
        }
        initSensor()
        activityContext = this
        Log.d(TAG_MESSAGE_RECEIVED, "Wear App started")

    }

    override fun onDataChanged(p0: DataEventBuffer) {
    }

    override fun onCapabilityChanged(p0: CapabilityInfo) {
    }


    private fun initSensor() {

        mSensorManager =  getSystemService(Context.SENSOR_SERVICE) as SensorManager

        mSensorTemp = mSensorManager!!.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        mSensorHeart = mSensorManager!!.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        mSensorLight = mSensorManager!!.getDefaultSensor(Sensor.TYPE_LIGHT);

        mSensorManager!!.registerListener(this, mSensorTemp, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager!!.registerListener(this, mSensorHeart, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager!!.registerListener(this, mSensorLight, SensorManager.SENSOR_DELAY_NORMAL);

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun currentTimeStr(): String? {
        val c: Calendar = Calendar.getInstance()
        val df = SimpleDateFormat("HH:mm:ss")
        return df.format(c.getTime())
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }


    @SuppressLint("SetTextI18n")
    override fun onMessageReceived(p0: MessageEvent) {
        try {
            Log.d(TAG_MESSAGE_RECEIVED, "onMessageReceived event received")
            val s1 = String(p0.data, StandardCharsets.UTF_8)
            val messageEventPath: String = p0.path

            Log.d(
                TAG_MESSAGE_RECEIVED,
                "onMessageReceived() A message from watch was received:"
                        + p0.requestId
                        + " "
                        + messageEventPath
                        + " "
                        + s1
            )

            //Send back a message back to the source node
            //This acknowledges that the receiver activity is open
            if (messageEventPath.isNotEmpty() && messageEventPath == APP_OPEN_WEARABLE_PAYLOAD_PATH) {
                try {
                    // Get the node id of the node that created the data item from the host portion of
                    // the uri.
                    val nodeId = p0.sourceNodeId.toString()
                    // Set the data of the message to be the bytes of the Uri.
                    val returnPayloadAck = wearableAppCheckPayloadReturnACK
                    val payload: ByteArray = returnPayloadAck.toByteArray()

                    // Send the rpc
                    // Instantiates clients without member variables, as clients are inexpensive to
                    // create. (They are cached and shared between GoogleApi instances.)
                    var sendMessageTask =
                        Wearable.getMessageClient(activityContext!!)
                            .sendMessage(nodeId, APP_OPEN_WEARABLE_PAYLOAD_PATH, payload)

                    Log.d(
                        TAG_MESSAGE_RECEIVED,
                        "Acknowledgement message successfully with payload : $returnPayloadAck"
                    )

                    messageEvent = p0
                    mobileNodeUri = p0.sourceNodeId

                    sendMessageTask.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d(TAG_MESSAGE_RECEIVED, "Message sent successfully")
                            messagelogTextView.visibility = View.VISIBLE

                            val sbTemp = StringBuilder()
                            sbTemp.append("\nMobile device connected.")
                            Log.d("receive1", " $sbTemp")
                            messagelogTextView.append(sbTemp)

                            mobileDeviceConnected = true


                            deviceconnectionStatusTv.text = "Mobile device is connected"
                        } else {
                            Log.d(TAG_MESSAGE_RECEIVED, "Message failed.")
                        }
                    }


                } catch (e: Exception) {
                    Log.d(
                        TAG_MESSAGE_RECEIVED,
                        "Handled in sending message back to the sending node"
                    )
                    e.printStackTrace()
                }
            }//emd of if
            else if (messageEventPath.isNotEmpty() && messageEventPath == MESSAGE_ITEM_RECEIVED_PATH) {
                try {
                    setSensorData(s1)

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            Log.d(TAG_MESSAGE_RECEIVED, "Handled in onMessageReceived")
            e.printStackTrace()
        }
    }



    override fun onSensorChanged(event: SensorEvent?) {
        if(!mWearBodySensorsPermissionApproved){
            try {
                permissionRequest()
            }

            catch (e: Exception) {
                e.printStackTrace()
            }
        }
        if (mobileDeviceConnected) {
        // The sensor type (as defined in the Sensor class).
        val sensorType: Int = event!!.sensor.getType()


        // The new data value of the sensor.  Both the light and proximity
        // sensors report one value at a time, which is always the first
        // element in the values array.
        val currentValue: Float = event.values.get(0)

        when (sensorType) {
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                CurrentSensorData[0] = "temp : $currentValue"
                sendSensorData()
            }
            Sensor.TYPE_HEART_RATE -> {
                CurrentSensorData[1] = "heart : $currentValue"
                sendSensorData()
            }
            Sensor.TYPE_LIGHT -> {
                CurrentSensorData[2] = "light : $currentValue"
                sendSensorData()
            }
            else -> {
            }
        }


    }

    }

    private fun setSensorData(sbTemp: String) {
                    if (StartPressed) {
                        val sbTempArr =  sbTemp.split(":")
                        if (sbTempArr[0].trim()=="temp"){
                            CurrentSensorData[0]="temp : "+sbTempArr[1]
                        }
                        if (sbTempArr[0].trim()=="heart"){
                            CurrentSensorData[1]="heart : "+ sbTempArr[1]
                        }
                        sendSensorData()
                    }

}
    private fun sendSensorData() {
        if (StartPressed) {

            val CurrentSensorDataString=CurrentSensorData[0]+"\n"+CurrentSensorData[1]+"\n"+CurrentSensorData[2]+"\n"
            if (CurrentSensorDataString != LastSensorData) {
                val nodeId = messageEvent?.sourceNodeId!!
                // Set the data of the message to be the bytes of the Uri.
                val payload: ByteArray = CurrentSensorDataString.toByteArray()

                // Send the rpc
                // Instantiates clients without member variables, as clients are inexpensive to
                // create. (They are cached and shared between GoogleApi instances.)
                val sendMessageTask =
                    Wearable.getMessageClient(activityContext!!)
                        .sendMessage(nodeId, MESSAGE_ITEM_RECEIVED_PATH, payload)
                deviceconnectionStatusTv.text = "Running..."
                LastSensorData=CurrentSensorDataString
            }
        }

    }
    fun permissionRequest() {
        mWearBodySensorsPermissionApproved =
            (ActivityCompat.checkSelfPermission(this, Manifest.permission.BODY_SENSORS)
                    === PackageManager.PERMISSION_GRANTED)
        if (!mWearBodySensorsPermissionApproved) {
            // On 23+ (M+) devices, GPS permission not granted. Request permission.
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
            Wearable.getMessageClient(activityContext!!).removeListener(this)
            Wearable.getCapabilityClient(activityContext!!).removeListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    override fun onResume() {
        super.onResume()
        try {
            Wearable.getDataClient(activityContext!!).addListener(this)
            Wearable.getMessageClient(activityContext!!).addListener(this)
            Wearable.getCapabilityClient(activityContext!!)
                .addListener(this, Uri.parse("wear://"), CapabilityClient.FILTER_REACHABLE)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}

private fun Button.setOnClickListener() {

}
