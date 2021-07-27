package com.newage.studlab.Home.BubtFragment

import MoreOptionFragment
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.newage.studlab.Adapter.BubtAdapter.NoticeRecyclerViewAdapter
import com.newage.studlab.Adapter.NotificationRecyclerViewAdapter
import com.newage.studlab.Application.StudLab
import com.newage.studlab.Application.StudLab.Companion.annexApis
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.encryption
import com.newage.studlab.Database.DatabaseHelper
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.BubtModel.AllNotice
import com.newage.studlab.Model.BubtModel.NoticeDetailed
import com.newage.studlab.Model.NotificationModel
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class NoticeBoardFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_notice_board, container, false)
    }

    companion object{
        var noticeObj = AllNotice()
        var noticeUrl:String = ""
        var allNotice = ArrayList<AllNotice>()
    }

    lateinit var processNoticeBoard: ProgressDialog

    private var typeHttp = OkHttpClient()



    var filteredList = ArrayList<AllNotice>()

    lateinit var notice: JSONArray


    lateinit var backButton:ImageView
    lateinit var linkButton:ImageView

    lateinit var generalNotice:TextView
    lateinit var classNotice:TextView
    lateinit var examNotice:TextView
    lateinit var recyclerView:RecyclerView

    lateinit var adapter: NoticeRecyclerViewAdapter


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        processNoticeBoard = ProgressDialog(requireContext()).apply {
            setTitle("Connecting....")
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        fragmentPosition = 0.0
        fragmentName = "NoticeBoardFragment"

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            linkButton = it.findViewById(R.id.fragment_link_button)
            generalNotice = it.findViewById(R.id.notice_general)
            classNotice = it.findViewById(R.id.notice_class)
            examNotice = it.findViewById(R.id.notice_exam)
            recyclerView = it.findViewById(R.id.notice_recycler_view)

        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val annexLoginWithSession = "${annexApis!!.annexLogin}id=${currentUser!!.annex_id}&pass=${encryption.decryptOrNull(currentUser!!.annex_pass)}&phpsessid=${currentUser!!.annex_session_id}"

        if(allNotice.isEmpty())
        annexLoginWithSessionId(annexLoginWithSession)

        loadNotice()
        allButtonClickEventListeners()

    }

    private fun loadNotice(){
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)

        adapter = if(allNotice.isNotEmpty()) {
            NoticeRecyclerViewAdapter(requireContext(), allNotice, clickDeleteListener = {

                noticeObj = it

                noticeUrl = it.url

                transaction.replace(R.id.main_home_fragment_container, NoticeDetailsFragment())
                transaction.commit()

            })
        }else{
            NoticeRecyclerViewAdapter(requireContext(), ArrayList(), clickDeleteListener = {

                noticeObj = it

                noticeUrl = it.url

                transaction.replace(R.id.main_home_fragment_container, NoticeDetailsFragment())
                transaction.commit()

            })
        }
        recyclerView.adapter = adapter
    }

    private fun allButtonClickEventListeners(){
        backButton.setOnClickListener{
            val transaction = fragmentManager!!.beginTransaction()
            transaction.setCustomAnimations(0, 0,0,0)
            transaction.replace(R.id.main_home_fragment_container, MoreOptionFragment())
            transaction.commit()
        }

        linkButton.setOnClickListener{
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.bubt.edu.bd/Home/all_notice"))
            startActivity(i)
        }


        generalNotice.setOnClickListener{

            filteredList = allNotice.filter { it.category == "General" } as ArrayList<AllNotice>
            adapter.filterNoticeList(filteredList)

            generalNotice.setBackgroundResource(R.drawable.square_round_white_blue_strok)
            classNotice.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            examNotice.setBackgroundResource(R.drawable.square_round_white_blue_disabled)

            generalNotice.setTextColor(resources.getColor(R.color.colorBlue2))
            classNotice.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            examNotice.setTextColor(resources.getColor(R.color.colorBlueDisabled))
        }

        classNotice.setOnClickListener{
            filteredList = allNotice.filter { it.category == "Class Related" } as ArrayList<AllNotice>
            adapter.filterNoticeList(filteredList)

            generalNotice.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            classNotice.setBackgroundResource(R.drawable.square_round_white_blue_strok)
            examNotice.setBackgroundResource(R.drawable.square_round_white_blue_disabled)

            generalNotice.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            classNotice.setTextColor(resources.getColor(R.color.colorBlue2))
            examNotice.setTextColor(resources.getColor(R.color.colorBlueDisabled))
        }

        examNotice.setOnClickListener{
            filteredList = allNotice.filter { it.category == "Exam Related" } as ArrayList<AllNotice>
            adapter.filterNoticeList(filteredList)

            generalNotice.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            classNotice.setBackgroundResource(R.drawable.square_round_white_blue_disabled)
            examNotice.setBackgroundResource(R.drawable.square_round_white_blue_strok)

            generalNotice.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            classNotice.setTextColor(resources.getColor(R.color.colorBlueDisabled))
            examNotice.setTextColor(resources.getColor(R.color.colorBlue2))
        }

    }

    private fun annexLoginWithSessionId(url:String){

        processNoticeBoard.show()
        processNoticeBoard.setMessage("Please wait...")

        var check = false
        var phpSessionId = ""

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
                    processNoticeBoard.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            activity!!.runOnUiThread {
                                processNoticeBoard.dismiss()
                                Toasty.error(requireContext(), "Error: Annex login!!", Toast.LENGTH_SHORT, true).show()
                            }
                            check = false
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {
                                check = true
                                val responseObj = JSONObject(response.body()!!.string())
                                phpSessionId = responseObj.getString("PHPSESSID")

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processNoticeBoard.dismiss()
                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {
                            Toasty.success(requireContext(), "Connected", Toast.LENGTH_LONG, true).show()
                            parseAllNoticeData(annexApis!!.annexNotice)
                        }
                    }
                }
            }
        })
    }

    private fun parseAllNoticeData(url: String){
        processNoticeBoard.show()
        processNoticeBoard.setTitle("Loading Notice")

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
                     processNoticeBoard.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            activity!!.runOnUiThread {
                                 processNoticeBoard.dismiss()
                                Toasty.error(requireContext(), "Error: By Annex!!", Toast.LENGTH_SHORT, true).show()
                            }
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {

                                check = true
                                allNotice.clear()
                                val responseObj = JSONObject(response.body()!!.string())
                                notice = responseObj.getJSONArray("data")



                                val body = notice.toString()
                                val gson = GsonBuilder().create()

                                val listNotice = object : TypeToken<ArrayList<AllNotice>>() {}.type
                                allNotice = gson.fromJson(body, listNotice)


                                activity!!.runOnUiThread{
                                     processNoticeBoard.dismiss()
                                }

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processNoticeBoard.dismiss()

                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            processNoticeBoard.dismiss()
                            Toasty.success(requireContext(), "Notice Is Ready", Toast.LENGTH_LONG, true).show()

                            filteredList = allNotice.filter { it.category == "General" } as ArrayList<AllNotice>
                            adapter.filterNoticeList(filteredList)
                        }
                    }
                }
            }
        })
    }



}
