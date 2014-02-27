package com.Neuroticz.src;

import java.util.ArrayList;

import me.dylan.NNL.HiddenNode;
import me.dylan.NNL.Input;
import me.dylan.NNL.NNLib;
import me.dylan.NNL.NNetwork;
import me.dylan.NNL.Output;
import me.dylan.NNL.Synapse;
import me.dylan.NNL.Utils.ArrayUtil;
import me.dylan.NNL.Utils.NetworkUtil;
import me.dylan.NNL.Visualizer.Display;

public class MainLoop implements Runnable {
    private String desiredOutput;
    public ArrayList<NNetwork> allNetworks = new ArrayList<NNetwork>();
    private ArrayList<NNetwork> netsSortedByFitness = new ArrayList<NNetwork>();
    ArrayList<NNetwork> parentNetworks = new ArrayList<NNetwork>();
    int generation = 1;

    double avg = 0;
    ArrayList<Double> similarityPercentages = new ArrayList<Double>();

    public MainLoop(String desiredOutput) {
	this.desiredOutput = desiredOutput;
    }

    @Override
    public void run() {
	while (!Thread.interrupted()) {
	    if(Display.isKeyDown(' '))
		continue;
	    ArrayList<NNetwork> allNetsClone = (ArrayList<NNetwork>) allNetworks
		    .clone();
	    for (NNetwork net : allNetsClone) {
		doMainLoopTick(net);

	    }
	    try {
		Thread.sleep(50);
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}
    }

    public void doMainLoopTick(NNetwork net) {

	// timer.start();
	net.removeUnusedSynapses();
	for (Input in : net.getInputNodesInNetwork()) {
//	    if(NNLib.GLOBAL_RANDOM.nextInt(101)<=NNLib.CHANCE_FOR_INPUT_ACTIVATION)
	    in.activateInputNode();
	}
	// timer.start();
	for (HiddenNode n : net.getHiddenNodesInNetwork()) {
	    n.doTick();
	}
	// timer.end();
	// System.out.println("Hidden Milis: " + timer.getElapsedTimeMilis());
	// timer.end();
	// System.out.println(timer.getElapsedTimeMilis());
	// System.out.println("Paint Milis: " + timer.getElapsedTimeMilis());
	doGenerationTick(net);
	System.out.println("Network count is " + allNetworks.size()
		+ " at generation " + generation
		+ " and the Output fitness is "
		+ net.getNetworkSimilarityPercentage(desiredOutput) + "%");
	System.out.println("Output: " + net.getNetworkOutput());
	// net.getOutputNodesInNetwork().clear();
//	for (Input in : net.getInputNodesInNetwork()) {
//	    in.deactiveInputNode();
//	}
	try {
	    Thread.sleep(20);
	} catch (InterruptedException e) {
	    e.printStackTrace();
	}
    }

    public void doGenerationTick(NNetwork net) {
	avg = 0;
	double matchPercentage = net
		.getNetworkSimilarityPercentage(desiredOutput);
	if (matchPercentage <= 90) {
		net.removeUnusedSynapses();
	    if (!netsSortedByFitness.isEmpty()) {
		// allNetworks.clear();
		// if (matchPercentage >= netsSortedByFitness.get(0)
		// .getNetworkSimilarityPercentage(desiredOutput)) {
		// allNetworks.clear();
		// mostFit = ArrayUtil.shiftNetworkArray(
		// mostFit, 1);
		// if (!netsSortedByFitness.contains(net)) {
		NetworkUtil.updateSynapseGenerations(net);
		for (Output out : net.getOutputNodesInNetwork()) {
		    if (out.getNodeConnections().isEmpty())
			continue;
		    for (Synapse synapse : out.traceBackSynapses(true, net)) {
			if (synapse.getSynapseWeight()
				+ NNLib.SYNAPSE_MULTIPLIER_PER_GENERATION <= NNLib.MAX_CONNECTION_WEIGHT) {

			    synapse.setSynapseWeight(synapse.getSynapseWeight()
				    * NNLib.SYNAPSE_MULTIPLIER_PER_GENERATION);

			}
		    }
		}
		// }
//		netsSortedByFitness.add(net);
//		ArrayUtil.swapItems(net, netsSortedByFitness.get(0),
//			netsSortedByFitness);
		// } else {
		// net.randomizeConnections();
		// }
		// for (int i = 0; i < netsSortedByFitness.size(); i++) {
		// double similarity = netsSortedByFitness.get(i)
		// .getNetworkSimilarityPercentage(desiredOutput);
		// avg += similarity;
		// similarityPercentages.add(similarity);
		//
		// }
		// avg /= netsSortedByFitness.size();
		// mostFit.set(0, net);
		// allNetworks.clear();

		// netsSortedByFitness.addAll(allNetworks);

		// for (int i = 0; i < netsSortedByFitness.size(); i++) {
		//
		// if (similarityPercentages.get(i) >= (avg *
		// Neuroticz.NETWORK_SURVIVAL_THRESHOLD_ADDITIVE)) {
		// parentNetworks.add(netsSortedByFitness.get(i));
		// // allNetworks.add(netsSortedByFitness.get(i));
		// }
		//
		// }
		// // if (parentNetworks.isEmpty()) {
		// // for (int i = 0; i < 3; i++) {
		// // parentNetworks.add(netsSortedByFitness.get(i));
		// // }
		// // }
		// allNetworks.clear();
		generation++;
		// allNetworks.addAll(parentNetworks);
		// for (int i = 0; i <
		// Neuroticz.NETWORKS_ADDITION_PER_GENERATION; i++) {
		// allNetworks.add(NetworkUtil.breedNetworks(parentNetworks,
		// 20));
		// }

	    } else {
		netsSortedByFitness.addAll(allNetworks);
	    }

	} else {
	    System.out.println("Training completed.");
	    System.out.println(netsSortedByFitness.get(0).getNetworkOutput());
	    System.exit(0);
	}
    }
}
