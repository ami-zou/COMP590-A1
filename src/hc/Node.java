package hc;

import java.util.ArrayList;
import java.util.List;

public class Node {
    Node left; //0
    Node right; //1
    int ascii;
    char symbol;
    int length; //from Root to this node
    boolean isSym;
    int height; //depth from this node to its longest leaf
    double prob;
    StringBuilder encode;

    public Node(int ascii, int length, Node left, Node right){
        this.ascii = ascii;
        this.length = length; //from Root to this node
        this.height = 0; //depth from this node to its longest leaf
        this.left = left;
        this.right = right;
        this.symbol = (char) this.ascii;
        this.isSym = (ascii == -1)? false : true; 
        this.prob = 0;
        this.encode = new StringBuilder();
    }
    
    public void addEncodeBit(int bit) {
    	encode.append(bit);
    }

    public Node(int ascii, int length){
        this(ascii, length, null, null);
    }
    
    public Node(int ascii, int count, int total) {
    	this(ascii, 0, null, null);
    	isSym = true;
    	updateProbability(count, total);
    }
    
    public Node(int ascii, int length, double probability) {
    	this(ascii, length, null, null);
    	this.prob = probability;
    }
    
    public Node(double prob, Node left, Node right) {
    	this(-1, 0, left, right); //left right could be null
    	
    	int leftH = (left==null)? -1 : left.getHeight();
    	int rightH = (right==null)? -1 : right.getHeight();
    	int height = Math.max(leftH, rightH) + 1;
    	this.height = height;
    	this.prob = prob;
    }
    
    public Node(double prob, Node left, Node right, int height) {
    	this(-1, 0, left, right);
    	this.height = height;
    	this.prob = prob;
    }
    
    private void updateProbability(int count, int total) {
    	this.prob = (double)count/(double)total; //Node: need to cast it before dividing
    	//this.prob = (double) count;
    }
    
    public double getProbability() {
    	return prob;
    }
    
    public StringBuilder getEncoding(){
    	return encode;
    }
    
    public void insert(Node node, int depth) {
    	if(depth==1) { //One more layer left, needs to insert the node now!!
    		if(left == null) { //insert left
    			left = node;
    		}else if(right == null){
    			right = node;
    		}else { //we are at a full node ==> not possible to insert
    			return;
    		}
    	}else { //needs to move one step down and recursively insert the node
    		Node internal = new Node(-1, 0);
    		
    		if(left == null) {
    			left = internal;
    			internal.insert(node, depth-1);
    		}else if(!left.isFull()) { //not full subtree, can insert here
    			left.insert(node, depth-1);
    		}else if(right == null) { //left full, check if we can insert right
    			right = internal;
    			internal.insert(node, depth-1);
    		}else {
    			right.insert(node, depth-1);
    		}
    	}
    	return;
    }

    public boolean isFull(){ //for each node, if it is a symbol or if both children are symbols = full
        if (isSym) {
        	return true;
        }else if (left == null || right == null){
        	return false;
        }else {
        	return (left.isFull() && right.isFull() );
        }
    }

    public void addLeft(Node left){
        this.left = left;
    }

    public void addRight(Node right){
        this.right = right;
    }

    public Node getLeft(){
        return left;
    }

    public Node getRight(){
        return right;
    }

    public char getSymbol(){
        return symbol;
    }

    public int getAscii(){
        return ascii;
    }
    
    public int getLength() {
    	return length;
    }
    
    public boolean isSym() {
    	return isSym;
    }
    
/*    public int getHeight() {
    	
    	if(isSym || this.left==null || this.right==null) {
    		this.height = 0;
    	}else {
    	//if(!isSym) { //not leaf node, o/w height = 0
    		int leftH = (this.left == null)? -1 : this.left.getHeight();
    		int rightH = (this.right == null)? -1 : this.right.getHeight();
    		
    		this.height = Math.max(leftH, rightH)+1;
    	}
    	
    	return this.height;
    }
*/
    public int getHeight() {
    	return height;
    }
}
