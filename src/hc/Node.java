package hc;

public class Node {
    Node left; //0
    Node right; //1
    int ascii;
    char symbol;
    int length;
    boolean isSym;

    public Node(int ascii, int length, Node left, Node right){
        this.ascii = ascii;
        this.length = length;
        this.left = left;
        this.right = right;
        this.symbol = (char) this.ascii;
        //this.isLeaf = (ascii == -1)? false : true;
        if(ascii == -1) {
        	this.isSym = false;
        }else {
        	this.isSym = true;
        }
    }

    public Node(int ascii, int length){
        this(ascii, length, null, null);
    }
    
    public void insert(Node node, int depth) {
    	if(depth==1) { //needs to insert now
    		if(left == null) { //insert left
    			left = node;
    		}else if(right == null){
    			right = node;
    		}else { //we are at a full node ==> not possible to insert
    			return;
    		}
    	}else { //needs to move one step down and recursively inserting
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

    public boolean isFull(){ //for each node, if it is leaf or if both children are leaves = full
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
}
