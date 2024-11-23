import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

public class votingManagement {
    private String url = "jdbc:mysql://ccinfo206db.ct2kugc6gcjf.ap-southeast-1.rds.amazonaws.com:3306/community";
    private String user = "root";
    private String password = "DLSU1234!";
    private Connection con;

    // SQL query to get the election results
    private String sql = "SELECT * " +
                         "FROM votingrecords ";

    public votingManagement() {
        try {
            // Establish connection to the database
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    // Method to display election results
    public void displayElectionResults() {
        String sql = "SELECT e.election_id, m.member_id, c.candidate_position, " +
             "CONCAT(m.first_name, ' ', m.last_name) AS completeName, COUNT(vr.vote) AS voteCount " +
             "FROM votingrecords vr " +
             "JOIN elections e ON vr.election_id3 = e.election_id " +
             "JOIN candidates c ON c.election_id2 = vr.election_id3 " +
             "JOIN members m ON m.member_id = c.candidate_info " +
             "GROUP BY completeName " +
             "ORDER BY e.election_id, c.candidate_position;";

            try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            
            System.out.println("Election Results:");
            System.out.println("   ==========================================================================================================");
            System.out.printf("%12s | %12s | %35s | %30s | %10s%n |", 
                                "Election ID", "Member ID", "Candidate Position", "Complete Name", "Vote Count");
            System.out.println("   ==========================================================================================================");
        
            while (rs.next()) {
                String electionId = rs.getString("election_id");
                String memberId = rs.getString("member_id");
                String candidPos = rs.getString("candidate_position");
                String name = rs.getString("completeName");
                int voteCount = rs.getInt("voteCount");
        
                System.out.printf("%12s | %12s | %35s | %30s | %10d%n", 
                                    electionId, memberId, candidPos, name, voteCount);
            }
            } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
            
    }

    public void displayVotingManagement() {
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            
            System.out.println("Voting Management:");
            System.out.println("============================================================");
            System.out.printf("%12s | %12s | %20s | %10s%n", 
                              "Vote ID", "Election ID", "Representative ID", "Vote");
            System.out.println("============================================================");
        
            while (rs.next()) {
                // Retrieve columns from ResultSet
                String voteId = rs.getString("vote_id");
                String electionId = rs.getString("election_id3");
                String representativeId = rs.getString("representative_id");
                String vote = rs.getString("vote");
        
                // Print formatted output
                System.out.printf("%12s | %12s | %20s | %10s%n", 
                                  voteId, electionId, representativeId, vote);
            }
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }        
    }



