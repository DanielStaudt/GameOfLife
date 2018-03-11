package de.gameoflife.connection.rmi;

import de.gameoflife.application.GameCanvas;
import de.gameoflife.application.User;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.concurrent.Task;
import queue.data.Generation;
import rmi.data.GameUI;
import rmi.data.rules.Evaluable;
import rmi.interfaces.IRemoteUI_Server;

/**
 *
 * @author Daniel
 */
public class GameHandler implements IGameConfiguration {

    //Contains all games which the user has opened in his GUI. 
    //For performance it creates an index over the game ids.
    private HashMap<Integer, GameUI> gameList = null;

    //Handles the communication between the RuleEditor and UI.
    //private IRemoteRuleEditor ruleEditor = null;
    private RuleEditorHandler ruleEditorHandler = null;
    
    //Handles the communication between the Engine and UI.
    private EngineHandler engineHandler = null;
    //private IGameEngineServer gameEngine = null;

    //Handles the communication between the Analyse and UI.
    //private IAnalysis analysis = null;
    private AnalysisHandler analysisHandler = null;

    //private IRemoteUI_Server uiServer = null;

    private static GameHandler instance;

    private UpdateTask canvasUpdateTask = null;
    private Thread updateThread = null;

    private GameHandler() {
        gameList = new HashMap<>();
        ruleEditorHandler = new RuleEditorHandler ();
        engineHandler = new EngineHandler ();
        analysisHandler = new AnalysisHandler ();
    }

    public static void init() throws NotBoundException, MalformedURLException, RemoteException {

        if (instance == null) {

            instance = new GameHandler();

            instance.establishConnection();

        }

    }

    public static GameHandler getInstance() {

        return instance;

    }

    public static void close() {

        if (instance != null) {

            instance.closeConnection();

            instance = null;

        }

    }
    
    public IConnectionRuleEditor getRuleEditor() {
        return ruleEditorHandler;
    }
    
    public IConnectionGameEngine getEngine() {
        return engineHandler;
    }
    
    public IConnectionAnalysis getAnalysis() {
        return analysisHandler;
    }

    public boolean establishConnection() throws NotBoundException, MalformedURLException, RemoteException {
        //TODO fehler abfangen
        //establishConnectionRuleEditor();

        //establishConnectionGameEngine();
        //establishConnectionAnalysis();
        //establishConnectionUIServer();
        return true;
    }

    public boolean closeConnection() {
        //closeConnectionRuleEditor();
        ruleEditorHandler.closeConnectionRuleEditor ();
        engineHandler.closeConnectionGameEngine ();
        analysisHandler.closeConnectionAnalysis ();
        //closeConnectionGameEngine();
        //closeConnectionAnalysis();
        //closeConnectionUIServer();
        return true;
    }

    public GameUI getGame(int gameId) {

        return gameList.get(gameId);

    }

    public GameUI getGame(String gamename) {

        Iterator<GameUI> it = gameList.values().iterator();

        GameUI game;

        while (it.hasNext()) {

            game = it.next();

            if (game.getGameName().equals(gamename)) {
                return game;
            }

        }

        return null;

    }

    /*
     * <---------------------------RuleEditor part ---------------------------->
     */
/*    @Override
    public boolean establishConnectionRuleEditor() throws NotBoundException, MalformedURLException, RemoteException {
        try {
            ruleEditor = (IRemoteRuleEditor) Naming.lookup("rmi://143.93.91.72/" + IRemoteRuleEditor.SERVICENAME);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {

            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
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
        //try {
        //    if (ruleEditor != null) {
                GameUI g = new GameUI (userId, 1, name);//ruleEditor.generateNewGame(userId, name);
                System.out.println( g.isHistoryAvailable() );
                gameList.put(g.getGameId(), g);
          } else {
                //TODO: throw error that the connection must be established first
                System.err.println("rule editor null");
                return false;
            }
        } catch (RemoteException ex) {
            //TODO
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public int copyGame(final int gameId) {
        try {

            if (ruleEditor == null) {
                return -1;
            }

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
            if (ruleEditor != null) {

                GameUI g = gameList.get(gameId);

                ruleEditor.saveGame(g);
            }
            return true;
        } catch (RemoteException ex) {
            //TODO
            return false;
        }
    }

    @Override
    public boolean loadGame(final int userId, final int gameId) {
        try {

            if (ruleEditor == null) {
                return false;
            }

            GameUI g = ruleEditor.getGameObject(userId, gameId);

            gameList.put(gameId, g);
        } catch (RemoteException ex) {
            //TODO
            return false;
        }
        return true;
    }

    @Override
    public ObservableList<GameUI> getGameList(final int userId) {
        try {

            if (ruleEditor == null) {
                return FXCollections.observableArrayList();
            }

            gameList.clear();
            
            List<GameUI> games = ruleEditor.getUserGames(userId);

            Iterator<GameUI> it = games.iterator();
            GameUI game;

            while (it.hasNext()) {

                game = it.next();
                
                gameList.put(game.getGameId(), game);

            }

            return FXCollections.observableArrayList(gameList.values());

        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }*/

