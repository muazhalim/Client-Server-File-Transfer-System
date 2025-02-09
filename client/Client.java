import java.io.*;
import java.net.*;

public class Client {
	private Socket fSocket = null;
	private PrintWriter socketOutput = null;
	private BufferedReader socketInput = null;

	public static void main(String[] args) {
		Client client = new Client();
		client.runClient(args);
	}


	/**
	 * Initiates the client application.
	 * @param args Command line arguments specifying the command and a filename.
	 */
	public void runClient(String[] args) {
		if (args.length == 0) {
			System.err.println("Usage: java Client <command> [filename]");
			System.exit(1);
		}
		try {
			fSocket = new Socket("localhost", 9100);
			socketOutput = new PrintWriter(fSocket.getOutputStream(), true);
			socketInput = new BufferedReader(new InputStreamReader(fSocket.getInputStream()));
			startClient(args);
		} catch (UnknownHostException e) {
			System.err.println("Could not find host.\n");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for the connection to host.\n");
			System.exit(1);
		} finally {
			stopConnection(); // Close the connection after handling the command
		}
	}


	/**
	 * Processes the user command and interacts with the server.
	 * @param args Command line arguments.
	 * @throws IOException If an I/O error occurs during communication.
	 */
	public void startClient(String[] args) throws IOException {
		String command = args[0].toLowerCase();
		if (command.startsWith("put") && args.length > 1) {
			String filename = args[1];
			File file = new File(filename);
			if (!file.exists()) {
				System.out.println("File does not exist: " + filename);
				return;
			}
			//Send command and filename to server
			socketOutput.println(command);
			socketOutput.println(filename);
			// Read and send file contents
			uploadFile(socketOutput, file);
			handleResponse(socketInput);
		} else if ("list".equalsIgnoreCase(command) && args.length == 1 ) {
			socketOutput.println(command); // Send user command to server// list
			handleResponse(socketInput); // Handle server response
		}else{
			System.out.println("Invalid command");
			System.exit(1);
		}
	}


	/**
	 * Reads and prints the server's response.
	 * @param socketInput BufferedReader connected to the server's output stream.
	 * @throws IOException If an I/O error occurs while reading the response.
	 */
	private static void handleResponse(BufferedReader socketInput) throws IOException {
		String inputLine;
		while (true) {
			inputLine = socketInput.readLine();
			if (inputLine.equals("END")) {
				System.exit(0);; // Exit the loop when "END" is received
			}
			System.out.println(inputLine);
		}
	}


	/**
	 * Uploads a file to the server.
	 * @param socketOutput PrintWriter connected to the server's input stream.
	 * @param file The file to upload.
	 */
	private static void uploadFile(PrintWriter socketOutput,File file ){
		try (BufferedReader fileReader = new BufferedReader(new FileReader(file))) {
			String fileLine;
			while ((fileLine = fileReader.readLine()) != null) {
				socketOutput.println(fileLine);
			}
			socketOutput.println("END_OF_FILE");
		} catch (IOException ex) {
			System.err.println("Error: An I/O error occurred while uploading '" + file.getName() + ".");
		}
	}


	/**
	 * Closes the connection to the server.
	 */
	public void stopConnection() {
		try {
			socketInput.close();
			socketOutput.close();
			fSocket.close();
		} catch (IOException e) {
			System.err.println("Error: Failed to close the connection.");
		}
	}
}