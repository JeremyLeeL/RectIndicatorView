package com.jeremy.lym.rectindicatorview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val fragments = mutableListOf(PageFragment(0), PageFragment(1), PageFragment(2))
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.adapter = Adapter()
        rectIndicator.data = mutableListOf("第一页", "第二页", "第三页")
        rectIndicator.setUpWithViewPager(viewPager)
    }


    private inner class Adapter: FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT){
        override fun getItem(position: Int): Fragment = fragments[position]

        override fun getCount(): Int = fragments.size

    }
}
