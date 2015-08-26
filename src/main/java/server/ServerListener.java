package server;

import data.DataLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by navid.mazaheri on 8/14/15.
 */
public class ServerListener {
    private static final Logger logger = LoggerFactory.getLogger(ServerListener.class);

    private final int socket;
    private final int maxClients;
    private DataLogger dataLogger;

    private ExecutorService clientProcessingPool;
    private ServerSocket serverSocket;
    private List<Socket> clientSockets = new ArrayList();

    public ServerListener(DataLogger dataLogger, int socket, int maxClients) {
        this.maxClients = maxClients;
        this.socket = socket;
        this.dataLogger = dataLogger;
        clientProcessingPool = Executors.newFixedThreadPool(maxClients);
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(socket, maxClients);
            logger.info("Server started on socket {}", socket);

            while (!serverSocket.isClosed()) {
                handleClient(serverSocket.accept());
            }
        } catch (SocketException e) {
            if (!serverSocket.isClosed())
                logger.warn("server SocketException", e);

        } catch (Exception e) {
            logger.error("Unable to process client request. ", e);
        }
    }

    synchronized private void handleClient(Socket clientSocket) {
        clientSockets.add(clientSocket);
        logger.info("client connected port={}; currentClientPorts={}", clientSocket.getPort(),
                getActivePorts(clientSockets));

        clientProcessingPool.submit(new ClientTask(clientSocket, dataLogger, this));
    }

    private String getActivePorts(List<Socket> clientSocketList) {
        int[] ports = new int[clientSocketList.size()];
        for (int i = 0; i < clientSocketList.size(); i++) {
            ports[i] = clientSocketList.get(i).getPort();
        }
        return Arrays.toString(ports);
    }

    synchronized protected void removeClient(Socket client) {
        clientSockets.remove(client);
    }

    public void shutdown() {
        logger.debug("closing server socket");
        try {
            serverSocket.close();
        } catch (IOException e) {
            logger.warn("unable to close serverSocket. ", e);
        }
        clientProcessingPool.shutdown();
        disconnectAllClients(clientSockets);
    }

    synchronized private void disconnectAllClients(List<Socket> clientSockets) {
        logger.debug("closing all remaining connections; {}", getActivePorts(clientSockets));
        Iterator<Socket> socketIterator = clientSockets.iterator();
        while (socketIterator.hasNext()) {
            try {
                socketIterator.next().close();
            } catch (IOException e) {
                logger.warn("unable to close client");
            }
        }
    }

}


