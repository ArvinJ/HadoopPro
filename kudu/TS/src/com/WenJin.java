package com;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class WenJin {

	private static final Integer ONE = 1;
	static String names[];

	public static void main(String args[]) throws IOException {
		Map<String, Integer> map = new HashMap<String, Integer>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(new File("D:\\145-310115-1548062629-19343-WA_SOURCE_FJ_1001-0.bcp")), "UTF-8"));
		String lineTxt = null;
		while ((lineTxt = br.readLine()) != null) {
			names = lineTxt.split("\t", -1);
			for (String name : names) {
				if (map.keySet().contains(name)) {
					map.put(name, (map.get(name) + ONE));
				} else {
					map.put(name, ONE);
				}
			}
		}
		br.close();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		sb.append(random.nextInt(20));
		
		for (int i = 0; i < names.length; i++) {
			
			if(null== names[i] || "".equals(names[i])) {
				names[i] = "0";
			}
			System.err.println(names[i]);
			
			
		}
		System.err.println("00000000000000000000000000000000");
		System.err.println(sb.append("|").append(names[2]).append("#").append(names[10]).append("#").append(names[9]));
		String ROWKEY = String.format("%s|%s#%s#%s", sb.toString(), names[2], names[10], names[9]);

		System.err.println("ROWKEY----" + ROWKEY);
	}

}
