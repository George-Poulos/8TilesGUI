
package sample;
import java.util.ArrayList;
import java.util.Scanner;
import java.lang.System;

/**
 * 8 Tiles puzzle, implemented as both text-based and graphical interfaces.
 *
 * Created 9/30/2016 for UIC CS 342, Software Design
 * System: IntelliJ Java IDE on Lenovo Thinkpad Yoga
 *
 * Driver class to interact with user and trigger automatic search mode if desired.
 */
public class TilesDriver
{
    // Declare the search tree, to be further initialized if we search for an automatic puzzle solution
    public SearchTree theSearchTree;           // Tree of Nodes
    public Board bestBoard;                    // Keep track of best board found, to be displayed if no solution found.
    public ArrayList<Integer> possibleMoves;   // List of possible moves from current board position
    public Board theBoard;                     // Current board being considered
    public int moveNumber = 1;                 // Enumerates total unique board positions explored


    //-----------------------------------------------------------------------------------------
    public static void main(String[] args) {
        TilesDriver theDriver = new TilesDriver();
        theDriver.doIt();       // Chain off to allow sharing of class variables
    }


    //-----------------------------------------------------------------------------------------
    void doIt() {

        displayIdentifyingInformation();
        displayInstructions();

        theBoard = initializeBoard();
        System.out.println("\nInitial board is: ");
        displayTheBoard( moveNumber, theBoard);
        moveNumber++;

        char pieceToMove = ' ';                 // Used for interactive user input
        bestBoard = theBoard;                   // Keep track of best board found, to be displayed if no solution found.
        Scanner userInput = new Scanner(System.in);  // User input for interactive play

        boolean inInteractiveMode = true;     // Puzzle can be solved interactively or automatically

        // Keep searching until solution is found or all possible moves are exhausted.  If at some point user input of
        // 's' (for "solve") is entered, the loop enters "automatic solve the puzzle" mode that is no longer interactive.
        while( theBoard.isFinished() != true) {

            // Prompt for piece to move, 'x' to exit or 's' to solve puzzle
            System.out.print("Piece to move: ");
            pieceToMove = userInput.next().charAt(0);

            if (pieceToMove == 's') {
                // Exit interactive mode
                solvePuzzleAutomatically();
                break;      // Upon returning break out of moves loop, to go display the solution if there is one.
            }
            else if( pieceToMove == 'x') {      // Exit program
                System.out.println("Exiting program...\n");
                System.exit( 1);
            }

            // Get current possible moves and use it to validate user input
            possibleMoves = theBoard.findPossibleMoves();
            boolean pieceToMoveIsValid = pieceToMoveIsOnValidMovesList( theBoard, pieceToMove - '0', possibleMoves);

            // If we have a valid move make the move, establish it as the current search tree node, and display it
            if( pieceToMoveIsValid) {
                theBoard.movePiece( pieceToMove-'0');     // Make the move
                // Store this new board as the current one in the search tree, so we can use
                // it as a starting point for an automatic solution search later if that is chosen.
                SearchTree.currentNode = new Node( SearchTree.currentNode, new Board( theBoard));
                System.out.println("\n");
                // Display the board
                displayTheBoard( moveNumber, theBoard);
                moveNumber++;
            }
            else {
                // Invalid move
                System.out.println("*** Invalid move.  Please retry. \n");
                continue;
            }
        }//end while( theBoard...

        // Display a solution if there was one
        if( theBoard.isFinished()) {
            // Display the solution in order
            theSearchTree.displaySolutionPath( SearchTree.currentNode);
        }

        System.out.println("Done.\n");
    }//end main()


    //-----------------------------------------------------------------------------------------
    public void displayIdentifyingInformation() {
        System.out.println(  "Author: Dale Reed \n" +
                "Class: CS 342, Fall 2016 \n" +
                "Program: #3, 8 Tiles. \n" +
                "\n");
    }

    //-----------------------------------------------------------------------------------------
    public void displayInstructions() {
        System.out.println( "Welcome to the 8-tiles puzzle. \n" +
                "Place the tiles in ascending numerical order.  For each  \n" +
                "move enter the piece to be moved into the blank square, \n" +
                "or 0 to exit the program. \n" +
                "\n");
    }


    //-----------------------------------------------------------------------------------------
    public Board initializeBoard()
    {
        System.out.println( "Choose a game option: \n" +
                "  1. Start playing \n" +
                "  2. Set the starting configuration");

        // Prompt for and get user input to initialize the board
        Scanner userInput = new Scanner(System.in);  // Used to handle user input
        int menuChoice = 1;
        System.out.print("Enter your choice --> ");
        menuChoice = userInput.nextInt();

        // Setup the starting Board using either a provided string of 8 values, or using random values.
        // Using random values may lead to an unsolvable board, of which there are many.
        Board theBoard;     // Declare the board, to be initialized below
        // Handle menu option to set the starting configuration
        if( menuChoice == 2) {
            System.out.println("Some boards such as 728045163 are impossible.");
            System.out.println("Others such as 245386107 are possible.");
            System.out.print("Enter a string of 6 digits (including 0) for the board --> ");
            String boardValues = userInput.next();
            theBoard = new Board( boardValues);
        }
        else {
            theBoard = new Board("");     // Default random board is created
        }

        return theBoard;
    }//end initializeBoard()


    //-----------------------------------------------------------------------------------------
    void solvePuzzleAutomatically()
    {
        System.out.println("Solving puzzle automatically...........................");
        // Set current board as the root node for the search tree
        Node rootNode = new Node(null, theBoard);
        theSearchTree = new SearchTree(rootNode);

        while( theBoard.isFinished() != true) {
            // Get current possible moves, store them in theSearchTree, and then find the best one
            possibleMoves = theBoard.findPossibleMoves();
            theSearchTree.generateAndStoreNextMoveNodes( possibleMoves);
            SearchTree.currentNode = theSearchTree.findNextBestMove( possibleMoves);

            // If there are still nodes to try in the search tree then see if this is a new best board
            if( SearchTree.currentNode != null) {
                theBoard = SearchTree.currentNode.theBoard;
                // Store possible new best move
                if( theBoard.getHeuristicValue() < bestBoard.getHeuristicValue()) {
                    bestBoard = theBoard;
                }
            }
            else {
                // We have exhausted all moves.  The current puzzle was impossible.
                System.out.println("\n");
                System.out.println("All " + moveNumber + " moves have been tried. \n" +
                        "That puzzle is impossible to solve.  Best board found was: \n" +
                        bestBoard + "\n" +
                        "Exiting program. \n");
                //System.exit( 0);
                return;
            }

            // Continue incrementing move number, used to display total moves tried if there is no solution
            moveNumber++;
        }
    }//end solvePuzzleAutomatically()


    //-----------------------------------------------------------------------------------------
    void displayTheBoard(int moveNumber, Board theBoard)
    {
        System.out.println(moveNumber + ". \n" + theBoard);
    }


    //-----------------------------------------------------------------------------------------
    public boolean pieceToMoveIsOnValidMovesList(
            Board theBoard,
            int pieceToMove,
            ArrayList<Integer> possibleMoves)
    {
        boolean pieceToMoveIsValid = false;
        for (int i : possibleMoves) {
            int pieceAtIndex = theBoard.getPieceAt(i);
            if (pieceToMove == pieceAtIndex) {
                // Desired move is one of the possibilities, so move is valid
                pieceToMoveIsValid = true;
                break;
            }
        }

        return pieceToMoveIsValid;
    }

}//end class TilesDriver
