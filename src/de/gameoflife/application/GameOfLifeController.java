package de.gameoflife.application;

import de.gameoflife.connection.rmi.GameHandler;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author JScholz
 *
 * @version 2014-12-11-1 TDOO Stop alle spiele, wenn sich ausgeloggt wird!
 */
public final class GameOfLifeController implements Initializable {

    @FXML
    private TabPane tabPane;
    @FXML
    private Label username;

    private GameOfLife application;
    private final HashMap<Integer, Tab> gameOpen = new HashMap<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        tabPane.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Tab> ov, Tab t, Tab t1) -> {
            GameHandler.getInstance().stopCurrentRunningGame();
        });

    }

    @FXML
    public void logout(ActionEvent event) throws IOException {

        GameHandler.getInstance().stopCurrentRunningGame();

        tabPane.getTabs().clear();
        gameOpen.clear();

        application.showLoginScreen();

    }

    @FXML
    public void newGame(ActionEvent event) throws IOException {
        application.newGame();
    }

    @FXML
    public void loadGame(ActionEvent event) throws IOException {
        application.loadGame();
    }

    @FXML
    public void deleteGame(ActionEvent event) throws IOException {
        application.deleteGame();
    }

    public void setRootApplication(GameOfLife rootApp) {
        application = rootApp;
    }
    
    public void renameGame(int gameid) {
        application.renameGame(gameid);
    }

    public void setUsername(String name) {
        username.setText(name);
    }

    public void closeTab(int gameId) {

        tabPane.getTabs().remove(gameOpen.get(gameId));

    }
    
    public void renameSelectedTab(String newName) {
        
        Tab t = tabPane.getSelectionModel().getSelectedItem();
        t.setText(newName);
        
    }

    public boolean gameIsOpen(int gameId) {

        Iterator<Integer> it = gameOpen.keySet().iterator();

        while (it.hasNext()) {

            if (it.next() == gameId) {
                return true;
            }

        }

        return false;

    }

    public void createTab(String gamename) throws IOException {

        int gameId = GameHandler.getInstance().getGameId(gamename);

        if (gameId != -1) {
            createTab(gameId);
        }

    }

    public void createTab(int gameId) throws IOException {

        GameHandler gameHandler = GameHandler.getInstance();

        FXMLLoader tabContentLoader = new FXMLLoader(getClass().getResource("FXML/Tab.fxml"));

        Parent tabContent = (Parent) tabContentLoader.load();

        GameTab tabController = tabContentLoader.getController();
        tabController.initCanvas(gameId);
        tabController.parentController(this);

        if (gameHandler.isHistoryAvailable(gameId)) {
            tabController.showPlayBar();
        } else {
            tabController.showEditorBar();
        }

        Tab newTab = new Tab();
        newTab.setText(gameHandler.getName(gameId));

        newTab.setOnCloseRequest((Event event) -> {
            tabController.closing();
            gameOpen.remove(gameId);
        });

        AnchorPane pane = new AnchorPane();
        pane.minHeight(0.0);
        pane.minHeight(0.0);
        pane.prefHeight(180.0);
        pane.prefWidth(200.0);

        pane.getChildren().add(tabContent);

        AnchorPane.setLeftAnchor(tabContent, 0.0);
        AnchorPane.setRightAnchor(tabContent, 0.0);
        AnchorPane.setBottomAnchor(tabContent, 0.0);
        AnchorPane.setTopAnchor(tabContent, 0.0);

        newTab.setContent(pane);

        tabPane.getTabs().add(newTab);

        SingleSelectionModel<Tab> selectionModel = tabPane.getSelectionModel();
        selectionModel.select(newTab);

        gameOpen.put(gameId, newTab);

    }

}
