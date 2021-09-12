package com.demirli.a60caloriecounter

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.jayway.jsonpath.JsonPath
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private var foodDescriptionByFirstChar: List<String>? = null
    private var autoComplateChar = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        foodDescriptionByFirstChar = listOf()

        val json = loadJSONFromAsset()

        search_food_tv.setOnKeyListener(object: View.OnKeyListener{
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if(search_food_tv.text.toString() != ""){
                    autoComplateChar = search_food_tv.text.toString().substring(0,1).toUpperCase() + search_food_tv.text.toString().substring(1).toLowerCase()

                    foodDescriptionByFirstChar = JsonPath.read<List<String>>(json, "\$.foods[?(@.fooddescription =~ /^.*$autoComplateChar.*\$/)].fooddescription")
                    val adapter = ArrayAdapter<String>(this@MainActivity, android.R.layout.simple_list_item_1, foodDescriptionByFirstChar!!)
                    search_food_tv.setAdapter(adapter)
                    adapter.notifyDataSetChanged()
                }
                return true
            }

        })

        search_btn.setOnClickListener {
            if(search_food_tv.text.toString() != ""){
                val foodByfoodDescription = JsonPath.read<List<String>>(json, "\$.foods[?(@.fooddescription =~ /^.*${search_food_tv.text}.*\$/)]")

                val jsonArray = JSONArray(foodByfoodDescription)

                if(jsonArray[0] != null){
                    val jsonObject = JSONObject(jsonArray[0].toString())

                    id_tv.setText("ID: " + jsonObject.getString("id"))
                    fooddescription_tv.setText("FOOD DESCRIPTION: " + jsonObject.getString("fooddescription"))
                    portionsize_tv.setText("PORTION SIZE: " + jsonObject.getString("portion size"))
                    calorie_tv.setText("CALORIE: " + jsonObject.getString("calorie"))
                }
            }
        }
    }

    private fun loadJSONFromAsset(): String{
        var json: String? = null

        val stream = assets.open("foods.json")
        val size = stream.available()
        val buffer = ByteArray(size)
        stream.read(buffer)
        stream.close()
        json = String(buffer, Charsets.UTF_8)

        return json
    }
}
