package com.company;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Tokenizer {

    public static boolean isWhite(char character){
        return     (character >= 48 && character <= 57)//English numbers
                || (character >= 64 &&   character <= 90)//English Capital letters
                || (character >= 97 &&   character <= 122);//English lowerCase letters
    }

    public static List<String> tokenize(String string){
        StringBuilder temp=new StringBuilder();
        List<String> distinctTokens =new ArrayList<>();
        for (int i = 0; i <string.length() ; i++) {
            char tempChar=string.charAt(i);
            if (isWhite(tempChar))
                temp.append(tempChar);
            else if (temp.length()>0){
                distinctTokens.add(temp.toString());
                temp=new StringBuilder();
            }
        }
        if (temp.length()>0)
            distinctTokens.add(temp.toString());
        return distinctTokens;
    }
}
