import java.io.*;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
public class Server {
	private static final int PORT_NUMBER = 9100;// Define server port as a constant

	public static void main(String[] args) throws IOException {
		ExecutorService service = Executors.newFixedThreadPool(20);
		int portNumber = PORT_NUMBER;
		PrintWriter log = new PrintWriter("log.txt");

		try (ServerSocket serverSocket = new ServerSocket(portNumber)) {
			System.out.println("Server is listening on port " + portNumber);

			while (true) { //keep server running
				try {
					final Socket clientSocket = serverSocket.accept();
					// Handle each client connection in a separate thread
					service.submit(() -> serveClient(clientSocket, log));
				}
				catch (IOException e) {
					System.err.println("Failed to accept connection.");
					System.exit(1);
				}
			}
		}catch (IOException e) {
			System.err.println("Server failed to start: " + e.getMessage());
		}
	}


	/**
	 * Handles client requests.
	 * @param clientSocket The socket connected to the client.
	 * @param log The PrintWriter to log client requests.
	 */
	private static void serveClient(Socket clientSocket, PrintWriter log){
		try{
			PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
			BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			System.out.println("Client connected: " + clientSocket.getInetAddress());

			String clientIP = clientSocket.getInetAddress().getHostAddress();
			String inLine;
			while ((inLine = in.readLine()) != null) {
				// Log each valid client request
				logValidRequest(log, clientIP, inLine);

				if ("list".equalsIgnoreCase(inLine.trim())) {
				listFiles(out);
				} else if (inLine.startsWith("put")) {
					putFile(out, in);
				}
				break;
			}
		} catch (IOException e) {
			System.err.println("Error handling client request: " + e.getMessage());
		}
	}


	/**
	 * Logs a valid request from the client.
	 * @param log The PrintWriter to log the request.
	 * @param clientIp The IP address of the client.
	 * @param request The request command from the client.(put // list)
	 */
	private static void logValidRequest(PrintWriter log, String clientIp, String request){
		String dateTime = new SimpleDateFormat("yyyy-MM-dd|HH:mm:ss").format(new Date());
		String logEntry = dateTime + "|" + clientIp + "|" + request;
		log.println(logEntry);
		log.flush();
	}


	/**
	 * Lists files stored in the server directory.
	 * @param out The PrintWriter to send response to client.
	 */
	private static void listFiles(PrintWriter out){
		File directory = new File("./serverFiles");
		File[] files = directory.listFiles();
		if (files != null && files.length > 0) {
			out.println("Listing " + files.length + " file(s):");
			for (File file : files) {
				if (file.isFile()) {
					out.println(file.getName());
				}
			}
		} else {
			out.println("No file on the server");
		}
		out.println("END");
	}


	/**
	 * Handles the 'put' command to save a file on the server.
	 * @param out The PrintWriter to send response to client.
	 * @param in The BufferedReader to read the file content from the client.
	 */
	private static void putFile(PrintWriter out, BufferedReader in) throws IOException {
		String filename = in.readLine();
		File directory = new File("./serverFiles");
		File file = new File(directory, filename);
		if (file.exists()) {
			out.println("Error: File already exist");
			out.println("END");
		} else {
			try (BufferedWriter fileOut = new BufferedWriter(new FileWriter(file))) {
				String fileLine;
				while (!(fileLine = in.readLine()).equals("END_OF_FILE")) {
					fileOut.write(fileLine);
					fileOut.newLine();
				}
				out.println("Upload file " + filename);
				out.println("END");

			} catch (IOException e) {
				out.println("Error: Failed to save " + filename + "'.");
				out.println("END");
			}
		}
	}
}