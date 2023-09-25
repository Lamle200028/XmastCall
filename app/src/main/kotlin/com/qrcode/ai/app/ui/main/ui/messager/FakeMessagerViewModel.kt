package com.qrcode.ai.app.ui.main.ui.messager

import android.view.View
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FakeMessagerViewModel @Inject constructor() : ViewModel() {

    companion object {
        private const val TAG = "FakeMessagerViewModel"
    }

    fun makeListItem(): ArrayList<ItemHintMessager> {
        val items = ArrayList<ItemHintMessager>()
        items.add(ItemHintMessager(null, "Hello"))
//        items.add(ItemHintMessager(null, "Hi"))
//        items.add(ItemHintMessager(null, "Thanks"))
        items.add(ItemHintMessager(null, "Name"))
        items.add(ItemHintMessager(null, "Old"))
        items.add(ItemHintMessager(null, "I like you"))
        items.add(ItemHintMessager(null, "I love you"))
        return items
    }

    fun randomMess(): ItemFakeMessager {
      val list = listOf(
            "Hoho,. Hello, dear child. Did you behave well the whole year?",
            "Ho ho ho. Age is but a number. Could you tell me how old you are?",
            "I can only tell you that I am much older than you are",
            "I like reading books",
            "And I like you, too",
            "That's great to hear",
            "Very interesting. Let me think",
            "Let me think about that",
            "I have to think about this",
            "It's my pleasure to help",
            "You know yourself better than me",
            "Well, let me guess,...",
            "good!",
            "okay",
            "Ho ho ho. Interesting!",
            "Glad you think so",
            "Yes, I am",
            "I want to know your name. What can I call you?",
            "How are you today",
            "This Christmas is so beautiful. The city is full of flash light and Christmast trees. Do you like this?",
            "I am going to give gifts to children on Christmas Eve. What kind of gift you want?",
            "There will be a surprise for you on Christmas Eve. I hope you will like it. Please wait for you.",
            "Bye. Good night. See you again",
            "Don't forget to go back to this app to talk to me everyday. I'm always here and waiting for you.",
        )
        return ItemFakeMessager(
            isMe = false,
            messager = list.random(), isTime = View.GONE, isAvatar = View.VISIBLE
        )
    }

    val questionAndAnswersMap = mapOf(
        "hello" to listOf("Hoho. Hello, dear child. Did you behave well the whole year? "),
        //Hi. Thanks. I am well. My last year was quite good......
        "hithanksiamwellmylastyearwasquitegood" to listOf(
            "Ho ho ho. Age is but a number. Could you tell me how old you are?"
        ),
        "hi" to listOf(
            "Ho ho ho. Age is but a number. Could you tell me how old you are?"
        ),
        "old" to listOf(
            "I can only tell you that I am much older than you are",
            "I like reading books"
        ),
        "favourite" to listOf("And I like you, too"),
        "whatdoyoulike" to listOf("And I like you, too"),

        //"Thank you so much. I also like you.
        "thankyousomuchialsolikeyou" to listOf(
            "That's great to hear",
            "Very interesting. Let me think",
            "Let me think about that",
            "I have to think about this",
            "It's my pleasure to help",
            "You know yourself better than me",
            "Well, let me guess,...",
            "good!",
            "okay",
            "Ho ho ho. Interesting!",
            "Glad you think so",
            "Yes, I am",
            "I want to know your name. What can I call you?",
        ),

        //i also like you
        "ialsolikeyou" to listOf(
            "That's great to hear",
            "Very interesting. Let me think",
            "Let me think about that",
            "I have to think about this",
            "It's my pleasure to help",
            "You know yourself better than me",
            "Well, let me guess,...",
            "good!",
            "okay",
            "Ho ho ho. Interesting!",
            "Glad you think so",
            "Yes, I am",
            "I want to know your name. What can I call you?",
        ),
        //I love you
        "iloveyou" to listOf(
            "That's great to hear",
            "Very interesting. Let me think",
            "Let me think about that",
            "I have to think about this",
            "It's my pleasure to help",
            "You know youself better than me",
            "Well, let me guess,...",
            "good!",
            "okay",
            "Ho ho ho. Interesting!",
            "Glad you think so",
            "Yes, I am",
            "I want to know your name. What can I call you?",
        ),
        //I like you"
        "ilikeyou" to listOf(
            "That's great to hear",
            "Very interesting. Let me think",
            "Let me think about that",
            "I have to think about this",
            "It's my pleasure to help",
            "You know youself better than me",
            "Well, let me guess,...",
            "good!",
            "okay",
            "Ho ho ho. Interesting!",
            "Glad you think so",
            "Yes, I am",
            "I want to know your name. What can I call you?",
        ),


        "name" to listOf("How are you today"),
        "finethanks" to listOf(
            "This Christmas is so beautiful. The city is full of flash light and Christmas trees. Do you like this?"
        ),
//        Yeah, I am very interested in that
        "yeahiamveryinterestedinthat" to listOf(
            "This Christmas is so beautiful. The city is full of flash light and Christmas trees. Do you like this?",
            "I am going to give gifts to children on Christmas Eve. What kind of gift you want?",
            "There will be a surprise for you on Christmas Eve. I hope you will like it. Please wait for you",
            "Bye. Good night. See you again"
        ),
        "bye" to listOf(
            "Bye!!"
        ),
    )


}
