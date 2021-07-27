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
import com.newage.studlab.Authentication.Verification.SignUpIdentification
import com.newage.studlab.Home.BubtFragment.NoticeBoardFragment.Companion.noticeObj
import com.newage.studlab.Home.BubtFragment.NoticeBoardFragment.Companion.noticeUrl
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.BubtModel.AllNotice
import com.newage.studlab.Model.BubtModel.NoticeDetailed
import com.newage.studlab.Model.BubtModel.Url
import com.newage.studlab.R
import com.squareup.picasso.Picasso
import es.dmoral.toasty.Toasty
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.util.concurrent.TimeUnit

class NoticeDetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notice_details, container, false)
    }

    lateinit var processNoticeDetails: ProgressDialog

    private var typeHttp = OkHttpClient()

    var noticeData = NoticeDetailed()

    lateinit var backButton:ImageView
    private lateinit var linkButton:ImageView

    lateinit var publishOnTitle:TextView

    private lateinit var noticeTitle:TextView
    private lateinit var noticeDescription:TextView

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


        processNoticeDetails = ProgressDialog(requireContext()).apply {
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        fragmentPosition = 0.1
        fragmentName = "NoticeDetailsFragment"

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            linkButton = it.findViewById(R.id.fragment_link_button)

            publishOnTitle = it.findViewById(R.id.publish_on)

            noticeTitle = it.findViewById(R.id.notice_title)
            noticeDescription = it.findViewById(R.id.notice_description)

            image1 = it.findViewById(R.id.notine_image1)
            image2 = it.findViewById(R.id.notine_image2)
            image3 = it.findViewById(R.id.notine_image3)

            download1 = it.findViewById(R.id.button1)
            download2 = it.findViewById(R.id.button2)
            download3 = it.findViewById(R.id.button3)

        }

        parseNoticeDetailsData("${annexApis!!.annexNoticeDetails}url=${noticeObj.url}")

        allButtonClickEventListeners()

    }

    private fun allButtonClickEventListeners(){

        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, NoticeBoardFragment())
            transaction.commit()
        }

    }


    private fun loadNoticeDetailed(notice:NoticeDetailed){

        publishOnTitle.text = notice.pubDate

        if(notice.title.isNotEmpty()){
            noticeTitle.text = Html.fromHtml("<b>Title:</b> ${notice.title}")
        }else{
            noticeTitle.text = Html.fromHtml("<b>Title:</b> ---")
        }

        if(notice.description.isNotEmpty()){
            noticeDescription.text = Html.fromHtml("<b>Body:</b> ${notice.description}")
        }else{
            noticeDescription.text = Html.fromHtml("<b>Body:</b> ---")
        }

        for(i in 0 until notice.images.size){

            if(notice.images[i].url.isNotEmpty() && i==0){
                Picasso.get().load(notice.images[i].url).into(image1)
            }else if(notice.images[i].url.isNotEmpty() && i==1){
                Picasso.get().load(notice.images[i].url).into(image2)
            }else if(notice.images[i].url.isNotEmpty() && i==2){
                Picasso.get().load(notice.images[i].url).into(image3)
            }

        }

        for(i in 0 until notice.downloads.size){

            if(notice.downloads[i].url.isNotEmpty() && i==0){
                download1.visibility = View.VISIBLE
                download1.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notice.downloads[i].url))
                    startActivity(intent)
                }

            }else if(notice.downloads[i].url.isNotEmpty() && i==1){
                download2.visibility = View.VISIBLE

                download2.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notice.downloads[i].url))
                    startActivity(intent)
                }

            }else if(notice.downloads[i].url.isNotEmpty() && i==2){
                download3.visibility = View.VISIBLE

                download3.setOnClickListener{
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(notice.downloads[i].url))
                    startActivity(intent)
                }

            }

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            noticeTitle.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
            noticeDescription.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        linkButton.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(noticeUrl))
            startActivity(i)
        }
    }


    private fun parseNoticeDetailsData(url: String){
        processNoticeDetails.show()

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
                     processNoticeDetails.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            activity!!.runOnUiThread {
                                 processNoticeDetails.dismiss()
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

                                noticeData = NoticeDetailed(description,pubDate,titles,downloadObj,imageObj)

                                activity!!.runOnUiThread{
                                     processNoticeDetails.dismiss()
                                }

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processNoticeDetails.dismiss()

                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            processNoticeDetails.dismiss()

                            loadNoticeDetailed(noticeData)

                        }
                    }
                }
            }
        })
    }

}
