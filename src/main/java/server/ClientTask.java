package server;

import data.DataLogger;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by navid.mazaheri on 8/16/15.
 */
public class ClientTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(ClientTask.class);

    private final Socket clientSocket;
    private DataLogger dataLogger;
    private ServerListener server;
    private BufferedReader inputReader;

    public ClientTask(Socket clientSocket, DataLogger dataLogger, ServerListener server) {
        this.clientSocket = clientSocket;
        this.dataLogger = dataLogger;
        this.server = server;
    }

    public void run() {
        String clientData = "";
        try {
            inputReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            do {
                clientData = inputReader.readLine();
                logger.trace("Client Data: {}", clientData);

                if (isTerminationString(clientData)) {
                    logger.warn("Termination String found = \"{}\" ", clientData);
                    server.shutdown();
                    return;
                }

                if (!isValidNumber(clientData)) {
                    logger.warn("\"{}\" is an invalid input data", clientData);
                    return;
                }

                dataLogger.update(clientData);
            } while (!clientSocket.isClosed());

        } catch (SocketException e) {
            if (!clientSocket.isClosed())
                logger.warn("client socketException", e);
        } catch (NumberFormatException e) {
            logger.warn("Unable to convert \"{}\" to an integer", clientData);
        } catch (IOException e) {
            logger.warn("Unable to read data from client socket", e);
        } finally {
            closeConnection();
        }
    }

    private boolean isValidNumber(String in) {
        return StringUtils.isNotEmpty(in) && in.length() == 9 && StringUtils.isNumeric(in);
    }

    private void closeConnection() {
        try {
            logger.info("closing socket on port {}", clientSocket.getPort());
            inputReader.close();
            server.removeClient(clientSocket);
        } catch (IOException e) {
            logger.warn("unable to close BufferedReader");
        }
    }

    private boolean isTerminationString(String in) {
        return "terminate".equals(in);
    }
}

