package com.Neuroticz.src;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

import me.dylan.NNL.HiddenNode;
import me.dylan.NNL.Input;
import me.dylan.NNL.NNetwork;
import me.dylan.NNL.Node;
import me.dylan.NNL.Output;
import me.dylan.NNL.Synapse;
import me.dylan.NNL.Utils.NetworkUtil;
import me.dylan.NNL.Visualizer.Display;

/**
 * Infinitely looping runnable for use with threads. Runs every 50 milliseconds
 * 
 */
public class MainLoop implements Runnable {
    public ArrayList<NNetwork> allNetworks = new ArrayList<NNetwork>();
    ArrayList<NNetwork> parentNetworks = new ArrayList<NNetwork>();
    int generation = 1;

    double avg = 0;
    ArrayList<Double> similarityPercentages = new ArrayList<Double>();

    @Override
    public void run() {
	while (!Thread.interrupted()) {
	    if (Display.isKeyDown(' '))
		continue;
	    ArrayList<NNetwork> allNetsClone = (ArrayList<NNetwork>) allNetworks
		    .clone();
	    for (NNetwork net : allNetsClone) {
		doMainLoopTick(net);

	    }
	    doDraw();
	    
	    try {
		Thread.sleep(50); // adjust to change loop time
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    /**
     * Resets the node state by setting the hidden nodes to active, declaring
     * that they have not pulsed this tick, and disabling input nodes. Also
     * outputs the current amount of networks, the current generation, and the
     * current fitness of the network compared to the desired output
     * 
     * @param parentNet
     *            Network to which the nodes belong
     */
    // TODO: MAY NEED TO CHANGE THE INPUT NODE DEACTIVATION FOR WHEN WE COMBINE
    // TWO NODE VALUES AND FEED IT BACK THROUGH
    public void doMainLoopTick(NNetwork parentNet) {

	for (Input in : parentNet.getInputNodesInNetwork()) {
	    in.activateInputNode();
	}

	for (HiddenNode n : parentNet.getHiddenNodesInNetwork()) {
	    n.doTick();
	}

	for (Synapse synapse : parentNet.getNetworkSynapses()) {
	    synapse.hasPulsedInTick = false;
	}
	for (Input in : parentNet.getInputNodesInNetwork()) {
	    in.deactivateInputNode();
	}
	System.out.println("Network count is " + allNetworks.size()
		+ " at generation " + generation
		+ " and the Output fitness is "
		+ parentNet.getNetworkSimilarityPercentage() + "%");
	System.out.println("Output: " + parentNet.getNetworkOutput());
    }

    public void doDraw() {
	ArrayList<NNetwork> networkClones = (ArrayList<NNetwork>) allNetworks
		.clone();
	int netcount = 0;
	Display.clearRect(0, 0, Display.getWidth(), Display.getHeight());
	for (NNetwork net : networkClones) {

	    netcount++;
	    Display.setOffset(new Point(netcount
		    * Neuroticz.NETWORK_DISPLAY_OFFSET_MULTIPLIER, 0));
	    draw(net);
	    Display.setOffset(new Point(-netcount
		    * Neuroticz.NETWORK_DISPLAY_OFFSET_MULTIPLIER, 0));
	}
	try {
	    Thread.sleep(20);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

	//TODO: More drawing stuff, do we want to document it?
	public void draw(NNetwork network) {
		drawSynapses(network);
		int row = 0;
		ArrayList<Input> netInputNodes = (ArrayList<Input>) network
				.getInputNodesInNetwork().clone();
		for (Input in : netInputNodes) {
			double x = Neuroticz.NODE_SHIFT.x
					+ 130
					+ (row * Node.NODE_DRAW_SIZE * Neuroticz.HORIZONTAL_NODE_SPACING * Neuroticz.ZOOM_FACTOR);
			in.paint((int) x, (int) ((Node.NODE_DRAW_SIZE * 2)
					* Neuroticz.VERTICAL_NODE_SPACING * Neuroticz.ZOOM_FACTOR)
					+ Neuroticz.NODE_SHIFT.y);
			row++;
		}
		int y = 1;
		row = 0;
		ArrayList<HiddenNode> netHiddenNodes = (ArrayList<HiddenNode>) network
				.getHiddenNodesInNetwork().clone();
		for (HiddenNode n : netHiddenNodes) {
			y += 1;
			double x = Neuroticz.NODE_SHIFT.x
					+ 180
					+ (row * Node.NODE_DRAW_SIZE * Neuroticz.HORIZONTAL_NODE_SPACING * Neuroticz.ZOOM_FACTOR);
			n.paint((int) x, (int) (y * (Node.NODE_DRAW_SIZE * 2)
					* Neuroticz.VERTICAL_NODE_SPACING * Neuroticz.ZOOM_FACTOR)
					+ Neuroticz.NODE_SHIFT.y);
			if (network.getHiddenNodesInNetwork().indexOf(n) % 10 == 0) {
				row++;
				y = 1;
			}

		}
		row = 0;
		for (Output out : network.getOutputNodesInNetwork()) {
			double x = Neuroticz.NODE_SHIFT.x
					+ 180
					+ 200
					+ (row * Node.NODE_DRAW_SIZE * Neuroticz.HORIZONTAL_NODE_SPACING * Neuroticz.ZOOM_FACTOR);

			out.paint((int) x, (int) (Node.NODE_DRAW_SIZE * 20
					* Neuroticz.VERTICAL_NODE_SPACING * Neuroticz.ZOOM_FACTOR)
					+ Neuroticz.NODE_SHIFT.y);
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
