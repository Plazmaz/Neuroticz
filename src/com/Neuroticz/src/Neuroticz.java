package com.Neuroticz.src;

import java.io.File;
import java.util.HashMap;

import me.dylan.NNL.Input;
import me.dylan.NNL.NNLib;
import me.dylan.NNL.NNetwork;
import me.dylan.NNL.Value;
import me.dylan.NNL.Utils.NetworkUtil;

public class Neuroticz {
	public static void main(String args[]) {
		// FileUtil.parseFile("Recipe.txt");
		FileUtil util = new FileUtil();
		// HashMap<String, Integer> instances =
		// util.learnWordImportance("recipes");
		// System.out.println("FileCount: " + util.fileCount);
		// for(String s : instances.keySet()) {
		// int wordAmt = instances.get(s);
		// float wordWeight = (wordAmt/(float)util.fileCount)/100;
		// System.out.println(s + " WordOccurences:" + wordAmt +
		// " WordWeight:" + wordWeight);
		// //System.out.println(s + " : " +
		// (float)(instances.get(s)/util.fileCount));
		// }

		HashMap<String, String> data = FileUtil.compileLearningData(new File(
				"recipes"), new File("recipesExpected"));
		int keySize = data.keySet().size();
		NNetwork initialNet = NetworkUtil.initializeNetwork(
				NNLib.rand.nextInt(keySize / 10) + 50, keySize / 10, 1);
		int incount = 0;
		for (Input in : initialNet.getInputs()) {
			in.setOutput(new Value((String) data.keySet().toArray()[incount]));
		}
	}
}