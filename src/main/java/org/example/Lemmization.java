package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Lemmization {

    public static void main(String[] args) throws IOException {
        File tokensAndLemmasFile = new File("/Users/daniilkarpov/Desktop/Github/lemmization/src/main/resources/lemmasAndTokens.txt");
        if (!tokensAndLemmasFile.exists()) {
            try {
                tokensAndLemmasFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        File lemmasFile = new File("/Users/daniilkarpov/Desktop/Github/lemmization/src/main/resources/lemmas.txt");
        if (!lemmasFile.exists()) {
            try {
                lemmasFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        FileWriter talWriter = null;
        FileWriter lemmasWriter = null;
        try {
            talWriter = new FileWriter(tokensAndLemmasFile);
            lemmasWriter = new FileWriter(lemmasFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Map<String, HashSet<String>> mapSet = new HashMap<>();
        try {
            for (int i = 0; i < 100; i++) {
                Optional<String> wordsFromFile = Files.lines(Paths.get("/Users/daniilkarpov/Desktop/Github/crawler/src/main/resources/crawler4j/" + i + ".txt")).findFirst();
                if (wordsFromFile.isPresent()) {
                    Analyzer analyzer;
                    String[] wordsFromFileAfterSplit = wordsFromFile.get().split(" ");
                    if (wordsFromFileAfterSplit[0].toLowerCase().charAt(0) >= 1072 || wordsFromFileAfterSplit[0].toLowerCase().charAt(0) <= 1103
                            || wordsFromFileAfterSplit[0].toLowerCase().charAt(0) == 'ё') {
                        analyzer = new RussianAnalyzer();
                    } else {
                        analyzer = new EnglishAnalyzer();
                    }
                    for (String s : wordsFromFileAfterSplit) {
                        String word = s.toLowerCase();
                        TokenStream stream = analyzer.tokenStream("field", word);
                        stream.reset();
                        while (stream.incrementToken()) {
                            String lemma = stream.getAttribute(CharTermAttribute.class).toString();
                            if (mapSet.get(lemma) == null) {
                                HashSet<String> set = new HashSet<>();
                                set.add(word);
                                mapSet.put(lemma, set);
                            } else {
                                HashSet<String> set;
                                set = mapSet.get(lemma);
                                set.add(word);
                                mapSet.put(lemma, set);
                            }
                        }
                        stream.end();
                        stream.close();
                    }
                }
            }
            for (String key : mapSet.keySet()) {
                StringBuilder allTokens = new StringBuilder(key + "");
                for (String token : mapSet.get(key)) {
                    allTokens.append(" ").append(token);
                }
                if (talWriter != null && lemmasWriter != null) {
                    talWriter.write(allTokens + "\n");
                    talWriter.flush();
                    lemmasWriter.write(key + "\n");
                    lemmasWriter.flush();
                }
            }
            talWriter.close();
            lemmasWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}