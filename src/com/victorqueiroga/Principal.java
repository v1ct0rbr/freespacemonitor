package com.victorqueiroga;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import com.victorqueiroga.model.MonitorDiskSpace;

public class Principal {

	public static void main(String[] args) {

	  final ExecutorService executor = Executors.newSingleThreadExecutor();
        final MonitorDiskSpace monitor;
        try {
            monitor = MonitorDiskSpace.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            // Exit if we cannot initialize the MonitorDiskSpace object.
            return;
        }

        final Scanner scanner = new Scanner(System.in);
        int choice = 1; // Default choice is 1

        do {
            System.out.println("Menu:");
            System.out.println("1. Inicializar monitoramento");
            System.out.println("2. Teste de e-mail");
            System.out.println("3. Sair");

            System.out.print("Escolha uma opção: ");

            // Create a Callable that will wait for the user's input
            Callable<Integer> inputGetter = () -> {
                try {
                    if (scanner.hasNextInt()) {
                        return scanner.nextInt();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            };

            // Submit the callable to the executor and get the future object
            Future<Integer> future = executor.submit(inputGetter);

            try {
                // Wait for the user's input with a timeout of 20 seconds
                Integer result = future.get(20, TimeUnit.SECONDS);
                if (result != null) {
                    choice = result;
                }
            } catch (TimeoutException e) {
                System.out.println("\nNenhuma entrada em 20 segundos, selecionando opção padrão 1.");
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            switch (choice) {
                case 1:
                    System.out.println("Executando monitoramento...");
                    try {
                        monitor.monitor();
                    } catch (Exception e) {
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
        executor.shutdown();
	}

}
