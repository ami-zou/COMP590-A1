package hc;

import java.io.FileInputStream;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

import java.util.*;

/* Algorithms:
1. A loop to read the first 256 bytes (8 bits each)
    - each byte contains info for the symbol and length
        - indices (0...255) is the symbol
        - the actually value you read is the length associate with that symbol
    - symbol: int for (char) ascii
    - length: the length of the leaf in Huffman tree
2. To construct the tree, needs to:
    - first read everything and store it in an array
    - sort the array
    - when there is a tie, small values come before larger int
    - construct the tree
2. Read next 4 bytes (32 bits) => length of the message (number of char in it)
3. A loop to read and decode the rest
    - needs a function to get the symbol according to the length

* Food for thought:
What data structure do we use?
Currently, I'm just using a Node class to implement everything.

However, for a better performance, maybe we can have a HashMap look-up table,
and construct the table when we are decoding (i.e. only store the values that
we have already decoded).
*/

public class Decoder {
    Node root;
    List<Node> nodeList;
    int total;
    
    public Decoder(){
        this.root = new Node(-1, 0);
        nodeList = new ArrayList<Node>();
        this.total = 0;
    }

    public void decode(String input_file_name, String output_file_name) throws InsufficientBitsLeftException, IOException{
    	InputStream fis = new FileInputStream(input_file_name);
    	InputStreamBitSource inputReader = new InputStreamBitSource(fis);
    	
    	nodeList = new ArrayList<Node>();
    	
    	//read one byte (8 bits) for each symbol -- 256 in total
    	for(int i = 0; i < 256; i++) {
    		int len = inputReader.next(8); 
    		Node node = new Node(i, len);
    		nodeList.add(node);
    	}
    	
    	//read the next 4 bytes (32 bits) for the total length
    	this.total = inputReader.next(32); 
    	
    	//Construct the tree
    	constructHuffmanTree();
    	
    	//Decode + Output the file
    	FileOutputStream fos = new FileOutputStream(output_file_name);
    	
    	int num = 0;
    	Node curr = root;
    	while(num < total) {
    		int bit = inputReader.next(1);

    		//Reading the bit: 0 goes left and 1 goes right
    		if(bit == 0) {
    			curr = curr.left;
    		}else {
    			curr = curr.right;
    		}
    		
    		//Output this symbol; increase counter; reset curr node to root
    		if(curr.isSym()) { 
    			char symbol = curr.symbol;
    			
    			fos.write(symbol);

    			num++;
    			curr = root;
    		}	
    	}
    	
    	fos.flush();
		fos.close();
		fis.close();
    }
    
    private void constructHuffmanTree() {
    	//Step 1: sort the node list
    	nodeList.sort(new Comparator<Node>() {
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
    	
    	//Step 2: insert into the tree
    	for(Node n : nodeList) {
    		root.insert(n, n.getLength());
    	}
    }
    
    //A helper method for checking the constructed Huffman tree
    private void printPreorder(Node node) { 
        if (node == null) 
            return; 
  
        /* first print data of node */
        System.out.print(node.symbol + " "); 
  
        /* then recur on left subtree */
        printPreorder(node.left); 
  
        /* now recur on right subtree */
        printPreorder(node.right); 
    } 
}
