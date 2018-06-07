package JavaServer;
import java.sql.*;

public class PostgreSQLJDBC {

    private Connection c;

    public PostgreSQLJDBC() {
        connectToDB();
        try {
            Statement init = c.createStatement();
            String create = "CREATE TABLE IF NOT EXISTS user " +
                    "(id SERIAL PRIMARY KEY NOT NULL," +
                    "username VARCHAR(20) PRIMARY KEY NOT NULL," +
                    "password VARCHAR(128) NOT NULL," +
                    "ip VARCHAR(80))";
            init.executeUpdate(create);
            System.out.println("Created table succesfully");
            c.close();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
    }

    public void connectToDB() {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://db.intern.mi.hs-rm.de:5432/nhafk001_chatApp");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
        }
        System.out.println("Opened database successfully");
    }

    public void insert(String username, String password, String ip) throws SQLException {
        connectToDB();
        String sql = "INSERT INTO user(username, password, ip)" +
                "VALUES(?, ?, ?)";
        PreparedStatement pstmt = c.prepareStatement( sql );
        pstmt.setString( 1, username);
        pstmt.setString( 2, password);
        pstmt.setString( 3, ip);
        ResultSet results = pstmt.executeQuery(sql);
        pstmt.close();
        System.out.println("Inserted values successfully");
        c.close();
    }

    /**
     * selects a user from the database with a given username
     * @param username
     * @return if exists, one row with user information
     * @throws SQLException if no user was found
     */
    public ResultSet getUser(String username) throws SQLException {
        connectToDB();
        String sql = "SELECT * FROM user WHERE username = ?";
        PreparedStatement pstmt = c.prepareStatement( sql );
        pstmt.setString( 1, username);
        ResultSet results = pstmt.executeQuery(sql);
        pstmt.close();
        System.out.println("Got user data successfully");
        c.close();
        return results;
    }

    public boolean updateUser(String username, int ip) {
        connectToDB();
        try {
            String sql = "UPDATE user SET ip = ? WHERE username = ?";
            PreparedStatement pstmt = c.prepareStatement( sql );
            pstmt.setInt( 1, ip);
            pstmt.setString( 2, username);
            ResultSet results = pstmt.executeQuery(sql);
            pstmt.close();
            System.out.println("Updated ip successfully");
            c.close();
            return true;
        } catch (SQLException e) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            return false;
        }
    }

}
