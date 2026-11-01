
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL =
        "jdbc:mysql://localhost:3306/library_db";

    private static final String USER = "root";

   private static final String PASSWORD = "YOUR_PASSWORD";
    public static Connection getConnection() {

        Connection connection = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            connection = DriverManager.getConnection(
                URL, USER, PASSWORD
            );

            System.out.println("Database Connected Successfully!");

        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver Not Found!");
            e.printStackTrace();

        } catch (SQLException e) {
            System.out.println("Database Connection Failed!");
            e.printStackTrace();
        }

        return connection;
    }

    public static void main(String[] args) {

        Connection connection = getConnection();

        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection Closed Successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
