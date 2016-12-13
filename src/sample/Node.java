package sample;

/**
 * Created by reed on 10/1/2016.
 *
 * Graph Node stores a Board as well as a parent pointer
 */
public class Node {
    Board theBoard;     // The board at this node
    Node parent;        // Reference to the parent
    Node[] children;    // Array of up 2 to 4 children, depending on the move position
    Node bestChild;     // Used recursively after solution is found, to set solution path children in forwards order

    //-----------------------------------------------------------------------------------------
    // Constructor
    public Node( Node theParent, Board oldBoard) {
        theBoard = new Board( oldBoard);
        parent = theParent;
        int numberOfChildren = theBoard.findPossibleMoves().size();
        children = new Node[ numberOfChildren];
    }

    //-----------------------------------------------------------------------------------------
    public int compareTo( Node otherNode) {
        // Use the Board comparison to also compare Nodes
        return theBoard.compareTo( otherNode.theBoard);
    }

    //-----------------------------------------------------------------------------------------
    // Equality comparison uses board comparison
    public boolean equals( Board oldBoard)
    {
        return theBoard.equals( oldBoard);
    }

    //-----------------------------------------------------------------------------------------
    public String toString()
    {
        return(theBoard + "\n and the parent board is: " + "\n");
    }
}//end class Node
