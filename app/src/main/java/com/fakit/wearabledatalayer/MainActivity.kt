package com.fakit.wearabledatalayer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.StandardCharsets
import java.util.*


class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {
    var activityContext: Context? = null
    private val wearableAppCheckPayload = "AppOpenWearable"
    private val wearableAppCheckPayloadReturnACK = "AppOpenWearableACK"
    private var wearableDeviceConnected: Boolean = false

    private var currentAckFromWearForAppOpenCheck: String? = null
    private val APP_OPEN_WEARABLE_PAYLOAD_PATH = "/APP_OPEN_WEARABLE_PAYLOAD"

    private val MESSAGE_ITEM_RECEIVED_PATH: String = "/message-item-received"

    private val TAG_GET_NODES: String = "getnodes1"
    private val TAG_MESSAGE_RECEIVED: String = "receive1"

    private var messageEvent: MessageEvent? = null
    private var wearableNodeUri: String? = null
    private var curNode=""
    val TemperatureValues: Queue<Double> = LinkedList<Double>()
    val HeartValues : Queue<Double> = LinkedList<Double>()
    val LightValues : Queue<Double> = LinkedList<Double>()
    val BlutPresValues : Queue<Double> = LinkedList<Double>()
    val AcceleroValues : Queue<Double> = LinkedList<Double>()

    var isAMan:Boolean=true
    var fieber:Boolean=false
    var inActivity:Boolean=false
    var GelenckSchmerzen:Boolean=false
    var positivTest:Boolean=false
    var alkohol:Boolean=false


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityContext = this
        wearableDeviceConnected = false


