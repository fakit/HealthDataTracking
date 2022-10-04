package com.fakit.healthdatatracking

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_symptoms.*
import kotlinx.android.synthetic.main.activity_oder_symptoms.*
import kotlinx.android.synthetic.main.activity_settings.*
import java.util.*

class OderSymptomsActivity : AppCompatActivity() {
    private var dbHandler: DBHandler? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_oder_symptoms)
        setSupportActionBar(toolbarodersymptoms)
        // creating a new dbhandler class
        // and passing our context to it.
        dbHandler = DBHandler(this);
        LoadOderSymptoms()
    }
    fun LoadOderSymptoms(){
        var OderSymptomes = dbHandler!!.getSymptomes("odersymptomes")

        checkBoxMuskelundGliederschmerzen.isChecked=OderSymptomes[0]
        checkBoxBauchschmerzen.isChecked=OderSymptomes[1]
        checkBoxBindehautentzuendung.isChecked=OderSymptomes[2]
        checkBoxStarkeBindehautentzuendung.isChecked=OderSymptomes[3]
        checkBoxHautausschlag.isChecked=OderSymptomes[4]
        checkBoxleichterespiratorischeSymptome.isChecked=OderSymptomes[5]
        checkBoxAtemnot.isChecked=OderSymptomes[6]
        checkBoxHalsundKopfschmerzen.isChecked=OderSymptomes[7]
        checkBoxSchuettelfrost.isChecked=OderSymptomes[8]
        checkBoxuebelkeitErbrechen.isChecked=OderSymptomes[9]
        checkBoxDurchfall.isChecked=OderSymptomes[10]
        checkBoxAppetitlosigkeit.isChecked=OderSymptomes[11]
        checkBoxLymphknotenschwellung.isChecked=OderSymptomes[12]
        checkBoxApathie.isChecked=OderSymptomes[13]
        checkBoxBenommenheit.isChecked=OderSymptomes[14]
        checkBoxLeichteErkaeltungssymptome.isChecked=OderSymptomes[15]
        checkBoxPneumonie1Grad.isChecked=OderSymptomes[16]
        checkBoxPneumonie2Grad.isChecked=OderSymptomes[17]
    }

    fun cancelbtnOderSymptomes(view: View) {
        //reset Data
        this.onBackPressed();
    }
    fun savebtnOderSymptomes(view: View) {
        //Save Data
        dbHandler!!.AddOderSymptomes(
            Date().toString(),
                    checkBoxMuskelundGliederschmerzen.isChecked,
                    checkBoxBauchschmerzen.isChecked,
                    checkBoxBindehautentzuendung.isChecked,
                    checkBoxStarkeBindehautentzuendung.isChecked,
                    checkBoxHautausschlag.isChecked,
                    checkBoxleichterespiratorischeSymptome.isChecked,
                    checkBoxAtemnot.isChecked,
                    checkBoxHalsundKopfschmerzen.isChecked,
                    checkBoxSchuettelfrost.isChecked,
                    checkBoxuebelkeitErbrechen.isChecked,
                    checkBoxDurchfall.isChecked,
                    checkBoxAppetitlosigkeit.isChecked,
                    checkBoxLymphknotenschwellung.isChecked,
                    checkBoxApathie.isChecked,
                    checkBoxBenommenheit.isChecked,
                    checkBoxLeichteErkaeltungssymptome.isChecked,
                    checkBoxPneumonie1Grad.isChecked,
                    checkBoxPneumonie2Grad.isChecked)
        this.onBackPressed()
    }
}