package com.thestratagemmc.biff.api;

/**
 * Created by Axel on 1/31/2016.
 */
public class NERWord {
    private QuestionType type;
    private boolean specific; //uses "the"
    private String word;

    public NERWord(QuestionType type, boolean specific, String word) {
        this.type = type;
        this.specific = specific;
        this.word = word;
    }

    public QuestionType getType() {
        return type;
    }

    public boolean isSpecific() {
        return specific;
    }

    public String getWord() {
        return word;
    }
}
