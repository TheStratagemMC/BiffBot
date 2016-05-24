package com.thestratagemmc.biff.listener;

import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.event.BiffListener;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.biff.history.*;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Axel on 1/31/2016.
 */
public class LearnedNamedEntity extends BiffListener{

    public LearnedNamedEntity(BiffPlugin plugin) {
        super(plugin);
        plugin.getMessages().registerDefault("learnedEntity","I am learning...","Registered named entity!","My brain is growing in size...");
    }

    @EventHandler
    public void stimuli(final BiffStimuliEvent event){
        //Bukkit.broadcastMessage("akdfa");
        if (!event.getHandler().senderHasPermission("biff.teach")) return;
        //Bukkit.broadcastMessage("akdfa2");


        Conversation c = plugin.getConversationStore().getConversation(event.getSender().getBiffId());
        if (c == null) return;
        synchronized (c){
            if ( c == null) return;
            //Bukkit.broadcastMessage("akdfa3-"+c.getConversation().size());
            Intention intention = c.getLastBiffIntention();

           // if (intention == null) return;
            //Bukkit.broadcastMessage("c: "+c);
            //Bukkit.broadcastMessage("i: "+intention);
            //Bukkit.broadcastMessage(c.getLastBiffIntentionType() + "");
            if (c.getLastBiffIntentionType() == IntentionType.LEARN_ENTITY){
                //Bukkit.broadcastMessage("yodelididiodod");
                NamedEntityFactory factory = plugin.currentFactory(event.getSender().getBiffId());
                if (factory == null){
                    NamedEntityType t;
                    if (intention.getInformation().containsKey("type")){
                        t = plugin.getType(intention.getInformation().get("type"));
                    }
                    else t = plugin.getBrain().findRecognizedEntityType(event.getRequest().getWordArray());
                    if (t == null){
                        Bukkit.getLogger().severe("Couldn't find Named Entity Type for "+t);
                        return;
                    }
                    factory = new NamedEntityFactory(intention.getInformation().get("title"), t, event.getSender().getBiffId(), null);
                    plugin.setFactory(event.getSender().getBiffId(), factory);
                }
                else{
                    String lastNea = intention.getInformation().get("nea");
                    //read information from
                    NamedEntityAttribute a = factory.getAttribute(lastNea);
                    if (a == null){
                        Bukkit.getLogger().severe("Couldn't find named entity attribute "+a);
                    }
                    a.apply(factory, a.searchFor(event.getRequest()));
                    factory.doneWith(a.getName());
                }

                nextAttribute(plugin, factory, event);

            }
        }

    }

    private static void nextAttribute(BiffPlugin plugin, NamedEntityFactory factory, final BiffStimuliEvent event){
        final NamedEntityAttribute attr = factory.nextAttribute();

        if (attr == null){
            plugin.getNamedEntities().register(factory.get());
            final String response = plugin.getMessages().getRandom("learnedEntity");
            event.getRequest().runSolution("learnEntities", "Learning named entities", BiffConviction.COMPLETELY_SURE, response, new BiffSolutionHandler() {
                @Override
                public void run(){
                    event.getHandler().reply(response);
                }
            }, event.getSender().getBiffId());
            Bukkit.getLogger().info("Registered named entity "+factory.getTitle()+"!");
            factory.getLearnCallback().callback();
            return;
        }
        final String question = plugin.getBrain().createQuestion(attr.getWord());
        final NamedEntityType type = factory.getType();
        final String title = factory.getTitle();
        ConversationMessage msg = new ConversationMessage(Chatter.BIFF, question, IntentionType.LEARN_ENTITY, System.currentTimeMillis()){

            @Override
            public Intention getIntention() {
                Intention intent = new Intention(IntentionType.LEARN_ENTITY);
                intent.getInformation().put("nea", attr.getName());
                intent.getInformation().put("type", type.getName());
                intent.getInformation().put("title", title);
                return intent;
            }

        };
        event.getRequest().runSolution("learnEntities", "Learning named entities", BiffConviction.COMPLETELY_SURE,  new BiffSolutionHandler() {
            @Override
            public void run() {
                event.getHandler().reply(question);
            }
        }, msg, event.getSender().getBiffId());
    }
    public static void learn(final BiffStimuliEvent event, final String title, final String type, LearnCallback callback){
        NamedEntityType t = BiffPlugin.getPlugin().getType(type);

        if (t == null){
            Bukkit.getLogger().severe("Couldn't find Named Entity Type for "+t);
            return;
        }
        NamedEntityFactory factory = new NamedEntityFactory(title, t, event.getSender().getBiffId(), callback);
        BiffPlugin.getPlugin().setFactory(event.getSender().getBiffId(), factory);

        nextAttribute(BiffPlugin.getPlugin(), factory, event);
    }
}
