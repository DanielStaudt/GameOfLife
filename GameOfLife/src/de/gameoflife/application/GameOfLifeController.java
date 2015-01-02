
package de.gameoflife.application;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

/**
 * FXML Controller class
 *
 * @author JScholz
 * 
 * @version 2014-12-11-1
 * 
 */
public final class GameOfLifeController {

    @FXML private TabPane tabPane;
    @FXML private Label username;
    private GameOfLife application;

    @FXML
    public void logout(ActionEvent event) throws IOException {
        
        tabPane.getTabs().clear();
        
        application.showLoginScreen();
        
    }
    
    @FXML
    public void newGame(ActionEvent event) throws IOException {
        application.newGame();
    }
    
    @FXML
    public void loadGame(ActionEvent event) throws IOException {
        
    }
        
    @FXML
    public void deleteGame(ActionEvent event) throws IOException {
        
    }
    
    public void setRootApplication( GameOfLife rootApp ) {
        application = rootApp;
    }
    
    public void setUsername( String name ) {
        username.setText(name);
    }
    
    public void createTab( String tabname ) throws IOException {
        
        Parent tabContent = (Parent) FXMLLoader.load( getClass().getResource("FXML/Tab.fxml") );
        
        Tab newTab = new Tab();
        newTab.setText(tabname);
        
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
        
    }
    
}