    /*
     * <-----------------------------Engine part ------------------------------>
     */
/*    @Override
    public boolean establishConnectionGameEngine() throws NotBoundException, MalformedURLException, RemoteException {
        try {

            gameEngine = (IGameEngineServer) Naming.lookup(IGameEngineServer.FULLSERVICEIDENTIFIER);

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {

            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return true;
    }

    @Override
    public boolean closeConnectionGameEngine() {
        gameEngine = null;
        return true;
    }

    @Override
    public boolean startEngine(final int userID, final int gameID) {
        try {

            if (gameEngine == null) {
                return false;
            }

            return gameEngine.sendIDsToEngine(userID, gameID);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public boolean stopEngine(final int userID, final int gameID) {
        try {

            if (gameEngine == null) {
                return false;
            }

            return gameEngine.stop(userID, gameID);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    @Override
    public Generation getGeneration(final int userID, final int gameID, final int genID) {
        try {

            if (gameEngine == null) {
                return null;
            }

            return gameEngine.getGeneration(userID, gameID, genID);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }*/
    /*
     * <----------------------------Analysis part ----------------------------->
     */

 /*   @Override
    public boolean establishConnectionAnalysis() throws NotBoundException, MalformedURLException, RemoteException {
        try {

            analysis = (IAnalysis) Naming.lookup(IAnalysis.RMI_ADDR);

        } catch (NotBoundException | MalformedURLException | RemoteException ex) {

            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return true;
    }

    @Override
    public boolean closeConnectionAnalysis() {
        analysis = null;
        return true;
    }

    @Override
    public void startAnalysis(int gameID, int genID) {
        try {

            if (analysis != null) {
                analysis.startAnalysis(gameList.get(gameID).getUserId(), gameID, genID);
            }

        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/

    /*
     * <-----------------------UI Server part ------------------------->
     */
/*    public boolean establishConnectionUIServer() throws NotBoundException, MalformedURLException, RemoteException {
        try {
            uiServer = (IRemoteUI_Server) Naming.lookup("rmi://143.93.91.71:1098/RemoteUIBackendIntern");
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {

            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
            throw ex;
        }
        return true;
    }

    public void closeConnectionUIServer() {
        uiServer = null;
    }

    @Override
    public Generation getNextGeneration(final int userId, final int gameId) {
        try {
            if (uiServer == null) {
                return null;
            }
            return uiServer.getNextGeneration(userId, gameId);
        } catch (RemoteException ex) {
            Logger.getLogger(GameHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }*/

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
    public int getStartGenHeight(final int gameId) {
        return gameList.get(gameId).getStartGen().length;
    }

