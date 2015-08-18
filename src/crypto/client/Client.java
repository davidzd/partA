package crypto.client;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.json.simple.parser.ParseException;

import client.exception.ServerBusyException;
import client.utils.StringParser;
import crypto.messages.request.DHExDoneRequest;
import crypto.messages.request.DHExRequest;
import crypto.messages.request.DHExStartRequest;
import crypto.messages.request.HelloRequest;
import crypto.messages.response.DHExResponse;
import crypto.messages.response.DHExStartResponse;
import crypto.messages.response.FinishResponse;
import crypto.messages.response.HelloResponse;

public class Client {
	// for debug and info
	private static Logger log = Logger.getLogger(Client.class);
	
	private final int BUFFER_SIZE = 1024 * 8; // 4 KB must be enough!
	
	// private attributes
	private String ip;
	private int port;
	private String studentId;
	private int counter;
	
	// crypto variables
	private BigInteger generator;
	private BigInteger prime;
	private BigInteger pkClient;
	private BigInteger pkServer;
	private BigInteger skClient;
	private BigInteger sharedKey;
	
	// network variables
	private Socket socket;
	private DataOutputStream writer;
	private DataInputStream reader;
	
	public Client(String ip, int port, String studentId) {
		// networking variables
		this.ip = ip;
		this.port = port;
		
		// messaging variables
		this.studentId = studentId;
		this.counter = 1;
	}

	public void startClient() {
		try {
			socket = new Socket(ip, port);
			writer = new DataOutputStream(socket.getOutputStream());
			reader = new DataInputStream(new BufferedInputStream(socket.getInputStream(), BUFFER_SIZE));
			log.info("Connected to Server...");
			
			log.info("==================== 1) Contact Phase Now ====================");
			contactPhase();
			log.info("==================== 2) Exchange Phase Now ===================");
			exchangePhase();
			exit();
			
		} catch (IOException ioe) {
			log.error("Connection with the server was not possible");
		} catch (ServerBusyException sbe) {
			log.error("The server is busy now. Please, try again later");
		} catch (ParseException pe) {
			log.error("The message received was not parsed correctly");
			pe.printStackTrace();
		}
	}

	private void contactPhase() throws IOException, ServerBusyException, ParseException {
		HelloRequest request = new HelloRequest(studentId, counter++);
		log.debug("Message to send: [" + request.toJSON() + "]");
		writer.write(request.toJSON().getBytes("UTF-8"));
		
		byte[] buffer = new byte[BUFFER_SIZE];
		reader.read(buffer);
		String reply = new String(buffer, "UTF-8");
		
		log.debug("Message received: [" + reply + "]");
		HelloResponse response = new HelloResponse();
		response.fromJSON(StringParser.getUTFString(reply));
	}
	
	private void exchangePhase() throws IOException, ParseException {
		DHExStartRequest startRequest = new DHExStartRequest(counter++);
		log.debug("Message to send: [" + startRequest.toJSON() + "]");
		writer.write(startRequest.toJSON().getBytes("UTF-8"));
		
		// get the public parameters and calculate the shared key
		byte[] buff = new byte[BUFFER_SIZE];
		reader.read(buff);
		String reply = new String(buff, "UTF-8");
		
		log.debug("Message received: [" + reply + "]");
		DHExStartResponse response = new DHExStartResponse();
		response.fromJSON(StringParser.getUTFString(reply));
		
		generator = response.getGenerator();
		prime = response.getPrime();
		pkServer = response.getPkServer();
		skClient = response.getSkClient();
		
		// after that the client got the public parameters, it calculates the shared key
		if (skClient != BigInteger.ZERO) {
			BigInteger[] pair = CustomCrypto.createDHPair(generator, prime, skClient);
			pkClient = pair[1];
		} else {
			BigInteger tempKey = CustomCrypto.createPrivateKey(2048);
			BigInteger[] pair = CustomCrypto.createDHPair(generator, prime, tempKey);
			skClient = pair[0];
			pkClient = pair[1];
		}
		
		// finalize the process properly
		DHExRequest dhexRequest = new DHExRequest(pkClient, counter++);
		log.debug("Message to send: [" + dhexRequest.toJSON() + "]");
		writer.write(dhexRequest.toJSON().getBytes("UTF-8"));
		
		buff = new byte[BUFFER_SIZE];
		reader.read(buff);
		reply = new String(buff, "UTF-8");
		
		log.debug("Message received: [" + reply +"]");
		DHExResponse dhResponse = new DHExResponse();
		dhResponse.fromJSON(StringParser.getUTFString(reply));
		
		sharedKey = CustomCrypto.getDHSharedKey(pkServer, skClient, prime);
		log.debug("The shared key is: [" + sharedKey + "]");
		
		DHExDoneRequest doneRequest = new DHExDoneRequest(counter++);
		log.debug("Message to send: [" + doneRequest.toJSON() + "]");
		writer.write(doneRequest.toJSON().getBytes("UTF-8"));
	}
	
	private void exit() throws IOException, ParseException {
		byte[] buff = new byte[BUFFER_SIZE];
		reader.read(buff);
		String reply = new String(buff, "UTF-8");
		
		log.debug("Message received: [" + reply + "]");
		FinishResponse response = new FinishResponse();
		response.fromJSON(StringParser.getUTFString(reply));
		
		log.info("Client Tasks completed successfully. Terminating cleanly...");
		if (socket != null && !socket.isClosed())
			socket.close();
	}
}
