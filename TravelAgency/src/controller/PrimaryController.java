package controller;

import  model.*;  
import utils.HostelAccommodationType;

import java.util.ArrayList;
import java.util.Collection;

import java.util.Date;
import java.util.HashSet;

import exception.CustomerNotExistException;
import exception.FailToAddException;
import exception.FlightToSameLocationException;
import exception.GuideExperienceMismatchException;
import exception.GuideNotExist;
import exception.IllegelCountryException;
import exception.IllegelInputException;
import exception.LocationNotExistException;
import exception.NegativNumberException;
import exception.NegativeNumberNotPriceException;
import exception.ObjectExistException;
import exception.OrderExistException;
import exception.PasswordMismatchException;
import javafx.collections.ObservableList;


/**
 * Created by Antonio Zaitoun on 14/07/2018.
 * Update by Heyam Abdalhade on 04/08/2018.
 */
public class PrimaryController {

	/**
	 * This method is used to register new locations (destinations).
	 *
	 * 1) validate parameters
	 * 2) create destination object.
	 * 3) check that destination does not exist.
	 * 4) add destination.
	 *
	 * @param country The name of the country.
	 * @param city The name of the city.
	 * @return true if successfully added.
	 */
	public boolean addLocation(String country,String city) throws Exception{
		//TODO: Complete this method

		if(!require(country,city)) {
			throw new IllegelInputException();
		}


		Destination destination = new Destination(country,city);
		ArrayList<Destination> arrayDes = new ArrayList<>();
		
		for(String s : Shared.getInstance().getDestinations().keySet())
		{
			arrayDes.addAll(Shared.getInstance().getDestinations().get(s));
		}
		if(arrayDes.contains(destination))
			throw new ObjectExistException();

		//check if location exists 
		if(Shared.getInstance().getDestinations().containsKey(country)){
			arrayDes = Shared.getInstance().getDestinations().get(country);
			if(arrayDes.contains(destination))
			{
				throw new ObjectExistException();

			}else {
				arrayDes.add(destination);
				Shared.getInstance().getDestinations().put(country, arrayDes);
				return true;
			}

		}

		arrayDes = new ArrayList<>();
		arrayDes.add(destination);
		Shared.getInstance().getDestinations().put(country, arrayDes);
		return true;

	}
	/**
	 * This method is used to add a new customer to the system.
	 *
	 * 1) validate parameters.
	 * 2) check if customer does not exist already.
	 * 3) create address object.
	 * 4) create customer object.
	 * 5) add customer to the system.
	 *
	 * @param id The id of the customer.
	 * @param firstName The first name of the customer.
	 * @param surname The surname of the customer.
	 * @param birthDate The customer's birth date.
	 * @param email The email of the customer.
	 * @param country The home country of the customer. (address parameter)
	 * @param city The city of the customer. (address parameter)
	 * @param street The street at which the customer lives. (address parameter)
	 * @param houseNumber The house number at which the customer lives. (address parameter)
	 * @return true if successfully added.
	 */
	public boolean addCustomer(Long id, String password, String verifyPass, String answer, String firstName, String surname, Date birthDate,
			String email, String country, String city, String street, int houseNumber,String phoneNumber) throws Exception{
		//TODO: Complete this method

		//check for parameters
		boolean isOk = require(firstName,surname,birthDate,email,country,city,street,phoneNumber)
				&& requireNotZero(id,houseNumber);

		if(!isOk)
			throw new IllegelInputException();
		if(id < 0 || houseNumber < 0)
			throw new NegativeNumberNotPriceException();
		if(password.length() < 8 || !(password.equals(verifyPass)))
			throw new PasswordMismatchException();
		//check if doesn't exist already
		if(Shared.getInstance().getUserConfirmation().containsKey(id))
			throw new ObjectExistException();

		//create address
		Address address = new Address(city,country,street,houseNumber,phoneNumber);


		//create customer object
		Customer customer = new Customer(id, password ,firstName,surname,birthDate,email,address, answer);

		// add the customer to the user confirmation system
		Shared.getInstance().getUserConfirmation().put(id , customer);

		//add
		return Shared.getInstance().getCustomers().put(id, customer) == null;
	}


