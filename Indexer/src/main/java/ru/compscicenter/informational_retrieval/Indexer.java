package ru.compscicenter.informational_retrieval;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.*;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.HashSet;

public class Indexer {

    public Indexer() throws IOException {
        index = new SetIndex();
        try {
            morphology = new RussianLuceneMorphology();
        }
        catch (IOException e) {
            System.out.println("Something wrong with russianmorphology :'(");
            throw e;
        }
    }

    public void indexDirectory(File dir) {
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("invalid directory name");
        }
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }
        int count = 0;
        for (File file : files) {
            if (file.isFile()) {
                Long startTime = System.currentTimeMillis();
                try {
                    indexDocument(file);
                }
                catch (IOException e) {
//                    System.out.println(e.getMessage());
                    System.out.println("file " + file.getName() + " was skipped because of I/O Exception");
                }
                Long estimatedTime = System.currentTimeMillis() - startTime;
                System.out.println("file №" + ++count + " " + file.getName() + " was indexed in " + estimatedTime + " ms");
            }
        }
    }

    public void indexDocument(File document) throws IOException {
        HashSet<String> vocabulary = readFromDoc(document);
        index.addDocument(document.getName(), vocabulary);
    }

    private HashSet<String> readFromDoc(File document) throws IOException {
        HashSet<String> result;
        try (
            RandomAccessFile docRaf = new RandomAccessFile(document, "r");
            FileChannel fileChannel = docRaf.getChannel()
        ) {
            MappedByteBuffer buffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, fileChannel.size());
            Charset charset = Charset.forName("UTF-8");
            CharsetDecoder decoder = charset.newDecoder();
            CharBuffer charBuffer = decoder.decode(buffer);

//            Long startTime = System.currentTimeMillis();
            result = readFromBuff(charBuffer);
//            System.out.println("curr: " + (System.currentTimeMillis() - startTime));
        }
        return result;
    }

    private HashSet<String> readFromBuff(CharBuffer buffer) {
        HashSet<String> result = new HashSet<>();
        char currChar = buffer.get();
        while (buffer.hasRemaining()) {
            StringBuilder word = new StringBuilder();
            while (buffer.hasRemaining() && !isValid(currChar)) {
                currChar = buffer.get();
            }
            while (buffer.hasRemaining() && isValid(currChar)) {
                word.append(Character.toLowerCase(currChar));
                currChar = buffer.get();
            }
            result.addAll(analyze(word.toString()));
        }
        return result;
    }

    private HashSet<String> analyze(String word) {
        HashSet<String> result = new HashSet<>();
        word = trim(word);
        if (word.length() == 0) return new HashSet<>();
        if (morphology.checkString(word)) {
            result.addAll(morphology.getNormalForms(word));
        }
        else {
            result.add(word);
        }
        return result;
    }

    public String trim(String word) {
        int length = word.length();
        int first = 0;
        while ((first < length) && (word.charAt(first) == '-')) {
            first++;
        }
        while ((first < length) && (word.charAt(length - 1) == ' ')) {
            --length;
        }
        return word.substring(first, length);
    }

    private boolean isValid(char c) {
        return (
                ((c >= 'а') && (c <= 'я'))
                        || ((c >= 'А') && (c <= 'Я'))
                        || (c == 'ё') || (c == 'Ё') || (c == '-')
                        || ((c >= 'a') && (c <= 'z'))
                        || ((c >= 'A') && (c <= 'Z'))
        );
    }

    public void printIndex(File output) throws IOException {
        try (
            FileOutputStream outputStream = new FileOutputStream(output);
            BufferedOutputStream out = new BufferedOutputStream(outputStream);
            ObjectOutputStream objectOut = new ObjectOutputStream(out)
        ) {
            objectOut.writeObject(getIndex());
//            out.write(i.getIndexAsString());
        }
    }

    public SetIndex getIndex() {
        return index;
    }

    private LuceneMorphology morphology;
    private SetIndex index;
}
