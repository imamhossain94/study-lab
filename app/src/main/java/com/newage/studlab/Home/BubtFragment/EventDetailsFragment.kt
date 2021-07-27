package com.newage.studlab.Home.BubtFragment

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.newage.studlab.Application.StudLab.Companion.annexApis
import com.newage.studlab.Home.BubtFragment.EventsFragment.Companion.eventObj
import com.newage.studlab.Home.BubtFragment.EventsFragment.Companion.eventUrl
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition

import com.newage.studlab.Model.BubtModel.NoticeDetailed
import com.newage.studlab.Model.BubtModel.Url
import com.newage.studlab.R
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class EventDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event_details, container, false)
    }

    lateinit var processEventsDetails: ProgressDialog

    private var typeHttp = OkHttpClient()

    var eventData = NoticeDetailed()

    lateinit var backButton:ImageView
    private lateinit var linkButton:ImageView

    private lateinit var publishOnTitle:TextView

    private lateinit var eventTitle:TextView
    private lateinit var eventDescription:TextView

    //-------------------------images------------------------------
    lateinit var image1:ImageView
    lateinit var image2:ImageView
    lateinit var image3:ImageView

    //-------------------------download------------------------------
    lateinit var download1:Button
    lateinit var download2:Button
    lateinit var download3:Button


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        processEventsDetails = ProgressDialog(requireContext()).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        fragmentPosition = 0.1
        fragmentName = "EventDetailsFragment"

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            linkButton = it.findViewById(R.id.fragment_link_button)

            publishOnTitle = it.findViewById(R.id.publish_on)

            eventTitle = it.findViewById(R.id.event_title)
            eventDescription = it.findViewById(R.id.event_description)

            image1 = it.findViewById(R.id.event_image1)
            image2 = it.findViewById(R.id.event_image2)
            image3 = it.findViewById(R.id.event_image3)

            download1 = it.findViewById(R.id.button1)
            download2 = it.findViewById(R.id.button2)
            download3 = it.findViewById(R.id.button3)

        }

        parseEventsData("${annexApis!!.annexEventsDetails}url=${eventObj.url}")

        allButtonClickEventListeners()

    }

    private fun allButtonClickEventListeners(){

        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, EventsFragment())
            transaction.commit()
        }

    }


    private fun loadEventDetailed(event:NoticeDetailed){

        publishOnTitle.text = event.pubDate

        if(event.title.isNotEmpty()){
            eventTitle.text = Html.fromHtml("<b>Title:</b> ${event.title}")
        }else{
            eventTitle.text = Html.fromHtml("<b>Title:</b> ---")
        }

        if(event.description.isNotEmpty()){
            eventDescription.text = Html.fromHtml("<b>Body:</b> ${event.description}")
        }else{
            eventDescription.text = Html.fromHtml("<b>Body:</b> ---")
        }

        for(i in 0 until event.images.size){

            if(event.images[i].url.isNotEmpty() && i==0){
                Picasso.get().load(event.images[i].url).into(image1)
            }else if(event.images[i].url.isNotEmpty() && i==1){
                Picasso.get().load(event.images[i].url).into(image2)
            }else if(event.images[i].url.isNotEmpty() && i==2){
                Picasso.get().load(event.images[i].url).into(image3)
            }

        }

        for(i in 0 until event.downloads.size){

            if(event.downloads[i].url.isNotEmpty() && i==0){
                download1.visibility = View.VISIBLE
                download1.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.downloads[i].url))
                    startActivity(intent)
                }

            }else if(event.downloads[i].url.isNotEmpty() && i==1){
                download2.visibility = View.VISIBLE

                download2.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.downloads[i].url))
                    startActivity(intent)
                }

            }else if(event.downloads[i].url.isNotEmpty() && i==2){
                download3.visibility = View.VISIBLE

                download3.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(event.downloads[i].url))
                    startActivity(intent)
                }

            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            eventTitle.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            eventDescription.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        linkButton.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(eventUrl))
            startActivity(i)
        }
    }


    private fun parseEventsData(url: String){
        processEventsDetails.show()

        var check = false

        val builder = OkHttpClient.Builder()
        builder.connectTimeout(30, TimeUnit.SECONDS)
        builder.readTimeout(30, TimeUnit.SECONDS)
        builder.writeTimeout(30, TimeUnit.SECONDS)

        typeHttp = builder.build()

        val request = Request.Builder().url(url).build()

        typeHttp.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                check = false
                activity!!.runOnUiThread {
                     processEventsDetails.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            activity!!.runOnUiThread {
                                 processEventsDetails.dismiss()
                                Toasty.error(requireContext(), "Error: By Annex!!", Toast.LENGTH_SHORT, true).show()
                            }
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {

                                check = true
                                val responseObj = JSONObject(response.body()!!.string())

                                val resObj = responseObj.getJSONObject("data")

                                val description = resObj.getString("description")
                                val pubDate = resObj.getString("pubDate")
                                val titles = resObj.getString("title")

                                val download = resObj.getJSONArray("downloads")
                                val image = resObj.getJSONArray("images")

                                val downloadBody = download.toString()
                                val downloadGSon = GsonBuilder().create()
                                val listDownload = object : TypeToken<ArrayList<Url>>() {}.type

                                val downloadObj:ArrayList<Url> = downloadGSon.fromJson(downloadBody, listDownload)

                                val imageBody = image.toString()
                                val imageGSon = GsonBuilder().create()
                                val listImage = object : TypeToken<ArrayList<Url>>() {}.type

                                val imageObj:ArrayList<Url> = imageGSon.fromJson(imageBody, listImage)

                                eventData = NoticeDetailed(description,pubDate,titles,downloadObj,imageObj)

                                activity!!.runOnUiThread{
                                     processEventsDetails.dismiss()
                                }

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processEventsDetails.dismiss()

                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            processEventsDetails.dismiss()

                            loadEventDetailed(eventData)

                        }
                    }
                }
            }
        })
    }

}
