package ru.dev;

import java.io.*;
import java.util.Map;

public class FileManager {
    private final File data = new File("data.txt");
    public FileManager(){
        if(!data.exists()){
            try {
                data.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void save(Map<String,String> d){
        try(BufferedWriter w = new BufferedWriter(new FileWriter(data))) {
            for(Map.Entry<String, String> entry : d.entrySet()){
                w.write(entry.getKey() + "=" + entry.getValue());
                w.newLine();
            }
        } catch (IOException e) {

        }

    }

    public void load(Map<String, String> d){
        d.clear();
        String line;
        try (BufferedReader r = new BufferedReader(new FileReader(data))) {
            while ((line = r.readLine()) != null){
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    d.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}
