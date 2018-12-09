import javafx.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class UserManagementApp extends Application {
	
	// Connection
	static Connection connection;
	
	final String DATABASE_URL = "jdbc:oracle:thin:@199.212.26.208:1521:SQLD";
	//final String DATABASE_URL = "jdbc:oracle:thin:@oracle1.centennialcollege.ca:1521:SQLD"; //From School
	final String USERNAME = "comp228sy_F18_3";
	final String PASSWORD = "password";
	
	// Title label
	private Label lApplicationTitle;
		
	// List Box with users
	private Label lUserListTitle;
	private ListView<Customer> lUserList;
	
	// Form to edit/add/delete a user
	private Label lUserManagemenForm;
	private GridPane gUserManagemenFormGrid;
	
	// Object in the user form
	private LabelInputComponent licFirstName;
	private LabelInputComponent licLastName;
	private LabelInputComponent licHomeAddress;
	private LabelInputComponent licCity;
	private LabelInputComponent licProvince;
	private LabelInputComponent licPostalCode;
	private LabelInputComponent licEmail;
	private LabelInputComponent licPhoneNumber;
	
	// Control buttons in the user form
	private GridPane gButtonPanel;
	private Button bCreateUser;
	private Button bUpdateUser;
	private Button bDeleteUser;
	private Button bCleanForm;
	
	// List of our users
	private ArrayList<Customer> Customers;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		GridPane pane = new GridPane();
    	pane.setAlignment(Pos.TOP_LEFT);
    	pane.setPadding(new Insets(11.5,12.5,13.5,14.5));
    	pane.setHgap(5.5);
    	pane.setVgap(5.5);
    	
    	// Get the customers from server
    	connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
    	
    	populateCustomersArrayList(connection);
        
        connection.close();
    	// Create the title label
    	lApplicationTitle = new Label("User management Application");
    	lApplicationTitle.setFont(new Font("Arial", 30));
    	lApplicationTitle.setStyle("font-weight: bold");
    	pane.setColumnSpan(lApplicationTitle, 2);
    	GridPane.setHalignment(lApplicationTitle, HPos.CENTER);
    	pane.add(lApplicationTitle, 0, 0);
    	
    	// Create the title for the list box
    	lUserListTitle = new Label("User list");
    	lUserListTitle.setFont(new Font("Arial", 18));
    	pane.add(lUserListTitle, 0, 1);
    	
    	// Create the list box
    	lUserList = new ListView<Customer>();    	
    	lUserList.setItems(FXCollections.observableArrayList(Customers));
    	lUserList.setMaxWidth(200);
    	lUserList.setOnMouseClicked(new GetInfoFromList());
    	pane.add(lUserList, 0, 2);
    	
    	// Create the title for the main form
    	lUserManagemenForm = new Label("User Info");
    	lUserManagemenForm.setFont(new Font("Arial", 18));
    	pane.add(lUserManagemenForm, 1, 1);
    	
    	// Create a 
    	gUserManagemenFormGrid = new GridPane();
    	gUserManagemenFormGrid.setPadding(new Insets(11.5,12.5,13.5,14.5));
    	gUserManagemenFormGrid.setHgap(5.5);
    	gUserManagemenFormGrid.setVgap(5.5);
    	gUserManagemenFormGrid.setStyle("-fx-border-color: black");
    	
    	// Create the control of the form
    	licFirstName = new LabelInputComponent("First Name");    	
    	licFirstName.CreateComponent(gUserManagemenFormGrid, 0);
    	
    	licLastName = new LabelInputComponent("Last Name");
    	licLastName.CreateComponent(gUserManagemenFormGrid, 2);
    	
    	licHomeAddress = new LabelInputComponent("Street");
    	licHomeAddress.CreateComponent(gUserManagemenFormGrid, 3);
    	
    	licCity = new LabelInputComponent("City");
    	licCity.CreateComponent(gUserManagemenFormGrid, 4);
    	
    	licProvince = new LabelInputComponent("Province");
    	licProvince.CreateComponent(gUserManagemenFormGrid, 5);
    	
    	licPostalCode = new LabelInputComponent("Postal Code");
    	licPostalCode.CreateComponent(gUserManagemenFormGrid, 6);
    	
    	licEmail = new LabelInputComponent("Email");
    	licEmail.CreateComponent(gUserManagemenFormGrid, 7);
    	
    	licPhoneNumber = new LabelInputComponent("Phone Number");
    	licPhoneNumber.CreateComponent(gUserManagemenFormGrid, 8);
    	
    	// Create the buttons in the user form
    	gButtonPanel = new GridPane();
    	gButtonPanel.setPadding(new Insets(11.5,12.5,13.5,14.5));
    	gButtonPanel.setHgap(5.5);
    	gButtonPanel.setVgap(5.5);
    	
    	bCreateUser = new Button("Create User");
    	bCreateUser.setOnAction(new ManipulateUserInfoClass());
    	gButtonPanel.add(bCreateUser, 0, 0);
    	
    	bUpdateUser = new Button("Update User");
    	bUpdateUser.setOnAction(new ManipulateUserInfoClass());
    	gButtonPanel.add(bUpdateUser, 1, 0);
    	
    	
    	
    	bDeleteUser = new Button("Delete User");
    	bDeleteUser.setOnAction(new ManipulateUserInfoClass());
    	gButtonPanel.add(bDeleteUser, 2, 0);
    	
    	bCleanForm = new Button("Clean Form");
    	bCleanForm.setOnAction(new ManipulateUserInfoClass());
    	bCleanForm.setMaxSize(99999, 99999);
    	gButtonPanel.setColumnSpan(bCleanForm, 3);
    	gButtonPanel.add(bCleanForm, 0, 1);
    	
    	gUserManagemenFormGrid.setColumnSpan(gButtonPanel, 2);
    	gUserManagemenFormGrid.add(gButtonPanel, 0, 9);
    	pane.add(gUserManagemenFormGrid, 1, 2);
    	
    	    	
    	//Create a scene  and place it in the stage    	
    	Scene scene = new Scene(pane);
    	    	
    	primaryStage.setTitle("User management Application");
    	primaryStage.setScene(scene);
    	primaryStage.show();
	}
	public void populateCustomersArrayList(Connection aconnection)
	{
		try {
			Statement statement = aconnection.createStatement();
			ResultSet resultSet = statement.executeQuery("SELECT * FROM CUSTOMERS JOIN ADDRESS USING(ADDRESSID)");
			Customers = new ArrayList<Customer>();
			while (resultSet.next())
			{
				Customer DBCustomer = new Customer();
			  
				// Fill the data
				DBCustomer.setId( Integer.valueOf(resultSet.getString("CUSTOMERID")));
				DBCustomer.setAddressId( Integer.valueOf(resultSet.getString("ADDRESSID")));
				DBCustomer.setFirstName(resultSet.getString("FIRSTNAME"));
				DBCustomer.setLastName(resultSet.getString("LASTNAME"));
			  	DBCustomer.setEmail(resultSet.getString("EMAIL"));
			  	DBCustomer.setPhone(resultSet.getString("PHONE"));
			  	DBCustomer.setStreet(resultSet.getString("STREET"));
			  	DBCustomer.setCity(resultSet.getString("CITY"));
			  	DBCustomer.setProvince(resultSet.getString("PROVINCE"));
			  	DBCustomer.setPostalCode(resultSet.getString("POSTALCODE"));
			  
			  	Customers.add(DBCustomer);
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void close()
	{
		try
		{
			connection.close();
		}
		catch (SQLException sqlException)
		{
			sqlException.printStackTrace();
		}
	}
	// Alert Method for input validation
	public void showAlert(String message, String title)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	class GetInfoFromList implements EventHandler<MouseEvent>
	{		
		@Override
		public void handle(MouseEvent e) {
			
			// Get the user we selected
			Customer Selection = lUserList.getSelectionModel().getSelectedItem();
            
			// If we selected a user
			if (Selection != null)
			{
				licFirstName.getTextField().setText(Selection.getFirstName());
				licLastName.getTextField().setText(Selection.getLastName());
				licHomeAddress.getTextField().setText(Selection.getStreet());
				licCity.getTextField().setText(Selection.getCity());
				licProvince.getTextField().setText(Selection.getProvince());
				licPostalCode.getTextField().setText(Selection.getPostalCode());
				licEmail.getTextField().setText(Selection.getEmail());
				licPhoneNumber.getTextField().setText(Selection.getPhone());
			}
		}
	}
	
	class ManipulateUserInfoClass implements EventHandler<ActionEvent>
	{		
		public void CreateUser()
		{
			Customer NewCustomer = new Customer();
			
			try {
				connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
				
				NewCustomer.setFirstName(licFirstName.GetValue());
				NewCustomer.setLastName(licLastName.GetValue());
				NewCustomer.setEmail(licEmail.GetValue());
				NewCustomer.setPhone(licPhoneNumber.GetValue());
				NewCustomer.setStreet(licHomeAddress.GetValue());
				NewCustomer.setCity(licCity.GetValue());
				NewCustomer.setProvince(licProvince.GetValue());
				NewCustomer.setPostalCode(licPostalCode.GetValue());
				
				//If empty or has numbers inside
				if("".equals(NewCustomer.getFirstName()) || !NewCustomer.getFirstName().matches("[a-zA-Z]+$"))
				{
					showAlert("Please Enter Valid First Name", "First Name Can Not Be Empty");
				}
				else if("".equals(NewCustomer.getLastName()) || !NewCustomer.getLastName().matches("[a-zA-Z]+$"))
				{
					showAlert("Please Enter Last Name", "Last Name Can Not Be Empty");
				}
				else if("".equals(NewCustomer.getEmail()) || !NewCustomer.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
				{
					showAlert("Please Enter Valid Email", "Email Can Not Be Empty");
				}
				else{
					Statement statement = connection.createStatement();
					final String SELECT_BY_EMAIL = "SELECT * FROM CUSTOMERS WHERE EMAIL = '" + NewCustomer.getEmail()  + "'";
					ResultSet resultsSelectByEmail = statement.executeQuery(SELECT_BY_EMAIL);
					if(resultsSelectByEmail.next())
					{
						showAlert("Please enter a different email", "Email Already Exists");
					}
					else
					{
						final String INSERT_ADDRESS_QUERY = "INSERT INTO Address VALUES (Address_Id_Seq.NextVal,'" + NewCustomer.getStreet() + "','" + NewCustomer.getCity() + "','" + NewCustomer.getProvince() + "','" + NewCustomer.getPostalCode() + "')";
						final String INSERT_CUSTOMER_QUERY = "INSERT INTO CUSTOMERS VALUES (Customers_Id_Seq.NextVal,'" + NewCustomer.getLastName() + "','" + NewCustomer.getFirstName() + "', Address_Id_Seq.Currval,'" + NewCustomer.getEmail() + "','" + NewCustomer.getPhone() + "')";
						statement.executeQuery(INSERT_ADDRESS_QUERY);
						statement.executeQuery(INSERT_CUSTOMER_QUERY);
						populateCustomersArrayList(connection);
						lUserList.setItems(FXCollections.observableArrayList(Customers));
					}
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally{
				close();
			}
		}
		
		public void UpdateUser()
		{
			try {
				// Get the user we selected
				
				Customer Selection = lUserList.getSelectionModel().getSelectedItem();
				
				if(Selection != null){
					connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
					
					int Index = Customers.indexOf(Selection);
					Selection = Customers.get(Index);			
					
					Selection.setFirstName(licFirstName.GetValue());
					Selection.setLastName(licLastName.GetValue());
					Selection.setEmail(licEmail.GetValue());
					Selection.setPhone(licPhoneNumber.GetValue());
					Selection.setStreet(licHomeAddress.GetValue());
					Selection.setCity(licCity.GetValue());
					Selection.setProvince(licProvince.GetValue());
					Selection.setPostalCode(licPostalCode.GetValue());
					if("".equals(Selection.getFirstName()) || !Selection.getFirstName().matches("[a-zA-Z]+$"))
					{
						showAlert("Please Enter Valid First Name", "First Name Can Not Be Empty");
					}
					else if("".equals(Selection.getLastName()) || !Selection.getLastName().matches("[a-zA-Z]+$"))
					{
						showAlert("Please Enter Valid Last Name", "Last Name Can Not Be Empty");
					}
					else if("".equals(Selection.getEmail()) || !Selection.getEmail().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$"))
					{
						showAlert("Please Enter Valid Email", "Email Can Not Be Empty");
					}
					else{

					Statement statement = connection.createStatement();
					final String UPDATE_CUSTUMER = "UPDATE CUSTOMERS SET LastName = '" + Selection.getLastName() + "',FirstName = '" + Selection.getFirstName() + "',Email = '" + Selection.getEmail() + "',Phone = '" + Selection.getPhone() + "' WHERE Customerid =" + Selection.getId();
					final String UPDATE_ADDRESS = "UPDATE ADDRESS SET Street = '" + Selection.getStreet() + "',City = '" + Selection.getCity() + "',Province = '"  + Selection.getProvince() + "', PostalCode = '" + Selection.getPostalCode() + "' WHERE AddressID =" + Selection.getAddressId();
					statement.executeQuery(UPDATE_CUSTUMER);
					statement.executeQuery(UPDATE_ADDRESS);
					
					populateCustomersArrayList(connection);
					lUserList.getItems().clear();
					lUserList.setItems(FXCollections.observableArrayList(Customers));
					}
				}
				else{
					showAlert("Please select a user to update", "No User Selected");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			finally{
				close();
			}
			
		}
		
		public void DeleteUser()
		{
			try {
				Customer Selection = lUserList.getSelectionModel().getSelectedItem();
				
				if(Selection != null){
				
					connection = DriverManager.getConnection(DATABASE_URL,USERNAME,PASSWORD);
					Statement statement = connection.createStatement();
					statement.executeQuery("DELETE FROM CUSTOMERS WHERE CUSTOMERID='" + Selection.getId() + "'");
					
					populateCustomersArrayList(connection);
					CleanForm();
					lUserList.setItems(FXCollections.observableArrayList(Customers));
				}
				else{
					showAlert("Please select a user to Delete", "No User Selected");
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			finally{
				close();
			}
		}
		public void CleanForm()
		{
			licFirstName.getTextField().clear();
			licLastName.getTextField().clear();
			licHomeAddress.getTextField().clear();
			licCity.getTextField().clear();
			licProvince.getTextField().clear();
			licPostalCode.getTextField().clear();
			licEmail.getTextField().clear();
			licPhoneNumber.getTextField().clear();
		}
	  @Override
	  public void handle(ActionEvent e)
	  {
		  if (e.getSource().equals(bCreateUser))
			  CreateUser();
		  else if (e.getSource().equals(bUpdateUser))
			  UpdateUser();
		  else if (e.getSource().equals(bDeleteUser))
			  DeleteUser();
		  else if (e.getSource().equals(bCleanForm))
			  CleanForm();		  		  
	  }
	}
	
	

	public static void main(String[] args) {
        try{

              Class.forName("oracle.jdbc.driver.OracleDriver");
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
        
		launch(args);
	}
	

}
