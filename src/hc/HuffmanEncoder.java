package hc; //Using skeleton from TA @onsmith

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;

import io.OutputStreamBitSink;

public class HuffmanEncoder {
	
	private HashMap<Integer, String> _code_map;
	
	public HuffmanEncoder(int[] symbols, int[] symbol_counts) {
		assert symbols.length == symbol_counts.length;
		
		// Given symbols and their associated counts, create initial
		// Huffman tree. Use that tree to get code lengths associated
		// with each symbol. Create canonical tree using code lengths.
		// Use canonical tree to form codes as strings of 0 and 1
		// characters that are inserted into _code_map.
		

		// Step 1:
		int total = 0;
		for (int c : symbol_counts) total+=c;
		Node huffTreeNode = constructHuffmanTree(total, symbol_counts);

	/*	Aaron's code using ArrayList: (but I implemented it using Priority Queue)
		// Start with an empty list of nodes
		List<Node> node_list = new ArrayList<Node>();
		
		// Create a leaf node for each symbol, encapsulating the
		// frequency count information into each leaf.

		// Sort the leaf nodes
		node_list.sort(null);

		// While you still have more than one node in your list...
		while(node_list.size() > 1) {
			// Remove the two nodes associated with the smallest counts
			
			// Create a new internal node with those two nodes as children.
			
			// Add the new internal node back into the list
			
			// Resort
		}
	*/
		// Create a temporary empty mapping between symbol values and their code strings
		HashMap<Integer, String> cmap = new HashMap<Integer, String>();

		// Start at root and walk down to each leaf, forming code string along the
		// way (0 means left, 1 means right). Insert mapping between symbol value and
		// code string into cmap when each leaf is reached.
		updateMap(huffTreeNode, cmap);
		
		// Create empty list of SymbolWithCodeLength objects
		List<SymbolWithCodeLength> sym_with_length = new ArrayList<SymbolWithCodeLength>();

		// For each symbol value, find code string in cmap and create new SymbolWithCodeLength
		// object as appropriate (i.e., using the length of the code string you found in cmap).
		for (Entry<Integer, String> e : cmap.entrySet()) {
			int value = e.getKey();
			int code_length = e.getValue().length();
			SymbolWithCodeLength symbol = new SymbolWithCodeLength(value, code_length);
			sym_with_length.add(symbol);
		}
		
		// Sort sym_with_lenght
		sym_with_length.sort(null);

		// Now construct the canonical tree as you did in HuffmanDecodeTree constructor
		
		Node canonical_root = new Node(-1,0); //internal root node
		for(SymbolWithCodeLength symbol : sym_with_length) {
			int ascii_value = symbol.value();
			int length = symbol.codeLength();
			Node node = new Node(ascii_value, length);
			canonical_root.insert(node, length);
		}

		// If all went well, tree should be full.
		assert canonical_root.isFull();
		
		// Create code map that encoder will use for encoding
		
		_code_map = new HashMap<Integer, String>();
		
		// Walk down canonical tree forming code strings as you did before and
		// insert into map.		
		updateMap(canonical_root, _code_map);
		
		// Calculate the entropy:
		double huff_entropy = 0.0;
		for(Entry<Integer, String> e : _code_map.entrySet()) {
			int ascii_value = e.getKey();
			double length = (double) e.getValue().length();
			double count = (double) symbol_counts[ascii_value];
			huff_entropy += length*count;
		}
		
		huff_entropy = huff_entropy/(double)total;
		
		System.out.println("My Huffman Encoder has entropy " + huff_entropy);
	}

	public String getCode(int symbol) {
		return _code_map.get(symbol);
	}

	public void encode(int symbol, OutputStreamBitSink bit_sink) throws IOException {
		bit_sink.write(_code_map.get(symbol));
	}
	
	private void updateMap(Node node, HashMap<Integer, String> map) {		
		if(node.isSym()) {
			int ascii_value = node.getAscii();
			String encode = node.getEncoding().toString();
			
			map.put(ascii_value, encode);
			return;
		}else { //recursively go left and go right
			if(node.left != null) {
				StringBuilder curr_encode = node.getEncoding();
				StringBuilder next_encode = new StringBuilder(curr_encode.toString());
				next_encode.append(0);
				
				node.left.encode = next_encode;
				
				updateMap(node.left, map);
			}
			
			if(node.right != null) {
				StringBuilder curr_encode = node.getEncoding();
				StringBuilder next_encode = new StringBuilder(curr_encode.toString());
				next_encode.append(1);
				
				node.right.encode = next_encode;
				
				updateMap(node.right, map);
			}
		}
	}

	private Node constructHuffmanTree(int total, int[] symbol_counts) {
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
		double total_entropy = 0.0;
		for(int i = 0; i < 256; i++) {
			int count = symbol_counts[i];
			Node node = new Node(i,count,total);
			
			//Calculate Entropy: -P(A)*log2(P(A))
			//// log2:  Logarithm base 2 = logK/log2 //Math.log(d)/Math.log(2.0);

			double probability = node.getProbability();
			double entropy = probability* (-1.0) * (Math.log(probability)/Math.log(2.0));
			if(!Double.isNaN(entropy)) total_entropy += entropy;
			System.out.println("Symbol " + (char)i + " has probability " + probability + " and entropy " + entropy);
			
			pq.add(node);
		}
		System.out.println("The optimal entropy is " + total_entropy);
		
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
		
		//Step 4: return the root node
		System.out.println("The root node is " + root.getProbability() + " with height " + root.getHeight());
		return root;
	}
}
