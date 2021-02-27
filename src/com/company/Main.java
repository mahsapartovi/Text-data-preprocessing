package com.company;

import opennlp.tools.stemmer.PorterStemmer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Main {

    public static void main(String[] args) throws IOException {
	// write your code here
        processFile("amazon_cells_labelled.txt","A_T.txt","A_P.txt","A_N.txt");
        processFile("imdb_labelled.txt","I_T.txt","I_P.txt","I_N.txt");
        processFile("yelp_labelled.txt","Y_T.txt","Y_P.txt","Y_N.txt");
    }

    public static boolean isStopWord(String token){
        String[] stopWordsArray=new String[]{"i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now"};
        for (String s : stopWordsArray) {
            if (s.equals(token))
                return true;
        }
        return false;
    }


    public static void processFile(String path ,String tokensOutput,String positiveOutput,String negativeOutput) throws FileNotFoundException {


        PorterStemmer porterStemmer=new PorterStemmer();
        //handle Amazon
        Set<String> amazonTokens =new HashSet();
        FileInputStream fileInputStreams =new FileInputStream(path);
        Scanner scanner =new Scanner(fileInputStreams);
        int positiveCommentCount=0;
        int negativeCommentCount=0;
        ArrayList<List> positiveComments=new ArrayList<>();
        ArrayList<List> negativeComments=new ArrayList<>();
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String normalized= line.substring(0,line.length()-1).toLowerCase();
            List<String> tokens= Tokenizer.tokenize(normalized);
            ///checking if the comment is positive
            boolean isPositive=false;
            if (line.endsWith("1"))
                isPositive=true;

            List<String> commentTokens=new ArrayList<>();

            for (String token : tokens) {
                //deleting the stop words from the tokens
                if (isStopWord(token))
                    continue;
                //stemming the tokens
                String stemmed= porterStemmer.stem(token);

                //adding the tokens to commentTokens
                commentTokens.add(stemmed);

                //resetting the stemmer
                porterStemmer.reset();
            }
            for (String commentToken : commentTokens) {
                amazonTokens.add(commentToken);
            }
            if (isPositive) {
                positiveComments.add(commentTokens);
                positiveCommentCount++;
            }
            else negativeComments.add(commentTokens);
            negativeCommentCount++;
        }

        //putting the tokens into an arrayList for easier usage
        ArrayList<String> distinctTokens=new ArrayList();
        int count=0;//the number of distinct tokens in the file
        for (String amazonToken : amazonTokens) {
            count++;
            distinctTokens.add(amazonToken);
        }

        //writing the distinct tokens of the path to tokensOutput
        int tokenCounter=0;
        PrintWriter TokensWriter=new PrintWriter(tokensOutput);
        for (String distinctToken : distinctTokens) {
            TokensWriter.print(tokenCounter+"-");
            tokenCounter++;
            TokensWriter.println(distinctToken);
        }
        TokensWriter.flush();
        TokensWriter.close();


        int[][] Positives=new int[positiveCommentCount][count];
        //Positive Array initialization
        for (int i = 0; i <positiveCommentCount ; i++) {
            for (int j = 0; j <count ; j++) {
                Positives[i][j]=0;
            }
        }

        //creating the bag of words for positive comments
        int rowCount=0;
        for (List processedPositiveComment : positiveComments) {

            for (Object o : processedPositiveComment) {
                Positives[rowCount][distinctTokens.indexOf(o)]++;
            }
            rowCount++;
        }



        //writing the bag of words of the positive comments of path to positiveOutput
        PrintWriter PositivesWriter=new PrintWriter(positiveOutput);

        for (int i = 0; i <positiveCommentCount ; i++) {
            for (int j = 0; j <count ; j++) {
                if (Positives[i][j]>0){
                PositivesWriter.print(j+":"+Positives[i][j]);
                PositivesWriter.print(' ');}
            }
            PositivesWriter.println();
        }
        PositivesWriter.flush();
        PositivesWriter.close();



        int[][] Negatives=new int[negativeCommentCount][count];
        //Negative Array initialization
        for (int i = 0; i <negativeCommentCount ; i++) {
            for (int j = 0; j <count ; j++) {
                Negatives[i][j]=0;
            }
        }


        //creating the bag of words for negative comments

        rowCount=0;
        for (List processedNegativeComment : negativeComments) {

            for (Object o : processedNegativeComment) {
                Negatives[rowCount][distinctTokens.indexOf(o)]++;
            }
            rowCount++;
        }

        //writing the bag of words of the negative comments of path to negativeOutput.txt
        PrintWriter NegativesWriter=new PrintWriter(negativeOutput);



        for (int i = 0; i <negativeCommentCount ; i++) {
            for (int j = 0; j <count ; j++) {
                if (Negatives[i][j]>0) {
                    NegativesWriter.print(j + ":"+Negatives[i][j]);
                    NegativesWriter.print(' ');
                }
            }
            NegativesWriter.println();
        }

        NegativesWriter.flush();
        NegativesWriter.close();



    }
}
