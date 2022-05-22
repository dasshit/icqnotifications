package io.jenkins.plugins.icqnotifications.utils;

public class IcqCallbackButton extends IcqBaseButton {

    private String text;
    private String url;
    private String style = "base";

    public IcqCallbackButton setText(String text){
        this.text = text;
        return this;
    }
    public String getText(){
        return text;
    }

    public IcqCallbackButton setUrl(String url){
        this.url = url;
        return this;
    }
    public String getUrl(){
        return url;
    }

    public IcqCallbackButton setStyle(String style){
        this.style = style;
        return this;
    }
    public String getStyle(){
        return style;
    }

}
