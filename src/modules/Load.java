package modules;

import static modules.Data.facilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import modules.Facility.Risk;
import modules.Facility.Type;
import modules.Inspection.Result;
import structures.FacilityWeightedGraph;

/**
 * Loads, processes and/or updates the data.
 * 
 * @author Debonair Coders
 */
public class Load {
	// The data directory.
	private static String directory = System.getProperty("user.dir")
			+ "\\data\\";

	// The url to download from.
	private static String url = "https://data.cityofchicago.org/api/views/4ijn-s7e5/rows.csv?accessType=DOWNLOAD&bom=true&format=true";

	/**
	 * Downloads the csv file.
	 */
	private static void download() throws IOException {
		ReadableByteChannel rbc = Channels
				.newChannel(new URL(url).openStream());
		FileOutputStream fos = new FileOutputStream(directory
				+ "\\inspections.csv");
		fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
	}

	/**
	 * Checks if the data needs to be updated.
	 * @throws ParseException 
	 * @throws NumberFormatException 
	 */
	public static void update() throws IOException, NumberFormatException, ParseException {
		BufferedReader m = null;
		try {
			m = new BufferedReader(new FileReader(directory + "version.txt"));
		} catch (FileNotFoundException e) {
			File version = new File(directory + "version.txt");
			version.createNewFile();
			m = new BufferedReader(new FileReader(directory + "version.txt"));
		} finally {
			String line = m.readLine();

			if (line != null) {
				Calendar last = Calendar.getInstance();
				Calendar curr = Calendar.getInstance();
				last.setTimeInMillis(Long.parseLong(line));
				curr.setTimeInMillis(System.currentTimeMillis());

				if (curr.get(Calendar.DAY_OF_WEEK) != 6
						|| (curr.get(Calendar.YEAR) == last.get(Calendar.YEAR) && curr
								.get(Calendar.DAY_OF_YEAR) == last
								.get(Calendar.DAY_OF_YEAR))) {
					m.close();
					return;
				}
			} else {
				m.close();
				BufferedWriter w = new BufferedWriter(new FileWriter(
						System.getProperty("user.dir") + "/data/version.txt"));
				w.write(Long.toString(System.currentTimeMillis()));
				w.close();
			}
		}

		download();

		ProcessBuilder pb = new ProcessBuilder("python", "preprocessor.py");
		pb.directory(new File(System.getProperty("user.dir") + "/data/"));
		Process p = pb.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				p.getInputStream()));

		if (new Integer(in.readLine()).intValue() != 0)
			throw new IOException();

		process(System.getProperty("user.dir") + "/data/inspections-out.csv",
				System.getProperty("user.dir") + "/data/inspections.cfi");
	}

	/**
	 * Process the written file from the python script to a cfi file.
	 * 
	 * @param in
	 *            The input file
	 * @param out
	 *            The output file
	 */
	private static void process(String in, String out) throws ParseException,
			NumberFormatException, IOException {

		ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(
				new File(out)));

		Scanner input = new Scanner(new File(in));
		input.useDelimiter(",[\"][(][^\"]*[)][\"][\n\r]|\"\"[\n\r]");
		input.nextLine();

		while (input.hasNext()) {
			String line = input.next().trim();
			String[] data = line.trim().split("(?<=\"),(?=\")");

			for (int i = 0; i < data.length; i++)
				data[i] = data[i].replaceAll("\"", "");

			if (data.length != 16 || data[3].isEmpty())
				continue;

			String violations = data[13].trim();

			Pattern p = Pattern.compile("(?<=\\s)\\d{1,2}|^\\d{1,2}");
			Matcher m = p.matcher(violations);
			ArrayList<Integer> numViol = new ArrayList<Integer>();
			while (m.find()) {
				numViol.add(Integer.parseInt(m.group()));
			}
			int[] inViol = new int[numViol.size()];
			for (int i = 0; i < numViol.size(); i++) {
				inViol[i] = numViol.get(i);
			}

			int inspectionID = Integer.parseInt(data[0].trim());
			Calendar c = Calendar.getInstance();
			c.setTime((new SimpleDateFormat("MM/dd/yyyy")).parse(data[10]
					.trim()));
			Result result = Result.parse(data[12].trim());

			String address = data[6].trim();
			int license = Integer.parseInt(data[3].trim());

			Inspection inspection = new Inspection(inspectionID, inViol,
					result, c.getTime());

			boolean found = false;

			for (Facility f : facilities) {
				if (f.getAddress().equals(address)) {
					if (f.getLicense() != license) {
						f.setLicense(license);
					}

					f.addInspection(inspection);
					found = true;
					break;
				}
			}

			if (!found) {
				if (data[9].isEmpty() || data[14].isEmpty())
					continue;

				int zip = Integer.parseInt(data[9].trim());
				double latitude = Double.parseDouble(data[14]);
				double longitude = Double.parseDouble(data[15]);
				Risk risk = Risk.parse(data[5].trim());
				String name = data[1].trim();

				Type type = Type.parse(data[4].trim());

				facilities.add(new Facility(name, license, address, zip,
						latitude, longitude, type, risk, inspection));

			}
		}

		input.close();

		o.writeObject(facilities);

		o.close();
	}

	/**
	 * Read the data from a cfi file.
	 * 
	 * @param file
	 *            The file path
	 */
	public static void read(String file) throws IOException,
			ClassNotFoundException {
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(
				new File(file)));
		facilities = (ArrayList<Facility>) input.readObject();
		Data.graph = new FacilityWeightedGraph(
				(ArrayList<Facility>) facilities.clone());
		input.close();
	}
}
