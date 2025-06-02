package com.roncolatoandpedro.soulinstruments.main;

import com.roncolatoandpedro.soulinstruments.guice.AppModule;
import com.roncolatoandpedro.soulinstruments.ui.*;
import com.google.inject.Guice;
import com.google.inject.Injector;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());

        SwingUtilities.invokeLater(() {
            JanelaPrincipal janelaPrincipal = injector.getInstance(JanelaPrincipal.class);
            janelaPrincipal.setVisible(true);
        });


    }
}