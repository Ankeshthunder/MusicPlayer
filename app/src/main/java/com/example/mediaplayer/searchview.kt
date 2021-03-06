package com.example.mediaplayer

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.SearchView
import kotlinx.android.synthetic.main.activity_songslist.*
import java.io.File
import java.util.ArrayList

class searchview : AppCompatActivity() {
      lateinit var sview :SearchView
      lateinit var plist :ListView
      lateinit var msong:ArrayList<File>

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_searchview)
        sview =findViewById(R.id.Sview)
        plist=findViewById(R.id.Plist)

        supportActionBar!!.title = "Playlist"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


        val i = intent
        var bd = i.extras
        msong = bd.getParcelableArrayList<Parcelable>("songs") as ArrayList<File>

        var items = ArrayList<String>(msong.size)

        for (i in msong.indices)
        {

            items[i] = msong[i].name.toString().replace(".mp3", "").replace(".wav", "")

        }
        var myAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        plist.adapter=myAdapter

        sview.setOnQueryTextListener(object : SearchView.OnQueryTextListener
        {
            override fun onQueryTextSubmit(query: String): Boolean
            {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean
            {
                if (newText != null && !newText.isEmpty()) {
                    val found = ArrayList<String>()
                    for (item in items) {
                        if (item.contains(newText)) {
                            found.add(item)
                        }
                    }
                    val adapter = ArrayAdapter(this@searchview, android.R.layout.simple_list_item_1, found)
                    plist.setAdapter(adapter)
                } else {
                    val adapter = ArrayAdapter(this@searchview, android.R.layout.simple_list_item_1, items)
                    plist.setAdapter(adapter)
                }

                return false
            }
        })
    }



    override fun onSupportNavigateUp(): Boolean
    {
        onBackPressed()
        return true
    }
}
