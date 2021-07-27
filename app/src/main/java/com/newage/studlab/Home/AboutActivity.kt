package com.newage.studlab.Home

import android.content.Intent
import android.graphics.text.LineBreaker
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.newage.studlab.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        overridePendingTransition(R.anim.enter_right_to_left, R.anim.exit_right_to_left)

        app_goto_link.setOnClickListener{

            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.newagedevs.com/"))
            startActivity(i)

        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            apps_description.justificationMode = LineBreaker.JUSTIFICATION_MODE_INTER_WORD
        }

        app_image_imam.setOnClickListener{

        }
        app_image_rafsan.setOnClickListener{

        }
        app_image_abdul_ahad.setOnClickListener{

        }
        app_image_xayed.setOnClickListener{

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.enter_left_to_right, R.anim.exit_left_to_right)
        this.finish()
    }
}
