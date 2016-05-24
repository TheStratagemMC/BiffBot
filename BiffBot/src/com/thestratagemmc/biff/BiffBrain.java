package com.thestratagemmc.biff;

import com.thestratagemmc.biff.api.*;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import com.thestratagemmc.biff.listener.LearnedNamedEntity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Created by Axel on 1/29/2016.
 */

/*
Utility methods for Biff pseudo language processing
 */
public class BiffBrain {
    private static Pattern annoyingPunctuation = Pattern.compile("[,?|/>\\s+]");
    private static Pattern lettersOnly = Pattern.compile("[a-zA-Z]+");
    //common words
    private List<String> mePronouns;
    private List<String> youPronouns;
    private List<String> thirdPersonPronouns;
    private List<String> toBePresent;
    private List<String> toBePast;
    private List<String> toBeFuture;
   /* //questions
    private List<String> countWords;
    private List<String> whoWords;
    private List<String> infoAboutWords;
    private List<String> whyWords;
    private List<String> howDoesWords;
    private List<String> timeWords;
    private List<String> personalWords;
    private List<String> theoreticalWords;*/
    private List<String> nonNameWords;

    private static Pattern IPV4_PATTERN =  null;
    private static Pattern IPV6_PATTERN = null;
    private static final String ipv4Pattern = "(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])";
    private static final String ipv6Pattern = "([0-9a-f]{1,4}:){7}([0-9a-f]){1,4}";

    public BiffBrain(){
        mePronouns = r("mePronouns","me","I", "mine");
        youPronouns = r("youPronouns","you","your","yours");
        thirdPersonPronouns = r("thirdPersonPronouns","he","she","her","him","hers","his");
        toBePresent = r("toBePresent","is","am","are");
        toBePast = r("toBePast", "was","were");
        toBeFuture = r("toBeFuture","will");
        nonNameWords=r("nonNameWords","or");
        r("countWords","how many");
        r("whoWords", "who", "who's");
        r("infoAboutWords", "what is","what's");
        r("whyWords", "why");
        r("howDoes", "how does", "how is");
        r("timeWords", "when");
        r("personalWords","do you");
        r("theoreticalWords","you think", "would");
        r("whereWords","where");
        r("specifics","the");
        r("greetings","hey","hello","yo","hallo","yooo","what's up","sup","wassup");
        r("group","guys","guiz","gurls","girlz","girls","people","server","gents","gentlemen","ladies");


        r("ignoreTags","mePronouns","youPronouns","thirdPersonPronouns",
                "toBePresent","toBePast","toBeFuture","nonNameWords",
                "countWords","whoWords","infoAboutWords","whyWords","howDoes","timeWords","personalWords","theoreticalWords","specifics",
                "greetings","group");
        IPV4_PATTERN =  Pattern.compile(ipv4Pattern, Pattern.CASE_INSENSITIVE);
        IPV6_PATTERN = Pattern.compile(ipv6Pattern, Pattern.CASE_INSENSITIVE);
    }

    private static List<String> r(String name, String... defaults){
        BiffPlugin.getPlugin().getMessages().registerDefault(name, defaults);
        return BiffPlugin.getPlugin().getMessages().get(name);
    }


    private static String g(String tag){
        return BiffPlugin.getPlugin().getMessages().getRandom(tag);
    }
    public List<QuestionType> findMatchingQuestionTypes(Map<String,Word> wordMap, String originalSentence){
        List<QuestionType> types = new ArrayList<>();
        for (QuestionType type : QuestionType.values()){
            List<String> words = BiffPlugin.getPlugin().getMessages().get(type.getTag());
            if (words == null) continue;
            for (String word : words){
                if (wordMap.containsKey(word) || originalSentence.toLowerCase().contains(word)){
                    types.add(type);
                }
            }
        }
        return types;
    }

    public BiffTense getTense(Map<String,Word> wordMap){
        int present = 0;
        int past = 0;
        int future = 0;
        for (String string : wordMap.keySet()){
            if (toBePresent.contains(string)) present++;
            if (toBePast.contains(string)) past++;
            if (toBeFuture.contains(string)) future++;
        }
        if (present > past && present > future) {
            if (past > 0 || future > 0) return BiffTense.UNKNOWN;
            return BiffTense.PRESENT;
        }
        else if (past > present && past > future){
            if (present > 0 || future > 0) return BiffTense.UNKNOWN;
            return BiffTense.PAST;
        }
        else if (future > present || future > past){
            if (present > 0 || past > 0) return BiffTense.UNKNOWN;
            return BiffTense.FUTURE;
        }
        return BiffTense.UNKNOWN;
    }

    public boolean isPartOfName(String word){
        return !nonNameWords.contains(word.toLowerCase());
    }

