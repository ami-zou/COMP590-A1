package hc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.InputStreamBitSource;

public class Encoder {
	private int[] symbol_counts; //symbol index --> count -> frequncy
	private List<List<Integer>> symbolTable; // symbol index --> encoding arr
	private int[] symbol_len;  // symbol index --> node length
	
	public Encoder() {
		symbol_counts = new int[256];
		symbol_len = new int[256];
		symbolTable = new ArrayList<List<Integer>>();
	}
	
	public void encode(String input_file_name, String output_file_name) throws IOException {
		InputStream fis = new FileInputStream(input_file_name);
    	InputStreamBitSource inputReader = new InputStreamBitSource(fis);
    	
		symbolTable = new ArrayList<List<Integer>>();
		
		int num_symbols = 0;
		int next_byte = fis.read(); //Cast the character into int index
		
		//Step 1: read the file once and construct the frequency table
		while (next_byte != -1) {
			symbol_counts[next_byte]++;
			num_symbols++;

			next_byte = fis.read();
		}
		fis.close();
		
		//Checking ---- Remove this
		System.out.println("[ Encoder ] Total number of symbols is " + num_symbols);
		for(int i = 0 ; i< 256; i ++) {
			System.out.println("Character " + (char)i + " has count " + symbol_counts[i]);
		}
		
		//Step 2: create Huffman Encoding Tree with proper sorting
		
		//Step 3: iterate through the tree and a. Store the length info (canonical tree), b. Store the encoding info
		
		//Step 4: read the file again, do a encode look-up for each symbol, and write the encode
		FileOutputStream fos = new FileOutputStream(output_file_name);
		//fos.write(encoding);
		
		fos.flush();
		fos.close();
		fis.close();
	}
}
