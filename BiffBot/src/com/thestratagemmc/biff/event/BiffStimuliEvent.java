package com.thestratagemmc.biff.event;

import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.history.ConversationMessage;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.event.Cancellable;

import java.util.UUID;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffStimuliEvent extends BiffEvent{
    private BiffPlugin plugin;
    private MessageHandler messageHandler;
    private String message;
    private BiffUser sender;

    private BiffRequest request;


    public BiffStimuliEvent(BiffPlugin plugin, MessageHandler messageHandler, String message, BiffUser sender) {
        super(true);
        this.plugin = plugin;
        this.messageHandler = messageHandler;
        this.message = message;
        this.sender = sender;
        request = new BiffRequest(plugin, message);
    }

    public BiffRequest getRequest(){
        return request;
    }

    public BiffUser getSender(){
        return sender;
    }

    public MessageHandler getHandler(){
        return messageHandler;
    }

    public BiffPlugin getPlugin(){
        return plugin;
    }

    public String getMessage() {
        return message;
    }

    public void respond(final String name, final String response, final BiffConviction conviction){
        getRequest().runSolution(name, "Made a basic response to request..", conviction, response, new BiffSolutionHandler() {
            @Override
            public void run() {
                getHandler().reply(response);
            }
        }, getSender().getBiffId());
    }

    public void respond(final String name, final TextComponent response, final BiffConviction conviction){
        getRequest().runSolution(name, "Made a basic response to request..", conviction, response.toPlainText(), new BiffSolutionHandler() {
            @Override
            public void run() {
                getHandler().reply(response);
            }
        }, getSender().getBiffId());
    }

    public void respond(final String name, final String response, final BiffConviction conviction, final ConversationMessage message){
        getRequest().runSolution(name, "Made a basic response to request..", conviction, new BiffSolutionHandler() {
            @Override
            public void run() {
                getHandler().reply(response);
            }
        }, message, getSender().getBiffId());
    }
}
