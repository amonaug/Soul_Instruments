/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.roncolatoandpedro.soulinstruments.ui;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO; // Usar a interface
import com.roncolatoandpedro.soulinstruments.dao.interfaces.InstrumentoDAO; // Usar a interface
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO; // Usar a interface
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Importe Optional
import java.util.logging.Level;
import java.util.logging.Logger; // Importe Logger

/**
 *
 * @author pedro
 */
public class atualizarGUI extends javax.swing.JFrame {

    private static final Logger logger = Logger.getLogger(atualizarGUI.class.getName()); // Usar Logger

    // As DAOs devem ser do tipo da interface, e injetadas via DAOFactory
    private final FornecedorDAO fornecedorDao; // Para o autocomplete
    private final ProdutoDAO produtoImpl;
    private final InstrumentoDAO instrumentoImpl;
    private final FornecedorDAO fornecedorImpl; // Para buscar o fornecedor no salvar/buscar produto

    private JList<FornecedorDTO> listaFornecedores;

    public atualizarGUI() throws SQLException {
        // Inicializa as DAOs usando a DAOFactory, que garante a conexão e as dependências
        this.fornecedorDao = DAOFactory.criarFornecedorDAO(); // Para o autocomplete
        this.produtoImpl = DAOFactory.criarProdutoDAO();
        this.instrumentoImpl = DAOFactory.criarInstrumentoDAO(); // Adicionado InstrumentoDAO
        this.fornecedorImpl = DAOFactory.criarFornecedorDAO(); // Pode ser a mesma instância que fornecedorDao se desejar

        initComponents();
        configurarAutoComplete();
    }

