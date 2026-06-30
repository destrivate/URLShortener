package ru.dev;


import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Main {
    public static HttpServer server;
    public static ShortenerService ss;
    public static void main(String[] args) throws IOException {
        ss = new ShortenerService();
        server = HttpServer.create(new InetSocketAddress("127.0.0.1",8080), 0);

        server.createContext("/add/", exchange -> {
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String response = "Method Not Allowed. Use POST.";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
                exchange.close();
                return;
            }

            InputStream inputStream = exchange.getRequestBody();
            String hash = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8).trim();

            if (hash.startsWith("{") && hash.endsWith("}")) {
                hash = hash.substring(1, hash.length() - 1).replace("\"", "");
            }

            String unicalURL = "";
            if ((unicalURL = ss.addURLS(hash)) != "") {
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

        server.createContext("/", exchange -> {
            String hash = exchange.getRequestURI().getPath()
                    .replaceAll("^/|/$", "");
            String originalUrl = ss.searchShortURL(hash);

            if (originalUrl != null) {
                exchange.getResponseHeaders().set("Location", originalUrl);
                exchange.sendResponseHeaders(302, -1);
            } else {
                String response = "<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <title>Ссылка не найдена</title>\n" +
                        "    <style>\n" +
                        "        body { font-family: Arial, sans-serif; text-align: center; margin-top: 50px; background-color: #f4f4f9; }\n" +
                        "        .container { background: white; padding: 30px; display: inline-block; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }\n" +
                        "        h1 { color: #ff4d4d; }\n" +
                        "        input[type=\"text\"] { width: 300px; padding: 10px; margin: 10px 0; border: 1px solid #ccc; border-radius: 5px; }\n" +
                        "        button { padding: 10px 20px; background: #28a745; color: white; border: none; border-radius: 5px; cursor: pointer; }\n" +
                        "        button:hover { background: #218838; }\n" +
                        "        .result-box { margin-top: 20px; padding: 15px; background: #e8f5e9; border: 1px solid #c8e6c9; border-radius: 5px; display: none; text-align: left; max-width: 320px; word-break: break-all; }\n" +
                        "        .result-box a { color: #2e7d32; font-weight: bold; text-decoration: none; }\n" +
                        "        .result-box a:hover { text-decoration: underline; }\n" +
                        "        .info-text { font-size: 12px; color: #666; margin-top: 8px; }\n" +
                        "    </style>\n" +
                        "</head>\n" +
                        "<body>\n" +
                        "    <div class=\"container\">\n" +
                        "        <h1>Упс! Ссылка не найдена</h1>\n" +
                        "        <p>Такой короткой ссылки не существует. Но вы можете создать свою прямо сейчас:</p>\n" +
                        "        <form action=\"/add/\" method=\"POST\" onsubmit=\"return sendAsJson(this);\">\n" +
                        "            <input type=\"text\" name=\"longUrl\" placeholder=\"Вставьте длинную ссылку\" required>\n" +
                        "            <button type=\"submit\">Сократить</button>\n" +
                        "        </form>\n" +
                        "        <div id=\"result\" class=\"result-box\">\n" +
                        "            <div>Готово! Ваша ссылка:</div>\n" +
                        "            <div style=\"margin: 5px 0;\"><a id=\"shortLink\" href=\"#\" target=\"_blank\"></a></div>\n" +
                        "            <div class=\"info-text\">Используйте символы после косой черты <b>/</b>, чтобы перенаправлять пользователей на ваш исходный URL.</div>\n" +
                        "        </div>\n" +
                        "    </div>\n" +
                        "    <script>\n" +
                        "        function sendAsJson(form) {\n" +
                        "            const inputVal = form.longUrl.value;\n" +
                        "            const resBox = document.getElementById('result');\n" +
                        "            const linkAnchor = document.getElementById('shortLink');\n" +
                        "            fetch(form.action, {\n" +
                        "                method: 'POST',\n" +
                        "                headers: { 'Content-Type': 'application/json' },\n" +
                        "                body: JSON.stringify({ longUrl: inputVal })\n" +
                        "            })\n" +
                        "            .then(res => res.text())\n" +
                        "            .then(data => {\n" +
                        "                const cleanHash = data.replace(/[^a-zA-Z0-9]/g, '');\n" +
                        "                const fullUrl = window.location.origin + '/' + cleanHash;\n" +
                        "                linkAnchor.href = fullUrl;\n" +
                        "                linkAnchor.textContent = fullUrl;\n" +
                        "                resBox.style.display = 'inline-block';\n" +
                        "            })\n" +
                        "            .catch(err => alert('Ошибка: ' + err));\n" +
                        "            return false;\n" +
                        "        }\n" +
                        "    </script>\n" +
                        "</body>\n" +
                        "</html>";


                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, response.getBytes().length);
                exchange.getResponseBody().write(response.getBytes());
            }
            exchange.close();
        });



        server.start();
    }


}
