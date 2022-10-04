package com.fakit.healthdatatracking

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_symptoms.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class MainSymptomsActivity : AppCompatActivity() {
    private var dbHandler: DBHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_symptoms)
        setSupportActionBar(toolbarmainsymptoms)
        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = DBHandler(this);
        LoadMainSymptoms()
    }
    public fun LoadMainSymptoms(){
        var MainSymptomes = dbHandler!!.getSymptomes("mainsymptomes")

        checkBoxGeruchsGechmack.isChecked=MainSymptomes[0]
        checkBoxSchnupfen.isChecked=MainSymptomes[1]
        checkBoxHusten.isChecked=MainSymptomes[2]


    }
    fun cancelbtmainSymptoms(view: View) {
        //reset Data
        this.onBackPressed()
    }

    fun savebtmainSymptoms(view: View) {

        //Save Data
        dbHandler!!.AddMainSymptomes(
            Date().toString(),
            checkBoxGeruchsGechmack.isChecked,
            checkBoxSchnupfen.isChecked,
            checkBoxHusten.isChecked,)
        this.onBackPressed()
    }

}