        gender.setOnClickListener{
            isAMan=!isAMan
        }
        checkwearablesButton.setOnClickListener {
            if (!wearableDeviceConnected) {
                val tempAct: Activity = activityContext as MainActivity
                AsyncTask.execute {
                    try {
                        val getNodesResBool = getNodes(tempAct.applicationContext)

                        //UI thread
                        tempAct.runOnUiThread {
                            if (getNodesResBool!![0]) {
                                //if message Acknowlegement Received
                                 if (getNodesResBool[1]) {
                                    Toast.makeText(
                                        activityContext,
                                        "Wearable device paired and app is open. Tap the \"Send Message to Wearable\" button to send the message to your wearable device.",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    deviceconnectionStatusTv.text =
                                        "Wearable device paired and app is open."
                                    deviceconnectionStatusTv.visibility = View.VISIBLE
                                    wearableDeviceConnected = true
                                    sendmessageButton.visibility = View.VISIBLE
                                } else {
                                Toast.makeText(
                                    activityContext,
                                    "A wearable device is paired",
                                    Toast.LENGTH_LONG
                                ).show()
                                deviceconnectionStatusTv.text =
                                    "Wearable device paired"
                                deviceconnectionStatusTv.visibility = View.VISIBLE
                                wearableDeviceConnected = true
                                sendmessageButton.visibility = View.GONE
                            }
                            } else {
                                Toast.makeText(
                                    activityContext,
                                    "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
                                    Toast.LENGTH_LONG
                                ).show()
                                deviceconnectionStatusTv.text =
                                    "Wearable device not paired and connected."
                                deviceconnectionStatusTv.visibility = View.VISIBLE
                                wearableDeviceConnected = false
                                sendmessageButton.visibility = View.GONE
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        Speichernbutton.setOnClickListener{
            fieber=Fieber.isChecked
            inActivity=On_activity.isChecked
            GelenckSchmerzen=gelenkschmerzen.isChecked
            positivTest=coronaTest.isChecked
            alkohol=alkoholGetrunken.isChecked

            mainAppView.visibility=View.VISIBLE
            settingsLayout.visibility=View.GONE
            settingsbutton.visibility=View.VISIBLE
        }
        Abrechenbutton.setOnClickListener{
            Fieber.isChecked=fieber
            On_activity.isChecked=  inActivity
            gelenkschmerzen.isChecked=GelenckSchmerzen
            coronaTest.isChecked=positivTest
            alkoholGetrunken.isChecked=alkohol
            mainAppView.visibility=View.VISIBLE
            settingsLayout.visibility=View.GONE
            settingsbutton.visibility=View.VISIBLE
        }
        settingsbutton.setOnClickListener {
            settingsbutton.visibility=View.GONE
            mainAppView.visibility=View.GONE
            settingsLayout.visibility=View.VISIBLE
        }
        sendmessageButton.setOnClickListener {
            if (wearableDeviceConnected) {
                if (messagecontentEditText?.text!!.isNotEmpty()) {

                    var nodeId: String = messageEvent?.sourceNodeId!!
                    if (nodeId=="start"){
                        nodeId="1"
                    }
                    else nodeId="0"
                    // Set the data of the message to be the bytes of the Uri.

                    val content=messagecontentEditText.text.toString()
                    val payload: ByteArray = content.toByteArray()

                    // Send the rpc
                    // Instantiates clients without member variables, as clients are inexpensive to
                    // create. (They are cached and shared between GoogleApi instances.)
                    val sendMessageTask =
                        Wearable.getMessageClient(activityContext!!)
                            .sendMessage(curNode, MESSAGE_ITEM_RECEIVED_PATH, payload)

                    sendMessageTask.addOnCompleteListener {
                        if (it.isSuccessful) {
                            Log.d("send1", "Command sent successfully")
                            val sbTemp = StringBuilder()
                            sbTemp.append("\n")
                            sbTemp.append(content)
                            sbTemp.append(" (Sent to Wearable)")
                            Log.d("receive1", " $sbTemp")
                            messagelogTextView?.append(sbTemp)
                            scrollviewText.post { scrollviewText.fullScroll(ScrollView.FOCUS_DOWN) }
                            //scrollviewText.requestFocus()
                            //scrollviewText.post {
                            //    scrollviewText.scrollTo(0, scrollviewText.bottom)
                            //}
                        } else {
                            Log.d("send1", "Message failed.")
                        }
                    }
                } else {
                    Toast.makeText(
                        activityContext,
                        "Message content is empty. Please enter either <<start>> or <<stop>>",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    private fun getNodes(context: Context): BooleanArray? {
        val nodeResults = HashSet<String>()
        val resBool = BooleanArray(2)
        resBool[0] = false //nodePresent
        resBool[1] = false //wearableReturnAckReceived
        val nodeListTask =
            Wearable.getNodeClient(context).connectedNodes
        try {
            // Block on a task and get the result synchronously (because this is on a background thread).
            val nodes =
                Tasks.await(
                    nodeListTask
                )
            Log.e(TAG_GET_NODES, "Task fetched nodes")
            for (node in nodes) {
                Log.e(TAG_GET_NODES, "inside loop")
                nodeResults.add(node.id)
                try {
                    val nodeId = node.id
                    curNode=nodeId
                    // Set the data of the message to be the bytes of the Uri.
                    val payload: ByteArray = wearableAppCheckPayload.toByteArray()
                    // Send the rpc
                    // Instantiates clients without member variables, as clients are inexpensive to
                    // create. (They are cached and shared between GoogleApi instances.)
                    val sendMessageTask =
                        Wearable.getMessageClient(context)
                            .sendMessage(nodeId, APP_OPEN_WEARABLE_PAYLOAD_PATH, payload)
                    try {
                        // Block on a task and get the result synchronously (because this is on a background thread).
                        val result = Tasks.await(sendMessageTask)
                        Log.d(TAG_GET_NODES, "send message result : $result")
                        resBool[0] = true
                        //Wait for 1000 ms/1 sec for the acknowledgement message
                        //Wait 1
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(100)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 1")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 2
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(150)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 2")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 3
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(200)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 3")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 4
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(250)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 4")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        //Wait 5
                        if (currentAckFromWearForAppOpenCheck != wearableAppCheckPayloadReturnACK) {
                            Thread.sleep(350)
                            Log.d(TAG_GET_NODES, "ACK thread sleep 5")
                        }
                        if (currentAckFromWearForAppOpenCheck == wearableAppCheckPayloadReturnACK) {
                            resBool[1] = true
                            return resBool
                        }
                        resBool[1] = false
                        Log.d(
                            TAG_GET_NODES,
                            "ACK thread timeout, no message received from the wearable "
                        )
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                    }
                } catch (e1: Exception) {
                    Log.d(TAG_GET_NODES, "send message exception")
                    e1.printStackTrace()
                }
            } //end of for loop
        } catch (exception: Exception) {
            Log.e(TAG_GET_NODES, "Task failed: $exception")
            exception.printStackTrace()
        }
        return resBool
    }
    override fun onDataChanged(p0: DataEventBuffer) {
    }
    var Running: Boolean = false
    @SuppressLint("SetTextI18n")
    override fun onMessageReceived(p0: MessageEvent) {

        if (!Running) {
            Running = true
            messagelogTextView.append("\n Running...")
            try {
                val s = String(p0.data, StandardCharsets.UTF_8)
                val messageEventPath: String = p0.path
                Log.d(
                    TAG_MESSAGE_RECEIVED,
                    "onCommandReceived() Received Data from watch:"
                            + p0.requestId
                            + " "
                            + messageEventPath
                            + " "
                            + s
                )

                if (messageEventPath == APP_OPEN_WEARABLE_PAYLOAD_PATH) {
                    currentAckFromWearForAppOpenCheck = s
                    Log.d(
                        TAG_MESSAGE_RECEIVED,
                        "Received acknowledgement message that app is open in wear"
                    )

                    val sbTemp = StringBuilder()
                    sbTemp.append(messagelogTextView.text.toString())
                    sbTemp.append("\nWearable device connected.")
                    Log.d("receive1", " $sbTemp")
                    messagelogTextView.text = sbTemp
                    textInputLayout.visibility = View.VISIBLE

                    checkwearablesButton?.visibility = View.GONE
                    messageEvent = p0
                    wearableNodeUri = p0.sourceNodeId
                } else if (messageEventPath.isNotEmpty() && messageEventPath == MESSAGE_ITEM_RECEIVED_PATH) {
                    try {
                        messagelogTextView.visibility = View.VISIBLE
                        textInputLayout?.visibility = View.VISIBLE
                        sendmessageButton?.visibility = View.VISIBLE
                        graph.visibility = View.VISIBLE


                        val sbTemp = StringBuilder()
                        sbTemp.append("\nNew Data -")
                        sbTemp.append(s)
                        sbTemp.append(" - (from wear) \n")
                        Log.d("receive1", " $sbTemp")
                        messagelogTextView.append(sbTemp)

                        var curGraph = findViewById(R.id.graph) as GraphView
                        curGraph.removeAllSeries()

                        PlotData(curGraph, TemperatureValues, Color.GREEN, 10, 3)
                        PlotData(curGraph, HeartValues, Color.RED, 10, 3)
                        PlotData(curGraph, LightValues, Color.YELLOW, 30, 1)

                        handleDataAsLists(s)
                        scrollviewText.requestFocus()
                        // scrollviewText.post {scrollviewText.scrollTo(0, scrollviewText.bottom)                    }
                        scrollviewText.post { scrollviewText.fullScroll(ScrollView.FOCUS_DOWN) }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("receive1", "Handled")
            }
            Running = false
        }
    }
    private fun PlotData(curGraph:GraphView,PlotsValues:Queue<Double>, lineColor: Int,maxDataPoint:Int,steps:Int) {
        if(PlotsValues.size>2){
            messagelogTextView.append(" \n ")
            var plot=0.00
            var series: LineGraphSeries<DataPoint> = LineGraphSeries(
                arrayOf( DataPoint(0.0, 0.0) ))
            series.setColor(lineColor)
            PlotsValues.forEach {
                series.appendData(DataPoint(plot,it),true,maxDataPoint,true)
                plot+=steps
                messagelogTextView.append(it.toString()+" | ")
            }
            curGraph.addSeries(series)
        }
    }
    private fun handleDataAsLists(s: String) {
        var TempAvg=TemperatureValues.average()
        var HeartAvg=HeartValues.average()
        var LightAvg=LightValues.average()
        var BlutPresAvg=BlutPresValues.average()
        var AcceleroAvg=AcceleroValues.average()
        val lineValue =s.split("\n").iterator()
        // do something with the rest of elements
        lineValue.forEach {
            var KeyValue =  it.split(":").toTypedArray()
            if (KeyValue.size>1) {

                var value = KeyValue[1].trim().toDouble()

                if (value > 0.0) {
                    var key = KeyValue[0].trim()

                    when (key) {
                        "temp" -> {

                            if (TemperatureValues.size > 0) {
                                if (TemperatureValues.last() != value) TemperatureValues.add(value)
                            } else TemperatureValues.add(value)

                            if (TemperatureValues.size > 10 || (TemperatureValues.size > 1 && TemperatureValues.first() == 0.0)) {
                                TemperatureValues.poll()
                            }
                            TempAvg = TemperatureValues.average()
                            if (TempAvg < 35.00 || (TempAvg > 39.00 && !inActivity)||
                                (TempAvg > 38.00 && (fieber||GelenckSchmerzen))||
                                (TempAvg <37.00 && alkohol)) {
                              AlertSignal("your temperature is now " + TempAvg + " please just visit a Doctor for further informations !")
                            }
                            if (TempAvg > 37.00 && !inActivity&&LightAvg>20) {
                                AlertSignal("your temperature is now " + TempAvg + " please just visit a Doctor for further informations !")
                            }
                        }

                        "heart" -> {

                            if (HeartValues.size > 0) {
                                if (HeartValues.last() != value) HeartValues.add(value)
                            } else HeartValues.add(value)
                            if (HeartValues.size > 10 || (HeartValues.size > 1 && HeartValues.first() == 0.0)) {
                                HeartValues.poll()
                            }
                            HeartAvg = HeartValues.average()
                            if (HeartAvg < 60.00 || HeartAvg > 100.00) {
                                AlertSignal("your Heart Values is now " + HeartAvg + " please just visit a Doctor for further informations !")
                            }
                        }

                        "light" -> {
                            if (LightValues.size > 0) {
                                if (LightValues.last() != value) LightValues.add(value)
                            } else LightValues.add(value)
                            if (LightValues.size > 30 || (LightValues.size > 1 && LightValues.first() == 0.0)) {
                                LightValues.poll()
                            }
                            LightAvg = LightValues.average()
                            if (LightAvg > 100.00) {
                                AlertSignal("your light is now " + LightAvg + " !")
                            }
                        }
                    }
                }
            }
        }
        val sbTemp = StringBuilder()
        sbTemp.append("\n\n Result Annalysis - ")
        sbTemp.append("\nTemp AVG = " + TempAvg +" (Size: "+TemperatureValues.size+")")
        sbTemp.append("\nHeart AVG = " + HeartAvg+" (Size: "+HeartValues.size+")")
        sbTemp.append("\nLight AVG = " + LightAvg+" (Size: "+LightValues.size+")")
        sbTemp.append("\n - (End Annalysis) \n\n")
        messagelogTextView.append(sbTemp)
        messagelogTextView.append("------------------------------------------------------------------------------------------------------------")
        scrollviewText.post { scrollviewText.fullScroll(ScrollView.FOCUS_DOWN) }
    }
    private fun AlertSignal(s: String) {
        messagelogTextView.append("\n\n ALLERT!!! \n" + s + " \n")
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
    override fun onCapabilityChanged(p0: CapabilityInfo) {
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
