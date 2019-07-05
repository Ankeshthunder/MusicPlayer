package com.example.mediaplayer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import java.io.File
import java.util.ArrayList
import android.net.Uri
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast


class songslist : AppCompatActivity() {
     lateinit var list: ListView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_songslist)

         list =findViewById<ListView>(R.id.mysongs)

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 0)
        } else {
             display()
        }

        //actionbar
        supportActionBar!!.title = "Playlist"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            display()
        } else {
            finish()
        }

    }

   // playlist fetcher

   fun findsong(file: File): ArrayList<File> {

        val arrayList = ArrayList<File>()

        val files = file.listFiles()
        for (singleFile in files) {
            if (singleFile.isDirectory && !singleFile.isHidden) {
                arrayList.addAll(findsong(singleFile))
            } else {
                if (singleFile.name.endsWith(".mp3") || singleFile.name.endsWith(".wav")) {
                    arrayList.add(singleFile)

                }

            }
        }
        return arrayList
    }

    //playlist maker

    fun display() {

        val mysongs = findsong(Environment.getExternalStorageDirectory())
         var items = arrayOfNulls<String>(mysongs.size)

        for (i in mysongs.indices) {

            items[i] = mysongs[i].name.toString().replace(".mp3", "").replace(".wav", "")

        }
        val myAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, items)
        list.adapter=myAdapter

        val sArtworkUri = Uri
            .parse("content://media/external/audio/albumart")

       /* val uri = ContentUris.withAppendedId(
            PlayerConstants.sArtworkUri,
            listOfAlbums.get(position).getAlbumID()
        )*/

        list.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val songname = list.getItemAtPosition(position).toString()


            startActivity(
                Intent(this, MainActivity::class.java)
                    .putExtra("songs", mysongs).putExtra("songname", songname)
                    .putExtra("pos", position))
            }

            }

    //back to home screen
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //Action bar menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        val inflater = menuInflater
        inflater.inflate(R.menu.option, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_button -> {
                Toast.makeText(this, "SEARCH", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.favourite -> {
                Toast.makeText(this, "FAVOURITES ", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.recent -> {
                Toast.makeText(this, "RECENT PLAY", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.frequent-> {
                Toast.makeText(this, "frequent play", Toast.LENGTH_LONG).show()
                return true
            }
            R.id.nowplay -> {

                startActivity(Intent(this, MainActivity::class.java))
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }
}








