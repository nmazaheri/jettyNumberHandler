import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class ClientUtil {
    private static final Logger logger = LoggerFactory.getLogger(ClientUtil.class);
    private static final Exception terminateServer = new Exception();

    public static String getActivePorts(List<Socket> clientSocketList) {
        int[] ports = new int[clientSocketList.size()];
        for (int i = 0; i < clientSocketList.size(); i++) {
            ports[i] = clientSocketList.get(i).getPort();
        }
        return Arrays.toString(ports);
    }

    public static void disconnectClients(List<Socket> clientSocketList) throws IOException {
        logger.warn("terminate keyword found, disconnecting all clients", getActivePorts(clientSocketList));
        for (Socket s: clientSocketList) {
            s.close();
        }
    }

    public static void removeClientFromList(Socket clientSocket, List<Socket> clientSocketList) {
        if (clientSocketList.contains(clientSocket)) {
            logger.debug("client disconnected; port={}", clientSocket.getPort());
            clientSocketList.remove(clientSocket);
        }
    }
}
