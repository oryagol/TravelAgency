package view;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import controller.PrimaryController;
import controller.Shared;
import controller.Sound;
import exception.CustomerNotExistException;
import exception.FailToAddException;
import exception.IllegelInputException;
import exception.NegativNumberException;
import exception.NegativeNumberNotPriceException;
import exception.OrderExistException;
import exception.OrderNotExistException;
import exception.RemoveOrderException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import model.Accommodation;
import model.Customer;
import model.Destination;
import model.Flight;
import model.GroupTrip;
import model.Order;

public class AdminManageOrdersController implements Initializable{
	private static final String Input = "JavaTravel.ser";

	@FXML
	private TextField customOrderId;

	@FXML
	private TextField customOrderNumOfPeople;

	@FXML
	private ComboBox<Long> customOrderCustomerId;

	@FXML
	private ComboBox<String> customOrderFlights;

	@FXML
	private ComboBox<Long> customOrderAccommodations;

	@FXML
	private ComboBox<Integer> customOrderGroupTrip;

	@FXML
	private Button customOrderAddTrips;

	@FXML
	private Button customOrderClearTrips;

	@FXML
	private Button customOrderaddAccommodations;

	@FXML
	private Button customOrderClearAccommodations;

	@FXML
	private Button customOrderAddFlights;

	@FXML
	private Button customOrderClearFlights;

	@FXML
	private TextArea customOrderTripView;

	@FXML
	private TextArea customOrderAccommodationListView;

	@FXML
	private TextArea customOrderFlightsListView;

	@FXML
	private Button addCustomeOrder;

	@FXML
	private TextField orderFromPackageId;

	@FXML
	private ComboBox<Long> orderFromPackageCustomerId;

	@FXML
	private ComboBox<String> orderFromPackageName;

	@FXML
	private Button orderFromPackageOrder;

	@FXML
	private AnchorPane CancelOrderCancel;

	@FXML
	private ComboBox<Integer> cancelOrderId;

	@FXML
	private Button orderFromPackageOrder1;