	/**
	 * This method is used to add a new tour guide to the system.
	 *
	 * 1) validate parameters
	 * 2) check that guide doesn't exist.
	 * 3) create address object.
	 * 4) create an array of destinations (use Destination(String) )
	 * 5) check that destinations exist in the system and get it.
	 * 6) create new guide object.
	 * 7) add guide to the system.
	 *
	 * @param id The id of the guide.
	 * @param firstName The first name of the tour guide.
	 * @param surname The surname of the tour guide.
	 * @param birthDate The guide's birth date.
	 * @param email The guide's email address.
	 * @param country The guide's country. (address parameter)
	 * @param city The guide's city. (address parameter)
	 * @param street The guide's street. (address parameter)
	 * @param houseNumber The guide's hose number. (address parameter)
	 * @param startDate The date at which this person has became a tour guide.
	 * @param experiencedDestinations A list of locations at which this guide is experienced.
	 * @return true if added successfully.
	 */
	public boolean addGuide(long id, String password, String verifyPass, String answer, String firstName, String surname, Date birthDate,
			String email,String country, String city, String street, int houseNumber, String phoneNumber,
			Date startDate, ArrayList<String> experiencedDestinations) throws Exception{
		//TODO: Complete this method

		boolean isOk = require(firstName, surname, birthDate, email, country, city, street, houseNumber,phoneNumber, startDate, experiencedDestinations)
				&& requireNotZero(id);

		if (!isOk)
			throw new IllegelInputException();
		if(id < 0 || houseNumber < 0)
			throw new NegativeNumberNotPriceException();
		if(password.length() < 8 || !(password.equals(verifyPass)))
			throw new PasswordMismatchException();

		//check if doesn't exist already
		if(Shared.getInstance().getUserConfirmation().containsKey(id))
			throw new ObjectExistException();

		//create address
		Address address = new Address(country, city, street, houseNumber,phoneNumber);

		//create destinations
		HashSet<Destination> destinations = new HashSet<>();


		for (String experiencedDestination : experiencedDestinations)
			destinations.add(new Destination(experiencedDestination));

		//check if this exist and get it

		HashSet<Destination> allDestinations = new HashSet<>();
		Collection<ArrayList<Destination>>  src = Shared.getInstance().getDestinations().values();
		for (ArrayList<Destination> arrayList : src) {
			allDestinations.addAll(arrayList);
		}


		if(!allDestinations.containsAll(destinations))
			throw new GuideExperienceMismatchException();

		Guide guide = new Guide(id, password, firstName, surname, birthDate, email, address, startDate, destinations, answer);

		Shared.getInstance().getUserConfirmation().put(id, guide);

		return Shared.getInstance().getGuides().put(id, guide) == null;

	}
	/**
	 *
	 * This method is used to add new flights to the system.
	 *
	 * 1) validate parameters.
	 * 2) check that toCountry and fromCountry is exists in system.
	 * 2) Create destination objects (using from and to).
	 * 3) check that destinations exists in the system (use contains).
	 * 4) Create a flight from parameters.
	 * 5) Check that flight doesn't exist (use contains).
	 * 6) Add flight to the system. (use add).
	 *
	 * @param flightNumber The number of the flight.
	 * @param numberOfSeats The amount of seats this flight has.
	 * @param fromCountry The country from which this flight takes off.
	 * @param fromCity The city from which this flight takes off.
	 * @param toCountry The destination country of this flight.
	 * @param toCity The destination city of this flight.
	 * @param price The price per seat.
	 * @param date The date of the flight.
	 * @return true if was successfully added.
	 */
	public boolean addFlight(String flightNumber, int numberOfSeats, String fromCountry, String fromCity,
			String toCountry, String toCity, double price, Date date) throws Exception{
		//TODO: Complete this method

		boolean isOk = require(flightNumber, fromCountry, fromCity, toCountry, toCity, date)
				&& requireNotZero(numberOfSeats, price);
		
		if(price < 0)
			throw new NegativNumberException();
		
		if(numberOfSeats <= 0)
			throw new NegativeNumberNotPriceException();

		if (!isOk)
			throw new IllegelInputException();

		//check if doesn't exist already
		if(Shared.getInstance().getFlights().containsKey(flightNumber))
			throw new ObjectExistException();
		//check if this country exist in system
		if(!Shared.getInstance().getDestinations().containsKey(toCountry) ||
				!Shared.getInstance().getDestinations().containsKey(fromCountry))
			throw new IllegelCountryException();

		Destination from = new Destination(fromCountry, fromCity);
		Destination to = new Destination(toCountry, toCity);

		if(from.equals(to))
			throw new FlightToSameLocationException();

		//check if location exists
		if (!Shared.getInstance().getDestinations().get(fromCountry).contains(from) ||
				!Shared.getInstance().getDestinations().get(toCountry).contains(to))
			throw new LocationNotExistException();

		Flight flight = new Flight(flightNumber, numberOfSeats, from, to, price, date);

		return Shared.getInstance().getFlights().put(flightNumber, flight) == null;
	}

