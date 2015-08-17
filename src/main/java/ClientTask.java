import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SocketUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class ClientTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientTask.class);
    private final String terminationString = "terminate";
    private List<Socket> clientSocketList;
    private final Socket clientSocket;
    private WindowDataStore windowDataStore;
    private ServerListener parent;

    public ClientTask(List<Socket> clientSocketList, Socket clientSocket, WindowDataStore windowDataStore,
            ServerListener parent) {
        this.clientSocketList = clientSocketList;
        this.clientSocket = clientSocket;
        this.windowDataStore = windowDataStore;
        this.parent = parent;
    }

    public void run() {
        String clientData = readDataUntilFailure();

        if (terminationString.equals(clientData)) {
            disableServer();
        } else {
            disableClient();
        }
    }

    private void disableServer() {
        logger.debug("terminate keyword found, disconnecting all clients",
                SocketUtils.getActivePorts(clientSocketList));
        parent.disableServer();
        SocketUtils.attemptToCloseSockets(clientSocketList);

    }

    private String readDataUntilFailure() {
        String clientData = "";
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            do {
                clientData = reader.readLine();
                logger.trace("Client Data: {}", clientData);
            } while (isValid(clientData));
        } catch (SocketException e) {
            logger.debug("client socket has been closed");
        } catch (IOException e) {
            logger.warn("Unable to read data from client socket", e);
        } finally {
            closeInput(reader);
        }

        return clientData;
    }

    private boolean isValid(String clientData) {
        return !clientSocket.isClosed() && clientData != null && windowDataStore.updateWindow(clientData);
    }

    private void closeInput(BufferedReader reader) {
        try {
            if (reader != null)
                reader.close();
        } catch (IOException e) {
            logger.warn("unable to close BufferedReader");
        }
    }

    private void disableClient() {
        SocketUtils.attemptToCloseSocket(clientSocket);
        clientSocketList.remove(clientSocket);
        logger.debug("client disconnected; port={}; remaining={}", clientSocket.getPort(),
                SocketUtils.getActivePorts(clientSocketList));
    }
}

