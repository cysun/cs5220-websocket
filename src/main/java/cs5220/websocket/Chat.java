package cs5220.websocket;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Based on the Tomcat example code at
 * http://svn.apache.org/viewvc/tomcat/trunk/webapps/examples/WEB-INF/classes/websocket/
 */
@ServerEndpoint("/chat")
public class Chat {

    private static final String GUEST_PREFIX = "Guest";

    private static final AtomicInteger connectionIds = new AtomicInteger( 0 );

    private static final Set<Chat> connections = new CopyOnWriteArraySet<>();

    private static final Logger logger = LoggerFactory.getLogger( Chat.class );

    private final String nickname;

    private Session session;

    public Chat()
    {
        nickname = GUEST_PREFIX + connectionIds.getAndIncrement();
    }

    @OnOpen
    public void start( Session session )
    {
        this.session = session;
        connections.add( this );
        send( this, "Welcome to the chat, " + nickname + "!" );
        broadcast( nickname + " has joined." );
        logger.info( nickname + " has joined." );
    }

    @OnClose
    public void end()
    {
        connections.remove( this );
        broadcast( nickname + " has disconnected." );
        logger.info( nickname + " has disconnected." );
    }

    @OnError
    public void onError( Throwable t ) throws Throwable
    {
        close( this );
    }

    @OnMessage
    public void incoming( String message )
    {
        logger.debug( nickname + ": " + message );
        broadcast( nickname + ": " + message );
    }

    private void broadcast( String message )
    {
        for( Chat client : connections )
            if( client != this ) send( client, message );
    }

    private void send( Chat client, String message )
    {
        try
        {
            synchronized (client)
            {
                client.session.getBasicRemote().sendText( message );
            }
        }
        catch( IOException e )
        {
            close( client );
        }
    }

    private void close( Chat client )
    {
        try
        {
            client.session.close();
        }
        catch( IOException e )
        {
        }
    }

}
