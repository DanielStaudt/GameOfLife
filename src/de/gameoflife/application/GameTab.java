package de.gameoflife.application;

import de.gameoflife.connection.rmi.GameHandler;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

/**
 *
 * @author JScholz
 *
 * @version 2014-12-11-1
 *
 */
public final class GameTab implements Initializable {

    @FXML
    private StackPane pane;
    @FXML
    private BorderPane content;
    @FXML
    private StackPane barpane;
    @FXML
    private AnchorPane tabContent;

    private GameCanvas canvas;
    private Parent editorBar;
    private Parent playBar;
    private Parent deathRulesParent;
    private Parent birthRulesParent;
    private Parent errorDialog;
    private Parent copyWarning;
    private EditorBarController editorController;
    private PlayBarController playController;
    private ErrorDialogController errorDialogController;
    private CopyGameDialogController copyWarningController;
    private int gameId;
    private GameOfLifeController parentcontroller;
    private DeathRulesController deathRulesController;
    private BirthRulesController birthRulesController;
    private final GameHandler gameHandler = GameHandler.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        assert (pane == null);
        assert (content == null);
        assert (barpane == null);
        assert (tabContent == null);

        try {

            FXMLLoader copyGameLoader = new FXMLLoader(getClass().getResource("FXML/CopyGameDialog.fxml"));

            copyWarning = (Parent) copyGameLoader.load();
            copyWarning.setVisible(false);

            copyWarningController = copyGameLoader.getController();
            copyWarningController.okClickEvent((ActionEvent event) -> {

                hideCopyWarningDialog();
                showEditorBar();

            });
            copyWarningController.cancelClickEvent((ActionEvent event) -> {

                hideCopyWarningDialog();

            });
            copyWarningController.copyGameClickEvent((ActionEvent event) -> {

                int copyGameId = gameHandler.copyGame(gameId);

                try {
                    hideCopyWarningDialog();
                    parentcontroller.createTab(copyGameId);
                } catch (IOException ex) {
                    Logger.getLogger(GameTab.class.getName()).log(Level.SEVERE, null, ex);
                }

            });

            FXMLLoader errorInfoLoader = new FXMLLoader(getClass().getResource("FXML/ErrorDialog.fxml"));

            errorDialog = (Parent) errorInfoLoader.load();
            errorDialog.setVisible(false);

            errorDialogController = errorInfoLoader.getController();
            errorDialogController.setErrorText("");
            errorDialogController.okClickEvent((ActionEvent event) -> {

                tabContent.toFront();
                tabContent.setDisable(false);

                errorDialog.setVisible(false);
                errorDialog.toBack();

            });

            FXMLLoader birthRuleLoader = new FXMLLoader(getClass().getResource("FXML/BirthRules.fxml"));

            birthRulesParent = (Parent) birthRuleLoader.load();
            birthRulesParent.setVisible(false);

            birthRulesController = birthRuleLoader.getController();
            birthRulesController.setParent(this);

            birthRulesController.closeActionEvent((ActionEvent event) -> {

                birthRulesParent.toBack();
                birthRulesParent.setVisible(false);
                birthRulesController.hidePattern();

                tabContent.toFront();
                tabContent.setDisable(false);

            });

            FXMLLoader deathRulesLoader = new FXMLLoader(getClass().getResource("FXML/DeathRules.fxml"));

            deathRulesParent = (Parent) deathRulesLoader.load();
            deathRulesParent.setVisible(false);

            deathRulesController = deathRulesLoader.getController();
            deathRulesController.setParent(this);

            deathRulesController.closeActionEvent((ActionEvent event) -> {

                deathRulesParent.toBack();
                deathRulesParent.setVisible(false);
                deathRulesController.hidePattern();

                tabContent.toFront();
                tabContent.setDisable(false);

            });

            FXMLLoader editorBarLoader = new FXMLLoader(getClass().getResource("FXML/EditorBar.fxml"));

            editorBar = editorBarLoader.load();

            editorController = editorBarLoader.getController();
            editorController.setParent(this);
            editorController.doneActionEvent((ActionEvent event) -> {

                //Wenn nicht gezeichnet worden ist, schauen ob geloescht wird!
                if (!editorController.isDrawing()) {
                    editorController.isErasing();
                }

                gameHandler.saveGame(gameId);

                showPlayBar();

            });

            FXMLLoader playBarLoader = new FXMLLoader(getClass().getResource("FXML/PlayBar.fxml"));

            playBar = playBarLoader.load();

            playController = playBarLoader.getController();
            playController.setParent(this);
            playController.editorActionEvent((ActionEvent event) -> {

                if (gameHandler.isHistoryAvailable(gameId)) {
                    showCopyWarningDialog();
                } else {
                    showEditorBar();
                }

            });

            barpane.getChildren().addAll(playBar, editorBar);

            pane.getChildren().addAll(deathRulesParent, birthRulesParent, errorDialog, copyWarning);

        } catch (IOException ex) {
            Logger.getLogger(GameTab.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int getGameId() {
        return gameId;
    }

    public GameCanvas getCanvas() {
        return canvas;
    }

    public void closing() {
        playController.close();
    }

    public void setCellColor(Color color) {

        canvas.setCellColor(color);

    }

    public void showCopyWarningDialog() {

        tabContent.toBack();
        tabContent.setDisable(true);

        copyWarning.setVisible(true);
        copyWarning.toFront();

    }

    public void hideCopyWarningDialog() {

        tabContent.toFront();
        tabContent.setDisable(false);

        copyWarning.setVisible(false);
        copyWarning.toBack();

    }

    public void showErrorDialog(String text) {

        errorDialogController.setErrorText(text);

        tabContent.toBack();
        tabContent.setDisable(true);

        errorDialog.setVisible(true);
        errorDialog.toFront();

    }

    public void setCanvasWidth(int width) {

        boolean[][] generation = gameHandler.getStartGen(gameId);

        generation = new boolean[generation.length][width];

        gameHandler.setStartGen(gameId, generation);

        canvas.setGridWidth(width);

    }

    public void setCanvasHeight(int height) {

        boolean[][] generation = gameHandler.getStartGen(gameId);

        generation = new boolean[height][generation[0].length];

        gameHandler.setStartGen(gameId, generation);

        canvas.setGridHeight(height);

    }

    public void setCanvasCellSize(int size) {

        canvas.setCellSize(size);

    }

    public void showPlayBar() {
        playBar.toFront();
        editorBar.toBack();
        playBar.setVisible(true);
        editorBar.setVisible(false);
    }

    public void showEditorBar() {
        playBar.toBack();
        editorBar.toFront();
        playBar.setVisible(false);
        editorBar.setVisible(true);
    }

    public void initCanvas(int gameId) {

        this.gameId = gameId;

        boolean[][] startGeneration = gameHandler.getStartGen(gameId);

        editorController.setBorderOverflow(gameHandler.getBorderOverflow(gameId));
        editorController.setCellHeight(startGeneration.length);
        editorController.setCellWidth(startGeneration[0].length);

        //3, 3, 20
        canvas = new GameCanvas(
                startGeneration[0].length,
                startGeneration.length,
                20,
                gameId
        );

        canvas.drawGrid();
        canvas.drawCells();
        //canvas.setGeneration(startGeneration);

        content.setCenter(canvas);

        playController.bindPropertyToCurrentGen(canvas.getCurrentGame().asString());

        playController.activateNextButton(!gameHandler.isHistoryAvailable(gameId));
    }

    public void showDeathRules() {

        deathRulesController.addItems(gameHandler.getDeathRules(gameId));

        tabContent.toBack();
        tabContent.setDisable(true);

        deathRulesParent.toFront();
        deathRulesParent.setVisible(true);

    }

    public void showBirthRules() {

        birthRulesController.addItems(gameHandler.getBirthRules(gameId));

        tabContent.toBack();
        tabContent.setDisable(true);

        birthRulesParent.toFront();
        birthRulesParent.setVisible(true);

    }

    public void parentController(GameOfLifeController controller) {
        parentcontroller = controller;
    }

    public void renameGame() {
        parentcontroller.renameGame(gameId);
    }

}
