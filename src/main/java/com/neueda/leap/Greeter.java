package com.neueda.leap;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class Greeter {
    public String greet(String name) {
        return "Good day, " + name;
    }

    public ResultSet getUserByUsername(Connection conn, String username) throws Exception {
        // SQL Injection vulnerability: user input directly concatenated into query
        String query = "SELECT * FROM users WHERE username = '" + username + "'";
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(query);
    }
}
