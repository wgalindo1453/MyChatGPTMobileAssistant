package com.example.mychatgpt;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class MessageViewModel extends ViewModel {
    private final MutableLiveData<List<Message>> messageList = new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<Message>> getMessageList() {
        return messageList;
    }

    public void addMessage(Message message) {
        List<Message> currentMessages = messageList.getValue();
        currentMessages.add(message);
        messageList.setValue(currentMessages);
    }
}
