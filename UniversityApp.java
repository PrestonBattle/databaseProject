import javax.swing.*;
import java.util.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Arrays;
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
    private JTable departmentTable, studentTable, instructorTable, courseTable, gradeReportTable;
    private JLabel gpaLabel = new JLabel("GPA: ");
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
        
        JPanel sectionPanel = sectionPanel();
        tabbedPane.addTab("Sections", sectionPanel);
        
        JPanel searchPanel = searchPanel();
        tabbedPane.addTab("Search", searchPanel);
        
        //Assign Tab
        JPanel assignPanel = assignStudentPanel();
        tabbedPane.addTab("Assign Student", assignPanel);
        
        
        JPanel gradePanel = gradeReportPanel();
        tabbedPane.addTab("Grade Reports", gradePanel);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }
    
    private JPanel gradeReportPanel() {
    	JPanel panel = new JPanel(new BorderLayout());
    	
    	JPanel inputPanel = new JPanel(new GridLayout(0, 2, 6, 6));
    	
    	JTextField studIdField = new JTextField();
    	JButton generateButton = new JButton("Generate Report");
    	
    	inputPanel.add(new JLabel("Generate Grade Report for Student"));
    	inputPanel.add(new JLabel());
    	
    	inputPanel.add(new JLabel("Student N Number: "));
    	inputPanel.add(new JLabel());
    	inputPanel.add(studIdField);
    	inputPanel.add(new JLabel());
    	inputPanel.add(generateButton);
    	inputPanel.add(new JLabel());
    	
    	panel.add(inputPanel, BorderLayout.NORTH);
    	
    	gradeReportTable = new JTable();
    	panel.add(new JScrollPane(gradeReportTable), BorderLayout.CENTER);
    	panel.add(new JScrollPane(gpaLabel), BorderLayout.SOUTH);
    	
    	generateButton.addActionListener(e -> {
    		
    		String studId = studIdField.getText().trim();
    		generateReport(studId);
    		
    	});
    	
    	return panel;
    }
    
    private void generateReport(String studId) {
        String sql = "SELECT s.first_name, s.last_name, s.n#, " +
                     "e.course_number as course ,e.semester, e.year, " +
                     "i.first_name as Instructor_first_name, " +
                     "i.last_name as Instructor_last_name, " +
                     "e.section#, e.grade  " +
                     "FROM student s " +
                     "JOIN enrolled_in e ON s.n# = e.n# " +
                     "JOIN section ON section.year = e.year " +
                     "AND section.semester = e.semester " +
                     "AND section.course_number = e.course_number " +
                     "AND section.section# = e.section# " +
                     "JOIN instructor i ON i.n# = section.instructor_n# " +
                     "WHERE UPPER(s.n#) = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studId.toUpperCase());

            try (ResultSet rs = pstmt.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int columns = meta.getColumnCount();

                Vector<String> columnNames = new Vector<>();
                for (int i = 1; i <= columns; i++) {
                    columnNames.add(meta.getColumnName(i));
                }

                Vector<Vector<Object>> data = new Vector<>();
                double totalPoints = 0;
                int courseCount = 0;

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= columns; i++) {
                        row.add(rs.getObject(i));
                    }

                    String grade = rs.getString("grade");
                    Double gradePoint = getGradePoint(grade);
                    if (gradePoint != null) {
                        totalPoints += gradePoint;
                        courseCount++;
                    }

                    data.add(row);
                }

                gradeReportTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));

                if (courseCount > 0) {
                    double gpa = totalPoints / courseCount;
                    gpaLabel.setText("GPA: " + String.format("%.2f", gpa));
                } else {
                    gpaLabel.setText("GPA: N/A (No valid grades found)");
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading report: " + e.getMessage());
        }
    }

    
    private Double getGradePoint(String grade) {
    	
    	if (grade == null) return null;
        switch (grade.toUpperCase()) {
            case "A": return 4.0;
            case "A-": return 3.7;
            case "B+": return 3.3;
            case "B": return 3.0;
            case "B-": return 2.7;
            case "C+": return 2.3;
            case "C": return 2.0;
            
            case "D": return 1.0;
            case "F": return 0.0;
            case "FA": return 0.0;
            default: return null; 
        }
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
					
					if(  checkPreReq(section,courseNum, nNum)   ) {
						
						addStudentToSection(section,courseNum, semester, year,grade, nNum);
						displayEnrolled(display);
					};
					
					
				}else{
					if(   checkPreReq(section,courseNum, nNum)  ) {
						
						
						addStudentToSection(section,courseNum, semester, year, nNum);
						displayEnrolled(display);
					};
					
					
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
    
    private boolean checkPreReq(String sectionID,String course, String nNumber) {
    	
    	//FIND PREREQ OF COURSE
    	String findPreReq = "SELECT PREREQUISITE_COURSES FROM COURSE_PREREQUISITES WHERE COURSE_NUMBER = ?";
    	// Here to store result
    	ResultSet coursePreReq; 
    	//Will determine if student meet prereq are not assume that they do by default
    	boolean meetRequirement = true;
    	
    	
    	try {
    		//Statement to find prereq of enter course
			PreparedStatement state = conn.prepareStatement(findPreReq);
			state.setString(1, course);
			
			//End of 
			coursePreReq = state.executeQuery();
			
			//If there any input continue
			if( coursePreReq.next() ) {
				
				//Find index of column that store the prequisite
				int index = coursePreReq.findColumn("PREREQUISITE_COURSES");
				//Get the course number of the prerequisite
				String output = coursePreReq.getNString(index);
				
				//Solely testing
				System.out.println("Index is: " + index + " Output is: " + output);
			//-----------------------------------Found PreReq-----------------------------	
			
				
			
				//Need to search if student is passed the prerequisite
				String checkEnrollment = "SELECT * FROM ENROLLED_IN WHERE N# = ? AND COURSE_NUMBER = ?";
				state = conn.prepareStatement(checkEnrollment);
								
				state.setString(1, nNumber);
				state.setString(2, output );
				
				//Inner  resultSet to find enrollment of student courses that match the prereq
				ResultSet studentInPreReq = state.executeQuery();
				
				//Need to search for the section that student is trying to enroll in
				String checkSection = "SELECT * FROM SECTION WHERE SECTION# = ?";
				state = conn.prepareStatement(checkSection);
								
				state.setString(1, sectionID);
								
				//Inner  resultSet to find section info that student wishes to enroll in
				ResultSet attemptedSection = state.executeQuery();
				attemptedSection.next();
				
				
			
				//If there is a value check it elsewise they never enrolled in the prereq and can't enroll in course
				if(studentInPreReq.next()) {
					
					//Get year of prereq
					int indexOfYear = studentInPreReq.findColumn("YEAR");
					String yearPreReq = studentInPreReq.getNString(indexOfYear);
					
					//Get year of attempted section
					int indexYearSection = attemptedSection.findColumn("YEAR");
					String yearSection = attemptedSection.getNString(indexYearSection);
					
					//Get semester of prereq
					int indexOfSemester = studentInPreReq.findColumn("SEMESTER");
					String semesterPreReq = studentInPreReq.getNString(indexOfSemester);
					
					//Get semester of attemepted section
					int indexSemesterSection = attemptedSection.findColumn("SEMESTER");
					String semesterSection = attemptedSection.getNString(indexSemesterSection);
					
					//Get grade of prereq
					int indexOfGrade = studentInPreReq.findColumn("GRADE");
					String gradePreReq = studentInPreReq.getNString(indexOfGrade);
					
					
					//If prereq year is before attempted Section all good
					if(Integer.parseInt(yearPreReq) < Integer.parseInt(yearSection)) {
						
						System.out.println("All clear");
						
						if( gradePreReq.equals("D") || gradePreReq.equals("F") || gradePreReq == null  ){
					    	
					    	JOptionPane.showMessageDialog(new JDialog(), "Can't enroll in Course if you did not pass the PreReq.");
						    return meetRequirement = false;
					    	
					    }else {
					    	System.out.println("All good — prerequisite was taken before and passed.");
					    	return meetRequirement = true;
					    }//End of if else that checks passing graade
					
						//If prereq year is same year as attempted section check semester
					}else if (Integer.parseInt(yearPreReq) == Integer.parseInt(yearSection)) {
						
						List<String> semesterOrder = Arrays.asList("Spring", "Summer", "Fall");

						int prereqIndex = semesterOrder.indexOf(semesterPreReq);
						int sectionIndex = semesterOrder.indexOf(semesterSection);

						// Invalid comparison (just in case values are misspelled)
						if (prereqIndex == -1 || sectionIndex == -1) {
						    JOptionPane.showMessageDialog(new JDialog(), "Invalid semester value entered.");
						    return meetRequirement = false;
						}

						// Prereq is in the same semester → Not allowed
						if (prereqIndex == sectionIndex) {
						    JOptionPane.showMessageDialog(new JDialog(), "Can't enroll in PreReq during the same Semester as the Course.");
						    return meetRequirement = false;

						// Prereq is **after** the course semester → Not allowed
						} else if (prereqIndex > sectionIndex) {
						    JOptionPane.showMessageDialog(new JDialog(), "Can't enroll in PreReq after the Course semester.");
						    return meetRequirement = false;

						// Prereq is **before** → Allowed
						} else {
						    System.out.println("All good — prerequisite was taken before.");
						    
						    if( gradePreReq.equals("D") || gradePreReq.equals("F") || gradePreReq == null  ){
						    	
						    	JOptionPane.showMessageDialog(new JDialog(), "Can't enroll in Course if you did not pass the PreReq.");
							    return meetRequirement = false;
						    	
						    }else {
						    	
						    	System.out.println("All good — prerequisite was taken before and passed.");
						    	return meetRequirement = true;
						    	
						    }//End of if else that checks passing graade
						}//End of if else that check semester align correctly


						
					}//End of if else for when year is the same
					
					
					
					
					
		//-----------------------------------Found Student has enrolled in PreReq----------------	
				}else {
					//If they didn't enroll and pass prereq
					JOptionPane.showMessageDialog(new JDialog(),"Must enrolled and pass " + output +" first.");
					return meetRequirement = false;
				}//End of if else statement of whether they have a record of being enrolled in PreReq
		//-----------------------------------Found Student has NOT enrolled in PreReq----------------	
				
			}else{
				
				return meetRequirement = true;
			}//End of if else that states whethere there a pre req statement
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(new JDialog(),"Error with checking Prerequisites please ensure information is correct.");
			return meetRequirement = false;
		}//End of try catch
    	
    	return meetRequirement;
    	
    }//End of checkPreReq
    
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
    	JPanel sPanel = new JPanel(new GridBagLayout());
    	
    	GridBagConstraints sC = new GridBagConstraints();
    	
    	sC.fill = GridBagConstraints.HORIZONTAL;
    	sC.ipady = 7;
    	
    	//----------------Row1----------------
    	sC.gridx = 0;
    	sC.gridy = 0;
    	
    	JLabel courseD = new JLabel("Search Course by Department:");
    	sPanel.add(courseD,sC);
    	sC.gridx = 1;
    	JTextField courseDept = new JTextField(20);
    	sPanel.add(courseDept, sC);
    	
    	//----------------Row2----------------
    	sC.gridx = 0;
    	sC.gridy = 1;
    	
    	JLabel courseI = new JLabel("Search Course by Instructor:");
    	sPanel.add(courseI, sC);
    	sC.gridx = 1;
    	JTextField courseInst = new JTextField(20);
    	sPanel.add(courseInst, sC);
    	
    	//-----------------Row3-------------
    	sC.gridx = 0;
    	sC.gridy = 2;
    	sC.gridwidth = 1;
    	
    	JButton reset = new JButton("Reset");
    	sPanel.add(reset, sC);
    	
    	sC.gridx = 1;
    	JButton search = new JButton("Search");
    	sPanel.add(search, sC);
    
    	//---------------Display Result----------------
    	sC.gridx = 0;
    	sC.gridy = 3;
    	sC.gridwidth = 2;
    	sC.weightx = 1.0;
    	sC.weighty = 1.0;
    	sC.gridheight = GridBagConstraints.REMAINDER;
    	JTable display = new JTable();
    	loadCourseInfo(display);
    	sPanel.add(new JScrollPane(display), sC);
    	
    	//-----------Button Functionality--------------
    	
    	search.addActionListener(new ActionListener() {
    			
    			public void actionPerformed(ActionEvent e) {
    				
    				String dept = courseDept.getText();
    				String instuctor = courseInst.getText();
    				
    				
    				if( !dept.isEmpty() ) {
    					searchCoursesByDept(display,dept);
    				}else if( !instuctor.isEmpty() ) {
    					searchCoursesByInstructor(instuctor, display);
    				}
    				
    			}//End of actionPerformed
    	}//End of outer wrap
    			);//End of addActionListener to search button
    	
    	reset.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
				
				loadCourseInfo(display);
			
			}//End of actionPerformed
	}//End of outer wrap
			);//End of addActionListener to reset button
	
    	
    	return sPanel;
    }
    
    private void searchCoursesByDept(JTable display,String deptId) {
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

                display.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading courses: " + e.getMessage());
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

 private void searchCoursesByInstructor(String instructorN, JTable display) {
	    String query = "SELECT * FROM SECTION WHERE INSTRUCTOR_N# = ?";

	    try {
	        //statement to execute the query
	        PreparedStatement statement = conn.prepareStatement(query);
	        statement.setString(1, instructorN);  // Set the Instructor N# parameter

	        // query and retrieve the result
	        ResultSet result = statement.executeQuery();

	        //meta data of result (number of columns)
	        ResultSetMetaData meta = result.getMetaData();
	        int columnCount = meta.getColumnCount();

	        //hold table headers (column names)
	        Vector<String> columnHeaders = new Vector<String>();
	        for (int a = 1; a <= columnCount; a++) {
	            columnHeaders.add(meta.getColumnName(a));  // Add column names to the vector
	        }

	        // Vector holds table data
	        Vector<Vector<Object>> data = new Vector<>();
	        while (result.next()) {
	            Vector<Object> row = new Vector<>();
	            for (int i = 1; i <= columnCount; i++) {
	                row.add(result.getObject(i));  // Add each column's value to the row
	            }
	            data.add(row);  // Add the row to the data vector
	        }

	        //model for the JTable with the retrieved data and column headers
	        display.setModel(new DefaultTableModel(data, columnHeaders));

	    } catch (SQLException e) {
	        e.printStackTrace();
	        JOptionPane.showMessageDialog(new JDialog(), "Error retrieving courses for Instructor N#: " + instructorN);
	    }
	}
}
