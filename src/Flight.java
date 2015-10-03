import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Flight.
 *
 * <p>Flight contains:</p>
 * <ul>
 * <li>Flight number</li>
 * <li>Total number of seats in flight</li>
 * <li>Price per seat.</li>
 * <li>Origin code</li>
 * <li>Destination code</li>
 * <li>Flight reservation list.</li>
 * </ul>
 */
public class Flight implements Comparable<Flight> {
    /**
     * Flight number.
     */
    private final String flightNumber;

    /**
     * Number of seats.
     */
    private final int numberOfSeats;

    /**
     * Price per seat.
     */
    private int pricePerSeat;

    /**
     * Origin code.
     */
    private final String originCode;

    /**
     * Destination code.
     */
    private final String destinationCode;

    /**
     * Reservation map for this flight.
     */
    private Map<Passenger, ReservationItem> reservationMap;

    /**
     * Seats that have been picked.
     */
    private List<Integer> existSeats;

    /**
     * Private constructor.
     * Initiate flight instance with FlightBuilder.
     * @param builder FlightBuilder instance.
     */
    private Flight(FlightBuilder builder) {
        this.flightNumber = builder.flightNumber;
        this.numberOfSeats = builder.numberOfSeats;
        this.pricePerSeat = builder.pricePerSeat;
        this.originCode = builder.originCode;
        this.destinationCode = builder.destinationCode;
        this.reservationMap = new HashMap<>();
        for (int i = 1; i <= numberOfSeats; i++) {
            existSeats.add(i);
        }
    }

    /**
     * Get flight number.
     * @return flight number.
     */
    public String getFlightNumber() {
        return flightNumber;
    }

    /**
     * Get numberOfSeats.
     * @return flight number.
     */
    public int getNumberOfSeats() {
        return numberOfSeats;
    }

    /**
     * Get pricePerSeat;
     * @return pricePerSeat.
     */
    public int getPricePerSeat() {
        return pricePerSeat;
    }

    /**
     * Get origin code.
     * @return origin code.
     */
    public String getOriginCode() {
        return originCode;
    }

    /**
     * Get destination code.
     * @return destination code.
     */
    public String getDestinationCode() {
        return destinationCode;
    }

    /**
     * Book a passenger on a flight.
     * @param item reservation item.
     */
    public void bookPassenger(final ReservationItem item) {
        this.reservationMap.put(item.getPassenger(), item);
    }

    /**
     * Check if flight is full.
     * @return true if flight is full.
     */
    public boolean isFull() {
        return this.reservationMap.size() >= this.numberOfSeats;
    }

    /**
     * Get reservation by passenger.
     * @return passenger's reservation item.
     */
    public ReservationItem getReservationByPassenger(Passenger passenger) {
        return reservationMap.get(passenger);
    }

    /**
     * Generate random seat number for passengers.
     * @return random picked seat number.
     */
    public int generateRandomSeatNumber() {
        int randomIndex = generateRandomNumber(0, existSeats.size() - 1);
        return existSeats.remove(randomIndex);
    }

    /**
     * Recover seat for flight.
     * If a passenger cancel a flight, we should add this seat number to existSeats.
     * @param seatNumber canceled flight's seat number.
     */
    public void recoverSeat(final int seatNumber) {
        existSeats.add(seatNumber);
    }

    /**
     * Generate random number.
     * @param max max possible number.
     * @param min possible number.
     * @return random number between max and min.
     */
    private int generateRandomNumber(final int max, final int min) {
        return min + (int)(Math.random() * ((max - min) + 1));
    }

    /**
     * Change price per seat.
     * @param newPrice new price per seat.
     */
    public void changePrice(final int newPrice) {
        this.pricePerSeat = newPrice;
    }

    /**
     * Cancel a booking on a flight.
     * @param item reservation item.
     */
    public void canclePassenger(final ReservationItem item) {
        Passenger passenger = item.getPassenger();
        if (reservationMap.get(passenger) != null) {
            reservationMap.remove(passenger);
        }
    }

    /**
     * Static FlightBuilder class.
     * Used to initiate Flight instance.
     */
    public static class FlightBuilder {
        /**
         * Flight number.
         */
        private String flightNumber;

        /**
         * Number of seats.
         */
        private int numberOfSeats;

        /**
         * Price per seat.
         */
        private int pricePerSeat;

        /**
         * Origin code.
         */
        private String originCode;

        /**
         * Destination code.
         */
        private String destinationCode;

        /**
         * Initiate flight number.
         * @param flightNumber flight number.
         * @return FightBuilder instance.
         */
        public FlightBuilder withFlightNumber(final String flightNumber) {
            this.flightNumber = flightNumber;
            return this;
        }

        /**
         * Initiate number of seats.
         * @param numberOfSeats number of seats.
         * @return FightBuiler instance.
         */
        public FlightBuilder withNumberOfSeats(final int numberOfSeats) {
            this.numberOfSeats = numberOfSeats;
            return this;
        }

        /**
         * Initiate price per seat.
         * @param pricePerSeat price per seat.
         * @return FightBuiler instance.
         */
        public FlightBuilder withPricePerSeat(final int pricePerSeat) {
            this.pricePerSeat = pricePerSeat;
            return this;
        }

        /**
         * Initiate origin code.
         * @param originCode origin code.
         * @return FightBuiler instance.
         */
        public FlightBuilder withOriginCode(final String originCode) {
            this.originCode = originCode;
            return this;
        }

        /**
         * Initiate destination code.
         * @param destinationCode destination code.
         * @return FightBuiler instance.
         */
        public FlightBuilder withDestinationCode(final String destinationCode) {
            this.destinationCode = destinationCode;
            return this;
        }

        /**
         * Create flight instance.
         * @return flight instance.
         */
        public Flight build() {
            return new Flight(this);
        }
    }

    @Override
    public int compareTo(Flight o) {
        return this.pricePerSeat - o.pricePerSeat;
    }
}
