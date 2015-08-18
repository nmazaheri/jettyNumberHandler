import data.WindowDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ServerUtils;

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
                logger.info("client connected; currentClientPorts={}", ServerUtils.getActivePorts(clientSocketList));
                clientProcessingPool.submit(new ClientTask(clientSocketList, clientSocket, windowDataStore, this));
            }
        } catch (SocketException e) {
            logger.debug("server socket has been closed");
        } catch (Exception e) {
            logger.error("Unable to process client request. ", e);
        }
    }

    public void shutdown() {
        logger.info("Shutting down server and disconnecting all clients={}",
                ServerUtils.getActivePorts(clientSocketList));

        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.warn("unable to close serverSocket. ", e);
        }

        ServerUtils.attemptToCloseSockets(clientSocketList);
        clientProcessingPool.shutdown();
    }

}


