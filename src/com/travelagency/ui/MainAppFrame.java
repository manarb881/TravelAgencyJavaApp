package com.travelagency.ui;

// DAOs
import com.travelagency.dao.AircraftDAO;
import com.travelagency.dao.AirlineDAO;
import com.travelagency.dao.AirportDAO;
import com.travelagency.dao.BookingDAO;
import com.travelagency.dao.CityDAO;
import com.travelagency.dao.CustomerDAO;
import com.travelagency.dao.FlightDAO;
import com.travelagency.dao.GeneralFlightDAO;

// Models
import com.travelagency.model.Aircraft;
import com.travelagency.model.Airline;
import com.travelagency.model.Airport;
import com.travelagency.model.Booking;
import com.travelagency.model.BookingDetail;
import com.travelagency.model.City;
import com.travelagency.model.Customer;
import com.travelagency.model.Flight; // Renamed from FlightInstance to Flight
import com.travelagency.model.GeneralFlight;

// Swing and AWT
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent; // For clearing text fields easily

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

// SQL and Util
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;


public class MainAppFrame extends JFrame {

    // DAOs
    private final CityDAO cityDAO;
    private final AirportDAO airportDAO;
    private final AirlineDAO airlineDAO;
    private final AircraftDAO aircraftDAO;
    private final CustomerDAO customerDAO;
    private final GeneralFlightDAO generalFlightDAO;
    private final FlightDAO flightDAO;
    private final BookingDAO bookingDAO;

    // Common UI component for feedback
    private JTextArea outputArea;

    // Formatters
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat timeFormatShort = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat timeFormatFull = new SimpleDateFormat("HH:mm:ss");


