package com.example.fragmentospart2

import android.content.res.Resources
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0 -> { PrimeroFragment() }
            1 -> { SegundoFragment() }
            2 -> { TerceroFragment() }
            else -> { throw Resources.NotFoundException("Posicion no encontrada") } }
        }
}