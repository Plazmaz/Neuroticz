package com.Neuroticz.src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.util.HashMap;

import me.dylan.NNL.Input;
import me.dylan.NNL.NNLib;
import me.dylan.NNL.NNetwork;
import me.dylan.NNL.Neuron;
import me.dylan.NNL.Node;
import me.dylan.NNL.Value;
import me.dylan.NNL.Utils.NetworkUtil;
import me.dylan.NNL.Visualizer.Display;

public class Neuroticz {
	NNetwork initialNet;

	public Neuroticz() {

		// FileUtil.parseFile("Recipe.txt");
		FileUtil util = new FileUtil();
		Display.show("Neuroticz Visualizer", new Dimension(500, 500),
				Color.WHITE);
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

		HashMap<File, String> indata = FileUtil.compileLearningData(new File(
				"recipes"));
		int keySize = indata.keySet().size() - 1;
		initialNet = NetworkUtil.initializeNetwork(
				NNLib.rand.nextInt(keySize / 5) + 50, 0, 1);
		int incount = 0;
		for (File key : indata.keySet()) {
			Input in = new Input();
			String outData = "";
			for (String str : indata.get(key).split("\\s")) {
				outData += str;
			}
			in.setOutput(new Value(outData));
			initialNet.addInput(in);
			incount++;
		}
		initialNet.randomize();
		while (true) {
			// int errorPercentage =
			// initialNet.getErrorPercentage(data.get(key));
			draw();
			for (Neuron n : initialNet.getNeurons()) {
				n.doTick();
			}
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String args[]) {
		new Neuroticz();
	}

	public void draw() {
		Display.repaint();
		for (Node n : initialNet.getNodes()) {
			if (n instanceof Input) {
				Display.setColor(Color.RED);
				Display.fillOval(100, initialNet.getInputs().indexOf(n) * 10,
						5, 5);
			}
		}
	}
}