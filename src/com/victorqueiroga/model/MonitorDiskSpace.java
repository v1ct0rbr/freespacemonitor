package com.victorqueiroga.model;

import java.io.IOException;
import java.nio.file.FileStore;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.victorqueiroga.utils.Functions;
import com.victorqueiroga.utils.MailUtils;

public final class MonitorDiskSpace {

	private static final String RESOURCES_PATH = "resources/";
	private static final String EMAIL_CONFIG_FILE = RESOURCES_PATH + "settings.email.properties";
	private static final String RESPONSIBLES_FILE = RESOURCES_PATH + "settings.responsibles.properties";
	private static final String DEVICE_SETTINGS_FILE = RESOURCES_PATH + "settings.device.properties";
	private static final float THRESHOLD_USAGE = 0.95f;
	private static final long ALERT_INTERVAL = 3600000; // 1 hour in milliseconds

	MailUtils mailUtils;
	Properties emailConfig;
	Properties deviceSettings;
	List<String> responsibles;
	private Long lastAlert;

	private static MonitorDiskSpace instance;

	private MonitorDiskSpace() throws IOException {
		super();
		mailUtils = new MailUtils();
		emailConfig = Functions.loadProperties(EMAIL_CONFIG_FILE);
		responsibles = Functions.loadResponsibles(RESPONSIBLES_FILE);
		deviceSettings = Functions.loadProperties(DEVICE_SETTINGS_FILE);
	}

	public static MonitorDiskSpace getInstance() throws IOException {
		if (instance == null) {
			instance = new MonitorDiskSpace();
		}
		return instance;
	}

	public void monitor() throws IOException {

		String message;
		while (true) {
			message = "";
			Iterator<FileStore> fileStores = Functions.getFileStores();
			while (fileStores.hasNext()) {
				FileStore store = fileStores.next();
				// FileStore não funciona muito bem no linux. O trecho comentado seria para
				// ignorar discos virtuais gerados pelo sistema operacional.
				// if (!type.equals("HDD") && !type.equals("SSD") && !name.startsWith("/dev")) {
				// System.out.println("Ignorando" + store.toString() + "...");
				// continue; // Ignora unidades que não são discos rígidos
				// }

				System.out.println("Analisando o hd " + store.toString() + "...");
				long totalSpace = store.getTotalSpace();
				long usableSpace = store.getUsableSpace();
				float usage = 1 - ((float) usableSpace / totalSpace);

				System.out.println("Total de capacidade: " + totalSpace);
				System.out.println("Espaço utilizado: " + usableSpace);
				System.out.println("Uso total: " + usage * 100 + "%");

				if (usage > THRESHOLD_USAGE) {

					message += String.format(
							"- Alerta para o dispositivo \"%s\": Foi detectado um alto uso do disco %s (%.2f%% used)\n" ,
							deviceSettings.getProperty("device.name"),
							store.toString(), usage * 100);
					System.err.println(message);

				} else {
					System.out.println("Capacidade aceita");
				}
			}
			if (!message.isEmpty()) {
				if (shouldSendAlert()) {
					mailUtils.sendEmail(emailConfig, responsibles, "Alerta de espaço em disco", message);
					lastAlert = new Date().getTime();
				} else {
					System.err.println(
							"Novos alertas não podem ser emitidos até se passar o tempo necessário de 1 hora.");
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

	private boolean shouldSendAlert() {
		Long lastAlertTime = lastAlert;
		long currentTime = new Date().getTime();
		return lastAlertTime == null || (currentTime - lastAlertTime) >= ALERT_INTERVAL;
	}

}
