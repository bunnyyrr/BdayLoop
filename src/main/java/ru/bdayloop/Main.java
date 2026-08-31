package ru.bdayloop;

import ru.bdayloop.db.ConnectionManager;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main (String[] args){
        try (Connection connection = ConnectionManager.getConnection()){
            System.out.println("Подключились:" + !connection.isClosed());
        } catch(SQLException e){
            e.printStackTrace();
        }
    }
}
