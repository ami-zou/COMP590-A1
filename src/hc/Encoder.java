package hc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.*;

import io.InputStreamBitSource;
import io.OutputStreamBitSink;

public class Encoder {
	private int[] symbol_counts; //symbol index --> count
	
	private HashMap<Integer, List<Integer>> symbolTable; // symbol index --> encoding arr
	private int[] symbol_len;  // symbol index --> node length
	
	public Encoder() {
		symbol_counts = new int[256];
		symbol_len = new int[256];
		symbolTable = new HashMap<Integer, List<Integer>>();
	}
	
	public void encode(String input_file_name, String output_file_name) throws IOException {
		InputStream fis = new FileInputStream(input_file_name);
    
		symbolTable = new HashMap<Integer, List<Integer>>();
		
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
		Node root = constructHuffmanTree(num_symbols);
		
		//Step 3: iterate through the tree and 
		//		  a. Store the length info (canonical tree)
		//		  b. Store the encoding info
		canonicalHuffmanTree(root);
		
		//Step 4:
		FileOutputStream fos = new FileOutputStream(output_file_name);
		OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);
		
		//1) write the symbols_len (index-->length) for 1 byte (8 bits)
		for (int i=0; i<256; i++) {
			bit_sink.write(symbol_len[i], 8);
		}
		
		//2) total number of symbols (4 bytes -- 32)
		bit_sink.write(num_symbols, 32);
		
		//3) read the file again + do a encode look-up for each symbol and write the encode
		fis = new FileInputStream(input_file_name);
		for (int i=0; i<num_symbols; i++) {
			int next_symbol = fis.read();
			List<Integer> encoding = symbolTable.get(next_symbol);
			for(int bit : encoding) bit_sink.write(bit,1);
		}
		fis.close();
		
		//fos.flush();
		fos.close();
	}
	
	private Node constructHuffmanTree(int total) {
		//Step 1: create PQ: lowest to highest. Tie: trees with smaller tree heights come first.
		PriorityQueue<Node> pq = new 
	             PriorityQueue<Node>(256, new Comparator<Node>() {
	            	@Override
	         	    public int compare(Node n1, Node n2) {
	         			if(n1.getProbability() < n2.getProbability()) {
	         				return -1;
	         			}else if(n1.getProbability() > n2.getProbability()) {
	         				return 1;
	         			}else {
	         				if(n1.getHeight() < n2.getHeight()){
	             	            return -1; //was -1
	             	        }else if(n1.getHeight() > n2.getHeight()) {
	             	        	return 1; //was 1
	             	        }else {
	             	        	return 0;
	             	        }
	         			}
	         		}
	             });
		
		//Step 2: Calculate the probabilities of each symbol + create node + insert into PQ
		for(int i = 0; i < 256; i++) {
			int count = symbol_counts[i];
			Node node = new Node(i,count,total);
			System.out.println("Now creating node with symbol " + (char)i + " and prob " + node.getProbability());
			pq.add(node);
		}
		
		//[Repeat] Step 3: 
		//				   Remove first 2 nodes to create a new subtree
		//				   Insert the combined tree to the array (re-sort)
		//				   Stop when there is only one node in the PQ
		
		while(pq.size() > 1) { //Stop when pq.size()==1
			Node n1 = pq.poll(); //smallest --> left
			Node n2 = pq.poll(); //2nd smallest --> right
			
			double prob_sum = n1.getProbability() + n2.getProbability();
			int height = Math.max(n1.getHeight(), n2.getHeight()) + 1;
			Node n = new Node(prob_sum, n1, n2, height);
			
			System.out.println("Adding new node with height " + n.getHeight() + " and prob: " + n.getProbability() + "from n1 with height " + n1.getHeight() + " and prob " + n1.getProbability() + " and n2 height " + n2.getHeight() + " and prob " + n2.getProbability());
			
			pq.add(n);
		}
		
		Node root = pq.poll();
		System.out.println("The root node is " + root.getProbability() + " with height " + root.getHeight());
		//Step 4: return the root node
		return root;
	}

	/* iterate through the tree and 
	 * a. Store the length info (canonical tree) //Update symbol_len
	 * b. Store the encoding info //Update symbolTable
	 */
	private void canonicalHuffmanTree(Node node) {
		if(node.getHeight()==0) {
			int symbol = node.getAscii();
			
			List<Integer> encoding = node.getEncoding();
			int length = encoding.size();
			node.length = length;
			symbol_len[symbol] = length;
			symbolTable.put(symbol, encoding);
			
			System.out.println("Index " + symbol + " Symbol " + (char)symbol + " with length " + node.length + " and encoding with size " + encoding.size()  + " :"+ encoding);
		}else {
			List<Integer> encoding = node.encode;
			
			if(node.left != null) {
				List<Integer> newEncoding = new ArrayList<Integer>(encoding); //clone
				newEncoding.add(0);
				node.left.encode = newEncoding;
				canonicalHuffmanTree(node.left);
			}
			
			if(node.right != null) {
				List<Integer> newEncoding = new ArrayList<Integer>(encoding);
				newEncoding.add(1);
				node.right.encode = newEncoding;
				canonicalHuffmanTree(node.right);
			}
		}
	}
	
	/*
	 * Unused level update method
	 */
	private void updateLevelNodes(Node node, int height, int max) { //depth = length = dis(root->node)		
		//only update legnth when height = 0 (leaf/symbol node)
		if(max-node.getHeight() == height) {
			int symbol = node.getAscii(); //could be level 1~3 which half are internal node ==> ascii = -1
			
			if(symbol != -1) symbol_len[symbol] = node.length; //TODO maybe another way of tracking the length!
			List<Integer> encoding = node.getEncoding();
			System.out.println("Symbol " + (char)symbol + " with length: " + node.length + " and encoding: " + encoding);
		}else {//o/w, go left or right and insert bits
			if(node.left != null) {
				node.left.addEncodeBit(0);
				node.left.length++;
				updateLevelNodes(node.left, height+1, max);
			}else if(node.right != null) {
				node.right.addEncodeBit(1);
				node.right.length++;
				updateLevelNodes(node.right, height+1, max);
			}else {
				//Not possible. Cuz in this case height = 0
			}
		}
	}
}
