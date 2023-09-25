package com.qrcode.ai.app.ui.fakecall

import android.content.Context
import android.content.Intent
import androidx.work.*
import com.ads.control.admob.AppOpenManager
import com.qrcode.ai.app.ui.main.MainActivity
import com.qrcode.ai.app.ui.main.ui.setupfakecall.SetupFakeCallViewModel
import com.qrcode.ai.app.ui.splash.SplashActivity
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import java.util.*
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit


class FakeCallWorker(
    appContext: Context, workerParams: WorkerParameters
) : Worker(appContext, workerParams) {
    override fun doWork(): Result {
        val name = inputData.getString("name")
        val img = inputData.getString("image")
        val type = inputData.getString("type")
        val vid = inputData.getString("vid")
        val direction = inputData.getInt("direction", SetupFakeCallViewModel.TYPE_INCOMING)
        val intent = Intent(applicationContext, FakeCallActivity::class.java)
        intent.apply {
            putExtra("type", type)
            putExtra("image", img)
            putExtra("name", name)
            putExtra("vid", vid)
            putExtra("direction", direction)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        AppOpenManager.getInstance().disableAppResumeWithActivity(FakeCallActivity::class.java)
        if (BasePrefers.getPrefsInstance().openResume) {
            AppOpenManager.getInstance().apply {
                enableAppResumeWithActivity(MainActivity::class.java)
                enableAppResumeWithActivity(SplashActivity::class.java)
            }
        }
        this.applicationContext.startActivity(intent)
        return Result.success()
    }

    companion object {
        fun scheduleSingleCall(context: Context, timer: Long, data: Data) {
            AppOpenManager.getInstance().apply {
                disableAppResumeWithActivity(MainActivity::class.java)
                disableAppResumeWithActivity(SplashActivity::class.java)
            }

            val now = Calendar.getInstance().timeInMillis
            val date = now + timer
            BasePrefers.getPrefsInstance().scheduled = date
            val mWorkManager = WorkManager.getInstance(context.applicationContext)
            val request = OneTimeWorkRequest.Builder(FakeCallWorker::class.java)
                .addTag(Constants.WORKER_TAG)
                .setInitialDelay(timer, TimeUnit.MILLISECONDS).setInputData(data).build()
            mWorkManager.enqueue(request)
        }
        fun cancelWorker(context: Context, tag : String){
            if (BasePrefers.getPrefsInstance().openResume) {
                AppOpenManager.getInstance().apply {
                    enableAppResumeWithActivity(MainActivity::class.java)
                    enableAppResumeWithActivity(SplashActivity::class.java)
                }
            }
            WorkManager.getInstance(context).cancelAllWorkByTag(tag)
        }

        fun isWorkScheduled(context: Context, tag: String): Boolean {
            val instance = WorkManager.getInstance(context)
            val statuses = instance.getWorkInfosByTag(tag)
            return try {
                var running = false
                val workInfoList = statuses.get()
                for (workInfo in workInfoList) {
                    val state = workInfo.state
                    running = (state == WorkInfo.State.RUNNING) or (state == WorkInfo.State.ENQUEUED)
                }
                running
            } catch (e: ExecutionException) {
                e.printStackTrace()
                false
            } catch (e: InterruptedException) {
                e.printStackTrace()
                false
            }
        }
    }
}
