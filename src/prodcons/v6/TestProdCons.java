package prodcons.v6;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Random;

public class TestProdCons {
	public static void main(String args[]) throws InvalidPropertiesFormatException, IOException, InterruptedException {
		Properties properties = new Properties();
		properties.loadFromXML(new FileInputStream("src/prodcons/v6/option.xml"));
		int nProd = Integer.parseInt(properties.getProperty("nProd"));
		int nCons = Integer.parseInt(properties.getProperty("nCons"));
		int bufSz = Integer.parseInt(properties.getProperty("bufSz"));
		int prodTime = Integer.parseInt(properties.getProperty("prodTime"));
		int consTime = Integer.parseInt(properties.getProperty("consTime"));
		int minProd = Integer.parseInt(properties.getProperty("minProd"));
		int maxProd = Integer.parseInt(properties.getProperty("maxProd"));

		ProdConsBuffer buffer = new ProdConsBuffer(bufSz,nProd);
		Random generator = new Random();

		Producer prods[] = new Producer[nProd];
		Consumer cons[] = new Consumer[nCons];

		int ip = 0;
		int ic = 0;
		for (int i = 0; (ip < nProd) || (ic < nCons); i++) {
			int gen = generator.nextInt(2);
			if (((gen == 0) && (ip < nProd)) || (ip == 0)) {
				prods[ip] = new Producer(buffer, ip + 1, prodTime, minProd, maxProd);
				prods[ip].start();
				ip++;
			} else if ((gen == 1) && (ic < nCons)) {
				cons[ic] = new Consumer(buffer, ic + 1, consTime);
				cons[ic].start();
				ic++;
			}
		}

		for (int i = 0; i < ip; i++)
			prods[i].join();
		for (int i = 0; i < ic; i++) {
			cons[i].join();
		}
		System.out.println("Main terminated");
	}
}
