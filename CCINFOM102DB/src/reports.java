import java.sql.*;
import java.util.Scanner;

public class reports {

    private String url = "jdbc:mysql://ccinfo206db.ct2kugc6gcjf.ap-southeast-1.rds.amazonaws.com:3306/community";
    private String user = "root";
    private String password = "DLSU1234!";
    private Connection connection;
    Scanner scanner = new Scanner(System.in);
    
    public reports(){
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connected!");
            } catch (SQLException e) {
                System.out.println("Error connecting to database: " + e.getMessage());
            }
    }

    public void generateReport(){
            System.out.println("elec_participation          ->     Election Participation for all homeowner representatives for the last X years ");
            System.out.println("reso_participation          ->     Resolution Participation for all homeowner representatives for the last X years ");
            System.out.println("homeelec_participation      ->     Homeowner Election and Voting Participation for the last X years ");
            System.out.println("cand_freq                   ->     Candidate Frequency Report  for the last X years \n");
            System.out.println("homerep_disq                ->     Homeowner Representative Disqualification Frequency Report for the last X years \n");

            System.out.println("Enter the name of the table you want to generate report:");
            String table = scanner.nextLine();

            System.out.print("How many x years: ");
            int x = scanner.nextInt();

            getreport(x, table);
    }

    public void getreport(int years, String chosenReport) {
        String query = "";
        boolean isParameterized = true; // Flag to indicate if the query uses ?
        
        switch (chosenReport.toLowerCase()) {
            case "elec_participation":
                query = """
                    SELECT 		
                        hr.representative_info AS VotingRep,
                        e.election_name AS Election,
                        COUNT(vr.vote_id) AS TotalVotes,
                        e.start_date AS ElectionDate
                    FROM 
                        votingrecords vr
                    JOIN 
                        elections e ON vr.election_id3 = e.election_id
                    JOIN 
                        homeownerreps hr ON vr.representative_id = hr.representative_info
                    WHERE 
                        e.start_date >= DATE_ADD(CURDATE(), INTERVAL -? YEAR)
                    GROUP BY 
                        hr.representative_info, e.election_name, e.start_date
                    ORDER BY 
                        hr.representative_info, e.start_date;
                """;
                break;
    
            case "reso_participation":
                query = """
                    SELECT
                        hr.representative_info AS Representative,
                        COUNT(DISTINCT rv.reso_vote_id) AS Total_Resolutions_Participated,
                        SUM(CASE WHEN rv.reso_chosen_vote = 'Yes' THEN 1 ELSE 0 END) AS Votes_Yes,
                        SUM(CASE WHEN rv.reso_chosen_vote = 'No' THEN 1 ELSE 0 END) AS Votes_No,
                        SUM(CASE WHEN rv.reso_chosen_vote IS NULL THEN 1 ELSE 0 END) AS Votes_Abstain,
                        hr.is_disqualified AS Disqualification_Status
                    FROM 
                        resovotingrecords rv
                    JOIN 
                        resolutions r ON rv.reso_vote_id = r.resolution_id
                    JOIN 
                        homeownerreps hr ON rv.reso_representative_id = hr.representative_info
                    WHERE 
                        r.ResolutionDate >= DATE_SUB(CURDATE(), INTERVAL ? YEAR)
                    GROUP BY 
                        hr.representative_info, hr.is_disqualified
                    ORDER BY 
                        Total_Resolutions_Participated DESC;
                """;
                break;
    
            case "homeelec_participation":
                isParameterized = false; // No placeholder, skip setting parameters
                query = """
                    SELECT		m.member_id, CONCAT(m.last_name, ", ", m.first_name) as complete_name, COUNT(m.member_id) as no_elections_participated
                    FROM		members m	LEFT JOIN	homeownerreps h	ON	h.representative_info = m.member_id
					LEFT JOIN	votingrecords v	ON	m.member_id = v.representative_id
					LEFT JOIN	elections e		ON	v.election_id3 = e.election_id
                    WHERE		e.election_date > DATE_SUB(CURDATE(), INTERVAL 5 YEAR)
                    GROUP BY	m.member_id, complete_name
                    ORDER BY	complete_name;
                """;
                break;
    
            case "cand_freq":
                query = """
                    SELECT c.candidate_position, COUNT(c2.candidate_info) AS no_candidates
                    FROM candidates c 
                    JOIN elections e ON c.election_id2 = e.election_id 
                    JOIN candidates c2 ON c.candidate_info = c2.candidate_info 
                    WHERE e.election_date >= DATE_SUB(CURDATE(), INTERVAL ? YEAR) 
                    GROUP BY c.candidate_position;
                """;
                break;
    
            case "homerep_disq":
                isParameterized = false; 
                query = """
                    SELECT
                        m.member_id,
                        CONCAT(m.last_name, ", ", m.first_name) AS complete_name,
                        h.is_disqualified,
                        h.disqualifiedDate
                    FROM 
                        homeownerreps h
                    JOIN 
                        members m ON h.representative_info = m.member_id
                    WHERE 
                        h.is_disqualified LIKE "%Disqualified%"
                        AND h.disqualifiedDate > DATE_SUB(CURDATE(), INTERVAL 5 YEAR)
                    ORDER BY 
                        complete_name;
                """;
                break;
    
            default:
                System.out.println("Invalid report type chosen.");
                return;
        }
    
        executeQuery(connection, query, years, isParameterized);
    }
    

    public void executeQuery(Connection connection, String query, int years, boolean isParameterized) {
        try (PreparedStatement statement = connection.prepareStatement(query)) {
    
            // Set the value of the parameter (years) if the query is parameterized
            if (isParameterized) {
                statement.setInt(1, years);
            }
    
            // Execute the query and process the results
            ResultSet resultSet = statement.executeQuery();
    
            // Print the results
            while (resultSet.next()) {
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(resultSet.getMetaData().getColumnName(i) + ": " + resultSet.getString(i) + " | ");
                }
                System.out.println();
            }
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
