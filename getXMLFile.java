package com.componentwise.eval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * get a XML file and ensure it is well formed
 */
public class getXMLFile {
    /**
     * get the file and display it as well as check if it's well formed
     * @throws FileNotFoundException if the file is somehow missing
     * @throws IOException if file isn't selected or something happens to selection
     */
    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        /*
         * allow user to choose only xml files from file browse
         * also catch any exceptions or cancellations
         */
        JFileChooser fileChooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "XML", "xml");
        fileChooser.setFileFilter(filter);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            System.out.println("you chose: " + fileChooser.getSelectedFile().getName());
            try{
                String text = stringConvert(fileChooser).toString().trim();
                System.out.println("how does the XML look:\n" + text);
                System.out.println("is XML well formed: " + parseXmlStack(fileChooser));
            }  catch (FileNotFoundException ex) {
                System.out.println("no file found");
            } catch (IOException ex) {
                System.out.println("selection failed");
            }
        } else {
            System.out.println("no file selected");
            throw new IOException();
        }
    }
    /**
     * @param file a JFileChooser object from user's selection
     * @return boolean that shows whether XML is valid or not
     * method reads through file line by line and removes extraneous bits like comments
     * and self closing xml tags just in case, then it
     *  */
    private static Boolean parseXmlStack(JFileChooser file) throws IOException {
        boolean isValid = true;
        BufferedReader in;
        in = new BufferedReader(new FileReader(file.getSelectedFile()));
        String line;
        StringBuilder stringXml = new StringBuilder();
        while ((line = in.readLine()) != null) {
            line = line.replaceAll("\\s","");
            if (!line.isEmpty() && !line.matches("<!--.*?-->") && !line.matches("<.*?/>") && !line.matches("<\\?.*?\\?>")) {
                Pattern regex = Pattern.compile(">.*?<");
                Matcher m = regex.matcher(line);
                StringBuilder str = new StringBuilder(line);
                while (m.find()) {
                    line = str.insert(m.start()+1, ",").toString();
                }
                Pattern regex2 = Pattern.compile(".*?</");
                Matcher m2 = regex2.matcher(line);
                StringBuilder str2 = new StringBuilder(line);
                while (m2.find()) {
                    line = str2.insert(m2.end()-2, ",").toString();
                }
                Pattern regex3 = Pattern.compile("<.*>");
                Matcher m3 = regex3.matcher(line);
                StringBuilder str3 = new StringBuilder(line);
                while (m3.find()) {
                    line = str3.insert(m3.end(), ",").toString();
                }
                stringXml.append(line);
            }
        }
        stringXml.delete(stringXml.length()-1, stringXml.length());
        String newString = stringXml.toString().replaceAll("\\,\\,", ",");
        List<String> listString = Arrays.asList(newString.split(","));
        Stack<xmlNoTag> xmlStack = new Stack<xmlNoTag>();
        for (String item : listString) {
            if (item.matches("<\\/.*?>")) {
                String text = stripTags(item);
                xmlNoTag xmlMapped = new xmlNoTag(text, false);
                xmlStack.push(xmlMapped);
            } else if (item.matches("<.*?>")){
                String text = stripTags(item);
                xmlNoTag xmlMapped = new xmlNoTag(text, true);
                xmlStack.push(xmlMapped);
            }
        }
        Stack<String> anotherStack = new Stack<String>();
        for(Object item : xmlStack) {
            String isOpenTag = ((xmlNoTag)item).getItems("isOpenTag");
            String text = ((xmlNoTag)item).getItems("text");
            // check if opening tag or closing tag
            if (isOpenTag.equals("true")) {
                anotherStack.push(text);
            }
            /* was using regex to check and replace < but using isOpenTag should be enough
            *  so decided to put together a stripTags method
            *  if false at any point return false */
            if (isOpenTag.equals("false") && !anotherStack.empty()) {
                String peekedText = anotherStack.peek().toString();
                //peekedText = peekedText.replaceAll("<","</");
                if(text.equals(peekedText)) {
                    isValid = true;
                    anotherStack.pop();
                } else {
                    return false;
                }
            }
        }
        return isValid;
    }

    /**
     * @return text
     * helps to display the xml file as a String
     */
    private static StringBuilder stringConvert (JFileChooser fileChooser) throws IOException {
        BufferedReader in;
        in = new BufferedReader(new FileReader(fileChooser.getSelectedFile()));
        String line;
        StringBuilder text = new StringBuilder();
        while ((line = in.readLine()) != null) {
            text.append(line + "\n");
        }
        return text;
    }

    /**
     * @return text
     * remove brackets and slashes since boolean isOpenTag can handle if opening/closing tag
    */
    private static String stripTags (String xmlString) throws IOException {
        StringBuilder text = new StringBuilder();
        for (char c : xmlString.toCharArray()) {
            String str = String.valueOf(c);
            if(!str.equals("<") && !str.equals(">") && !str.equals("/")) {
                text.append(c);
            }
        }
        return text.toString();
    }
}