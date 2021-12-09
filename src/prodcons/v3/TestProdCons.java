package prodcons.v3;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

public class TestProdCons {
	public static void main(String args[]) throws InvalidPropertiesFormatException, IOException, InterruptedException {
		Properties properties = new Properties();
		// il faut parser toutes les données qui sont dans le fichier option.xml
		// pour recuperer toutes les valeurs concernant les productions et consommations.
		properties.loadFromXML(new FileInputStream("src/prodcons/v3/option.xml"));
		int nProd = Integer.parseInt(properties.getProperty("nProd"));
		int nCons = Integer.parseInt(properties.getProperty("nCons"));
		int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
		int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
		int consTime = Integer.parseInt(properties.getProperty("consTime"));
		int minProd = Integer.parseInt(properties.getProperty("minProd"));
		int maxProd = Integer.parseInt(properties.getProperty("maxProd"));

		ProdConsBuffer buffer = new ProdConsBuffer(bufSz,nProd); // creation du buffer
		Random generator = new Random();

		Producer prods[] = new Producer[nProd]; // liste des threads producteurs
		Consumer cons[] = new Consumer[nCons]; // liste des threads consommateurs

		int ip = 0;
		int ic = 0;
		long start = System.currentTimeMillis();
		for (int i = 0; (ip < nProd) || (ic < nCons); i++) {
			int gen = generator.nextInt(2); // choisi de maniere random si on lance un producer(0) ou un consumer(1)
			if (((gen == 0) && (ip < nProd)) || (ip == 0)) { // ip == 0 pour etre sur de commencer avec un producer
				prods[ip] = new Producer(buffer, ip + 1, prodTime, minProd, maxProd);
				prods[ip].start(); // lancement d'un thread producer
				ip++;
			} else if ((gen == 1) && (ic < nCons)) {
				cons[ic] = new Consumer(buffer, ic + 1, consTime);
				cons[ic].start(); // lancement d'un thread consumer
				ic++;
			}
		}

		// une fois les producers et consumers tous utilisés
		// et leurs actions terminées on les tue
		for (int i = 0; i < ip; i++)
			prods[i].join();
		for (int i = 0; i < ic; i++) {
			cons[i].join();
		}
		long end = System.currentTimeMillis();
		long duration =  end - start ;
		System.out.println("l'execution a durée :" + duration + "ms");
		System.out.println("Main terminated");
	}
}
