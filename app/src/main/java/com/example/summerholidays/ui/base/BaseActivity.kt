package com.tapi.a0028speedtest.base

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import java.util.*


abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    protected open fun onPreCreate(): Boolean {
        return true
    }

    protected open fun onPostCreate() {

    }

    fun sendAction(actionName: String, data: Any?) {
        lifecycleScope.launchWhenResumed {
            if (!onActionReceived(actionName, data)) {
                supportFragmentManager.fragments.forEach {
                    sendActionToFragment(it, actionName, data)
                }
            }
        }
    }

    protected open fun onActionReceived(actionName: String, data: Any?): Boolean {
        return false
    }

    private fun sendActionToFragment(fragment: Fragment, actionName: String, data: Any?) {
        if (fragment is BaseFragment) {
            if (fragment.onActionReceived(actionName, data)) {
                return
            }
        }
        fragment.childFragmentManager.fragments.forEach {
            sendActionToFragment(it, actionName, data)
        }
    }

}