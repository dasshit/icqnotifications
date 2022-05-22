package io.jenkins.plugins.icqnotifications.utils;

public class IcqUrlButton extends IcqBaseButton {

    private String text;
    private String url;
    private String style = "base";

    public IcqUrlButton setText(String text){
        this.text = text;
        return this;
    }
    public String getText(){
        return text;
    }

    public IcqUrlButton setUrl(String url){
        this.url = url;
        return this;
    }
    public String getUrl(){
        return url;
    }

    public IcqUrlButton setStyle(String style){
        this.style = style;
        return this;
    }
    public String getStyle(){
        return style;
    }

}
