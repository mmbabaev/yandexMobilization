package artistsinfo.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class Helper {
    /**
     * Return string with correct russian noun
     * after number
     *
     * @param number
     * @param normative Nominative case
     * @param genitive  Genetive case
     * @param plural    Plural case
     * @return numb + correct string
     */
    public static String units(int number, String normative, String genitive, String plural) {
        int n = number % 100;
        String ending;

        if (n >= 11 && n <= 19) {
            ending = plural;
        } else {
            switch (n % 10) {
                case 1:
                    ending = normative;
                    break;
                case 2:
                case 3:
                case 4:
                    ending = genitive;
                    break;
                default:
                    ending = plural;
            }
        }
        return number + " " + ending;
    }

    /**
     * @param url target file url
     * @return text content of url file
     * @throws Exception
     */
    public static String getTextFromUrl(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()
                )
        );

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);

        in.close();

        return response.toString();
    }

    /**
     * @param file target file
     * @return text content of file
     * @throws Exception
     */
    public static String readTextFromFile(File file) throws Exception {
        StringBuilder text = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(file));
        String line;

        while ((line = br.readLine()) != null) {
            text.append(line);
            text.append('\n');
        }
        br.close();
        return text.toString();
    }
}
