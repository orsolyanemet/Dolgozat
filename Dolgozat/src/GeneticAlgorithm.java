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
		requests = new ArrayList<>();
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Reservation");
		List<Document> requestsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document request : requestsFromDB) {
			ArrayList<Object> properties = new ArrayList<>();
			ArrayList<Date> time = new ArrayList<>();
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
		rooms = new ArrayList<>();
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "Room");
		List<Document> roomsFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document room : roomsFromDB) {
			ArrayList<Object> properties = new ArrayList<>();
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
		roomTypes = new ArrayList<>();
		MongoDatabase db = MongoConnectionManager.getDatabase(connection);
		collection = MongoConnectionManager.getCollection(db, "RoomType");
		List<Document> roomTypesFromDB = (List<Document>) collection.find().into(new ArrayList<Document>());
		for (Document roomType : roomTypesFromDB) {
			ArrayList<Object> properties = new ArrayList<>();
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

	private static int optimizeGeneratedTime(int generatedMinutes) {
		if (generatedMinutes < 30)
			return 0;
		else
			return 30;
	}

	private static boolean isOnSameDate(Date fromTime, Date toTime) {
		return (fromTime.getDate() == toTime.getDate() && fromTime.getMonth() == toTime.getMonth()
				&& fromTime.getYear() == toTime.getYear());
	}

	private static boolean isTheNewTimeInTheInterval(Date newFromTime, Date newToTime) {
		// Vizsgalja, hogy az uj ido a minStartTime es maxEndTime kozott van-e
		return newFromTime.getHours() >= minStartTime && newFromTime.getHours() <= maxEndTime
				&& (newToTime.getHours() < maxEndTime
						|| (newToTime.getHours() == maxEndTime && newToTime.getMinutes() == 0));
	}

	private static ArrayList<Date> getInitialTime(int indexOfRequest) {
		List<Date> times = (List<Date>) requests.get(indexOfRequest).get(1);
		Date fromTime = times.get(0);
		Date toTime = times.get(1);
		int duration = (int) requests.get(indexOfRequest).get(3);
		ArrayList<Date> time = new ArrayList<>();
		if (isOnSameDate(fromTime, toTime) && toTime.getTime() - fromTime.getTime() > duration * 3600 * 1000) {
			/*
			 * Datum egyezes eseten az idot csak akkor lehet mutalni, ha a kezdes es
			 * befejezes kozott nagyobb ido van, mint az esemeny idotartama
			 */
			boolean found = false;
			while (!found) {
				Date newFromTime = new Date(ThreadLocalRandom.current().nextLong(fromTime.getTime(), toTime.getTime()));
				newFromTime.setMinutes(optimizeGeneratedTime(newFromTime.getMinutes()));
				newFromTime.setSeconds(0);
				Date durationTime = new Date(toTime.getTime() - duration * 3600 * 1000);
				if (newFromTime.before(durationTime)) {
					fromTime = newFromTime;
					toTime = new Date(fromTime.getTime() + duration * 3600 * 1000);
					found = true;
				}
			}
		} else if (!isOnSameDate(fromTime, toTime)) {
			boolean found = false;
			while (!found) {
				Date newFromTime = new Date(ThreadLocalRandom.current().nextLong(fromTime.getTime(), toTime.getTime()));
				newFromTime.setMinutes(optimizeGeneratedTime(newFromTime.getMinutes()));
				newFromTime.setSeconds(0);
				Date newToTime = new Date(newFromTime.getTime() + duration * 3600 * 1000);
				if (isTheNewTimeInTheInterval(newFromTime, newToTime)) {
					/*
					 * Megbizonyosodunk arrol hogy az uj esemeny nem kezdodik egy minimalis kezdesi
					 * idonel hamarabb, illetve, hogy nem vegzodik egy maximalis befejezesi ido utan
					 */
					fromTime = newFromTime;
					toTime = newToTime;
					found = true;
				}
			}
		}
		time.add(fromTime);
		time.add(toTime);
		return time;
	}

	private static void getInitialChromosome() {
		initialChromosome = new ArrayList<>();
		for (int i = 0; i < requests.size(); i++) {
			ArrayList<Object> gene = new ArrayList<>();
			gene.add(0, requests.get(i).get(0));
			gene.add(1, getInitialTime(i));
			gene.add(2, requests.get(i).get(2));
			initialChromosome.add(gene);
		}
	}

	private static void getInitialPopulation() {
		/*
		 * Tobbszoros mutaciot(ido, szoba) alkalmazva az adatbazibol kiolvasott egyeden
		 * megkapja a kezdeti populaciot
		 */
		population = new ArrayList<>();
		for (int i = 0; i < populationSize; i++) {
			population.add(roomMutation(timeMutation(initialChromosome)));
		}
	}

	private static List<Date> getTimeLimit(int indexOfChromosome, Date fromTime) {
		List<Date> timeLimit = (List<Date>) requests.get(indexOfChromosome).get(1);
		Date fromTimeLimit = timeLimit.get(0);
		Date toTimeLimit = timeLimit.get(1);
		if (!isOnSameDate(fromTimeLimit, toTimeLimit)) {
			fromTimeLimit.setYear(fromTime.getYear());
			fromTimeLimit.setMonth(fromTime.getMonth());
			fromTimeLimit.setDate(fromTime.getDate());
			fromTimeLimit.setHours(minStartTime);
			fromTimeLimit.setMinutes(0);
			toTimeLimit.setYear(fromTime.getYear());
			toTimeLimit.setMonth(fromTime.getMonth());
			toTimeLimit.setDate(fromTime.getDate());
			toTimeLimit.setHours(maxEndTime);
			toTimeLimit.setMinutes(0);
		}
		return timeLimit;
	}

	private static long getRandomTimeForMutation(Date fromTime, Date toTime, Date fromTimeLimit, Date toTimeLimit) {
		boolean forward = true;
		boolean backward = true;
		if (fromTime.getTime() - fromTimeLimit.getTime() == 0)
			forward = false;
		if (toTimeLimit.getTime() - toTime.getTime() == 0)
			backward = false;
		if (forward && !backward) {
			if (fromTime.getHours() <= 12)
				return ThreadLocalRandom.current().nextLong(fromTimeLimit.getTime(),
						fromTime.getTime() + 12 * 3600 * 1000);
			else
				return ThreadLocalRandom.current().nextLong(fromTimeLimit.getTime(), fromTime.getTime());
		} else {
			if (!forward && backward)
				return ThreadLocalRandom.current().nextLong(fromTime.getTime(), toTimeLimit.getTime());
			else {
				double random = Math.random();
				if (random < 0.5) {
					return ThreadLocalRandom.current().nextLong(fromTimeLimit.getTime(), fromTime.getTime());
				} else {
					return ThreadLocalRandom.current().nextLong(fromTime.getTime(), toTimeLimit.getTime());
				}
			}
		}
	}

	private static boolean newTimeCheck(Date fromTimeLimit, Date toTimeLimit, Date fromTime, Date newFromTime,
			Date newToTime) {
		return fromTimeLimit.getTime() <= newFromTime.getTime() && toTimeLimit.getTime() >= newToTime.getTime()
				&& newFromTime.getHours() >= minStartTime
				&& (newToTime.getHours() < maxEndTime
						|| (newToTime.getHours() == maxEndTime && newToTime.getMinutes() == 0))
				&& (fromTime.getHours() != newFromTime.getHours() || (fromTime.getHours() == newFromTime.getHours()
						&& fromTime.getMinutes() != newFromTime.getMinutes()));
	}

	private static ArrayList<ArrayList<Object>> timeMutation(ArrayList<ArrayList<Object>> chromosome) {
		/*
		 * Elvegezi egy adott kromoszoman az ido szerinti mutaciot
		 */
		ArrayList<ArrayList<Object>> mutatedChromosome = new ArrayList<>();
		for (int i = 0; i < chromosome.size(); i++) {
			ArrayList<Object> gene = new ArrayList<>();
			ArrayList<Date> time = new ArrayList<>();
			double toMutate = Math.random();
			if (toMutate > pmt) {
				List<Date> times = (List<Date>) chromosome.get(i).get(1);
				Date fromTime = times.get(0);
				Date toTime = times.get(1);
				int duration = (int) requests.get(i).get(3);
				List<Date> timeLimit = getTimeLimit(i, fromTime);
				Date fromTimeLimit = timeLimit.get(0);
				Date toTimeLimit = timeLimit.get(1);
				// System.out.println("From time limit:" + fromTimeLimit);
				// System.out.println("To time limit:" + toTimeLimit);
				// System.out.println("From time:" + fromTime);
				// System.out.println("To time:" + toTime);
				// System.out.println("Duration:" + duration);
				if (toTimeLimit.getTime() - fromTimeLimit.getTime() > duration * 3600 * 1000) {
					/*
					 * Datum egyezes eseten az idot csak akkor lehet mutalni, ha a kezdes es
					 * befejezes kozott nagyobb ido van, mint az esemeny idotartama
					 */
					boolean found = false;
					while (!found) {
						long randomTime = getRandomTimeForMutation(fromTime, toTime, fromTimeLimit, toTimeLimit);
						Date newFromTime = new Date(randomTime);
						newFromTime.setMinutes(optimizeGeneratedTime(newFromTime.getMinutes()));
						newFromTime.setSeconds(0);
						// System.out.println("generalt:" + newFromTime);
						Date newToTime = new Date(newFromTime.getTime() + duration * 3600 * 1000);
						if (newTimeCheck(fromTimeLimit, toTimeLimit, fromTime, newFromTime, newToTime)) {
							fromTime = newFromTime;
							toTime = newToTime;
							found = true;
							// System.out.println("New from time:" + fromTime);
							// System.out.println("New to time:" + toTime);
							// System.out.println();
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
		int i = 0;
		while (i < rooms.size()) {
			if (roomId.equals(rooms.get(i).get(0))) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private static int getRoomTypeIndex(ObjectId roomTypeId) {
		int i = 0;
		while (i < roomTypes.size()) {
			if (roomTypeId.equals(roomTypes.get(i).get(0))) {
				return i;
			}
			i++;
		}
		return -1;
	}

	private static List<ObjectId> addEquivalentRoomProperties(ObjectId roomId) {
		int i = getRoomIndex(roomId);
		// Lekerem a tulajdonsagait
		List<ObjectId> roomProperties = new ArrayList<>();
		List<ObjectId> properties = (List<ObjectId>) rooms.get(i).get(1);
		for (i = 0; i < properties.size(); i++) {
			roomProperties.add(properties.get(i));
		}
		nrOfAddedProperties = 0;
		int n = roomProperties.size();
		// Vegigmegyek a tulajdonsagokon
		for (i = 0; i < n; i++) {
			// Megnezem, hogy van-e ekvivalense.
			int j = getRoomTypeIndex(roomProperties.get(i));
			if (roomTypes.get(j).get(1) != null) {
				List<ObjectId> equivalents = (List<ObjectId>) roomTypes.get(j).get(1);
				for (int g = 0; g < equivalents.size(); g++) {
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
		List<ObjectId> actualRoomProperties = new ArrayList<>();
		List<ObjectId> equivalentProperties = addEquivalentRoomProperties(actualRoomId);
		for (int i = 0; i < equivalentProperties.size(); i++) {
			actualRoomProperties.add(equivalentProperties.get(i));
		}
		int i = getRoomIndex(newRoomId);
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
		ArrayList<ArrayList<Object>> mutatedChromosome = new ArrayList<>();
		// Vegigmegyek a kromoszoma genjein
		for (int i = 0; i < chromosome.size(); i++) {
			// Eldontom, hogy mutalom-e
			ArrayList<Object> gene = new ArrayList<>();
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
		ArrayList<Integer> indexOfChromosomes = new ArrayList<>();
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
		ArrayList<ArrayList<Object>> chromosomeToSort = new ArrayList<>();
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
				if (chromosomeToSort.get(i).get(2).equals(chromosomeToSort.get(j).get(2)) && !isChecked[j]) {
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
		ArrayList<ArrayList<ArrayList<Object>>> newPopulation = new ArrayList<>();
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
			System.out.println(iter);
			fitnessCalculation();
			while (i < populationSize && !found) {
				if (isTheFittestChromosome(population.get(i))) {
					found = true;
					printFittestChromosome(i);
				}
				i++;
			}
			if (!found) { // Szelecioval uj populacio
				getNewPopulation(); // Keresztezem a kivalasztott kromoszomakat population =
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
		System.out.println("Chromosome:");
		for (int j = 0; j < chromosome.size(); j++) {
			System.out.println(chromosome.get(j));
		}
		System.out.println();
	}

	private static void printPopulation(ArrayList<ArrayList<ArrayList<Object>>> population) {
		System.out.println("Population:");
		for (int i = 0; i < population.size(); i++) {
			for (int j = 0; j < population.get(i).size(); j++) {
				System.out.println(population.get(i).get(j));
			}
			System.out.println();
		}
		System.out.println();
	}

	public static void main(String[] args) throws IOException{
		long startTime = System.currentTimeMillis();
		getParameters();
		readDataFromDB();
		printChromosome(requests);
		getInitialChromosome();
		printChromosome(initialChromosome);
		getInitialPopulation();
		printPopulation(population);
		geneticAlgorithm();
		/*
		 * SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss"); String
		 * dateInString = "12-09-2019 10:00:00"; Date fromTimeLimit =
		 * sdf.parse(dateInString); dateInString = "12-09-2019 13:00:00"; Date
		 * toTimeLimit = sdf.parse(dateInString); dateInString = "12-09-2019 12:00:00";
		 * Date fromTime = sdf.parse(dateInString); dateInString =
		 * "12-09-2019 13:00:00"; Date toTime = sdf.parse(dateInString); Date
		 * newFromTime = new Date(getRandomTimeForMutation(fromTime, toTime,
		 * fromTimeLimit, toTimeLimit)); System.out.println(newFromTime);
		 */
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Run time:" + totalTime);
	}
}
