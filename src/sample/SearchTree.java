package sample;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Implement the A* algorithm.  Each new Board configuration is added to both a Priority Queue (bestNextNodeQueue) and to a
 * HashMap of all Nodes (allNodes) if it does not already exist.  The Priority Queue allows implementing a heap,
 * where the Node with the lowest value (the most promising) heuristic is chosen to be the next node to be expanded.
 */
public class SearchTree {
    // Priority Queue to store graph nodes
    public Node rootNode;
    PriorityQueue< Node> bestNextNodeQueue; // Priority Queue to give next best Node, by heuristic evaluation
    HashMap<Integer, Node> allNodes;        // List of all Nodes, to ensure new nodes are not duplicates
    public static Node currentNode;                // The current Node is shared across all instances
    public static int sizeOfBestNextNodeQueue = 0;
    public static int sizeOfAllNodes = 0;
    public int sizeOfSolvedTree = 0;


    public int getSizeOfTree(){
        return sizeOfSolvedTree;
    }

    //-----------------------------------------------------------------------------------------
    // Constructor
    public SearchTree(Node theRootNode) {
        rootNode = theRootNode;
        currentNode = rootNode;     // Set the starting value for the current node

        // Setup the Priority Queue to be used in retrieving the best next board to explore on each turn
        bestNextNodeQueue = new PriorityQueue< Node>( new NodeComparator());
        bestNextNodeQueue.add( rootNode);
        sizeOfBestNextNodeQueue++;

        // Setup the HashMap to store all Nodes, which will be used to ensure new boards are unique
        allNodes = new HashMap< Integer, Node>();
        allNodes.put( rootNode.theBoard.hashCode(), rootNode);
        sizeOfAllNodes++;
    }

    //-----------------------------------------------------------------------------------------
    // Private inner class to create a Comparator object to be used with the Priority Queue
    private class NodeComparator implements Comparator< Node>
    {
        @Override
        public int compare(Node x, Node y)
        {
            return x.compareTo( y);
        }
    }

    /**
     * Get Next BestBoard --> Used for TimeLine
     * @return
     */
    public Node getNextBoard(){
        return rootNode = rootNode.bestChild;
    }

    //-----------------------------------------------------------------------------------------
    // Generate the boards with the next moves, storing unique ones on the list of all moves generated so far.
    public void generateAndStoreNextMoveNodes(
            ArrayList<Integer> possibleMoves) // board array index values of 2-4 possible moves
    {
        // For each move make a board with that move and see if it already exists on the allNodes HashMap.
        // If it doesn't already exist make a new Board with this move,
        // make a new node with that Board, and add it to the Priority Queue
        boolean boardAlreadyExists = false;
        for (int indexOfPieceToMove : possibleMoves) {
            // Make a new board copied from the existing one
            Board newBoard = new Board( currentNode.theBoard);

            // Make the move for this new board
            newBoard.movePiece( newBoard.getPieceAt( indexOfPieceToMove));

            // If this new board does *not* already exist, add it to the priorityQueue and to the List
            if( ! allNodes.containsKey( newBoard.hashCode()) ) {
                // Make a new Node with this board
                Node theNewNode = new Node( currentNode, newBoard);
                // Add it to the nodes on the Priority Queue
                bestNextNodeQueue.add( theNewNode);
                sizeOfBestNextNodeQueue++;

                // Add it to the HashMap of all Nodes
                allNodes.put( newBoard.hashCode(), theNewNode);
                sizeOfAllNodes++;
            }
        }//end for( int...
    }


    //-----------------------------------------------------------------------------------------
    // Generate the boards with the next moves, storing unique ones on the list of all moves generated so far.
    // Return the Node with the board that is most promising.
    public Node findNextBestMove( ArrayList<Integer> possibleMoves)   // board array index values of 2-4 possible moves
    {
        // Remove the most promising next move from the bestNextNodeQueue, and set it as the currentNode
        if( bestNextNodeQueue.size() > 0) {
            currentNode = bestNextNodeQueue.remove();
            sizeOfBestNextNodeQueue--;
        }
        else {
            // All moves were exhausted.  Puzzle is impossible to solve.
            currentNode = null;
        }
        return currentNode;
    }//end findNextBestMove()


    //-----------------------------------------------------------------------------------------
    // Recurse backwards through the solution graph until reaching the beginning, setting solution path
    // pointers as we go.
    public void setSolutionPath( Node theNode)
    {
        if( theNode != null && theNode.parent != null ) {
            theNode.parent.bestChild = theNode;
            sizeOfSolvedTree++;
            setSolutionPath( theNode.parent);
        }
    }


    //-----------------------------------------------------------------------------------------
    public void displaySolutionPath( Node endingNode) {
        // recursively set the solution path pointers
        setSolutionPath( endingNode);

        // Now use those pointers in the forwards direction to display the numbered solution path
        int moveNumber = 1;
        Node currentNode = rootNode;
        while( currentNode.theBoard.getHeuristicValue() != 0) {
            System.out.println(moveNumber + ". \n" + currentNode.theBoard);
            moveNumber++;
            currentNode = currentNode.bestChild;
        }
        // Display final solution board
        System.out.println(moveNumber + ". \n" + currentNode.theBoard);
    }


    //-----------------------------------------------------------------------------------------
    public String toString() {
        // Display all elements on the priority queue
        return "Queue: " + bestNextNodeQueue.toString() + "\n" + "All Nodes:" + allNodes.toString() + "\n";
    }


}//end class SearchTree()
