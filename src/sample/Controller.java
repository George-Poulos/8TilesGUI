package sample;

/**
 * This file holds the GUI portion of te 8 tiles game. It initializes buttons and sets
 * handlers for specific events.
 *
 * @author : George Poulos & Dale Reed
 */


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ArrayList<Button> buttonGrid;
    @FXML
    private Button startButton;
    @FXML
    private ChoiceBox choicebox1;
    @FXML
    private Button autoButton;
    @FXML
    private Button exitButton;
    @FXML
    private Label counterLabel;
    @FXML
    private Label heuristicLabel;

    private Board guiBoard;
    private int moveNumber;
    private char [] chooseBoardString;
    private boolean userChoose = false;
    private int countChoose = 0;

    @Override
    /**
     * initialize javafx
     */
    public void initialize(URL location, ResourceBundle resources) {
        for (Button button : buttonGrid) {
            setGridHandler(button);
        }
        chooseBoardString = new char [9];
        setExitHandler();
        setAutoHandler();
        setChoiceBox1();
        setStartButton();
        moveNumber = 0;
    }

    /**
     * Checking if the user has already used the number on the grid
     * @param button - button being checked
     * @return - return true or false
     */
    private boolean isNumberUsed(Button button){
        String text = button.getText();
        for(int i = 0; i < 9; i++){
            if(text.equals(Integer.toString(i)) || text.equals(" ")){
                return true;
            }
        }
        return false;
    }

    /**
     * Setting board after configuration selected
     * @param button - button being set on the board by user
     */
    private void setBoard(Button button){
        if (!isNumberUsed(button)) {
            if (countChoose == 0) {
                button.setText(" ");
            } else {
                button.setText(Integer.toString(countChoose));
            }
            int index = buttonGrid.indexOf(button);
            chooseBoardString[index] = Integer.toString(countChoose).charAt(0);
            System.out.println(chooseBoardString[index] + " : " + index);

            countChoose++;
            if(countChoose > 8){
                userChoose = false;
                countChoose = 0;
                guiBoard = new Board(new String(chooseBoardString));
            }
        }
    }

    /**
     * Make a move on the gui
     * @param button - button to be moved
     */
    private void moveGUI(Button button){
        moveNumber++;
        int indexOf0 = guiBoard.movePiece(Integer.parseInt(button.getText()));
        System.out.println(guiBoard);
        String textOfPiece = button.getText();
        buttonGrid.get(indexOf0).setText(textOfPiece);
        button.setText(" ");
        heuristicLabel.setText("Heuristic : " + Integer.toString(guiBoard.heuristicValue));
        counterLabel.setText("Counter : " + moveNumber);
    }

    /**
     * Handler for the grid buttons
     * @param button - button to be setup in the button grid
     */
    private void setGridHandler(Button button){
        button.setOnMouseClicked(event -> {
            if (event.getClickCount() > 0) {
                if (userChoose) {
                    setBoard(button);
                }
                else{
                    ArrayList<Integer> possibleMoves = guiBoard.findPossibleMoves();
                    char pieceToMove = button.getText().charAt(0);
                    boolean pieceToMoveIsValid = pieceToMoveIsOnValidMovesList( guiBoard, pieceToMove - '0', possibleMoves);
                    System.out.println(guiBoard);
                    if(pieceToMoveIsValid){
                        moveGUI(button);
                        if(guiBoard.isFinished()){
                            solvableWindow();
                        }
                    }
                }
            }
        });
    }

    /**
     * Checking if a movie is valid
     * @param theBoard - board to be checked
     * @param pieceToMove - piece that wants to be moved
     * @param possibleMoves - list of possible moves
     * @return - return if the move is possible
     */
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

    /**
     * Exit button handler
     */
    private void setExitHandler(){
        exitButton.setOnMouseClicked(event -> {
            if (event.getClickCount() > 0) {
                Platform.exit();
                System.exit(0);
            }
        });
    }

    private void updateHeuristicAndCount(Board board, int count){
        heuristicLabel.setText("Heuristic : " + board.getHeuristicValue());
        counterLabel.setText("Count : " + count);

    }

    /**
     * play solved animation one step at a time
     * @param solver - TilesDriver variable
     */
    public void playAnimation(TilesDriver solver){
        int size = solver.theSearchTree.getSizeOfTree();
        solver.moveNumber = 0;
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(300), new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Node curr = solver.theSearchTree.getNextBoard();
                setBoardVisibility(false);
                boardToGUI(curr.theBoard);
                updateHeuristicAndCount(curr.theBoard, solver.moveNumber);
                solver.moveNumber++;
                setBoardVisibility(true);
            }
        }));
        timeline.setCycleCount(size);
        timeline.playFromStart();
    }

    /**
     * Set Button Visibility of the board
     * @param bool - boolean true = visible false = invisible
     */
    void setBoardVisibility(boolean bool) {
        for (int i = 0; i < 9; i++) {
            buttonGrid.get(i).setVisible(bool);
        }
    }

    /**
     * Display window when unsolvable board tries to be auto-solved
     */
    void unsolvableWindow(){
        Dialog dialog = new Dialog();
        dialog.setTitle("Unsolvable!");
        dialog.setContentText("This board is unsolvable, best board being displayed");
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE));
        try {
            dialog.showAndWait();
        } catch (Exception e) {
        }
    }

    /**
     * Displayed when board is solved
     */
    void solvableWindow(){
        Dialog dialog = new Dialog();
        dialog.setTitle("Solved!");
        dialog.setContentText("This board has been Solved!");
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Okay", ButtonBar.ButtonData.OK_DONE));
        try {
            dialog.showAndWait();
        } catch (Exception e) {
        }
    }

    /**
     * Auto Solver Handler
     */
    private void setAutoHandler(){
        autoButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TilesDriver solver = new TilesDriver();
                solver.theBoard = guiBoard;
                solver.bestBoard = guiBoard;
                solver.solvePuzzleAutomatically();
                solver.theSearchTree.setSolutionPath(solver.theSearchTree.currentNode);
                // Now use those pointers in the forwards direction to display the numbered solution path
                int moveNumber = 1;
                if(solver.theSearchTree.currentNode == null){
                    unsolvableWindow();
                    boardToGUI(solver.bestBoard);
                    guiBoard = solver.bestBoard;
                    return;
                }
                else {
                    playAnimation(solver);
                    solvableWindow();
                    guiBoard = solver.theBoard;
                }
            }
        });
    }


    /**
     * Initializing choicebox options
     */
    private void setChoiceBox1(){
        choicebox1.setItems(FXCollections.observableArrayList("Random Board", "ChooseBoard"));
        choicebox1.setTooltip(new Tooltip("Select a Mode"));
    }

    /**
     * Handler for the start Button
     */
    private void setStartButton(){
        for(Button button : buttonGrid)
            button.setText(" ");
        startButton.setOnMouseClicked(event -> {
            if (event.getClickCount() > 0) {
                if(choicebox1.getSelectionModel().getSelectedIndex() == 0){
                    System.out.println("Random Board");
                    guiBoard = new Board("");
                    paintBoard(false);
                }
                if(choicebox1.getSelectionModel().getSelectedIndex() == 1 && !userChoose){
                    paintBoard(true);
                    System.out.println("Choose Board");
                    userChoose = true;
                }
                moveNumber = 0;
                heuristicLabel.setText("Heuristic : " + Integer.toString(0));
                counterLabel.setText("Counter : " + Integer.toString(moveNumber));
            }
        });
    }

    /**
     * Takes int array and places it on GUI
     * @param board - board to be printed
     */
    private void boardToGUI(Board board){
        for(int i = 0; i < 9; i++){
            if(board.board[i] == 0){
                buttonGrid.get(i).setText(" ");
            }
            else
                buttonGrid.get(i).setText(Integer.toString(board.board[i]));
        }
    }

    /**
     * Sets board with a given configuration
     * @param clearBoard - boolean option to clear Board if its true
     */
    private void paintBoard(boolean clearBoard){
        for(int i = 0; i < 9; i++) {
            if (clearBoard) {
                buttonGrid.get(i).setText("");
            } else {
                String newText = Integer.toString(guiBoard.board[i]);
                if (newText.equals("0"))
                    buttonGrid.get(i).setText(" ");
                else
                    buttonGrid.get(i).setText(newText);
            }
        }
    }

}
