package nothing;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class UniversityApp {

	//Represents connection to database
    private Connection conn;
    
    //Private fields for the department
    private JTextField deptIdField, deptNameField, deptCollegeField, deptOfficeNumField, deptPhoneField,officePhoneField, ageField, officeNumField;
    
    private JTextField lNameField, fNameField, midInitField,sexField, ssnField, nNumField, cityField, stateField, streetField, zipField, permCityField, 
    permStateField, permStreetField, permZipField, degreeField, studClassField, 
    curPhoneField, curAddressField, minorField, majorField, permPhoneField, studentDegreeField;
    
    //JTables to represent current instances of departments and students.
    private JTable departmentTable, studentTable, instructorTable, courseTable;
    
   
   

    //Main method that start the University App
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
        	
        	//Create an instance of the app
            UniversityApp app = new UniversityApp();
            //Connect said instance to the database
            app.connectToDatabase();
            //Make GUI Visible to user
            app.createAndShowGUI();
            
        });
    
    
    }//End of Main method

    //database connection handler -----------------------------------------------------------------------------------------------
    private void connectToDatabase() {
        try {
	            Class.forName("oracle.jdbc.driver.OracleDriver");
	
	            String url = "jdbc:oracle:thin:@cisvm-oracle.unfcsd.unf.edu:1521:orcl";
	            String username = "G03";
	            String password = "kh8nDbN7";
	
	            conn = DriverManager.getConnection(url, username, password);

	            if (conn.isValid(10)) {
	                System.out.println("Database connected successfully.");
	            }
            
        	} catch (Exception e) {
        		JOptionPane.showMessageDialog(null, "Database connection failed: " + e.getMessage());
        	}//End of try catch
        
    }//End of connectToDatabase

    
    private void createAndShowGUI() {
    	
    	//Set name of window, size, change the ability to close it based off whether they hit the x 
        JFrame frame = new JFrame("University Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        //Create overarching page for all tabs
        JTabbedPane tabbedPane = new JTabbedPane();

        // Department Tab
        JPanel departmentPanel = createDepartmentPanel();
        tabbedPane.addTab("Departments", departmentPanel);

        // Student Tab with scroll bar 
        JPanel studentPanel = studentPanel();
        tabbedPane.addTab("Students", new JScrollPane(studentPanel));

        // Instructor Tab
        JPanel instructorPanel = new JPanel();
        tabbedPane.addTab("Instructors", instructorPanel);
        
        
        //Course Tab
        JPanel coursePanel = coursePanel();  
        tabbedPane.addTab("Courses",  new JScrollPane( coursePanel) );
        
        //Section Tab
        JPanel sectionPanel = sectionPanel();
        tabbedPane.addTab("Sections", sectionPanel);
        
        //Assign Tab
        JPanel assignPanel = assignStudentPanel();
        tabbedPane.addTab("Assign Student", assignPanel);
        
        //Search
        JPanel searchPanel = searchPanel();
        tabbedPane.addTab("Search", searchPanel);

        //Place tab page on frame and make it visible
        frame.add(tabbedPane);
        frame.setVisible(true);
    }//End of createAndShowGUI
    
//--------------------------------Student Tab and Functionality----------------------------------  
    //Method that will create and return the student panel
	private JPanel studentPanel() {
		//Create the panel with a borderlayout
    	JPanel panel = new JPanel(new BorderLayout());

    	//Create inner panel to store all labels and inputs
    	JPanel inputPanel = new JPanel(new GridLayout(0, 4, 6, 6));
    	//Name
        fNameField = new JTextField();
        lNameField = new JTextField();
        midInitField = new JTextField();
        //Primary Keys and Sex
        sexField = new JTextField();
        ssnField = new JTextField();
        nNumField = new JTextField();
        //Current Address
        cityField = new JTextField();
        stateField = new JTextField();
        streetField = new JTextField();
        zipField = new JTextField();
        
        //Permanent Address
        permCityField = new JTextField();
        permStateField = new JTextField();
        permStreetField = new JTextField();
        permZipField = new JTextField();
        
        //Major + Minor
        minorField = new JTextField();
        majorField = new JTextField();
        //student attributes *Idea might just make another table for address*
        curPhoneField = new JTextField();
        //curAddressField = new JTextField();
        permPhoneField = new JTextField();
        //Classification of type of degree and year
        degreeField = new JTextField();
        studClassField = new JTextField();
        
        //Spinner component for dates being created
        SpinnerDateModel dateModel = new SpinnerDateModel();
        //Adding arrows to said spinner
        JSpinner birthDateSpinner = new JSpinner(dateModel);
        //Set the format to year month day
        birthDateSpinner.setEditor(new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd"));
        
        //Submit entered info button
        JButton addButton = new JButton("Add Student");
        
        //Add name labels and text fields to inner panel
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(fNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lNameField);
        inputPanel.add(new JLabel("Middle Initial:"));
        inputPanel.add(midInitField);
        
        //Add labels for primary keys and sex alongside input components to inner panel
        inputPanel.add(new JLabel("Sex:"));
        inputPanel.add(sexField);
        inputPanel.add(new JLabel("Social Security Number:"));
        inputPanel.add(ssnField);
        inputPanel.add(new JLabel("Student N#:"));
        inputPanel.add(nNumField);
        inputPanel.add(new JLabel("Birthdate:"));
        inputPanel.add(birthDateSpinner);
        
        //Add labels for current address alongside input
        inputPanel.add(new JLabel("Current Address"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel("Street:"));
        inputPanel.add(streetField);
        inputPanel.add(new JLabel("City:"));
        inputPanel.add(cityField);
        inputPanel.add(new JLabel("State:"));
        inputPanel.add(stateField);
        inputPanel.add(new JLabel("Zip:"));
        inputPanel.add(zipField);
       
        
        //Add labels for permanent address and input
        inputPanel.add(new JLabel("Permenant Address"));
        inputPanel.add(new JLabel(""));
        inputPanel.add(new JLabel("Street:"));
        inputPanel.add(permStreetField);
        inputPanel.add(new JLabel("City:"));
        inputPanel.add(permCityField);
        inputPanel.add(new JLabel("State:"));
        inputPanel.add(permStateField);
        inputPanel.add(new JLabel("Zip:"));
        inputPanel.add(permZipField);
        
        //Current Phone Number and Permanent Phone Number
        inputPanel.add(new JLabel("Current Phone:"));
        inputPanel.add(curPhoneField);
        inputPanel.add(new JLabel("Permanent Phone:"));
        inputPanel.add(permPhoneField);
        
        //Add labels for degree type, classification of which year in studies, 
        inputPanel.add(new JLabel("Degree:"));
        inputPanel.add(degreeField);
        inputPanel.add(new JLabel("Major Program:"));
        inputPanel.add(majorField);
        inputPanel.add(new JLabel("Minor Program:"));
        inputPanel.add(minorField);
        inputPanel.add(new JLabel("Class:"));
        inputPanel.add(studClassField);
        
        //Extra empty label added to get button push to the right
        inputPanel.add(new JLabel()); 
        inputPanel.add(addButton);
        
        //Center all the user controls the top of of the page
        panel.add(inputPanel, BorderLayout.NORTH);      
        
        //panel.add(new JScrollPane(), BorderLayout.WEST);
        
        
        //Create a table to represent all current student info give it scroll bar and put in center of page
        studentTable = new JTable();
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        
        //Give button functionality
        addButton.addActionListener(e -> {
            //Name           
            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String midInit = midInitField.getText().trim();
            
            //Primary keys, sex, and birthdate
            String nNum = nNumField.getText().trim();
            String sex = sexField.getText().trim();
            String ssn = ssnField.getText().trim();
            Date birthDate = (Date) birthDateSpinner.getValue();
              
            //Current Address
            String street = streetField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String zip = zipField.getText().trim();
            
            //Permanent Address
            String permStreet = permStreetField.getText().trim();
            String permCity = permCityField.getText().trim();
            String permState = permStateField.getText().trim();
            String permZip = permZipField.getText().trim();
            
            //Current phone number and permanent phone number
            String currPhone = curPhoneField.getText().trim();
            String permPhone = permPhoneField.getText().trim();
            
            //Classification of degree type, major, minor, year in university
            String major = majorField.getText().trim();
            String minor = minorField.getText().trim();
            String studClass = studClassField.getText().trim();
            String degree_program = degreeField.getText().trim();
            
            //If N number or first name is empty don't accept input
            if (nNum.isEmpty() || fName.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Student N# and First Name are required.");
                return;
            }
            
            //Attempt to add info into corresponding tables
            try {
                addStudents(ssn, nNum, fName, midInit, lName, birthDate, sex, permZip, permCity, permState, permStreet, zip, city, state, street, permPhone,
                            currPhone, degree_program, studClass);
                
                addMajorIn(nNum, major);
                
                if (!minor.isEmpty()) {
                    addMinorIn(nNum, minor);
                }
                
                //Loads current data from student table to be display
                loadStudents();
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Student failed: " + ex.getMessage());
            }//End of Try catch

        
        });//End of addActionListener for Button
         
      //Loads current data from student table to be display in case the attempt to insert values failed
        loadStudents();
        
        return panel;
    }//End of StudentPanel

	//DisplayStudent info
	private void loadStudents() {
    	//Statement needed to display content of student
    	String sql = """
    	        SELECT s.N#, s.ssn, s.First_Name, s.Middle_Initial, s.Last_Name, s.BirthDate, s.sex,s.Permanent_Zip, s.Permanent_City, 
    s.Permanent_State, s.Permanent_Street, s.Current_Zip, s.Current_City, s.Current_State, s.Current_Street, s.Permanent_Phone, s.Current_Phone, 
    s.Degree_Program, s.Class, m.deptcode AS Major_Code, mi.deptcode AS Minor_Code
FROM 
    student s
LEFT JOIN 
    majorin m ON s.N# = m.N#
LEFT JOIN 
    minorin mi ON s.N# = mi.N#
    	        """;
    	try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

               ResultSetMetaData meta = rs.getMetaData();
               int columns = meta.getColumnCount();

               Vector<String> columnNames = new Vector<>();
               for (int i = 1; i <= columns; i++) {
                   columnNames.add(meta.getColumnName(i));
               }

               Vector<Vector<Object>> data = new Vector<>();
               while (rs.next()) {
                   Vector<Object> row = new Vector<>();
                   for (int i = 1; i <= columns; i++) {
                       row.add(rs.getObject(i));
                   }
                   data.add(row);
               }

               studentTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
           } catch (SQLException e) {
               JOptionPane.showMessageDialog(null, "Error loading students: " + e.getMessage());
           }
    	
    }//End of loadStudents
	
	//Add Student
    private void addStudents(String ssn, String nNum, String fName, String midInit, String lName, Date birthDate, String sex,
    	    String permZip, String permCity, String permState, String permStreet, String currZip, String currCity, String currState,
    	    String currStreet, String permPhone, String currPhone, String degree_program, String studClass) {

    	    String sql = "INSERT INTO student (SSN, N#, First_Name, Middle_Initial, Last_Name, Birthdate, sex, Permanent_Zip"
    	            + ", Permanent_city, permanent_state, permanent_street, current_zip, current_city, current_state,"
    	            + "current_street, permanent_phone, current_phone, degree_program, class) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    	    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    	        stmt.setString(1, ssn);
    	        stmt.setString(2, nNum);
    	        stmt.setString(3, fName);
    	        stmt.setString(4, midInit);
    	        stmt.setString(5, lName);
    	        stmt.setDate(6, new java.sql.Date(birthDate.getTime()));
    	        stmt.setString(7, sex);
    	        stmt.setString(8, permZip);
    	        stmt.setString(9, permCity);
    	        stmt.setString(10, permState);
    	        stmt.setString(11, permStreet);
    	        stmt.setString(12, currZip);
    	        stmt.setString(13, currCity);
    	        stmt.setString(14, currState);
    	        stmt.setString(15, currStreet);
    	        stmt.setString(16, permPhone);
    	        stmt.setString(17, currPhone);  // Add this line to set the 17th parameter
    	        stmt.setString(18, degree_program);
    	        stmt.setString(19, studClass);

    	        stmt.executeUpdate();
    	        JOptionPane.showMessageDialog(null, "Student added!");
    	    } catch (SQLException e) {
    	        JOptionPane.showMessageDialog(null, "Error adding student: " + e.getMessage());
    	    }
    }//End of addStudents
    
    //Add Major
    private void addMajorIn(String id, String depId) {
    	 String sql = "INSERT INTO majorIn (N#, deptCode) VALUES (?, ?)";
         try (PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setString(1, id);
             stmt.setString(2, depId);
             stmt.executeUpdate();
             JOptionPane.showMessageDialog(null, "Student major added!");
         } catch (SQLException e) {
             JOptionPane.showMessageDialog(null, "Error adding student: " + e.getMessage());
         }
    }
    
    //Add Minor
    private void addMinorIn(String id, String depId) {
   	 String sql = "INSERT INTO minorIn (N#, deptCode) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, depId);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Student minor added!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding student: " + e.getMessage());
        }
    }
    
  //--------------------------------Department Tab and Functionality----------------------------------  	
	
    //Method that will create and return the department panel
    private JPanel createDepartmentPanel() {
    	//Overarching panel
        JPanel panel = new JPanel(new BorderLayout());
        
        //Inner panel that stores all user input
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        deptIdField = new JTextField();
        deptNameField = new JTextField();
        deptCollegeField = new JTextField();
        deptOfficeNumField = new JTextField();
        deptPhoneField = new JTextField();
        JButton addButton = new JButton("Add Department");

        //Add all text fields with corresponding labels to the inner panel
        inputPanel.add(new JLabel("Department Name:"));
        inputPanel.add(deptNameField);
        inputPanel.add(new JLabel("Department ID:"));
        inputPanel.add(deptIdField);
        inputPanel.add(new JLabel("Department College:"));
        inputPanel.add(deptCollegeField);
        inputPanel.add(new JLabel("Department Office #:"));
        inputPanel.add(deptOfficeNumField);
        inputPanel.add(new JLabel("Department Phone:"));
        inputPanel.add(deptPhoneField);
        inputPanel.add(new JLabel()); 
        inputPanel.add(addButton);

        //Place the inner panel at the top of the page
        panel.add(inputPanel, BorderLayout.NORTH);

        //Place Table that represent current data of department in the center
        departmentTable = new JTable();
        panel.add(new JScrollPane(departmentTable), BorderLayout.CENTER);

        //Add functionality to add department button
        addButton.addActionListener(e -> {
            String id = deptIdField.getText().trim();
            String name = deptNameField.getText().trim();
            String college = deptCollegeField.getText().trim();
            String officeNum = deptOfficeNumField.getText().trim();
            String phone = deptPhoneField.getText().trim();

            //If ID or name is empty refuse input elsewise proceed
            if (!id.isEmpty() && !name.isEmpty()) {
                addDepartment(id, name, college, officeNum, phone);
                loadDepartments();
                deptIdField.setText("");
                deptNameField.setText("");
                deptCollegeField.setText("");
                deptOfficeNumField.setText("");
                deptPhoneField.setText("");
            } else {
                JOptionPane.showMessageDialog(panel, "ID and Name are required.");
            }
        });//End of AddActionLisenter

        //Call updated data from department incase the insert fail
        loadDepartments();

        return panel;
    }//End of createDepartmentPanel

    //Insert values into the Department table
    private void addDepartment(String id, String name, String college, String officeNum, String phone) {
    	//String necessry for insert statement
        String sql = "INSERT INTO department (Department_Name, Department_Code, College, Office#, Office_Phone) VALUES (?, ?, ?, ?, ?)";
        //Create statement that you wish to exexute
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        	//Change the missing values to the strings passed to the method
            stmt.setString(1, name);
            stmt.setString(2, id);
            stmt.setString(3, college);
            stmt.setString(4, officeNum);
            stmt.setString(5, phone);
            
            //Execute said statement
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Department added!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding department: " + e.getMessage());
        }//End of try catch statement
        
    }//End of addDepartment method

  
    //viewing departments
    private void loadDepartments() {
    	//Statement needed to display content of department
        String sql = "SELECT * FROM department";
        
        //Setup statement to be executed
        try (Statement stmt = conn.createStatement();
        		//Execute the statement and store results
             ResultSet rs = stmt.executeQuery(sql)) {

        	//Store meta data in variable 
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();

            //Store the name of the headers  within the Vector 
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columns; i++) {
                columnNames.add(meta.getColumnName(i));
            }//End of for loop

            //Store the data of the results within the Vector
            Vector<Vector<Object>> data = new Vector<>();
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columns; i++) {
                    row.add(rs.getObject(i));
                }//End of for loop that store the specific values of that row inside the Vector named row
                
                //Add those Vectors inside of the Vector named data that store all the data acquired from the ResultSet
                data.add(row);
            }//End of while statement that loops as long as another row exist

            //Pass the values to the table to display
            departmentTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading departments: " + e.getMessage());
        }
    }
    
  
    //----------------------------------------Instructor Tab and Functionality------------------------
	
    private JPanel instrutorPanel() {
    	JPanel iPanel = new JPanel();
    	
    	
    	return iPanel;
    }
    
    
    private void addInstructor(String ssn, String nNum, String fName, String lName, int age, String officeNumber, String zip, String city, String state, String street, String dep, String phone) {
	        
	    	String sql = "INSERT INTO INSTRUCTOR (SSN, N#, First_Name, Last_Name, Age, office#, zip, city, state, street, associated_dept, office_phone) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	    	try (PreparedStatement stmt = conn.prepareStatement(sql)) {
	            stmt.setString(1, ssn);
	            stmt.setString(2, nNum);
	            stmt.setString(3, fName);
	            stmt.setString(4, lName);
	            stmt.setInt(5, age);
	            stmt.setString(6, officeNumber); 
	            stmt.setString(7, zip);
	            stmt.setString(8, city);
	            stmt.setString(9, state);
	            stmt.setString(10, street);
	            stmt.setString(11, dep);
	            stmt.setString(12, phone); 
	
	            stmt.executeUpdate();
	            JOptionPane.showMessageDialog(null, "Instructor added!");
	        } catch (SQLException e) {
	            JOptionPane.showMessageDialog(null, "Error adding instructor: " + e.getMessage());
	        }
	    	
	}
    

    
    //--------------------------------------Course Tab and Functionality----------------------------
    private JPanel coursePanel() {
    	// Create the main panel with GridBagLayout for flexible component arrangement
    	JPanel cPanel = new JPanel(new GridBagLayout());
    	GridBagConstraints courseConstraints = new GridBagConstraints();
    	
    	// ---------- Input Labels and Fields ----------
    	JLabel courseNumber = new JLabel("Course Number:");
    	JTextField courseNumberInput = new JTextField(10);
    	
    	
    	JLabel courseName = new JLabel("Course Name:");
    	JTextField courseNameInput = new JTextField(20);
    	
    	JLabel courseDeparment = new JLabel("Course Department:");
    	JTextField courseDepartmentInput = new JTextField(20);
    	
    	JLabel courseDescription = new JLabel("Course Description:");
    	JTextField courseDescriptionInput = new JTextField(30);
    	
    	JLabel courseHour = new JLabel("Course Hour:");
    	JTextField courseHourInput = new JTextField(20);
    	
    	JButton submitCourseInfo = new JButton("Submit Course");
    	
    	//Display current data of courses
    	JTable displayInfo = new JTable();
    	loadCourseInfo(displayInfo);
    	JScrollPane scrollPane = new JScrollPane(displayInfo);
    	
    
    	
    	courseConstraints.weightx = 2.0;
    	courseConstraints.weighty = 0.0;  
    	courseConstraints.fill = GridBagConstraints.HORIZONTAL;
    	courseConstraints.anchor = GridBagConstraints.FIRST_LINE_START;
    	
    	courseConstraints.ipady = 7;
    	
    	
    	//------------Row 1---------
    	courseConstraints.gridx = 0;
    	courseConstraints.gridy = 0;
    	
    	
    	
    	cPanel.add(courseNumber, courseConstraints);
    	courseConstraints.gridx = 1;
    	cPanel.add(courseNumberInput, courseConstraints);
    	
    	//------------Row 2---------
    	courseConstraints.gridx = 0;
    	courseConstraints.gridy = 1;
    	
    	cPanel.add(courseName, courseConstraints);
    	courseConstraints.gridx = 1;
    	cPanel.add(courseNameInput, courseConstraints);
    	
    	//------------Row 3---------
    	courseConstraints.gridx = 0;
    	courseConstraints.gridy = 2;
    	
    	cPanel.add(courseDeparment, courseConstraints);
    	courseConstraints.gridx = 1;
    	cPanel.add(courseDepartmentInput, courseConstraints);
    	
    	//------------Row 4---------
    	courseConstraints.gridx = 0;
    	courseConstraints.gridy = 3;
    	
    	cPanel.add(courseDescription, courseConstraints);
    	courseConstraints.gridx = 1;
    	cPanel.add(courseDescriptionInput, courseConstraints);
    	
    	//------------Row 5---------
    	courseConstraints.gridx = 0;
    	courseConstraints.gridy = 4;
    	
    	cPanel.add(courseHour, courseConstraints);
    	courseConstraints.gridx = 1;
    	cPanel.add(courseHourInput, courseConstraints);
    	
    	//------------Row 6 Aka just the Button-----------
    	courseConstraints.fill = GridBagConstraints.HORIZONTAL;
    	courseConstraints.gridx = 0;
    	courseConstraints.gridy = 5;
    	courseConstraints.gridwidth = 2;
    	cPanel.add(submitCourseInfo, courseConstraints);
    	
    	//------------Row 7 Aka the display area---------
    	courseConstraints.gridy = 6;
    	courseConstraints.gridwidth = 2;
    	
    	courseConstraints.weighty = 1.0;
    	courseConstraints.gridheight = GridBagConstraints.REMAINDER;
    	//courseConstraints.fill = GridBagConstraints.BOTH;
    	
    	//Scroll Bar for display section incase it get that long
    	cPanel.add(new JScrollPane(displayInfo), courseConstraints);
    	
    	//Action listener for button to give it functionality
    	submitCourseInfo.addActionListener(new ActionListener() {
    		
	        @Override
	        public void actionPerformed(ActionEvent e) {
	            // Action to perform when the button is clicked
	            System.out.println("Course Button Clicked!");
	            
	            String courseNumber = courseNumberInput.getText().trim();
	            String courseName = courseNameInput.getText().trim();
	            String courseDepartment = courseDepartmentInput.getText().trim();
	            String courseDescription = courseDescriptionInput.getText().trim();
	            String courseHours = courseHourInput.getText().trim();
	            
	            if (!courseNumber.isEmpty() && !courseDepartment.isEmpty()) {
		            
		            addCourses(courseNumber, courseName, courseDepartment, courseDescription, courseHours);
		            
	            }else{
	                JOptionPane.showMessageDialog(new JDialog(),"ID and Name are required.");
	            }//End of if else statement
	        }//End of ActionPerformed
	        
	    });//End of AddActionListener for submit course info button
    	
    	//cPanel.setBorder(BorderFactory.createLineBorder(Color.RED)); // for debugging layout bounds


    	
    	return cPanel;
    }//End of coursePanel
    
    
    
    private void addCourses(String num, String name, String department, String desc, String hours) {
    	
    	
    	
    	String insert = "INSERT INTO COURSE(COURSE_NUMBER, NAME, DEPARTMENT_OFFERING, DESCRIPTION, SEMESTER_HOURS) VALUES (?,?,?,?,?)";
    	try {
			PreparedStatement statement = conn.prepareStatement(insert);
			statement.setString(1, num);
			statement.setString(2, name);
			statement.setString(3, department);
			statement.setString(4, desc);
			statement.setString(5, hours);
			
			 statement.executeUpdate();
	            JOptionPane.showMessageDialog(null, "Course added!");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	
    	
    	
    }
    
    private void loadCourseInfo(JTable display) {
    	
    	//COURSE_NUMBER	DEPARTMENT_OFFERING	NAME	DESCRIPTION	SEMESTER_HOURS	
    	String getCourse = "SELECT * FROM COURSE";
    	
    	try {
    		//Prepare statement foe execution
			PreparedStatement statement = conn.prepareStatement(getCourse);
			//Store result of executed statement
			ResultSet result = statement.executeQuery();
			
			//Store meta data of result such as number of columns 
			ResultSetMetaData meta = result.getMetaData();
			int columnCount = meta.getColumnCount();

			//Vector for Table Header
			Vector<String> columnHeaders = new Vector<String>();
			//Loop to retrive column names and store in Vector columnHeaders
			for(int a = 1; a <= columnCount; a++) {
				
				//System.out.println(meta.getColumnName(a));
				columnHeaders.add(meta.getColumnName(a));			
			}//End of for loop
			
			//Vector for Table data
			Vector<Vector<Object>> data = new Vector<>();
			while (result.next()) {
			    Vector<Object> row = new Vector<>();
			    for (int i = 1; i <= columnCount; i++) {
			        row.add(result.getObject(i));
			    }//End of for loop
			    data.add(row);
			}//End of while loop
			
			display.setModel(new DefaultTableModel(data, columnHeaders));
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//End of try catch
    	
    	
    }//End of load courseInfo

    
    //-------------------------------------Search Course Tab and Functionality--------------------------------
    
    
    private void searchCoursesByDept(String deptId) {
        String sql = """
            SELECT *
            FROM course
            WHERE department_offering = ?
            """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, deptId);
            try (ResultSet rs = stmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columns = meta.getColumnCount();

                Vector<String> columnNames = new Vector<>();
                for (int i = 1; i <= columns; i++) {
                    columnNames.add(meta.getColumnName(i));
                }

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= columns; i++) {
                        row.add(rs.getObject(i));
                    }
                    data.add(row);
                }

                courseTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading courses: " + e.getMessage());
        }
    }

    private void searchCourseByInstructor() {
        
    }
    
    
    //-------------------------------------Section Tab and Functionality---------------------------------------
    private JPanel sectionPanel() {
		JPanel sPanel = new JPanel(new GridBagLayout());
		
		GridBagConstraints sectionConstraints = new GridBagConstraints();
		
		sectionConstraints.ipady = 7;
		sectionConstraints.weightx = 2.0;
		
		//------------Row 1---------
		JLabel instructorN = new JLabel("Instructor");
		JTextField inputInstructor = new JTextField(20);
		
		sectionConstraints.gridx = 0;
		sectionConstraints.gridy = 0;
		sectionConstraints.fill = GridBagConstraints.HORIZONTAL;
		sPanel.add(instructorN, sectionConstraints);
		sectionConstraints.gridx = 1;
		sPanel.add(inputInstructor, sectionConstraints);
		
		//------------Row 2---------
		JLabel sectionID = new JLabel("Section");
		JTextField inputSectionID = new JTextField(20);
		
		sectionConstraints.gridx = 0;
		sectionConstraints.gridy = 1;
		sPanel.add(sectionID, sectionConstraints);
		sectionConstraints.gridx = 1;
		sPanel.add(inputSectionID, sectionConstraints);
		
		//------------Row 3---------
		
		JLabel courseNumber = new JLabel("CourseNumber");
		JTextField inputCourseNumber = new JTextField(20);
		
		sectionConstraints.gridx = 0;
		sectionConstraints.gridy = 2;
		sPanel.add(courseNumber, sectionConstraints);
		sectionConstraints.gridx = 1;
		sPanel.add(inputCourseNumber, sectionConstraints);
		
		
		//------------Row 4---------
		JLabel Semester = new JLabel("Semester");
		JTextField inputSemester = new JTextField(20);
		
		sectionConstraints.gridx = 0;
		sectionConstraints.gridy = 3;
		sPanel.add(Semester, sectionConstraints);
		sectionConstraints.gridx = 1;
		sPanel.add(inputSemester, sectionConstraints);
		
		//------------Row 5---------
		JLabel Year = new JLabel("Year");
		JTextField inputYear = new JTextField(20);
		
		sectionConstraints.gridx = 0;
		sectionConstraints.gridy = 4;
		sPanel.add(Year, sectionConstraints);
		sectionConstraints.gridx = 1;
		sPanel.add(inputYear, sectionConstraints);
		
		//------------Row 6 Aka just Buttons---------
		JButton submitInfo = new JButton("Add Section");
		
		sectionConstraints.gridx = 0;
		sectionConstraints.gridy = 5;
		
		sectionConstraints.gridwidth = 2;
		sPanel.add(submitInfo, sectionConstraints);
		
		
		
		
		//------------Row 7 Aka just display---------
		sectionConstraints.gridy = 7;
		sectionConstraints.gridwidth = 2;
		
		sectionConstraints.weightx = 2.0;
		sectionConstraints.weighty = 2.0;
		sectionConstraints.gridheight = GridBagConstraints.REMAINDER;
		sectionConstraints.fill = GridBagConstraints.BOTH;
		
		
		JTable displayInfo = new JTable();
		displaySections(displayInfo);
		sPanel.add(new JScrollPane(displayInfo), sectionConstraints);
		
		//---------------Add ActionListener to Button
		submitInfo.addActionListener(new ActionListener() {
				
			public void actionPerformed(ActionEvent e) { 
		
            // Action to perform when the button is clicked
            System.out.println("Submit section button Clicked!");
            
            
            String instructor = inputInstructor.getText();
            String sectionID = inputSectionID.getText();
            String courseNum = inputCourseNumber.getText();
            String semester = inputSemester.getText();
            String year = inputYear.getText();
            
            
            if( instructor.isEmpty() ) {
            	JOptionPane.showMessageDialog(new JDialog(),"Instructor N number  are required.");
            	
            }else if(sectionID.isEmpty()) {
            	JOptionPane.showMessageDialog(new JDialog(),"Section number are required.");
            	
            }else if(courseNum.isEmpty()) {
            	JOptionPane.showMessageDialog(new JDialog(),"Course number are required.");
            	
            }else if(semester.isEmpty()) {
            	JOptionPane.showMessageDialog(new JDialog(),"Semester are required.");
            	
            }else if(year.isEmpty()) {
            	JOptionPane.showMessageDialog(new JDialog(),"Year are required.");
            	
            }else {
            	addSections(instructor, sectionID, courseNum, semester, year);
            	displaySections(displayInfo);
            }
            
            
            displaySections(displayInfo);
            
            
			}//End of actionPerformed
		}//End function for actionlistener of submitInfo
		
		);//End of addActionListener
		
		
		
		
		
		return sPanel;
	}

    private void addSections(String instructN, String sectionID, String courseNum, String Semester, String  Year) {
        
    	String insert = "INSERT INTO SECTION(INSTRUCTOR_N#, SECTION#, COURSE_NUMBER, SEMESTER, YEAR)VALUES(?,?,?,?,?)";
    	
    	try {
			PreparedStatement statement = conn.prepareStatement(insert);
			statement.setString(1, instructN);
			statement.setString(2, sectionID);
			statement.setString(3, courseNum);
			statement.setString(4, Semester);
			statement.setString(5, Year);
			
			statement.execute();
			
			} catch (SQLException e) {
				
				// TODO Auto-generated catch block
			e.printStackTrace();
			}//End of try catch
    }//End of addSections

    private void displaySections(JTable display) {
    	String getSection = "SELECT * FROM SECTION";
    	
    	try {
    		//Prepare statement foe execution
			PreparedStatement statement = conn.prepareStatement(getSection);
			//Store result of executed statement
			ResultSet result = statement.executeQuery();
			
			//Store meta data of result such as number of columns 
			ResultSetMetaData meta = result.getMetaData();
			int columnCount = meta.getColumnCount();

			//Vector for Table Header
			Vector<String> columnHeaders = new Vector<String>();
			//Loop to retrive column names and store in Vector columnHeaders
			for(int a = 1; a <= columnCount; a++) {
				
				//System.out.println(meta.getColumnName(a));
				columnHeaders.add(meta.getColumnName(a));			
			}//End of for loop
			
			//Vector for Table data
			Vector<Vector<Object>> data = new Vector<>();
			while (result.next()) {
				
			    Vector<Object> row = new Vector<>();
			    for (int i = 1; i <= columnCount; i++) {
			        row.add(result.getObject(i));
			    }//End of for loop
			    data.add(row);
			    
			}//End of while loop
			
			display.setModel(new DefaultTableModel(data, columnHeaders));
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//End of try catch
    	
    	
   
    }//End of displaySections
    
  //-------------------------------------Assign Student  Tab and Functionality---------------------------------------
    
    private JPanel assignStudentPanel() {
    	JPanel aPanel = new JPanel(new GridBagLayout());
    	
    	GridBagConstraints aC = new GridBagConstraints();
    	
    	aC.fill = GridBagConstraints.HORIZONTAL;
    	aC.weightx = 2.0;
    	aC.ipady = 7;
    	
    	//----------------Row 1---------------
    	
    	aC.gridx = 0;
    	aC.gridy = 0;
    	JLabel sectionL = new JLabel("Section:");
    	aPanel.add(sectionL, aC);
    	aC.gridx = 1;
    	JTextField sectionID = new JTextField();
    	aPanel.add(sectionID, aC);
    	
    	//----------------Row 2---------------
    	
    	aC.gridx = 0;
    	aC.gridy = 1;
    	
    	JLabel courseL = new JLabel("Course Number:");
    	aPanel.add(courseL, aC);
    	aC.gridx = 1;
    	JTextField courseID = new JTextField();
    	aPanel.add(courseID, aC);
    	
    	//----------------Row 3---------------
    	
    	aC.gridx = 0;
    	aC.gridy = 2;
    	
    	JLabel semesterL = new JLabel("Semester:");
    	aPanel.add(semesterL, aC);
    	aC.gridx = 1;
    	JTextField semesterInput = new JTextField();
    	aPanel.add(semesterInput, aC);
    	
    	
    	//----------------Row 4---------------
    	
    	aC.gridx = 0;
    	aC.gridy = 3;
    	JLabel yearL = new JLabel("Year:");
    	aPanel.add(yearL, aC);
    	aC.gridx = 1;
    	JTextField yearInput = new JTextField();
    	aPanel.add(yearInput, aC);
    	
    	
    	//----------------Row 5---------------
    	
    	aC.gridx = 0;
    	aC.gridy = 4;
    	JLabel nNumberL = new JLabel("N Number:");
    	aPanel.add(nNumberL, aC);
    	aC.gridx = 1;
    	JTextField nNumber = new JTextField();
    	aPanel.add(nNumber, aC);
    	
    	
    	//----------------Row 6---------------
    	
    	aC.gridx = 0;
    	aC.gridy = 5;
    	JLabel gradeL = new JLabel("Grade:");
    	aPanel.add(gradeL, aC);
    	aC.gridx = 1;
    	JTextField gradeInput = new JTextField();
    	aPanel.add(gradeInput, aC);
    	

    	//-------------Assign Student Button + Change Grade Button-------------
    	aC.gridx = 0;
    	aC.gridy = 6;
    	aC.gridwidth = 2;
    	JButton assignStudent = new JButton("Assign Student to Section");
    	aPanel.add(assignStudent, aC);
    	
    	aC.gridy = 7;
    	aC.gridwidth = 2;
    	JButton changeGrade = new JButton("Change Student Grade");
    	aPanel.add(changeGrade, aC);
    	
    	//-------------Display Currently Enrolled table-------------
    	
    	aC.gridy = 8;
    	aC.weightx = 2;
    	aC.weighty = 2;
    	aC.gridheight = GridBagConstraints.REMAINDER;
    	aC.fill = GridBagConstraints.BOTH;
    	JTable display = new JTable();
    	
    	displayEnrolled(display);
    	
    	aPanel.add(new JScrollPane(display), aC);
    	
    	
		assignStudent.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) { 
				System.out.println("Assign Student Button Pressed");
				
				String section = sectionID.getText();
				String courseNum = courseID.getText();
				String semester = semesterInput.getText();
				String year = yearInput.getText();
				String nNum = nNumber.getText();
				String grade = gradeInput.getText();
				
				if( section.isEmpty() ) {
					
					JOptionPane.showMessageDialog(new JDialog(),"Section Number is required.");
				}else if( courseNum.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"Course Number is required.");
				}else if( semester.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"Semester is required.");
				}else if( year.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"Year is required.");
				}else if( nNum.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"N Numbers are required.");
				}else if( !grade.isEmpty() ){
					addStudentToSection(section,courseNum, semester, year,grade, nNum);
					displayEnrolled(display);
					
				}else{
					addStudentToSection(section,courseNum, semester, year, nNum);
					displayEnrolled(display);
					
				}//End of if else statement
				
			
			}//End of actionPerformed
						
		});//End of addActionListener for assignStudent;
		
		changeGrade.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) { 
				
				System.out.println("Change Grade Button Clicked");
				String section = sectionID.getText();
				String courseNum = courseID.getText();
				String semester = semesterInput.getText();
				String year = yearInput.getText();
				String nNum = nNumber.getText();
				String grade = gradeInput.getText();
				
				if( section.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"Section Number is required.");
				}else if( courseNum.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"Course Number is required.");
				}else if( semester.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"Semester is required.");
				}else if( year.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"Year is required.");
				}else if( nNum.isEmpty() ) {
					JOptionPane.showMessageDialog(new JDialog(),"N Numbers are required.");
				}else if( !grade.isEmpty() ){
					display.revalidate();
					display.repaint();

					assignGrade(section, courseNum, semester,year, grade, nNum);
					displayEnrolled(display);
				}else{
					JOptionPane.showMessageDialog(new JDialog(),"Grade is required.");
				
				}//End of if else
				
			}//End of actionPerformed
			
		});//End of addActionListener for changeGrade
    	
    	return aPanel;
    }//End of assignStudentPanel
    
    //AKA enroll student into course
    private void addStudentToSection(String sectionNum, String courseNum, String semester, String year, String NNumber) {
      
    	//String to insert values into ENROLLED_IN table
    	String insert = "INSERT INTO ENROLLED_IN (SECTION#, COURSE_NUMBER, SEMESTER, YEAR, N#) VALUES (?, ?, ?, ?, ?)";
    	
    	//Insert the values given then add to table
    	try {
			PreparedStatement state = conn.prepareStatement(insert);
			state.setString(1,sectionNum);
			state.setString(2,courseNum);
			state.setString(3,semester);
			state.setString(4,year);
			state.setString(5,NNumber);
			
			state.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JDialog(),"Ensure that Section#, Course#, N# all exist.");
			
		}//End of try catch
    	
    }//End of addStudentToSection
    
    private void addStudentToSection(String sectionNum, String courseNum, String semester, String year,String Grade, String NNumber) {
        
    	//String to insert values into ENROLLED_IN table
    	String insert = "INSERT INTO ENROLLED_IN (SECTION#, COURSE_NUMBER, SEMESTER, YEAR, GRADE, N#) VALUES (?, ?, ?, ?, ?, ?)";
    	
    	//Insert the values given then add to table
    	try {
			PreparedStatement state = conn.prepareStatement(insert);
			state.setString(1,sectionNum);
			state.setString(2,courseNum);
			state.setString(3,semester);
			state.setString(4,year);
			state.setString(5,Grade);
			state.setString(6,NNumber);
			
			state.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JDialog(),"Ensure that Section#, Course#, N# all exist.");
			
		}//End of try catch
    	
    }//End of addStudentToSection with Grade
    
    
    private void assignGrade(String sectionNum, String courseNum, String semester, String year,String Grade, String NNumber) {
        
    	String update = "UPDATE ENROLLED_IN SET GRADE = ? WHERE SECTION# = ? AND COURSE_NUMBER = ? AND SEMESTER = ? AND YEAR = ? AND N# = ?";
    	
    	try {
			PreparedStatement state = conn.prepareStatement(update);
			state.setString(1, Grade);
			state.setString(2, sectionNum);
			state.setString(3, courseNum);
			state.setString(4, semester);
			state.setString(5, year);
			state.setString(6, NNumber);
			
			
			state.executeUpdate();
			
			String commit = "COMMIT";
			state = conn.prepareStatement(commit);
			state.executeQuery();
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//End of try catch
    	
    	
    }//End of AssignGrade
    
    
    private void displayEnrolled(JTable display) {
    	System.out.println("Enroll display");
    	String getEnrolled = "SELECT * FROM ENROLLED_IN";
    	
    	try {
    		//Prepare statement foe execution
			PreparedStatement statement = conn.prepareStatement(getEnrolled);
			//Store result of executed statement
			ResultSet result = statement.executeQuery();
			
			//Store meta data of result such as number of columns 
			ResultSetMetaData meta = result.getMetaData();
			int columnCount = meta.getColumnCount();

			//Vector for Table Header
			Vector<String> columnHeaders = new Vector<String>();
			//Loop to retrive column names and store in Vector columnHeaders
			for(int a = 1; a <= columnCount; a++) {
				
				//System.out.println(meta.getColumnName(a));
				columnHeaders.add(meta.getColumnName(a));			
			}//End of for loop
			
			//Vector for Table data
			Vector<Vector<Object>> data = new Vector<>();
			while (result.next()) {
				
			    Vector<Object> row = new Vector<>();
			    for (int i = 1; i <= columnCount; i++) {
			        row.add(result.getObject(i));
			    }//End of for loop
			    data.add(row);
			    
			}//End of while loop
			
			display.setModel(new DefaultTableModel(data, columnHeaders));
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//End of try catch
    	
    	
   
    }//End of displayEnrolled for assignStudentPanel
    
    //----------------------------------------Student Grades  && Search Tab and Functionality------------------------------

    
    
    private JPanel searchPanel() {
    	JPanel sPanel = new JPanel();
    	
    	return sPanel;
    }
}
