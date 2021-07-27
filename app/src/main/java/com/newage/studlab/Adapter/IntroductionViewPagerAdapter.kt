package com.newage.studlab.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.newage.studlab.Authentication.*



class IntroductionViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {

    override fun getItem(position: Int): Fragment {
        
        when(position){
            0->{
                return Intro1()
            }
            1->{
                return Intro2()
            }
            2->{
                return Intro3()
            }
            3->{
                return Intro4()
            }
            4->{
                return Intro5()
            }
            5->{
                return Intro6()
            }
            else->{
                return Intro7()
            }
        }
    }

    override fun getCount(): Int {
        return 7
    }
}
