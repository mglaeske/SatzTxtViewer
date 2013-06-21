/*-----------------------------------------------------------------------------
 * Modul Name       : SatzTxt.java
 * Verwendung       : Drucken
 * Autor            : mg
 * Verantwortlich   : mg
 * Kontrolle        : 
 * Beratungspflicht : nein
 * Copyright        : (c)1990-2013 ABAS Software AG
 *
 *---------------------------------------------------------------------------*/

package de.abas.print;


import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class SatzTxtGroup {
    private final int groupNumber;
    private final int groupDatabase;
    private final String groupName;
    private final List<String> lines = new ArrayList<String>();
    private boolean isSeparator = false;
    
    public SatzTxtGroup(final String satzTxtLine) {
        String newdata = satzTxtLine.replaceAll("  ", " ").replaceAll("  ", " ").replaceAll("  ", " ");
        // dxxx IF_drucker Drucker 91 1 IF@ Infrastruktur
        String[] values = newdata.split(" ");
        groupDatabase = Integer.parseInt(values[3]);
        groupNumber = Integer.parseInt(values[4]);
        groupName = values[6];
    }
    
    public void addLine(final String data) {
        if (valueIsValid(data)) {
            if (isSeparatorLine(data)) {
                lines.add("");
            }
            else {
                lines.add(data);
            }
        }
    }
    
    public int getGroupNumber() {
        return groupNumber;
    }

    public int getGroupDatabase() {
        return groupDatabase;
    }

    public String getGroupName() {
        return groupName;
    }
    
    public String getData() {
        StringBuilder data = new StringBuilder();
        for (String value : lines) {
            if (value.isEmpty()) {
                data.append("----------");
            }
            else if (value.equals("t")) {
                data.append("\n");
            }
            else {
                StringTokenizer token = new StringTokenizer(value);
                token.nextToken();
                token.nextToken();
                token.nextToken();
                token.nextToken();
                data.append(token.nextToken());
            }
            data.append("\n");
        }
        return data.toString();
    }
    
    private boolean valueIsValid(final String value) {
        if (value.isEmpty()) {
            return isSeparator;
        }
        if (value.startsWith(" ")) {
            if (value.contains("===")) {
                isSeparator = true;
            }
            return false;
        }
        if (value.startsWith("B")) {
            return false;
        }
        if (value.startsWith("b")) {
            return false;
        }
        if (value.startsWith("N")) {
            return false;
        }
        if (value.contains("resstart ")) {
            return false;
        }
        if (value.startsWith("Inherit ")) {
            return false;
        }
        if (value.startsWith("L")) {
            return false;
        }
        if (value.startsWith("rx")) {
            return false;
        }
        if (value.startsWith("kx")) {
            return false;
        }
        return true;
    }
    
    private boolean isSeparatorLine(final String value) {
        if (value.isEmpty()) {
            return isSeparator;
        }
        return false;
    }
}    
