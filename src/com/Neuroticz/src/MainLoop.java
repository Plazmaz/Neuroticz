package com.Neuroticz.src;

import java.util.ArrayList;

import me.dylan.NNL.HiddenNode;
import me.dylan.NNL.Input;
import me.dylan.NNL.NNetwork;
import me.dylan.NNL.Synapse;
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
//	net.removeUnusedSynapses();
	for (Input in : net.getInputNodesInNetwork()) {
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
	for(Synapse synapse : net.getNetworkSynapses()) {
	    synapse.hasPulsedInTick = false;
//	    synapse.setPulseBack(false);
	}
//	System.out.println("Network count is " + allNetworks.size()
//		+ " at generation " + generation
//		+ " and the Output fitness is "
//		+ net.getNetworkSimilarityPercentage() + "%");
//	System.out.println("Output: " + net.getNetworkOutput());
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
		.getNetworkSimilarityPercentage();
	if (matchPercentage <= 90) {
//		net.removeUnusedSynapses();
//	    if (!netsSortedByFitness.isEmpty()) {
		NetworkUtil.updateSynapseGenerations(net);
		/*for (Output out : net.getOutputNodesInNetwork()) {
		    if (out.getNodeConnections().isEmpty())
			continue;
		    for (Synapse synapse : out.traceBackSynapses(true, net)) {
			if (synapse.getSynapseWeight()
				+ NNLib.SYNAPSE_MULTIPLIER_PER_GENERATION <= NNLib.MAX_CONNECTION_WEIGHT) {

			    synapse.setSynapseWeight(synapse.getSynapseWeight()
				    * NNLib.SYNAPSE_MULTIPLIER_PER_GENERATION);

			}
		    }
		}*/
		generation++;

//	    } else {
//		netsSortedByFitness.addAll(allNetworks);
//	    }

	} else {
	    System.out.println("Training completed.");
	    System.out.println(allNetworks.get(0).getNetworkOutput());
	    System.exit(0);
	}
    }
}
