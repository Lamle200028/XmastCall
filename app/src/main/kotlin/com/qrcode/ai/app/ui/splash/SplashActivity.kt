package com.qrcode.ai.app.ui.splash

import android.Manifest
import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import androidx.lifecycle.MutableLiveData
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.ActivitySplashBinding
import com.qrcode.ai.app.platform.BaseActivity
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.isShowInterSplash
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private val splashViewModel: SplashViewModel by viewModels()
    private var isDestroyActivity: Boolean = false
    private var isRequestingNotiPermission: Boolean = false
    private val timeout: Long = 30000
    private val timeDelay: Long = 3000
    private val timeInterval: Long = 10
    private var restart = false
    private var timer = object : CountDownTimer(timeDelay, timeInterval) {
        override fun onTick(millisUntilFinished: Long) {
        }

        override fun onFinish() {
            onFinishTimer = true

            if (BasePrefers.getPrefsInstance().interSplash) {
                loadAndShowInterAds()
            } else {
                isShowInterSplash {
                    if (it) {
                        loadAndShowInterAds()
                    } else {
                        navigateNextScreen()
                    }
                }
            }
        }
    }
    private var onNextScreen = false
    private var mPrevConfig: Configuration? = null
    private var onFinishTimer = false
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _: Boolean ->
        isRequestingNotiPermission = false
        if (!onNextScreen) {
            if (!onFinishTimer) {
                timer.cancel()
            }
            startSplash()
        }
    }

    companion object {
        val isShowingSplash = MutableLiveData(true)
    }

    override fun setupUI() {
        super.setupUI()
        AppOpenManager.getInstance().disableAppResumeWithActivity(SplashActivity::class.java)
        requestPermissionAndroid13()
        mPrevConfig = Configuration(resources.configuration)
    }

    override fun onResume() {
        super.onResume()
        AperoAd.getInstance().onCheckShowSplashWhenFail(this, adListener, timeDelay.toInt())
    }

    override fun setupListener() {
        super.setupListener()
        startSplash()
    }

    private fun startSplash() {
        (timer as CountDownTimer).start()
    }

    private val adListener: AperoAdCallback = object : AperoAdCallback() {

        override fun onNextAction() {
            splashViewModel.isShowInterSplash.postValue(true)
            super.onNextAction()
        }

        override fun onAdFailedToLoad(adError: ApAdError?) {
            navigateNextScreen()
        }

        override fun onAdFailedToShow(adError: ApAdError?) {
            navigateNextScreen()
        }

        override fun onAdClosed() {
            super.onAdClosed()
            navigateNextScreen()
        }
    }

    private fun loadAndShowInterAds() {
        AppOpenManager.getInstance().disableAppResume()
        AperoAd.getInstance().loadSplashInterstitialAds(
            this, BuildConfig.Inter_Splash, timeout, 5000, adListener
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val navController = Navigation.findNavController(this, R.id.nav_host_splash_fragment)
        if (navController.currentDestination?.id == R.id.splashActivity) {
            exitProcess(0)
        } else {
            finish()
        }
    }

    override fun onStop() {
        super.onStop()
        restart = true
        timer.cancel()
    }

    override fun onDestroy() {
        isDestroyActivity = true
        super.onDestroy()
    }

    override fun onRestart() {
        super.onRestart()
        restart = false
        if (
            findNavController(R.id.nav_host_splash_fragment).currentDestination?.id == R.id.splashNavFragment &&
            splashViewModel.isShowInterSplash.value == false
        ) {
            timer.start()
        }
    }

    private fun navigateNextScreen() {
        val navController = Navigation.findNavController(this, R.id.nav_host_splash_fragment)
        if (!restart && !isDestroyActivity && !isRequestingNotiPermission && !onNextScreen) {
            onNextScreen = true
            isShowingSplash.postValue(false)
            val newUser = BasePrefers.getPrefsInstance().newUser
            newUser.let {
                if (it) {
                    navController.navigate(R.id.openFristLanguage)
                } else {
                    if (!BasePrefers.getPrefsInstance().openboard) {
                        navController.navigate(R.id.openOnboarding)
                    } else {
                        Log.d("checkk", BasePrefers.getPrefsInstance().openboard.toString())
                        navController.navigate(R.id.openMain)
                        Handler().postDelayed({
                            finish()
                        }, 1000)
                    }
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean =
        findNavController(R.id.nav_host_splash_fragment).navigateUp()

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        mPrevConfig = Configuration(newConfig)
    }

    private fun requestPermissionAndroid13() {
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PermissionChecker.PERMISSION_DENIED &&
                !shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)
            ) {
                isRequestingNotiPermission = true
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    override val layoutId: Int
        get() = R.layout.activity_splash
}
