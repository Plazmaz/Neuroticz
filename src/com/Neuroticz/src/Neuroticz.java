package com.Neuroticz.src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.dylan.NNL.HiddenNode;
import me.dylan.NNL.Input;
import me.dylan.NNL.NNetwork;
import me.dylan.NNL.Node;
import me.dylan.NNL.Output;
import me.dylan.NNL.Synapse;
import me.dylan.NNL.Test.TestUtil;
import me.dylan.NNL.Utils.NetworkUtil;
import me.dylan.NNL.Utils.ThreadUtil;
import me.dylan.NNL.Visualizer.Display;

public class Neuroticz {
	Timer timer = new Timer();
	public static double ZOOM_FACTOR = 1.0;
	public static final int NETWORKS_ADDITION_PER_GENERATION = 1;
	public static final int NETWORK_DISPLAY_OFFSET_MULTIPLIER = 300;
	public static final double HORIZONTAL_NODE_SPACING = 4;
	public static final double VERTICAL_NODE_SPACING = 2;
	public static int TOP_SHIFT_INIT = 100;
	public static Point NODE_SHIFT = new Point(0, TOP_SHIFT_INIT);
	private static Point NODE_SHIFT_INIT = NODE_SHIFT;
	private boolean panning = false;
	MainLoop mainLoop;
	String desiredOutput = "";

	/**
	 * The main class for Neuroticz. This function does the initial code to start
	 * the network, begin processing the input data, creates the mainloop
	 * functionality, and then starts the mainloop.
	 */
	// TODO: Lots of code here, maybe want to break it up? Not sure if we can..
	// review with Dylan
	public Neuroticz() {

		Display.showDisplay("Neuroticz Visualizer", new Dimension(1400, 700),
				Color.BLACK);
		Display.setMouseLocation(new Point(Display.getWindowLocOnScreen().x
				+ Display.getWidth() / 2, Display.getWindowLocOnScreen().y
				+ Display.getHeight() / 2));
		Display.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent evt) {
				NODE_SHIFT.x = Display.getWidth() / 2 - evt.getX();
				NODE_SHIFT.y = TOP_SHIFT_INIT + Display.getHeight() / 2
						- evt.getY();
			}

