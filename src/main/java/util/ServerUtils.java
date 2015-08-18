package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class ServerUtils {
    private static final Logger logger = LoggerFactory.getLogger(ServerUtils.class);

    public static String getActivePorts(List<Socket> clientSocketList) {
        int[] ports = new int[clientSocketList.size()];
        for (int i = 0; i < clientSocketList.size(); i++) {
            ports[i] = clientSocketList.get(i).getPort();
        }
        return Arrays.toString(ports);
    }

    synchronized public static void attemptToCloseSocket(Socket s) {
        try {
            if (s != null && !s.isClosed()) {
                logger.info("closing connection on port={}", s.getPort());
                s.close();
            }
        } catch (IOException e) {
            logger.warn("unable to close connection on port={}; ", s.getPort(), e);
        }
    }

    public static void attemptToCloseSockets(List<Socket> clientSocketList) {
        logger.debug("closing all connections; {}", getActivePorts(clientSocketList));
        for (Socket s : clientSocketList) {
            ServerUtils.attemptToCloseSocket(s);
        }
    }

    public static boolean isTerminationString(String in) {
        return "terminate".equals(in);
    }
}
