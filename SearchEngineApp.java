import java.util.List;
import java.util.Scanner;

public class SearchEngineApp {
    public static void main(String[] args) {
        QueryProcessor qp = new QueryProcessor();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter your search query:");
        String query = scanner.nextLine();
        List<String> results = qp.search(query);
        if (results.isEmpty()) {
            System.out.println("No results found.");
        } else {
            System.out.println("Search Results:");
            for (String result : results) {
                System.out.println(result);
            }
        }
        scanner.close();
    }
}
