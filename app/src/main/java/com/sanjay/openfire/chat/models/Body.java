package com.sanjay.openfire.chat.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Awesome Pojo Generator
 */
public class Body {

    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("xml:lang")
    @Expose
    private String xmlLang;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getXmlLang() {
        return xmlLang;
    }

    public void setXmlLang(String xmlLang) {
        this.xmlLang = xmlLang;
    }

}