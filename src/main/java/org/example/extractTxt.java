package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** This class function as to extract list of address from .txt file
 * turn it into the list of address, then combine all the address
 * into a line of string to be readable by ViewModel
 */

public class extractTxt {
    public String strRecipients;

    public extractTxt(){
        this.strRecipients = "";
    }
    public extractTxt(String filename) throws Exception{
        this.strRecipients = "";
        try {
            List list = readList(filename);
            list.forEach((element) -> {
                if (element != "null") {addToList((String) element);}
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List readList(String fileName) throws IOException {
        List<String> result = new ArrayList<>();
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
        return result;
    }

    private void addToList(String element) {
        this.strRecipients += element;
        this.strRecipients += ",";
    }
}
