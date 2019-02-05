package hc;

public class Node {
    Node left;
    Node right;
    int ascii;
    char symbol;
    int length;

    public Node(int ascii, int length, Node left, Node right){
        this.ascii = ascii;
        this.length = length;
        this.left = left;
        this.right = right;
        this.symbol = (char) this.ascii;
    }

    public Node(int ascii, int length){
        this(ascii, length, null, null);
    }

    public boolean isFull(){
        return (left != null && right != null);
    }

    public boolean isLeftFull(){
        return left != null;
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
}
