import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ServerUtils;

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

    private final Socket clientSocket;
    private List<Socket> clientSockets;
    private WindowDataStore windowDataStore;
    private ServerListener parent;
    private BufferedReader inputReader;

    public ClientTask(List<Socket> clientSockets, Socket clientSocket, WindowDataStore windowDataStore,
            ServerListener parent) {
        this.clientSockets = clientSockets;
        this.clientSocket = clientSocket;
        this.windowDataStore = windowDataStore;
        this.parent = parent;
    }

    public void run() {
        String clientData = "";
        try {
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            do {
                clientData = inputReader.readLine();
                logger.trace("Client Data: {}", clientData);
            } while (continueReadingFromClient(clientData));
        } catch (SocketException e) {
            if (!clientSocket.isClosed())
                logger.warn("client socketException", e);

            return;
        } catch (IOException e) {
            logger.warn("Unable to read data from client socket", e);
        } finally {
            closeConnection();
        }

        if (ServerUtils.isTerminationString(clientData)) {
            parent.shutdown();
        }
    }

    private boolean continueReadingFromClient(String clientData) {
        return !clientSocket.isClosed() && clientData != null && windowDataStore.updateWindow(clientData);
    }

    private void closeConnection() {
        try {
            logger.info("closing socket on port {}", clientSocket.getPort());
            inputReader.close();
            synchronized (clientSockets) {
                clientSockets.remove(clientSocket);
            }
        } catch (IOException e) {
            logger.warn("unable to close BufferedReader");
        }
    }
}

