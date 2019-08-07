package com.pinhsiang.firebasepracticeps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.MutableLiveData
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.pinhsiang.firebasepracticeps.databinding.ActivityMainBinding
import java.util.HashMap

const val AUTHOR = "PinHsiang"
const val TAG_BEAUTY = "Beauty"
const val TAG_GOSSIPING = "Gossiping"
const val TAG_JOKE = "Joke"
const val TAG_SchoolLife = "SchoolLife"


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = setContentView(this, R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        val inputTitle = MutableLiveData<String>()
        val inputContent = MutableLiveData<String>()
        val inputTag = MutableLiveData<String>()

        // Set tag list
        val tagList = ArrayAdapter.createFromResource(
            applicationContext,
            R.array.tags,
            android.R.layout.simple_spinner_dropdown_item
        )
        binding.spinnerTag.adapter = tagList
        binding.spinnerTag.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                // Display the selected item text on text view
                when (position) {
                    0 -> inputTag.value = TAG_BEAUTY
                    1 -> inputTag.value = TAG_GOSSIPING
                    2 -> inputTag.value = TAG_JOKE
                    3 -> inputTag.value = TAG_SchoolLife
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


        binding.buttonSaveArtical.setOnClickListener {
            if (inputTitle.value != null &&
                inputContent.value != null &&
                inputTag.value != null
            ) {
                addArticle(title = inputTitle.value!!, content = inputContent.value!!, tag = inputTag.value!!)
            } else {
                Toast.makeText(applicationContext, "請輸入標題與內文且選擇 Tag", Toast.LENGTH_SHORT).show()
            }
        }

        val articleResult = MutableLiveData<List<HashMap<String, Any>>>()

        val art = db.collection("article")
            .get()
            .addOnSuccessListener { result ->
                val i = 0
                for (document in result) {
                    Log.d("PSDEBUG", "${document.id} => ${document.data}")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("PSDEBUG", "Error getting documents.", exception)
            }

    }

    // Create a new user with a first and last name
    fun addArticle(title: String, content: String, tag: String) {
        val article = hashMapOf(
            "author" to AUTHOR,
            "content" to content,
            "createdTime" to FieldValue.serverTimestamp(),
            "tag" to tag,
            "title" to title
        )
        db.collection("article")
            .document(title)
            .set(article)
            .addOnSuccessListener { documentReference ->
                Log.d("PSDEBUG", "DocumentSnapshot added with ID: $documentReference")
            }
            .addOnFailureListener { e ->
                Log.w("PSDEBUG", "Error adding document", e)
            }
    }
}
