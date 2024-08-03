package telran.employees.net;

import java.util.Scanner;

import telran.employees.*;
import telran.io.Persistable;
import telran.net.Protocol;
import telran.net.TcpServer;
import static telran.net.TcpConfigurationProperties.*;

public class CompanyServerAppl {

	private static final String FILE_NAME = "employeesTest.data";
	private static final int PORT = 5000;

	public static void main(String[] args) {

		Company company = new CompanyMapsImpl();
		Persistable persistable = null;
		if (company instanceof Persistable) {
			persistable = (Persistable) company;
		}
		if (persistable != null) {
			try {
				persistable.restore(FILE_NAME);
			} catch (Exception e) {

			}
		}
		Protocol protocol = new CompanyProtocol(company);
		TcpServer tcpServer = new TcpServer(protocol, PORT);
		Thread serverThread = new Thread(tcpServer);
		serverThread.start();
		boolean running = true;
		try (Scanner scanner = new Scanner(System.in);) {
			while (running) {
				System.out.println(String.format("Enter %s for the server shutdown", SHUTDOWN));
				String line = scanner.nextLine();
				if (line != null && line.equals(SHUTDOWN)) {
					running = false;
					tcpServer.shutdown();
					serverThread.join();
					if (persistable != null) {
						persistable.save(FILE_NAME);
					}
				}
			}
		} catch (InterruptedException e) {
			 System.out.println("Server shutdown interrupted: " + e.getMessage());
		}

	}
}