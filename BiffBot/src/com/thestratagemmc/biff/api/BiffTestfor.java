package com.thestratagemmc.biff.api;

import com.google.common.base.Joiner;
import com.thestratagemmc.biff.BiffPlugin;
import com.thestratagemmc.biff.event.BiffStimuliEvent;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Created by Axel on 1/29/2016.
 */
public class BiffTestfor {
    private BiffPlugin plugin;
    private Map<String,Word> wordMap;
    private String originalSentence;
    private String[] wordArray;
    private BiffSolutionHandler solution;
    private UUID id;

    private static Pattern ipSearchPattern = Pattern.compile( "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)");
    private static Pattern annoyingPunctuation = Pattern.compile("[,?|/>\\s+]");

    public void setSolutionHandler(BiffSolutionHandler solution){
        this.solution = solution;
    }
    public BiffTestfor(BiffStimuliEvent event){
        plugin =event.getPlugin();
        wordMap = event.getRequest().getWordMap();
        originalSentence = event.getRequest().getOriginalMessage();
        wordArray = event.getRequest().getWordArray();
        id = event.getRequest().getId();
    }

    boolean shouldBeQuestion = false;
    List<String> necessaryWords = new ArrayList<>(); //can be split with / for alt. words
    QuestionType questionType = null;

    public void setPlugin(BiffPlugin plugin) {
        this.plugin = plugin;
    }

    public void setShouldBeQuestion(boolean shouldBeQuestion) {
        this.shouldBeQuestion = shouldBeQuestion;
    }

    public void addNecessaryWords(String tag){
        necessaryWords.addAll(plugin.getMessages().get(tag));
    }
    @Deprecated
    public void addNecessaryWord(String word) {
        necessaryWords.add(word);
    }

    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }

    public BiffConviction matches(){
        int matchingWords = 0;
        for (String string : necessaryWords){
            for (String s : string.split("/")){
                if (wordMap.containsKey(s)) matchingWords++;
            }
        }

        if (shouldBeQuestion){
            if (plugin.getBrain().findMatchingQuestionTypes(wordMap, originalSentence).contains(questionType)){
                if (matchingWords >= necessaryWords.size()) return BiffConviction.COMPLETELY_SURE;
                else if (necessaryWords.size() - matchingWords == 1) return BiffConviction.POSSIBLY;
                else if (necessaryWords.size() > 5 && necessaryWords.size()-matchingWords < 4) return BiffConviction.DOUBT_IT;
                else return BiffConviction.DEFINITELY_NOT;
            }
            return BiffConviction.DEFINITELY_NOT;
        }
        if (matchingWords == necessaryWords.size()) return BiffConviction.PRETTY_SURE;
        if (matchingWords == 0) return BiffConviction.DEFINITELY_NOT;
        return BiffConviction.DOUBT_IT;
    }

    public boolean wordFollowedByWord(String word1, String word2){
        for (String _word1 : word1.split("/")){
            for (String _word2: word2.split("/")){
                if (!wordMap.containsKey(_word1)) continue;
                if (!wordMap.containsKey(_word2)) continue;
                if (wordArray[wordMap.get(_word1).getPosition()+1].equals(_word2)) return true;
            }
        }
        return false;
    }

    public boolean isQuestion(Map<String,Word> wordMap, String input){
        if (plugin.getBrain().findMatchingQuestionTypes(wordMap, input).size() > 0){
            return true;
        }
        if (input.contains("?")) return true;
        return false;
    }

    public boolean isQuestion(){
        return isQuestion(wordMap, originalSentence);
    }


    public String getWordAfter(String input){
        String[] newWords = originalSentence.split(" ");
        for (int i=0; i<newWords.length; i++){
            String w = newWords[i];
            //Bukkit.broadcastMessage(w);
            if (w.contains(input) && newWords.length != (i+1)) return annoyingPunctuation.matcher(newWords[i+1]).replaceAll("");
        }
        return null;
    }
    public String getWordBefore(String input){
        String[] newWords = originalSentence.split(" ");
        for (int i=0; i<newWords.length; i++){
            String w = newWords[i];
            //Bukkit.broadcastMessage(w);
            if (w.contains(input) && newWords.length != i+1) return annoyingPunctuation.matcher(newWords[i-1]).replaceAll("");
        }
        return null;
    }

    public BiffTense getLikelyTense(){
        return plugin.getBrain().getTense(wordMap);
    }

    public boolean isDefinitelyNotTense(BiffTense tense){
        BiffTense t = getLikelyTense();
        if (t == BiffTense.UNKNOWN) return false;
        if (t == tense) return false;
        return true;
    }

    public boolean isQuestionType(QuestionType type, boolean allowMultipleQuestions){
        List<QuestionType> list = plugin.getBrain().findMatchingQuestionTypes(wordMap, originalSentence);
        if (!allowMultipleQuestions && list.size()> 1) return false;
        if (list.contains(type)) return true;
        return false;
    }


    public List<String> getWordsAfter(String word){
        ArrayList<String> words = new ArrayList<>();
        for (String _word : word.split("/")){
            if (wordMap.containsKey(_word)){
                Word w = wordMap.get(_word);
                int position = w.getPosition();
                if (position == wordArray.length-1) return null;
                for (int i = position+1; i < wordArray.length; i++){ //get the rest of the words
                    //Bukkit.broadcastMessage(wordArray.length+"");
                    //Bukkit.broadcastMessage("debug"+i);
                    if (!plugin.getBrain().isPartOfName(wordArray[i])) break;
                    words.add(wordArray[i]);
                }
                return words;
            }
        }
        return null;
    }

    public List<String> removeNonNamedWords(ArrayList<String> list){
        List<String> words = new ArrayList<>();
        for (String word : list){
            if (!plugin.getBrain().isPartOfName(word)) continue;
            String newWord = word;
            if (word.contains("'")){
                newWord = newWord.split("'")[0];
            }
            words.add(newWord);
        }
        return words;
    }
    public NamedEntity getNamedEntity(String words){
        if (words == null) return null;
        return plugin.getNamedEntities().get(words);
    }
    public NamedEntity getNamedEntity(List<String> words){
        if (words == null) return null;
        return getNamedEntity(Joiner.on(" ").join(words));
    }
    public boolean contains(String words){
        for (String word : words.split("/")){
            if (wordMap.containsKey(word)) return true;
        }
        return false;
    }

    public boolean containsTag(String tag){
        List<String> m = plugin.getMessages().get(tag);
        if (m == null) return false;
        for (String word : m){
            if (wordMap.containsKey(word)) return true;
        }
        return false;
    }

    public ArrayList<String> getWordsBetween(String word1, String word2){
        boolean tracking = false;
        ArrayList<String> words = new ArrayList<>();
        for (String word : wordArray){
            if (tracking){
                for (String w : word2.split("/")){
                    if (word.equalsIgnoreCase(w)) tracking = false;
                }

                if (tracking) words.add(word);
            }
            else{
                for (String w : word1.split("/")){
                    if (word.equalsIgnoreCase(w)) tracking = true;
                }
;
            }
        }
        return words;
    }
}
