package com.alekseyzhelo.evilislands.mobplugin.utility;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * Created by Aleks on 26-07-2015.
 */
public class CollectorMain {

    public static void main(String[] args) {
        processHtmlHere();
    }

    private static void processHtmlHere() {
        String currentDir = System.getProperty("user.dir");
        PathMatcher matcher =
                FileSystems.getDefault().getPathMatcher("glob:*.htm");

        System.out.println("current directory: " + currentDir);
        Path currentDirPath = FileSystems.getDefault().getPath(currentDir);
        try {
            String declarations = Files.list(currentDirPath)
                    .filter(x -> matcher.matches(x.getFileName()))
                    .map(CollectorMain::getFunctionDeclaration)
                    .collect(Collectors.joining(System.lineSeparator()));
            // still requires manual editing, unfortunately
            Files.write(Paths.get("./result.txt"), declarations.getBytes());
            // could have done it in one pass, opened the files only once, etc - do not care
            String documentation = Files.list(currentDirPath)
                    .filter(x -> matcher.matches(x.getFileName()))
                    .map(CollectorMain::getFunctionDocumentation)
                    .collect(Collectors.joining(System.lineSeparator()));
            Files.write(Paths.get("./documentation.txt"), documentation.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String getFunctionDeclaration(Path file) {
        Document doc;
        try {
            doc = Jsoup.parse(Files.newInputStream(file), "windows-1251", "");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        Element firstBold = doc.select("b").first();
        if (firstBold != null) {
            return firstBold.text();
        } else {
            return "";
        }
    }

    private static String getFunctionDocumentation(Path file) {
        Document doc;
        try {
            doc = Jsoup.parse(Files.newInputStream(file), "windows-1251", "");
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        for (Node child : doc.body().childNodes()) {
            if (child instanceof TextNode) {
                String nodeText = ((TextNode) child).text();
                if(nodeText.isEmpty() || nodeText.equals(" ")){
                    continue;
                }
                return ((TextNode) child).text();
            }
        }
        return "";
    }
}
