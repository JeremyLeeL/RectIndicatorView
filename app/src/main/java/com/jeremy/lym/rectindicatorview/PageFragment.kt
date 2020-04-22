package com.jeremy.lym.rectindicatorview

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_page.*

class PageFragment(val position: Int): Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(activity).inflate(R.layout.fragment_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        when(position){
            0 ->{
                tv.text = "第一页"
                tv.setBackgroundColor(Color.parseColor("#181818"))
            }
            1 ->{
                tv.text = "第二页"
                tv.setBackgroundColor(Color.parseColor("#222222"))
            }
            2 ->{
                tv.text = "第三页"
                tv.setBackgroundColor(Color.parseColor("#1CBAF2"))
            }
        }
    }
}