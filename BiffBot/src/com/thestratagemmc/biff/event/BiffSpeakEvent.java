package com.thestratagemmc.biff.event;

import com.thestratagemmc.biff.api.BiffConviction;
import com.thestratagemmc.biff.api.BiffSolutionHandler;
import com.thestratagemmc.biff.api.MessageHandler;
import com.thestratagemmc.biff.api.Word;
import com.thestratagemmc.biff.history.ConversationMessage;
import org.bukkit.event.Cancellable;

import java.util.Map;
import java.util.UUID;

/**
 * Created by Axel on 2/10/2016.
 */
public class BiffSpeakEvent extends BiffEvent implements Cancellable{
    private String reason;
    private String description;
    private BiffConviction conviction;
    private BiffSolutionHandler handler;
    private Map<String,Word> inputMap;
    private ConversationMessage message;
    private UUID with;
    private MessageHandler messageHandler;
    private boolean cancelled = false;

    public BiffSpeakEvent(boolean async, String reason, String description, BiffConviction conviction, BiffSolutionHandler handler, Map<String, Word> inputMap, ConversationMessage message, UUID with, MessageHandler messageHandler) {
        super(async);
        this.reason = reason;
        this.description = description;
        this.conviction = conviction;
        this.handler = handler;
        this.inputMap = inputMap;
        this.message = message;
        this.with = with;
        this.messageHandler = messageHandler;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getReason() {
        return reason;
    }

    public String getDescription() {
        return description;
    }

    public BiffConviction getConviction() {
        return conviction;
    }

    public BiffSolutionHandler getHandler() {
        return handler;
    }

    public Map<String, Word> getInputMap() {
        return inputMap;
    }

    public ConversationMessage getMessage() {
        return message;
    }

    public MessageHandler getMessageHandler() {
        return messageHandler;
    }

    public UUID getWith() {
        return with;
    }
}
