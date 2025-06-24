package com.roncolatoandpedro.soulinstruments;

// Importações necessárias para Swing e Look and Feel
import javax.swing.*;

// Importe a janela principal da sua UI
import com.roncolatoandpedro.soulinstruments.ui.janelaPrincipalGUI;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.SQLException; // Importe SQLException para o tratamento de erros se necessário

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
        // Define o Look and Feel para Nimbus, para uma aparência mais moderna e consistente
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            // Registra qualquer erro que ocorra ao definir o Look and Feel
            logger.log(Level.SEVERE, "Erro ao configurar o Look and Feel Nimbus", ex);
        }

        // Garante que a criação e manipulação da GUI ocorra na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            try {
                // Cria e exibe a janela principal da sua aplicação
                janelaPrincipalGUI mainFrame = new janelaPrincipalGUI();
                mainFrame.setVisible(true);
            } catch (Exception e) {
                // Captura qualquer exceção que possa ocorrer durante a inicialização da janela
                logger.log(Level.SEVERE, "Erro crítico ao iniciar a aplicação principal.", e);
                JOptionPane.showMessageDialog(null,
                        "Ocorreu um erro crítico ao iniciar a aplicação:\n" + e.getMessage(),
                        "Erro de Inicialização",
                        JOptionPane.ERROR_MESSAGE);
                System.exit(1); // Encerra a aplicação com um código de erro
            }
        });
    }
}