    @Override
    public int getStartGenWidth(final int gameId) {
        return gameList.get(gameId).getStartGen()[0].length;
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
    public void setNowModified(final int gameId) {
        gameList.get(gameId).setNowModified();
    }

    @Override
    public int getUserId() {
        Collection<GameUI> collection = gameList.values();
        Iterator<GameUI> it = collection.iterator();
        if (it.hasNext()) {
            return it.next().getUserId();
        } else {
            return -1;
        }
    }

    @Override
    public Date getCreationDate(final int gameId) {
        return gameList.get(gameId).getCreationDate();
    }

    @Override
    public Date getModifiedDate(final int gameId) {
        return gameList.get(gameId).getModifiedDate();
    }

    @Override
    public String getName(final int gameId) {
        return gameList.get(gameId).getGameName();
    }

    @Override
    public boolean[][] getStartGen(final int gameId) {
        return gameList.get(gameId).getStartGen();
    }

    @Override
    public List<Evaluable> getDeathRules(final int gameId) {
        return gameList.get(gameId).getDeathRules();
    }

    @Override
    public List<Evaluable> getBirthRules(final int gameId) {
        return gameList.get(gameId).getBirthRules();
    }

    @Override
    public boolean getBorderOverflow(final int gameId) {
        return gameList.get(gameId).getBorderOverflow();
    }

    @Override
    public boolean isHistoryAvailable(final int gameId) {
        return gameList.get(gameId).isHistoryAvailable();
    }

    @Override
    public boolean isAnalysisAvailable(final int gameId) {
        return gameList.get(gameId).isAnalysisAvailable();
    }

    @Override
    public String toString(final int gameId) {
        return gameList.get(gameId).toString();
    }

    @Override
    public int getGameId(final String gameName) {

        Iterator<GameUI> it = gameList.values().iterator();

        GameUI game;

        while (it.hasNext()) {

            game = it.next();

            if (game.getGameName().equals(gameName)) {
                return game.getGameId();
            }

        }

        return -1;
    }

    @Override
    public void removeDeathRule(final int gameId, int index) {

        getDeathRules(gameId).remove(index);

    }

    @Override
    public void removeBirthRule(final int gameId, int index) {

        getBirthRules(gameId).remove(index);

    }

    /*
     * <-----------------------Sonstiges part ------------------------->
     */
    public boolean startGame(final int gameId, DoubleProperty sliderProperty, GameCanvas canvas, boolean showCellAge) {

        User u = User.getInstance();

        if (canvasUpdateTask == null) {
            
            System.out.println("es läuft noch kein updateTask");
            boolean successful = true;//startEngine(u.getId(), gameId);
            System.out.println("succ " + successful);
            
            if (successful) {
                System.out.println("StartGame " + gameId);
                System.out.println("Engine gestartet");
                getGame(gameId).setHistoryAvailable(true);
                createUpdateTask(gameId, sliderProperty, canvas, showCellAge);
                return true;
                
            } else {
                System.out.println("Smt went wrong");
            }

        } else {

            if (canvasUpdateTask.isRunning()) {
                System.out.println("stopppen!!");
                stopGame(gameId);

                createUpdateTask(gameId, sliderProperty, canvas, showCellAge);
                return true;
            }

        }
        return false;
    }

    public void stopCurrentRunningGame() {

        if (canvasUpdateTask != null && canvasUpdateTask.isRunning()) {

            stopGame(canvasUpdateTask.getGameId());

        }

    }

    public void stopGame(final int gameId) {

        if (canvasUpdateTask != null && canvasUpdateTask.isRunning()) {

            boolean b = true;//stopEngine(User.getInstance().getId(), gameId);

            System.out.println( b );
            
            canvasUpdateTask.cancel();

            canvasUpdateTask = null;

        }

    }

    public boolean gameRunning() {
        return canvasUpdateTask != null && canvasUpdateTask.isRunning();
    }

    private void createUpdateTask(final int gameId, DoubleProperty sliderProperty, GameCanvas canvas, boolean showCellAge) {

        canvasUpdateTask = new UpdateTask(gameId, canvas, sliderProperty, showCellAge);

        updateThread = new Thread(canvasUpdateTask);
        updateThread.setName("UpdateThread");
        updateThread.start();

    }

    private class UpdateTask extends Task<Void> {

        private final GameHandler handler;
        private final int userId;
        private final int gameId;
        private final GameCanvas canvas;
        private final DoubleProperty sliderProperty;
        private final boolean showCellAge;

        public UpdateTask(int gameId, GameCanvas canvas, DoubleProperty sliderProperty, boolean showCellAge) {
            handler = GameHandler.getInstance();
            userId = User.getInstance().getId();
            this.gameId = gameId;
            this.canvas = canvas;
            this.sliderProperty = sliderProperty;
            this.showCellAge = showCellAge;
        }

        public int getGameId() {
            return gameId;
        }

        @Override
        protected Void call() throws Exception {
            System.out.println("start Game");
            //Generation gen;
            long time;
            //int currentGen = 0;

            while (!isCancelled()) {

                try {
                    time = 60 * 1000 / (sliderProperty != null ? sliderProperty.longValue() : 60L);
                    //System.out.println(time);
                    Thread.sleep(time);
                } catch (InterruptedException interrupted) {
                }

                final Generation gen = null;//handler.getNextGeneration(userId, gameId);

                if (gen != null) {

                    //++currentGen;

                    //TODO wird ein Latch wirklich benoetigt?
                    final CountDownLatch doneLatch = new CountDownLatch(1);

                    //final int value = currentGen;
                    
                    Platform.runLater(new Runnable() {

                        //final int value = tmp;

                        @Override
                        public void run() {
                            //System.out.println( "gameid: " + gameId);
                            //Generation g = handler.getGeneration(userId, gameId, value);

                            //if (g != null) {
                                //System.out.println(gen.getGenID() + " gen " + gen.getGameID());
                                canvas.drawCells(gen, showCellAge);
                                canvas.setCurrentGeneration(gen.getGenID());
                                doneLatch.countDown();
                            //}
                        }
                    });

                    doneLatch.await();

                } else {
                    System.out.println("gen ist null");
                }

            }
            System.out.println("stopped");
            return null;

        }

    }
    
    public void clearGameList() {
        gameList.clear();
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
    /*@Override
    public String getAnalyseData(int userId, int gameId) throws RemoteException {
        return uiServer.getAnalyseData(userId, gameId);
    }*/
}
