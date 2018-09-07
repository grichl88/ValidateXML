package com.sample.validateXML;

/**
 * class xmlNoTag has boolean and string
 * using boolen to decide if tag is an opening or closing tag
 * */
class xmlNoTag {
    private Boolean isOpenTag;
    private String text;

    /**
     *  @param text takes a String
     *  @param isOpenTag takes a Boolean
     */
    xmlNoTag(String text, Boolean isOpenTag) {
        this.text = text;
        this.isOpenTag = isOpenTag;
    }

    public String toString() {
        return "tag is " + text + ", tag type is " + isOpenTag;
    }

    /**
     *  @param item takes a String and checks if it's isOpenTag or text then returns String
     *  @return String will either return the tag name or true/false depending on whether tag was opening/closing tag
     */
    public String getItems(String item) {
        String returnString = null;
        if (item.equals("text")) {
            returnString = text;
        } else if (item.equals("isOpenTag")) {
            returnString = isOpenTag.toString();
        }
        return returnString;
    }
}