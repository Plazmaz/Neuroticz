package me.dylan.WebSum;

import java.util.HashMap;

public class WebSummary {
	public static void main(String args[]) {
		//FileUtil.parseFile("Recipe.txt");
		FileUtil util = new FileUtil();
		HashMap<String, Integer> instances = util.learnWordImportance("recipes");
		System.out.println("FileCount: " + util.fileCount);
		for(String s : instances.keySet()) {
			int wordAmt = instances.get(s);
			float wordWeight = (wordAmt/(float)util.fileCount)/100;
			System.out.println(s + " WordOccurences:" + wordAmt +
					" WordWeight:" + wordWeight);
			//System.out.println(s + " : " + (float)(instances.get(s)/util.fileCount));
		}
		/*
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(
					"C:\\Users\\Hunter\\Desktop\\Server\\server.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			String text = in.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}*/

	}
}