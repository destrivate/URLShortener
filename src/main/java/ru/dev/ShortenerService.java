package ru.dev;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ShortenerService {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static FileManager fm;
    private Map<String , String> data = new HashMap<>();

    public ShortenerService(){
        fm = new FileManager();
        fm.load(data);
    }

    public String addURLS(String u){
        String shortURL = "";
        while (true){
            shortURL = generateRandomString(8);
            if(!data.containsKey(shortURL)){
                data.put(shortURL,u);
                break;
            }
        }
        fm.save(data);
        return shortURL;
    }

    public String searchShortURL(String url){
        return data.get(url);
    }



    public String generateRandomString(int length) {
        return ThreadLocalRandom.current()
                .ints(length, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