	/**
	 * This method is used to add new hotels to the system.
	 *
	 * 1) validate parameters.
	 * 2) check that location exists
	 * 3) create new hotel object.
	 * 4) add hotel to the system.
	 *
	 * Note: There is no need to check if the hotel already exists.
	 *
	 * @param displayName The name of the hotel.
	 * @param numberOfRooms The number of rooms this hotel has.
	 * @param numberOfPeoplePerRoom The number of people that can fit in each room.
	 * @param pricePerPerson The price per person.
	 * @param country The country where this hotel is located.
	 * @param city The city at which this hotel is located.
	 * @param starRating The star rating of the hotel (a number between 1 and 5).
	 * @param serversBreakfast If the hotel serves breakfast or not.
	 * @param hasPool If the hotel has a pool or not.
	 * @return true if was successfully added to the system.
	 */
	public boolean addHotel(long id,String displayName, int numberOfRooms, int numberOfPeoplePerRoom, double pricePerPerson,
			String country, String city, double starRating, boolean serversBreakfast, boolean hasPool) throws Exception{
		//TODO: Complete this method

		//check parameters
		boolean isOk = require(displayName,country,city,serversBreakfast,hasPool)
				&& requireNotZero(id,numberOfRooms,numberOfPeoplePerRoom,pricePerPerson,starRating);
		
		if(pricePerPerson < 0)
			throw new NegativNumberException();
		
		if(numberOfPeoplePerRoom <= 0 || numberOfRooms <= 0 || starRating < 1.0 || id < 0)
			throw new NegativeNumberNotPriceException();

		//one or more parameters are missing.
		if(!isOk)
			throw new IllegelInputException();

		//if already exist
		if(Shared.getInstance().getAccommodations().containsKey(id))
			throw new ObjectExistException();
		// if country exist
		if(!Shared.getInstance().getDestinations().containsKey(country))
			throw new IllegelCountryException();

		Destination location = new Destination(country,city);

		//check if location exists
		if(!Shared.getInstance().getDestinations().get(country).contains(location))
			throw new LocationNotExistException();

		//create instance
		Hotel hotel = new Hotel(id,displayName,numberOfRooms,numberOfPeoplePerRoom,pricePerPerson,
				location,starRating,serversBreakfast,hasPool);

		//add hotel to accommodations
		return Shared.getInstance().getAccommodations().put(id, hotel)== null;
	}

	/**
	 * This method is used to add a new hostel to the system.
	 *
	 * 1) validate parameters.
	 * 2) check that location exists
	 * 3) create new hostel object.
	 * 4) add hostel to the system.
	 *
	 * Note: There is no need to check if the hostel already exists.
	 *
	 * @param displayName The name of the hostel.
	 * @param numberOfRooms The number of rooms this hostel has.
	 * @param numberOfPeoplePerRoom The number of people that can fit in each room.
	 * @param pricePerPerson The price per person.
	 * @param country The country where this hostel is located.
	 * @param city The city at which this hostel is located.
	 * @param accommodationType The accommodation type (either 0 or 1)
	 * @return true if was successfully added to the system.
	 */
	public boolean addHostel(long id, String displayName, int numberOfRooms, int numberOfPeoplePerRoom, double pricePerPerson,
			String country, String city, int accommodationType) throws Exception{
		//TODO: Complete this method

		//check parameters
		boolean isOk = require(displayName,country,city)
				&& requireNotZero(id,numberOfRooms,numberOfPeoplePerRoom,pricePerPerson);
		
		if(pricePerPerson < 0)
			throw new NegativNumberException();
		if(numberOfPeoplePerRoom <= 0 || numberOfRooms <= 0 || id < 0)
			throw new NegativeNumberNotPriceException();
		//one or more parameters are missing.
		if(!isOk)
			throw new IllegelInputException();

		//if already exist
		if(Shared.getInstance().getAccommodations().containsKey(id))
			throw new ObjectExistException();
		// if country exist
		if(!Shared.getInstance().getDestinations().containsKey(country))
			throw new IllegelCountryException();

		Destination location = new Destination(country,city);

		//check if location exists
		if(!Shared.getInstance().getDestinations().get(country).contains(location))
			throw new LocationNotExistException();

		HostelAccommodationType type = HostelAccommodationType.fromCode(accommodationType);

		//create instance
		Hostel hostel = new Hostel(id,displayName,numberOfRooms,numberOfPeoplePerRoom,pricePerPerson,
				location,type);

		//add hotel to accommodations
		return Shared.getInstance().getAccommodations().put(id, hostel) == null;
	}

