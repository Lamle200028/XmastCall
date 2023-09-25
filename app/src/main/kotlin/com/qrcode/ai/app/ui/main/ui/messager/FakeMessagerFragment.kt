package com.qrcode.ai.app.ui.main.ui.messager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ads.control.admob.AppOpenManager
import com.ads.control.ads.AperoAd
import com.ads.control.ads.AperoAdCallback
import com.ads.control.ads.wrapper.ApAdError
import com.ads.control.ads.wrapper.ApInterstitialAd
import com.applovin.sdk.AppLovinSdkUtils
import com.qrcode.ai.app.BuildConfig
import com.qrcode.ai.app.R
import com.qrcode.ai.app.R.*
import com.qrcode.ai.app.databinding.FragmentFakeMessagerBinding
import com.qrcode.ai.app.platform.BaseFragment
import com.qrcode.ai.app.ui.main.ui.setupfakecall.SetupFakeCallViewModel
import com.qrcode.ai.app.utils.BasePrefers
import com.qrcode.ai.app.utils.Constants
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.alicebot.ab.AIMLProcessor
import org.alicebot.ab.Bot
import org.alicebot.ab.Chat
import org.alicebot.ab.Graphmaster
import org.alicebot.ab.MagicBooleans
import org.alicebot.ab.MagicStrings
import org.alicebot.ab.PCAIMLProcessorExtension
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FakeMessagerFragment : BaseFragment<FragmentFakeMessagerBinding>() {
    private var adapter: FakeMessagerAdapter? = null
    private var adapterHint: HintMessagerAdapter? = null
    private var interAds: ApInterstitialAd? = null
    private val viewModel: FakeMessagerViewModel by viewModels()
    private val setupCallViewModel: SetupFakeCallViewModel by viewModels()
    private var PERMISSION_CAMERA1 = 1
    private var isRequestingPermission = false

    private lateinit var bot: Bot
    private lateinit var chat: Chat

    override val layoutId: Int
        get() = layout.fragment_fake_messager

    companion object {
        fun newInstance(): FakeMessagerFragment {
            return FakeMessagerFragment()
        }
    }

    override fun setupUI() {
        binding.apply {
            // message
            recyclerMessageDetails.adapter = adapter
            //hint
            recyclerHint.adapter = adapterHint

            editText.onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                if (hasFocus) {
                    btnSend.visibility = View.VISIBLE
                    recyclerHint.visibility = View.VISIBLE
                } else {
                    clearFocusEdittext()
                }
            }
        }
    }

    private fun clearFocusEdittext() {
        binding.apply {
            btnSend.visibility = View.GONE
            recyclerHint.visibility = View.GONE
            editText.clearFocus()
            hideKeyboard()
        }
    }

    override fun setupData() {
        adapter = FakeMessagerAdapter(requireContext())
        adapterHint = HintMessagerAdapter(viewModel.makeListItem())
        loadInter()
        binding.typing.visibility = View.VISIBLE
        lifecycleScope.launch {
            delay(1500)
            AppLovinSdkUtils.runOnUiThread {
                sendMessageAutoNew()
            }
        }

    }

    override fun setupListener() {
        binding.apply {
            icBack.setOnClickListener {
                findNavController().popBackStack()
            }
            icCall.setOnClickListener {
                context?.let { it1 ->
                    setupCallViewModel.startCall(it1, Constants.VOICE, SetupFakeCallViewModel.TYPE_OUTGOING)
                }
            }
            icVideo.setOnClickListener {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    if (!isRequestingPermission) {
                        isRequestingPermission = true
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.CAMERA),
                            PERMISSION_CAMERA1
                        )
                    } else {
                        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                        builder.setMessage(R.string.popup_per)
                        builder.setPositiveButton("Yes") { _, _ ->
                            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                            val uri = Uri.fromParts("package", requireActivity().packageName, null)
                            intent.data = uri
                            AppOpenManager.getInstance().disableAppResume()
                            startActivity(intent)
                        }
                        builder.setNegativeButton("No") { dialog, _ ->
                            dialog.dismiss()
                        }
                        builder.show()
                    }
                } else {
                    if (BasePrefers.getPrefsInstance().interChatVideoCall) {
                        if (interAds == null) {
                            loadAndShowInterAds {
                                context?.let { it1 ->
                                    setupCallViewModel.startCall(it1, Constants.VIDEO, SetupFakeCallViewModel.TYPE_OUTGOING)
                                }
                            }
                        } else {
                            showInterAds {
                                context?.let { it1 ->
                                    setupCallViewModel.startCall(it1, Constants.VIDEO, SetupFakeCallViewModel.TYPE_OUTGOING)
                                }
                            }
                        }
                    } else {
                        context?.let { it1 ->
                            setupCallViewModel.startCall(it1, Constants.VIDEO, SetupFakeCallViewModel.TYPE_OUTGOING)
                        }
                    }
                }
            }

            btnSend.setOnClickListener {
                val userMessage = editText.text.toString().trim()
                if (userMessage.isNotBlank()) {
                    sendMessage(userMessage)
                    adapter?.scrollToLastMessage(binding.recyclerMessageDetails)
                    clearFocusEdittext()
                    messageAutoReply()
                    editText.text.clear()
                } else {
                    // not empty
                }
            }
            adapterHint?.setOnItemClickListener(object : HintMessagerAdapter.OnItemClickListener {
                override fun onItemCLick(position: Int, data: String) {
                    val fakeUserMessage = ItemFakeMessager(
                        isMe = true,
                        messager = data,
                        isTime = View.GONE,
                        isAvatar = View.GONE
                    )
                    adapter?.addMessage(fakeUserMessage)
                    adapter?.scrollToLastMessage(binding.recyclerMessageDetails)
                    clearFocusEdittext()
                    val answer = getAnswerFromQuestion(data)
                    val response = chat.multisentenceRespond(data)
                    typing.visibility = View.VISIBLE
                    recyclerHint.visibility = View.GONE
                    if (answer != null) {
                        val fakeAutoReply =
                            ItemFakeMessager(
                                isMe = false,
                                messager = answer,
                                isTime = View.GONE,
                                isAvatar = View.VISIBLE
                            )
                        lifecycleScope.launch {
                            delay(1000)
                            AppLovinSdkUtils.runOnUiThread {
                                adapter?.addMessage(fakeAutoReply)
                                typing.visibility = View.GONE
                                adapter?.scrollToLastMessage(binding.recyclerMessageDetails)

                            }
                        }
                    } else {
                        lifecycleScope.launch {
                            delay(1000)
                            AppLovinSdkUtils.runOnUiThread {
                                mimicOtherMessage(response)
                                typing.visibility = View.GONE
                                adapter?.scrollToLastMessage(binding.recyclerMessageDetails)
                            }
                        }
                    }
                }
            })
        }
    }

    private fun sendMessageAutoNew() {
        val currentTimeMillis = System.currentTimeMillis()
        val timeString = convertTimeInMillisToTimeString(currentTimeMillis)
        Handler(Looper.getMainLooper()).postDelayed({
            val reply = "HO HO HO!"
            val listReply = listOf("Do you have a Christmas tree?",
                "Do you like snow? Have you ever made a snowman?",
            "Hello, dear. How do you do?",
            )
            binding.typing.visibility = View.GONE
            adapter?.addMessage(
                ItemFakeMessager(
                    isMe = false,
                    messager = reply,
                    isTime = View.VISIBLE,
                    time = timeString,
                    isAvatar = View.VISIBLE
                )
            )
            adapter?.addMessage(
                ItemFakeMessager(
                    isMe = false,
                    messager = listReply.random(),
                    isTime = View.GONE,
                    isAvatar = View.GONE
                )
            )
        }, 100)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun messageAutoReply() {
        binding.apply {
            val question = editText.text.toString()
            var response = chat.multisentenceRespond(question)
            val answer = getAnswerFromQuestion(question)
            if (answer != null) {
                val fakeAutoReply = ItemFakeMessager(
                    isMe = false,
                    isTime = View.GONE,
                    messager = answer,
                    isAvatar = View.VISIBLE
                )
                typing.visibility = View.VISIBLE
                recyclerHint.visibility = View.GONE

                GlobalScope.launch {
                    delay(1000)
                    requireActivity().runOnUiThread {
                        adapter?.addMessage(fakeAutoReply)
                        typing.visibility = View.GONE
                        adapter?.scrollToLastMessage(binding.recyclerMessageDetails)
                    }
                }
            } else {
                typing.visibility = View.VISIBLE
                recyclerHint.visibility = View.GONE
                GlobalScope.launch {
                    delay(1000)
                    requireActivity().runOnUiThread {
                        if (response.contains("</oob>")) {
                            response = "It sounds good, I'm on my wayyyyyy, Ho Ho Ho"
                        }
                        mimicOtherMessage(response)
                        typing.visibility = View.GONE
                        adapter?.scrollToLastMessage(binding.recyclerMessageDetails)
                    }
                }
            }
        }
    }

    private fun sendMessage(message: String) {
        val chatMessage = ItemFakeMessager(
            null, true, messager = message, isTime = View.GONE, isAvatar = View.GONE
        )
        adapter?.addMessage(chatMessage)

    }

    private fun mimicOtherMessage(message: String) {
        val chatMessage = ItemFakeMessager(
            null, false, messager = message, isTime = View.GONE, isAvatar = View.VISIBLE
        )
        adapter?.addMessage(chatMessage)
    }


    private fun getAnswerFromQuestion(question: String): String? {
        val normalizedQuestion = normalizeString(question)
        val keywords = extractKeywordsFromQuestion(normalizedQuestion)
        for (keyword in keywords) {
            val answers = viewModel.questionAndAnswersMap[keyword]
            if (answers != null) {
                return answers.random()
            }
        }
        return null
    }

    private fun normalizeString(input: String): String {
        return input.replace("[^A-Za-z]".toRegex(), "").lowercase(Locale.getDefault())
    }

    private fun extractKeywordsFromQuestion(question: String): List<String> {
        return question.split(" ").map { normalizeString(it) }
    }

    private fun convertTimeInMillisToTimeString(timeInMillis: Long): String {
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        return dateFormat.format(Date(timeInMillis))
    }

    private fun hideKeyboard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    private fun isSDCARDAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    // Function to copy the file
    @Throws(IOException::class)
    private fun copyFile(`in`: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int
        while (`in`.read(buffer).also { read = it } != -1) {
            out.write(buffer, 0, read)
        }
    }

    private fun mainFunction() {
        MagicBooleans.trace_mode = true
        println("trace mode = " + MagicBooleans.trace_mode)
        Graphmaster.enableShortCuts = true
        val request = "Hello."
        val response = chat.multisentenceRespond(request)
        println("Human: $request")
        println("Robot: $response")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isSDCARDAvailable()
        val assets = resources.assets
        val jayDir = File(context?.applicationContext?.filesDir?.absolutePath + "/hari/bots/Hari")
        if (!jayDir.exists()) jayDir.mkdirs()
        if (jayDir.exists()) {
            //Reading the file
            try {
                for (dir in assets.list("Hari")!!) {
                    val subdir = File(jayDir.path + "/" + dir)
                    subdir.mkdirs()
                    for (file in assets.list("Hari/$dir")!!) {
                        val f = File(jayDir.path + "/" + dir + "/" + file)
                        if (f.exists()) {
                            continue
                        }
                        var inputStream: InputStream?
                        var out: OutputStream?
                        inputStream = assets.open("Hari/$dir/$file")
                        out = FileOutputStream(jayDir.path + "/" + dir + "/" + file)
                        //copy file from assets to the mobile's SD card or any secondary memory
                        copyFile(inputStream, out)
                        inputStream.close()
                        out.flush()
                        out.close()
                    }
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        MagicStrings.root_path = context?.applicationContext?.filesDir?.absolutePath + "/hari"
        println("Working Directory = " + MagicStrings.root_path)
        AIMLProcessor.extension = PCAIMLProcessorExtension()
        bot = Bot("Hari", MagicStrings.root_path, "chat")
        chat = Chat(bot)
        mainFunction()
    }

    private fun loadAndShowInterAds(callback: () -> Unit) {
        AppOpenManager.getInstance().disableAppResume()
        AperoAd.getInstance().getInterstitialAds(activity,
            BuildConfig.Inter_Chat_VideoCall,
            loadAdCallback.apply { nextScreen2 = callback })
    }

    private fun loadInter() {
        if (BasePrefers.getPrefsInstance().interVideoCall) {
            AperoAd.getInstance().getInterstitialAds(
                activity,
                BuildConfig.Inter_VideoCall, object : AperoAdCallback() {
                    override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
                        super.onInterstitialLoad(interstitialAd)
                        interAds = interstitialAd
                    }
                })
        }
    }

    private fun showInterAds(callback: () -> Unit) {
        AppOpenManager.getInstance().disableAppResume()
        AperoAd.getInstance().forceShowInterstitial(
            requireActivity(),
            interAds,
            object : AperoAdCallback() {
                override fun onNextAction() {
                    super.onNextAction()
                    binding.bgWhite.visibility = View.VISIBLE
                }
                override fun onAdFailedToShow(adError: ApAdError?) {
                    super.onAdFailedToShow(adError)
                    callback.invoke()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                    callback.invoke()
                }
            },
            true
        )
    }

    private val loadAdCallback = object : AperoAdCallback() {

        var nextScreen2 = {}

        override fun onInterstitialLoad(interstitialAd: ApInterstitialAd?) {
            AperoAd.getInstance().forceShowInterstitial(
                requireActivity(),
                interstitialAd,
                adListener.apply { nextScreen = nextScreen2 },
                true
            )
        }

    }

    private val adListener = object : AperoAdCallback() {

        var nextScreen = {}

        override fun onAdFailedToLoad(adError: ApAdError?) {
            nextScreen.invoke()
        }

        override fun onAdClosed() {
            nextScreen.invoke()
        }
    }
}
