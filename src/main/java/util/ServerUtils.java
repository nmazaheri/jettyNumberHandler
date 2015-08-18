package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.Iterator;
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

    public static void disconnectAllClients(List<Socket> clientSockets) {
        synchronized (clientSockets) {
            logger.debug("closing all remaining connections; {}", getActivePorts(clientSockets));
            Iterator<Socket> iter = clientSockets.iterator();
            while (iter.hasNext()) {
                try {
                    iter.next().close();
                } catch (IOException e) {
                    logger.warn("unable to close client");
                }
            }
        }
    }

    public static boolean isTerminationString(String in) {
        return "terminate".equals(in);
    }
}