	/**
	 * This method is used to add a new motel to the system.
	 *
	 * 1) validate parameters.
	 * 2) check that location exists.
	 * 3) create new motel object.
	 * 4) add motel to the system.
	 *
	 * Note: There is no need to check if the hostel already exists.
	 *
	 * @param displayName The name of the motel.
	 * @param numberOfRooms The number of rooms this motel has.
	 * @param numberOfPeoplePerRoom The number of people that can fit in each room.
	 * @param pricePerPerson The price per person.
	 * @param country The country where this motel is located.
	 * @param city The city at which this motel is located.
	 * @param hasKitchen If this motel has a kitchen.
	 * @param hasWifi If this motel has a wifi.
	 * @return true if was added successfully.
	 */
	public boolean addMotel(long id,String displayName, int numberOfRooms, int numberOfPeoplePerRoom, double pricePerPerson,
			String country, String city, boolean hasKitchen, boolean hasWifi) throws Exception{
		//TODO: Complete this method

		//check parameters
		boolean isOk = require(displayName,country,city,hasKitchen, hasWifi)
				&&
				requireNotZero(id,numberOfRooms,numberOfPeoplePerRoom,pricePerPerson);
		
		if(pricePerPerson < 0)
			throw new NegativNumberException();
		if(numberOfPeoplePerRoom <= 0 || numberOfRooms <= 0 || id < 0)
			throw new NegativeNumberNotPriceException();
		//one or more parameters are missing.
		if(!isOk)
			throw new IllegelInputException();

		//if already exist
		if(Shared.getInstance().getAccommodations().containsKey(id))
			throw new ObjectExistException();
		// if country exist
		if(!Shared.getInstance().getDestinations().containsKey(country))
			throw new IllegelCountryException();

		Destination location = new Destination(country,city);

		//check if location exists
		if(!Shared.getInstance().getDestinations().get(country).contains(location))
			throw new LocationNotExistException();

		//create instance
		Motel motel = new Motel(id,displayName,numberOfRooms,numberOfPeoplePerRoom,pricePerPerson,
				location,hasKitchen,hasWifi);

		//add hotel to accommodations
		return Shared.getInstance().getAccommodations().put(id, motel)== null;
	}

	/**
	 * This method is used to add a new experience to the guide.
	 *
	 * 1) validate parameters.
	 * 2) check that location exists.
	 * 2) check that guide exists.
	 * 4) add experience to guide.
	 * 
	 * @param id
	 * @param country
	 * @param city
	 * @return true if was added successfully.
	 */
	public boolean addExperiencedDestinationsforGuide(long id,String country, String city ) throws Exception{
		boolean isOk = require(country,city)
				&& requireNotZero(id);

		if (!isOk)
			throw new IllegelInputException();

		Destination location = new Destination(country,city);
		// if country exist
		if(!Shared.getInstance().getDestinations().containsKey(country))
			throw new IllegelCountryException();
		//check if location exists
		if(!Shared.getInstance().getDestinations().get(country).contains(location))
			throw new LocationNotExistException();
		//check if location exists
		if(!Shared.getInstance().getGuides().containsKey(id))
			throw new GuideNotExist();
		Guide guide =Shared.getInstance().getGuides().get(id);

		if(guide.addExpDestination(location))
			return true;
		return false;
	}

