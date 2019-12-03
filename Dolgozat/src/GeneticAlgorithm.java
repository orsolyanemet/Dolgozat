import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.bson.Document;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class GeneticAlgorithm {

	private static ArrayList<ArrayList<ArrayList<Object>>> population;

	private static int[] fitnessOfChromosomes;

	private static MongoCollection<Document> collection;

	private static MongoClient connection;

	private static int maxIteration;
	private static double pm;
	private static double pc;
	private static double pmt;
	private static double pmr;
	private static int minStartTime;
	private static int maxEndTime;
	private static int populationSize;
	private static int nrOfAddedProperties;

	private static void getPropertyValues() throws IOException {
		// Beolvassa a parametereket a config.txt filebol
		try (InputStream input = new FileInputStream("resources/config")) {
			Properties properties = new Properties();
			properties.load(input);
			maxIteration = Integer.parseInt(properties.getProperty("maxIteration"));
			pm = Double.parseDouble(properties.getProperty("pm"));
			pc = Double.parseDouble(properties.getProperty("pc"));
			pmt = Double.parseDouble(properties.getProperty("pmt"));
			pmr = Double.parseDouble(properties.getProperty("pmr"));
			minStartTime = Integer.parseInt(properties.getProperty("minStartTime"));
			maxEndTime = Integer.parseInt(properties.getProperty("maxEndTime"));
			populationSize = Integer.parseInt(properties.getProperty("populationSize"));
			fitnessOfChromosomes = new int[populationSize];
		} catch (IOException ex) {
			System.err.println("Exception: " + ex);
		}
	}

	private static ArrayList<ArrayList<Object>> readChromosomeFromDB() {
		/*
		 * Beolvassa a kromoszomat az adatbazisbol. A kromoszoma reprezentacioja egy
		 * tomb lesz, mely tartalmazza: - reservationId - time tombot (fromTime,toTime)
		 * - roomId
		 */
		ArrayList<ArrayList<Object>> chromosome = new ArrayList<ArrayList<Object>>();
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Reservation");
		List<Document> reservations = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document reservation : reservations) {
			ArrayList<Object> gene = new ArrayList<Object>();
			ArrayList<Date> time = new ArrayList<Date>();
			List<Date> timesFromDB = (List<Date>) reservation.get("time");
			time.add(0, timesFromDB.get(0));
			time.add(1, timesFromDB.get(1));
			gene.add(0, reservation.getObjectId("_id"));
			gene.add(1, time);
			gene.add(2, reservation.getObjectId("idRoom"));
			chromosome.add(gene);
		}
		return chromosome;
	}

	private static void getInitialPopulation() {
		/*
		 * Tobbszoros mutaciot(ido, szoba) alkalmazva az adatbazibol kiolvasott egyeden
		 * megkapja a kezdeti populaciot
		 */
		population = new ArrayList<ArrayList<ArrayList<Object>>>();
		ArrayList<ArrayList<Object>> initialChromosome = readChromosomeFromDB();
		ArrayList<ArrayList<Object>> mutatedChromosome = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < populationSize; i++) {
			mutatedChromosome = timeMutation(initialChromosome);
			mutatedChromosome = roomMutation(mutatedChromosome);
			population.add(mutatedChromosome);
		}
	}

	private static int findDuration(ArrayList<ArrayList<Object>> chromosome, int chromosomeId) {
		// Megkeresi egy gennek az id-ja utan a duration parameterjet
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Reservation");
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id", chromosome.get(chromosomeId).get(0));
		Document reservation = (Document) collection.find(whereQuery).first();
		return reservation.getInteger("duration");
	}

	private static ArrayList<ArrayList<Object>> timeMutation(ArrayList<ArrayList<Object>> chromosome) {
		/*
		 * Elvegezi egy adott kromoszoman az ido szerinti mutaciot
		 */
		ArrayList<ArrayList<Object>> mutatedChromosome = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < chromosome.size(); i++) {
			ArrayList<Object> gene = new ArrayList<Object>();
			ArrayList<Date> time = new ArrayList<Date>();
			double toMutate = Math.random();
			if (toMutate > pmt) {
				List<Date> times = (List<Date>) chromosome.get(i).get(1);
				Date fromTime = times.get(0);
				Date toTime = times.get(1);
				int duration = findDuration(chromosome, i);
				if ((fromTime.getDate() == toTime.getDate()
						&& toTime.getTime() - fromTime.getTime() > duration * 3600 * 1000)) {
					/*
					 * Datum egyezes eseten az idot csak akkor lehet mutalni, ha a kezdes es
					 * befejezes kozott nagyobb ido van, mint az esemeny idotartama
					 */
					boolean found = false;
					while (!found) {
						Date newFromTime = new Date(
								ThreadLocalRandom.current().nextLong(fromTime.getTime(), toTime.getTime()));
						Date durationTime = new Date(toTime.getTime() - duration * 3600 * 1000);
						if (newFromTime.before(durationTime)) {
							fromTime = newFromTime;
							toTime = new Date(fromTime.getTime() + duration * 3600 * 1000);
							found = true;
						}
					}
				} else if (fromTime.getDate() != toTime.getDate()) {
					boolean found = false;
					while (!found) {
						long random = ThreadLocalRandom.current().nextLong(fromTime.getTime(), toTime.getTime());
						Date newFromTime = new Date(random);
						Date durationTime = new Date(toTime.getTime() - duration * 3600 * 1000);
						if (newFromTime.before(durationTime) && newFromTime.getHours() <= maxEndTime - duration
								&& newFromTime.getHours() >= minStartTime) {
							/*
							 * Megbizonyosodunk arrol hogy az uj esemeny nem kezdodik egy minimalis kezdesi
							 * idonel hamarabb, illetve, hogy nem vegzodik egy maximalis befejezesi ido utan
							 */
							fromTime = newFromTime;
							toTime = new Date(fromTime.getTime() + duration * 3600 * 1000);
							found = true;
						}
					}
				}
				time.add(0, fromTime);
				time.add(1, toTime);
				gene.add(0, chromosome.get(i).get(0));
				gene.add(1, time);
				gene.add(2, chromosome.get(i).get(2));
			} else {
				gene = chromosome.get(i);
			}
			mutatedChromosome.add(gene);
		}
		return mutatedChromosome;
	}

	private static int countRooms() {
		// Megadja, hogy hany szoba all rendelkezesunkre
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Room");
		int numberOfRooms = (int) collection.countDocuments();
		return numberOfRooms;
	}

	private static ArrayList<ObjectId> findRoomIds() {
		// Megadja a szobak id-jat
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Room");
		List<Document> rooms = (List<Document>) collection.find().into(new ArrayList<Document>());
		ArrayList<ObjectId> roomIds = new ArrayList<ObjectId>();
		for (int i = 0; i < rooms.size(); i++) {
			roomIds.add(i, rooms.get(i).getObjectId("_id"));
		}
		return roomIds;
	}

	private static ArrayList<ObjectId> getRoomProperties(ObjectId roomId, boolean checkEquivalentTo) {
		// Egy bizonyos szobanak a tulajdonsagait teriti vissza
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Room");
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id", roomId);
		Document room = (Document) collection.find(whereQuery).first();
		List<ObjectId> propertiesFromDB = (List<ObjectId>) room.get("roomTypeList");
		ArrayList<ObjectId> properties = new ArrayList<>();
		for (int i = 0; i < propertiesFromDB.size(); i++) {
			properties.add(propertiesFromDB.get(i));
		}
		if (checkEquivalentTo) {
			nrOfAddedProperties = 0;
			db = MongoConnectionManager.getDatabase(connection);
			collection = MongoConnectionManager.getCollection(db, "RoomType");
			for (int i = 0; i < propertiesFromDB.size(); i++) {
				whereQuery = new BasicDBObject();
				whereQuery.put("_id", propertiesFromDB.get(i));
				Document roomType = (Document) collection.find(whereQuery).first();
				if (roomType.get("equivalentTo") != null) {
					List<ObjectId> equivalentToFromDB = (List<ObjectId>) roomType.get("equivalentTo");
					for (int j = 0; j < equivalentToFromDB.size(); j++) {
						properties.add(equivalentToFromDB.get(j));
						nrOfAddedProperties++;
					}
				}
			}
		}
		return properties;
	}

	private static boolean isEquivalentRoom(ObjectId actualRoomId, ObjectId newRoomId) {
		// Meghatarozza, hogya ket parameterkent megadott szoba ekvivelens-e
		ArrayList<ObjectId> actualRoomProperties = getRoomProperties(actualRoomId, true);
		ArrayList<ObjectId> newRoomProperties = getRoomProperties(newRoomId, false);
		int nrOfFoundProperties = 0;
		for (int i = 0; i < actualRoomProperties.size(); i++) {
			int j = 0;
			boolean found = false;
			while (j < newRoomProperties.size() && !found) {
				if (actualRoomProperties.get(i).equals(newRoomProperties.get(j))) {
					found = true;
					nrOfFoundProperties++;
				}
				j++;
			}
		}
		return (((actualRoomProperties.size() - nrOfAddedProperties) <= newRoomProperties.size())
				&& ((actualRoomProperties.size() - nrOfAddedProperties) == nrOfFoundProperties));
	}

	private static ArrayList<ArrayList<Object>> roomMutation(ArrayList<ArrayList<Object>> chromosome) {
		/*
		 * Elvegezi egy adott kromoszoman a szoba szerinti mutaciot
		 */
		ArrayList<ArrayList<Object>> mutatedChromosome = new ArrayList<ArrayList<Object>>();
		int nrOfRooms = countRooms();
		ArrayList<ObjectId> roomIds = findRoomIds();
		for (int i = 0; i < chromosome.size(); i++) {
			ArrayList<Object> gene = new ArrayList<Object>();
			double toMutate = Math.random();
			if (toMutate > pmr) {
				boolean found = false;
				while (!found) {
					Random r = new Random();
					ObjectId newRoomId = roomIds.get(r.nextInt((nrOfRooms)));
					ObjectId actualRoomId = (ObjectId) chromosome.get(i).get(2);
					if (!actualRoomId.equals(newRoomId) && isEquivalentRoom(actualRoomId, newRoomId)) {
						found = true;
						gene.add(0, chromosome.get(i).get(0));
						gene.add(1, chromosome.get(i).get(1));
						gene.add(2, newRoomId);
					}
				}
			} else {
				gene = chromosome.get(i);
			}
			mutatedChromosome.add(gene);
		}
		return mutatedChromosome;
	}

	private static ArrayList<ArrayList<ArrayList<Object>>> mutation(
			ArrayList<ArrayList<ArrayList<Object>>> populationToMutate) {
		/*
		 * A parameterkent kapott populaciobol random egyedeket kivalaszt, majd elvegzi
		 * rajtuk az ido, illetve szoba szerinti mutaciot
		 */
		for (int i = 0; i < populationSize; i++) {
			double toMutate = Math.random();
			if (toMutate > pm) {
				ArrayList<ArrayList<Object>> mutatedChromosome = timeMutation(populationToMutate.get(i));
				mutatedChromosome = roomMutation(mutatedChromosome);
				populationToMutate.set(i, mutatedChromosome);
			}
		}
		return populationToMutate;
	}

	private static void doCrossover(ArrayList<ArrayList<Object>> chromosome1,
			ArrayList<ArrayList<Object>> chromosome2) {
		/*
		 * Valasztunk egy random keresztezesi pontot, majd a ket kromoszomat
		 * keresztezzuk annal a pontnal
		 */
		Random r = new Random();
		int crossPoint = r.nextInt((chromosome1.size()));
		for (int i = crossPoint; i < chromosome1.size(); i++) {
			ArrayList<Object> temp = chromosome1.get(i);
			chromosome1.set(i, chromosome2.get(i));
			chromosome2.set(i, temp);
		}
	}

	private static ArrayList<ArrayList<ArrayList<Object>>> crossover(
			ArrayList<ArrayList<ArrayList<Object>>> crossoverPopulation) {
		// Kivalaszt random kromoszomakat a parameterkent megadott populaciobol
		ArrayList<Integer> indexOfChromosomes = new ArrayList<Integer>();
		for (int i = 0; i < populationSize; i++) {
			double toCrossover = Math.random();
			if (toCrossover > pc) {
				indexOfChromosomes.add(i);
			}
		}
		int i = 0;
		while (i < indexOfChromosomes.size() - 1) {
			// Egy kromoszoma parra meghivja a keresztezo fuggvenyt
			doCrossover(crossoverPopulation.get(indexOfChromosomes.get(i)),
					crossoverPopulation.get(indexOfChromosomes.get(i + 1)));
			i = i + 2;
		}
		return crossoverPopulation;
	}

	private static int partition(ArrayList<ArrayList<Object>> chromosome, int low, int high) {
		List<Date> times = (List<Date>) chromosome.get(high).get(1);
		Date pivotTime = times.get(0);
		int i = (low - 1);
		for (int j = low; j < high; j++) {
			// If current element is before pivotTime
			List<Date> currentTimes = (List<Date>) chromosome.get(j).get(1);
			Date currentFromTime = currentTimes.get(0);
			if (currentFromTime.before(pivotTime)) {
				i++;
				ArrayList<Object> temp = chromosome.get(i);
				chromosome.set(i, chromosome.get(j));
				chromosome.set(j, temp);
			}
		}
		ArrayList<Object> temp = chromosome.get(i + 1);
		chromosome.set(i + 1, chromosome.get(high));
		chromosome.set(high, temp);
		return i + 1;
	}

	private static void quickSortGenesByTime(ArrayList<ArrayList<Object>> chromosome, int low, int high) {
		// Rendezi a kromoszomaban szereplo geneket kezdesi ido szerint
		if (low < high) {
			int partitioningIndex = partition(chromosome, low, high);
			quickSortGenesByTime(chromosome, low, partitioningIndex - 1);
			quickSortGenesByTime(chromosome, partitioningIndex + 1, high);
		}
	}

	private static int countCollisions(ArrayList<ArrayList<Object>> chromosome) {
		// Egy kromoszomaban szereplo utkozesek szamat adja meg
		quickSortGenesByTime(chromosome, 0, chromosome.size() - 1);
		boolean[] isChecked = new boolean[chromosome.size()];
		for (int i = 0; i < chromosome.size(); i++) {
			isChecked[i] = false;
		}
		int i = 0;
		int collisions = 0;
		while (i < chromosome.size()) {
			isChecked[i] = true;
			List<Date> times1 = (List<Date>) chromosome.get(i).get(1);
			Date toTime = times1.get(1);
			for (int j = i + 1; j < chromosome.size(); j++) {
				if (chromosome.get(i).get(2).equals(chromosome.get(j).get(2)) && isChecked[j] == false) {
					isChecked[j] = true;
					List<Date> times2 = (List<Date>) chromosome.get(j).get(1);
					Date fromTime = times2.get(0);
					if (toTime.before(fromTime) || toTime.getTime() == fromTime.getTime()) {
						toTime = times2.get(1);
					} else {
						collisions++;
					}
				}
			}
			i++;
		}
		return collisions;
	}

	private static boolean isTheFittestChromosome(ArrayList<ArrayList<Object>> chromosome) {
		// Ha az utkozesek szama, akkor ez a legfittebb egyed
		return countCollisions(chromosome) == 0;
	}

	private static void fitnessCalculation() {
		for (int i = 0; i < populationSize; i++) {
			// !!!! Otlet mivel a szoba biztos telejesiti a felteteleket ezert csak az ido
			// szerinti fitnessz marad ami az utkozesek szama annal fittebb az egyed minnel
			// kisebb az utkozes szamja
			fitnessOfChromosomes[i] = countCollisions(population.get(i));
		}
	}

	private static int sumFitness() {
		// F=sum(fitness[i]) i=0,population.size()-1
		int sum = 0;
		for (int i = 0; i < fitnessOfChromosomes.length; i++) {
			sum += fitnessOfChromosomes[i];
		}
		return sum;
	}

	private static int selection() {
		int totalOfFitness = sumFitness();
		Random rand = new Random();
		int randomSelection = rand.nextInt(totalOfFitness);
		int partialSum = 0;
		for (int i = 0; i < populationSize; i++) {
			partialSum += totalOfFitness - fitnessOfChromosomes[i];
			if (partialSum >= randomSelection)
				return i;
		}
		return -1;
	}

	private static void getNewPopulation() {
		ArrayList<ArrayList<ArrayList<Object>>> newPopulation = new ArrayList<ArrayList<ArrayList<Object>>>();
		fitnessCalculation();
		int newPopulationSize = 0;
		while (newPopulationSize < populationSize) {
			int selected = selection();
			if (selected != -1) {
				newPopulation.add(population.get(selected));
				newPopulationSize++;
			}
		}
		population = newPopulation;
	}

	private static void printFittestChromosome(int indexOfChromosome) {
		System.out.println("The fittest chromosome is:");
		for (int j = 0; j < population.get(indexOfChromosome).size(); j++) {
			System.out.println(population.get(indexOfChromosome).get(j));
		}
	}

	private static void foundFitChromosome() {
		fitnessCalculation();
		int fitnessValue = fitnessOfChromosomes[0];
		int fitnessValueIndex = 0;
		for (int i = 1; i < populationSize; i++) {
			if (fitnessValue > fitnessOfChromosomes[i]) {
				fitnessValue = fitnessOfChromosomes[i];
				fitnessValueIndex = i;
			}
		}
		System.out.println("Az eddigi legfittebb kromoszoma: utkozesek:" + fitnessValue);
		int chromosomeSize = population.get(0).size();
		for (int i = 0; i < chromosomeSize; i++) {
			System.out.println(population.get(fitnessValueIndex).get(i));
		}
	}

	private static void geneticAlgorithm() {
		// Populacio inicializalasa
		connection = MongoConnectionManager.getConnection();
		getInitialPopulation();
		// A kezdeti populacio tartalmazza-e a legjobb/legfittebb egyedet
		int i = 0;
		boolean found = false;
		int iter = 0;
		found = false;
		while (iter < maxIteration && !found) {
			// Fitnessz fuggveny kiertekelese a populaciora
			fitnessCalculation();
			while (i < populationSize && !found) {
				if (isTheFittestChromosome(population.get(i))) {
					found = true;
					printFittestChromosome(i);
				}
				i++;
			}
			if (!found) {
				// Szelecioval uj populacio
				selection();
				// Keresztezem a kivalasztott kromoszomakat
				population = crossover(population);
				// Mutalom(szoba es ido szerint) a kivalasztott kromoszomakat
				population = mutation(population);
			}
			iter++;
		}
		if (!found) {
			foundFitChromosome();
		}
		connection.close();
	}

	public static void main(String args[]) throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		getPropertyValues();
		geneticAlgorithm();
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Run time:" + totalTime);
	}
}
