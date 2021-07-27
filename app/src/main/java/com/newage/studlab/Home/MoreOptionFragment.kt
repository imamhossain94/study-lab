import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.newage.studlab.Application.StudLab.Companion.appContext
import com.newage.studlab.Calculators.CgpaCalculator
import com.newage.studlab.Calculators.SemFeesCalculator
import com.newage.studlab.Calculators.SgpaCalculator
import com.newage.studlab.Home.BubtFragment.EventsFragment
import com.newage.studlab.Home.BubtFragment.NoticeBoardFragment
import com.newage.studlab.Home.HomeMainActivity
import com.newage.studlab.Home.HomeMainActivity.Companion.fragmentName
import com.newage.studlab.Home.HomeMainActivity.Companion.position
import com.newage.studlab.Model.AnnexModel.AnnexResults
import com.newage.studlab.R
import es.dmoral.toasty.Toasty
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit


class MoreOptionFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_more_option, container, false)
    }

    lateinit var annexHome:Button
    lateinit var noticeButton:Button
    lateinit var cGpaCalculatorButton:Button
    lateinit var sGpaCalculatorButton:Button
    lateinit var semFeesCalculatorButtin:Button
    lateinit var eventButton:Button


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        activity?.let {

            annexHome = it.findViewById(R.id.annex_home_button)
            noticeButton = it.findViewById(R.id.notice_board_button)
            cGpaCalculatorButton = it.findViewById(R.id.c_gpa_cal_button)
            sGpaCalculatorButton = it.findViewById(R.id.s_gpa_cal_button)
            semFeesCalculatorButtin = it.findViewById(R.id.sem_fees_calculat_button)

            eventButton = it.findViewById(R.id.events_button)

        }

        position = 4
        fragmentName = "MoreOptionFragment"

        allButtonClickListener()
    }

    private fun allButtonClickListener(){

        val transaction = fragmentManager!!.beginTransaction()
        transaction.setCustomAnimations(0, 0,0,0)

        annexHome.setOnClickListener{

            transaction.replace(R.id.main_home_fragment_container, AnnexHomeFragment())
            transaction.commit()
        }

        noticeButton.setOnClickListener{



            transaction.replace(R.id.main_home_fragment_container, NoticeBoardFragment())
            transaction.commit()
        }

        cGpaCalculatorButton.setOnClickListener{

            Toasty.info(requireContext(), "Dev: broken calculation system. wait for fix.", Toast.LENGTH_LONG, true).show()
            startActivity(Intent(appContext, CgpaCalculator::class.java))
        }

        sGpaCalculatorButton.setOnClickListener{
            startActivity(Intent(appContext, SgpaCalculator::class.java))
        }

        semFeesCalculatorButtin.setOnClickListener{
            startActivity(Intent(appContext, SemFeesCalculator::class.java))
        }

        eventButton.setOnClickListener{

            transaction.replace(R.id.main_home_fragment_container, EventsFragment())
            transaction.commit()
        }
    }



}