package com.newage.studlab.Adapter

import HomeFragment
import MoreOptionFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.newage.studlab.Home.BloodFragment.BloodHomeFragment
import com.newage.studlab.Home.ProfileFragment
import com.newage.studlab.Home.StudLabFragment

class MainHomeViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {

    override fun getItem(position: Int): Fragment {

        when(position){
            0->{
                return HomeFragment()
            }
            1->{
                return BloodHomeFragment()
            }
            2->{
                return ProfileFragment()
            }
            3->{
                return StudLabFragment()
            }
            else->{
                return MoreOptionFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 5
    }
}
