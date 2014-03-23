package com.slize.edmpircbot;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;

public class Config {
    private Document doc;

    public Config(String fileName) throws Exception {
        try {
            ClassLoader loader = getClass().getClassLoader();

            File file = new File(loader.getResource(fileName).toURI());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            this.doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();
        }
        catch(Exception err) {
            System.err.println("Critical Error: Could not load config file.");
            err.printStackTrace();
        }
    }

    public String[] loadBotSettings() {
        String[] bot = new String[3];

        NodeList nList = doc.getElementsByTagName("Bot");
        Node node = nList.item(0);

        Element element = (Element) node;

        bot[0] = element.getElementsByTagName("Nick").item(0).getTextContent();
        bot[1] = element.getElementsByTagName("Channel").item(0).getTextContent();
        bot[2] = element.getElementsByTagName("Subreddit").item(0).getTextContent();

        return bot;
    }

    public String[] loadNickServ() throws IOException {
        String[] nickserv = new String[2];

        NodeList nList = doc.getElementsByTagName("NickServ");
        Node node = nList.item(0);

        Element element = (Element) node;

        nickserv[0] = element.getElementsByTagName("Username").item(0).getTextContent();
        nickserv[1] = element.getElementsByTagName("Password").item(0).getTextContent();

        return nickserv;
    }

    public String[] loadReddit() {
        String[] reddit = new String[2];

        NodeList nList = doc.getElementsByTagName("Reddit");
        Node node = nList.item(0);

        Element element = (Element) node;

        reddit[0] = element.getElementsByTagName("Username").item(0).getTextContent();
        reddit[1] = element.getElementsByTagName("Password").item(0).getTextContent();

        return reddit;
    }
}
























