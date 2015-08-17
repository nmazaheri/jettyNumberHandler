import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.SocketUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by navid.mazaheri on 8/14/15.
 */
public class ServerListener extends Thread {
    private static final Logger logger = LoggerFactory.getLogger(ServerListener.class);

    private final int socket = 4000;
    private final int maxClients = 5;
    private List<Socket> clientSocketList = new ArrayList();
    private WindowDataStore windowDataStore;
    private ExecutorService clientProcessingPool = Executors.newFixedThreadPool(maxClients);
    private ServerSocket serverSocket = null;

    public ServerListener(WindowDataStore windowDataStore) {
        this.windowDataStore = windowDataStore;
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(socket, maxClients);
            logger.info("Server started on socket {}", socket);
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();
                clientSocketList.add(clientSocket);
                logger.debug("client connected; currentClientPorts={}", SocketUtils.getActivePorts(clientSocketList));
                clientProcessingPool.submit(new ClientTask(clientSocketList, clientSocket, windowDataStore, this));
            }
        } catch (SocketException e) {
            logger.debug("server socket has been closed");
        } catch (Exception e) {
            logger.error("Unable to process client request. ", e);
        }
    }

    public void disableServer() {
        closeServerSocket(serverSocket);
        clientProcessingPool.shutdownNow();
    }

    private void closeServerSocket(ServerSocket serverSocket) {
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.warn("unable to close serverSocket. ", e);
        }
    }
}


