package de.gameoflife.connection.rabbitmq;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.gof.communication.DeleteInfo;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author Daniel
 */
public class RabbitMQConnection {
    
    private ConnectionFactory factory;
    private Connection con;
    private Channel channel;
    
    
    public void init(){
        factory= new ConnectionFactory();
        factory.setUsername("vsys");
        factory.setPassword("vsys");
        factory.setVirtualHost("vsyshost");
        factory.setHost("143.93.91.73");
        factory.setPort(5672);

        try {
            con = factory.newConnection();
            if(con.isOpen()){
                System.out.println("Connected");
            } else{
                System.out.println("Not Connected");
            }
        } catch (IOException ex) {
            Logger.getLogger(RabbitMQConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void closeConnection(){
        try {
            channel.close();
            con.close();
        } catch (IOException ex) {
            Logger.getLogger(RabbitMQConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteUser(final int userId){
        try {
            DeleteInfo delInfo = new DeleteInfo(userId);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);   
                out.writeObject(delInfo);
                byte[] delInfo_byte = bos.toByteArray();
              
                channel = con.createChannel();
                
                channel.basicPublish( "VisuExchange", "", null, delInfo_byte);
            } finally {
              try {
                if (out != null) {
                  out.close();
                }
              } catch (IOException ex) {
                // ignore close exception
              }
              try {
                bos.close();
              } catch (IOException ex) {
                // ignore close exception
              }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(RabbitMQConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void deleteGame(final int userId, final int gameId){        
        try {
            DeleteInfo delInfo = new DeleteInfo(userId, gameId);
            
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutput out = null;
            try {
                out = new ObjectOutputStream(bos);   
                out.writeObject(delInfo);
                byte[] delInfo_byte = bos.toByteArray();
              
                channel = con.createChannel();
                
                channel.basicPublish( "VisuExchange", "", null, delInfo_byte);
            } finally {
              try {
                if (out != null) {
                  out.close();
                }
              } catch (IOException ex) {
                // ignore close exception
              }
              try {
                bos.close();
              } catch (IOException ex) {
                // ignore close exception
              }
            }
            
        } catch (IOException ex) {
            Logger.getLogger(RabbitMQConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
}
