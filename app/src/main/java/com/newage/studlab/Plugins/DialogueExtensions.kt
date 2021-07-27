package com.newage.studlab.Plugins

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.LayoutRes
import com.newage.studlab.R
import kotlinx.android.synthetic.main.dialogue_routine_info.view.*
import kotlinx.android.synthetic.main.dialogue_upload_routine.view.close_button
import kotlinx.android.synthetic.main.dialogue_upload_routine.view.containers


fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun <T> Context.openActivity(it: Class<T>, extras: Bundle.() -> Unit = {}) {
    val intent = Intent(this, it)
    intent.putExtras(Bundle().apply(extras))
    startActivity(intent)
}

fun Context.toast(message: CharSequence) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

fun View.getActivity(): Activity? {
    var context = this.context
    while (context is ContextWrapper) {
        if (context is Activity) {
            return context
        }
        context = (context as ContextWrapper).baseContext
    }
    return null
}

//info dialogue
@SuppressLint("SetTextI18n")
fun Activity.showInfoDialog(proCode:String,intakeNo:String,secNo:String,sesName:String,semName:String,uploadBy:String,uploadDate:String,routineType:String) {

    val dialogBuilder = AlertDialog.Builder(this)
    val inflater = this.layoutInflater
    @SuppressLint("InflateParams")
    val dialogView = inflater.inflate(R.layout.dialogue_routine_info, null)
    dialogBuilder.setView(dialogView)

    val routineView = dialogView.containers
    val crossButton = dialogView.close_button

    val infoProgram = dialogView.info_program
    val infoIntake = dialogView.info_intake
    val infoSection = dialogView.info_sestion
    val infoSession = dialogView.info_session
    val infoSemester = dialogView.info_semester
    val infoUploadBy = dialogView.info_upload_by
    val infoUploadDate = dialogView.info_upload_date
    val infoRoutineType = dialogView.info_routine_type

    val alertDialog = dialogBuilder.create()
    val animPopUp = AnimationUtils.loadAnimation(this, R.anim.popupanim)
    routineView.startAnimation(animPopUp)

    alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    alertDialog.setCanceledOnTouchOutside(false)
    alertDialog.show()

    //load some value
    infoProgram.text = proCode
    infoIntake.text = intakeNo
    infoSection.text = secNo
    infoSession.text = sesName
    infoSemester.text = semName
    infoUploadBy.text = uploadBy
    infoUploadDate.text = uploadDate
    infoRoutineType.text = routineType

    crossButton.setOnClickListener{
        alertDialog.dismiss()
    }
}


fun blinkingView(view: View, tracking: Boolean) {
    val anim = AlphaAnimation(0.0f, 1.0f)
    anim.duration = 1000
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    //anim.repeatCount = Animation.INFINITE
    anim.repeatCount = 0
    if (tracking) {
        view.startAnimation(anim)
    } else {
        view.clearAnimation()
    }
}


