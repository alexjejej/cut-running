package com.cut.android.running.provider.services.navigation

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.cut.android.running.R

object NavigationObj {
    fun navigateTo(fragmentManager: FragmentManager, fragment: Fragment, tag: String): Boolean {
        //fragmentManager.popBackStack()
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.home_container_fragment, fragment)
            addToBackStack(tag)
        }
        Log.d("NavigationObj","${fragmentManager.backStackEntryCount}")
        return true
    }
}