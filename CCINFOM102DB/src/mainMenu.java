import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class mainMenu {
    private String url = "jdbc:mysql://ccinfo206db.ct2kugc6gcjf.ap-southeast-1.rds.amazonaws.com:3306/community";
    private String user = "root";
    private String password = "DLSU1234!";
    private Connection con;

    // SQL query to get the election results
    private String sql = "SELECT election_id, election_info, COUNT(election_info) " +
                         "FROM votingrecords " +
                         "GROUP BY election_id, election_info;";

    public void displayOptions() {
        Scanner scanner = new Scanner(System.in);
        int option;
        while (true) {
            // Display options to the user
            System.out.println("\n--- Main Menu ---");
            System.out.println("[1] Candidate Management");
            System.out.println("[2] Voting Management");
            System.out.println("[3] Resolution Management");
            System.out.println("[4] Disqualification Management");
            System.out.println("[5] Generate Reports");
            System.out.println("[6] Exit");
            System.out.print("Please enter an option (1-6): ");

            // Get the user input
            option = scanner.nextInt();

            // Handle the user's choice
            switch (option) {
                case 1:
                    System.out.print("\033[H\033[2J");
                    System.out.println("You selected Candidate Management.");
                    candidateManagement candidateManagement = new candidateManagement();
                    candidateManagement.candidateMenu(scanner);
                    break;
                case 2:
                    System.out.print("\033[H\033[2J");
                    System.out.println("You selected Voting Management.");
                    votingManagement votingManagement = new votingManagement();
                    votingManagement.displayMenu(scanner); 
                    break;
                case 3:
                    System.out.print("\033[H\033[2J");
                    System.out.println("You selected Resolution Management.");
                    resolutionManagement resolutionManagement = new resolutionManagement();
                    resolutionManagement.displayMenu(scanner); 
                    break;
                case 4:
                    System.out.print("\033[H\033[2J");
                    System.out.println("You selected Disqualification Management.");
                    disqualificationManagement disqualificationManagement = new disqualificationManagement();
                    disqualificationManagement.showMenu(scanner);
                    break;
                case 5:
                    System.out.print("\033[H\033[2J");
                    System.out.println("You selected Generate Reports.\n");
                    reports reports = new reports();
                    reports.generateReport();
                    break;
                case 6:
                    System.out.print("\033[H\033[2J");
                    System.out.println("Exiting program...");
                    scanner.close();
                    return; 
                default:
                    System.out.println("Invalid option selected. Please choose a number between 1 and 5.");
            }
        }
    }

    public static void main(String[] args) {
        mainMenu menu = new mainMenu();
        menu.displayOptions();
    }
}
