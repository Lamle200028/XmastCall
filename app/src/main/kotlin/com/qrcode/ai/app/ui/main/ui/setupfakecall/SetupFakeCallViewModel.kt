package com.qrcode.ai.app.ui.main.ui.setupfakecall

import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.Data
import com.qrcode.ai.app.ui.fakecall.FakeCallActivity
import com.qrcode.ai.app.ui.fakecall.FakeCallWorker
import com.qrcode.ai.app.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SetupFakeCallViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val TYPE_INCOMING = 1
        const val TYPE_OUTGOING = 2
    }

    private var direction = MutableLiveData(TYPE_INCOMING)
    var callStarted: (() -> Unit)? = null
    var time: Time? = null
    var url: String? = null

    fun selectDirection(type: Int) {
        this.direction.value = type
    }

    fun getDirection(): MutableLiveData<Int> {
        return direction
    }

    /**
     * scheduling a call
     * @param type video call or voice call
     * @param context any context
     */
    fun schedulingCall(context: Context, type: String?) {
        if (direction.value == TYPE_OUTGOING ||
            time?.timeInMillis == 0L ||
            time == null
        ) {
            startCall(context, type)
            return
        }
        FakeCallWorker.scheduleSingleCall(context, time?.timeInMillis ?: 0, Data.Builder().apply {
            putString(Constants.KEY_TYPE, type ?: Constants.VOICE)
            putString("vid", url)
            direction.value?.let { putInt("direction", it) }
        }.build())
        direction.postValue(TYPE_INCOMING)
    }

    fun startCall(context: Context?, type: String?, direct: Int? = null) {
        context?.let { cancelWorker(it, Constants.WORKER_TAG) }
        val intent = Intent(context, FakeCallActivity::class.java)
        intent.putExtra(Constants.KEY_TYPE, type ?: Constants.VOICE)
        intent.putExtra(Constants.VIDEO, url)
        direction.value?.let { intent.putExtra("direction", direct ?: it) }
        direction.postValue(TYPE_INCOMING)
        callStarted?.invoke()
        context?.startActivity(intent)
    }

    fun cancelWorker(context: Context, tag: String) {
        FakeCallWorker.cancelWorker(context, tag)
    }
}
