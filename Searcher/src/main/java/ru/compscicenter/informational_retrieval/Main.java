package ru.compscicenter.informational_retrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("1 argument is required: INDEX_FILE");
            System.exit(1);
        }

        File inputFile = new File(args[0]);
        Searcher searcher = new Searcher(inputFile);

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            String input;
            while ((input = br.readLine().trim()).length() > 0) {
                try {
                    HashSet<String> result = searcher.find(input);
                    if (result.size() == 0) {
                        System.out.println("no documents found");
                    }
                    else {
                        System.out.print("found:");
                        int i = 0;
                        for (String doc : result) {
                            if (i < 3) {
                                System.out.print(" " + doc);
                                ++i;
                            }
                            else {
                                System.out.print(" and " + (result.size() - i) + " more");
                                break;
                            }
                        }
                        System.out.println();
                    }
                }
                catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                }
            }
            br.close();
        } catch (IOException e) {
            // do nothing
        }
    }
}