    private void configurarAutoComplete() {
        // Criar e configurar a lista de fornecedores
        listaFornecedores = new JList<>();
        listaFornecedores.setBackground(new Color(22, 21, 27));
        listaFornecedores.setForeground(Color.WHITE);
        listaFornecedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Criar scroll pane para a lista
        JScrollPane scrollPane = new JScrollPane(listaFornecedores);
        scrollPane.setPreferredSize(new Dimension(215, 150));

        // Adicionar scroll pane ao popup menu
        jPopupMenu1.removeAll(); // Limpar items existentes
        jPopupMenu1.add(scrollPane);

        // Adicionar documento listener ao campo de texto
        txtFornecedor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { mostrarSugestoes(); }
            @Override
            public void removeUpdate(DocumentEvent e) { mostrarSugestoes(); }
            @Override
            public void changedUpdate(DocumentEvent e) { mostrarSugestoes(); }
        });

        // Adicionar seleção listener à lista
        listaFornecedores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                FornecedorDTO selecionado = listaFornecedores.getSelectedValue();
                if (selecionado != null) {
                    txtFornecedor.setText(selecionado.getNomeFornecedor());
                    jPopupMenu1.setVisible(false);
                }
            }
        });
    }

    private void mostrarSugestoes() {
        SwingUtilities.invokeLater(() -> {
            String texto = txtFornecedor.getText();

            if (texto.isEmpty()) {
                jPopupMenu1.setVisible(false);
                return;
            }

            try { // Adicionado try-catch, pois FornecedorDAO.buscarFornecedoresPorNome lança SQLException
                // Buscar fornecedores que correspondem ao texto
                List<FornecedorDTO> fornecedores = fornecedorDao.buscarFornecedoresPorNome(texto);
                DefaultListModel<FornecedorDTO> modelo = new DefaultListModel<>();

                for (FornecedorDTO fornecedor : fornecedores) {
                    modelo.addElement(fornecedor);
                }

                listaFornecedores.setModel(modelo);

                if (modelo.isEmpty()) {
                    jPopupMenu1.setVisible(false);
                    return;
                }

                // Ajustar tamanho do popup baseado no número de itens
                int altura = Math.min(modelo.getSize() * 25, 150);
                listaFornecedores.setPreferredSize(new Dimension(
                        txtFornecedor.getWidth(),
                        altura
                ));

                // Mostrar popup se não estiver visível
                if (!jPopupMenu1.isVisible()) {
                    jPopupMenu1.show(txtFornecedor, 0, txtFornecedor.getHeight());
                }

            } catch (SQLException e) { // Captura SQLException
                logger.log(Level.SEVERE, "Erro ao buscar fornecedores para autocomplete: " + e.getMessage(), e);
                jPopupMenu1.setVisible(false);
                JOptionPane.showMessageDialog(this, "Erro ao buscar fornecedores: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        btnEstoque = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnEntradaSaida = new javax.swing.JButton();
        comBoxCategoria = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtQuantidade = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtMarca2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtFornecedor = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        txtPreco = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtModelo1 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnSalvar = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

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
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 942, Short.MAX_VALUE)
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

        comBoxCategoria.setBackground(new java.awt.Color(21, 22, 27));
        comBoxCategoria.setForeground(new java.awt.Color(255, 255, 255));
        comBoxCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SOPRO", "CORDAS", "PERCUSSAO", "ELETRONICO" }));
        comBoxCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comBoxCategoriaActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(21, 22, 27));
        jLabel4.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("CATEGORIA");

        jLabel5.setBackground(new java.awt.Color(21, 22, 27));
        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("NOME");

        txtQuantidade.setBackground(new java.awt.Color(22, 21, 27));
        txtQuantidade.setForeground(new java.awt.Color(255, 255, 255));
        txtQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(21, 22, 27));
        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("MARCA");

        jLabel7.setBackground(new java.awt.Color(21, 22, 27));
        jLabel7.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));

        jLabel8.setBackground(new java.awt.Color(21, 22, 27));
        jLabel8.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));

        txtMarca2.setBackground(new java.awt.Color(22, 21, 27));
        txtMarca2.setForeground(new java.awt.Color(255, 255, 255));
        txtMarca2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMarca2ActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(21, 22, 27));
        jLabel10.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("PRECO");

        txtFornecedor.setBackground(new java.awt.Color(22, 21, 27));
        txtFornecedor.setForeground(new java.awt.Color(255, 255, 255));
        txtFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFornecedorActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(21, 22, 27));
        jLabel11.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("FORNECEDOR");

        txtPreco.setBackground(new java.awt.Color(22, 21, 27));
        txtPreco.setForeground(new java.awt.Color(255, 255, 255));
        txtPreco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPrecoActionPerformed(evt);
            }
        });

        jLabel12.setBackground(new java.awt.Color(21, 22, 27));
        jLabel12.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("DESCRICAO");

        txtDescricao.setBackground(new java.awt.Color(22, 21, 27));
        txtDescricao.setForeground(new java.awt.Color(255, 255, 255));
        txtDescricao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtDescricaoActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(21, 22, 27));
        jLabel9.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("MODELO");

        txtNome.setBackground(new java.awt.Color(22, 21, 27));
        txtNome.setForeground(new java.awt.Color(255, 255, 255));
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(21, 22, 27));
        jLabel13.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("QUANTIDADE");

        txtModelo1.setBackground(new java.awt.Color(22, 21, 27));
        txtModelo1.setForeground(new java.awt.Color(255, 255, 255));
        txtModelo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtModelo1ActionPerformed(evt);
            }
        });

        jLabel14.setBackground(new java.awt.Color(21, 22, 27));
        jLabel14.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("ID");

        txtId.setBackground(new java.awt.Color(22, 21, 27));
        txtId.setForeground(new java.awt.Color(255, 255, 255));
        txtId.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtIdActionPerformed(evt);
            }
        });

        btnBuscar.setBackground(new java.awt.Color(202, 207, 214));
        btnBuscar.setForeground(new java.awt.Color(4, 138, 129));
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnSalvar.setBackground(new java.awt.Color(202, 207, 214));
        btnSalvar.setForeground(new java.awt.Color(4, 138, 129));
        btnSalvar.setText("SALVAR");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                try {
                    btnSalvarActionPerformed(evt);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(44, 44, 44)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(jLabel12)
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(txtNome, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtPreco, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                                                        .addComponent(jLabel11, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtFornecedor, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
                                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(comBoxCategoria, javax.swing.GroupLayout.Alignment.LEADING, 0, 215, Short.MAX_VALUE)
                                                        .addComponent(jLabel10, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtMarca2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE))
                                                .addGap(91, 91, 91)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel7)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(jLabel8))
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(jLabel9)
                                                                                                .addComponent(txtModelo1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                                        .addGap(271, 271, 271)
                                                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                                .addComponent(jLabel14)
                                                                                                .addGroup(jPanel1Layout.createSequentialGroup()
                                                                                                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                        .addGap(56, 56, 56)
                                                                                                        .addComponent(btnBuscar)))))
                                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                                .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.LEADING)
                                                                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                                                .addContainerGap())))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(102, 102, 102)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel9)
                                        .addComponent(jLabel14))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(comBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtModelo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar))
                                .addGap(23, 23, 23)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtMarca2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(jLabel8)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGap(22, 22, 22)
                                                .addComponent(jLabel7))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtPreco, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(235, 235, 235))
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
                                .addContainerGap(572, Short.MAX_VALUE))
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {
        // Para uma melhor navegação em Swing, dispose da janela atual
        // antes de abrir a nova.
        this.dispose();
        java.awt.EventQueue.invokeLater(() -> new janelaPrincipalGUI().setVisible(true));
    }

    private void btnEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstoqueActionPerformed
        this.dispose(); // Dispose da janela atual
        java.awt.EventQueue.invokeLater(() -> new estoqueGUI().setVisible(true));
    }//GEN-LAST:event_btnEstoqueActionPerformed

    private void btnAtualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAtualizarActionPerformed
        // Se já está na tela de atualização, não precisa abrir uma nova.
        // Se a intenção é recarregar, pode adicionar uma lógica de recarga aqui.
        // Por enquanto, apenas logs.
        logger.log(Level.INFO, "Já está na tela de atualização.");
    }//GEN-LAST:event_btnAtualizarActionPerformed

    private void btnEntradaSaidaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEntradaSaidaActionPerformed
        this.dispose(); // Dispose da janela atual
        java.awt.EventQueue.invokeLater(() -> new entradaSaidaGUI().setVisible(true));
    }//GEN-LAST:event_btnEntradaSaidaActionPerformed

    private void comBoxCategoriaActionPerformed(java.awt.event.ActionEvent evt) {
        // Pode adicionar lógica aqui se a seleção da categoria afetar outros campos
    }//GEN-LAST:event_comBoxCategoriaActionPerformed

    private void txtQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtQuantidade (se necessário)
    }//GEN-LAST:event_txtQuantidadeActionPerformed

    private void txtMarca2ActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtMarca2 (se necessário)
    }//GEN-LAST:event_txtMarca2ActionPerformed

    private void txtFornecedorActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtFornecedor (se necessário)
    }//GEN-LAST:event_txtFornecedorActionPerformed

    private void txtPrecoActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtPreco (se necessário)
    }//GEN-LAST:event_txtPrecoActionPerformed

    private void txtDescricaoActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtDescricao (se necessário)
    }//GEN-LAST:event_txtDescricaoActionPerformed

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtNome (se necessário)
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtModelo1ActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtModelo1 (se necessário)
    }//GEN-LAST:event_txtModelo1ActionPerformed

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {
        // Lógica de ação para o campo txtId (se necessário)
    }//GEN-LAST:event_txtIdActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String idText = txtId.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID de produto para buscar.", "Campo Vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long id = Long.parseLong(idText);
            // Busca o produto usando Optional
            Optional<ProdutoDTO> produtoOpt = produtoImpl.buscarPorId(id);

            if (produtoOpt.isPresent()) {
                ProdutoDTO produto = produtoOpt.get();
                // Busca o instrumento usando Optional
                Optional<InstrumentoDTO> instrumentoOpt = instrumentoImpl.buscarPorId(produto.getIdInstrumento());
                // Busca o fornecedor usando Optional
                Optional<FornecedorDTO> fornecedorOpt = fornecedorImpl.buscarPorId(produto.getIdFornecedor());

                // Preenche os campos da GUI
                txtNome.setText(instrumentoOpt.isPresent() ? instrumentoOpt.get().getNome() : "");
                comBoxCategoria.setSelectedItem(instrumentoOpt.isPresent() ? instrumentoOpt.get().getCategoria().name() : "SOPRO"); // Default se não encontrado
                txtModelo1.setText(produto.getModelo());
                txtQuantidade.setText(String.valueOf(produto.getQuantidadeEstoque()));
                txtMarca2.setText(produto.getMarca());
                txtFornecedor.setText(fornecedorOpt.isPresent() ? fornecedorOpt.get().getNomeFornecedor() : ""); // Preenche com nome do fornecedor
                txtPreco.setText(String.valueOf(produto.getPreco()));
                txtDescricao.setText(produto.getDescricao());

                JOptionPane.showMessageDialog(this, "Produto encontrado e campos preenchidos!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

            } else {
                JOptionPane.showMessageDialog(this, "Produto com ID " + id + " não encontrado.", "Produto Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
                limparCamposProduto(); // Limpa os campos se o produto não for encontrado
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido. Por favor, insira um número.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, "Erro de formato ao buscar produto: " + e.getMessage(), e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao buscar produto no banco de dados: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Erro SQL ao buscar produto: " + e.getMessage(), e);
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao buscar o produto: " + e.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Erro inesperado ao buscar produto: " + e.getMessage(), e);
        }
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) throws SQLException {
        String idText = txtId.getText();
        if (idText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "ID do produto não pode estar vazio para salvar.", "Campo Vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long id = Long.parseLong(idText);
            Optional<ProdutoDTO> produtoOpt = produtoImpl.buscarPorId(id); // Usa Optional

            if (produtoOpt.isEmpty()) { // Verifica se o produto existe
                JOptionPane.showMessageDialog(this, "Produto com ID " + id + " não encontrado para atualização. Por favor, busque o produto primeiro.", "Produto Não Encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }
            ProdutoDTO produto = produtoOpt.get(); // Obtém o produto do Optional

            // Validações de entrada de dados
            if (txtNome.getText().isEmpty() || txtModelo1.getText().isEmpty() ||
                    txtQuantidade.getText().isEmpty() || txtMarca2.getText().isEmpty() ||
                    txtFornecedor.getText().isEmpty() || txtPreco.getText().isEmpty() ||
                    txtDescricao.getText().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos os campos devem ser preenchidos.", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Busca fornecedor pelo nome. FornecedorDAO.buscarFornecedoresPorNome retorna List<FornecedorDTO>
            String nomeFornecedor = txtFornecedor.getText();
            List<FornecedorDTO> fornecedoresEncontrados = fornecedorImpl.buscarFornecedoresPorNome(nomeFornecedor);

            FornecedorDTO fornecedor = null;
            if (fornecedoresEncontrados != null && !fornecedoresEncontrados.isEmpty()) {
                // Tenta encontrar uma correspondência exata para o nome do fornecedor para evitar ambiguidades
                for (FornecedorDTO f : fornecedoresEncontrados) {
                    if (f.getNomeFornecedor().equalsIgnoreCase(nomeFornecedor)) {
                        fornecedor = f;
                        break;
                    }
                }
            }

            if (fornecedor == null) {
                JOptionPane.showMessageDialog(this, "Fornecedor '" + nomeFornecedor + "' não encontrado ou correspondência exata não encontrada. Por favor, selecione um fornecedor válido ou adicione-o.", "Fornecedor Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Atualiza os dados do DTO do Produto
            produto.setDescricao(txtDescricao.getText());
            produto.setMarca(txtMarca2.getText());
            produto.setPreco(Double.parseDouble(txtPreco.getText()));
            produto.setQuantidadeEstoque(Integer.parseInt(txtQuantidade.getText()));
            produto.setModelo(txtModelo1.getText());
            produto.setIdFornecedor(fornecedor.getId());

            // Buscar Instrumento pelo nome e atualizar o idInstrumento no ProdutoDTO
            String nomeInstrumento = txtNome.getText();
            Optional<InstrumentoDTO> instrumentoOpt = instrumentoImpl.buscarPorNome(nomeInstrumento); // Usa Optional

            if (instrumentoOpt.isEmpty()) { // Verifica se o instrumento existe
                JOptionPane.showMessageDialog(this, "Instrumento '" + nomeInstrumento + "' não encontrado. Por favor, insira um nome de instrumento existente.", "Instrumento Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            InstrumentoDTO instrumento = instrumentoOpt.get(); // Obtém o instrumento do Optional

            // Atualiza o ID do instrumento no DTO do produto
            produto.setIdInstrumento(instrumento.getIdInstrumento());

            // Realiza a atualização no banco de dados
            produtoImpl.atualizar(produto);

            JOptionPane.showMessageDialog(this, "Produto atualizado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Erro de formato em Preço ou Quantidade. Verifique se são números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, "Erro de formato ao salvar produto: " + e.getMessage(), e);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Erro ao atualizar produto no banco de dados: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Erro SQL ao salvar produto: " + e.getMessage(), e);
        } catch (Exception e) {
            // Captura outras exceções inesperadas
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao salvar o produto: " + e.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Erro inesperado ao salvar produto: " + e.getMessage(), e);
        }
    }

    private void limparCamposProduto() {
        txtNome.setText("");
        comBoxCategoria.setSelectedItem("SOPRO");
        txtModelo1.setText("");
        txtQuantidade.setText("");
        txtMarca2.setText("");
        txtFornecedor.setText("");
        txtPreco.setText("");
        txtDescricao.setText("");
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEntradaSaida;
    private javax.swing.JButton btnEstoque;
    private javax.swing.JButton btnHome;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<String> comBoxCategoria;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtFornecedor;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtMarca2;
    private javax.swing.JTextField txtModelo1;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPreco;
    private javax.swing.JTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}
