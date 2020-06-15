package com.example.mediaplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.SeekBar
import kotlinx.android.synthetic.main.activity_main.*
import android.net.Uri
import android.os.*
import android.widget.TextView
import java.io.File
import java.util.*
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.widget.ImageView
import android.content.ContentUris
import java.io.ByteArrayInputStream


class MainActivity : AppCompatActivity() {

    lateinit var mp : MediaPlayer
    private var totTime: Int = 0
    internal var positn: Int = 0
    lateinit var msong : ArrayList<File>
    lateinit var  sname: String
    lateinit var track: TextView
    lateinit var songpic:ImageView
    lateinit var artname:TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

       //ACTION BAR DISPLAY
        supportActionBar!!.title = "Now Playing"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        artname= findViewById(R.id.artname)
        artname.isSelected=true
        artname.setSingleLine()
        songpic= findViewById(R.id.songpic)
        track = findViewById(R.id.songname)
        track.isSelected=true
        track.setSingleLine()


        //function call to play song
        playsong()




        //volume bar
      /*  volmbar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        var vol = progress / 100.0f
                        mp.setVolume(vol, vol)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {

                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {

                }
            }
        )*/

        //song progress timer
        posbar.max = totTime
        posbar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mp.seekTo(progress)
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }
            }
        )

        //thread
        Thread(Runnable {
            while (mp != null) {
                try {
                    var msg = Message()
                    msg.what = mp.currentPosition
                    handler.sendMessage(msg)
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {

                }
            }
        }).start()
    }




    //handler
    @SuppressLint("handlerleak")
    var handler = object : Handler() {
        override fun handleMessage(msg: Message) {
            var curposition = msg.what

            //update position bar
            posbar.progress = curposition

            //update label
            var elapsedtime = createTime(curposition)
            elapsedtimer.text = elapsedtime

            var remTime = createTime(totTime - curposition)
            remainingtime.text = "-$remTime"

            if(remTime=="0:00")
            {
                mp.stop()
                mp.release()
                val rand = Random()
                positn = rand.nextInt((msong.size) - 1 - 0 + 1) + 0
                sname = msong.get(positn).name.toString()
                track.text=sname
                val uri = Uri.parse(msong.get(positn).toString())
                albumart(uri)
                creator(uri)

            }


        }

    }

    // Timelabel updater

    fun createTime(time: Int): String {
        var timelabel = " "
        var min = time / 1000 / 60
        var sec = time / 1000 % 60

        timelabel = "$min:"
        if (sec < 10) timelabel += "0"
        timelabel += sec

        return timelabel


    }
     //play and pause button
    fun playbtn(v: View) {

        if (mp.isPlaying) {
            mp.pause()
            play.setBackgroundResource(R.drawable.ic_media_play)

        } else {
            mp.start()
            play.setBackgroundResource(R.drawable.ic_media_pause)
        }

    }

        //forward song button
        fun nextsong(v: View)
        {

                mp.stop()
                mp.release()
                positn = (positn + 1) % msong.size
                val uri = Uri.parse(msong[positn].toString())
                sname = msong[positn].name.toString()
                track.text = sname
                albumart(uri)
                creator(uri)

        }

    //previous song button
    fun prevsong(v: View)
    {
        mp.stop()
        mp.release()
        positn = if (positn - 1 < 0) msong.size - 1 else positn - 1
        val uri = Uri.parse(msong[positn].toString())
        sname = msong[positn].name.toString()
        track.text = sname
        albumart(uri)
        creator(uri)

    }

    //song player
    fun playsong()
    {

        val i = intent
        var bd:Bundle = i.extras

            msong = bd.getParcelableArrayList<Parcelable>("songs") as ArrayList<File>

            sname = msong.get(positn).name.toString()
            val songname = i.getStringExtra("songname")
            track.text = songname
            positn = bd.getInt("pos", 0)
            val uri = Uri.parse(msong.get(positn).toString())
             albumart(uri)
            creator(uri)
    }

     // action bar button
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

     fun creator( u:Uri){
         mp = MediaPlayer.create(this, u)
         mp.setVolume(5.5f, 5.5f)
         totTime = mp.duration
         posbar.max=mp.duration
         if(mp.isPlaying)
         {

         }
         mp.start()
     }

     fun albumart(u:Uri) :Unit {
          val mmr = MediaMetadataRetriever()
         mmr.setDataSource(this,u)
         val data = mmr.embeddedPicture
         if (data!=null) {
             val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
             songpic.setImageBitmap(bitmap)
         }
         else
         {
             songpic.setImageDrawable(resources.getDrawable(R.drawable.ms))
         }

         val artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
         if(artist !=null) {
             artname.setText(artist)
         }
         else
         {
             artname.text=(sname)
         }
     }

}




