package com.victorqueiroga.model;

import java.io.IOException;
import java.nio.file.FileStore;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.victorqueiroga.utils.Functions;
import com.victorqueiroga.utils.MailUtils;

public final class MonitorDiskSpace {

	private static final String EMAIL_CONFIG_FILE = "resources/email.properties";
	private static final String RESPONSIBLES_FILE = "resources/responsibles.properties";
	private static final float THRESHOLD_USAGE = 0.95f;

	MailUtils mailUtils;
	Properties emailConfig;
	List<String> responsibles;

	private static MonitorDiskSpace instance;

	private MonitorDiskSpace() {
		super();
		mailUtils = new MailUtils();
	}

	public static MonitorDiskSpace getInstance() {
		if (instance == null) {
			instance = new MonitorDiskSpace();

		}
		return instance;
	}

	public void monitor() throws IOException {

		emailConfig = Functions.loadProperties(EMAIL_CONFIG_FILE);
		responsibles = Functions.loadResponsibles(RESPONSIBLES_FILE);
		while (true) {
			Iterator<FileStore> fileStores = Functions.getFileStores();
			while (fileStores.hasNext()) {
				FileStore store = fileStores.next();
				String type = store.type();
				String name = store.toString();
				if (!type.equals("HDD") && !type.equals("SSD") && !name.startsWith("/dev")) {
					System.out.println("Ignorando" + store.toString() + "...");
					continue; // Ignora unidades que não são discos rígidos
				}

				System.out.println("Analisando o hd " + store.toString() + "...");
				long totalSpace = store.getTotalSpace();
				long usableSpace = store.getUsableSpace();
				float usage = 1 - ((float) usableSpace / totalSpace);

				System.out.println("Total de capacidade: " + totalSpace);
				System.out.println("Espaço utilizavel: " + usableSpace);
				System.out.println("Uso total: " + usage * 100 + "%");

				if (usage > THRESHOLD_USAGE) {
					String message = String.format("Alert: High disk space usage detected on %s (%.2f%% used)",
							store.toString(), usage * 100);
					System.err.println(message);
					mailUtils.sendEmail(emailConfig, responsibles, "Disk Space Alert", message);
				} else {
					System.out.println("Capacidade aceita");
				}
			}
			System.out.println("Todos os hds verificados.");

			try {
				Thread.sleep(60000); // 1 minute delay between checks
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void sendTestMail() throws IOException {
		emailConfig = Functions.loadProperties(EMAIL_CONFIG_FILE);
		responsibles = Functions.loadResponsibles(RESPONSIBLES_FILE);
		mailUtils.sendEmail(emailConfig, responsibles, "Teste de envio", "teste bem sucedido.");
	}

}
