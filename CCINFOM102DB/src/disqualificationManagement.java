import java.sql.*;
import java.util.Scanner;

public class disqualificationManagement {

    private String url = "jdbc:mysql://ccinfo206db.ct2kugc6gcjf.ap-southeast-1.rds.amazonaws.com:3306/community";
    private String user = "root";
    private String password = "DLSU1234!";
    private Connection con;

    public disqualificationManagement() {
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    // Method to update the disqualification status and date of a representative
    public void updateDisqualification(String representativeInfo, String isDisqualified, String disqualifiedDate) {
        String sql = "UPDATE homeownerreps SET is_disqualified = ?, disqualifiedDate = ? WHERE representative_info = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, isDisqualified); 
            
            if (disqualifiedDate == null || disqualifiedDate.trim().isEmpty()) {
                pstmt.setNull(2, java.sql.Types.DATE); 
            } else {
                pstmt.setString(2, disqualifiedDate); 
            }
            
            pstmt.setString(3, representativeInfo);
            
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Disqualification status updated successfully!");
            } else {
                System.out.println("No records found for the given representative info.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating disqualification for representative: " + representativeInfo);
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    // Method to insert a new representative record
    public void insertRepresentative(String representativeInfo, String isDisqualified, String disqualifiedDate) {
        String sql = "INSERT INTO homeownerreps (representative_info, is_disqualified, disqualifiedDate) VALUES (?, ?, ?)";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, representativeInfo);
            pstmt.setString(2, isDisqualified);
            
            if (disqualifiedDate == null || disqualifiedDate.trim().isEmpty()) {
                pstmt.setNull(3, java.sql.Types.DATE); // Set as NULL if no date provided
            } else {
                pstmt.setString(3, disqualifiedDate); // Set date if valid
            }
    
            int rowsInserted = pstmt.executeUpdate();
            
            if (rowsInserted > 0) {
                System.out.println("New representative added successfully!");
            } else {
                System.out.println("Failed to add the new representative.");
            }
        } catch (SQLException e) {
            System.err.println("Error inserting representative with info: " + representativeInfo);
            System.err.println("SQLException: " + e.getMessage());
        }
    }

    // Method to delete a representative
    public void deleteRepresentative(String representativeInfo) {
        String sql = "DELETE FROM homeownerreps WHERE representative_info = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, representativeInfo);
            
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                System.out.println("Representative deleted successfully!");
            } else {
                System.out.println("No records found for the given representative info.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting representative: " + e.getMessage());
        }
    }

    // Method to display all representatives
    public void displayRepresentatives() {
        String sql = "SELECT * FROM homeownerreps";
        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Representative Info: " + rs.getString("representative_info") +
                                   ", Disqualified: " + rs.getString("is_disqualified") +
                                   ", Disqualification Date: " + rs.getString("disqualifiedDate"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching representatives: " + e.getMessage());
        }
    }

    // Main menu method to allow user interaction
    public void showMenu(Scanner scanner) {
        int choice;

        do {
            System.out.println("\n----- Disqualification Management -----");
            System.out.println("1. Insert Representative");
            System.out.println("2. Update Disqualification");
            System.out.println("3. Delete Representative");
            System.out.println("4. Display All Representatives");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");
            
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    // Insert a new representative
                    displayRepresentatives();
                    System.out.print("Enter representative info: ");
                    String repInfo = scanner.nextLine();
                    System.out.print("Is disqualified ?: ");
                    String isDisqualified = scanner.nextLine();
                    System.out.print("Enter disqualification date (yyyy-mm-dd): ");
                    String disqualifiedDate = scanner.nextLine();
                    insertRepresentative(repInfo, isDisqualified, disqualifiedDate);
                    break;
                case 2:
                    // Update disqualification status of a representative
                    displayRepresentatives();
                    System.out.print("Enter representative info: ");
                    repInfo = scanner.nextLine();
                    System.out.print("Is disqualified? : ");
                    isDisqualified = scanner.nextLine();
                    System.out.print("Enter disqualification date (yyyy-mm-dd): ");
                    disqualifiedDate = scanner.nextLine();
                    updateDisqualification(repInfo, isDisqualified, disqualifiedDate);
                    break;
                case 3:
                    // Delete a representative
                    displayRepresentatives();
                    System.out.print("Enter representative info to delete: ");
                    repInfo = scanner.nextLine();
                    deleteRepresentative(repInfo);
                    break;
                case 4:
                    // Display all representatives
                    displayRepresentatives();
                    break;
                case 5:
                    // Exit the program
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice! Please try again.");
            }
        } while (choice != 5);
    }
}
