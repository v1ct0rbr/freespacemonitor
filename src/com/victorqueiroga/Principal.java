package com.victorqueiroga;

import java.util.Scanner;

import com.victorqueiroga.model.MonitorDiskSpace;

public class Principal {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MonitorDiskSpace monitor = MonitorDiskSpace.getInstance();

		Scanner scanner = new Scanner(System.in);
		int choice;

		do {
			System.out.println("Menu:");
			System.out.println("1. Inicializar monitoramento");
			System.out.println("2. Teste de e-mail");
			System.out.println("3. Sair");

			System.out.print("Escolha uma opção: ");
			choice = scanner.nextInt();

			switch (choice) {
			case 1:
				System.out.println("Executando monitoramento...");
				try {
					monitor.monitor();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
				break;
			case 2:
				System.out.println("Executando teste de envio");
				try {
					monitor.sendTestMail();
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			case 3:
				System.out.println("Saindo do programa...");
				break;
			default:
				System.out.println("Opção inválida. Tente novamente.");
			}

			System.out.println();
		} while (choice != 3);

		scanner.close();

	}

}
