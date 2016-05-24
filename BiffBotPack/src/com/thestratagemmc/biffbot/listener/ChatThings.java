package com.thestratagemmc.biffbot.listener;

import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.MessageHandler;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import org.bukkit.event.EventHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Axel on 2/23/2016.
 */
public class ChatThings extends BiffListener {

    public List<MessageInfo> recentMessages = new ArrayList<>();
    public ChatThings(BiffPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void stim(BiffStimuliEvent event){
        if (event.getMessage().startsWith("s/")){
            try{
                String[] args = event.getMessage().split("/");
                for (int i = 0; i <= 100; i++){
                    if (i >= recentMessages.size()) break;
                    MessageInfo message = recentMessages.get(i);
                    if (message.message.contains(args[1])){
                        String newM = message.message.replace(args[1],args[2]);
                        message.handler.reply("Correction, ["+message.sender+"]: "+newM);
                        return;
                    }
                }
            }catch(Exception e){
                //
            }
        }
        else{
            recentMessages.add(0, new MessageInfo(event.getHandler(), event.getSender().getName(), event.getMessage()));
        }
    }

    public class MessageInfo{
        MessageHandler handler;
        String sender;
        String message;

        public MessageInfo(MessageHandler handler, String sender, String message) {
            this.handler = handler;
            this.sender = sender;
            this.message = message;
        }
    }
}
