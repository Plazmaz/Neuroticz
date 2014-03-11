package com.Neuroticz.src;

import java.util.ArrayList;
import me.dylan.NNL.HiddenNode;
import me.dylan.NNL.Input;
import me.dylan.NNL.NNetwork;
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

}
