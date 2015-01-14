/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.gameoflife.connection.rmi;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import queue.data.Generation;
import rmi.data.GameUI;
import rmi.data.rules.Evaluable;
import rmi.interfaces.IAnalysis;
import rmi.interfaces.IGameEngineServer;
import rmi.interfaces.IRemoteRuleEditor;


/**
 *
 * @author Daniel
 */
public class GameHandler implements IGameConfiguration, IConnectionRuleEditor, 
        IConnectionGameEngine, IConnectionAnalysis{
    
    //Contains all games which the user has opened in his GUI. 
    //For performance it creates an index over the game ids.
    private HashMap<Integer,GameUI> gameList = null;
    
    //Handles the communication between the RuleEditor and UI.
    private IRemoteRuleEditor ruleEditor = null;
    
    //Handles the communication between the Engine and UI.
    private IGameEngineServer gameEngine = null;
    
    //Handles the communication between the Analyse and UI.
    private IAnalysis analysis = null;
    
    public GameHandler(){
        gameList = new HashMap<>();
    }
    
    public boolean establishConnection(){
        //TODO fehler abfangen
        establishConnectionRuleEditor();
        establishConnectionGameEngine();
        establishConnectionAnalysis();
        return true;
    }
    
    public boolean closeConnection(){
        closeConnectionRuleEditor();
        closeConnectionGameEngine();
        closeConnectionAnalysis();
        return true;
    }

    /*
     * <---------------------------RuleEditor part ---------------------------->
     */
    
    @Override
    public boolean establishConnectionRuleEditor() {
        try {
            ruleEditor = (IRemoteRuleEditor) Naming.lookup("rmi://143.93.91.72/RuleEditor");
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean closeConnectionRuleEditor() {
        ruleEditor = null;
        return true;
    }

    @Override
    public boolean generateNewGame(int userId, String name) {
        try {
            if (ruleEditor != null){
                GameUI game = ruleEditor.generateNewGame(userId, name);
                gameList.put(game.getGameId(), game);
            } else {
                //TODO: throw error that the connection must be established first
                
                return false;
            }
        } catch (RemoteException ex) {
            //TODO
            return false;
        }
        return true;
    }
    
    @Override
    public int copyGame(final int gameId){
        try {
            GameUI temp = ruleEditor.copyGame(gameList.get(gameId).getUserId(), gameId);
            gameList.put(temp.getGameId(), temp);
            return temp.getGameId();
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return -1;
        }
    }
    
    @Override
    public boolean saveGame(final int gameId) {
        try {
            ruleEditor.saveGame(gameList.get(gameId));
            return true;
        } catch (RemoteException ex) {
            //TODO
            return false;
        }
    }

    @Override
    public boolean loadGame(final int userId, final int gameId) {
        try {
            gameList.put(gameId, ruleEditor.getGameObject(userId,gameId));
        } catch (RemoteException ex) {
            //TODO
            return false;
        }
        return true;
    }

    @Override
    public List<Integer> getGameList(final int userId) {
        try {
            List<Integer> gameIds = new ArrayList<>();
            List<GameUI> games = ruleEditor.getUserGames(userId);
            
            Iterator<GameUI> it = games.iterator();
            while(it.hasNext()){
                gameIds.add(it.next().getGameId());
            }
            
            return gameIds;
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    /*
     * <-----------------------------Engine part ------------------------------>
     */
    
    @Override
    public boolean establishConnectionGameEngine(){
         try {
            //TODO: Adresse anpassen
            gameEngine = (IGameEngineServer) Naming.lookup("rmi://143.93.91.72/Engine");
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean closeConnectionGameEngine(){
        gameEngine=null;
        return true;
    }
    
    @Override
    public boolean startEngine(final int userID, final int gameID){
        try {
            return gameEngine.sendIDsToEngine(userID, gameID);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    @Override
    public boolean stopEngine(final int userID, final int gameID){
         try {
            return gameEngine.stop(userID, gameID);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }
    
    @Override
    public Generation getGeneration(final int userID, final int gameID, final int genID){
        try {
            return gameEngine.getGeneration(userID, gameID, genID);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
    /*
     * <----------------------------Analysis part ----------------------------->
     */
    
    @Override
    public boolean establishConnectionAnalysis(){
        try {
            analysis = (IAnalysis) Naming.lookup("rmi://143.93.91.72/RuleEditor");
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }
    
    @Override
    public boolean closeConnectionAnalysis(){
        analysis=null;
        return true;
    }
    
    @Override
    public void startAnalysis(int gameID){
        try {
            analysis.startAnalysis(gameList.get(gameID).getUserId(), gameID);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /*
     * <-----------------------GameConfiguration part ------------------------->
     */
    
    @Override
    public void setGameName(final int gameId, String gameName) {
        gameList.get(gameId).setGameName(gameName);
    }

    @Override
    public void setStartGen(final int gameId, boolean[][] startGen) {
        gameList.get(gameId).setStartGen(startGen);
    }

    @Override
    public void addDeathRule(final int gameId, Evaluable rule) {
        gameList.get(gameId).addDeathRule(rule);
    }

    @Override
    public void addBirthRule(final int gameId, Evaluable rule) {
        gameList.get(gameId).addBirthRule(rule);
    }

    @Override
    public void setBorderOverflow(final int gameId, boolean borderOverflow) {
        gameList.get(gameId).setBorderOverflow(borderOverflow);
    }
    
    @Override
    public void setNowModified(final int gameId){
        gameList.get(gameId).setNowModified();
    }
    
    @Override
    public int getUserId(){
        Collection<GameUI> collection = gameList.values();
        Iterator<GameUI> it = collection.iterator();
        if (it.hasNext()){
            return it.next().getUserId();
        } else {
            return -1;
        }
    }
    
    @Override
    public boolean[][] getGeneration(final int gameId){
        return gameList.get(gameId).getGeneration();
    }
    
    @Override
    public Date getCreationDate(final int gameId){
        return gameList.get(gameId).getCreationDate();
    }
    
    @Override
    public Date getModifiedDate(final int gameId){
        return gameList.get(gameId).getModifiedDate();
    }
    
    @Override
    public String getName(final int gameId){
        return gameList.get(gameId).getGameName();
    }
    
    @Override
    public boolean[][] getStartGen(final int gameId){
        return gameList.get(gameId).getStartGen();
    }
    
    @Override
    public List<Evaluable> getDeathRules(final int gameId){
        return gameList.get(gameId).getDeathRules();
    }
    
    @Override
    public List<Evaluable> getBirthRules(final int gameId){
        return gameList.get(gameId).getBirthRules();
    }
    
    @Override
    public boolean getBorderOverflow(final int gameId){
        return gameList.get(gameId).getBorderOverflow();
    }
    
    @Override
    public boolean isHistoryAvailable(final int gameId){
        return gameList.get(gameId).isHistoryAvailable();
    }
    
    @Override
    public boolean isAnalysisAvailable(final int gameId){
        return gameList.get(gameId).isAnalysisAvailable();
    }
    
    @Override
    public String toString(final int gameId){
        return gameList.get(gameId).toString();
    }
    
     /*public static void main(String[] args) {

        
                // 2. Save modified GameUI Object back
                final RulePattern oneBirthrule = new RulePattern(new int[]{1,1,1,0,0,1,1,1});
                final NumericRule oneDeathrule = new NumericRule();
                oneDeathrule.setTriggerAtNumberOfNeighbours(5, true); //Death at 5 alive neigbours
                oneDeathrule.setTriggerAtNumberOfNeighbours(4, true); //Death at 4 alive neigbours
                game.setBorderOverflow(true);
                game.addBirthRule(oneBirthrule);
                game.addDeathRule(oneDeathrule);
                final boolean[][] field = new boolean[][] {
                                new boolean[]{false,false,false,false,false,false,false,false,false},
                                new boolean[]{false,true ,true ,true ,false,true ,false,true ,false},
                                new boolean[]{false,true ,false,false,false,true ,false,true ,false},
                                new boolean[]{false,true ,true ,true ,false,true ,true ,true ,false},
                                new boolean[]{false,true ,false,false,false,true ,false,true ,false},
                                new boolean[]{false,true ,false,false,false,true ,false,true ,false},
                                new boolean[]{false,false,false,false,false,false,false,false,false},
                        };
                game.setStartGen(field);
         
    }*/
}