	PrimaryController control=new PrimaryController();
	ArrayList<String> flightList=new ArrayList<String>();
	ArrayList<Integer> tripList=new ArrayList<Integer>();
	ArrayList<Long> accommodationList=new ArrayList<Long>();




	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ObservableList<String> fList=FXCollections.observableArrayList();
		ObservableList<Long> cList=FXCollections.observableArrayList();
		ObservableList<Long> aList=FXCollections.observableArrayList();
		ObservableList<Integer> tList=FXCollections.observableArrayList();
		ObservableList<Integer> oList=FXCollections.observableArrayList();
		ObservableList<String> pList=FXCollections.observableArrayList();
		for(String flight	:Shared.getInstance().getFlights().keySet())
		{
			fList.add(flight);
		}
		for(Long l : Shared.getInstance().getCustomers().keySet())
		{
			cList.add(l);
		}
		for(Long a : Shared.getInstance().getAccommodations().keySet()) {
			aList.add(a);
		}
		for(Integer t : Shared.getInstance().getTrips().keySet()) {
			tList.add(t);
		}
		for(Integer o : Shared.getInstance().getOrders().keySet()) {
			oList.add(o);
		}
		for(String pack : Shared.getInstance().getPackages().keySet()) {
			pList.add(pack);
		}
		customOrderFlights.setItems(fList);
		customOrderCustomerId.setItems(cList);
		customOrderAccommodations.setItems(aList);
		customOrderGroupTrip.setItems(tList);
		cancelOrderId.setItems(oList);
		orderFromPackageCustomerId.setItems(cList);
		orderFromPackageName.setItems(pList);
	}

	public void makeCustomOrder(ActionEvent e)
	{
		String a = "Order";
		try {
			boolean isOK;
			Customer c = Shared.getInstance().getCustomers().get(customOrderCustomerId.getValue());
			int orderid = Shared.getInstance().orderNextNumber();
			int numOfPeople = Integer.parseInt(customOrderNumOfPeople.getText());
			control.makeCustomOrder(orderid, c.getId(), numOfPeople);
			for(String s : flightList) {
				Flight f = Shared.getInstance().getFlights().get(s);
				isOK = control.addTicketCheckInToOrder(String.valueOf(Shared.getInstance().getCount()), orderid, c.getId(), f);
				Shared.getInstance().boostCount();
				if(isOK == false) 
				{
					throw new FailToAddException();
				}
			}
			for(Long l : accommodationList) {
				Accommodation acc = Shared.getInstance().getAccommodations().get(l);
				isOK = control.addTicketCheckInToOrder(String.valueOf(Shared.getInstance().getCount()), orderid, c.getId(), acc);
				Shared.getInstance().boostCount();
				if(isOK == false)
				{
					throw new FailToAddException();
				}
			}
			for(Integer i : tripList) {
				GroupTrip g = Shared.getInstance().getTrips().get(i);
				isOK = control.addTicketCheckInToOrder(String.valueOf(Shared.getInstance().getCount()), orderid, c.getId(), g);
				Shared.getInstance().boostCount();
				if(isOK == false)
				{
					throw new FailToAddException();
				}
			}
			goodSound();
			success(a, "Success");
			Shared.save(Input);
		}
		catch(IllegelInputException e1) {
			badSound();
			fail(a, e1.toString());
		} 
		catch(OrderExistException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(NegativeNumberNotPriceException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(NumberFormatException e1) {
			badSound();
			fail(a, "Wrong Input!");
		}
		catch(FailToAddException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(CustomerNotExistException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(OrderNotExistException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch (Exception e1) {
			badSound();
			fail(a, e1.toString());
		}

	}
	public void addFlightsToList(ActionEvent e)
	{
		if(!flightList.contains(customOrderFlights.getValue()))
			flightList.add(customOrderFlights.getValue());
		String list="";
		for(String s:flightList)
			list+=s+"\n";

		customOrderFlightsListView.setText(list);

	}
	public void clearFlights(ActionEvent e)
	{
		flightList.removeAll(customOrderFlights.getItems());
		customOrderFlightsListView.setText("");
	}
	public void addAccommodationsToList(ActionEvent e)
	{
		if(!accommodationList.contains(customOrderAccommodations.getValue()))
			accommodationList.add(customOrderAccommodations.getValue());
		String list="";
		for(long l : accommodationList)
			list+=l+"\n";

		customOrderAccommodationListView.setText(list);
	}
	public void clearAccommodations(ActionEvent e)
	{
		accommodationList.removeAll(customOrderAccommodations.getItems());
		customOrderAccommodationListView.setText("");
	}
	public void addTripsToList(ActionEvent e)
	{
		if(!tripList.contains(customOrderGroupTrip.getValue()))
			tripList.add(customOrderGroupTrip.getValue());
		String list="";
		for(Integer l : tripList)
			list+=l+"\n";

		customOrderTripView.setText(list);
	}
	public void clearTrips(ActionEvent e)
	{
		tripList.removeAll(customOrderGroupTrip.getItems());
		customOrderTripView.setText("");
	}

	public void orderFromPackage(ActionEvent e)
	{
		String a = "Package";
		try {
			int ordId = Shared.getInstance().orderNextNumber();
			Long custId = orderFromPackageCustomerId.getValue();
			String name = orderFromPackageName.getValue();
			if(!control.makeOrderFromPackage(ordId, custId, name))
				throw new FailToAddException();
			Shared.save(Input);
			goodSound();
			success(a, "Success");

		}
		catch(FailToAddException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(IllegelInputException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(CustomerNotExistException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(OrderNotExistException e1) {
			badSound();
			fail(a, e1.toString());
		}
		catch(NumberFormatException e1) {
			badSound();
			fail(a, "Wrong Input!");
		} catch (Exception e1) {
			badSound();
			fail(a, e1.toString());
		}
	}

	public void cancelOrder(ActionEvent e)
	{
		String a = "Order";
		try {
			boolean isOK = false;
			isOK = control.cancelOrder(cancelOrderId.getValue());
			if(isOK == false)
				throw new RemoveOrderException();
			else
			{
				goodSound();
				Shared.save(Input);
				remove(a);
			}
		}
		catch(RemoveOrderException e1) {
			badSound();
			failRemove(a);
		}
	}

	public void success(String content, String header) {
		Alert al = new Alert(Alert.AlertType.INFORMATION);
		al.setContentText(content+" Added Successfully");
		al.setHeaderText(header);
		al.setTitle("Database");
		al.setResizable(false);
		al.showAndWait();
	}

	public void fail(String content, String header) {
		Alert al = new Alert(Alert.AlertType.ERROR);
		al.setContentText("Faild to add : " + content);
		al.setHeaderText(header);
		al.setTitle("Database");
		al.setResizable(false);
		al.showAndWait();
	}

	public void remove(String a) {
		Alert al = new Alert(Alert.AlertType.INFORMATION);
		al.setContentText(a+" Removed Successfully");
		al.setHeaderText("Success");
		al.setTitle("Database");
		al.setResizable(false);
		al.showAndWait();
	}

	public void failRemove(String a) {
		Alert al = new Alert(Alert.AlertType.ERROR);
		al.setContentText(a+" Failed To Remove");
		al.setHeaderText("Fail");
		al.setTitle("Database");
		al.setResizable(false);
		al.showAndWait();
	}

	public void goodSound() {
		Sound s = new Sound();
		try {
			s.successSound();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}

	public void badSound() {
		Sound s = new Sound();
		try {
			s.errorSound();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
	}



}
