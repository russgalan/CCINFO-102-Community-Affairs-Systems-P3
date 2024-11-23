import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.util.Scanner;

public class resolutionManagement {
    private String url = "jdbc:mysql://ccinfo206db.ct2kugc6gcjf.ap-southeast-1.rds.amazonaws.com:3306/community";
    private String user = "root";
    private String password = "DLSU1234!";
    private Connection con;

    // SQL query to get the election results
    private String sql = "SELECT reso_election_id, reso_chosen_vote, COUNT(reso_chosen_vote) " +
                         "FROM resovotingrecords " +
                         "GROUP BY reso_election_id, reso_chosen_vote;";

    public resolutionManagement() {
        try {
            // Establish connection to the database
            con = DriverManager.getConnection(url, user, password);
            System.out.println("Database connected!");
        } catch (SQLException e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    public void addVote(Scanner scanner) {
        String listSql = "SELECT * FROM resovotingrecords";
        
        try (Statement stmt = con.createStatement()) {
            // Execute the list query
            ResultSet rs = stmt.executeQuery(listSql);
            
            // Print table headers
            System.out.println("========================");
            System.out.println("|Reso Vote ID          |");
            System.out.println("========================");
            
            // Iterate and print rows
            while (rs.next()) {
                while (rs.next()) {
                    String resoVoteId = rs.getString("reso_vote_id");
                    String resoElectionId = rs.getString("reso_election_id");
                    String resoChosenVote = rs.getString("reso_chosen_vote");
                    String resoRepID = rs.getString("reso_representative_id");
                    System.out.printf("%12s | %16s | %12s | %15s%n", resoVoteId, resoElectionId, resoRepID, resoChosenVote);
                }
            }
            
            System.out.println("========================");
        } catch (SQLException e) {
            System.out.println("Error listing records: " + e.getMessage());
        }

        System.out.print("Enter new resolution vote id to add\n Note: do not pick the ones already displayed above \nEnter: ");
        scanner.nextLine();
        String reso_vote_id = scanner.nextLine();

        String elections = "SELECT re.resolution_election_id " +
                           "FROM resolutionelections re " +
                           "WHERE re.resolution_elections_status = 'Ongoing' ";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(elections);
    
            // Print table headers
            System.out.println("\n========================");
            System.out.println("|Election ID           |");
            System.out.println("========================");
    
            // Iterate through the results and print rows
            while (rs.next()) {
                String electionId = rs.getString("resolution_election_id");
                System.out.printf("|%-12s          |%n", electionId);
            }
    
            System.out.println("========================");
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
        
        System.out.print("Enter the resolution election id to add\nEnter: ");
        String reso_election_id = scanner.nextLine();


        String homerep = "SELECT * " + 
                        "FROM homeownerreps h " + 
                        "WHERE h.representative_info NOT IN (SELECT rv.reso_representative_id FROM resovotingrecords rv WHERE reso_election_id = '" + reso_election_id + "')" +
                        "AND h.representative_info NOT IN (SELECT rv.reso_election_id FROM resovotingrecords rv WHERE reso_election_id = '" + reso_election_id + "')";
        
        try (Statement stmt = con.createStatement()) {
            // Execute the list query
            ResultSet rs = stmt.executeQuery(homerep);
            
            // Print table headers
            System.out.println("==================================");
            System.out.println("|Reso Representative ID          |");
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

        System.out.print("Enter the resolution representative id to add: ");
        String reso_representative_id = scanner.nextLine();
        System.out.print("Enter the resolution chosen vote (Yes/No) to add: ");
        String reso_chosen_vote = scanner.nextLine();

        String checkStatusSql = "SELECT is_disqualified FROM homeownerreps WHERE representative_info = '" + reso_representative_id + "'";
        try (Statement stmt = con.createStatement()) {
        ResultSet rs = stmt.executeQuery(checkStatusSql);
        if (rs.next()) {
            String status = rs.getString("is_disqualified");
            if (status.equals("Temporary Disqualified") || status.equals("Permanently Disqualified")) {
                System.out.println("Error: Representative is " + status + " and cannot add a vote.");
                return; // Exit the function
            }
        } else {
            System.out.println("Error: Representative ID not found.");
            return; // Exit the function
        }
    } catch (SQLException e) {
        System.out.println("Error checking representative status: " + e.getMessage());
        return;
    }

        String insertSql = "INSERT INTO resovotingrecords (reso_vote_id, reso_election_id, reso_representative_id, reso_chosen_vote) " +
                  "VALUES ('" + reso_vote_id + "', '" + reso_election_id + "', '" + reso_representative_id + "', '" + reso_chosen_vote + "')";
        try (Statement stmt = con.createStatement()) {
            int rowsAffected = stmt.executeUpdate(insertSql);
            System.out.println(rowsAffected + " row(s) inserted.");
        } catch (SQLException e) {
            System.out.println("Error inserting data: " + e.getMessage());
        }
    }

    public void updateVote(Scanner scanner) {
        String listSql = "SELECT * FROM resovotingrecords";
    
        try (Statement stmt = con.createStatement()) {
            // Execute the list query
            ResultSet rs = stmt.executeQuery(listSql);
            
            // Print table headers
            System.out.println("==============================================");
            System.out.println("Reso Vote ID | Reso Election ID | Reso Chosen Vote");
            System.out.println("==============================================");
            
            // Iterate and print rows
            while (rs.next()) {
                String resoVoteId = rs.getString("reso_vote_id");
                String resoElectionId = rs.getString("reso_election_id");
                String resoChosenVote = rs.getString("reso_chosen_vote");
                System.out.printf("%12s | %16s | %15s%n", resoVoteId, resoElectionId, resoChosenVote);
            }
            
            System.out.println("==============================================");
        } catch (SQLException e) {
            System.out.println("Error listing records: " + e.getMessage());
        }

                    System.out.print("Enter vote id to update its vote: ");
                    scanner.nextLine();
                    String reso_vote_id = scanner.nextLine();
                    System.out.print("Enter new vote to update: ");
                    String new_vote = scanner.nextLine();

        // SQL statement to update only the reso_chosen_vote column
        String updateSql = "UPDATE resovotingrecords " +
                           "SET reso_chosen_vote = '" + new_vote + "' " +
                           "WHERE reso_vote_id = '" + reso_vote_id + "'";
        try (Statement stmt = con.createStatement()) {
            // Execute the update statement
            int rowsAffected = stmt.executeUpdate(updateSql);
            System.out.println(rowsAffected + " row(s) updated.");
        } catch (SQLException e) {
            // Print error message in case of failure
            System.out.println("Error updating data: " + e.getMessage());
        }
    }
    

    public void deleteVote(Scanner scanner) {
        String listSql = "SELECT * FROM resovotingrecords";
    
        try (Statement stmt = con.createStatement()) {
            // Execute the list query
            ResultSet rs = stmt.executeQuery(listSql);
            
            // Print table headers
            System.out.println("========================");
            System.out.println("Reso Vote ID | Reso Election ID | Reso Chosen Vote");
            System.out.println("========================");
            
            // Iterate and print rows
            while (rs.next()) {
                String resoVoteId = rs.getString("reso_vote_id");
                String resoElectionId = rs.getString("reso_election_id");
                String resoChosenVote = rs.getString("reso_chosen_vote");
                System.out.printf("%12s | %16s | %15s%n", resoVoteId, resoElectionId, resoChosenVote);
            }
            
            System.out.println("========================");
        } catch (SQLException e) {
            System.out.println("Error listing records: " + e.getMessage());
        }

            System.out.print("Enter vote id to delete record: ");
            scanner.nextLine();
            String reso_vote_id = scanner.nextLine();

            String deleteSql =  "DELETE FROM resovotingrecords " +
                                "WHERE reso_vote_id = '" + reso_vote_id + "' ";
            try (Statement stmt = con.createStatement()) {
                // Execute the delete statement
                int rowsAffected = stmt.executeUpdate(deleteSql);
                System.out.println(rowsAffected + " row(s) deleted.");
            } catch (SQLException e) {
                // Print error message in case of failure
                System.out.println("Error deleting data: " + e.getMessage());
            }
    }
    

    // Method to display election results
    public void displayElectionResults(Scanner scanner) {
        String elections = "SELECT reso_election_id " +
                           "FROM resovotingrecords " +
                           "GROUP BY reso_election_id";
        try (Statement stmt = con.createStatement()) {
            ResultSet rs = stmt.executeQuery(elections);
    
            // Print table headers
            System.out.println("========================");
            System.out.println("Election ID");
            System.out.println("========================");
    
            // Iterate through the results and print rows
            while (rs.next()) {
                String electionId = rs.getString("reso_election_id");
                System.out.printf("%-12s%n", electionId);
            }
    
            System.out.println("========================");
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    
        System.out.println("\nEnter Election ID to view results:");
        scanner.nextLine(); // Clear buffer
        String electionIdInput = scanner.nextLine().trim();
    
        // SQL query to get the resolution title
        String titleSql = "SELECT r.ResolutionTitle " +
                          "FROM resolutions r " +
                          "JOIN resolutionelections re ON r.election_id = re.resolution_election_id " +
                          "JOIN resovotingrecords rv ON re.resolution_election_id = rv.reso_election_id " +
                          "WHERE rv.reso_election_id = '" + electionIdInput + "' " +
                          "GROUP BY r.ResolutionTitle";
    
        // SQL query to filter by election ID
        String resultSql = "SELECT reso_election_id, reso_chosen_vote, COUNT(reso_chosen_vote) AS vote_count " +
                           "FROM resovotingrecords " +
                           "WHERE reso_election_id = '" + electionIdInput + "' " +
                           "GROUP BY reso_election_id, reso_chosen_vote " +
                           "ORDER BY vote_count DESC";
    
        String resolutionTitle = null; // Variable to store resolution title
        try (Statement stmt = con.createStatement()) {
            // Fetch and print the resolution title
            ResultSet titleRs = stmt.executeQuery(titleSql);
            if (titleRs.next()) {
                resolutionTitle = titleRs.getString("ResolutionTitle");
                System.out.println("\nResolution: " + resolutionTitle);
            } else {
                System.out.println("\nNo resolution title found for Election ID: " + electionIdInput);
                return;
            }
    
            // Fetch and print the election results
            ResultSet rs = stmt.executeQuery(resultSql);
    
            // Check if the ResultSet has any rows
            if (!rs.isBeforeFirst()) { // No rows
                System.out.println("No results found for Election ID: " + electionIdInput);
                return;
            }
    
            // Print results
            System.out.println("\nElection Results:");
            System.out.println("==========================================");
            System.out.println("Election ID | Chosen Vote | Vote Count");
            System.out.println("==========================================");
    
            String winningVote = null;
            int maxVotes = 0;
    
            while (rs.next()) {
                String electionId = rs.getString("reso_election_id");
                String chosenVote = rs.getString("reso_chosen_vote");
                int voteCount = rs.getInt("vote_count");
    
                System.out.printf("%12s | %12s | %10d%n", electionId, chosenVote, voteCount);
    
                if (voteCount > maxVotes) {
                    maxVotes = voteCount;
                    winningVote = chosenVote;
                }
            }
    
            System.out.println("==========================================");
            System.out.printf("Winner for Resolution \"%s\": %s with %d vote(s)%n", resolutionTitle, winningVote, maxVotes);
    
        } catch (SQLException e) {
            System.out.println("Error executing query: " + e.getMessage());
        }
    }
       

    // Display the menu and interact with the user
    public void displayMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n--- Resolution Management Menu ---");
            System.out.println("1. View Election Results");
            System.out.println("2. Add Vote");
            System.out.println("3. Update Vote");
            System.out.println("4. Delete Vote");
            System.out.println("5. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    // Display election results
                    displayElectionResults(scanner);
                    break;
                case 2:
                    addVote(scanner);
                    break;
                case 3:
                    updateVote(scanner);
                    break;
                case 4:
                    deleteVote(scanner);
                    break;
                case 5:
                    // Exit the program
                    System.out.println("Exiting program...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}
