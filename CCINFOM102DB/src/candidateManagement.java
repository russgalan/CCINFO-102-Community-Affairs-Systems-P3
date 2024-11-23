import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class candidateManagement {

    private String url = "jdbc:mysql://ccinfo206db.ct2kugc6gcjf.ap-southeast-1.rds.amazonaws.com:3306/community";
    private String user = "root";
    private String password = "DLSU1234!";
    private Connection con;

    public candidateManagement() {
        try {
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    // Method to insert a candidate
    public void insertCandidate(String candidateInfo, String electionId, String candidatePosition, String candidateStatus) {
        String sql = "INSERT INTO candidates (candidate_info, election_id2, candidate_position, candidate_status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, candidateInfo);
            pstmt.setString(2, electionId);
            pstmt.setString(3, candidatePosition);
            pstmt.setString(4, candidateStatus);
            int rowsInserted = pstmt.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("A new candidate was inserted successfully!");
            }
        } catch (SQLException e) {
            System.out.println("Error inserting candidate: " + e.getMessage());
        }
    }

    // Method to update a candidate
    public void updateCandidate(String candidateInfo, String newPosition, String newStatus) {
        String sql = "UPDATE candidates SET candidate_position = ?, candidate_status = ? WHERE candidate_info = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, newPosition);
            pstmt.setString(2, newStatus);
            pstmt.setString(3, candidateInfo); 
    
            int rowsUpdated = pstmt.executeUpdate();
            
            if (rowsUpdated > 0) {
                System.out.println("Candidate updated successfully!");
            } else {
                System.out.println("No candidate found with the provided election ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating candidate with election ID: " + candidateInfo);
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    

    // Method to delete a candidate
    public void deleteCandidate(String candidateInfo) {
        String sql = "DELETE FROM candidates WHERE candidate_info = ?";
        
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, candidateInfo);
            
            int rowsDeleted = pstmt.executeUpdate();
            
            if (rowsDeleted > 0) {
                System.out.println("Candidate deleted successfully!");
            } else {
                System.out.println("No candidate found for the given election ID.");
            }
        } catch (SQLException e) {
            System.err.println("Error deleting candidate with election ID: " + candidateInfo);
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    

    // Optional: Method to fetch and display all candidates
    public void displayCandidates() {
        String sql = "SELECT * FROM candidates";
        try (PreparedStatement pstmt = con.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                System.out.println("Candidate Info: " + rs.getString("candidate_info") +
                                   ", Election ID: " + rs.getString("election_id2") +
                                   ", Position: " + rs.getString("candidate_position") +
                                   ", Status: " + rs.getString("candidate_status"));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching candidates: " + e.getMessage());
        }
    }

    public void candidateMenu(Scanner scanner) {
        int choice = 0;

        while (choice != 5) {
            System.out.println("\nCandidate Management Menu:");
            System.out.println("[1] Insert Candidate");
            System.out.println("[2] Update Candidate");
            System.out.println("[3] Delete Candidate");
            System.out.println("[4] Display Candidates");
            System.out.println("[5] Exit");

            System.out.print("Please choose an option (1-5): ");
            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayCandidates();
                    System.out.print("Enter candidate info: ");
                    String candidateInfo = scanner.nextLine();
                    System.out.print("Enter election ID: ");
                    String electionId = scanner.nextLine();
                    System.out.print("Enter candidate position: ");
                    String candidatePosition = scanner.nextLine();
                    System.out.print("Enter candidate status: ");
                    String candidateStatus = scanner.nextLine();
                    insertCandidate(candidateInfo, electionId, candidatePosition, candidateStatus);
                    break;
                case 2:
                    displayCandidates();
                    System.out.print("Enter Candidate Info of the candidate to update: ");
                    String updateInfo = scanner.nextLine();
                    System.out.print("Enter new position: ");
                    String newPosition = scanner.nextLine();
                    System.out.print("Enter new status: ");
                    String newStatus = scanner.nextLine();
                    updateCandidate(updateInfo, newPosition, newStatus);
                    break;
                case 3:
                    displayCandidates();
                    System.out.print("Enter election ID of the candidate to delete: ");
                    String deleteId = scanner.nextLine();
                    deleteCandidate(deleteId);
                    break;
                case 4:
                    displayCandidates();
                    break;
                case 5:
                    System.out.println("Exiting Candidate Management...");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number between 1 and 5.");
            }
        }
    }

}
