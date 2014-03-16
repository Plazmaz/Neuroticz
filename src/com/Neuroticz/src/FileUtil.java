package com.Neuroticz.src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
	public int fileCount = 0;

	/**
	 * Pulls in the initial input files and begins to add them to the
	 * fileContents arraylist to be passed into the network
	 * 
	 * @param path
	 *            the location on the system where the intial files reside
	 * @return the arraylist containing the lines from the file that have been
	 *         parsed
	 */
	public static ArrayList<String> parseFileByLine(String path) {
		ArrayList<String> fileContents = new ArrayList<String>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new FileReader(path));
		} catch (FileNotFoundException err) {
			err.printStackTrace();
		}
		try {
			String line;
			float likelyhood = 0; // out of 100
			while ((line = in.readLine()) != null) {
				fileContents.add(line);

			}

		} catch (IOException err2) {
			err2.printStackTrace();
		}
		try {
			in.close();
		} catch (IOException err3) {
			err3.printStackTrace();
		}
		return fileContents;
	}

	/**
	 * Compile input and expected data for the learning process.
	 * 
	 * @param inputFolder
	 *            The folder with all of the input files
	 * @param expectedFolder
	 *            The folder with all expected outputs
	 * @return learningData The data compiled for input to the neural network
	 */
	public static List<File> compileLearningData(File inputFolder) {
		return Arrays.asList(inputFolder.listFiles());
	}
}
