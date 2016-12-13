package sample;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Dale Reed
 */

public class Board {
    public int board[];         // Array of board pieces
    public int heuristicValue;  // Estimate of distance to final ordered board configuration
    public int BoardSize = 9;     // Number of playing squares on the board

    //-----------------------------------------------------------------------------------------
    // Constructor, placing pieces into random starting locations
    Board( String boardValues) {
        // Allocate space for the board
        board = new int[ BoardSize];

        // Initialize board to all 0s
        for( int i=0; i< BoardSize; i++) {
            board[ i] = 0;
        }

        // if board initialization string is empty then find an random unoccupied piece
        if( boardValues.length() == 0) {
            // Step through each value 1..8 and find a random location for it that is not yet taken.
            Random randomGenerator = new Random();
            randomGenerator.setSeed( System.currentTimeMillis());
            for (int i = 1; i < BoardSize; i++) {
                int randomLocation;
                do {
                    randomLocation = randomGenerator.nextInt(BoardSize);
                } while (board[randomLocation] != 0);
                // Set the current number into place
                board[randomLocation] = i;
            }
        }
        else {
            // Use initialization string values sent to constructor to setup board
            for (int i = 0; i < BoardSize; i++) {
                board[i] = boardValues.charAt(i) - '0';
            }
        }

        // Set the heuristic value for the board
        this.findHeuristicValue();
    }//end Board()

    //-----------------------------------------------------------------------------------------
    // Copy constructor
    public Board( Board oldBoard) {
        // Allocate space for the playing pieces
        board = new int[ BoardSize];

        // Copy existing board into new board
        for( int i=0; i< BoardSize; i++) {
            board[ i] = oldBoard.board[ i];
        }

        this.heuristicValue = oldBoard.heuristicValue;
    }//end Board( oldBoard) copy constructor


    // Getters and Setters
    //-----------------------------------------------------------------------------------------
    public int getPieceAt( int position)
    {
        return board[ position];
    }

    //-----------------------------------------------------------------------------------------
    public int getHeuristicValue() {
        return heuristicValue;
    }


    //-----------------------------------------------------------------------------------------
    // Find the index of some piece
    private int findIndexOfPiece( int pieceToFind)
    {
        int returnValue = -1;
        // Iterator through array
        for(int i: board) {
            if( board[i] == pieceToFind) {
                returnValue = i;
                break;
            }
        }

        assert( returnValue != -1);
        return returnValue;
    }//end findIndexOfPiece()


    //-----------------------------------------------------------------------------------------
    // Move from some source, using the stored position of the blank as the destination.
    public int movePiece(int pieceToMove) {
        // Find the index values of the piece being moved as well as the destination blank square represented by 0
        int fromIndex = findIndexOfPiece( pieceToMove);
        int toIndex = findIndexOfPiece( 0);

        // Make the move
        board[ toIndex] = board[ fromIndex];
        board[ fromIndex] = 0;

        // Reset the heuristic value for the board to reflect the new configuration
        this.findHeuristicValue();
        return toIndex;
    }


    //-----------------------------------------------------------------------------------------
    // Estimate how close we are to a solution by adding the distance from each piece
    // to its final location using city-blocks distance of difference in rows and columns
    public void findHeuristicValue()
    {
        heuristicValue = 0;
        for( int i=0; i<BoardSize; i++) {
            // Add the row difference from where it should be
            heuristicValue += rowDifference( i);

            // Add the column difference from where it should be
            heuristicValue += columnDifference( i);
        }
    }//end heuristicValue()


    //-----------------------------------------------------------------------------------------
    // Find the difference in row from where the valueToCheck is and the row where
    // it will finally be.
    int rowDifference( int valueToCheck)
    {
        // Find the destination row
        int currentRow = -1;
        int destinationRow = (valueToCheck-1) / 3;
        // Special case when value is 0, since it should end up on the bottom row
        if( valueToCheck == 0) {
            destinationRow = 2;
        }

        // Find the current row for the value
        for( int i=0; i< BoardSize; i++) {
            if( board[ i] == valueToCheck) {
                currentRow = i/3;
                break;
            }
        }

        //System.out.println("row diff for " + valueToCheck + " is: abs(" + destinationRow + "-" +
        //                    currentRow + ") = " + Math.abs( destinationRow - currentRow));
        // return the vertical distance from current row to its destination row
        return Math.abs( destinationRow - currentRow);
    }//end rowDifference()


