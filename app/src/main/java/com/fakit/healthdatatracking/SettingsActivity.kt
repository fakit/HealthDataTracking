package com.fakit.healthdatatracking

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*


class SettingsActivity : AppCompatActivity() {

    private var dbHandler: DBHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val toolbar = findViewById<Toolbar>(R.id.toolbarsettings)
        setSupportActionBar(toolbar)
        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = DBHandler(this);
       try {
           LoadLastWearData()
           LoadLastUser()
           LoadVorerkrankungen()
       }catch (e: Exception) {
    }
    }
fun LoadLastWearData(){

    var LastWearData = dbHandler!!.lastWearData
    // Average
    editTextTemperature.setText(LastWearData.Temp_COL.toString())
    editTextHerzschlag.setText(LastWearData.Heart_COL.toString())
    editTextBlutdruck.setText(LastWearData.Press_COL.toString())
    editTextLicht_Intensitaet.setText(LastWearData.Light_COL.toString())
    editTextLinearAcceleration.setText(LastWearData.LinearAccelero_COL.toString())
    editTextFeutigkeit.setText(LastWearData.Rhumid_COL.toString())
}

fun LoadLastUser(){
    var user = dbHandler!!.lastUser

    editTextAge.setText(user.Age.toString());
    editTextWeight.setText(user.Weight.toString());
    if (  user.Sex.equals("weiblich")){
        weiblich.isChecked=true
    }else maennlich.isChecked=true

}
fun LoadVorerkrankungen(){
    var Illness = dbHandler!!.vorerkrankungen

    checkBoxHerzerkrankungen.isChecked=Illness.Herzerkrankungen
    checkBoxLungenerkrankungen.isChecked=Illness.Lungenerkrankungen
    checkBoxDiabetes_mellitus.isChecked=Illness.Diabetesmellitus
    checkBoxNierenerkrankungen.isChecked=Illness.Nierenerkrankungen
    checkBoxMalignom.isChecked=Illness.Malignom
    }

    fun cancelbtnuserdata(view: View) {
        //reset Data
        this.onBackPressed();
    }
    fun savebtnuserdata(view: View) {
        //Save Data
        var editTextTemperature_value = 0.0f
        if  (editTextTemperature.text.toString().length >0){
            editTextTemperature_value =editTextTemperature.text.toString().toFloat()
        }
        var editTextHerzschlag_value = 0.0f
        if  (editTextHerzschlag.text.toString().length >0){
            editTextHerzschlag_value =editTextHerzschlag.text.toString().toFloat()
        }
        var editTextBlutdruck_value = 0.0f
        if  (editTextBlutdruck.text.toString().length >0){
            editTextBlutdruck_value =editTextBlutdruck.text.toString().toFloat()
        }
        var editTextLicht_Intensitaet_value = 0.0f
        if  (editTextLicht_Intensitaet.text.toString().length >0){
            editTextLicht_Intensitaet_value =editTextLicht_Intensitaet.text.toString().toFloat()
        }

        var editTextLinearAcceleration_value = 0.0f
        if  (editTextLinearAcceleration.text.toString().length >0){
            editTextLinearAcceleration_value =editTextLinearAcceleration.text.toString().toFloat()
        }
        var editTextFeutigkeit_value = 0.0f
        if  (editTextFeutigkeit.text.toString().length >0){
            editTextFeutigkeit_value =editTextFeutigkeit.text.toString().toFloat()
        }


        dbHandler!!.AddWearData(Date().toString(),editTextTemperature_value,
            editTextHerzschlag_value,
            editTextBlutdruck_value,
            editTextLicht_Intensitaet_value,
            editTextLinearAcceleration_value,
            editTextFeutigkeit_value)

        dbHandler!!.AddIllness(Date().toString(),checkBoxHerzerkrankungen.isChecked,
            checkBoxLungenerkrankungen.isChecked,
            checkBoxDiabetes_mellitus.isChecked,
            checkBoxNierenerkrankungen.isChecked,
            checkBoxMalignom.isChecked)

        var editTextAge_value = 0
        if  (editTextAge.text.toString().length >0){
            editTextAge_value =editTextAge.text.toString().toInt()
        }
        var editTextWeight_value = 0
        if  (editTextWeight.text.toString().length >0){
            editTextWeight_value =editTextWeight.text.toString().toInt()
        }
        var editTextSex_value = "weiblich"
        if (!weiblich.isChecked){
            editTextSex_value="männlich"
        }


        dbHandler!!.AddUserData(Date().toString(),  editTextAge_value,
            editTextWeight_value,
            editTextSex_value)
        this.onBackPressed()
    }
}