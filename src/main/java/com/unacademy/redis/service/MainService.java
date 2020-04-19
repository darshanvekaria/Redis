package com.unacademy.redis.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.File;
import java.io.*;
import java.util.*;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class MainService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainService.class);

    public static HashMap<String, String[]> MapMain = new HashMap<>();
    public static HashMap<String, TreeMap< Double, TreeSet<String>>> MapScore = new HashMap<>();

    // Process to get data from file and store them in respective hashmaps.
    public void preprocess() throws Exception {

        File file = new File("src\\main\\resources\\static\\Datafile.txt");
        FileInputStream fis = new FileInputStream("src\\main\\resources\\static\\Datafile.txt");

        File FileScore = new File("src\\main\\resources\\static\\ScoreFile.txt");
        FileInputStream fisrc = new FileInputStream("src\\main\\resources\\static\\ScoreFile.txt");

        // List to Store Words of DataFile
        ArrayList<String> al = new ArrayList<String>();

        try {
            Scanner sc = new Scanner(fis);
            if (sc.hasNext()) {
                while (sc.hasNext()) {
                    String s = sc.next();
                    al.add(s);
                }
            }
            sc.close();
        } catch (Exception ex) {
            LOGGER.error("Exception Occured : {}", ex);
        }

        // Store the Input in a HashMap
        // Value of every key is an array
        // First Element of array is the value for key and second element is the expiry
        // time for Key.
        for (int i = 0; i <= al.size() - 3; i = i + 3) {
            String key = al.get(i);
            String arr[] = new String[2];
            arr[0] = al.get(i + 1);
            arr[1] = al.get(i + 2);
            MapMain.put(key, arr);
        }

        // FOR MapScore
        try {

            Scanner scan = new Scanner(fisrc);
            while (scan.hasNextLine()) {
                String line = scan.nextLine();
                String[] StringArray = line.split("\t");
                if (StringArray.length > 2) {
                    String key = StringArray[0];
                    TreeMap< Double, TreeSet<String>> TMap;
                    if (MapScore.containsKey(key)) {
                        TMap = MapScore.get(key);
                    } else {
                        TMap = new TreeMap<>();
                    }

                    TreeSet<String> TSet = new TreeSet<>();
                     double score =  Double.valueOf(StringArray[1]);
                    for (int i = 2; i < StringArray.length; i++) {
                        TSet.add(StringArray[i]);
                    }
                    TMap.put(score, TSet);
                    MapScore.put(key, TMap);

                }
            }
            scan.close();
        } catch (Exception ex) {
            LOGGER.error("Exception Occured : {}", ex);
        }
    }

    public String getval(String key) {
        if (MapMain.containsKey(key)) {
            return (MapMain.get(key)[0]);
        } else
            return ("NIL");

    }

    public String SetValue(String key, String value, String exptime) {
        String[] arr = { value, exptime };
        MapMain.put(key, arr);

        try {
            AfterProcess();
        } catch (Exception ex) {
            LOGGER.error("Exception Occured : {}", ex);
        }

        return ("OK");
    }

    public String SetExpiryTime(String key, String exptime) {
        if (MapMain.containsKey(key)) {
            String arr[] = MapMain.get(key);
            arr[1] = exptime;
            MapMain.put(key, arr);

            try {
                AfterProcess();
            } catch (Exception ex) {
            }

            return ("1");
        } else
            return ("0");
    }

    public String ZADD(String key,  double score, String element) {
        if (MapScore.containsKey(key) && MapScore.get(key) == null) {
            return ("Error Occured!");

        }

        else if (!MapScore.containsKey(key)) {
            TreeSet<String> TSet = new TreeSet<>();
            TSet.add(element);
            TreeMap< Double, TreeSet<String>> TMap = new TreeMap<>();
            TMap.put(score, TSet);
            MapScore.put(key, TMap);

        }

        TreeMap< Double, TreeSet<String>> TMap = MapScore.get(key);
        if (TMap.containsKey(score)) {
            TreeSet<String> TSet = TMap.get(score);
            TSet.add(element);
            TMap.put(score, TSet);
        } else {
            TreeSet<String> TSet = new TreeSet<>();
            TSet.add(element);
            TMap.put(score, TSet);
        }
        MapScore.put(key, TMap);

        try {
            AfterProcess();
        } catch (Exception ex) {
            LOGGER.error("Exception Occured : {}", ex);
        }

        return ( Double.toString(score));
    }

    public String ZRANK(String key, String element) {
        if (!MapScore.containsKey(key)) {
            return ("NIL");
        } else {
            TreeMap< Double, TreeSet<String>> TMap = MapScore.get(key);
            int c = 0;
            for ( double i : TMap.keySet()) {
                TreeSet<String> TSet = TMap.get(i);
                if (TSet.contains(element)) {
                    break;
                }
                c++;
            }

            try {
                AfterProcess();
            } catch (Exception ex) {
                LOGGER.error("Exception Occured : {}", ex);
            }

            return (Integer.toString(c));
        }
    }

    public ArrayList<String> ZRANGE(String key, int start, int stop, int ws) {

        System.out.println(MapScore);

        if (!MapScore.containsKey(key))
            return (new ArrayList<String>());

        TreeMap< Double, TreeSet<String>> TMap = MapScore.get(key);
        if (start > stop && start > 0 && stop > 0)
            return (new ArrayList<String>());
        int length = TMap.size();
        if (stop > length)
            stop = length;

        // Bring the index to valid range
        if (stop < 0 || start < 0) {
            if (stop < 0)
                stop += length;
            if (start < 0)
                start += length;
        }
        int count = 0;
        ArrayList<String> al = new ArrayList<String>();
        for ( double i : TMap.keySet()) {
            if (count >= start || count <= stop) {
                for (String s : TMap.get(i)) {
                    al.add(s);
                    if (ws == 1)
                        al.add(Integer.toString(count));
                }

            }
            count++;
        }

        try {
            AfterProcess();
        } catch (Exception ex) {
            LOGGER.error("Exception Occured : {}", ex);
        }

        return al;

    }

    public void AfterProcess() throws Exception {

        File file = new File("src\\main\\resources\\static\\Datafile.txt");
        File FileScore = new File("src\\main\\resources\\static\\ScoreFile.txt");

        // Overwrite the Data of the Hashmaps in the respective files
        try {
            FileWriter fw = new FileWriter(file, false);
            for (String i : MapMain.keySet()) {

                fw.write(i + "\t");
                fw.write(MapMain.get(i)[0] + "\t");
                fw.write(MapMain.get(i)[1] + "\n");

            }
            fw.close();
        } catch (Exception ex) {
            LOGGER.error("Exception Occured : {}", ex);
        }

        try {
            FileWriter fw2 = new FileWriter(FileScore, false);

            for (String MapKey : MapScore.keySet()) {

                TreeMap< Double, TreeSet<String>> TMap = MapScore.get(MapKey);
                for ( Double a : TMap.keySet()) {
                    fw2.write(MapKey + "\t");
                    fw2.write(a + "\t");
                    TreeSet<String> TSet = TMap.get(a);
                    for (String s : TSet) {
                        fw2.write(s + "\t");
                    }
                    fw2.write("\n");
                }
                // fw2.write("\n");
            }
            fw2.close();
        } catch (Exception ex) {
            LOGGER.error("Exception Occured : {}", ex);
        }

    }

}