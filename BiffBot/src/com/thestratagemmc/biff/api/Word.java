package com.thestratagemmc.biff.api;

/**
 * Created by Axel on 1/29/2016.
 */
public class Word {
    private int position;
    private String word;
    //other arbritrary information


    public Word(int position, String word) {
        this.position = position;
        this.word = word;
    }

    public int getPosition(){
        return position;
    }
}