    //-----------------------------------------------------------------------------------------
    // Find the difference in column from where the valueToCheck is and the column where
    // it will finally be.
    int columnDifference( int valueToCheck)
    {

        int currentColumn = -1;
        int destinationColumn;
        // Special case when value is 0, since it should end up on the rightmost column
        if( valueToCheck == 0) {
            destinationColumn = 2;
        } else {
            destinationColumn = (valueToCheck - 1) % 3;
        }

        // Find the current column for the value
        for (int i = 0; i < BoardSize; i++) {
            if (board[i] == valueToCheck) {
                currentColumn = i % 3;
                break;
            }
        }

        //System.out.println("  col diff for " + valueToCheck + " is: abs(" + destinationColumn + "-" +
        //                   currentColumn + ") = " + Math.abs( destinationColumn - currentColumn));
        // return the vertical distance from where it is to where it needs to be
        return Math.abs( destinationColumn - currentColumn);
    }//end ColumnDifference()


    //-----------------------------------------------------------------------------------------
    // Find all possible valid moves, which is the list of indices of squares adjacent to the blank (0) square.
    public ArrayList findPossibleMoves() {
        ArrayList<Integer> possibleMoves = new ArrayList<>();

        // Find the "blank" square, represented by integer value 0
        // Create the array list of index values of adjacent squares, using this as reference:
        //      0  1  2
        //      3  4  5
        //      6  7  8
        for( int i=0; i<BoardSize; i++) {

            if( board[ i] == 0) {
                // Add the elements adjacent to it on all 4 sides, ensuring on each side we don't go out of bounds.
                switch( i) {
                    case 0: possibleMoves.add( new Integer(1)); possibleMoves.add( new Integer(3)); break;
                    case 1: possibleMoves.add( new Integer(0)); possibleMoves.add( new Integer(2));
                        possibleMoves.add( new Integer(4)); break;
                    case 2: possibleMoves.add( new Integer(1)); possibleMoves.add( new Integer(5)); break;
                    case 3: possibleMoves.add( new Integer(0)); possibleMoves.add( new Integer(4));
                        possibleMoves.add( new Integer(6)); break;
                    case 4: possibleMoves.add( new Integer(1)); possibleMoves.add( new Integer(5));
                        possibleMoves.add( new Integer(7)); possibleMoves.add( new Integer(3)); break;
                    case 5: possibleMoves.add( new Integer(2)); possibleMoves.add( new Integer(8));
                        possibleMoves.add( new Integer(4)); break;
                    case 6: possibleMoves.add( new Integer(3)); possibleMoves.add( new Integer(7)); break;
                    case 7: possibleMoves.add( new Integer(4)); possibleMoves.add( new Integer(8));
                        possibleMoves.add( new Integer(6)); break;
                    case 8: possibleMoves.add( new Integer(5)); possibleMoves.add( new Integer(7)); break;
                }
            }//end if( board...

        }//end for( int i...
        return possibleMoves;
    }//end findPossibleMoves()


    //-----------------------------------------------------------------------------------------
    // Convert output value to an int.  This is really only needed to convert 0 to ' '
    private char outputChar( int n)
    {
        char returnValue = ' ';
        if( board[n]!=0) {
            returnValue = (char)('0' + board[n]);
        }
        return returnValue;
    }


    //-----------------------------------------------------------------------------------------
    // Return true if board is in its final configuration, with heuristic value of 0, since each
    // piece is now it is final position.
    public boolean isFinished()
    {
        return (this.heuristicValue < 2);
    }


    //-----------------------------------------------------------------------------------------
    public int compareTo( Board oldBoard) {
        return heuristicValue - oldBoard.heuristicValue;
    }


    //-----------------------------------------------------------------------------------------
    // Equality comparison, using each board position
    public boolean equals( Board oldBoard) {
        boolean boardsAreTheSame = true;
        for(int i=0; i<BoardSize; i++) {
            if( board[ i] != oldBoard.board[ i]) {
                boardsAreTheSame = false;
                break;
            }
        }
        return boardsAreTheSame;
    }//end equals()


    //-----------------------------------------------------------------------------------------
    // hash function for use in HashMap, which is the sum of all the digits times their place values
    public int hashCode()
    {
        int hashValue = 1;
        for(int i=0; i<BoardSize; i++) {
            hashValue += board[ i] * Math.pow(10, i);
        }

        return hashValue;
    }


    //-----------------------------------------------------------------------------------------
    public String toString() {
        return( "   " + outputChar( 0) + " " + outputChar( 1) + " " + outputChar( 2) + "\n" +
                "   " + outputChar( 3) + " " + outputChar( 4) + " " + outputChar( 5) + "\n" +
                "   " + outputChar( 6) + " " + outputChar( 7) + " " + outputChar( 8) + "\n" +
                "Heuristic value: " + heuristicValue + "\n");
    }

}//end class Board