	/**
	 * This method is used to added a new group trip to the system.
	 *
	 * 1) validate parameters.
	 * 2) check that destination exists.
	 * 3) find guide (if exists)
	 * 4) create a new group trip.
	 * 5) add the group trip to the system.
	 *
	 * @param guideId The id of the tour guide.
	 * @param description A basic description of the tour.
	 * @param country The country at which this tour takes place.
	 * @param city The city at which this tour takes place.
	 * @param price The price per person of this tour.
	 * @param maxPeople The maximum number of people that can participate in this tour.
	 * @return true if successfully added.
	 */
	public boolean addGroupTrip(int idtrip,long guideId,String description,Date tripDate, String country,
			String city, double price, int maxPeople) throws Exception{
		//TODO: Complete this method
		if(description == "")
			description = " ";
		boolean isOk = require(description,tripDate,country,city,price)
				&& requireNotZero(idtrip,price,maxPeople);


		if (!isOk)
			throw new IllegelInputException();
		if(idtrip < 0 || maxPeople <= 0 || guideId < 0)
			throw new NegativeNumberNotPriceException();
		if(price < 0)
			throw new NegativNumberException();

		//if already exist
		if(Shared.getInstance().getTrips().containsKey(idtrip))
			throw new ObjectExistException();

		// if country exist
		if(!Shared.getInstance().getDestinations().containsKey(country))
			throw new IllegelCountryException();


		Destination location = new Destination(country,city);

		//check if location exists
		if(!Shared.getInstance().getDestinations().get(country).contains(location))
			throw new LocationNotExistException();

		//get guide
		Guide guide = null;
		if(Shared.getInstance().getGuides().containsKey(guideId))
			guide = Shared.getInstance().getGuides().get(guideId);
		else
			throw new GuideNotExist();
		GroupTrip groupTrip = new GroupTrip(idtrip,description,tripDate,location,guide,price,maxPeople);

		if (guide.addGroupTrip(groupTrip))
		{
			return Shared.getInstance().getTrips().put(idtrip, groupTrip) == null;
		}
		else
		{
			return false;
		}
	}

	/**
	 * This method is used to add a new travel package.
	 *
	 * 1) convert string array of flight numbers to flight array.
	 * 2) convert int array of accommodations business id to accommodations array.
	 * 3) convert int array of trips ids to group trips array.
	 * 4) check all flights, accommodations and trips exists in the system.
	 * 5) create travel package object.
	 * 6) add travel package object to `packages`.
	 *
	 * @param numberOfPeople The number of people this package is meant for (quantity).
	 * @param price The price of this package.
	 * @param flights The list of flight numbers that this package will contain.
	 * @param accommodations A list of accommodations' business ids.
	 * @param trips  A list of group trips' ids.
	 * @return true if package was successfully added.
	 */
	public boolean addTravelPackage(String name,int numberOfPeople,double price,String[] codesTicket,
			String[] flights,Long[] accommodations,Integer[] trips) throws Exception{

		//TODO: Complete this method
		//check parameters
		boolean isOk = require(name,flights,accommodations,trips)
				&& requireNotZero(numberOfPeople,price);
		//if(!name.equals("Business Package")) return  false;
		if(!isOk)
			throw new IllegelInputException();
		if(numberOfPeople <= 0)
			throw new NegativeNumberNotPriceException();
		if(price < 0)
			throw new NegativNumberException();
		//check that all flights exists
		for(String flight : flights)
		{
			if(!Shared.getInstance().getFlights().containsKey(flight))
				return false;
		}

		//check that all accommodations exists
		for(long accomm : accommodations)
		{
			if(!Shared.getInstance().getAccommodations().containsKey(accomm))
				return false;
		}

		//check that all trips exists
		for(int trip : trips)
		{
			if(!Shared.getInstance().getTrips().containsKey(trip))
				return false;
		}

		//create package
		TravelPackage travelPackage = new TravelPackage(name, price, numberOfPeople);
		if(Shared.getInstance().getPackages().containsKey(name))
			throw new ObjectExistException();

		//convert arrays
		HashSet<TicketCheckIn<Flight>> allFlights = new HashSet<>() ;
		HashSet<TicketCheckIn<Accommodation>> allAccommodations = new HashSet<>();
		HashSet<TicketCheckIn<GroupTrip>> allTrips = new HashSet<>();

		int i=0;
		//create flight tickets 
		for(String flight : flights)
		{
			TicketCheckIn<Flight> t = new TicketCheckIn<Flight>(codesTicket[i++], 
					null, travelPackage, Shared.getInstance().getFlights().get(flight));
			allFlights.add(t);
		}

		//create accommodation tickets 
		for(long accomm : accommodations)
		{
			TicketCheckIn<Accommodation> t = new TicketCheckIn<Accommodation>(codesTicket[i++], 
					null, travelPackage, Shared.getInstance().getAccommodations().get(accomm));
			allAccommodations.add(t);
		}

		//create trip tickets 
		for(int trip : trips)
		{
			TicketCheckIn<GroupTrip> t = new TicketCheckIn<GroupTrip>(codesTicket[i++], 
					null, travelPackage, Shared.getInstance().getTrips().get(trip));
			allTrips.add(t);
		}

		if(travelPackage.addTichetsFlightForPackage(allFlights)&&
				travelPackage.addTicketsAccommodationForPackage(allAccommodations)&&
				travelPackage.addTicketsGroupTripForPackage(allTrips))
			return Shared.getInstance().getPackages().put(name, travelPackage) == null;

		return false;
	}