    public String createQuestion(NERWord word){
        String question = g(word.getType().getTag()) +" "+ (word.isSpecific() ? g("specifics") : "") + " "+word.getWord()+"?";
        return capitalizeFirstLettersOfSentence(question);
    }

    public String capitalizeFirstLettersOfSentence(String input){
        int pos = 0;
        boolean capitalize = true;
        StringBuilder sb = new StringBuilder(input);
        while (pos < sb.length()) {
            if (sb.charAt(pos) == '.') {
                capitalize = true;
            } else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
                sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
                capitalize = false;
            }
            pos++;
        }

        return sb.toString();
    }
    public String findIpAddressOrWebsite(String originalText){
        for (String word : originalText.split(" ")){
            if (word.contains(".")){
                String newWord = annoyingPunctuation.matcher(word).replaceAll("");

                if (isIpAddress(word)) return word;
                try{
                    InetAddress address = InetAddress.getByName(word);
                }catch(Exception e){
                    continue;
                }
                return word;
            }
        }
        return null;
    }
    public List<String> findIpAddressesOrWebsites(String originalText){
        List<String> results = new ArrayList<>();
        for (String word : originalText.split(" ")){
            if (word.contains(".") && word.lastIndexOf(".") < word.length()-1){
                String newWord = annoyingPunctuation.matcher(word).replaceAll("");

                if (isIpAddress(word)) {
                    results.add(word);
                    continue;
                }
                try{
                    InetAddress address = InetAddress.getByName(word);
                }catch(Exception e){
                    continue;
                }
                results.add(word);
            }
        }
        return results;
    }


    public NamedEntityType findRecognizedEntityType(String[] words){
        for (String string : words){
            for (NamedEntityType type : BiffPlugin.getPlugin().getTypes()){
                for (String name : BiffPlugin.getPlugin().getMessages().get(type.getDisplayNameTag())){
                    if (string.toLowerCase().contains(name)) return type;
                }

            }

        }
        return null;
    }

    public boolean isIpAddress(String ipAddress) {

        Matcher m1 = IPV4_PATTERN.matcher(ipAddress);
        if (m1.matches()) {
            return true;
        }
        Matcher m2 = IPV6_PATTERN.matcher(ipAddress);
        return m2.matches();
    }

    public double compareMaps(Map<String,Word> map1, Map<String,Word> map2){
        int score = 0;
        for (String key : map1.keySet()){
            if (map2.containsKey(key)) score++;
        }

        double score1 = score / map1.size();

        int _score = 0;
        for (String key : map2.keySet()){
            if (map1.containsKey(key)) _score++;
        }
        double score2 = _score/map2.size();

        return (score1 + score2) / 2;
    }

    public boolean isGreeting(Map<String,Word> map){
        int score = 0;
        for (String greeting : BiffPlugin.getPlugin().getMessages().get("greetings")){
            if (map.containsKey(greeting)) score++;
        }

        double p = score / map.size();
        if (p == 1) return true;
        for (String group : BiffPlugin.getPlugin().getMessages().get("group")){
            if (map.containsKey(group)) return true;
        }
        if (p < 0.2) return false;
        return true;
    }

    public Player getRelevantPlayer(String name){
        BiffUser user = BiffPlugin.getPlugin().getUserStore().getUserOnIndex(name);
        String n = null;
        if (BiffPlugin.getPlugin().getNamedEntities().get(name) != null){
            n = BiffPlugin.getPlugin().getNamedEntities().get(name).getAttributes().get("username");
        }
        Player _p = null;
        for (Player player : Bukkit.getOnlinePlayers()){
            if (player.getName().toLowerCase().startsWith(name.toLowerCase())) _p = player;
        }

        Player p;
        if (user != null){
            p = user.getMinecraftPlayer();
        }
        else if (_p != null){
            p = _p;
        }
        else{
            p = Bukkit.getPlayer(n);
        }
        return p;
    }

    public boolean isToBiff(BiffStimuliEvent event){
        if (event.getHandler().isPersonalChat()) return true;
        if (event.getHandler().getChatSize() > 3) return false;
        for (String string : BiffPlugin.getPlugin().getMessages().get("group")){
            if (event.getRequest().getWordMap().containsKey(string)) return false;
        }
        for (String string : BiffPlugin.getPlugin().getMessages().get("biffNames")){
            if (event.getRequest().getWordMap().containsKey(string)) return true;
        }
        for (String string : BiffPlugin.getPlugin().getMessages().get("greetings")){
            if (event.getRequest().getWordMap().containsKey(string)) return true;
        }
        return false;
    }

    public String location(Location location){
        return "World: "+location.getWorld()+", X: "+location.getX()+", Y: "+location.getY()+", Z: "+location.getZ();
    }


}
