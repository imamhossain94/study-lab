package com.newage.studlab.Adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.newage.studlab.StudLab.*


class StudLabViewPagerAdapter(supportFragmentManager: FragmentManager) : FragmentStatePagerAdapter(supportFragmentManager) {

    override fun getItem(position: Int): Fragment {
        when(position){
            0->{
                return BooksFragment()
            }
            1->{
                return SlidesFragment()
            }
            2->{
                return SheetsFragment()
            }
            3->{
                return LecturesFragment()
            }
            else->{
                return CoursesFragment()
            }
        }
    }

    override fun getCount(): Int {
        return 5
    }
}
