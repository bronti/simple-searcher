package ru.compscicenter.informational_retrieval;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

public class Searcher {

    public Searcher(File indexFile) {
        try (
            FileInputStream inputStream = new FileInputStream(indexFile);
            BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
            ObjectInputStream objectIn = new ObjectInputStream(bufferedIn)
        ) {
            index = (SetIndex)objectIn.readObject();
        }
        catch (Exception e) {
            System.out.println("cannot read index");
            System.out.println(e.toString());
        }
    }

    public SetIndex getIndex() {
        return index;
    }

    public HashSet<String> find(String request) {
        Lexer lexer = new Lexer(request);
        String word = lexer.getWord();
        HashSet<String> result = index.getDocumentsByWord(word);
        while (lexer.hasNext()) {
            Lexer.TokenType tokenType = lexer.getTokenType();
            if (tokenType == Lexer.TokenType.AND) {
                Set<String> excess = index.getDocumentsByWord(word);
                excess.removeAll(result);
                result.removeAll(excess);
            }
            else {
                result.addAll(index.getDocumentsByWord(word));
            }
            word = lexer.getWord();

        }
        return result;
    }


    private SetIndex index;
}
