package ru.compscicenter.informational_retrieval;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

//import org.mozilla.universalchardet.Constants.CharsetListener.UniversalDetector

public class SetIndex implements Serializable {

    public SetIndex() throws IOException{
        index = new HashMap<>();
        documents = new ArrayList<>();
//        morphology = new RussianLuceneMorphology();
    }

    public void addDocument (String name, Set<String> vocabulary) {
        int documentId = documents.size();
        documents.add(name);
        for (String word : vocabulary) {
            HashSet<Integer> tail = index.get(word);
            if (tail == null) {
                tail = new HashSet<>();
                tail.add(documentId);
                index.put(word, tail);
            }
            else {
                tail.add(documentId);
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        for (String word : index.keySet()) {
            (result.append(word)).append(":");
            for (int documentId : index.get(word)) {
                result.append(" ");
                result.append(documents.get(documentId));
            }
            result.append("\n");
        }
        return result.toString();
    }

    public HashSet<String> getDocumentsByWord(String word) {
        HashSet<Integer> docIds = index.get(word);
        HashSet<String> result = new HashSet<>();
        if (docIds == null) {
            return result;
        }
        for (int id : docIds) {
            result.add(documents.get(id));
        }
        return result;
    }

    private HashMap<String, HashSet<Integer>> index;
    private ArrayList<String> documents;
}
