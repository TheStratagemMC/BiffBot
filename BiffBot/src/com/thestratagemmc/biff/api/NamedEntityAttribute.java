package com.thestratagemmc.biff.api;

/**
 * Created by Axel on 1/31/2016.
 */
public interface NamedEntityAttribute{
    String getName();
    NERWord getWord();
    void apply(NamedEntityFactory factory, String value);

    String searchFor(BiffRequest request);
}
