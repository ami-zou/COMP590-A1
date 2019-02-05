package hc;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.InputStreamBitSource;
import io.InsufficientBitsLeftException;

import java.util.*;

public class Decoder {
    Node root;
    StringBuilder message;
    List<Node> nodeList;
    
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
    
    public Decoder(){
        this.root = new Node(-1, 0);
        message = new StringBuilder();

        nodeList = new ArrayList<Node>();
        
        //System.out.println("Just checking: " + (char)-1);
    }
    
    public void decode(InputStream stream) throws InsufficientBitsLeftException, IOException{
    	readBits(stream);
    	constructHuffmanTree();
    }

    private void readBits(InputStream stream) throws InsufficientBitsLeftException, IOException{
    	InputStreamBitSource inputReader = new InputStreamBitSource(stream);
    	nodeList = new ArrayList<Node>();
    	
    	for(int i = 0; i < 256; i++) {
    		int len = inputReader.next(8); //read one byte -- 8
    		char symbol = (char)i;
    		System.out.println("Index i: " + i + ", symbol: "+ symbol+", with length " + len);
    		Node node = new Node(i, len);
    		System.out.println("Create node with symbol " + node.getSymbol());
    		nodeList.add(node);
    	}
    }
    
    private void constructHuffmanTree() {
    	System.out.println("Hey hey now we're constructing Huffman Coding tree!");
    	
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
    	
    	//Checking function -- remove this
    	for(Node n : nodeList) {
    		System.out.println("Now the sorted node i " + n.getAscii() + " with symbol " + n.getSymbol() + " and length " + n.length);
    	}
    	
    	//Step 2: insert into the tree
    	int H = nodeList.get(nodeList.size()-1).getLength(); //Get the last node's length ==> limit
    	
    	//List<List> tree
    	
    	int i = 0;
    	for(int h = 0; h < H; h++) { //Notice: each level contains 2^h nodes (Math.pow(2,h))
    								 //Iterating from the parent level and insert to its children
    		int num = 1;
    		while(num <= Math.pow(2, h)) {
    			num++;
    		}
    	}
    }

    public String getDecodedMessage(){
        return message.toString();
    }
}