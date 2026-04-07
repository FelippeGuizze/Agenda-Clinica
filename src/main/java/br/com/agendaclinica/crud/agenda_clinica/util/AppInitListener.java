package br.com.agendaclinica.crud.agenda_clinica.util;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("═════════════════════════════════════════════");
        System.out.println("  Iniciando Agenda Clínica...");
        System.out.println("═════════════════════════════════════════════");
        
        // Executar seed do banco de dados
        SeedService.seedDatabase();
        
        System.out.println("  Aplicação iniciada com sucesso!");
        System.out.println("═════════════════════════════════════════════");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("═════════════════════════════════════════════");
        System.out.println("  Encerrando Agenda Clínica...");
        System.out.println("═════════════════════════════════════════════");
    }
}
