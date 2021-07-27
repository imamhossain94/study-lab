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
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.newage.studlab.Adapter.BubtAdapter.EventsRecyclerViewAdapter
import com.newage.studlab.Application.StudLab.Companion.annexApis
import com.newage.studlab.Application.StudLab.Companion.currentUser
import com.newage.studlab.Application.StudLab.Companion.encryption
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentPosition
import com.newage.studlab.Model.BubtModel.AllEvents
import com.newage.studlab.Model.BubtModel.AllNotice
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

class EventsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_events, container, false)
    }

    companion object{
        var eventObj = AllEvents()
        var eventUrl:String = ""
        var allEvents = ArrayList<AllEvents>()
    }

    lateinit var processEvents: ProgressDialog

    private var typeHttp = OkHttpClient()

    var filteredList = ArrayList<AllEvents>()

    lateinit var event: JSONArray


    lateinit var backButton:ImageView
    lateinit var linkButton:ImageView

    lateinit var recyclerView:RecyclerView

    lateinit var adapter: EventsRecyclerViewAdapter


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        processEvents = ProgressDialog(requireContext()).apply {
            setTitle("Connecting....")
            setCancelable(true)
            setCanceledOnTouchOutside(false)
        }

        fragmentPosition = 0.0
        fragmentName = "EventsFragment"

        activity?.let {
            backButton = it.findViewById(R.id.fragment_back_button)
            linkButton = it.findViewById(R.id.fragment_link_button)

            recyclerView = it.findViewById(R.id.events_recycler_view)

        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val annexLoginWithSession = "${annexApis!!.annexLogin}id=${currentUser!!.annex_id}&pass=${encryption.decryptOrNull(currentUser!!.annex_pass)}&phpsessid=${currentUser!!.annex_session_id}"

        if(allEvents.isEmpty())
        annexLoginWithSessionId(annexLoginWithSession)

        loadEvent()
        allButtonClickEventListeners()

    }

    private fun loadEvent(){
        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)

        adapter = if(allEvents.isNotEmpty()) {
            EventsRecyclerViewAdapter(requireContext(), allEvents, clickDeleteListener = {

                eventObj = it

                eventUrl = it.url

                transaction.replace(R.id.main_home_fragment_container, EventDetailsFragment())
                transaction.commit()

            })
        }else{
            EventsRecyclerViewAdapter(requireContext(), ArrayList(), clickDeleteListener = {

                eventObj = it

                eventUrl = it.url

                transaction.replace(R.id.main_home_fragment_container, EventDetailsFragment())
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



    }

    private fun annexLoginWithSessionId(url:String){

        processEvents.show()
        processEvents.setMessage("Please wait...")

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
                    processEvents.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            activity!!.runOnUiThread {
                                processEvents.dismiss()
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
                                    processEvents.dismiss()
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
                            parseEventsData(annexApis!!.annexEvent)
                        }
                    }
                }
            }
        })
    }

    private fun parseEventsData(url: String){
        processEvents.show()
        processEvents.setTitle("Loading Notice")

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
                     processEvents.dismiss()
                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_SHORT, true).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    when {
                        !response.isSuccessful -> {
                            check = false
                            activity!!.runOnUiThread {
                                 processEvents.dismiss()
                                Toasty.error(requireContext(), "Error: By Annex!!", Toast.LENGTH_SHORT, true).show()
                            }
                            throw IOException("Unexpected code $response")
                        }
                        else -> {
                            try {

                                check = true
                                allEvents.clear()
                                val responseObj = JSONObject(response.body()!!.string())
                                event = responseObj.getJSONArray("data")



                                val body = event.toString()
                                val gson = GsonBuilder().create()

                                val listNotice = object : TypeToken<ArrayList<AllEvents>>() {}.type
                                allEvents = gson.fromJson(body, listNotice)


                                activity!!.runOnUiThread{
                                     processEvents.dismiss()
                                }

                            } catch (e: JSONException) {
                                check = false
                                activity!!.runOnUiThread {
                                    processEvents.dismiss()

                                    Toasty.error(requireContext(), e.message!!, Toast.LENGTH_LONG, true).show()
                                }
                            }
                        }
                    }
                }

                activity!!.runOnUiThread {
                    when {
                        check -> {

                            processEvents.dismiss()
                            Toasty.success(requireContext(), "Events are ready", Toast.LENGTH_LONG, true).show()

                            adapter.filterEventList(allEvents)
                        }
                    }
                }
            }
        })
    }



}
