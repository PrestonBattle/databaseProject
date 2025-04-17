import javax.swing.*;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class UniversityApp {

    private Connection conn;
    private JTextField deptIdField, deptNameField, deptCollegeField, deptOfficeNumField, deptPhoneField, lNameField, fNameField, midInitField
    ,sexField, ssnField, nNumField, cityField, stateField, streetField, zipField, degreeField, studClassField, 
    curPhoneField, curAddressField, minorField, majorField;
    private JTable departmentTable, studentTable;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UniversityApp app = new UniversityApp();
            app.connectToDatabase();
            app.createAndShowGUI();
        });
    }

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
        }
    }

    private void createAndShowGUI() {
        JFrame frame = new JFrame("University Manager");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Department Tab
        JPanel departmentPanel = createDepartmentPanel();
        tabbedPane.addTab("Departments", departmentPanel);

        // Student Tab 
        JPanel studentPanel = studentPanel();
        tabbedPane.addTab("Students", studentPanel);

        // Instructor 
        JPanel instructorPanel = new JPanel();
        tabbedPane.addTab("Instructors", instructorPanel);
        
        JPanel coursePanel = new JPanel();
        tabbedPane.addTab("Courses", coursePanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }
    
    private JPanel studentPanel() {
    	JPanel panel = new JPanel(new BorderLayout());

    	JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        fNameField = new JTextField();
        lNameField = new JTextField();
        midInitField = new JTextField();
        sexField = new JTextField();
        ssnField = new JTextField();
        nNumField = new JTextField();
        cityField = new JTextField();
        stateField = new JTextField();
        streetField = new JTextField();
        zipField = new JTextField();
        minorField = new JTextField();
        majorField = new JTextField();
        //student attributes
        curPhoneField = new JTextField();
        curAddressField = new JTextField();
        degreeField = new JTextField();
        studClassField = new JTextField();
        
        
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner birthDateSpinner = new JSpinner(dateModel);
        birthDateSpinner.setEditor(new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd"));
        
        
        JButton addButton = new JButton("Add Student");
        
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(fNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lNameField);
        inputPanel.add(new JLabel("Middle Initial:"));
        inputPanel.add(midInitField);
        inputPanel.add(new JLabel("Sex:"));
        inputPanel.add(sexField);
        inputPanel.add(new JLabel("Birthdate:"));
        inputPanel.add(birthDateSpinner);
        inputPanel.add(new JLabel("Student N#:"));
        inputPanel.add(nNumField);
        inputPanel.add(new JLabel("Social Security Number:"));
        inputPanel.add(ssnField);
        inputPanel.add(new JLabel("Street:"));
        inputPanel.add(streetField);
        inputPanel.add(new JLabel("City:"));
        inputPanel.add(cityField);
        inputPanel.add(new JLabel("State:"));
        inputPanel.add(stateField);
        inputPanel.add(new JLabel("Zip:"));
        inputPanel.add(zipField);
        inputPanel.add(new JLabel("Current Address:"));
        inputPanel.add(curAddressField);
        inputPanel.add(new JLabel("Current Phone:"));
        inputPanel.add(curPhoneField);
        inputPanel.add(new JLabel("Degree:"));
        inputPanel.add(degreeField);
        inputPanel.add(new JLabel("Major Program:"));
        inputPanel.add(majorField);
        inputPanel.add(new JLabel("Minor Program:"));
        inputPanel.add(minorField);
        inputPanel.add(new JLabel("Class:"));
        inputPanel.add(studClassField);
        inputPanel.add(new JLabel()); 
        inputPanel.add(addButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        studentTable = new JTable();
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        
        addButton.addActionListener(e -> {
            String id = nNumField.getText().trim();
            String sex = sexField.getText().trim();
            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String midInit = midInitField.getText().trim();
            String ssn = ssnField.getText().trim();
            String phone = curPhoneField.getText().trim();
            Date birthDate = (Date) birthDateSpinner.getValue();
            String street = streetField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String zip = zipField.getText().trim();
            String curAddress = curAddressField.getText().trim();
            String major = majorField.getText().trim();
            String minor = minorField.getText().trim();
            String studClass = studClassField.getText().trim();
            String degree = degreeField.getText().trim();
            

            if (minor.isEmpty() && !id.isEmpty()) {
            	//handling students without minors
            	try {
            	//addPerson(id, ssn, fName, lName, birthDate, sex, city, state, street, zip, midInit);
            	addStudents(id, curAddress, phone, studClass, degree);
            	addMajorIn(id, major);
            	loadStudents();
            	}catch (Exception ex) {
            	    JOptionPane.showMessageDialog(panel, "Student failed: " + ex.getMessage());
            	}
               
            } else if(!minor.isEmpty() && !id.isEmpty()){
            	try {
           
                	//addStudents(id, curAddress, fName, lName, birthDate, sex, city, state, street, zip, midInit, phone, studClass, degree);
                	addMinorIn(id, minor);
                	loadStudents();
                	}catch (Exception ex) {
                	    JOptionPane.showMessageDialog(panel, "Student failed: " + ex.getMessage());
                	}
            }else {
                JOptionPane.showMessageDialog(panel, "ID and Name are required.");
            }
        });
        
        loadStudents();
        
        return panel;
    }
    
//    private JPanel instructorPanel() {
//    	JPanel panel = new JPanel(new BorderLayout());
//
//    	JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
//        fNameField = new JTextField();
//        lNameField = new JTextField();
//        midInitField = new JTextField();
//        sexField = new JTextField();
//        ssnField = new JTextField();
//        nNumField = new JTextField();
//        cityField = new JTextField();
//        stateField = new JTextField();
//        streetField = new JTextField();
//        zipField = new JTextField();
//        minorField = new JTextField();
//        majorField = new JTextField();
//        //student attributes
//        deptIdField = new JTextField();
//        curAddressField = new JTextField();
//        degreeField = new JTextField();
//        studClassField = new JTextField();
//        
//        
//        SpinnerDateModel dateModel = new SpinnerDateModel();
//        JSpinner birthDateSpinner = new JSpinner(dateModel);
//        birthDateSpinner.setEditor(new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd"));
//        
//        
//        JButton addButton = new JButton("Add Student");
//        
//        inputPanel.add(new JLabel("First Name:"));
//        inputPanel.add(fNameField);
//        inputPanel.add(new JLabel("Last Name:"));
//        inputPanel.add(lNameField);
//        inputPanel.add(new JLabel("Middle Initial:"));
//        inputPanel.add(midInitField);
//        inputPanel.add(new JLabel("Sex:"));
//        inputPanel.add(sexField);
//        inputPanel.add(new JLabel("Birthdate:"));
//        inputPanel.add(birthDateSpinner);
//        inputPanel.add(new JLabel("Student N#:"));
//        inputPanel.add(nNumField);
//        inputPanel.add(new JLabel("Social Security Number:"));
//        inputPanel.add(ssnField);
//        inputPanel.add(new JLabel("Street:"));
//        inputPanel.add(streetField);
//        inputPanel.add(new JLabel("City:"));
//        inputPanel.add(cityField);
//        inputPanel.add(new JLabel("State:"));
//        inputPanel.add(stateField);
//        inputPanel.add(new JLabel("Zip:"));
//        inputPanel.add(zipField);
//        inputPanel.add(new JLabel("Current Address:"));
//        inputPanel.add(curAddressField);
//        inputPanel.add(new JLabel("Current Phone:"));
//        inputPanel.add(curPhoneField);
//        inputPanel.add(new JLabel("Degree:"));
//        inputPanel.add(degreeField);
//        inputPanel.add(new JLabel("Major Program:"));
//        inputPanel.add(majorField);
//        inputPanel.add(new JLabel("Minor Program:"));
//        inputPanel.add(minorField);
//        inputPanel.add(new JLabel("Class:"));
//        inputPanel.add(studClassField);
//        inputPanel.add(new JLabel()); 
//        inputPanel.add(addButton);
//        
//        panel.add(inputPanel, BorderLayout.NORTH);
//        
//        studentTable = new JTable();
//        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
//        
//        addButton.addActionListener(e -> {
//            String id = nNumField.getText().trim();
//            String sex = sexField.getText().trim();
//            String fName = fNameField.getText().trim();
//            String lName = lNameField.getText().trim();
//            String midInit = midInitField.getText().trim();
//            String ssn = ssnField.getText().trim();
//            String phone = curPhoneField.getText().trim();
//            Date birthDate = (Date) birthDateSpinner.getValue();
//            String street = streetField.getText().trim();
//            String city = cityField.getText().trim();
//            String state = stateField.getText().trim();
//            String zip = zipField.getText().trim();
//            
//            
//
//        
//        //loadStudents();
//        
//        //return panel;
//    }

    private JPanel createDepartmentPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        deptIdField = new JTextField();
        deptNameField = new JTextField();
        deptCollegeField = new JTextField();
        deptOfficeNumField = new JTextField();
        deptPhoneField = new JTextField();
        JButton addButton = new JButton("Add Department");

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

        panel.add(inputPanel, BorderLayout.NORTH);

        departmentTable = new JTable();
        panel.add(new JScrollPane(departmentTable), BorderLayout.CENTER);

        addButton.addActionListener(e -> {
            String id = deptIdField.getText().trim();
            String name = deptNameField.getText().trim();
            String college = deptCollegeField.getText().trim();
            String officeNum = deptOfficeNumField.getText().trim();
            String phone = deptPhoneField.getText().trim();

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
        });

        loadDepartments();

        return panel;
    }

    private void addDepartment(String id, String name, String college, String officeNum, String phone) {
        String sql = "INSERT INTO department (Department_Name, Department_Code, College, Office#, Office_Phone) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, id);
            stmt.setString(3, college);
            stmt.setString(4, officeNum);
            stmt.setString(5, phone);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Department added!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding department: " + e.getMessage());
        }
    }

   
    //my functions to display data
    
    //viewing departments
    private void loadDepartments() {
        String sql = "SELECT * FROM department";
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

            departmentTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading departments: " + e.getMessage());
        }
    }
    
    private void loadStudents() {
    	String sql = """
    	        SELECT 
    	            p.N#, p.ssn, p.First_Name, p.Last_Name, p.Middle_Initial, p.BirthDate, p.sex,
    	            p.city, p.state, p.street, p.zip, s.Current_Address, s.Current_Phone, s.Degree_Program, s.Class,
    	            m.deptcode AS Major_Code,
    	            mi.deptcode AS Minor_Code
    	        FROM 
    	            person p
    	        JOIN 
    	            student s ON p.N# = s.N#
    	        LEFT JOIN 
    	            majorin m ON p.N# = m.N#
    	        LEFT JOIN 
    	            minorin mi ON p.N# = mi.N#
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
    	
    }

    // Placeholder methods
    private void addStudents(String id, String curAddress, String curPhone, String studClass, String Degree) {

        String sql = "INSERT INTO student (N#, Current_Address, Current_Phone, Degree_Program, Class) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id);
            stmt.setString(2, curAddress);
            stmt.setString(3, curPhone);
            stmt.setString(4, Degree);
            stmt.setString(5, studClass);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Student added!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding student: " + e.getMessage());
        }
        
        //need to add major in functions and minor in functions
        
    }
    
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

    private void addInstructor() {
        
    }

//    private void addPerson(String id, String ssn, String fName, String lName,
//    		Date birthDate, String sex, String city, String state, String street,
//    		String zip, String midInit) {
//
//    	String sql = "INSERT INTO person (SSN, N#, First_Name, Middle_Initial, Last_Name, BirthDate, sex, city, state, street, zip) " +
//    			"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
//
//    	try (PreparedStatement stmt = conn.prepareStatement(sql)) {
//    		stmt.setString(1, ssn);
//    		stmt.setString(2, id);
//    		stmt.setString(3, fName);
//    		stmt.setString(4, midInit); 
//    		stmt.setString(5, lName);
//    		stmt.setDate(6, new java.sql.Date(birthDate.getTime())); 
//    		stmt.setString(7, sex);
//    		stmt.setString(8, city);
//    		stmt.setString(9, state);
//    		stmt.setString(10, street);
//    		stmt.setString(11, zip);
//
//    		stmt.executeUpdate();
//    		JOptionPane.showMessageDialog(null, "Add person successful");
//    	} catch (SQLException e) {
//    		JOptionPane.showMessageDialog(null, "Error adding person: " + e.getMessage());
//    	}
//    }

    private void addCourses() {
       
    }

    private void searchCourses() {
       
    }

    private void searchCourseByInstructor() {
        
    }

    private void addSections() {
        
    }

    private void addStudentToSection() {
      
    }

    private void AssignGrade() {
        
    }
}
