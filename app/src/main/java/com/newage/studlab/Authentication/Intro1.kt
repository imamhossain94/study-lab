package com.newage.studlab.Authentication

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView

import com.newage.studlab.R
import com.squareup.picasso.Picasso

class Intro1 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_intro1, container, false)
    }



    lateinit var imgView:ImageView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            imgView = it.findViewById(R.id.background)
        }

        //Picasso.get().load("https://image.freepik.com/free-vector/abstract-background-mobile-fluid-shapes-with-gradient-effect_79603-560.jpg").into(imgView)


    }

}
