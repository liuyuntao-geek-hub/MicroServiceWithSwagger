package com.deloitte.demo.DroolsPrototype.drools;

import java.io.Serializable;

/**
 * Created by yuntliu on 12/7/2017.
 */
public class Message implements Serializable{
    private String content;


    public Message(String content) {
        this.content = content;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override public String toString() {
        return "Message{" + "content='" + content + '\'' + '}';
    }
}