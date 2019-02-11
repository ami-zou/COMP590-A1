package hc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import io.InputStreamBitSource;
import io.OutputStreamBitSink;

/*
 * Not working properly! Check out HuffmanEncoder
 */
public class Encoder {
	private int[] symbol_counts; //symbol index --> count
	
	private HashMap<Integer, String> symbolTable; // symbol index --> encoding arr
	private int[] symbol_len;  // symbol index --> node length
	private List<Node> sym_with_length;
	private HashMap<Integer, String> encodingTable;
	
	public Encoder() {
		symbol_counts = new int[256];
		symbol_len = new int[256];
		symbolTable = new HashMap<Integer, String>();
		encodingTable = new HashMap<Integer, String>();
		sym_with_length = new ArrayList<Node>();
	}
	
	public void encode(String input_file_name, String output_file_name) throws IOException {
		InputStream fis = new FileInputStream(input_file_name);
    
		symbol_counts = new int[256];
		symbol_len = new int[256];
		symbolTable = new HashMap<Integer, String>();
		encodingTable = new HashMap<Integer, String>();
		
		int num_symbols = 0;
		int next_byte = fis.read(); //Cast the character into int index
		
		//Step 1: read the file once and construct the frequency table
		while (next_byte != -1) {
			symbol_counts[next_byte]++;
			num_symbols++;

			next_byte = fis.read();
		}
		fis.close();
		
/*		//Checking ---- Remove this
		System.out.println("[ Encoder ] Total number of symbols is " + num_symbols);
		for(int i = 0 ; i< 256; i ++) {
			System.out.println("Character " + (char)i + " has count " + symbol_counts[i]);
		}
*/
		
		//Step 2: create Huffman Encoding Tree with proper sorting
		Node root = constructHuffmanTree(num_symbols);
		
		//Step 3: iterate through the tree and 
		//		  a. Store the length info (canonical tree)
		//		  b. Store the encoding info
		encodingHuffmanTree(root);
		
		//Step 4: sort symbol_len (shorter to longer)
		// 		  construct the canonicalHuffmanTree: similar to decoding --- insert
		//		  update encodingTable with the length
		sym_with_length = new ArrayList<Node>();
		for(int i = 0; i < symbol_len.length; i++) {
			int len = symbol_len[i];
			Node n = new Node(i, len);
		}
		// sorting:
		sym_with_length.sort(new Comparator<Node>() {
    		@Override
    	    public int compare(Node n1, Node n2) {
    			if(n1.getLength() < n2.getLength()) {
    				return -1;
    			}else if(n1.getLength() > n2.getLength()) {
    				return 1;
    			}else {
    				if(n1.getAscii() < n2.getAscii()){
        	            return -1;
        	        }else if(n1.getAscii() > n2.getAscii()) {
        	        	return 1;
        	        }else {
        	        	return 0;
        	        }
    			}
    		}
    	});
		
		Node canonicalRoot = new Node(-1,0);
		canonicalHuffTree(canonicalRoot);
		
		//	update encodingTable
		updateEncodingTable(canonicalRoot);
		
		//Step 5:
		FileOutputStream fos = new FileOutputStream(output_file_name);
		OutputStreamBitSink bit_sink = new OutputStreamBitSink(fos);
		
		//1) write the symbols_len (index-->length) for 1 byte (8 bits)
		for (int i=0; i<256; i++) {
			int length = encodingTable.get(i).length(); //symbol_len[i]
			System.out.println("now writing symbol i " + i +" : " + (char)i + " with symbol_len " + symbol_len[i]);
			bit_sink.write(length, 8);
		}
		
		//2) total number of symbols (4 bytes -- 32)
		bit_sink.write(num_symbols, 32);
		
		//3) read the file again + do a encode look-up for each symbol and write the encode
		fis = new FileInputStream(input_file_name);
		for (int i=0; i<num_symbols; i++) {
			int next_symbol = fis.read();
			String encoding = encodingTable.get(next_symbol);
			System.out.println("Now encoding i " + next_symbol + " : " + (char)next_symbol + " as " + encoding);
			bit_sink.write(encoding);
		}
		//Include the extre bits
		bit_sink.padToWord();
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
				//System.out.println("Now creating node with symbol " + (char)i + " and prob " + node.getProbability());
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
			
				//System.out.println("Adding new node with height " + n.getHeight() + " and prob: " + n.getProbability() + "from n1 with height " + n1.getHeight() + " and prob " + n1.getProbability() + " and n2 height " + n2.getHeight() + " and prob " + n2.getProbability());
			
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
	private void encodingHuffmanTree(Node node) {
		if(node.isSym) { //node.getAscii()!=-1 is this needed??
			System.out.println("Index is " +  node.getAscii());
			if(node.getAscii()==-1) return;
			
			int symbol = node.getAscii();
			
			StringBuilder encoding = node.getEncoding();
			int length = encoding.length();
			node.length = length;
			symbol_len[symbol] = length;
			symbolTable.put(symbol, encoding.toString());
			
			System.out.println("[encodingHuffmanTree] Index " + symbol + " Symbol " + (char)symbol + " with length " + node.length + " and encoding with size " + encoding.length()  + " :"+ encoding);
		}else {
			StringBuilder encoding = node.encode;
			
			if(node.ascii==105) {
				System.out.println("Found ya!");
				System.out.println("Left is null?" + node.left==null);
				System.out.println("Right is null?" + node.right==null);
			}
			
			if(node.left != null) {
				StringBuilder newEncoding = new StringBuilder(encoding.toString()); //clone
				newEncoding.append(0);
				node.left.encode = newEncoding;
				encodingHuffmanTree(node.left);
			}
			
			if(node.right != null) {
				StringBuilder newEncoding = new StringBuilder(encoding.toString());
				newEncoding.append(1);
				node.right.encode = newEncoding;
				encodingHuffmanTree(node.right);
			}
		}
	}
	
	private void canonicalHuffTree(Node node) { //symbol_len : index i --> 
		for(Node n : sym_with_length) {
			System.out.println("The length is " + n.getLength());
    		node.insert(n, n.getLength());
    	}
	}
	
	private void updateEncodingTable(Node node) { //Node only has info: ascii and len --> else need to be 
		if(node.isSym) { //node.getAscii()!=-1 needed?
			int symbol = node.getAscii();
			
			StringBuilder encoding = node.getEncoding();
			int length = encoding.length();
			//node.length = length;
			//symbol_len[symbol] = length;
			encodingTable.put(symbol, encoding.toString());
			System.out.println("Hey it's me. Index is " + node.getAscii());
			System.out.println("[updateEncodingTable] Index " + symbol + " Symbol " + (char)symbol + " with length " + node.length + " and encoding with size " + encoding.length()  + " :"+ encoding);
		}else {
			StringBuilder encoding = node.encode;
			
			if(node.left != null) {
				StringBuilder newEncoding = new StringBuilder(encoding.toString()); //clone
				newEncoding.append(0);
				node.left.encode = newEncoding;
				encodingHuffmanTree(node.left);
			}
			
			if(node.right != null) {
				StringBuilder newEncoding = new StringBuilder(encoding.toString());
				newEncoding.append(1);
				node.right.encode = newEncoding;
				encodingHuffmanTree(node.right);
			}
		}
	}
}
