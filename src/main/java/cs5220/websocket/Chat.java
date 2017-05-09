package cs5220.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/chat/{name}")
public class Chat {

    private String name;

    private Session session;

    private static Set<Session> sessions = ConcurrentHashMap.newKeySet();

    private static final Logger logger = LoggerFactory.getLogger( Chat.class );

    @OnOpen
    public void onOpen( @PathParam("name") String name, Session session )
    {
        this.name = name;
        this.session = session;
        sessions.add( session );
        logger.info( name + " has joined." );
    }

    @OnClose
    public void onClose()
    {
        sessions.remove( session );
        logger.info( name + " has disconnected." );
    }

    @OnError
    public void onError( Throwable t ) throws Throwable
    {
        close( session );
    }

    @OnMessage
    public void onMessage( String message )
    {
        broadcast( message );
    }

    private void broadcast( String message )
    {
        for( Session s : sessions )
            if( s != session ) send( s, message );
    }

    private void send( Session session, String message )
    {
        try
        {
            synchronized (session)
            {
                session.getBasicRemote().sendText( name + ": " + message );
            }
        }
        catch( IOException e )
        {
            close( session );
        }
    }

    private void close( Session session )
    {
        try
        {
            session.close();
        }
        catch( IOException e )
        {
        }
    }

}
