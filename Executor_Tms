package thomas;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;

public class Executor {

	public static void main(String[] args) {
		/*
		 * String[] stringArray = {"bloomberg", "reuteurs", "ebs"};
		 * 
		 * 
		 * //helps to remove the first index from stringArray. aka modifiedArray[0] =
		 * stringArray[1] String[] modifiedArray = Arrays.copyOfRange(stringArray, 1,
		 * stringArray.length);
		 * 
		 * System.out.println(stringArray[2]); System.out.println(modifiedArray[2]);
		 */

		List<bbg> bbgs = readbbgsFromCSV("bbg.csv");
		List<ebs> ebss = readebssFromCSV("ebs.csv");
		List<reu> reus = readreusFromCSV("reu.csv");

		for (bbg b : bbgs) {
			System.out.println(b);
		}

		for (ebs c : ebss) {
			System.out.println(c);
		}

		for (reu d : reus) {

			System.out.println(d);
		}

		String[][][] timestampName = new String[50][50][50];
		
		
		
		int[] bid = new int[40];
		int[] ask = new int[40];

		double totalprofit = 0;
		for (int i = 0; i < bid.length; i++) {
			if (ask[i] > bid[i]) {
				double profit = ask[i] - bid[i];
				totalprofit =+profit;

				i++;
			}
		
			
		}
	}

	private static List<bbg> readbbgsFromCSV(String fileName) {
		List<bbg> bbgs = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);

		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

			String line = br.readLine();

			while (line != null) {

				String[] attributes = line.split(",");

				bbg bbg = createbbg(attributes);

				bbgs.add(bbg);

				line = br.readLine();

			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return bbgs;
	}

	private static List<ebs> readebssFromCSV(String fileName) {
		List<ebs> ebss = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);
		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {
			String line = br.readLine();

			while (line != null) {

				String[] attributes = line.split(",");

				ebs ebs = createebs(attributes);

				ebss.add(ebs);
				line = br.readLine();
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return ebss;
	}

	private static List<reu> readreusFromCSV(String fileName) {
		List<reu> reus = new ArrayList<>();
		Path pathToFile = Paths.get(fileName);

		try (BufferedReader br = Files.newBufferedReader(pathToFile, StandardCharsets.US_ASCII)) {

			String line = br.readLine();
			while (line != null) {
				String[] attributes = line.split(",");

				reu reu = createreu(attributes);
				reus.add(reu);
				line = br.readLine();

			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		return reus;
	}

	private static bbg createbbg(String[] metadata) {
		String name = "bbg";
		String timestamp = (metadata[0]);
		String currencyPair = metadata[1];
		double bid = Double.parseDouble(metadata[2]);
		double ask = Double.parseDouble(metadata[3]);
		return new bbg(name, timestamp, currencyPair, bid, ask);
	}

	private static ebs createebs(String[] metadata) {
		String name = "ebs";
		String timestamp = (metadata[0]);

		String currencyPair = metadata[1];
		double bid = Double.parseDouble(metadata[2]);
		double ask = Double.parseDouble(metadata[3]);

		return new ebs(name, timestamp, currencyPair, bid, ask);
	}

	private static reu createreu(String[] metadata) {
		String name = "reu";
		String timestamp = (metadata[0]);

		String currencyPair = metadata[1];
		double bid = Double.parseDouble(metadata[2]);
		double ask = Double.parseDouble(metadata[3]);

		return new reu(name, timestamp, currencyPair, bid, ask);
	}

}
