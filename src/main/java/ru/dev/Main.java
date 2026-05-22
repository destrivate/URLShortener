package ru.dev;


import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {
    public static HttpServer server;
    public static void main(String[] args) throws IOException {
        server = HttpServer.create(new InetSocketAddress(8080), 0);

        server.createContext("/", exchange -> {
            String hash = exchange.getRequestURI().getPath()
                    .replaceAll("^/|/$", "");
            String originalUrl = ShortenerService.searchShortURL(hash);

            if (originalUrl != null) {
                exchange.getResponseHeaders().set("Location", originalUrl);
                exchange.sendResponseHeaders(302, -1);
                System.out.println("123");
            } else {
                String response = "Link not found";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.close();
        });

        server.createContext("/add/", exchange -> {
            String fullPath = exchange.getRequestURI().getPath();
            String hash = fullPath.replace("/add/", "").replaceAll("^/|/$", "");

            String unicalURL = ShortenerService.generateRandomString(8);


            if (ShortenerService.addURLS(hash,unicalURL)) {
                String response = "Success. Your unique key: " + unicalURL;
                exchange.sendResponseHeaders(202, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            } else {
                String response = "Error. Please try again.";
                exchange.sendResponseHeaders(404, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.close();
        });

        server.start();
    }


}
