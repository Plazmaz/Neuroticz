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
    public static int LEFT_NODE_SHIFT = 0;
    public static int TOP_NODE_SHIFT = 0;
    public static int TOP_SHIFT_INIT = 100;
    /**
     * This is a how much to add to the network survival threshold(beginning at
     * the average score of all networks) this is a multiplier as a fraction of
     * the average
     */
    public static final double NETWORK_SURVIVAL_THRESHOLD_ADDITIVE = 1;
    MainLoop mainLoop;
    String desiredOutput = "";

    public Neuroticz() {

	Display.showDisplay("Neuroticz Visualizer", new Dimension(700, 700),
		Color.BLACK);
	Display.setMouseLocation(new Point(Display.getWindowLocOnScreen().x
		+ Display.getWidth() / 2, Display.getWindowLocOnScreen().y
		+ Display.getHeight() / 2));
	Display.addMouseMotionListener(new MouseMotionListener() {

	    @Override
	    public void mouseMoved(MouseEvent evt) {
		LEFT_NODE_SHIFT = Display.getWidth() / 2 - evt.getX();
		TOP_NODE_SHIFT = TOP_SHIFT_INIT + Display.getHeight() / 2
			- evt.getY();
	    }

	    @Override
	    public void mouseDragged(MouseEvent evt) {
		// Point mouseLoc = evt.getPoint();
		// LEFT_NODE_SHIFT = Display.getWidth() / 2 - (int)
		// mouseLoc.getX();
		// TOP_NODE_SHIFT = Display.getWidth() / 2 - (int)
		// mouseLoc.getY();
	    }
	});

	Display.addMouseListener(new MouseListener() {
	    @Override
	    public void mouseReleased(MouseEvent e) {

	    }

	    @Override
	    public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON1) {
		    ZOOM_FACTOR += 0.5;
		} else if (e.getButton() == MouseEvent.BUTTON3) {
		    ZOOM_FACTOR -= 0.5;
		}
		System.out.println("Updated zoom to " + ZOOM_FACTOR);
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
			desiredOutput += inLine;
			tmpLines.add(inLine);
		    }
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }

	}
	mainLoop = new MainLoop(desiredOutput);
	for (int i = 0; i < NETWORKS_ADDITION_PER_GENERATION; i++) {

	    NNetwork initialNet = new NNetwork();
	    initialNet.addOutputNodeToNetwork(new Output());
	    initialNet.addInputNodeToNetwork(new Input());
	    Input inputNode = new Input();
	    for (String s : tmpLines) {
		HiddenNode hiddenNode = NetworkUtil.createHidden(s,
			inputNode.getNodeVariety());
		initialNet.addHiddenNodeToNetwork(hiddenNode);
	    }
	    if (TestUtil.AnyNodesExist(initialNet)) {
		TestUtil.WhatNodesExist(initialNet);
	    }
	    System.out.println("Any hidden nodes have values assigned? "
		    + TestUtil.AHiddenNodeHasValue(initialNet));
	    initialNet.connectAll();
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
			Thread.sleep(20);
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

    public void draw(NNetwork network) {
	int row = 0;
	drawSynapses(network);
	ArrayList<Input> netInputNodes = (ArrayList<Input>) network
		.getInputNodesInNetwork().clone();
	for (Input in : netInputNodes) {
	    double x = LEFT_NODE_SHIFT
		    + 130
		    + (row * Node.NODE_DRAW_SIZE * HORIZONTAL_NODE_SPACING * ZOOM_FACTOR);
	    in.paint((int) x, (int) ((Node.NODE_DRAW_SIZE * 2)
		    * VERTICAL_NODE_SPACING * ZOOM_FACTOR)
		    + TOP_NODE_SHIFT);
	    row++;
	}
	int y = 1;
	row = 0;
	ArrayList<HiddenNode> netHiddenNodes = (ArrayList<HiddenNode>) network
		.getHiddenNodesInNetwork().clone();
	for (HiddenNode n : netHiddenNodes) {
	    y += 1 /* + network.getHiddenNodesInNetwork().indexOf(n) % 4 */;
	    double x = LEFT_NODE_SHIFT
		    + 180
		    + (row * Node.NODE_DRAW_SIZE * HORIZONTAL_NODE_SPACING * ZOOM_FACTOR);
	    n.paint((int) x, (int) (y * (Node.NODE_DRAW_SIZE * 2)
		    * VERTICAL_NODE_SPACING * ZOOM_FACTOR)
		    + TOP_NODE_SHIFT);
	    if (network.getHiddenNodesInNetwork().indexOf(n) % 10 == 0) {
		row++;
		y = 1;
	    }
	    // if(column >= 2) {
	    // column+=10;
	    // }

	}
	row = 0;
	for (Output out : network.getOutputNodesInNetwork()) {
	    double x = LEFT_NODE_SHIFT
		    + 180
		    + 200
		    + (row * Node.NODE_DRAW_SIZE * HORIZONTAL_NODE_SPACING * ZOOM_FACTOR);

	    out.paint((int) x, (int) (Node.NODE_DRAW_SIZE * 20
		    * VERTICAL_NODE_SPACING * ZOOM_FACTOR)
		    + TOP_NODE_SHIFT);
	    row++;
	}
    }

    public void drawSynapses(NNetwork net) {
	// net.removeUnusedSynapses();
	ArrayList<Synapse> synapsesClone = (ArrayList<Synapse>) net
		.getNetworkSynapses().clone();
	synapsesClone = (ArrayList<Synapse>) synapsesClone.clone();
	for (Synapse connection : synapsesClone) {
	    if (connection == null
		    || connection.getConnectionDestination() == null
		    || connection.getConnectionOrigin() == null)
		continue;
	    if (!connection.hasPaintedInTick()) {
		Point originDrawingPoint = connection.getConnectionOrigin().graphicsRepresentationObject
			.getPaintCoords();
		Point destinationDrawingPoint = connection
			.getConnectionDestination().graphicsRepresentationObject
			.getPaintCoords();
		Color displayColor = Display.getDisplayBackgroundColor();
		Display.setDisplayBackgroundColor(NetworkUtil
			.returnWeightColor(connection.getSynapseWeight()));
		Display.drawLine(originDrawingPoint.x, originDrawingPoint.y,
			destinationDrawingPoint.x, destinationDrawingPoint.y);
		Display.setDisplayBackgroundColor(displayColor);
		connection.setHasPaintedInTick(true);
	    }
	    // } else {
	    //
	    // }

	}
    }
}