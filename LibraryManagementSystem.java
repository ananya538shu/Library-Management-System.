
import java.sql.*;
import java.util.Scanner;

public class LibraryManagementSystem {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try (Connection con = DatabaseConnection.getConnection()) {

            if (con == null) {
                System.out.println("Please check database connection.");
                return;
            }

            createTables(con);

            while (true) {
                System.out.println("\n================================");
                System.out.println("     LIBRARY MANAGEMENT SYSTEM");
                System.out.println("================================");
                System.out.println("1. View All Books");
                System.out.println("2. Add New Book");
                System.out.println("3. Search Book");
                System.out.println("4. Update Book");
                System.out.println("5. Delete Book");
                System.out.println("6. Register Member");
                System.out.println("7. View Members");
                System.out.println("8. Issue Book");
                System.out.println("9. Return Book");
                System.out.println("10. View Issue Records");
                System.out.println("0. Exit");
                System.out.print("Enter your choice: ");

                int choice = readInt();

                switch (choice) {
                    case 1: viewBooks(con); break;
                    case 2: addBook(con); break;
                    case 3: searchBook(con); break;
                    case 4: updateBook(con); break;
                    case 5: deleteBook(con); break;
                    case 6: registerMember(con); break;
                    case 7: viewMembers(con); break;
                    case 8: issueBook(con); break;
                    case 9: returnBook(con); break;
                    case 10: viewIssueRecords(con); break;
                    case 0:
                        System.out.println("Thank you for using Library Management System!");
                        return;
                    default:
                        System.out.println("Invalid choice. Try again.");
                }
            }

        } catch (SQLException e) {
            System.out.println("Database Error!");
            e.printStackTrace();
        }
    }

    static int readInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Enter a valid number: ");
            sc.nextLine();
        }
        int value = sc.nextInt();
        sc.nextLine();
        return value;
    }

    static void createTables(Connection con) throws SQLException {

        String members = """
            CREATE TABLE IF NOT EXISTS members (
                id INT AUTO_INCREMENT PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                email VARCHAR(100) UNIQUE,
                phone VARCHAR(20)
            )
            """;

        String issues = """
            CREATE TABLE IF NOT EXISTS issued_books (
                id INT AUTO_INCREMENT PRIMARY KEY,
                book_id INT NOT NULL,
                member_id INT NOT NULL,
                issue_date DATE NOT NULL,
                return_date DATE,
                status VARCHAR(20) DEFAULT 'ISSUED',
                FOREIGN KEY (book_id) REFERENCES books(id),
                FOREIGN KEY (member_id) REFERENCES members(id)
            )
            """;

        try (Statement stmt = con.createStatement()) {
            stmt.execute(members);
            stmt.execute(issues);
        }
    }

    static void viewBooks(Connection con) throws SQLException {

        String sql = "SELECT * FROM books";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n========== BOOK LIST ==========");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    "ID: " + rs.getInt("id")
                    + " | Title: " + rs.getString("title")
                    + " | Author: " + rs.getString("author")
                );
            }

            if (!found) System.out.println("No books available.");
        }
    }

    static void addBook(Connection con) throws SQLException {

        System.out.print("Enter book title: ");
        String title = sc.nextLine();

        System.out.print("Enter author name: ");
        String author = sc.nextLine();

        String sql = "INSERT INTO books (title, author) VALUES (?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);

            ps.executeUpdate();
            System.out.println("Book added successfully!");
        }
    }

    static void searchBook(Connection con) throws SQLException {

        System.out.print("Enter Book ID: ");
        int id = readInt();

        String sql = "SELECT * FROM books WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("Book Found!");
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Title: " + rs.getString("title"));
                    System.out.println("Author: " + rs.getString("author"));
                } else {
                    System.out.println("Book not found.");
                }
            }
        }
    }

    static void updateBook(Connection con) throws SQLException {

        System.out.print("Enter Book ID to update: ");
        int id = readInt();

        System.out.print("Enter new title: ");
        String title = sc.nextLine();

        System.out.print("Enter new author: ");
        String author = sc.nextLine();

        String sql = "UPDATE books SET title = ?, author = ? WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                ? "Book updated successfully!"
                : "Book ID not found.");
        }
    }

    static void deleteBook(Connection con) throws SQLException {

        System.out.print("Enter Book ID to delete: ");
        int id = readInt();

        String check = """
            SELECT COUNT(*) FROM issued_books
            WHERE book_id = ? AND status = 'ISSUED'
            """;

        try (PreparedStatement ps = con.prepareStatement(check)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();

                if (rs.getInt(1) > 0) {
                    System.out.println("Return this book before deleting it.");
                    return;
                }
            }
        }

        String sql = "DELETE FROM books WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            System.out.println(rows > 0
                ? "Book deleted successfully!"
                : "Book ID not found.");
        }
    }

    static void registerMember(Connection con) throws SQLException {

        System.out.print("Enter member name: ");
        String name = sc.nextLine();

        System.out.print("Enter email: ");
        String email = sc.nextLine();

        System.out.print("Enter phone: ");
        String phone = sc.nextLine();

        String sql =
            "INSERT INTO members (name, email, phone) VALUES (?, ?, ?)";

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, phone);

            ps.executeUpdate();
            System.out.println("Member registered successfully!");
        }
    }

    static void viewMembers(Connection con) throws SQLException {

        String sql = "SELECT * FROM members";

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n========== MEMBER LIST ==========");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    "ID: " + rs.getInt("id")
                    + " | Name: " + rs.getString("name")
                    + " | Email: " + rs.getString("email")
                    + " | Phone: " + rs.getString("phone")
                );
            }

            if (!found) System.out.println("No members registered.");
        }
    }

    static void issueBook(Connection con) throws SQLException {

        System.out.print("Enter Book ID: ");
        int bookId = readInt();

        System.out.print("Enter Member ID: ");
        int memberId = readInt();

        String bookCheck = "SELECT id FROM books WHERE id = ?";
        String memberCheck = "SELECT id FROM members WHERE id = ?";

        try (PreparedStatement ps = con.prepareStatement(bookCheck)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Book not found.");
                    return;
                }
            }
        }

        try (PreparedStatement ps = con.prepareStatement(memberCheck)) {
            ps.setInt(1, memberId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    System.out.println("Member not found.");
                    return;
                }
            }
        }

        String activeCheck = """
            SELECT id FROM issued_books
            WHERE book_id = ? AND status = 'ISSUED'
            """;

        try (PreparedStatement ps = con.prepareStatement(activeCheck)) {
            ps.setInt(1, bookId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    System.out.println("This book is already issued.");
                    return;
                }
            }
        }

        String sql = """
            INSERT INTO issued_books
            (book_id, member_id, issue_date, status)
            VALUES (?, ?, CURDATE(), 'ISSUED')
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, bookId);
            ps.setInt(2, memberId);
            ps.executeUpdate();

            System.out.println("Book issued successfully!");
        }
    }

    static void returnBook(Connection con) throws SQLException {

        System.out.print("Enter Issue Record ID: ");
        int issueId = readInt();

        String sql = """
            UPDATE issued_books
            SET status = 'RETURNED', return_date = CURDATE()
            WHERE id = ? AND status = 'ISSUED'
            """;

        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, issueId);

            int rows = ps.executeUpdate();

            System.out.println(rows > 0
                ? "Book returned successfully!"
                : "Active issue record not found.");
        }
    }

    static void viewIssueRecords(Connection con) throws SQLException {

        String sql = """
            SELECT i.id, b.title, m.name,
                   i.issue_date, i.return_date, i.status
            FROM issued_books i
            JOIN books b ON i.book_id = b.id
            JOIN members m ON i.member_id = m.id
            ORDER BY i.id DESC
            """;

        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            System.out.println("\n========== ISSUE RECORDS ==========");

            boolean found = false;

            while (rs.next()) {
                found = true;
                System.out.println(
                    "Issue ID: " + rs.getInt("id")
                    + " | Book: " + rs.getString("title")
                    + " | Member: " + rs.getString("name")
                    + " | Issued: " + rs.getDate("issue_date")
                    + " | Returned: " + rs.getDate("return_date")
                    + " | Status: " + rs.getString("status")
                );
            }

            if (!found) System.out.println("No issue records found.");
        }
    }
}
