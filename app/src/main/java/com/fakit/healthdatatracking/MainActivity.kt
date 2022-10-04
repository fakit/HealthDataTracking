package com.fakit.healthdatatracking

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.*
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.provider.Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS
import android.provider.Settings.SettingNotFoundException
import android.text.TextUtils
import android.text.TextUtils.SimpleStringSplitter
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.*
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_main.*
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.collections.ArrayList

var CurrentActivity : MainActivity?=MainActivity();
public class MainActivity : AppCompatActivity(), DataClient.OnDataChangedListener,
    MessageClient.OnMessageReceivedListener,
    CapabilityClient.OnCapabilityChangedListener {

   // private val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"
   // private val ACTION_NOTIFICATION_LISTENER_SETTINGS =        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"

    private var valueChanged: Boolean = false
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
    private var dbHandler: DBHandler? = null


    private var imageChangeBroadcastReceiver: ReceiveBroadcastReceiver? = null
    private var enableNotificationListenerAlertDialog: AlertDialog? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = DBHandler(this);

        activityContext = this

        checkwearablesButton.setOnClickListener {
            wear_connection()
        }


        if (!isNotificationServiceEnabled()) {
            enableNotificationListenerAlertDialog = buildNotificationServiceAlertDialog()
            enableNotificationListenerAlertDialog!!.show()
        }
        // Finally we register a receiver to tell the MainActivity when a notification has been received
        imageChangeBroadcastReceiver = ReceiveBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.fakit.healthdatatracking")
        registerReceiver(imageChangeBroadcastReceiver, intentFilter)

        checkrecommandation()
        CurrentActivity=this@MainActivity
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val findMenuItems = menuInflater
        findMenuItems.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
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

                    messageEvent = p0
                    wearableNodeUri = p0.sourceNodeId
                    wearableDeviceConnected=true
                } else if (messageEventPath.isNotEmpty() && messageEventPath == MESSAGE_ITEM_RECEIVED_PATH  && wearableDeviceConnected) {
                    try {
                        val sbTemp = StringBuilder()
                        sbTemp.append("\n New Data -")
                        sbTemp.append(s)
                        sbTemp.append(" - (from wear) \n")
                        Log.d("receive1", " $sbTemp")

                        handleData(s)
                        PlotData()
                        scrollviewText.requestFocus()
                        scrollviewText.post {scrollviewText.scrollTo(0, scrollviewText.bottom)                    }
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
    private fun PlotData(){

        var curGraph = findViewById(R.id.graph) as GraphView
        curGraph.removeAllSeries()
        DoPlot(curGraph,  dbHandler!!.getPlotData("light"), Color.YELLOW, 100, 1)
        DoPlot(curGraph,  dbHandler!!.getPlotData("temperature"), Color.GREEN, 100, 1)
        DoPlot(curGraph,  dbHandler!!.getPlotData("heartbeat"), Color.RED, 100, 1)
        DoPlot(curGraph,  dbHandler!!.getPlotData("pressure"), Color.BLUE, 100, 1)

    }
    private fun DoPlot(curGraph:GraphView,PlotsValues:ArrayList<Double>, lineColor: Int,maxDataPoint:Int,steps:Int) {


        if(PlotsValues.size>2){

            var plot=0.00
            var series: LineGraphSeries<DataPoint> = LineGraphSeries(
                arrayOf( DataPoint(0.0, 0.0) ))
            series.setColor(lineColor)
            PlotsValues.forEach {
                series.appendData(DataPoint(plot,it),true,maxDataPoint,true)
                plot+=steps

            }
            curGraph.addSeries(series)
        }
    }
    private fun handleData(s: String) {
        var LastWearData = dbHandler!!.lastWearData

        val lineValue =s.split("\n").iterator()
        valueChanged = false
        // do something with the rest of elements
        lineValue.forEach {
            var KeyValue =  it.split(":").toTypedArray()
            if (KeyValue.size>1) {

                var value = KeyValue[1].trim().toFloat()

                if (value > 0.0f) {
                    var key = KeyValue[0].trim()

                    when (key) {
                        "temp" -> {
                            if (LastWearData.Temp_COL!=value) {
                                LastWearData.Temp_COL=value
                                valueChanged = true
                            }

                        }

                        "heart" -> {
                            if (LastWearData.Heart_COL!=value) {
                                LastWearData.Heart_COL=value
                                valueChanged = true
                            }

                        }

                        "pressure" -> {

                            if (LastWearData.Press_COL!=value) {
                                LastWearData.Press_COL=value
                                valueChanged = true;
                            }

                        }
                        "light" -> {

                            if (LastWearData.Light_COL!=value) {
                                LastWearData.Light_COL=value
                                valueChanged = true;
                            }

                        }

                        "linearacceleration" -> {

                            if (LastWearData.LinearAccelero_COL!=value) {
                                LastWearData.LinearAccelero_COL=value
                                valueChanged = true;
                            }

                        }
                        "rhumid" -> {

                            if (LastWearData.Rhumid_COL!=value) {
                                LastWearData.Rhumid_COL=value
                                valueChanged = true;
                            }

                        }

                    }
                }
            }
        }
        if( valueChanged){

            dbHandler!!.AddWearData(Date().getTime().toString(),LastWearData.Temp_COL, LastWearData.Heart_COL,
                LastWearData.Press_COL,LastWearData.Light_COL,LastWearData.LinearAccelero_COL, LastWearData.Rhumid_COL );
            checkrecommandation()

        }
}
    // SensorData
    val CG_TempGewicht1=1
    val CG_TempGewicht2=4
    val CG_TempGewicht3=16
    val CG_TempGewicht4=20
    val CG_TempGewicht5=26
    val CG_TempGewicht6=30

    val CG_HeartGewicht=10
    val CG_PressureGewicht=10

    val CG_LightGewicht1=-5
    val CG_LightGewicht2=5

    val CG_RhumidGewicht1=-20
    val CG_RhumidGewicht2=20


    // Illness
    val CG_Age1=6
    val CG_Age2=15
    val CG_Age3=27
    val CG_Age4=38

    val CG_weight=25
    val CG_sex=10


    // Illness
    val CG_Herzerkrankungen=20
    val CG_Lungenerkrankungen=20
    val CG_Diabetesmellitus=20
    val CG_Nierenerkrankungen=20
    val CG_Malignom=20

    // Main Symptomes

    val CG_GeruchsGechmack=19
    val CG_Schnupfen=31
    val CG_Husten=42


    // Other symptoms
    val CG_MuskelundGliederschmerzen=10
    val CG_Bauchschmerzen=5
    val CG_Bindehautentzuendung=1
    val CG_StarkeBindehautentzuendung=3
    val CG_Hautausschlag=1
    val CG_leichterespiratorischeSymptome=4
    val CG_Atemnot=4
    val CG_HalsundKopfschmerzen=4
    val CG_Schuettelfrost=4
    val CG_uebelkeitErbrechen=4
    val CG_Durchfall=5
    val CG_Appetitlosigkeit=10
    val CG_Lymphknotenschwellung=1
    val CG_Apathie=1
    val CG_Benommenheit=1
    val CG_LeichteErkaeltungssymptome=10
    val CG_Pneumonie1Grad=2
    val CG_Pneumonie2Grad=5


    //Logik
    val Gruppen_AnteilBodydata = 35.00
    val Gruppen_AnteilOfMainSyptomes= 26.00
    val Gruppen_AnteilIllness= 22.00
    val Gruppen_AnteilOfOderSyptomes=17.00
     fun checkrecommandation() {

        // Get BodysensorData

        // below variable is for average  temperature column
        val AVG_Temp = getavg("temperature")

        // below variable is for average  Heartbeat column
        val AVG_Heart = getavg("heartbeat")

        // below variable for average pressure column.
        val AVG_Pressure = getavg("pressure")

        // below variable id for average Light column.
        val AVG_Light =getavg("light")

        // below variable for average linearaccelero column.
        val AVG_LinearAccelero = getavg("linearaccelero")

        // below variable for average Rhumid column.
        val AVG_Rhumid = getavg("rhumid")

        var TempGewicht =0
        if(AVG_Temp>36.5 &&AVG_Temp<37.4) TempGewicht=CG_TempGewicht1
        else if(AVG_Temp<=38) TempGewicht=CG_TempGewicht2
        else if(AVG_Temp<=38.5) TempGewicht=CG_TempGewicht3
        else if(AVG_Temp<=39) TempGewicht=CG_TempGewicht4
        else if(AVG_Temp<=39.9) TempGewicht=CG_TempGewicht5
        else if(AVG_Temp>40) TempGewicht=CG_TempGewicht6

        var HeartGewicht =0
        if(AVG_Heart>99) HeartGewicht=CG_HeartGewicht

        var PressureGewicht =0
        if(AVG_Pressure>130 && AVG_Pressure<120) PressureGewicht=CG_PressureGewicht

        var LightGewicht =0
        if(AVG_Light>100) LightGewicht=CG_LightGewicht1
        else if(AVG_Light<50) LightGewicht=CG_LightGewicht2

        var RhumidGewicht =0
        if(AVG_Rhumid>35 && AVG_Rhumid<65) RhumidGewicht=CG_RhumidGewicht1
        else  RhumidGewicht=CG_RhumidGewicht2

        var ListOfBodysensorData = ArrayList<Int>(listOf(TempGewicht,HeartGewicht,PressureGewicht,
            LightGewicht,RhumidGewicht))

        // berechne Anteil
        var AnteilOfBodySensorData = rechneAnteil( ListOfBodysensorData,listOf(CG_TempGewicht6,CG_HeartGewicht,
            CG_PressureGewicht,CG_LightGewicht2,CG_RhumidGewicht2).sum())




         //User Data

         var age =0
         var weight =0
         var sex =0

         var user = dbHandler!!.lastUser
        if(user.Age>79){
            age=CG_Age4
        }else {
            age = CG_Age3
            if (user.Age < 69) age = CG_Age2
            if (user.Age < 60) age = CG_Age1
        }

        if(user.Weight>100){
            weight=CG_weight
                }

         if ( ! user.Sex.equals("weiblich")){
             sex=CG_sex
         }


         // berechne Anteil

         val UserPrediktor = rechneAnteil( ArrayList<Int>(listOf(age,weight,sex)),listOf(CG_Age4,CG_weight,CG_sex).sum())

         //Get Illness
        var Iillness = dbHandler!!.getSymptomes("illness")

        var Herzerkrankungen  =0
        if(Iillness[0]) Herzerkrankungen=CG_Herzerkrankungen
        var Lungenerkrankungen=  0
        if(Iillness[1]) Lungenerkrankungen=CG_Lungenerkrankungen
        var Diabetesmellitus= 0
        if(Iillness[2]) Diabetesmellitus=CG_Diabetesmellitus
        var Nierenerkrankungen  =0
        if(Iillness[3]) Nierenerkrankungen=CG_Nierenerkrankungen
        var Malignom=  0
        if(Iillness[4]) Malignom=CG_Malignom

        var ListOfIllness = ArrayList<Int>(listOf(Herzerkrankungen,Lungenerkrankungen,Diabetesmellitus,
            Nierenerkrankungen,Malignom))

        // berechne Anteil
        var AnteilOfIllness = rechneAnteil( ListOfIllness,listOf(CG_Herzerkrankungen,CG_Lungenerkrankungen,
            CG_Diabetesmellitus,CG_Nierenerkrankungen,CG_Malignom).sum())

        // Get CORONA WARN APP Notifications
        val CoronaLevel=dbHandler!!.coronaLevel

        //Get MainSymptomsData
        var MainSymptomes = dbHandler!!.getSymptomes("mainsymptomes")

        var GeruchsGechmack  =0
        if(MainSymptomes[0]) GeruchsGechmack=CG_GeruchsGechmack
        var Schnupfen=  0
        if(MainSymptomes[1]) Schnupfen=CG_Schnupfen
        var Husten= 0
        if(MainSymptomes[2]) Husten=CG_Husten

        var ListOfMainSyptomes = ArrayList<Int>(listOf(GeruchsGechmack,Schnupfen,Husten))

        // berechne Anteil
        var AnteilOfMainSyptomes = rechneAnteil( ListOfMainSyptomes,listOf(CG_GeruchsGechmack,
            CG_Schnupfen,CG_Husten).sum())



        //Get Score  Oder SymptomsData
        var OderSymptomes = dbHandler!!.getSymptomes("odersymptomes")

        var MuskelundGliederschmerzen =0
        if(OderSymptomes[0]) MuskelundGliederschmerzen=CG_MuskelundGliederschmerzen
         var Bauchschmerzen =0
        if(OderSymptomes[1]) Bauchschmerzen=CG_Bauchschmerzen
         var Bindehautentzuendung =0
        if(OderSymptomes[2]) Bindehautentzuendung=CG_Bindehautentzuendung
         var StarkeBindehautentzuendung =0
        if(OderSymptomes[3]) StarkeBindehautentzuendung=CG_StarkeBindehautentzuendung
         var Hautausschlag =0
        if(OderSymptomes[4]) Hautausschlag=CG_Hautausschlag
         var leichterespiratorischeSymptome =0
        if(OderSymptomes[5]) leichterespiratorischeSymptome=CG_leichterespiratorischeSymptome
         var Atemnot =0
        if(OderSymptomes[6]) Atemnot=CG_Atemnot
         var HalsundKopfschmerzen =0
        if(OderSymptomes[7]) HalsundKopfschmerzen=CG_HalsundKopfschmerzen
         var Schuettelfrost =0
        if(OderSymptomes[8]) Schuettelfrost=CG_Schuettelfrost
         var uebelkeitErbrechen =0
        if(OderSymptomes[9]) uebelkeitErbrechen=CG_uebelkeitErbrechen
         var Durchfall =0
        if(OderSymptomes[10]) Durchfall=CG_Durchfall
         var Appetitlosigkeit =0
        if(OderSymptomes[11]) Appetitlosigkeit=CG_Appetitlosigkeit
         var Lymphknotenschwellung =0
        if(OderSymptomes[12]) Lymphknotenschwellung=CG_Lymphknotenschwellung
         var Apathie =0
        if(OderSymptomes[13]) Apathie=CG_Apathie
         var Benommenheit =0
        if(OderSymptomes[14]) Benommenheit=CG_Benommenheit
         var LeichteErkaeltungssymptome =0
        if(OderSymptomes[15]) LeichteErkaeltungssymptome=CG_LeichteErkaeltungssymptome
         var Pneumonie1Grad =0
        if(OderSymptomes[16]) Pneumonie1Grad=CG_Pneumonie1Grad
         var Pneumonie2Grad =0
        if(OderSymptomes[17]) Pneumonie2Grad=CG_Pneumonie2Grad

        var ListOfOderSyptomes = ArrayList<Int>(listOf(MuskelundGliederschmerzen,Bauchschmerzen,
            Bindehautentzuendung,StarkeBindehautentzuendung,Hautausschlag,leichterespiratorischeSymptome,
            Atemnot, HalsundKopfschmerzen,Schuettelfrost,uebelkeitErbrechen,Durchfall,Appetitlosigkeit,
            Lymphknotenschwellung,Apathie,Benommenheit,LeichteErkaeltungssymptome,Pneumonie1Grad,Pneumonie2Grad))
        // berechne Anteil
        var AnteilOfOderSyptomes = rechneAnteil( ListOfOderSyptomes,listOf(CG_MuskelundGliederschmerzen,CG_Bauchschmerzen,
            CG_Bindehautentzuendung,CG_StarkeBindehautentzuendung,CG_Hautausschlag,CG_leichterespiratorischeSymptome,
            CG_Atemnot, CG_HalsundKopfschmerzen,CG_Schuettelfrost,CG_uebelkeitErbrechen,CG_Durchfall,CG_Appetitlosigkeit,
            CG_Lymphknotenschwellung,CG_Apathie,CG_Benommenheit,CG_LeichteErkaeltungssymptome,CG_Pneumonie1Grad,CG_Pneumonie2Grad).sum())


        //Gesamtanteile

        val Gesamt_AnteilBodydata = Gruppen_AnteilBodydata * AnteilOfBodySensorData / 100.00;
        val Gesamt_AnteilOfMainSyptomes= Gruppen_AnteilOfMainSyptomes * AnteilOfMainSyptomes / 100.00
        val Gesamt_AnteilIllness= Gruppen_AnteilIllness * AnteilOfIllness / 100.00
        val Gesamt_AnteilOfOderSyptomes= Gruppen_AnteilOfOderSyptomes * AnteilOfOderSyptomes / 100.00

        val GesamtSumme= Gesamt_AnteilBodydata+
                Gesamt_AnteilOfMainSyptomes+
                Gesamt_AnteilIllness+
                Gesamt_AnteilOfOderSyptomes

         var Coronaleveltext= "Niedriges Risiko ohne Risikobegegnung"
         if (CoronaLevel==2)Coronaleveltext= "Niedriges Risiko mit Risikobegegnung"
           if (CoronaLevel==3)Coronaleveltext= "Erhöhtes Risiko "

         val sbTemp =java.lang.StringBuilder()
            sbTemp.append(Date().toString()  )
            sbTemp.append("\n\n"+"Corona-Warn-App : "+ Coronaleveltext+ "\n\n"+
                    getTextRecommandation(AVG_Temp,AVG_LinearAccelero,AVG_Pressure,
            AVG_Heart,AVG_Light))

        if (CoronaLevel==1) {
            if(GesamtSumme>70){
                AlertSignal( )
                sbTemp.append("\n \n Es besteht zwar keine Wahrnung vom CoronaWarnApp, " +
                        "aber nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen Kritischen Wert ! " +
                        "\n Sie sollen unbedingt einen Artz besuchen, um mehr darüber zu erfahren !")
            }
            else if (GesamtSumme>60|| UserPrediktor>30){
                sbTemp.append("\n \n Es besteht zwar keine Wahrnung vom CoronaWarnApp, " +
                        "aber nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen hohen Wert ! " +
                        "\n Sie sollen unbedingt einen Coronatest machen !")
            }
            else {
                sbTemp.append("\n \n Es besteht keine Wahrnung vom CoronaWarnApp. " +
                        "Nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen normalen Wert. " +
                        "\n Sie sollen nur rücksich nehmen un sich weiterhin schützen !")
            }
        }

        if (CoronaLevel==2) {

            if(GesamtSumme>60){
                AlertSignal( )
                sbTemp.append("\n \n Es besteht zwar keine kritische Meldung vom CoronaWarnApp, " +
                        "aber nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen Kritischen Wert ! zudem hatten Sie eine " +
                        "entfernte Begegnung mit einem infizierte Person." +

                        "\n Sie sollen unbedingt einen Artz besuchen, um mehr darüber zu erfahren !")
            }
            else if (GesamtSumme>40){
                sbTemp.append("\n \n Es besteht zwar keine kritische Meldung vom CoronaWarnApp, " +
                        "aber nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen hohen Wert ! " +
                        "\n Sie sollen unbedingt einen PCR Coronatest machen !")
            }
            else if (GesamtSumme>30 || UserPrediktor>30){
                sbTemp.append("\n \n Es besteht zwar keine kritische Meldung vom CoronaWarnApp, " +
                        "aber nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier angesichts ihrer Benutzer Daten um einen hohen Wert ! " )
                if (AVG_Rhumid>10){
                    sbTemp.append("\n Angesich der aktuellen Feutigkeit, befinden Sie sich im " +
                            "Kältezeiten zB. im Winter, Sie sollen daher einen PCR Coronatest " +
                            "machen um mehr über eine Ansteckungsgefahr zu erfahren!")
                }
                else{
                    sbTemp.append("\n Sie sollen einen Corona Bürgertest machen !")
                }
            }
            else {
                sbTemp.append("\n \n Es besteht keine kritische Meldung vom CoronaWarnApp. " +
                        "Nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen normalen Wert. " +
                        "\n Sie sollen nur rücksich nehmen un sich weiterhin schützen !")
            }
        }

        if (CoronaLevel==3) {
            AlertSignal( )
            sbTemp.append("\n\n Achtung, die CoronaWarnApp hat eine potenzielle Ansteckung gemeldet !!")
            if(GesamtSumme>50){
                sbTemp.append("\n \n Es besteht eine kritische Meldung vom CoronaWarnApp. " +
                        "Nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen Kritischen Wert ! zudem hatten Sie eine " +
                        "entfernte Begegnung mit einem infizierte Person." +
                        "\n Sie sollen unbedingt einen Artz besuchen, um mehr darüber zu erfahren !")
            }
            else if (GesamtSumme>30|| UserPrediktor>30){
                sbTemp.append("\n \n Es besteht eine kritische Meldung vom CoronaWarnApp. " +
                        "Nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen hohen Wert ! " +
                        "\n Sie sollen unbedingt einen PCR Coronatest machen !")
            }
            else{
                sbTemp.append("\n \n Es besteht eine kritische Meldung vom CoronaWarnApp. " +
                        "Nach Berechnung liegt ihr gesamt Corona score bei "+ GesamtSumme+" " +
                        "Es handel sich hier um einen hohen Wert ! " +
                        "\n Sie sollen unbedingt einen Corona Bürgertest machen !")
            }

        }
        recomandationTextView.setText(sbTemp.toString())
        progressBar.progress=GesamtSumme.toInt()
        progressBar.visibility=View.VISIBLE
    }
    private fun rechneAnteil(values: ArrayList<Int>, sum:Int): Float {
        var Anteil=0.0f

        values.forEach {
            if (sum>0){
                //Ermittlung der MittlerenGewichtsAnteil mithilfe
                    // der Summe der Standard Corona-Gewichte
                Anteil=Anteil+ it.toFloat()*100.00f/sum.toFloat()
             }
        }
        return Anteil
    }

    val TEMP_KRITIK=false
    val IN_BEWEGUNG=false
    val BLUTDRUCK_HOCH=false
    val FEUCHTIGKEIT_HOCH=false

    private fun getTextRecommandation(
        AVG_Temp: Double, AVG_LinearAccelero: Double, AVG_Pressure: Double,
        AVG_Heart: Double, AVG_Light: Double,
    ): StringBuilder {
        val sbTemp = StringBuilder()
        sbTemp.append("\n Neuigkeiten für Sie !: \n\n")

        if (AVG_Temp<38.00 &&AVG_Temp>0.00) sbTemp.append("\n Ihre Temperatur liegt gerade bei "+AVG_Temp+" Sie haben höchstwahrscheinlich kein Fieber,")
        else  if (AVG_Temp>0.00){
            sbTemp.append(" Ihre Temperatur ist über die normale. Dies liegt bereits bei " +
                    ""+AVG_Temp+"°C. sie haben gerade bestimmt Fieber")
        }
        if (AVG_LinearAccelero>10) {
            sbTemp.append("\n Nach Überprüfung sind Sie in Bewegung mit einer Beschleunigung " +
                    "von  " + AVG_LinearAccelero + ", m/s². ")
            if(AVG_Temp>39.00){
                sbTemp.append(" ihr erhöhte Temperatur ist möglicherweise Aufgrung Ihre Bewegung.")
            }
        }
        else{
            sbTemp.append("\n Nach Überprüfung sind Sie fast im Ruhezustand.")
            if(AVG_Temp>=38.00){
                sbTemp.append("Da ihre Temperatur so hoch ist, " +
                        "leiden Sie gerade höchtswahrschienlich an Fieber")
            }
        }
        if (AVG_Pressure>80||AVG_Heart>80){
        if (AVG_Pressure>80){
            sbTemp.append("\n Ihr Blutdruck Wert liegt mit "+AVG_Pressure+" im kritiken Bereich, ")

        }
        else   {
            sbTemp.append("\n Ihr Blutdruck Wert ist normal mit einem Wert von "+AVG_Pressure+" .")
        }
        if (AVG_Heart>80){
            sbTemp.append("\n Ihr Herzschlag Wert liegt mit "+AVG_Heart+" im kritiken Bereich, ")

        }
        else   {
            sbTemp.append("\n Ihr Herzschlag Wert ist normal mit einem Wert von "+AVG_Pressure+" .")
        }
            if (AVG_Light<80){
                sbTemp.append("\n Sie befinden sich gerade im dunkel, wollen Sie vielleicht schon " +
                        "schlafen ? Aufgund Ihren Blutdruckswert und/oder ihren Herzschlagswert empfehlen wir " +
                        "ihnen vor dem Schlaff massnahmen zu ergreifen. Einige Tipss: Wasser trinken, " +
                        "sich entspannen, und ggf. Tabletten nehmen.")
            }
        }
        return sbTemp
    }
    private fun getavg(s: String): Double {

        return dbHandler!!.getPlotData(s).average();
    }
    private fun AlertSignal() {

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


/**
* Is Notification Service Enabled.
*/
private fun isNotificationServiceEnabled(): Boolean {
val pkgName = packageName
val flat = Settings.Secure.getString(
    contentResolver, "enabled_notification_listeners"
)
if (!TextUtils.isEmpty(flat)) {
    val names = flat.split(":").toTypedArray()
    for (i in names.indices) {
        val cn = ComponentName.unflattenFromString(names[i])
        if (cn != null) {
            if (TextUtils.equals(pkgName, cn.packageName)) {
                return true
            }
        }
    }
}
return false
}

private fun isAccessibilityOn(
    context: Context,
    clazz: Class<out AccessibilityService?>,
): Boolean {
var accessibilityEnabled = 0
val service = context.packageName + "/" + clazz.canonicalName
try {
    accessibilityEnabled = Settings.Secure.getInt(
        context.applicationContext.contentResolver,
        Settings.Secure.ACCESSIBILITY_ENABLED
    )
} catch (ignored: SettingNotFoundException) {
}
val colonSplitter = SimpleStringSplitter(':')
if (accessibilityEnabled == 1) {
    val settingValue = Settings.Secure.getString(
        context.applicationContext.contentResolver,
        Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
    )
    if (settingValue != null) {
        colonSplitter.setString(settingValue)
        while (colonSplitter.hasNext()) {
            val accessibilityService = colonSplitter.next()
            if (accessibilityService.equals(service, ignoreCase = true)) {
                return true
            }
        }
    }
}
return false
}

/**
* Receive Broadcast Receiver.
*/
public class ReceiveBroadcastReceiver : BroadcastReceiver() {
var notif=""
private var dbHandler: DBHandler? = null

    override fun onReceive(context: Context, intent: Intent) {
   try {

       dbHandler = DBHandler(context)
    val receivedNotificationCode = intent.getIntExtra("notificationCode", -1)
    val packages = intent.getStringExtra("package")
    val title = intent.getStringExtra("title")
    val text = intent.getStringExtra("text")



    if (text != null ) {

            val android_id = Settings.Secure.getString(context.applicationContext.contentResolver,
                Settings.Secure.ANDROID_ID
            )
            val devicemodel = Build.MANUFACTURER + Build.MODEL + Build.BRAND + Build.SERIAL

            // Save in the database.

            notif =Date().toString()+devicemodel+ " | "+ receivedNotificationCode.toString()+" | " +
                    ""+packages+" | "+title+" | "+text
                //"Notification : $receivedNotificationCode\nPackages : $packages\nTitle : $title\nText : $text\nId : $date\nandroid_id : $android_id\ndevicemodel : $devicemodel"
        Log.d("Details Received : ", "Notification : " + receivedNotificationCode + "\nPackages : " +
                packages + "\nTitle : " + title + "\nText : " + text + "\nId : " + Date()+ "\nandroid_id : " +
                android_id+ "\ndevicemodel : " + devicemodel);

            dbHandler!!.AddCoronaNotification(Date().getTime().toString(), notif);
            CurrentActivity!!.checkrecommandation()

    }
   }catch ( e: Exception){}

}
}
/**
* Build Notification Listener Alert Dialog.
*/
private fun buildNotificationServiceAlertDialog(): AlertDialog? {
val alertDialogBuilder = AlertDialog.Builder(this)
alertDialogBuilder.setTitle(R.string.notification_listener_service)
alertDialogBuilder.setMessage(R.string.notification_listener_service_explanation)
alertDialogBuilder.setPositiveButton(R.string.yes,
    DialogInterface.OnClickListener { dialog, id -> startActivity(Intent(ACTION_NOTIFICATION_LISTENER_SETTINGS)) })
alertDialogBuilder.setNegativeButton(R.string.no,
    DialogInterface.OnClickListener { dialog, id ->
        // If you choose to not enable the notification listener
        // the app. will not work as expected
    })
return alertDialogBuilder.create()
}
fun wear_connection(){
        wearableDeviceConnected = false
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
                                "Wearable device conected.",
                                Toast.LENGTH_LONG
                            ).show()
                            deviceconnectionStatusTv.text =
                                "Wearable device conected."

                        } else {
                            if (!wearableDeviceConnected){
                            Toast.makeText(
                                activityContext,
                                "A wearable device is paired. \n please open the App on the Wearable " +
                                        "device and start Job ",
                                Toast.LENGTH_LONG
                            ).show()
                            deviceconnectionStatusTv.text =
                                "A wearable device is paired."}

                        }
                        wearableDeviceConnected = true
                    } else {
                        val myToast=Toast.makeText(
                            activityContext,
                            "No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again.",
                            Toast.LENGTH_LONG
                        ).show()
                        /*

                        val mytoastView =
                            findViewById<TextView>(R.id.toastView)
                        mytoastView.text="No wearable device paired. Pair a wearable device to your phone using the Wear OS app and try again."

                        val toast = Toast(activityContext)
                        val toast_view: View = LayoutInflater.from(activityContext)
                            .inflate(R.layout.toast_text, null)
                        toast.setView(toast_view)
                        toast.duration = Toast.LENGTH_SHORT
                        toast.setGravity(Gravity.TOP, 0, 0)
                        toast.show()
                        */
                        deviceconnectionStatusTv.text =
                            "Try again!"
                        deviceconnectionStatusTv.visibility = View.VISIBLE

                        welcomescreen.visibility=View.VISIBLE

                    }

                if (wearableDeviceConnected){
                    mainAppView.visibility=View.VISIBLE
                    welcomescreen.visibility=View.GONE

                }
                else{

                    welcomescreen.visibility=View.VISIBLE
                    mainAppView.visibility=View.GONE
                }
                   PlotData()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }



}
    fun reconnect(item: MenuItem) {
        wear_connection()
        checkrecommandation()

    }
    fun settings_activity(item: MenuItem) {
        val intent = Intent(this@MainActivity, SettingsActivity ::class.java)


        intent.putExtra(
            "LastWearData",
            ""
        )



        startActivity(intent)
    }
    fun help_activity(item: MenuItem) {
        val intent = Intent(this@MainActivity,HelpActivity ::class.java)
         startActivity(intent)
    }
    fun mainsymptoms_activity(item: MenuItem) {
        val intent = Intent(this@MainActivity,MainSymptomsActivity ::class.java)
        startActivity(intent)
    }
    fun odersymptoms_activity(item: MenuItem) {
        val intent = Intent(this@MainActivity,OderSymptomsActivity::class.java)
        startActivity(intent)
    }


}
