import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


public class FileInput {
	public static void main(String args[]){
	
	BufferedReader in = null;
	try {
		in = new BufferedReader(new FileReader("C:\\Users\\Hunter\\Desktop\\Server\\server.properties"));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} 
	try {
		String text = in.readLine();
	} catch (IOException e) {
		e.printStackTrace();
	} 
	try {
		in.close();
	} catch (IOException e) {
		e.printStackTrace();
	}

	}