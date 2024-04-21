package api;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Scanner;
public class FinalProject {
    //We have 5 class :
    //1.Main Class
    //2.Generate Url Class
    //3.VideoCache Class
    //4.URLReader Class
    //5.JsonParser Class
    public static void main(String[] args) {
        String apiKey = "AIzaSyAOfAh3sEmonFNTb6y-oQiDSptDb7OAqjE"; //youtube api
        Scanner scanner = new Scanner(System.in); //get user input
        Boolean start = true;
        do {
            System.out.print("Enter a keyword to search YouTube videos: ");
            String keyword = scanner.nextLine();
            System.out.print("Enter a number of item to search YouTube videos: ");
            Integer numberResult = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter a keyword to sort YouTube videos: ");
            String orderBy = scanner.nextLine();
            //1 Call GenerateURL class from user input such as keyword,numberResult,apiKey,orderBy
            //with url() function
            GenerateURL generateURL = new GenerateURL(keyword,numberResult,apiKey,orderBy);
            String url = generateURL.url();

            //2 Call videoCache class to check weather that url already cached or not
            //if it in cache it returns jsonData and if not, it goes to fetch jsonData then return as String

            VideoCache videoCache = new VideoCache();
            long startTimeFirst = System.currentTimeMillis();
            String jsonData = videoCache.checkCache(url);

            //3. Convert from json to arraylist
            //If we do not want to create customize Video class we can use the default JsonObject class from Gson Library
            JsonObject jsonObject = new JsonParser<JsonObject>().parseJSON(jsonData, JsonObject.class);
            JsonArray items = jsonObject.getAsJsonArray("items");
            for (JsonElement item : items) {
                JsonObject snippet = item.getAsJsonObject().getAsJsonObject("snippet");
                String title = snippet.get("title").getAsString();
                String channelId = snippet.get("channelId").getAsString();
                String channelTitle = snippet.get("channelTitle").getAsString();
                String publishedAt = snippet.get("publishedAt").getAsString();
                System.out.println("Title: " + title);
                System.out.println("Channel ID: " + channelId);
                System.out.println("Channel Title: " + channelTitle);
                System.out.println("Published At: " + publishedAt);
                System.out.println();
            }
            //Show the fetching duration
            //If the url already existed in cache it will fetch immediately
            long endTimeFirst = System.currentTimeMillis();
            double secondsFirst = (endTimeFirst - startTimeFirst) / 1000.0;
            System.out.println(" Total time taken: " + secondsFirst + " seconds");
            System.out.print("Enter 0 to end : ");
            String end = scanner.nextLine();
            if (end.equals("0")){
                break;
            }
        } while (start);
    }

    public static class GenerateURL {
        String keyword;
        Integer numberResult;
        String api_key;
        String orderBy;

        public GenerateURL(String keyword, Integer numberResult, String api_key, String orderBy) {
            this.keyword = keyword;
            this.numberResult = numberResult;
            this.api_key = api_key;
            this.orderBy = orderBy;
        }

        public String url () {
            String urlString = "https://www.googleapis.com/youtube/v3/search?q=" + keyword +
                    "&part=snippet,id&maxResults=" + numberResult + "&key=" + api_key + "&order=" + orderBy ;
            return urlString;
        }
    }

    public static class VideoCache {
        private static HashMap<String, String> cache = new HashMap<>();

        public String checkCache(String url){
            if(cache.containsKey(url)){
                return cache.get(url);
            }else{
                String jsonData = fetchData(url); // Fetch data from URL
                cache.put(url, jsonData); // Cache the fetched data
                return jsonData;
            }
        }
        public String fetchData(String url){
            String jsonData = Final.URLReader.readURL(url);
            return jsonData;
        }
    }

    public static class URLReader {
        public static String readURL(String urlString) {
            try {
                URL url = new URL(urlString);
                String content = IOUtils.toString(url, "UTF-8");
                return content;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    public  static class JsonParser<T> {
        public T parseJSON(String jsonString , Type type) {
            Gson gson = new Gson();
            return gson.fromJson(jsonString, type );
        }
    }
}
