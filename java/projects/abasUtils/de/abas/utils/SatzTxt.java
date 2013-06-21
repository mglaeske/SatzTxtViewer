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


import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class SatzTxt {
    private final String satzTxtFileName;
    private final List<SatzTxtGroup> satzTxtGroups = new ArrayList<SatzTxtGroup>();

    public SatzTxt(final String fileName) {
        satzTxtFileName = fileName;
    }
    
    public List<SatzTxtGroup> getGroups() {
        return satzTxtGroups;
    }

    public SatzTxtGroup getGroup(final int number) {
        return satzTxtGroups.get(number);
    }

    public void read() {
        File satzTxtFile = new File(satzTxtFileName);
        BufferedReader reader;
        try {
            SatzTxtGroup satzgruppe = null;
            reader = new BufferedReader(new FileReader(satzTxtFile));

            String data = reader.readLine();
            while (data != null) {
                if (data.startsWith("dxxx")) {
                    // Dateianfang, erste Gruppe
                    satzgruppe = new SatzTxtGroup(data);
                }
                else if (data.startsWith("d-xx")) {
                    satzTxtGroups.add(satzgruppe);
                    // Dateianfang, weitere Gruppe
                    satzgruppe = new SatzTxtGroup(data);
                }
                else {
                    satzgruppe.addLine(data);
                }
                
                data = reader.readLine();
            }

            // Letzte Gruppe noch hinzufügen
            satzTxtGroups.add(satzgruppe);
            reader.close();
            System.out.println("anzahl gruppen=" + satzTxtGroups.size());
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
