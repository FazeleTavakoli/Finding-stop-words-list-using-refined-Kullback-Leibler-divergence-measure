package edu.stanford.nlp.com.company ;
import java.util.*;
import java.lang.*;

public class Spliter {
    private List<String> splittedTextList;
    private String spliteString = "";
    public void sentenceSplitter(String word){
        String splittedText[] = word.split("[\\s\\[\\]\\+=* ,.;'-_،؛! \\)\\( \\« \\»]");
        if(word.length()>0 && splittedText.length == 0)
        {
            splittedText = new String[1];
            splittedText[0] = word;
        }
        for (String item: splittedText) {
            if (!item.isEmpty()) {
                spliteString = spliteString + " " + item;
                spliteString = spliteString.trim();
            }
        }

        this.splittedTextList = new ArrayList<String>();
        for (int i = 0; i < splittedText.length; i++)
            this.splittedTextList.add(splittedText[i]);


    }

    public String getStringSpliter(){
          return spliteString;
    }

    public List <String> getSpliter(){
        return this.splittedTextList;
    }

    public String conllSplitter(String text){
        String conllString = "";
        String splittedText[] = text.split("\\t");
        String singleWord = splittedText[1];
        return singleWord;
    }

    public String[] lineSplitter(String line){
        String splittedText[] = line.split("[;.؛!؟،:+-]");
        return splittedText;

    }




}




