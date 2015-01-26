package de.gameoflife.application;

import de.gameoflife.connection.rmi.GameHandler;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import rmi.data.GameUI;
import rmi.data.rules.Evaluable;
import rmi.data.rules.NumericRule;

/**
 *
 * @author JScholz
 *
 * @version 2014-12-11-1
 *
 */
public class GameTab implements Initializable {

    @FXML
    private StackPane pane;
    @FXML
    private BorderPane content;
    @FXML
    private StackPane barpane;
    @FXML
    private AnchorPane tabContent;
    @FXML
    private AnchorPane analysisContent;

    private GameCanvas canvas;
    private Parent editorBar;
    private Parent playBar;
    private Parent deathRulesParent;
    private EditorBarController editorController;
    private PlayBarController playController;
    private GameUI game;
    private GameOfLifeController parentcontroller;
    private DeathRulesController deathRulesController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        assert (pane == null);
        assert (content == null);
        assert (barpane == null);
        assert (tabContent == null);

        try {

            FXMLLoader deathRulesLoader = new FXMLLoader(getClass().getResource("FXML/DeathRules.fxml"));

            deathRulesParent = (Parent) deathRulesLoader.load();
            deathRulesParent.setVisible(false);

            deathRulesController = deathRulesLoader.getController();
            deathRulesController.setParent(this);           
            
            deathRulesController.closeActionEvent((ActionEvent event) -> {

                deathRulesParent.toBack();
                deathRulesParent.setVisible(false);

                tabContent.toFront();
                tabContent.setDisable(false);

            });

            FXMLLoader editorBarLoader = new FXMLLoader(getClass().getResource("FXML/EditorBar.fxml"));

            editorBar = editorBarLoader.load();

            editorController = editorBarLoader.getController();
            editorController.setParent(this);
            editorController.doneActionEvent((ActionEvent event) -> {

                boolean successful = GameHandler.getInstance().saveGame(game.getGameId());

                System.out.println("Save successful: " + successful);
                System.out.println(game);
                System.out.println(game.getBirthRules());
                System.out.println(game.getDeathRules());
                playBar.toFront();
                editorBar.toBack();

            });

            FXMLLoader playBarLoader = new FXMLLoader(getClass().getResource("FXML/PlayBar.fxml"));

            playBar = playBarLoader.load();

            playController = playBarLoader.getController();
            playController.setParent(this);
            playController.editorActionEvent((ActionEvent event) -> {

                if (game.isHistoryAvailable()) {

                    int copyGameId = GameHandler.getInstance().copyGame(game.getGameId());

                    try {
                        parentcontroller.createTab(copyGameId);
                    } catch (IOException ex) {
                        Logger.getLogger(GameTab.class.getName()).log(Level.SEVERE, null, ex);
                    }

                } else {
                    playBar.toBack();
                    editorBar.toFront();
                }

            });

            barpane.getChildren().addAll(playBar, editorBar);

            pane.getChildren().add(deathRulesParent);

        } catch (IOException ex) {
            Logger.getLogger(GameTab.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getGameId() {
        return game.getGameId();
    }

    public GameCanvas getCanvas() {
        return canvas;
    }

    public GameUI getGame() {
        return game;
    }

    public void setCellColor(Color color) {

        canvas.setCellColor(color);

    }

    public void setCanvasWidth(int width) {

        boolean[][] generation = game.getStartGen();

        generation = new boolean[generation.length][width];

        game.setStartGen(generation);

        canvas.setGridWidth(width);

    }

    public void setCanvasHeight(int height) {

        boolean[][] generation = game.getStartGen();

        generation = new boolean[height][generation[0].length];

        game.setStartGen(generation);

        canvas.setGridHeight(height);

    }

    public void setCanvasCellSize(int size) {

        canvas.setCellSize(size);

    }

    public void initCanvas(GameUI game) {

        this.game = game;

        editorController.setBorderOverflow(game.getBorderOverflow());
        editorController.setCellHeight(game.getStartGen().length);
        editorController.setCellWidth(game.getStartGen()[0].length);

        //3, 3, 20
        canvas = new GameCanvas(
                game.getStartGen()[0].length,
                game.getStartGen().length,
                20
        );

        canvas.drawGrid();
        canvas.setGeneration(game.getStartGen());

        content.setCenter(canvas);

        if (game.isHistoryAvailable()) {

            playBar.toFront();
            editorBar.toBack();

        } else {

            playBar.toBack();
            editorBar.toFront();

        }

    }

    public void showDeathRules() {
        
        deathRulesController.addItems( game.getDeathRules() );

        tabContent.toBack();
        tabContent.setDisable(true);

        deathRulesParent.toFront();
        deathRulesParent.setVisible(true);

    }

    public void parentController(GameOfLifeController controller) {
        parentcontroller = controller;
    }

}
