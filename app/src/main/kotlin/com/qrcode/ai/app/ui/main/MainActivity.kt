package com.qrcode.ai.app.ui.main

import  android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.Navigation
import com.ads.control.admob.AppOpenManager
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.ActivityMainBinding
import com.qrcode.ai.app.platform.BaseActivity
import com.qrcode.ai.app.ui.main.ui.writeletter.WriteLetterFragment
import com.qrcode.ai.app.ui.main.widget.PrivacyDialog
import com.qrcode.ai.app.utils.AppManager
import com.qrcode.ai.app.utils.Constants
import com.qrcode.ai.app.utils.Constants.LINK_STORE
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {

    @Inject
    lateinit var appManager: AppManager
    private var dialogPrivacy: PrivacyDialog? = null
    private var connectivityManager: ConnectivityManager? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        listenForNetworkState()
        checkNetworkState()
    }

    override fun onResume() {
        super.onResume()
        listenForNetworkState()
    }

    private fun checkNetworkState() {
        val navController = Navigation.findNavController(
            this, R.id.nav_host_main_fragment
        )
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo == null || !networkInfo.isConnected) {
            runOnUiThread {
                navController.navigate(R.id.noInternet)
            }
        }
    }

    override fun setupListener() {
        super.setupListener()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        val navController = Navigation.findNavController(this, R.id.nav_host_main_fragment)
        if (navController.currentDestination?.id != R.id.homeFragment ) {
            if (navController.currentDestination?.id != R.id.backGroundLetter || navController.currentDestination?.id != R.id.headerLetter) {
                navController.popBackStack(R.id.homeFragment, false)
            }
            WriteLetterFragment.page = 0
        } else {
            if (appManager.isBackPressFinish) {
                finishAffinity()
            } else {
                Toast.makeText(this, R.string.back_pressed_finish, Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun openShare() {
        AppOpenManager.getInstance().disableAppResume()
        val text = buildString {
            append(getString(R.string.your_friend))
            append(getString(R.string.app_name))
            append(getString(R.string.visit_app))
            append(LINK_STORE)
        }
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, text)
        intent.type = Constants.type_plain_text
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.choose_way_share)),
            Constants.request_code_share
        )
    }

    private fun openRate() {
        openAppInPlayStore()
    }

    private fun openPrivacy() {
        AppOpenManager.getInstance().disableAppResume()
        dialogPrivacy = PrivacyDialog()
        dialogPrivacy?.show(supportFragmentManager, null)
    }

    @Suppress("DEPRECATION")
    private fun goToFeedback() {
        AppOpenManager.getInstance().disableAppResume()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = Constants.type_plain_text
            putExtra(Intent.EXTRA_EMAIL, arrayOf(Constants.email_support))
            putExtra(Intent.EXTRA_SUBJECT, buildString {
                append(getString(R.string.feedback))
                append(getString(R.string.app_name))
            })
            putExtra(Intent.EXTRA_TEXT, getString(R.string.feedback))
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.feedback)), Constants.request_code_share
        )
    }

    private fun openAppInPlayStore() {
        AppOpenManager.getInstance().disableAppResume()
        val uri = Uri.parse(Constants.store_uri)
        val goToMarketIntent = Intent(Intent.ACTION_VIEW, uri)

        var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
        flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
        goToMarketIntent.addFlags(flags)

        try {
            startActivityForResult(goToMarketIntent, Constants.request_code_share)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(
                Intent.ACTION_VIEW, Uri.parse(LINK_STORE)
            )
            startActivityForResult(intent, Constants.request_code_share)
        }
    }

    private fun listenForNetworkState() {
        val navController = Navigation.findNavController(
            this, R.id.nav_host_main_fragment
        )
        connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                runOnUiThread {
                    if (navController.currentDestination?.id == R.id.noInternetFragment) {
                        navController.popBackStack()
                    }
                }
            }
            override fun onLost(network: Network) {
                runOnUiThread {
                    navController.navigate(R.id.noInternet)
                }
            }
        }
        connectivityManager?.registerDefaultNetworkCallback(networkCallback as ConnectivityManager.NetworkCallback)
    }
}