			@Override
			public void mouseDragged(MouseEvent evt) {
			}
		});

		Display.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {
				if (!panning) {
					if (e.getButton() == MouseEvent.BUTTON1) {
						ZOOM_FACTOR += 0.5;
					} else if (e.getButton() == MouseEvent.BUTTON3) {
						ZOOM_FACTOR -= 0.5;
					}
				}
				panning = false;
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});

		List<File> indata = FileUtil.compileLearningData(new File("recipes"));
		List<String> tmpLines = new ArrayList<String>();
		for (File trainingData : indata) {
			BufferedReader fileIn = null;
			try {
				fileIn = new BufferedReader(new FileReader(trainingData));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			String inLine = "";
			try {
				while ((inLine = fileIn.readLine()) != null) {
					if (!inLine.isEmpty() && inLine.trim().length() > 0) {
						desiredOutput += "\n" + inLine;
						tmpLines.add(inLine);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		mainLoop = new MainLoop();
		for (int i = 0; i < NETWORKS_ADDITION_PER_GENERATION; i++) {

			NNetwork initialNet = NetworkUtil.initializeNetwork(0, 1, 1,
					desiredOutput, tmpLines);
			// initialNet.addOutputNodeToNetwork(new Output());
			// Input inputNode = new Input(desiredOutput.split("\n"));
			// initialNet.addInputNodeToNetwork(inputNode);
			if (TestUtil.AnyNodesExist(initialNet)) {
				TestUtil.WhatNodesExist(initialNet);
			}
			System.out.println("Any hidden nodes have values assigned? "
					+ TestUtil.AHiddenNodeHasValue(initialNet));
			System.out.println("Are all synapses properly assigned? "
					+ TestUtil.IsSynapseCountProper(initialNet));
			// initialNet.connectAll();
			mainLoop.allNetworks.add(initialNet);
		}
		// net.randomizeConnections();
		ThreadUtil.spinThreadForPool("mainLoop", mainLoop);
		ThreadUtil.spinThreadForPool("drawThread", new Runnable() {
			@Override
			public void run() {
				while (!Thread.interrupted()) {
					ArrayList<NNetwork> networkClones = (ArrayList<NNetwork>) mainLoop.allNetworks
							.clone();
					int netcount = 0;
					for (NNetwork net : networkClones) {

						netcount++;
						Display.fillRect(0, 0, Display.getWidth(),
								Display.getHeight());
						Display.setOffset(new Point(netcount
								* Neuroticz.NETWORK_DISPLAY_OFFSET_MULTIPLIER,
								0));
						draw(net);
						Display.setOffset(new Point(-netcount
								* Neuroticz.NETWORK_DISPLAY_OFFSET_MULTIPLIER,
								0));
					}
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		});
	}

	public static void main(String args[]) {
		new Neuroticz();
	}

	//TODO: More drawing stuff, do we want to document it?
	public void draw(NNetwork network) {
		drawSynapses(network);
		int row = 0;
		ArrayList<Input> netInputNodes = (ArrayList<Input>) network
				.getInputNodesInNetwork().clone();
		for (Input in : netInputNodes) {
			double x = NODE_SHIFT.x
					+ 130
					+ (row * Node.NODE_DRAW_SIZE * HORIZONTAL_NODE_SPACING * ZOOM_FACTOR);
			in.paint((int) x, (int) ((Node.NODE_DRAW_SIZE * 2)
					* VERTICAL_NODE_SPACING * ZOOM_FACTOR)
					+ NODE_SHIFT.y);
			row++;
		}
		int y = 1;
		row = 0;
		ArrayList<HiddenNode> netHiddenNodes = (ArrayList<HiddenNode>) network
				.getHiddenNodesInNetwork().clone();
		for (HiddenNode n : netHiddenNodes) {
			y += 1;
			double x = NODE_SHIFT.x
					+ 180
					+ (row * Node.NODE_DRAW_SIZE * HORIZONTAL_NODE_SPACING * ZOOM_FACTOR);
			n.paint((int) x, (int) (y * (Node.NODE_DRAW_SIZE * 2)
					* VERTICAL_NODE_SPACING * ZOOM_FACTOR)
					+ NODE_SHIFT.y);
			if (network.getHiddenNodesInNetwork().indexOf(n) % 10 == 0) {
				row++;
				y = 1;
			}

		}
		row = 0;
		for (Output out : network.getOutputNodesInNetwork()) {
			double x = NODE_SHIFT.x
					+ 180
					+ 200
					+ (row * Node.NODE_DRAW_SIZE * HORIZONTAL_NODE_SPACING * ZOOM_FACTOR);

			out.paint((int) x, (int) (Node.NODE_DRAW_SIZE * 20
					* VERTICAL_NODE_SPACING * ZOOM_FACTOR)
					+ NODE_SHIFT.y);
			row++;
		}
	}
	//TODO: More drawing stuff, do we want to document it?
	// Left the commented code alone here because not sure if its used or not currently
	public void drawSynapses(NNetwork net) {
		// net.removeUnusedSynapses();
		ArrayList<Synapse> synapsesClone = (ArrayList<Synapse>) net
				.getNetworkSynapses().clone();
		for (Synapse connection : synapsesClone) {
			if (connection == null
					|| connection.getConnectionDestination() == null
					|| connection.getConnectionOrigin() == null)
				continue;
			Point originDrawingPoint = connection.getConnectionOrigin().graphicsRepresentationObject
					.getPaintCoords();
			Point destinationDrawingPoint = connection
					.getConnectionDestination().graphicsRepresentationObject
					.getPaintCoords();
			Color displayColor = Display.getDisplayBackgroundColor();
			Display.setDisplayBackgroundColor(NetworkUtil
					.returnWeightColor(connection.getSynapseWeight()));
			// if(connection.hasPulsedInTick)
			// Display.setDisplayBackgroundColor(Color.MAGENTA);
			// else if(connection.hasPulsedInTick)
			// Display.setDisplayBackgroundColor(Color.BLUE);
			Display.drawLine(originDrawingPoint.x, originDrawingPoint.y,
					destinationDrawingPoint.x, destinationDrawingPoint.y);
			Display.setDisplayBackgroundColor(displayColor);

		}
	}
}