    public MainAppFrame() {
        // Initialize DAOs
        cityDAO = new CityDAO();
        airportDAO = new AirportDAO();
        airlineDAO = new AirlineDAO();
        aircraftDAO = new AircraftDAO();
        customerDAO = new CustomerDAO();
        generalFlightDAO = new GeneralFlightDAO();
        flightDAO = new FlightDAO();
        bookingDAO = new BookingDAO();

        // Set lenient to false for strict date/time parsing
        dateFormat.setLenient(false);
        timeFormatShort.setLenient(false);
        timeFormatFull.setLenient(false);


        setTitle("Travel Agency Management System (Java)");
        setSize(1024, 768); // Adjusted size
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Handle closing explicitly
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // --- Create and Add Panels for each functionality ---
        tabbedPane.addTab("1a. Add City", createAddCityPanel());
        tabbedPane.addTab("1b. Add Airport", createAddAirportPanel());
        tabbedPane.addTab("1c. Add Airline", createAddAirlinePanel());
        tabbedPane.addTab("1d. Add Aircraft", createAddAircraftPanel());
        tabbedPane.addTab("1e. Add Customer", createAddCustomerPanel());
        tabbedPane.addTab("1f. Add Gen. Flight", createAddGeneralFlightPanel());
        tabbedPane.addTab("1g. Add Op. Flight", createAddFlightInstancePanel());
        tabbedPane.addTab("1h. Add Booking", createAddBookingPanel());

        tabbedPane.addTab("2. Search Flights (Cities)", createSearchFlightsByCitiesPanel());
        tabbedPane.addTab("3. Search Flights (Period)", createSearchFlightsByCityAndPeriodPanel());
        tabbedPane.addTab("4. Retrieve Booking (SP)", createRetrieveBookingDetailsPanel());
        tabbedPane.addTab("5. Flight Customers", createFlightCustomersPanel());
        tabbedPane.addTab("6. Airlines in City", createAirlinesInCityPanel());

        tabbedPane.addTab("7. Update Booking", createUpdateBookingPanel());
        tabbedPane.addTab("8. Update Op. Flight", createUpdateFlightInstancePanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Output area at the bottom
        outputArea = new JTextArea(6, 30); // Increased rows
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPaneOutput = new JScrollPane(outputArea);
        add(scrollPaneOutput, BorderLayout.SOUTH);

        // Ensure DB connection is closed when window closes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int confirmed = JOptionPane.showConfirmDialog(MainAppFrame.this,
                        "Are you sure you want to exit the application?", "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION);

                if (confirmed == JOptionPane.YES_OPTION) {
                    com.travelagency.db.DatabaseConnection.closeConnection();
                    System.out.println("Application closing.");
                    dispose(); // Close the frame
                    System.exit(0); // Terminate the application
                }
            }
        });
    }

    // --- Helper method for GridBagConstraints ---
    private GridBagConstraints createGBC(int x, int y, int width, double weighty, int anchor, Insets insets, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.anchor = anchor;
        gbc.insets = insets != null ? insets : new Insets(2, 5, 2, 5); // Reduced vertical insets
        gbc.fill = fill;
        gbc.weightx = (fill == GridBagConstraints.HORIZONTAL || fill == GridBagConstraints.BOTH) ? 1.0 : 0.0;
        gbc.weighty = weighty;
        return gbc;
    }

    private GridBagConstraints createGBC(int x, int y) { // simpler version for labels and standard fields
        return createGBC(x, y, 1, 0.0, GridBagConstraints.WEST, new Insets(2,5,2,5), GridBagConstraints.HORIZONTAL);
    }
     private GridBagConstraints createLabelGBC(int x, int y) { // For JLabels, aligned EAST
        return createGBC(x, y, 1, 0.0, GridBagConstraints.EAST, new Insets(2,5,2,5), GridBagConstraints.NONE);
    }


    // --- Panel Creation Methods ---

    // Functionality 1a: Add City
    private JPanel createAddCityPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField cityNameField = new JTextField(20);
        JButton addCityButton = new JButton("Add City");

        panel.add(new JLabel("City Name:"), createLabelGBC(0, 0));
        panel.add(cityNameField, createGBC(1, 0));
        panel.add(addCityButton, createGBC(0, 1, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        addCityButton.addActionListener(e -> {
            String name = cityNameField.getText().trim();
            if (name.isEmpty()) {
                JO("City name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            City newCity = new City(name);
            try {
                if (cityDAO.addCity(newCity)) {
                    oa("City '" + newCity.getCityName() + "' added with ID: " + newCity.getCityId());
                    cf(cityNameField);
                } else { oa("Failed to add city."); }
            } catch (SQLException ex) { hs("add city", ex); }
        });
        return panel;
    }

    // Functionality 1b: Add Airport
    private JPanel createAddAirportPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField airportNameField = new JTextField(20);
        JComboBox<City> cityComboBox = new JComboBox<>();
        JButton addAirportButton = new JButton("Add Airport");
        JButton refreshCitiesButton = new JButton("Refresh Cities");
        populateCityComboBox(cityComboBox);

        int y = 0;
        panel.add(new JLabel("Airport Name:"), createLabelGBC(0, y));
        panel.add(airportNameField, createGBC(1, y++, 2,0.0,GridBagConstraints.WEST, null, GridBagConstraints.HORIZONTAL)); // Span 2 cols for field

        panel.add(new JLabel("City:"), createLabelGBC(0, y));
        panel.add(cityComboBox, createGBC(1, y));
        panel.add(refreshCitiesButton, createGBC(2, y++,1,0.0,GridBagConstraints.WEST, new Insets(2,0,2,5), GridBagConstraints.NONE));


        panel.add(addAirportButton, createGBC(0, y, 3,0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        refreshCitiesButton.addActionListener(e -> populateCityComboBox(cityComboBox));
        addAirportButton.addActionListener(e -> {
            String name = airportNameField.getText().trim();
            City selectedCity = (City) cityComboBox.getSelectedItem();
            if (name.isEmpty()){ JO("Airport name empty.","Input Error",JOptionPane.ERROR_MESSAGE); return; }
            if (selectedCity == null || selectedCity.getCityId() == -1) { JO("Please select a valid city.","Input Error",JOptionPane.ERROR_MESSAGE); return; }

            Airport newAirport = new Airport(name, selectedCity.getCityId());
            try {
                if (airportDAO.addAirport(newAirport)) {
                    oa("Airport '" + newAirport.getAirportName() + "' ID:" + newAirport.getAirportId() + " in " + selectedCity.getCityName());
                    cf(airportNameField); cityComboBox.setSelectedIndex(0); // Assuming first item is valid or a placeholder
                } else { oa("Failed to add airport."); }
            } catch (SQLException ex) { hs("add airport", ex); }
        });
        return panel;
    }

    // Functionality 1c: Add Airline
    private JPanel createAddAirlinePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField airlineNameField = new JTextField(20);
        JButton addAirlineButton = new JButton("Add Airline");

        panel.add(new JLabel("Airline Name:"), createLabelGBC(0, 0));
        panel.add(airlineNameField, createGBC(1, 0));
        panel.add(addAirlineButton, createGBC(0, 1, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        addAirlineButton.addActionListener(e -> {
            String name = airlineNameField.getText().trim();
            if (name.isEmpty()){ JO("Airline name empty.","Input Error",JOptionPane.ERROR_MESSAGE); return; }
            Airline newAirline = new Airline(name);
            try {
                if (airlineDAO.addAirline(newAirline)) {
                    oa("Airline '" + newAirline.getAirlineName() + "' ID:" + newAirline.getAirlineId());
                    cf(airlineNameField);
                } else { oa("Failed add airline."); }
            } catch (SQLException ex) { hs("add airline", ex); }
        });
        return panel;
    }

    // Functionality 1d: Add Aircraft
    private JPanel createAddAircraftPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField aircraftNameField = new JTextField(20);
        JButton addAircraftButton = new JButton("Add Aircraft");

        panel.add(new JLabel("Aircraft Name/Model:"), createLabelGBC(0, 0));
        panel.add(aircraftNameField, createGBC(1, 0));
        panel.add(addAircraftButton, createGBC(0, 1, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        addAircraftButton.addActionListener(e -> {
            String name = aircraftNameField.getText().trim();
            if (name.isEmpty()){ JO("Aircraft name empty.","Input Error",JOptionPane.ERROR_MESSAGE); return; }
            Aircraft newAircraft = new Aircraft(name);
            try {
                if (aircraftDAO.addAircraft(newAircraft)) {
                    oa("Aircraft '" + newAircraft.getAircraftName() + "' ID:" + newAircraft.getAircraftId());
                    cf(aircraftNameField);
                } else { oa("Failed add aircraft."); }
            } catch (SQLException ex) { hs("add aircraft", ex); }
        });
        return panel;
    }
    
    // Functionality 1e: Add Customer
    private JPanel createAddCustomerPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        JTextField customerIdField = new JTextField(15);
        JTextField firstNameField = new JTextField(20);
        JTextField lastNameField = new JTextField(20);
        JTextField addressField = new JTextField(30);
        JTextField emailField = new JTextField(25);
        JTextField phoneField = new JTextField(15);
        JButton addCustomerButton = new JButton("Add Customer");

        int y = 0;
        panel.add(new JLabel("Customer ID (National ID/SSN):"), createLabelGBC(0, y));
        panel.add(customerIdField, createGBC(1, y++));
        panel.add(new JLabel("First Name:"), createLabelGBC(0, y));
        panel.add(firstNameField, createGBC(1, y++));
        panel.add(new JLabel("Last Name:"), createLabelGBC(0, y));
        panel.add(lastNameField, createGBC(1, y++));
        panel.add(new JLabel("Address:"), createLabelGBC(0, y));
        panel.add(addressField, createGBC(1, y++));
        panel.add(new JLabel("Email:"), createLabelGBC(0, y));
        panel.add(emailField, createGBC(1, y++));
        panel.add(new JLabel("Phone Number:"), createLabelGBC(0, y));
        panel.add(phoneField, createGBC(1, y++));
        panel.add(addCustomerButton, createGBC(0, y, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        addCustomerButton.addActionListener(e -> {
            String custId = customerIdField.getText().trim(); String fName = firstNameField.getText().trim();
            String lName = lastNameField.getText().trim(); String address = addressField.getText().trim();
            String email = emailField.getText().trim(); String phone = phoneField.getText().trim();
            if (custId.isEmpty() || fName.isEmpty() || lName.isEmpty() || email.isEmpty()) {
                JO("Cust ID, FName, LName, Email are required.","Input Error",JOptionPane.ERROR_MESSAGE); return;
            }
            Customer newCust = new Customer(custId, fName, lName, address, email, phone);
            try {
                if (customerDAO.addCustomer(newCust)) {
                    oa("Customer '" + fName + " " + lName + "' ID:" + custId + " added.");
                    cf(customerIdField, firstNameField, lastNameField, addressField, emailField, phoneField);
                } else { oa("Failed to add customer (no rows affected, check constraints).");}
            } catch (SQLException ex) {
                if (ex.getErrorCode() == 1062) { // MySQL duplicate entry
                    hs("add customer - Duplicate ID or Email", ex);
                } else { hs("add customer", ex); }
            }
        });
        return panel;
    }

    // Functionality 1f: Add General Flight (Schedule)
    private JPanel createAddGeneralFlightPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        JTextField fDayField = new JTextField(10); // YYYY-MM-DD
        JTextField takeoffTimeField = new JTextField(8); // HH:MM or HH:MM:SS
        JTextField arrivalTimeField = new JTextField(8); // HH:MM or HH:MM:SS
        JTextField seatsField = new JTextField(5);
        JComboBox<Airport> depAirportComboBox = new JComboBox<>();
        JComboBox<Airport> arrAirportComboBox = new JComboBox<>();
        JComboBox<Airline> airlineComboBox = new JComboBox<>();
        JButton addButton = new JButton("Add General Flight Schedule");
        JButton refreshListsButton = new JButton("Refresh Airport/Airline Lists");

        int y = 0;
        p.add(new JLabel("FDay (YYYY-MM-DD):"), createLabelGBC(0, y)); p.add(fDayField, createGBC(1, y++));
        p.add(new JLabel("Takeoff Time (HH:MM):"), createLabelGBC(0, y)); p.add(takeoffTimeField, createGBC(1, y++));
        p.add(new JLabel("Arrival Time (HH:MM):"), createLabelGBC(0, y)); p.add(arrivalTimeField, createGBC(1, y++));
        p.add(new JLabel("Departure Airport:"), createLabelGBC(0, y)); p.add(depAirportComboBox, createGBC(1, y++));
        p.add(new JLabel("Arrival Airport:"), createLabelGBC(0, y)); p.add(arrAirportComboBox, createGBC(1, y++));
        p.add(new JLabel("Airline:"), createLabelGBC(0, y)); p.add(airlineComboBox, createGBC(1, y++));
        p.add(new JLabel("Number of Seats:"), createLabelGBC(0, y)); p.add(seatsField, createGBC(1, y++));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshListsButton);
        buttonPanel.add(addButton);
        p.add(buttonPanel, createGBC(0, y, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        // Populate ComboBoxes
        populateAirportComboBox(depAirportComboBox, "Departure");
        populateAirportComboBox(arrAirportComboBox, "Arrival");
        populateAirlineComboBox(airlineComboBox);

        refreshListsButton.addActionListener(e -> {
            populateAirportComboBox(depAirportComboBox, "Departure");
            populateAirportComboBox(arrAirportComboBox, "Arrival");
            populateAirlineComboBox(airlineComboBox);
            oa("Airport and Airline lists refreshed.");
        });

        addButton.addActionListener(e -> {
            Date fDay = parseSqlDate(fDayField.getText());
            Time takeoffTime = parseSqlTime(takeoffTimeField.getText());
            Time arrivalTime = parseSqlTime(arrivalTimeField.getText());
            Airport depAirport = (Airport) depAirportComboBox.getSelectedItem();
            Airport arrAirport = (Airport) arrAirportComboBox.getSelectedItem();
            Airline airline = (Airline) airlineComboBox.getSelectedItem();
            String seatsStr = seatsField.getText().trim();

            if (fDay == null || takeoffTime == null || arrivalTime == null ||
                depAirport == null || arrAirport == null || airline == null || seatsStr.isEmpty() ||
                depAirport.getAirportId() == -1 || arrAirport.getAirportId() == -1 || airline.getAirlineId() == -1) {
                JO("All fields and valid selections are required.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
            }
            if (depAirport.getAirportId() == arrAirport.getAirportId()) {
                JO("Departure and Arrival airports must be different.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
            }
            if (arrivalTime.before(takeoffTime) || arrivalTime.equals(takeoffTime)) { // Simple check for same day
                JO("Arrival time must be after takeoff time.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
            }
            int numSeats;
            try {
                numSeats = Integer.parseInt(seatsStr);
                if (numSeats <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                JO("Number of seats must be a positive integer.", "Input Error", JOptionPane.ERROR_MESSAGE); return;
            }

            GeneralFlight newGf = new GeneralFlight(takeoffTime, arrivalTime, fDay, depAirport.getAirportId(), arrAirport.getAirportId(), airline.getAirlineId(), numSeats);
            try {
                if (generalFlightDAO.addGeneralFlight(newGf)) {
                    oa("General Flight Schedule ID: " + newGf.getGeneralFlightId() + " added.");
                    cf(fDayField, takeoffTimeField, arrivalTimeField, seatsField);
                    depAirportComboBox.setSelectedIndex(0); arrAirportComboBox.setSelectedIndex(0); airlineComboBox.setSelectedIndex(0);
                } else { oa("Failed to add General Flight Schedule."); }
            } catch (SQLException ex) { hs("add General Flight", ex); }
        });
        return p;
    }
    
    // --- TODO: Implement the rest of the panel creation methods using the examples above as a guide ---
    // For complex JComboBox displays (like showing more flight details), you'll need Wrapper classes.

    // Functionality 1g: Add Operational Flight (Instance)
    private JPanel createAddFlightInstancePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        Insets defaultInsets = new Insets(3, 5, 3, 5); // Slightly smaller insets

        JComboBox<GeneralFlightDisplayWrapper> generalFlightComboBox = new JComboBox<>();
        JTextField takeOffDateField = new JTextField(10); // YYYY-MM-DD
        JTextField arrivalDateField = new JTextField(10); // YYYY-MM-DD
        JComboBox<Aircraft> aircraftComboBox = new JComboBox<>();
        String[] flightStates = {"0 - Scheduled", "1 - In Progress", "2 - Completed", "3 - Cancelled"};
        JComboBox<String> stateComboBox = new JComboBox<>(flightStates);
        JButton addButton = new JButton("Add Operational Flight");
        JButton refreshListsButton = new JButton("Refresh Schedules/Aircraft");

        int y = 0;
        panel.add(new JLabel("General Flight Schedule:"), createLabelGBC(0, y));
        panel.add(generalFlightComboBox, createGBC(1, y++, 2,0.0,GridBagConstraints.WEST,null,GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Actual Takeoff Date (YYYY-MM-DD):"), createLabelGBC(0, y));
        panel.add(takeOffDateField, createGBC(1, y++, 2,0.0,GridBagConstraints.WEST,null,GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Actual Arrival Date (YYYY-MM-DD):"), createLabelGBC(0, y));
        panel.add(arrivalDateField, createGBC(1, y++, 2,0.0,GridBagConstraints.WEST,null,GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Assign Aircraft (Optional):"), createLabelGBC(0, y));
        panel.add(aircraftComboBox, createGBC(1, y++, 2,0.0,GridBagConstraints.WEST,null,GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Initial State:"), createLabelGBC(0, y));
        panel.add(stateComboBox, createGBC(1, y++, 2,0.0,GridBagConstraints.WEST,null,GridBagConstraints.HORIZONTAL));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshListsButton);
        buttonPanel.add(addButton);
        panel.add(buttonPanel, createGBC(0, y, 3,0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        // Populate ComboBoxes
        populateGeneralFlightComboBox(generalFlightComboBox);
        populateAircraftComboBox(aircraftComboBox);

        refreshListsButton.addActionListener(e -> {
            populateGeneralFlightComboBox(generalFlightComboBox);
            populateAircraftComboBox(aircraftComboBox);
            oa("General Flight Schedules and Aircraft lists refreshed.");
        });

        addButton.addActionListener(e -> {
            GeneralFlightDisplayWrapper selectedGfWrapper = (GeneralFlightDisplayWrapper) generalFlightComboBox.getSelectedItem();
            Date takeOffDate = parseSqlDate(takeOffDateField.getText());
            Date arrivalDate = parseSqlDate(arrivalDateField.getText());
            Aircraft selectedAircraft = (Aircraft) aircraftComboBox.getSelectedItem();
            String selectedStateStr = (String) stateComboBox.getSelectedItem();

            if (selectedGfWrapper == null || selectedGfWrapper.getGeneralFlight().getGeneralFlightId() == -1 ||
                takeOffDate == null || arrivalDate == null || selectedStateStr == null) {
                JO("General Flight Schedule, Takeoff Date, Arrival Date, and State are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (arrivalDate.before(takeOffDate)) {
                JO("Arrival Date cannot be before Takeoff Date.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int generalFlightId = selectedGfWrapper.getGeneralFlight().getGeneralFlightId();
            Integer aircraftId = (selectedAircraft != null && selectedAircraft.getAircraftId() != -1) ? selectedAircraft.getAircraftId() : null;
            int state = Integer.parseInt(selectedStateStr.substring(0, selectedStateStr.indexOf(" ")));

            Flight newFlight = new Flight(takeOffDate, arrivalDate, state, generalFlightId, aircraftId);
            try {
                if (flightDAO.addFlight(newFlight)) {
                    oa("Operational Flight ID: " + newFlight.getFlightId() + " for GFID: " + generalFlightId + " added.");
                    cf(takeOffDateField, arrivalDateField);
                    generalFlightComboBox.setSelectedIndex(0); // Assuming first is a valid or placeholder
                    aircraftComboBox.setSelectedIndex(0); // Assuming first is "--Select Aircraft (Optional)--"
                    stateComboBox.setSelectedIndex(0);
                } else {
                    oa("Failed to add Operational Flight.");
                }
            } catch (SQLException ex) {
                hs("add Operational Flight", ex);
            }
        });
        return panel;
    }
    // Functionality 1h: Add Booking
    private JPanel createAddBookingPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        Insets defaultInsets = new Insets(3, 5, 3, 5);

        JComboBox<Customer> customerComboBox = new JComboBox<>();
        JComboBox<FlightDisplayWrapper> operationalFlightComboBox = new JComboBox<>(); // For bookable flights
        String[] bookingStates = {"0 - Pending", "1 - Confirmed"}; // Usually new bookings are pending or confirmed
        JComboBox<String> stateComboBox = new JComboBox<>(bookingStates);
        JButton addButton = new JButton("Add Booking");
        JButton refreshListsButton = new JButton("Refresh Customer/Flight Lists");

        int y = 0;
        panel.add(new JLabel("Select Customer:"), createLabelGBC(0, y));
        panel.add(customerComboBox, createGBC(1, y++, 2, 0.0, GridBagConstraints.WEST, null, GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Select Operational Flight:"), createLabelGBC(0, y));
        panel.add(operationalFlightComboBox, createGBC(1, y++, 2, 0.0, GridBagConstraints.WEST, null, GridBagConstraints.HORIZONTAL));

        panel.add(new JLabel("Booking State:"), createLabelGBC(0, y));
        panel.add(stateComboBox, createGBC(1, y++, 2, 0.0, GridBagConstraints.WEST, null, GridBagConstraints.HORIZONTAL));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(refreshListsButton);
        buttonPanel.add(addButton);
        panel.add(buttonPanel, createGBC(0, y, 3, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

        // Populate ComboBoxes
        populateCustomerComboBox(customerComboBox);
        populateOperationalFlightComboBox(operationalFlightComboBox); // This helper needs to filter for bookable flights

        refreshListsButton.addActionListener(e -> {
            populateCustomerComboBox(customerComboBox);
            populateOperationalFlightComboBox(operationalFlightComboBox);
            oa("Customer and Operational Flight lists refreshed.");
        });

        addButton.addActionListener(e -> {
            Customer selectedCustomer = (Customer) customerComboBox.getSelectedItem();
            FlightDisplayWrapper selectedFlightWrapper = (FlightDisplayWrapper) operationalFlightComboBox.getSelectedItem();
            String selectedStateStr = (String) stateComboBox.getSelectedItem();

            if (selectedCustomer == null || (selectedCustomer.getCustomerId() != null && selectedCustomer.getCustomerId().equals("-1")) || // Check for placeholder
                selectedFlightWrapper == null || selectedFlightWrapper.getFlight() == null || selectedFlightWrapper.getFlight().getFlightId() == -1 || // Check for placeholder
                selectedStateStr == null) {
                JO("Customer, Operational Flight, and Booking State are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String customerId = selectedCustomer.getCustomerId();
            Flight selectedFlight = selectedFlightWrapper.getFlight();
            int flightId = selectedFlight.getFlightId();
            int generalFlightId = selectedFlight.getGeneralFlightId(); // Flight model should have this
            int state = Integer.parseInt(selectedStateStr.substring(0, selectedStateStr.indexOf(" ")));
            Timestamp bookingDate = new Timestamp(System.currentTimeMillis()); // Set booking date to now

            Booking newBooking = new Booking(state, customerId, flightId, generalFlightId);
            newBooking.setBookingDate(bookingDate); // Set explicitly if your constructor doesn't

            try {
                if (bookingDAO.addBooking(newBooking)) {
                    oa("Booking successful! Booking Number: " + newBooking.getBookingNumber() +
                       " for Customer: " + customerId + " on Flight ID: " + flightId);
                    // Clear selections or reset to default
                    customerComboBox.setSelectedIndex(0);
                    operationalFlightComboBox.setSelectedIndex(0); // This might not be ideal if list is long
                    stateComboBox.setSelectedIndex(0);
                    // IMPORTANT: Refresh the operational flight combo box as seat availability might have changed
                    populateOperationalFlightComboBox(operationalFlightComboBox);
                } else {
                    // This 'else' might not be hit if addBooking throws exception for constraint violation
                    // or if the trigger signals an error that is caught as SQLException
                    oa("Failed to add booking (no rows affected).");
                }
            } catch (SQLException ex) {
                // Check if the error is from our "No available seats" trigger
                if (ex.getErrorCode() == 1644 && ex.getMessage() != null &&
                    ex.getMessage().toLowerCase().contains("no available seats")) {
                    JO("Booking Failed: " + ex.getMessage(), "Seat Availability Error", JOptionPane.ERROR_MESSAGE);
                    oa("Booking Failed: " + ex.getMessage());
                } else {
                    hs("add booking", ex); // General SQL error
                }
                // Refresh flight list even on error, as an attempt was made
                populateOperationalFlightComboBox(operationalFlightComboBox);
            }
        });
        return panel;
    }
    // Functionality 2: Search Flight Schedules by Departure and Arrival Cities
    private JPanel createSearchFlightsByCitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10)); // Main panel with BorderLayout

        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Use FlowLayout for simple horizontal arrangement
        JTextField departureCityField = new JTextField(15);
        JTextField arrivalCityField = new JTextField(15);
        JButton searchButton = new JButton("Search Flight Schedules");

        inputPanel.add(new JLabel("Departure City:"));
        inputPanel.add(departureCityField);
        inputPanel.add(new JLabel("Arrival City:"));
        inputPanel.add(arrivalCityField);
        inputPanel.add(searchButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        // --- Results Table (Center) ---
        String[] columnNames = {"GFID", "Airline", "Dep. Airport", "Arr. Airport", "Dep. Time", "Arr. Time", "Ref. Date", "Seats Avail."};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table cells non-editable
            }
        };
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true); // Table uses entire height of scroll pane
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        panel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // --- Action Listener for Search Button ---
        searchButton.addActionListener(e -> {
            String depCity = departureCityField.getText().trim();
            String arrCity = arrivalCityField.getText().trim();

            if (depCity.isEmpty() || arrCity.isEmpty()) {
                JO("Both Departure and Arrival city names are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.setRowCount(0); // Clear previous results
            oa("Searching for flight schedules from " + depCity + " to " + arrCity + "...");

            try {
                // This method was in FlightDAO, but logically searches GeneralFlight schedules
                // Let's assume it's `flightDAO.getFlightSchedulesByCities`
                List<GeneralFlight> schedules = flightDAO.getFlightSchedulesByCities(depCity, arrCity);

                if (schedules.isEmpty()) {
                    oa("No flight schedules found matching your criteria.");
                } else {
                    oa(schedules.size() + " schedule(s) found.");
                    for (GeneralFlight gf : schedules) {
                        // For display, we ideally want names, not just IDs.
                        // This requires fetching related data or having a more complex DTO from the DAO.
                        // For now, we'll try to fetch some names. This can be slow if done per row.
                        // A better DAO method would return richer objects or use a JOIN.
                        String airlineName = "AirlineID:" + gf.getAirlineId();
                        String depAirportName = "AirportID:" + gf.getDepAirportId();
                        String arrAirportName = "AirportID:" + gf.getArrAirportId();

                        try {
                            Airline airline = airlineDAO.getAirlineById(gf.getAirlineId());
                            if (airline != null) airlineName = airline.getAirlineName();
                        } catch (SQLException ex) { /* ignore, use ID */ }
                        try {
                            Airport depAp = airportDAO.getAirportById(gf.getDepAirportId());
                            if (depAp != null) depAirportName = depAp.getAirportName();
                        } catch (SQLException ex) { /* ignore, use ID */ }
                        try {
                            Airport arrAp = airportDAO.getAirportById(gf.getArrAirportId());
                            if (arrAp != null) arrAirportName = arrAp.getAirportName();
                        } catch (SQLException ex) { /* ignore, use ID */ }


                        tableModel.addRow(new Object[]{
                                gf.getGeneralFlightId(),
                                airlineName,
                                depAirportName,
                                arrAirportName,
                                timeFormatShort.format(gf.getTakeoffTime()), // Format time
                                timeFormatShort.format(gf.getArrivalTime()), // Format time
                                dateFormat.format(gf.getfDay()),     // Format date
                                (gf.getNumberSeats() - gf.getSeatsBooked())
                        });
                    }
                }
            } catch (SQLException ex) {
                hs("searching flight schedules by cities", ex);
            }
        });

        return panel;
    }
    // Functionality 3: Search Operational Flights by Departure City and Period
    private JPanel createSearchFlightsByCityAndPeriodPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new GridBagLayout());
        JTextField departureCityField = new JTextField(15);
        JTextField startDateField = new JTextField(10); // YYYY-MM-DD
        JTextField endDateField = new JTextField(10);   // YYYY-MM-DD
        JButton searchButton = new JButton("Search Operational Flights");

        int y = 0;
        inputPanel.add(new JLabel("Departure City:"), createLabelGBC(0, y));
        inputPanel.add(departureCityField, createGBC(1, y++));
        inputPanel.add(new JLabel("Start Date (YYYY-MM-DD):"), createLabelGBC(0, y));
        inputPanel.add(startDateField, createGBC(1, y++));
        inputPanel.add(new JLabel("End Date (YYYY-MM-DD):"), createLabelGBC(0, y));
        inputPanel.add(endDateField, createGBC(1, y++));
        inputPanel.add(searchButton, createGBC(0, y, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));
        panel.add(inputPanel, BorderLayout.NORTH);

        // --- Results Table (Center) ---
        String[] columnNames = {"Flight ID", "Airline", "Dep. City", "Arr. City", "Takeoff Date", "Arrival Date", "State", "Aircraft", "Seats Avail."};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // --- Action Listener ---
        searchButton.addActionListener(e -> {
            String depCity = departureCityField.getText().trim();
            Date startDate = parseSqlDate(startDateField.getText()); // Uses your helper
            Date endDate = parseSqlDate(endDateField.getText());   // Uses your helper

            if (depCity.isEmpty() || startDate == null || endDate == null) {
                JO("Departure City, Start Date, and End Date are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (endDate.before(startDate)) {
                JO("End Date cannot be before Start Date.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.setRowCount(0);
            oa("Searching operational flights from " + depCity + " between " + dateFormat.format(startDate) + " and " + dateFormat.format(endDate) + "...");

            try {
                List<Flight> flights = flightDAO.getFlightsFromCityInPeriod(depCity, startDate, endDate);

                if (flights.isEmpty()) {
                    oa("No operational flights found matching your criteria.");
                } else {
                    oa(flights.size() + " operational flight(s) found.");
                    for (Flight f : flights) {
                        // Fetch related details for display (can be slow, better in DAO with JOINs)
                        String airlineName = "GFID:" + f.getGeneralFlightId();
                        String depCityName = "N/A";
                        String arrCityName = "N/A";
                        String aircraftNameStr = "N/A";
                        int seatsAvailable = 0;

                        try {
                            GeneralFlight gf = generalFlightDAO.getGeneralFlightById(f.getGeneralFlightId());
                            if (gf != null) {
                                seatsAvailable = gf.getNumberSeats() - gf.getSeatsBooked();
                                try {
                                    Airline airline = airlineDAO.getAirlineById(gf.getAirlineId());
                                    if (airline != null) airlineName = airline.getAirlineName();
                                } catch (SQLException ignored) {}
                                try {
                                    Airport depAp = airportDAO.getAirportById(gf.getDepAirportId());
                                    if (depAp != null) {
                                        City city = cityDAO.getCityById(depAp.getCityId());
                                        if (city != null) depCityName = city.getCityName();
                                    }
                                } catch (SQLException ignored) {}
                                try {
                                    Airport arrAp = airportDAO.getAirportById(gf.getArrAirportId());
                                    if (arrAp != null) {
                                        City city = cityDAO.getCityById(arrAp.getCityId());
                                        if (city != null) arrCityName = city.getCityName();
                                    }
                                } catch (SQLException ignored) {}
                            }
                            if (f.getAircraftId() != null) {
                                Aircraft aircraft = aircraftDAO.getAircraftById(f.getAircraftId());
                                if (aircraft != null) aircraftNameStr = aircraft.getAircraftName();
                            }
                        } catch (SQLException ex) {
                            oa("Error fetching details for flight ID " + f.getFlightId() + ": " + ex.getMessage());
                        }

                        tableModel.addRow(new Object[]{
                                f.getFlightId(),
                                airlineName,
                                depCityName,
                                arrCityName,
                                dateFormat.format(f.getTakeOffDate()),
                                dateFormat.format(f.getArrivalDate()),
                                f.getStateString(), // Uses helper in Flight model
                                aircraftNameStr,
                                seatsAvailable
                        });
                    }
                }
            } catch (SQLException ex) {
                hs("searching operational flights by city/period", ex);
            }
        });
        return panel;
    }
     // Functionality 4: Retrieve Booking Details (Using Stored Procedure)
     private JPanel createRetrieveBookingDetailsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField bookingNumberField = new JTextField(10);
        JButton retrieveButton = new JButton("Retrieve Booking Details");
        inputPanel.add(new JLabel("Booking Number:"));
        inputPanel.add(bookingNumberField);
        inputPanel.add(retrieveButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        // --- Details Display Area (Center) ---
        JTextArea detailsArea = new JTextArea(20, 70); // Generous size for details
        detailsArea.setEditable(false);
        detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        detailsArea.setLineWrap(true);
        detailsArea.setWrapStyleWord(true);
        panel.add(new JScrollPane(detailsArea), BorderLayout.CENTER);

        // --- Action Listener ---
        retrieveButton.addActionListener(e -> {
            String bookingNumStr = bookingNumberField.getText().trim();
            if (bookingNumStr.isEmpty()) {
                JO("Booking Number cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int bookingNumber;
            try {
                bookingNumber = Integer.parseInt(bookingNumStr);
            } catch (NumberFormatException nfe) {
                JO("Booking Number must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            detailsArea.setText(""); // Clear previous details
            oa("Retrieving details for Booking Number: " + bookingNumber + "...");

            try {
                BookingDetail detail = bookingDAO.retrieveBookingDetailsSP(bookingNumber);

                if (detail != null) {
                    oa("Booking details retrieved successfully.");
                    StringBuilder sb = new StringBuilder();
                    sb.append("--- BOOKING INFORMATION ---\n");
                    sb.append(String.format("%-25s: %s\n", "Booking Number", detail.getBookingNumber()));
                    sb.append(String.format("%-25s: %s\n", "Booking Status", detail.getBookingStateString())); // Using helper
                    sb.append(String.format("%-25s: %s\n", "Booking Date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(detail.getBookingDate())));
                    sb.append("\n--- CUSTOMER INFORMATION ---\n");
                    sb.append(String.format("%-25s: %s\n", "Customer ID", detail.getCustomerId()));
                    sb.append(String.format("%-25s: %s %s\n", "Customer Name", detail.getCustomerFirstName(), detail.getCustomerLastName()));
                    sb.append(String.format("%-25s: %s\n", "Email", detail.getCustomerEmail()));
                    sb.append(String.format("%-25s: %s\n", "Address", detail.getCustomerAddress()));
                    sb.append(String.format("%-25s: %s\n", "Phone", detail.getCustomerPhone()));
                    sb.append("\n--- FLIGHT INFORMATION ---\n");
                    sb.append(String.format("%-25s: %s\n", "Flight ID (Instance)", detail.getFlightId()));
                    sb.append(String.format("%-25s: %s\n", "Airline", detail.getAirlineName()));
                    sb.append(String.format("%-25s: %s\n", "Aircraft", detail.getAircraftName() != null ? detail.getAircraftName() : "N/A"));
                    sb.append(String.format("%-25s: %s (%s)\n", "Departure", detail.getDepartureAirport(), detail.getDepartureCity()));
                    sb.append(String.format("%-25s: %s (%s)\n", "Arrival", detail.getArrivalAirport(), detail.getArrivalCity()));
                    sb.append(String.format("%-25s: %s\n", "Actual Takeoff Date", dateFormat.format(detail.getFlightTakeOffDate())));
                    sb.append(String.format("%-25s: %s\n", "Actual Arrival Date", dateFormat.format(detail.getFlightArrivalDate())));
                    sb.append(String.format("%-25s: %s\n", "Flight Scheduled Dep.", detail.getGeneralFlightScheduledDeparture()));
                    sb.append(String.format("%-25s: %s\n", "Flight Scheduled Arr.", detail.getGeneralFlightScheduledArrival()));
                    sb.append(String.format("%-25s: %s\n", "Flight State", detail.getFlightStateString())); // Using helper
                    sb.append("\n--- SEAT INFORMATION (General Flight Schedule) ---\n");
                    sb.append(String.format("%-25s: %s\n", "General Flight ID", detail.getGeneralFlightId()));
                    sb.append(String.format("%-25s: %d\n", "Total Seats on Schedule", detail.getNumberSeats()));
                    sb.append(String.format("%-25s: %d\n", "Seats Booked on Schedule", detail.getSeatsBooked()));

                    detailsArea.setText(sb.toString());
                } else {
                    oa("No booking found with Booking Number: " + bookingNumber);
                    JO("No booking found with Booking Number: " + bookingNumber, "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                hs("retrieving booking details via SP", ex);
                detailsArea.setText("Error retrieving details: " + ex.getMessage());
            }
        });
        return panel;
    }
    // Functionality 5: Retrieve Customers Traveling on a Specific Flight
    private JPanel createFlightCustomersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<FlightDisplayWrapper> flightComboBox = new JComboBox<>(); // To select an operational flight
        JButton listCustomersButton = new JButton("List Customers");
        JButton refreshFlightsButton = new JButton("Refresh Flights List");

        inputPanel.add(new JLabel("Select Operational Flight:"));
        inputPanel.add(flightComboBox);
        inputPanel.add(refreshFlightsButton);
        inputPanel.add(listCustomersButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        // Populate the flight combo box
        populateOperationalFlightComboBox(flightComboBox);

        // --- Results Table (Center) ---
        String[] columnNames = {"Customer ID", "First Name", "Last Name", "Email", "Phone Number"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // --- Action Listeners ---
        refreshFlightsButton.addActionListener(e -> {
            populateOperationalFlightComboBox(flightComboBox);
            oa("Operational flights list refreshed.");
        });

        listCustomersButton.addActionListener(e -> {
            FlightDisplayWrapper selectedFlightWrapper = (FlightDisplayWrapper) flightComboBox.getSelectedItem();

            if (selectedFlightWrapper == null || selectedFlightWrapper.getFlight() == null || selectedFlightWrapper.getFlight().getFlightId() == -1) {
                JO("Please select a valid operational flight.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int flightId = selectedFlightWrapper.getFlight().getFlightId();
            tableModel.setRowCount(0); 
            oa("Listing customers for Flight ID: " + flightId + "...");

            try {
                List<Customer> customers = customerDAO.getCustomersByFlight(flightId);

                if (customers.isEmpty()) {
                    oa("No customers found for the selected flight.");
                } else {
                    oa(customers.size() + " customer(s) found.");
                    for (Customer cust : customers) {
                        tableModel.addRow(new Object[]{
                                cust.getCustomerId(),
                                cust.getFirstName(),
                                cust.getLastName(),
                                cust.getEmail(),
                                cust.getPhoneNumber()
                        });
                    }
                }
            } catch (SQLException ex) {
                hs("listing customers for flight", ex);
            }
        });
        return panel;
    }
     // Functionality 6: Retrieve List of Airlines Operating in a Specific City
     private JPanel createAirlinesInCityPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // --- Input Panel (North) ---
        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField cityNameField = new JTextField(20);
        JButton listAirlinesButton = new JButton("List Airlines in City");

        inputPanel.add(new JLabel("Enter City Name:"));
        inputPanel.add(cityNameField);
        inputPanel.add(listAirlinesButton);
        panel.add(inputPanel, BorderLayout.NORTH);

        // --- Results Table (Center) ---
        String[] columnNames = {"Airline ID", "Airline Name"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        panel.add(new JScrollPane(resultsTable), BorderLayout.CENTER);

        // --- Action Listener ---
        listAirlinesButton.addActionListener(e -> {
            String cityName = cityNameField.getText().trim();
            if (cityName.isEmpty()) {
                JO("City Name cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.setRowCount(0);
            oa("Listing airlines operating in/from city: " + cityName + "...");

            try {
                List<Airline> airlines = airlineDAO.getAirlinesInCity(cityName);

                if (airlines.isEmpty()) {
                    oa("No airlines found operating in/from city: " + cityName);
                } else {
                    oa(airlines.size() + " airline(s) found.");
                    for (Airline airline : airlines) {
                        tableModel.addRow(new Object[]{
                                airline.getAirlineId(),
                                airline.getAirlineName()
                        });
                    }
                }
            } catch (SQLException ex) {
                hs("listing airlines in city", ex);
            }
        });
        return panel;
    }
     // Functionality 7: Update Booking Information (State)
     private JPanel createUpdateBookingPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // --- Search Panel (North) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField bookingNumberSearchField = new JTextField(10);
        JButton searchBookingButton = new JButton("Search Booking");
        searchPanel.add(new JLabel("Enter Booking Number:"));
        searchPanel.add(bookingNumberSearchField);
        searchPanel.add(searchBookingButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // --- Update Form Panel (Center) - Initially hidden ---
        JPanel updateFormPanel = new JPanel(new GridBagLayout());
        updateFormPanel.setBorder(BorderFactory.createTitledBorder("Booking Details for Update"));
        updateFormPanel.setVisible(false); // Hide until a booking is searched
        mainPanel.add(new JScrollPane(updateFormPanel), BorderLayout.CENTER);

        // Components for the update form (will be populated after search)
        final JLabel bookingNumberDisplayLabel = new JLabel(); // To display the booking number
        final JLabel customerIdDisplayLabel = new JLabel();
        final JLabel flightIdDisplayLabel = new JLabel();
        final JLabel currentGeneralFlightIdLabel = new JLabel(); // Display associated GFID
        final JLabel currentSeatsBookedLabel = new JLabel();     // Display seats info
        final JComboBox<String> newStateComboBox = new JComboBox<>(new String[]{"0 - Pending", "1 - Confirmed", "2 - Cancelled"});
        final JButton updateStateButton = new JButton("Update Booking State");
        final Booking[] currentBookingHolder = new Booking[1]; // To hold the fetched booking (final for lambda)

        searchBookingButton.addActionListener(e -> {
            String bookingNumStr = bookingNumberSearchField.getText().trim();
            if (bookingNumStr.isEmpty()) {
                JO("Booking Number cannot be empty.", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateFormPanel.setVisible(false); // Hide if empty search
                return;
            }
            int bookingNumber;
            try {
                bookingNumber = Integer.parseInt(bookingNumStr);
            } catch (NumberFormatException nfe) {
                JO("Booking Number must be a valid integer.", "Input Error", JOptionPane.ERROR_MESSAGE);
                updateFormPanel.setVisible(false);
                return;
            }

            try {
                Booking booking = bookingDAO.getBookingByNumber(bookingNumber);
                currentBookingHolder[0] = booking; // Store for update action

                if (booking != null) {
                    // Populate the update form
                    ((javax.swing.border.TitledBorder) updateFormPanel.getBorder()).setTitle("Update Booking #" + booking.getBookingNumber());
                    updateFormPanel.removeAll(); // Clear previous components

                    int y = 0;
                    updateFormPanel.add(new JLabel("Booking #:"), createLabelGBC(0, y));
                    bookingNumberDisplayLabel.setText(String.valueOf(booking.getBookingNumber()));
                    updateFormPanel.add(bookingNumberDisplayLabel, createGBC(1, y++));

                    updateFormPanel.add(new JLabel("Customer ID:"), createLabelGBC(0, y));
                    customerIdDisplayLabel.setText(booking.getCustomerId());
                    updateFormPanel.add(customerIdDisplayLabel, createGBC(1, y++));

                    updateFormPanel.add(new JLabel("Flight ID:"), createLabelGBC(0, y));
                    flightIdDisplayLabel.setText(String.valueOf(booking.getFlightId()));
                    updateFormPanel.add(flightIdDisplayLabel, createGBC(1, y++));
                    
                    updateFormPanel.add(new JLabel("GeneralFlight ID:"), createLabelGBC(0,y));
                    currentGeneralFlightIdLabel.setText(String.valueOf(booking.getGeneralFlightId()));
                    updateFormPanel.add(currentGeneralFlightIdLabel, createGBC(1,y++));

                    // Optionally display current seat info for the GeneralFlight
                    try {
                        GeneralFlight gf = generalFlightDAO.getGeneralFlightById(booking.getGeneralFlightId());
                        if (gf != null) {
                            currentSeatsBookedLabel.setText(gf.getSeatsBooked() + " / " + gf.getNumberSeats() + " seats");
                            updateFormPanel.add(new JLabel("Current Sched. Seats:"), createLabelGBC(0,y));
                            updateFormPanel.add(currentSeatsBookedLabel, createGBC(1,y++));
                        }
                    } catch (SQLException ignored) {}


                    updateFormPanel.add(new JLabel("New State:"), createLabelGBC(0, y));
                    newStateComboBox.setSelectedIndex(booking.getState()); // Set to current state
                    updateFormPanel.add(newStateComboBox, createGBC(1, y++));

                    updateFormPanel.add(updateStateButton, createGBC(0, y, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

                    updateFormPanel.setVisible(true);
                    mainPanel.revalidate(); // Refresh layout
                    mainPanel.repaint();
                    oa("Booking #" + bookingNumber + " loaded for update.");
                } else {
                    updateFormPanel.setVisible(false);
                    oa("No booking found with Booking Number: " + bookingNumber);
                    JO("No booking found with Booking Number: " + bookingNumber, "Not Found", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException ex) {
                hs("searching booking for update", ex);
                updateFormPanel.setVisible(false);
            }
        });

        updateStateButton.addActionListener(e -> {
            Booking bookingToUpdate = currentBookingHolder[0];
            if (bookingToUpdate == null) {
                JO("No booking has been searched and loaded for update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String selectedStateStr = (String) newStateComboBox.getSelectedItem();
            int newState = Integer.parseInt(selectedStateStr.substring(0, selectedStateStr.indexOf(" ")));

            try {
                // The database trigger 'trg_booking_update_seats' handles seat adjustments.
                if (bookingDAO.updateBookingState(bookingToUpdate.getBookingNumber(), newState)) {
                    oa("Booking #" + bookingToUpdate.getBookingNumber() + " state updated successfully.");
                    updateFormPanel.setVisible(false); // Hide form after update
                    bookingNumberSearchField.setText(""); // Clear search field
                    // Optionally re-fetch and display the GeneralFlight seat info if needed
                } else {
                    // This might happen if state was already the same, or trigger prevented update
                    oa("Booking #" + bookingToUpdate.getBookingNumber() + " state not changed (or update failed).");
                }
            } catch (SQLException ex) {
                 // Check for the specific trigger error (no seats on re-activation)
                if (ex.getErrorCode() == 1644 && ex.getMessage() != null &&
                    ex.getMessage().toLowerCase().contains("no available seats")) {
                    JO("Update Failed: " + ex.getMessage(), "Seat Availability Error", JOptionPane.ERROR_MESSAGE);
                    oa("Update Failed: " + ex.getMessage());
                } else {
                    hs("updating booking state", ex);
                }
            }
        });
        return mainPanel;
    }
     // Functionality 8: Update Operational Flight (Dates and/or State)
     private JPanel createUpdateFlightInstancePanel() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // --- Search Panel (North) ---
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JComboBox<FlightDisplayWrapper> flightSearchComboBox = new JComboBox<>(); // Use wrapper for better display
        JButton searchFlightButton = new JButton("Load Flight for Update");
        JButton refreshFlightsButton = new JButton("Refresh Flights List");
        searchPanel.add(new JLabel("Select Operational Flight:"));
        searchPanel.add(flightSearchComboBox);
        searchPanel.add(refreshFlightsButton);
        searchPanel.add(searchFlightButton);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        populateOperationalFlightComboBox(flightSearchComboBox); // Populate on init

        // --- Update Form Panel (Center) - Initially hidden ---
        JPanel updateFormPanel = new JPanel(new GridBagLayout());
        updateFormPanel.setBorder(BorderFactory.createTitledBorder("Flight Instance Details for Update"));
        updateFormPanel.setVisible(false);
        mainPanel.add(new JScrollPane(updateFormPanel), BorderLayout.CENTER);

        // Components for the update form
        final JLabel flightIdDisplayLabel = new JLabel();
        final JTextField newTakeOffDateField = new JTextField(10);
        final JTextField newArrivalDateField = new JTextField(10);
        final JComboBox<String> newStateComboBox = new JComboBox<>(new String[]{"0 - Scheduled", "1 - In Progress", "2 - Completed", "3 - Cancelled"});
        final JButton updateFlightButton = new JButton("Update Flight Details");
        final Flight[] currentFlightHolder = new Flight[1]; // To hold the fetched flight

        refreshFlightsButton.addActionListener(e -> populateOperationalFlightComboBox(flightSearchComboBox));

        searchFlightButton.addActionListener(e -> {
            FlightDisplayWrapper selectedFlightWrapper = (FlightDisplayWrapper) flightSearchComboBox.getSelectedItem();
            if (selectedFlightWrapper == null || selectedFlightWrapper.getFlight() == null || selectedFlightWrapper.getFlight().getFlightId() == -1) {
                JO("Please select a valid operational flight to update.", "Selection Error", JOptionPane.ERROR_MESSAGE);
                updateFormPanel.setVisible(false);
                return;
            }

            Flight flight = selectedFlightWrapper.getFlight(); // Get the actual Flight object
            currentFlightHolder[0] = flight;

            if (flight != null) {
                ((javax.swing.border.TitledBorder) updateFormPanel.getBorder()).setTitle("Update Flight Instance #" + flight.getFlightId());
                updateFormPanel.removeAll();

                int y = 0;
                updateFormPanel.add(new JLabel("Flight ID:"), createLabelGBC(0, y));
                flightIdDisplayLabel.setText(String.valueOf(flight.getFlightId()));
                updateFormPanel.add(flightIdDisplayLabel, createGBC(1, y++));

                updateFormPanel.add(new JLabel("New Takeoff Date (YYYY-MM-DD):"), createLabelGBC(0, y));
                newTakeOffDateField.setText(dateFormat.format(flight.getTakeOffDate()));
                updateFormPanel.add(newTakeOffDateField, createGBC(1, y++));

                updateFormPanel.add(new JLabel("New Arrival Date (YYYY-MM-DD):"), createLabelGBC(0, y));
                newArrivalDateField.setText(dateFormat.format(flight.getArrivalDate()));
                updateFormPanel.add(newArrivalDateField, createGBC(1, y++));

                updateFormPanel.add(new JLabel("New State:"), createLabelGBC(0, y));
                newStateComboBox.setSelectedIndex(flight.getState()); // Assumes states 0,1,2,3 map directly
                updateFormPanel.add(newStateComboBox, createGBC(1, y++));

                updateFormPanel.add(updateFlightButton, createGBC(0, y, 2, 0.0, GridBagConstraints.CENTER, null, GridBagConstraints.NONE));

                updateFormPanel.setVisible(true);
                mainPanel.revalidate();
                mainPanel.repaint();
                oa("Flight #" + flight.getFlightId() + " loaded for update.");
            } else {
                // Should not happen if ComboBox selection is valid
                updateFormPanel.setVisible(false);
                oa("Selected flight data could not be retrieved (should be impossible if selected from list).");
            }
        });

        updateFlightButton.addActionListener(e -> {
            Flight flightToUpdate = currentFlightHolder[0];
            if (flightToUpdate == null) {
                JO("No flight has been loaded for update.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Date newTakeOff = parseSqlDate(newTakeOffDateField.getText());
            Date newArrival = parseSqlDate(newArrivalDateField.getText());
            String selectedStateStr = (String) newStateComboBox.getSelectedItem();
            
            if (newTakeOff == null || newArrival == null || selectedStateStr == null) {
                JO("New Takeoff Date, Arrival Date, and State are required.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (newArrival.before(newTakeOff)) {
                JO("New Arrival Date cannot be before New Takeoff Date.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int newState = Integer.parseInt(selectedStateStr.substring(0, selectedStateStr.indexOf(" ")));

            // Update the flight object's properties
            flightToUpdate.setTakeOffDate(newTakeOff);
            flightToUpdate.setArrivalDate(newArrival);
            flightToUpdate.setState(newState);

            try {
                if (flightDAO.updateFlightDatesAndState(flightToUpdate)) {
                    oa("Flight Instance #" + flightToUpdate.getFlightId() + " updated successfully.");
                    updateFormPanel.setVisible(false); // Hide form
                    // Refresh the ComboBox as flight details (like state) might have changed
                    populateOperationalFlightComboBox(flightSearchComboBox); 
                    flightSearchComboBox.setSelectedIndex(0); // Reset selection
                } else {
                    oa("Flight Instance #" + flightToUpdate.getFlightId() + " not updated (no changes or update failed).");
                }
            } catch (SQLException ex) {
                hs("updating operational flight", ex);
            }
        });
        return mainPanel;
    }


    // --- Helper methods to populate JComboBoxes ---
    private void populateCityComboBox(JComboBox<City> comboBox) {
        try {
            List<City> items = cityDAO.getAllCities();
            comboBox.removeAllItems();
            if (items.isEmpty()) { comboBox.addItem(new City(-1, "-- No Cities --")); }
            else { for (City item : items) { comboBox.addItem(item); } }
        } catch (SQLException e) { hs("load Cities for ComboBox", e); }
    }
    private void populateAirportComboBox(JComboBox<Airport> comboBox, String typePlaceholder) {
        try {
            List<Airport> items = airportDAO.getAllAirports();
            comboBox.removeAllItems();
            if (items.isEmpty()) { comboBox.addItem(new Airport(-1, "-- No " + typePlaceholder + " Airports --", -1)); }
            else { for (Airport item : items) { comboBox.addItem(item); } }
        } catch (SQLException e) { hs("load Airports for ComboBox", e); }
    }
    private void populateAirlineComboBox(JComboBox<Airline> comboBox) {
        try {
            List<Airline> items = airlineDAO.getAllAirlines();
            comboBox.removeAllItems();
            if (items.isEmpty()) { comboBox.addItem(new Airline(-1, "-- No Airlines --")); }
            else { for (Airline item : items) { comboBox.addItem(item); } }
        } catch (SQLException e) { hs("load Airlines for ComboBox", e); }
    }
    private void populateAircraftComboBox(JComboBox<Aircraft> comboBox) {
        try {
            List<Aircraft> items = aircraftDAO.getAllAircraft();
            comboBox.removeAllItems();
            comboBox.addItem(new Aircraft(-1, "-- Select Aircraft (Optional) --")); // Default option
            if (!items.isEmpty()) { for (Aircraft item : items) { comboBox.addItem(item); } }
        } catch (SQLException e) { hs("load Aircraft for ComboBox", e); }
    }
    private void populateCustomerComboBox(JComboBox<Customer> comboBox) {
        try {
            List<Customer> items = customerDAO.getAllCustomers();
            comboBox.removeAllItems();
            if (items.isEmpty()) { comboBox.addItem(new Customer("-1", "-- No", "Customers --", "", "", "")); }
            else { for (Customer item : items) { comboBox.addItem(item); } }
        } catch (SQLException e) { hs("load Customers for ComboBox", e); }
    }

    // Wrapper class for GeneralFlight JComboBox display
    private static class GeneralFlightDisplayWrapper {
        GeneralFlight generalFlight; String displayString;
        public GeneralFlightDisplayWrapper(GeneralFlight gf, String display) { this.generalFlight = gf; this.displayString = display; }
        public GeneralFlight getGeneralFlight() { return generalFlight; }
        @Override public String toString() { return displayString; }
    }
    private void populateGeneralFlightComboBox(JComboBox<GeneralFlightDisplayWrapper> comboBox) {
        try {
            List<GeneralFlight> flights = generalFlightDAO.getAllGeneralFlights();
            comboBox.removeAllItems();
            if (flights.isEmpty()) {
                comboBox.addItem(new GeneralFlightDisplayWrapper(new GeneralFlight() {{ setGeneralFlightId(-1); }}, "-- No Schedules --"));
            } else {
                for (GeneralFlight gf : flights) {
                    // Attempt to get names for display, fallback to IDs
                    String depApName = "ID:"+gf.getDepAirportId(), arrApName = "ID:"+gf.getArrAirportId(), airlineName = "ID:"+gf.getAirlineId();
                    try { Airport dep = airportDAO.getAirportById(gf.getDepAirportId()); if(dep!=null) depApName = dep.getAirportName(); } catch (SQLException ignored) {}
                    try { Airport arr = airportDAO.getAirportById(gf.getArrAirportId()); if(arr!=null) arrApName = arr.getAirportName(); } catch (SQLException ignored) {}
                    try { Airline al  = airlineDAO.getAirlineById(gf.getAirlineId());  if(al!=null)  airlineName = al.getAirlineName(); } catch (SQLException ignored) {}

                    String display = String.format("GFID:%d (%s -> %s) by %s on %s @%s [%d/%d seats]",
                            gf.getGeneralFlightId(), depApName, arrApName, airlineName,
                            dateFormat.format(gf.getfDay()), timeFormatShort.format(gf.getTakeoffTime()),
                            (gf.getNumberSeats() - gf.getSeatsBooked()), gf.getNumberSeats());
                    comboBox.addItem(new GeneralFlightDisplayWrapper(gf, display));
                }
            }
        } catch (SQLException e) { hs("load GeneralFlights for ComboBox", e); }
    }

    // Wrapper class for Operational Flight JComboBox display
    private static class FlightDisplayWrapper {
        Flight flight; String displayString;
        public FlightDisplayWrapper(Flight f, String display) { this.flight = f; this.displayString = display; }
        public Flight getFlight() { return flight; }
        @Override public String toString() { return displayString; }
    }
    private void populateOperationalFlightComboBox(JComboBox<FlightDisplayWrapper> comboBox) {
        try {
            List<Flight> flights = flightDAO.getAllFlights(); // Get all flights
            comboBox.removeAllItems();
            boolean addedBookable = false;
            if (flights.isEmpty()) {
                 comboBox.addItem(new FlightDisplayWrapper(new Flight() {{ setFlightId(-1); }}, "-- No Operational Flights --"));
            } else {
                for (Flight f : flights) {
                    // Only show flights that are Scheduled or InProgress AND have available seats
                    if (f.getState() == 0 || f.getState() == 1) {
                        GeneralFlight gf = null;
                        try { gf = generalFlightDAO.getGeneralFlightById(f.getGeneralFlightId()); } catch (SQLException ignored) {}

                        if (gf != null && (gf.getNumberSeats() - gf.getSeatsBooked() > 0)) {
                            String depApName = "GFID:"+f.getGeneralFlightId(), arrApName = "";
                             try { Airport dep = airportDAO.getAirportById(gf.getDepAirportId()); if(dep!=null) depApName = dep.getAirportName(); } catch (SQLException ignored) {}
                             try { Airport arr = airportDAO.getAirportById(gf.getArrAirportId()); if(arr!=null) arrApName = arr.getAirportName(); } catch (SQLException ignored) {}

                            String display = String.format("FID:%d (%s -> %s) on %s [%s] Seats Left:%d",
                                    f.getFlightId(), depApName, arrApName, dateFormat.format(f.getTakeOffDate()),
                                    f.getStateString(), (gf.getNumberSeats() - gf.getSeatsBooked()));
                            comboBox.addItem(new FlightDisplayWrapper(f, display));
                            addedBookable = true;
                        }
                    }
                }
                 if (!addedBookable && !flights.isEmpty()) { // if flights exist but none are bookable
                    comboBox.addItem(new FlightDisplayWrapper(new Flight() {{ setFlightId(-1); }}, "-- No Bookable Flights Available --"));
                } else if (flights.isEmpty()){ // Should be caught by first if, but for safety
                    comboBox.addItem(new FlightDisplayWrapper(new Flight() {{ setFlightId(-1); }}, "-- No Operational Flights --"));
                }
            }
        } catch (SQLException e) { hs("load OperationalFlights for ComboBox", e); }
    }


    // --- Helper methods for date/time parsing and UI updates ---
    private Date parseSqlDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) return null;
        try {
            return new Date(dateFormat.parse(dateStr.trim()).getTime());
        } catch (ParseException e) {
            JO("Invalid date '" + dateStr + "'. Use YYYY-MM-DD.", "Date Format Error", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }
    private Time parseSqlTime(String timeStr) {
        if (timeStr == null || timeStr.trim().isEmpty()) return null;
        try {
            return new Time(timeFormatFull.parse(timeStr.trim()).getTime());
        } catch (ParseException e1) {
            try {
                return new Time(timeFormatShort.parse(timeStr.trim()).getTime());
            } catch (ParseException e2) {
                JO("Invalid time '" + timeStr + "'. Use HH:MM or HH:MM:SS.", "Time Format Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
        }
    }
    private void cf(JTextComponent... components) { // Clear Fields
        for (JTextComponent component : components) { if (component != null) component.setText(""); }
    }
    private void hs(String action, SQLException ex) { // Handle SQLException
        String msg = "DB Error during '" + action + "': " + ex.getMessage();
        oa(msg); // Output to JTextArea
        JO(msg + "\n(SQLState:" + ex.getSQLState() + ", ErrorCode:" + ex.getErrorCode() + ")", "Database Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace(); // Print stack trace to console for detailed debugging
    }
    private void oa(String message) { // Output Area append
        SwingUtilities.invokeLater(() -> outputArea.append(message + "\n"));
    }
    private void JO(String message, String title, int messageType) { // JOptionPane
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, message, title, messageType));
    }
} // <<<----- MAKE ABSOLUTELY SURE THIS IS THE FINAL CLOSING BRACE OF THE CLASS