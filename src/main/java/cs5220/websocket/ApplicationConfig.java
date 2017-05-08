package cs5220.websocket;

import java.util.HashSet;
import java.util.Set;

import javax.websocket.Endpoint;
import javax.websocket.server.ServerApplicationConfig;
import javax.websocket.server.ServerEndpointConfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationConfig implements ServerApplicationConfig {

    private static final Logger logger = LoggerFactory
        .getLogger( ApplicationConfig.class );

    @Override
    public Set<Class<?>> getAnnotatedEndpointClasses( Set<Class<?>> scanned )
    {
        logger.info( scanned.size() + " endpoints found:" );
        for( Class<?> clazz : scanned )
            logger.debug( "\t" + clazz.getName() );

        // Include every scanned endpoint class
        return scanned;
    }

    @Override
    public Set<ServerEndpointConfig> getEndpointConfigs(
        Set<Class<? extends Endpoint>> scanned )
    {
        // We don't have any additional ServerEndPointConfig
        return new HashSet<ServerEndpointConfig>();
    }

}