    // Method to insert a new record
    public void insertVote(Scanner scanner) {

        System.out.print("Enter new vote id to add\n Note: do not pick the ones already displayed above \nEnter: ");
        String vote_id = scanner.nextLine();

        String showSql1 =  "SELECT vr.election_id3 " +
                            "FROM votingrecords vr JOIN elections e ON vr.election_id3 =  e.election_id " +
                            "WHERE e.elections_status = 'Ongoing' " +
                            "GROUP BY vr.election_id3";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(showSql1);
    
            // Print table headers
            System.out.println("\n========================");
            System.out.println("|Election ID           |");
            System.out.println("========================");
    
            // Iterate through the results and print rows
            while (rs.next()) {
                String electionId = rs.getString("election_id3");
                System.out.printf("|%-12s                 |%n", electionId);
            }
    
            System.out.println("========================");
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        
        System.out.print("Enter the election id to add\nEnter: ");
        String election_id = scanner.nextLine();


        String showSql2 = """
                        SELECT c.candidate_info, m.last_name, m.first_name
                        FROM votingrecords v
                        JOIN candidates c ON v.election_id3 = c.election_id2
                        JOIN members m ON m.member_id = c.candidate_info
                        GROUP BY c.candidate_info, m.last_name, m.first_name
                        ORDER BY c.candidate_info
                    """;
        
        try (Statement stmt = con.createStatement();
         ResultSet rs = stmt.executeQuery(showSql2)) {

        System.out.printf("%-20s %-20s %-20s%n", "Candidate Info", "Last Name", "First Name");
        System.out.println("-------------------------------------------------------------");

        while (rs.next()) {
            String candidateInfo = rs.getString("candidate_info");
            String lastName = rs.getString("last_name");
            String firstName = rs.getString("first_name");

            System.out.printf("%-20s %-20s %-20s%n", candidateInfo, lastName, firstName);
        }

        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }

        System.out.println("Select from above");
        System.out.print("Enter the chosen vote to add: ");
        String vote = scanner.nextLine();
        
        String homerep = "SELECT * " + 
                        "FROM homeownerreps h " + 
                        "WHERE h.representative_info NOT IN (SELECT vr.representative_id FROM votingrecords vr WHERE vr.election_id3 = '" + election_id + "') " +
                        "AND h.representative_info NOT IN (SELECT vr.representative_id FROM votingrecords vr)";
        
            try (Statement stmt = con.createStatement()) {
            // Execute the list query
            ResultSet rs = stmt.executeQuery(homerep);
            
            // Print table headers
            System.out.println("==================================");
            System.out.println("|Representative ID               |");
            System.out.println("==================================");
            
            // Iterate and print rows
            while (rs.next()) {
                while (rs.next()) {
                    String resoRepID = rs.getString("h.representative_info");
                    String disqualified = rs.getString("h.is_disqualified");
                    System.out.printf("%12s| %15s%n",resoRepID, disqualified);
                }
            }
            
            System.out.println("========================");
        } catch (SQLException e) {
            System.out.println("Error listing records: " + e.getMessage());
        }

        System.out.print("Enter the representative id to add: ");
        String representative_id = scanner.nextLine();
        

        String insertSql = "INSERT INTO votingrecords (vote_id, election_id3, representative_id, vote) " +
                  "VALUES ('" + vote_id + "', '" + election_id + "', '" + representative_id + "', '" + vote + "')";
        try (Statement stmt = con.createStatement()) {
            int rowsAffected = stmt.executeUpdate(insertSql);
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }
    

    public void updateVote(String voteId, String electionId, String repId, String vote) {
        String updateSql = "UPDATE votingrecords SET election_id3 = ?, representative_id = ?, vote = ? WHERE vote_id = ?";
    
        try (PreparedStatement pstmt = con.prepareStatement(updateSql)) {
            // Set parameters for the PreparedStatement
            pstmt.setString(1, electionId);
            pstmt.setString(2, repId);
    
            if (vote == null || vote.trim().isEmpty()) {
                pstmt.setNull(3, java.sql.Types.VARCHAR); // Set vote to NULL if empty
            } else {
                pstmt.setString(3, vote);
            }
    
            pstmt.setString(4, voteId);
    
            // Execute the UPDATE statement
            int rowsAffected = pstmt.executeUpdate();
    
            if (rowsAffected > 0) {
                System.out.println("Vote updated successfully.");
            } else {
                System.out.println("No matching record found to update.");
            }
        } catch (SQLException e) {
            System.err.println("Error updating vote for vote_id: " + voteId);
            System.err.println("SQLException: " + e.getMessage());
        }
    }
    
    
    

    // Method to delete a vote record
    public void deleteVote(String voteId) {
        String deleteSql = "DELETE FROM votingrecords WHERE vote_id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(deleteSql)) {
            // Set the parameter for the PreparedStatement
            pstmt.setString(1, voteId);
    
            // Execute the DELETE statement
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Vote deleted successfully.");
            } else {
                System.out.println("No matching record found to delete.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting data: " + e.getMessage());
        }
    }

    public boolean voteIdExists(String voteId) {
        String checkSql = "SELECT COUNT(*) FROM votingrecords WHERE vote_id = ?";
        try (PreparedStatement checkStmt = con.prepareStatement(checkSql)) {
            checkStmt.setString(1, voteId);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking vote ID: " + e.getMessage());
        }
        return false; // Return false if an error occurs or voteId doesn't exist
    }
    
    

    // Display the menu and interact with the user
    public void displayMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Voting Management Menu ---");
            System.out.println("1. View Election Results");
            System.out.println("2. View Voting Managements");
            System.out.println("3. Insert Vote");
            System.out.println("4. Update Vote");
            System.out.println("5. Delete Vote");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume the newline character

            switch (choice) {
                case 1:
                    // Display election results
                    displayElectionResults();
                    break;
                case 2:
                    // Display Voting Management
                    displayVotingManagement();
                    break;
                case 3:
                    // Insert a new vote
                    displayVotingManagement();
                    insertVote(scanner);
                    break;
                case 4:
                    // Update an existing vote
                    displayVotingManagement();
                    System.out.print("Enter vote ID: ");
                    String updateVoteId = scanner.nextLine();
                    System.out.print("Enter Election ID: ");
                    String updateElectionId = scanner.nextLine();
                    System.out.print("Enter Representative ID: ");
                    String updateRepId = scanner.nextLine();
                    System.out.print("Enter Vote: ");
                    String updateVote = scanner.nextLine();
                    if(voteIdExists(updateVoteId)){
                        updateVote(updateVoteId, updateElectionId, updateRepId, updateVote);
                    } else {
                        System.out.println("Vote Id selected not in database");
                    }
                    
                    break;
                case 5:
                    // Delete a vote record
                    displayVotingManagement();
                    System.out.print("Enter vote ID to delete: ");
                    String deleteElectionId = scanner.nextLine();
                    deleteVote(deleteElectionId);
                    break;
                case 6:
                    // Exit the program
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
