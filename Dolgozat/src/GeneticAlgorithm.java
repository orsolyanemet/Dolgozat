import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
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

	private static ArrayList<ArrayList<Object>> requests;
	private static ArrayList<ArrayList<Object>> rooms;
	private static ArrayList<ArrayList<Object>> roomTypes;

	private static ArrayList<ArrayList<ArrayList<Object>>> population;
	private static ArrayList<ArrayList<Object>> initialChromosome;
	private static int[] fitnessOfChromosomes;

	private static void getParameters() throws IOException {
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

	private static void getRequestsFromDB() {
		/*
		 * Beolvassa a kereseket az adatbazisbol. Request:[id1,(fromtime1,totime1),
		 * idRoom1, duration1]
		 */
		requests = new ArrayList<ArrayList<Object>>();
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Reservation");
		List<Document> requestsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document request : requestsFromDB) {
			ArrayList<Object> properties = new ArrayList<Object>();
			ArrayList<Date> time = new ArrayList<Date>();
			List<Date> timesFromDB = (List<Date>) request.get("time");
			time.add(0, timesFromDB.get(0));
			time.add(1, timesFromDB.get(1));
			properties.add(0, request.getObjectId("_id"));
			properties.add(1, time);
			properties.add(2, request.getObjectId("idRoom"));
			properties.add(3, request.getInteger("duration"));
			requests.add(properties);
		}
	}

	private static void getRoomsFromDB() {
		/*
		 * Beolvassa a kereseket az adatbazisbol. Room:[id1,[property1, property2,...]]
		 */
		rooms = new ArrayList<ArrayList<Object>>();
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Room");
		List<Document> roomsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document room : roomsFromDB) {
			ArrayList<Object> properties = new ArrayList<Object>();
			properties.add(0, room.getObjectId("_id"));
			List<ObjectId> roomPropertiesFromDB = (List<ObjectId>) room.get("roomTypeList");
			properties.add(1, roomPropertiesFromDB);
			rooms.add(properties);
		}
	}

	private static void getRoomTypes() {
		/*
		 * Beolvassa a kereseket az adatbazisbol.
		 * RoomProperty:[idProperty1,[equivalent]]
		 */
		roomTypes = new ArrayList<ArrayList<Object>>();
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "RoomType");
		List<Document> roomTypesFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomType : roomTypesFromDB) {
			ArrayList<Object> properties = new ArrayList<Object>();
			properties.add(0, roomType.getObjectId("_id"));
			List<ObjectId> equivalentPropertiesFromDB = (List<ObjectId>) roomType.get("equivalentTo");
			properties.add(1, equivalentPropertiesFromDB);
			roomTypes.add(properties);
		}
	}

	private static void readDataFromDB() {
		connection = MongoConnectionManager.getConnection();
		getRequestsFromDB();
		getRoomsFromDB();
		getRoomTypes();
		connection.close();
	}

	private static void getInitialChromosome() {
		initialChromosome=new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < requests.size(); i++) {
			//timeMutation for all requests
		}
	}

	private static void getInitialPopulation() {
		/*
		 * Tobbszoros mutaciot(ido, szoba) alkalmazva az adatbazibol kiolvasott egyeden
		 * megkapja a kezdeti populaciot
		 */
		population = new ArrayList<ArrayList<ArrayList<Object>>>();
		ArrayList<ArrayList<Object>> mutatedChromosome = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < populationSize; i++) {
			mutatedChromosome = timeMutation(initialChromosome);
			mutatedChromosome = roomMutation(mutatedChromosome);
			population.add(mutatedChromosome);
		}
	}

	//TO-DO
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
				int duration = (int) requests.get(i).get(3);
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
	
	private static int getRoomIndex(ObjectId roomId) {
		boolean found = false;
		int i = 0;
		while (!found && i < rooms.size()) {
			if (roomId.equals(rooms.get(i).get(0))) {
				found=true;
				return i;
			}
			i++;
		}
		return -1;
	}
	
	private static int getRoomTypeIndex(ObjectId roomTypeId) {
		boolean found = false;
		int i = 0;
		while (!found && i < roomTypes.size()) {
			if (roomTypeId.equals(roomTypes.get(i).get(0))) {
				found=true;
				return i;
			}
			i++;
		}
		return -1;
	}
	
	private static List<ObjectId> addEquivalentRoomProperties(ObjectId roomId) {
		int i= getRoomIndex(roomId);
		//Lekerem a tulajdonsagait
		List<ObjectId> roomProperties = new ArrayList<ObjectId>();
	    List<ObjectId> properties = (List<ObjectId>) rooms.get(i).get(1);
	    for(i=0;i<properties.size();i++) {
	    	roomProperties.add(properties.get(i));
	    }
	    nrOfAddedProperties = 0;
	    int n=roomProperties.size();
	    //Vegigmegyek a tulajdonsagokon
		for (i = 0; i < n; i++) {
			//Megnezem, hogy van-e ekvivalense.
			int j=getRoomTypeIndex(roomProperties.get(i));
			if(roomTypes.get(j).get(1)!=null) {
				List<ObjectId> equivalents=(List<ObjectId>)roomTypes.get(j).get(1);
				for(int g=0;g<equivalents.size();g++) {
					roomProperties.add(equivalents.get(g));
					nrOfAddedProperties++;
				}
			}	
		}
		return roomProperties;
	}

	private static boolean isEquivalentRoom(ObjectId actualRoomId, ObjectId newRoomId) {
		// Meghatarozza, hogya ket parameterkent megadott szoba ekvivelens-e
		// Lekerem a ket szoba tulajdonsagait
		List<ObjectId> actualRoomProperties = new ArrayList<ObjectId>();
		List<ObjectId> equivalentProperties = addEquivalentRoomProperties(actualRoomId);;
		for(int i=0;i<equivalentProperties.size();i++) {
	    	actualRoomProperties.add(equivalentProperties.get(i));
	    }
		int i= getRoomIndex(newRoomId);
	    List<ObjectId> newRoomProperties = (List<ObjectId>) rooms.get(i).get(1);
		int nrOfFoundProperties = 0;
		// Megkeresi, hogy hany kozos tulajdonsaga van a ket szobanak
		for (i = 0; i < actualRoomProperties.size(); i++) {
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
		// Vegigmegyek a kromoszoma genjein
		for (int i = 0; i < chromosome.size(); i++) {
			// Eldontom, hogy mutalom-e
			ArrayList<Object> gene = new ArrayList<Object>();
			double toMutate = Math.random();
			if (toMutate > pmr) {
				// Amig nem talalok olyan szobat, amivel mutalom
				boolean found = false;
				while (!found) {
					// Generalok egy random szamot 0 es a szoba db szam kozott
					Random r = new Random();
					ObjectId newRoomId = (ObjectId) rooms.get(r.nextInt((rooms.size()))).get(0);
					// Lekerem az aktualis szoba id-t
					ObjectId actualRoomId = (ObjectId) chromosome.get(i).get(2);
					// Ha nem ugyanaz a szoba es az uj szoba a regivel ekvivalens akkor mutalok
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

	private static int partition(ArrayList<ArrayList<Object>> chromosomeToSort, int low, int high) {
		List<Date> times = (List<Date>) chromosomeToSort.get(high).get(1);
		Date pivotTime = times.get(0);
		int i = (low - 1);
		for (int j = low; j < high; j++) {
			// If current element is before pivotTime
			List<Date> currentTimes = (List<Date>) chromosomeToSort.get(j).get(1);
			Date currentFromTime = currentTimes.get(0);
			if (currentFromTime.before(pivotTime)) {
				i++;
				ArrayList<Object> temp = chromosomeToSort.get(i);
				chromosomeToSort.set(i, chromosomeToSort.get(j));
				chromosomeToSort.set(j, temp);
			}
		}
		ArrayList<Object> temp = chromosomeToSort.get(i + 1);
		chromosomeToSort.set(i + 1, chromosomeToSort.get(high));
		chromosomeToSort.set(high, temp);
		return i + 1;
	}

	private static void quickSortGenesByTime(ArrayList<ArrayList<Object>> chromosomeToSort, int low, int high) {
		// Rendezi a kromoszomaban szereplo geneket kezdesi ido szerint
		if (low < high) {
			int partitioningIndex = partition(chromosomeToSort, low, high);
			quickSortGenesByTime(chromosomeToSort, low, partitioningIndex - 1);
			quickSortGenesByTime(chromosomeToSort, partitioningIndex + 1, high);
		}
	}

	private static int countCollisions(ArrayList<ArrayList<Object>> chromosome) {
		// Egy kromoszomaban szereplo utkozesek szamat adja meg
		ArrayList<ArrayList<Object>> chromosomeToSort = new ArrayList<ArrayList<Object>>();
		for (int i = 0; i < chromosome.size(); i++) {
			chromosomeToSort.add(chromosome.get(i));
		}
		quickSortGenesByTime(chromosomeToSort, 0, chromosomeToSort.size() - 1);
		boolean[] isChecked = new boolean[chromosomeToSort.size()];
		for (int i = 0; i < chromosomeToSort.size(); i++) {
			isChecked[i] = false;
		}
		int i = 0;
		int collisions = 0;
		while (i < chromosomeToSort.size()) {
			isChecked[i] = true;
			List<Date> times1 = (List<Date>) chromosomeToSort.get(i).get(1);
			Date toTime = times1.get(1);
			for (int j = i + 1; j < chromosomeToSort.size(); j++) {
				if (chromosomeToSort.get(i).get(2).equals(chromosomeToSort.get(j).get(2)) && isChecked[j] == false) {
					isChecked[j] = true;
					List<Date> times2 = (List<Date>) chromosomeToSort.get(j).get(1);
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
		System.out.println("Az eddigi legfittebb kromoszoma:");
		System.out.println("Utkozesek:" + fitnessValue);
		int chromosomeSize = population.get(0).size();
		for (int i = 0; i < chromosomeSize; i++) {
			System.out.println(population.get(fitnessValueIndex).get(i));
		}
	}

	private static void geneticAlgorithm() {
		getInitialPopulation();
		// A kezdeti populacio tartalmazza-e a legjobb/legfittebb egyedet
		int i = 0;
		boolean found = false;
		int iter = 0;
		found = false;
		while (iter < maxIteration && !found) { // Fitness fuggveny kiertekelese a populaciora
			fitnessCalculation();
			while (i < populationSize && !found) {
				if (isTheFittestChromosome(population.get(i))) {
					found = true;
					printFittestChromosome(i);
				}
				i++;
			}
			if (!found) { // Szelecioval uj populacio
				// selection(); // Keresztezem a kivalasztott kromoszomakat population =
				crossover(population); // Mutalom(szoba es ido szerint) a kivalasztott kromoszomakat
				population = mutation(population);
			}
			iter++;
		}
		if (!found) {
			foundFitChromosome();
		}
	}

	private static void printChromosome(ArrayList<ArrayList<Object>> chromosome) {
		for (int j = 0; j < chromosome.size(); j++) {
			System.out.println(chromosome.get(j));
		}
		System.out.println();
	}

	private static void printPopulation(ArrayList<ArrayList<ArrayList<Object>>> population) {
		for (int i = 0; i < population.size(); i++) {
			for (int j = 0; j < population.get(i).size(); j++) {
				System.out.println(population.get(i).get(j));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void main(String args[]) throws IOException, InterruptedException {
		long startTime = System.currentTimeMillis();
		getParameters();
		readDataFromDB();
		getInitialPopulation();
		printPopulation(population);
		// geneticAlgorithm();
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Run time:" + totalTime);
	}
}
