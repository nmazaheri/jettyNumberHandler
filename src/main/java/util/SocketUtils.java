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
public class SocketUtils {
    private static final Logger logger = LoggerFactory.getLogger(SocketUtils.class);

    public static String getActivePorts(List<Socket> clientSocketList) {
        int[] ports = new int[clientSocketList.size()];
        for (int i = 0; i < clientSocketList.size(); i++) {
            ports[i] = clientSocketList.get(i).getPort();
        }
        return Arrays.toString(ports);
    }

    public static void attemptToCloseSocket(Socket s) {
        try {
            if (s != null) {
                logger.debug("closing connection; port={}", s.getPort());
                s.close();
            }
        } catch (IOException e) {
            logger.warn("unable to close connection; socket = {}", s.getPort());
        }
    }

    public static void attemptToCloseSockets(List<Socket> clientSocketList) {
        logger.debug("closing all connections");
        for (Socket s : clientSocketList) {
            SocketUtils.attemptToCloseSocket(s);
        }
    }
}
