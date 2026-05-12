package com.example.demo.config;

import com.example.demo.dao.AppDAO;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.sql.SQLException;

@Component
public class DatabaseInitializer {

    private final AppDAO appDAO = new AppDAO();

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationEvent() {
        try {
            System.out.println("Initializing Database Schema...");
            appDAO.setupDatabase();
            System.out.println("Database Schema Initialized Successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
