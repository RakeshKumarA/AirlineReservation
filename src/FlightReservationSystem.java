import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * Class for handling all the transactions.
 *
 * <p>Valid transactions contains:</p>
 * <ul>
 * <li>Book a passenger on a flight</li>
 * <li>Change the price of a seat on a flight</li>
 * <li>Cancel a booking on a flight</li>
 * </ul>
 */
public class FlightReservationSystem {
    /**
     * FlightsMap, map of <OriginDestinationPair, flights>.
     * For each OriginDestinationPair, we maintain a TreeSet of flight. TreeSet is sorted by flight price.
     */
    private Map<OriginDestinationPair, TreeSet<Flight>> flightsMap;

    /**
     * flight number to flight map.
     */
    private Map<String, Flight> flightNumberToFlightMap;

    /**
     * Constructor.
     * @throws IOException
     *              Throws when failed or interrupted I/O operations happens.
     * @throws FileNotFoundException
     *              Throws when an attempt to open the file denoted by a specified pathname has failed.
     */
    public FlightReservationSystem() throws FileNotFoundException, IOException {
        initiateFlights();
    }

    /**
     * Initiate flight information based on inputfile1.txt.
     * @throws IOException
     *              Throws when failed or interrupted I/O operations happens.
     * @throws FileNotFoundException
     *              Throws when an attempt to open the file denoted by a specified pathname has failed.
     */
    private void initiateFlights() throws FileNotFoundException, IOException {
        flightsMap = new HashMap<>();
        flightNumberToFlightMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("./in/inputfile1.txt"))) {
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                addFlight(line.replaceAll("\\s", "").split(","));
                line = br.readLine();
            }
        }
    }

    /**
     * Add one flight item to flightsMap.
     * Add flightNumber to flight relation to flightNumberToFlightMap.
     * @param flightInfoArr String[] which contains flight information.
     */
    private void addFlight(final String[] flightInfoArr) {
        String flightNumber = flightInfoArr[FlightInfoCSVIndexEnum.FLIGHT_NUMBER.getIndex()];
        String originCode = flightInfoArr[FlightInfoCSVIndexEnum.ORIGIN.getIndex()];
        String destinationCode = flightInfoArr[FlightInfoCSVIndexEnum.DESTINATION.getIndex()];
        int numberOfSeats = Integer.parseInt(flightInfoArr[FlightInfoCSVIndexEnum.NUMBER_OF_SEATS.getIndex()]);
        int pricePerSeat = Integer.parseInt(flightInfoArr[FlightInfoCSVIndexEnum.PRICE_PER_SEAT.getIndex()]);
        OriginDestinationPair originDestinationPair = new OriginDestinationPair(originCode, destinationCode);
        TreeSet<Flight> flights = flightsMap.get(originDestinationPair);

        if (flights == null) {
            flights = new TreeSet<>();
        }
        Flight flight = new Flight.FlightBuilder()
                                .withFlightNumber(flightNumber)
                                .withNumberOfSeats(numberOfSeats)
                                .withPricePerSeat(pricePerSeat)
                                .withOriginCode(originCode)
                                .withDestinationCode(destinationCode)
                                .build();
        flights.add(flight);
        flightsMap.put(originDestinationPair, flights);

        Flight temp = flightNumberToFlightMap.get(flightNumber);
        if (temp == null) {
            flightNumberToFlightMap.put(flightNumber, flight);
        }
    }

    /**
     * Handle transactions in inputfile2.txt.
     * @throws IOException
     *              Throws when failed or interrupted I/O operations happens.
     * @throws FileNotFoundException
     *              Throws when an attempt to open the file denoted by a specified pathname has failed.
     */
    public void handleTransactions() throws FileNotFoundException, IOException {
        try (BufferedReader br = new BufferedReader(new FileReader("./in/inputfile2.txt"))) {
            String line = br.readLine();
            while (line != null) {
                System.out.println(line);
                processTransaction(line.replaceAll("\\s", "").split(","));
                line = br.readLine();
            }
        }
    }

    /**
     * Create Output.
     * The output should contains:
     * <ul>
     * <li>Summary information of a flight</li>
     * <li>EOD summary</li>
     * </ul>
     *
     * @throws IOException
     *              Throws when failed or interrupted I/O operations happens.
     */
    public void createOutput() throws IOException {
        StringBuffer sBuffer = new StringBuffer();
        int totalSeatsSold = 0;
        long totalRevenue = 0;
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("./out/output.txt"))) {
            for (Map.Entry<String, Flight>  entry : flightNumberToFlightMap.entrySet()) {
                Flight flight = entry.getValue();
                FlightSummary summary = flight.summaryFlight();
                sBuffer.append(summary.getSummary())
                       .append("\n");
                totalSeatsSold += summary.getSoldSeats();
                totalRevenue += summary.getTotalAvenue();
            }
            sBuffer.append("\n")
                   .append("System's summary")
                   .append("\n")
                   .append("Total seats sold: ")
                   .append(totalSeatsSold)
                   .append("\n")
                   .append("Total revenue: ")
                   .append(String.valueOf(totalRevenue));
            bw.write(sBuffer.toString());
        }
    }

    /**
     * Process Transaction.
     * @param transactionInfoArr String[] transaction information.
     */
    private void processTransaction(final String[] transactionInfoArr) {
        String operation = transactionInfoArr[0];
        if (TransactionTypeEnum.BOOK_PASSENGER.getTransactionType().equals(operation)) {
            processBookPassenger(transactionInfoArr);
        } else if (TransactionTypeEnum.CHANGE_PRICE.getTransactionType().equals(operation)) {
            processChangePrice(transactionInfoArr);
        } else if (TransactionTypeEnum.CANCEL_PASSENGER.getTransactionType().equals(operation)) {
            processCancelPassenger(transactionInfoArr);
        }
    }

    /**
     * Get flights TreeSet by OriginDestinationPair.
     * @param transactionInfoArr String[] transaction information.
     * @return flights with given origin and destination.
     */
    private TreeSet<Flight> getFlightsByOriginDestinationPair(final String[] transactionInfoArr) {
        String originCode = transactionInfoArr[2];
        String destinationCode = transactionInfoArr[3];
        OriginDestinationPair originDestinationPair = new OriginDestinationPair(originCode, destinationCode);
        return flightsMap.get(originDestinationPair);
    }

    /**
     * Process cancel passenger transaction.
     * @param transactionInfoArr String[] transaction information.
     */
    private void processCancelPassenger(final String[] transactionInfoArr) {
        Passenger passenger = new Passenger(transactionInfoArr[1]);
        TreeSet<Flight> flights = getFlightsByOriginDestinationPair(transactionInfoArr);
        if (flights == null) {
            return;
        }
        Iterator<Flight> iterator = flights.iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            ReservationItem reservation = flight.getReservationByPassenger(passenger);
            if (reservation == null) {
                continue;
            } else {
                flight.canclePassenger(reservation);
                flight.recoverSeat(reservation.getSeatNumber());
            }
        }
    }

    /**
     * Process change price transaction.
     * @param transactionInfoArr String[] transaction information.
     */
    private void processChangePrice(final String[] transactionInfoArr) {
        String flightNumber = transactionInfoArr[1];
        int newPrice = Integer.parseInt(transactionInfoArr[2]);
        Flight flight = flightNumberToFlightMap.get(flightNumber);
        if (flight == null) {
            return;
        }
        flight.changePrice(newPrice);
    }

    /**
     * Process BookPassenger Transaction.
     * @param transactionInfoArr String[] transaction information.
     */
    private void processBookPassenger(final String[] transactionInfoArr) {
        Passenger passenger = new Passenger(transactionInfoArr[1]);
        TreeSet<Flight> flights = getFlightsByOriginDestinationPair(transactionInfoArr);
        if (flights == null) {
            return;
        }
        Iterator<Flight> iterator = flights.iterator();
        while (iterator.hasNext()) {
            Flight flight = iterator.next();
            if (flight.isFull()) {
                continue;
            } else {
                ReservationItem reservationItem = new ReservationItem(passenger,
                        flight.getPricePerSeat(), flight.generateRandomSeatNumber());
                flight.bookPassenger(reservationItem);
            }
        }
    }

}
