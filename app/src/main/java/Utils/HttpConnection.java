package Utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class HttpConnection {

    private static HttpURLConnection connection;
    private static String charset = "UTF-8";
    public static String CreateHTTPRequest(URL url) throws IOException {

        String data = "";
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", "application/json;odata=verbose");
            connection.setRequestProperty("Accept", "application/json;odata=verbose");
            connection.setRequestMethod("GET");
            connection.connect();

          //  System.out.println("my url : " + connection.getResponseCode());
            InputStream inputStream = new BufferedInputStream(connection.getInputStream());
            //System.out.println("my InputStream donesaaa : ");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
          //  System.out.println("my BufferedReader done : ");

            String line;
            while((line = bufferedReader.readLine()) != null) {
                data = data + line;
            }
        }finally {
            connection.disconnect();
        }
      //  System.out.println("Data : " + data);
        return data;
    }

}
