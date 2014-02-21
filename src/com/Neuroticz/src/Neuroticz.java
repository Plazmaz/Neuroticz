package com.Neuroticz.src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.dylan.NNL.Input;
import me.dylan.NNL.NNLib;
import me.dylan.NNL.NNetwork;
import me.dylan.NNL.Neuron;
import me.dylan.NNL.Node;
import me.dylan.NNL.Output;
import me.dylan.NNL.Synapse;
import me.dylan.NNL.Value;
import me.dylan.NNL.Utils.NetworkUtil;
import me.dylan.NNL.Utils.ThreadUtil;
import me.dylan.NNL.Visualizer.Display;

public class Neuroticz {
	Timer timer = new Timer();
	public static final int NETWORKS_PER_GENERATION = 1;
	public static final int NETWORK_DISPLAY_OFFSET_MULTIPLIER = 10;
	public static final int VERTICAL_NODE_SPACING = 35;
	ArrayList<NNetwork> networks = new ArrayList<NNetwork>();

	public Neuroticz() {

		Display.showDisplay("Neuroticz Visualizer", new Dimension(700, 700),
				Color.BLACK);
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
		for (int i = 0; i < NETWORKS_PER_GENERATION; i++) {

			NNetwork net = NetworkUtil.initializeNetwork(20/*
															 * NNLib.GLOBAL_RANDOM
															 * .nextInt(keySize)
															 */,
					NNLib.GLOBAL_RANDOM.nextInt(keySize), 2);
			for (File key : indata.keySet()) {
				Input in = new Input();
				String outData = "";
				for (String str : indata.get(key).split("\\s")) {
					outData += str;
				}
				in.setInformation(new Value(outData));
				net.addInputNodeToNetwork(in);
			}
			net.randomizeConnections();
			networks.add(net);
		}
		ThreadUtil.spinThreadForPool("mainLoop", new Runnable() {

			@Override
			public void run() {
				while (!Thread.interrupted()) {
					int netcount = 0;
					for (NNetwork net : networks) {
						netcount++;
						Display.setOffset(new Point(netcount
								* NETWORK_DISPLAY_OFFSET_MULTIPLIER, 0));
						runVisualizeLoop(net);
						Display.setOffset(new Point(-netcount
								* NETWORK_DISPLAY_OFFSET_MULTIPLIER, 0));
					}
				}
			}
		});
	}

	public void runVisualizeLoop(NNetwork net) {
		// int errorPercentage =
		// initialNet.getErrorPercentage(data.get(key));
		// timer.start();
		Display.repaint();
		timer.start();
		for (Neuron n : net.getNeuronsInNetwork()) {
			n.doTick();
		}
		timer.end();
		System.out.println("Neuron Ticks: " + timer.getElapsedTimeMilis());
		// timer.end();
		// System.out.println(timer.getElapsedTimeMilis());
		timer.start();
		draw(net);
		timer.end();
		System.out.println("Paint Ticks: " + timer.getElapsedTimeMilis());
		System.out.println("Output: " + net.getNetworkOutput());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		new Neuroticz();
	}

	public void draw(NNetwork network) {
		Display.repaint();
		
		int column = 0;
		for (Input in : network.getInputNodesInNetwork()) {
			in.paint(130 + (column * Node.NODE_DRAW_SIZE * VERTICAL_NODE_SPACING),
					1 * (Node.NODE_DRAW_SIZE * 2));
			column++;
		}
		int y = 1;
		Random rand = NNLib.GLOBAL_RANDOM;
		Color color = new Color(rand.nextInt(255), rand.nextInt(255),
				rand.nextInt(255));
		column = 0;
		for (Neuron n : network.getNeuronsInNetwork()) {
			y++;
			n.setDisplayColor(color);

			n.paint(180 + (column * Node.NODE_DRAW_SIZE * VERTICAL_NODE_SPACING), y
					* (Node.NODE_DRAW_SIZE * 2));
			if (network.getNeuronsInNetwork().indexOf(n) % 5 == 0) {
				column++;
				y = 1;
			}
			// if(column >= 2) {
			// column+=10;
			// }

		}
		column = 0;
		for (Output out : network.getOutputNodesInNetwork()) {
			out.paint(200 + (column * Node.NODE_DRAW_SIZE * VERTICAL_NODE_SPACING),
					Node.NODE_DRAW_SIZE * 20);
			column++;
		}
		drawSynapses(network);
	}

	public void drawSynapses(NNetwork net) {
		for (Synapse connection : net.getNetworkSynapses()) {
			// if (!connection.hasPaintedInTick()) {
			Point originDrawingPoint = connection.getConnectionOrigin().graphicsRepresentationObject
					.getPaintCoords();
			Point destinationDrawingPoint = connection
					.getConnectionDestination().graphicsRepresentationObject
					.getPaintCoords();

			Color displayColor = Display.getDisplayBackgroundColor();
			Display.setDisplayBackgroundColor(Color.YELLOW);
			Display.setStrokeWidth(connection.getSynapseWeight()/5);
			Display.drawLine(originDrawingPoint.x, originDrawingPoint.y,
					destinationDrawingPoint.x, destinationDrawingPoint.y);
			Display.setDisplayBackgroundColor(displayColor);
			connection.setHasPaintedInTick(true);
			// } else {
			//
			// }

		}
	}
}