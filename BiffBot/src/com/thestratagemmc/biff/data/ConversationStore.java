package com.thestratagemmc.biff.data;

import com.thestratagemmc.biff.api.BiffUser;
import com.thestratagemmc.biff.history.Conversation;
import com.thestratagemmc.biff.history.ConversationMessage;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Axel on 1/31/2016.
 */
public class ConversationStore {

    private HashMap<UUID,Conversation>  conversations = new HashMap<>();

    public void continueConversation(UUID with, ConversationMessage message){
        if (with == null || message == null) return;
        Conversation conversation;
        if (conversations.containsKey(with)) conversation = conversations.get(with);
        else conversation = new Conversation(with);

        conversation.add(message);
        conversations.put(with, conversation);
        //Bukkit.broadcastMessage("oioioioi");
    }

    public Conversation getConversation(UUID with){
        return conversations.get(with);
    }
}