	/**
	 * This method is used to create a new order for a certain customer.
	 *
	 * 1) validate parameters.
	 * 2) check if person exists.
	 * 3) convert arrays and check if flights, accommodations and trips exists.
	 * 4) create a new Order object with the person.
	 * 5) iterate over the  flights, accommodations and trips, and add them to the order (use order.add...)
	 * 6) check that all additions were successful. if not then cancel the order (use order.cancel()).
	 * 7) finally if everything was successful return true.
	 *
	 * @param customerId The id of the customer that is making this order.
	 * @param numberOfPeople The number of people for which the customer is making the order.
	 * @param flights A list of flight numbers.
	 * @param accommodations A list of accommodations business id.
	 * @param trips a list of group trips ids.
	 * @return
	 */
	public boolean makeCustomOrder(int id, long customerId,int numberOfPeople) throws Exception{
		//TODO: Complete this method
		boolean isOk = requireNotZero(id,customerId,numberOfPeople);

		if(!isOk)
			throw new IllegelInputException();
		if(numberOfPeople <= 0 || customerId < 0)
			throw new NegativeNumberNotPriceException();
		//if customer doesn't exist
		if(!Shared.getInstance().getCustomers().containsKey(customerId)){
			return false;
		}

		//get customer
		Customer cust = Shared.getInstance().getCustomers().get(customerId);

		//check if is exist and create order with customer.
		if(Shared.getInstance().getOrders().containsKey(id))
			throw new OrderExistException();

		Order newOrder = new Order(id, cust,numberOfPeople);

		return cust.addOrder(newOrder) && Shared.getInstance().getOrders().put(id, newOrder)== null;
	}

	/**
	 * 
	 * @param code
	 * @param customer
	 * @param idOrder
	 * @param checkIn
	 * @return
	 */
	public <T> boolean addTicketCheckInToOrder(String code,int idOrder, long customer,  T checkIn) throws Exception{

		boolean isOk = require(code,checkIn) && requireNotZero(customer,idOrder);
		boolean a = true;

		if(!isOk)
			throw new IllegelInputException();

		if(!Shared.getInstance().getCustomers().containsKey(customer))
			throw new CustomerNotExistException();
		if(!Shared.getInstance().getOrders().containsKey(idOrder))
			throw new OrderExistException();

		Customer cust = Shared.getInstance().getCustomers().get(customer);
		Order order = Shared.getInstance().getOrders().get(idOrder);




		if(checkIn instanceof Accommodation
				&& ((Accommodation)checkIn).isAvailable(1))
		{
			TicketCheckIn<Accommodation> newTicket = new TicketCheckIn<Accommodation>(code, cust, order, ((Accommodation)checkIn));
			a =  order.addTicketAccommodation((TicketCheckIn<Accommodation>)newTicket) &&
					cust.addTicketsAccpommodation(newTicket)&&
					((Accommodation)checkIn).AddTicket(newTicket);
		}
		else 
			if(checkIn instanceof Flight
					&& ((Flight)checkIn).isAvailable(1) )
			{
				TicketCheckIn<Flight> newTicket = new TicketCheckIn<Flight>(code, cust, order, ((Flight)checkIn));
				a = order.addTicketFlight((TicketCheckIn<Flight>)newTicket) &&
						cust.addTicketFlight(newTicket)&&
						((Flight)checkIn).AddTicket(newTicket);
			}
			else 
				if(checkIn instanceof GroupTrip && ((GroupTrip)checkIn).isAvailable(1))
				{
					TicketCheckIn<GroupTrip> newTicket = new TicketCheckIn<GroupTrip>(code, cust, order, ((GroupTrip)checkIn));
					a = order.addTicketGroupTrip((TicketCheckIn<GroupTrip>)newTicket) &&
							cust.addTicketsGroupTrips(newTicket)&&
							((GroupTrip)checkIn).AddTicket(newTicket);
				}
		if(a == false)
			throw new FailToAddException();
		return true;

	}

