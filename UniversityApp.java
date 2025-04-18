import javax.swing.*;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.Vector;

public class UniversityApp {

    private Connection conn;
    private JTextField deptIdField, deptNameField, deptCollegeField, deptOfficeNumField, deptPhoneField, lNameField, fNameField, midInitField
    ,sexField, ssnField, nNumField, cityField, stateField, streetField, zipField, permCityField, permStateField, permStreetField, permZipField, degreeField, studClassField, 
    curPhoneField, curAddressField, minorField, majorField, permPhoneField, officePhoneField, ageField, officeNumField, studentDegreeField;
    private JTable departmentTable, studentTable, instructorTable, courseTable;

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
        JPanel instructorPanel = instructorPanel();
        tabbedPane.addTab("Instructors", instructorPanel);
        
        JPanel coursePanel = coursePanel();
        tabbedPane.addTab("Courses", coursePanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }
    
    private JPanel studentPanel() {
    	JPanel panel = new JPanel(new BorderLayout());

    	JPanel inputPanel = new JPanel(new GridLayout(0, 4, 6, 6));
    	ssnField = new JTextField();
        fNameField = new JTextField();
        lNameField = new JTextField();
        midInitField = new JTextField();
        sexField = new JTextField();
        
        nNumField = new JTextField();
        cityField = new JTextField();
        stateField = new JTextField();
        streetField = new JTextField();
        zipField = new JTextField();
        
        permCityField = new JTextField();
        permStateField = new JTextField();
        permStreetField = new JTextField();
        permZipField = new JTextField();
        minorField = new JTextField();
        majorField = new JTextField();
        //student attributes
        curPhoneField = new JTextField();
        permPhoneField = new JTextField();
        curAddressField = new JTextField();
        degreeField = new JTextField();
        studentDegreeField = new JTextField();
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
        inputPanel.add(new JLabel("Current Phone:"));
        inputPanel.add(curPhoneField);
        inputPanel.add(new JLabel("Permanent Phone:"));
        inputPanel.add(permPhoneField);
        inputPanel.add(new JLabel("Degree:"));
        inputPanel.add(studentDegreeField);
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
        	String ssn = ssnField.getText().trim();
            String nNum = nNumField.getText().trim();
            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String midInit = midInitField.getText().trim();
            String sex = sexField.getText().trim();
            String currPhone = curPhoneField.getText().trim();
            String permPhone = permPhoneField.getText().trim();
            Date birthDate = (Date) birthDateSpinner.getValue();
            String street = streetField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String zip = zipField.getText().trim();
            String permStreet = permStreetField.getText().trim();
            String permCity = permCityField.getText().trim();
            String permState = permStateField.getText().trim();
            String permZip = permZipField.getText().trim();
            String curAddress = curAddressField.getText().trim();
            String major = majorField.getText().trim();
            String minor = minorField.getText().trim();
            String studClass = studClassField.getText().trim();
            String degree_program = degreeField.getText().trim();
            

            if (minor.isEmpty()) {
            	//handling students without minors
            	try {
            	//addPerson(id, ssn, fName, lName, birthDate, sex, city, state, street, zip, midInit);
            	addStudents(ssn, nNum, fName, midInit, lName, birthDate, sex, permZip, permCity, permState, permStreet, zip, city, state, street, permPhone,
                		currPhone, degree_program, studClass);
            	addMajorIn(nNum, major);
            	loadStudents();
            	}catch (Exception ex) {
            	    JOptionPane.showMessageDialog(panel, "Student failed: " + ex.getMessage());
            	}
               
            } else if(!minor.isEmpty()){
            	try {
           
                	//addStudents(id, curAddress, fName, lName, birthDate, sex, city, state, street, zip, midInit, phone, studClass, degree);
                	addMinorIn(nNum, minor);
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

    
    private JPanel instructorPanel() {
    	JPanel panel = new JPanel(new BorderLayout());

    	JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        fNameField = new JTextField();
        lNameField = new JTextField();
        ssnField = new JTextField();
        nNumField = new JTextField();
        cityField = new JTextField();
        stateField = new JTextField();
        streetField = new JTextField();
        zipField = new JTextField();
        //student attributes
        deptIdField = new JTextField();
        officeNumField = new JTextField();
        ageField = new JTextField();
        officePhoneField = new JTextField();
        
        
        
        
        
        
        JButton addButton = new JButton("Add Instructor");
        
        inputPanel.add(new JLabel("First Name:"));
        inputPanel.add(fNameField);
        inputPanel.add(new JLabel("Last Name:"));
        inputPanel.add(lNameField);
        inputPanel.add(new JLabel("Age:"));
        inputPanel.add(ageField);
        inputPanel.add(new JLabel("Instructor N#:"));
        inputPanel.add(nNumField);
        inputPanel.add(new JLabel("Social Security Number:"));
        inputPanel.add(ssnField);
        inputPanel.add(new JLabel("Current Address:"));
        inputPanel.add(new JLabel()); 
        inputPanel.add(new JLabel("Street:"));
        inputPanel.add(streetField);
        inputPanel.add(new JLabel("City:"));
        inputPanel.add(cityField);
        inputPanel.add(new JLabel("State:"));
        inputPanel.add(stateField);
        inputPanel.add(new JLabel("Zip:"));
        inputPanel.add(zipField);
        inputPanel.add(new JLabel("Office Number:"));
        inputPanel.add(officeNumField);
        inputPanel.add(new JLabel("Office Phone:"));
        inputPanel.add(officePhoneField);
        inputPanel.add(new JLabel("Associated Dept:"));
        inputPanel.add(degreeField);
        
       
       
        inputPanel.add(new JLabel()); 
        inputPanel.add(addButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);
        
        instructorTable = new JTable();
        panel.add(new JScrollPane(instructorTable), BorderLayout.CENTER);
        
        addButton.addActionListener(e -> {
        	String id = nNumField.getText().trim();
            String sex = sexField.getText().trim();
            String fName = fNameField.getText().trim();
            String lName = lNameField.getText().trim();
            String midInit = midInitField.getText().trim();
            String ssn = ssnField.getText().trim();
            int age = Integer.parseInt(ageField.getText().trim());
            String phone = officePhoneField.getText().trim();

            String street = streetField.getText().trim();
            String city = cityField.getText().trim();
            String state = stateField.getText().trim();
            String zip = zipField.getText().trim();
            
            String officeNumber = officeNumField.getText().trim();

            String dep = degreeField.getText().trim();
            
            addInstructor(ssn, id, fName, lName, age, officeNumber, zip, city, state, street, dep, phone);
            loadInstructor();

        });
        loadInstructor();
        
        return panel;
    }
    
    
//DONE
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
    
    //DONE
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
    
    //viewing departments doNE
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
    
    //function for loading students done
    private void loadStudents() {
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
    	
    }

    // Placeholder methods
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
    
    private void loadInstructor() {
    	String sql = """
    	        SELECT *
    	        From instructor
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

               instructorTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
           } catch (SQLException e) {
               JOptionPane.showMessageDialog(null, "Error loading students: " + e.getMessage());
           }
    	
    }

    private JPanel coursePanel() {
    	JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField courseIdField = new JTextField();
        JTextField courseNameField = new JTextField();
        JTextField courseDescriptionField = new JTextField();
        JTextField semesterHoursField = new JTextField();
        deptIdField = new JTextField();
        JTextField searchIdField = new JTextField();
        JButton addButton = new JButton("Add Course");
        JButton searchButton = new JButton("Search");

        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(courseNameField);
        inputPanel.add(new JLabel("Course ID:"));
        inputPanel.add(courseIdField);
        inputPanel.add(new JLabel("Course Description:"));
        inputPanel.add(courseDescriptionField);
        inputPanel.add(new JLabel("Semester Hours:"));
        inputPanel.add(semesterHoursField);
        inputPanel.add(new JLabel("Department Offered By:"));
        inputPanel.add(deptIdField);
        inputPanel.add(new JLabel()); 
        inputPanel.add(addButton);
        
        inputPanel.add(new JLabel("Search Courses"));
        inputPanel.add(new JLabel()); 
        
        inputPanel.add(new JLabel("Department Id:"));
        inputPanel.add(searchIdField);
        
        inputPanel.add(new JLabel()); 
        inputPanel.add(searchButton);
        
        panel.add(inputPanel, BorderLayout.NORTH);

        courseTable = new JTable();
        panel.add(new JScrollPane(courseTable), BorderLayout.CENTER);
        searchButton.addActionListener(e -> {
        	String searchId = searchIdField.getText().trim();
        	
        	searchCoursesByDept(searchId);
        	searchIdField.setText("");
        	
        });
        addButton.addActionListener(e -> {
            String deptId = deptIdField.getText().trim();
            String name = courseNameField.getText().trim();
            String description = courseDescriptionField.getText().trim();
            String courseId = courseIdField.getText().trim();
            int hours = Integer.parseInt(semesterHoursField.getText().trim());

            if (!courseId.isEmpty() && !deptId.isEmpty()) {
                addCourse(courseId, deptId, name, description, hours);
                courseIdField.setText("");
                courseNameField.setText("");
                courseDescriptionField.setText("");
                semesterHoursField.setText("");
                deptIdField.setText("");;
                loadDepartments();
                
            } else {
                JOptionPane.showMessageDialog(panel, "ID and Name are required.");
            }
        });

        

        return panel;
    }
    
    private void addCourse(String courseNum, String deptId, String name, String description, int hours) {
    	 
    	String sql = "INSERT INTO course (course_number, department_offering, name, description, semester_hours) values (?, ?, ?, ?, ?)";
    	try (PreparedStatement stmt = conn.prepareStatement(sql)) {
    		stmt.setString(1, courseNum);
            stmt.setString(2, deptId);
            stmt.setString(3, name);
            stmt.setString(4, description);
            stmt.setInt(5, hours);

            stmt.executeUpdate();
            JOptionPane.showMessageDialog(null, "Course added");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error adding course: " + e.getMessage());
        }
    	
    }
    
    
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

    private void addSections() {
        
    }

    private void addStudentToSection() {
      
    }

    private void AssignGrade() {
        
    }
}
