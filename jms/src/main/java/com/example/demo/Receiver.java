package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class Receiver {

    private List<String> messageList= new ArrayList<>();

    @JmsListener(destination = "hello")
    public void onMessage(String message) {
        log.debug("receving message: {}", message);
        messageList.add(message);
    }

    public List<String> getMessageList() {
        return messageList;
    }
}
