package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Test {
    private static final Integer ONE = 1;
    public static void main(String[] args) throws Exception {
        Map<String, Integer> map = new HashMap<String, Integer>();
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("D:\\145-310115-1548062629-19343-WA_SOURCE_FJ_1001-0.bcp")),
                "UTF-8"));
        String lineTxt = null;
        while ((lineTxt = br.readLine()) != null) {
            String[] names = lineTxt.split("\t",-1);
            for (String name : names) {
                if (map.keySet().contains(name)) {
                    map.put(name, (map.get(name) + ONE));
                } else {
                    map.put(name, ONE);
                }
        }
        }
        System.err.println(map.toString());
        br.close();
    }
}
