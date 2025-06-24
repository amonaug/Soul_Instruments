/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.roncolatoandpedro.soulinstruments.ui;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.impl.InstrumentoDAOImpl; // Importe InstrumentoDAOImpl
import com.roncolatoandpedro.soulinstruments.dao.impl.ProdutoDAOImpl;
import com.roncolatoandpedro.soulinstruments.dto.Categoria; // Importe Categoria
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO; // Importe InstrumentoDTO
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel; // Importe DefaultTableModel
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector; // Para uso com DefaultTableModel
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author pedro
 */
public class estoqueGUI extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(estoqueGUI.class.getName());

    private ProdutoDAOImpl produtoImpl;
    private InstrumentoDAOImpl instrumentoImpl; // Adiciona InstrumentoDAOImpl

    /**
     * Creates new form estoqueGUI
     */
    public estoqueGUI() {
        initComponents();
        try {
            // Inicializa as DAOs usando a fábrica de conexões
            Connection connection = DAOFactory.getConexao();
            this.produtoImpl = new ProdutoDAOImpl(connection);
            this.instrumentoImpl = new InstrumentoDAOImpl(connection);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao inicializar DAOs para estoque: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar ao banco de dados para gerenciar estoque: " + e.getMessage(),
                    "Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);
            // Se a conexão for crítica, é melhor sair da aplicação ou desabilitar funcionalidades.
            // System.exit(1);
        }

        configurarComponentesIniciais();
        carregarProdutosNaTabela(null, null); // Carrega todos os produtos inicialmente
    }

    /**
     * Configura JComboBox e outras configurações iniciais.
     */
    private void configurarComponentesIniciais() {
        // Popula o ComboBox de Categoria
        comBoxCategoria.removeAllItems();
        comBoxCategoria.addItem("TODAS"); // Adiciona uma opção para "Todas as Categorias"
        for (Categoria categoria : Categoria.values()) {
            comBoxCategoria.addItem(categoria.name());
        }
        comBoxCategoria.setSelectedIndex(0); // Seleciona "TODAS" por padrão

        // Configura o modelo da tabela
        DefaultTableModel model = new DefaultTableModel(
                new Object [][] {},
                new String [] { "ID", "Nome", "Categoria", "Marca", "Preço", "Quantidade", "Modelo", "Descrição" }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
        };
        jTable1.setModel(model);

        // Opcional: Ajustar largura das colunas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(50); // ID
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150); // Nome
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100); // Categoria
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(100); // Marca
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);  // Preço
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(80);  // Quantidade
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(120); // Modelo
        jTable1.getColumnModel().getColumn(7).setPreferredWidth(200); // Descrição
    }

    /**
     * Carrega os produtos na tabela, com opção de filtro.
     * @param nomeFiltro Nome do produto para filtrar (pode ser null para não filtrar).
     * @param categoriaFiltro Categoria do produto para filtrar (pode ser null para não filtrar).
     */
    private void carregarProdutosNaTabela(String nomeFiltro, String categoriaFiltro) {
        if (produtoImpl == null || instrumentoImpl == null) {
            logger.log(Level.SEVERE, "DAOs não inicializadas. Não é possível carregar produtos.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Limpa as linhas existentes

        try {
            List<ProdutoDTO> produtos = produtoImpl.listarTodos(); // Busca todos os produtos

            for (ProdutoDTO produto : produtos) {
                // Busca o instrumento para obter Nome e Categoria
                InstrumentoDTO instrumento = instrumentoImpl.buscarPorId(produto.getIdInstrumento());

                String nomeProduto = (instrumento != null) ? instrumento.getNome() : "N/A";
                String categoriaProduto = (instrumento != null) ? instrumento.getCategoria().name() : "N/A";

                // Aplica os filtros
                boolean passaNoNome = (nomeFiltro == null || nomeProduto.toLowerCase().contains(nomeFiltro.toLowerCase()));
                boolean passaNaCategoria = (categoriaFiltro == null || "TODAS".equals(categoriaFiltro) || categoriaProduto.equalsIgnoreCase(categoriaFiltro));

                if (passaNoNome && passaNaCategoria) {
                    Vector<Object> row = new Vector<>();
                    row.add(produto.getIdProduto());
                    row.add(nomeProduto);
                    row.add(categoriaProduto);
                    row.add(produto.getMarca());
                    row.add(produto.getPreco());
                    row.add(produto.getQuantidadeEstoque());
                    row.add(produto.getModelo());
                    row.add(produto.getDescricao());
                    model.addRow(row);
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao carregar produtos na tabela: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Erro ao carregar produtos: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ocorreu um erro inesperado ao carregar produtos: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextField1 = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        btnEstoque = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnEntradaSaida = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtNome = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        comBoxCategoria = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        jTextField1.setText("jTextField1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(46, 52, 59));

        jPanel4.setBackground(new java.awt.Color(21, 22, 27));

        jLabel1.setBackground(new java.awt.Color(64, 127, 190));
        jLabel1.setFont(new java.awt.Font("Liberation Mono", 1, 30)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(4, 138, 129));
        jLabel1.setText("SOUL INSTRUMENTS");

        btnHome.setBackground(new java.awt.Color(202, 207, 214));
        btnHome.setForeground(new java.awt.Color(4, 138, 129));
        btnHome.setText("HOME");
        btnHome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHomeActionPerformed(evt);
            }
        });

        btnEstoque.setBackground(new java.awt.Color(202, 207, 214));
        btnEstoque.setForeground(new java.awt.Color(4, 138, 129));
        btnEstoque.setText("ESTOQUE");
        btnEstoque.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEstoqueActionPerformed(evt);
            }
        });

        btnAtualizar.setBackground(new java.awt.Color(202, 207, 214));
        btnAtualizar.setForeground(new java.awt.Color(4, 138, 129));
        btnAtualizar.setText("ATUALIZAR");
        btnAtualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAtualizarActionPerformed(evt);
            }
        });

        btnEntradaSaida.setBackground(new java.awt.Color(202, 207, 214));
        btnEntradaSaida.setForeground(new java.awt.Color(4, 138, 129));
        btnEntradaSaida.setText("ENTRADA/SAIDA");
        btnEntradaSaida.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEntradaSaidaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 383, Short.MAX_VALUE)
                                .addComponent(btnHome)
                                .addGap(37, 37, 37)
                                .addComponent(btnEstoque)
                                .addGap(43, 43, 43)
                                .addComponent(btnAtualizar)
                                .addGap(32, 32, 32)
                                .addComponent(btnEntradaSaida)
                                .addGap(130, 130, 130))
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addGap(19, 19, 19)
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                .addComponent(btnHome)
                                                .addComponent(btnEstoque)
                                                .addComponent(btnAtualizar)
                                                .addComponent(btnEntradaSaida)))
                                .addContainerGap(14, Short.MAX_VALUE))
        );

        jTable1.setBackground(new java.awt.Color(21, 22, 27));
        jTable1.setForeground(new java.awt.Color(251, 251, 255));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null}
                },
                new String [] {
                        "ID", "Nome", "Categoria", "Marca", "Preço", "Quantidade"
                }
        ));
        jScrollPane1.setViewportView(jTable1);
        if (jTable1.getColumnModel().getColumnCount() > 0) {
            jTable1.getColumnModel().getColumn(5).setResizable(false);
        }

        txtNome.setBackground(new java.awt.Color(22, 21, 27));
        txtNome.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(21, 22, 27));
        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("NOME");

        comBoxCategoria.setBackground(new java.awt.Color(21, 22, 27));
        comBoxCategoria.setForeground(new java.awt.Color(255, 255, 255));
        comBoxCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SOPRO", "CORDAS", "PERCUSSAO", "ELETRONICO" }));
        comBoxCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comBoxCategoriaActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(21, 22, 27));
        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("CATEGORIA");

        btnBuscar.setBackground(new java.awt.Color(4, 138, 129)); // Alterado cor de fundo para teal
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255)); // Alterado cor da fonte para branco
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(34, 34, 34)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(115, 115, 115)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(comBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE) // Tamanho ajustado
                                                                .addGap(139, 139, 139))))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 75, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(61, 61, 61))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 100, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(1192, 1192, 1192)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 209, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        this.dispose(); // Fecha a janela atual
        java.awt.EventQueue.invokeLater(() -> new janelaPrincipalGUI().setVisible(true));
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstoqueActionPerformed
        // Já está na tela de estoque, não precisa abrir uma nova.
        logger.log(Level.INFO, "Já está na tela de Estoque.");
    }//GEN-LAST:event_btnEstoqueActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        this.dispose(); // Fecha a janela atual
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new atualizarGUI().setVisible(true);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao abrir tela de atualização: " + e.getMessage(), e);
                JOptionPane.showMessageDialog(this, "Erro ao abrir tela de atualização: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnEntradaSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntradaSaidaActionPerformed
        this.dispose(); // Fecha a janela atual
        java.awt.EventQueue.invokeLater(() -> new entradaSaidaGUI().setVisible(true));
    }//GEN-LAST:event_btnEntradaSaidaActionPerformed

    private void comBoxCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comBoxCategoriaActionPerformed
        // Quando a categoria muda, atualiza a tabela com o filtro
        btnBuscarActionPerformed(evt); // Chama o método de busca para aplicar o filtro
    }//GEN-LAST:event_comBoxCategoriaActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String nomeFiltro = txtNome.getText().trim();
        String categoriaFiltro = (String) comBoxCategoria.getSelectedItem();

        // Se o campo de nome estiver vazio, passa null para o filtro de nome
        if (nomeFiltro.isEmpty()) {
            nomeFiltro = null;
        }

        // Se "TODAS" for selecionado na categoria, passa null para o filtro de categoria
        if ("TODAS".equals(categoriaFiltro)) {
            categoriaFiltro = null;
        }

        carregarProdutosNaTabela(nomeFiltro, categoriaFiltro);
    }//GEN-LAST:event_btnBuscarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(estoqueGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(estoqueGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(estoqueGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(estoqueGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new estoqueGUI().setVisible(true);
                } catch (Exception e) { // Captura qualquer exceção durante a inicialização
                    logger.log(Level.SEVERE, "Erro crítico ao iniciar a tela de estoque: " + e.getMessage(), e);
                    JOptionPane.showMessageDialog(null, "Erro crítico ao iniciar a tela de estoque: " + e.getMessage(), "Erro de Inicialização", JOptionPane.ERROR_MESSAGE);
                    System.exit(1); // Encerra a aplicação
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEntradaSaida;
    private javax.swing.JButton btnEstoque;
    private javax.swing.JButton btnHome;
    private javax.swing.JComboBox<String> comBoxCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField txtNome;
    // End of variables declaration//GEN-END:variables
}
