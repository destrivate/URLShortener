package ru.dev;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class ShortenerService {
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static List<String> URL = new CopyOnWriteArrayList<>();
    private static List<String> shortURL = new CopyOnWriteArrayList<>();

    public static boolean addURLS(String u,String i){
        if(!URL.contains(u)){
            URL.add(u);
            shortURL.add(i);
            return true;
        }
        return false;
    }

    public static String searchShortURL(String url){
        for(String s:shortURL){
            if(s.equals(url)){
                return URL.get(shortURL.indexOf(s));
            }
        }
        return null;
    }



    public static String generateRandomString(int length) {
        return ThreadLocalRandom.current()
                .ints(length, 0, CHARACTERS.length())
                .mapToObj(CHARACTERS::charAt)
                .map(Object::toString)
                .collect(Collectors.joining());
    }
}
