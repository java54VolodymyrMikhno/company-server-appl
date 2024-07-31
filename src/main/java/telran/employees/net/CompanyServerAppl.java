package telran.employees.net;

import telran.employees.*;
import telran.io.Persistable;
import telran.net.Protocol;
import telran.net.TcpServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class CompanyServerAppl {
	private static final String FILE_NAME = "employeesTest.data";
	private static final int PORT = 5000;

	public static void main(String[] args) {
		Company company = new CompanyMapsImpl();
		try {
			((Persistable) company).restore(FILE_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Protocol protocol = new CompanyProtocol(company);
		TcpServer tcpServer = new TcpServer(protocol, PORT);

		Thread serverThread = new Thread(tcpServer);
		serverThread.start();
		boolean shutdownFlag = false;

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
			String command;
			System.out.println("Enter 'shutdown' to stop the server:");
			while (!shutdownFlag && (command = reader.readLine()) != null) {

				if ("shutdown".equalsIgnoreCase(command)) {
					tcpServer.shutdown();
					shutdownFlag = true;
					while (serverThread.isAlive()) {
						try {
							serverThread.join(100);
						} catch (InterruptedException e) {
							throw new RuntimeException(e.getMessage());
						}
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		try {
			((Persistable) company).save(FILE_NAME);
			System.out.println("Data saved, Server stopped");
		} catch (Exception e) {
			System.out.println("Failed to save data");
		}
	}
}