	/**
	 * This method is used to create an order for a customer from an existing package.
	 *
	 * 1) check parameters
	 * 2) check if customer exists
	 * 3) check if travel package exists and is available.
	 * 4) find the customer
	 * 5) create a new order from the package (use travelPackage.purchase)
	 * 6) add the order to the customer
	 * 7) if the customer hasn't successfully added the order then cancel the order and return false.
	 * 8) add the order to the `orders` array in the system
	 * 9) return true.
	 *
	 * @param customerId The id of the customer.
	 * @param packageName The name of the package.
	 * @return
	 */
	public boolean makeOrderFromPackage(int idPackageOrder, long customerId, String packageName) throws Exception{
		//TODO: Complete this method
		
		boolean isOk = require(packageName)
				&& requireNotZero(customerId);

		if (!isOk)
			throw new IllegelInputException();
		//	if(idPackageOrder !=77 && idPackageOrder != 95)return false;
		//check person

		if(!Shared.getInstance().getCustomers().containsKey(customerId))
			throw new CustomerNotExistException();
		Customer customer = Shared.getInstance().getCustomers().get(customerId);
		
		//check package if is exist and is available.
		if(!Shared.getInstance().getPackages().containsKey(packageName) )
			return false;

		TravelPackage tp = Shared.getInstance().getPackages().get(packageName);
		
		if(!tp.isAvailable())
			throw new FailToAddException();

		//purchase order
		Order o = tp.purchase(idPackageOrder,customer);

		//add order to customer
		boolean result = customer.addOrder(o);
		if (!result) {
			o.cancel();
			return false;
		}

		//add tickets order to flight, GroupTrip and Accommodation
		for (TicketCheckIn<Flight> flight : o.getFlights())
		{
			customer.addTicketFlight(flight);
			flight.getCheckIn().AddTicket(flight);
		}

		for (TicketCheckIn<Accommodation> accommodation :  o.getAccommodations())
		{
			customer.addTicketsAccpommodation(accommodation);
			accommodation.getCheckIn().AddTicket(accommodation);
		}

		for (TicketCheckIn<GroupTrip> trip : o.getGroupTrips())
		{
			customer.addTicketsGroupTrips(trip);
			trip.getCheckIn().AddTicket(trip);
		}

		Shared.getInstance().getOrders().put(idPackageOrder, o);

		return true;
		
	}

	/**
	 * This method is used to cancel an existing order.
	 *
	 * 1) check parameters
	 * 2) check if order exists
	 * 3) find the order.
	 * 4) get the owner of the order.
	 * 5) remove the order from the customer (owner).
	 * 6) cancel the order.
	 * 7) return true.
	 *
	 * @return true if the order was cancelled successfully.
	 */
	public boolean cancelOrder(int orderId) {
		//TODO: Complete this method

		//check parameters
		if(!requireNotZero(orderId))
			return false;

		//check if order exists
		Order o = null;

		if(!Shared.getInstance().getOrders().containsKey(orderId))
			return false;

		o= Shared.getInstance().getOrders().get(orderId);

		//cancel all ticket for this order
		removeAllTicketByOrder(o);

		Customer c = (Customer) o.getOwner();
		boolean val = c.removeOrder(o.getOrderId());
		
		//failed to remove the order from the customer
		if(!val)
			return false;

		o.cancel();

		Shared.getInstance().getOrders().remove(o.getOrderId());

		return true;

	}

	//helper methods

	private void removeAllTicketByOrder(Order o) {
		for(Customer cust:Shared.getInstance().getCustomers().values())
		{
			cust.removeTicketByOrder(o);
		}
		for(Accommodation acc:Shared.getInstance().getAccommodations().values())
		{
			acc.removeTicketByOrder(o);
		}
		for(GroupTrip trip:Shared.getInstance().getTrips().values())
		{
			trip.removeTicketByOrder(o);
		}
		for(Flight flight:Shared.getInstance().getFlights().values())
		{
			flight.removeTicketByOrder(o);
		}
	}

	public static boolean require(Object... values){
		for (Object value : values)
			if(value == null)
				return false;

		return true;
	}

	public static boolean requireNotZero(Number... numbers){
		for (Number number : numbers) {
			if(number.equals(0))
				return false;
		}
		return true;
	}








}
