package com.thestratagemmc.biff.api;

/**
 * Created by Axel on 1/29/2016.
 */
public enum QuestionType {//to expand
    COUNT("countWords"),
    WHO("whoWords"),
    INFO_ABOUT("infoAboutWords"),
    WHY("whyWords"),
    HOW_DOES("howDoesWords"),
    TIME("timeWords"),
    PERSONAL("personalWords"),
    WHERE("whereWords"),
    THEORETICAL("theoreticalWords");

    private String tag;
    private QuestionType(String tag){
        this.tag = tag;
    }

    public String getTag(){
        return tag;
    }
}
