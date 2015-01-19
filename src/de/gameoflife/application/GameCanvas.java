
package de.gameoflife.application;

import java.util.ArrayList;
import java.util.Iterator;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

/**
 *
 * @author JScholz
 * 
 * @version 2014-12-11-1 
 * 
 * TODO: Draw Color auswahl
 * TODO: Draw zeichnen rückgaengig machen
 * TODO: Draw mouse press und draw
 */
public class GameCanvas extends Group {
    
    protected final Canvas grid;
    protected final Canvas elements;
    protected int cellSize;
    protected int width;
    protected int height;
    protected ArrayList<Cell> cells = new ArrayList<>();
   
    public GameCanvas(int width, int height, int cellSize) {

        super();
        
        double screenWidth = GameOfLife.stageWidthProperty.get();
        double screenHeight = GameOfLife.stageHeightProperty.get();
        
        this.width = width * cellSize;
        this.height = height * cellSize;
        this.cellSize = cellSize;
        

        elements = new Canvas( this.width, this.height );
        grid = new Canvas( this.width, this.height );
        
        drawGrid();
        
        getChildren().addAll( grid, elements );
        
        
    }

    
    public void addListener() {
        setOnMouseClicked( new GameCanvasListener() );
    }
    
    public void removeListener() {
        setOnMouseClicked(null);
    }
    
    
    public void gridPosition( ScrollPane scrollpane ) {
    
        double h = scrollpane.getContent().getBoundsInLocal().getHeight();
        double y = (grid.getBoundsInParent().getMaxY() + 
                grid.getBoundsInParent().getMinY()) / 2.0;
        double v = scrollpane.getViewportBounds().getHeight();
        scrollpane.setVvalue(scrollpane.getVmax() * ((y - 0.5 * v) / (h - v)));
        
    }
    
    private void drawGrid() {
    
        GraphicsContext gc = grid.getGraphicsContext2D();
        
        for( int y=0; y < grid.getHeight(); y += cellSize ) {
        
            for( int x=0; x < grid.getWidth(); x += cellSize ) {
                
                gc.setFill(Color.WHITE);
                gc.setStroke(Color.BLACK);
                gc.strokeRect(x, y, cellSize, cellSize);
                
                cells.add( new Cell( x, y ) );
                
            } 
        
        }  
        
    }
    
    private void clearGrid() {
    
        cells.clear();
        
        GraphicsContext gc = grid.getGraphicsContext2D();
        
        gc.clearRect( 0, 0, width, height );
        
        gc = elements.getGraphicsContext2D();
        
        gc.clearRect( 0, 0, width, height );
    
    }
    
    public void setGridWidth( int newWidth ) {
        
        width = newWidth * cellSize;
        grid.setWidth(width);
        elements.setWidth(width);
        drawGrid();
        
    }
    
    public void setGridHeight( int newHeight ) {
    
        height = newHeight * cellSize;
        grid.setHeight(height);
        elements.setHeight(height);
        drawGrid();
    
    }
    
    public void setCellSize( int size ) {
    
        width = ( width / cellSize ) * size;
        height = ( height / cellSize ) * size;
        grid.setHeight(height);
        grid.setWidth(width);
        elements.setWidth(width);
        elements.setHeight(height);
        
        this.cellSize = size;
        clearGrid();
        drawGrid();
        
    }
    
    public int getGridWidth() {
        return width;
    }
    
    public int getGridHeight() {
        return height;
    }
    
    public Canvas getBackgroundCanvas() {
        return grid;
    }
    
    public Canvas getFrontendCanvas() {
        return elements;
    }
    
    private class GameCanvasListener implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            
            
            
            System.out.println("Hit");
            Iterator<Cell> it = cells.iterator();
            boolean found = false;
            Cell c = null;
            
            while( it.hasNext() && !found ) {
            
                c = it.next();
                
                if( c.hit( (int)event.getX(), (int)event.getY() ) ) {
                    
                    //Wenn bereits ein Cell schon befüllt werden ist,
                    //soll nichts passieren, daher c = null setzen.
                    if( !c.hasDrawing() ) c.setDrawing( true );
                    else c = null;
                    
                    found = true;
                    
                }
                
            }
            
            if( found && c != null ) {
                
                GraphicsContext gc = elements.getGraphicsContext2D();
                
                gc.setFill(Color.RED);
                gc.setStroke(Color.RED);
                gc.fillRect(c.getX(), c.getY(), cellSize, cellSize);
                System.out.println("hit: x: " + c.getX() + " y: " + c.getY() );
            }
            
        }


    }
    
    private class Cell {
        
        private final int x;
        private final int y;
        private boolean drawing = false;
        
        Cell( int x, int y ) {
            this.x = x;
            this.y = y;
        }
        
        int getX() {
            return x;
        }
        
        int getY() {
            return y;
        }
        
        boolean hit( int x, int y ) {
        
            return this.x <= x && x < (this.x + cellSize) && this.y <= y && y < ( this.y + cellSize );
            
        }
        
        void setDrawing( boolean b ) {
            this.drawing = b;
        }
    
        boolean hasDrawing() {
            return drawing;
        }
        
    }
    
}
