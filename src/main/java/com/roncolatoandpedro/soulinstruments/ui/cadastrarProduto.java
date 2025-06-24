/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.roncolatoandpedro.soulinstruments.ui;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO; // Importe a interface FornecedorDAO
import com.roncolatoandpedro.soulinstruments.dao.interfaces.InstrumentoDAO; // Importe a interface InstrumentoDAO
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO; // Importe a interface ProdutoDAO
import com.roncolatoandpedro.soulinstruments.dto.Categoria;
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.roncolatoandpedro.soulinstruments.dto.InstrumentoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent; // Necessário para DocumentListener
import javax.swing.event.DocumentListener; // Necessário para DocumentListener
import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Importe Optional
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AmonA
 */
public class cadastrarProduto extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(cadastrarProduto.class.getName());

    // Instâncias das DAOs, usando as interfaces
    private ProdutoDAO produtoImpl;
    private InstrumentoDAO instrumentoImpl;
    private FornecedorDAO fornecedorImpl;

    // Para o autocomplete de fornecedores
    private JList<FornecedorDTO> listaFornecedores;
    private JPopupMenu popupMenuFornecedores;

    /**
     * Creates new form cadastrarProduto
     * @param parent O Frame pai do diálogo.
     * @param modal Define se o diálogo é modal (bloqueia a interação com a janela pai).
     */
    public cadastrarProduto(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        try {
            // Inicializa as DAOs usando os métodos factory da DAOFactory
            this.produtoImpl = DAOFactory.criarProdutoDAO();
            this.instrumentoImpl = DAOFactory.criarInstrumentoDAO();
            this.fornecedorImpl = DAOFactory.criarFornecedorDAO(); // Usa o método criarFornecedorDAO
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao inicializar DAOs para cadastro de produto: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar ao banco de dados para o cadastro de produto: " + e.getMessage(),
                    "Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);
            // Se a conexão for crítica, é melhor fechar o diálogo.
            dispose();
            return; // Garante que não prossegue com DAOs nulas
        }

        // Popula o JComboBox com os valores da enum Categoria
        comBoxCategoria.removeAllItems();
        for (Categoria categoria : Categoria.values()) {
            comBoxCategoria.addItem(categoria.name());
        }
        comBoxCategoria.setSelectedIndex(0); // Seleciona o primeiro item por padrão

        configurarAutoCompleteFornecedor();
    }

    private void configurarAutoCompleteFornecedor() {
        listaFornecedores = new JList<>();
        listaFornecedores.setBackground(new Color(22, 21, 27));
        listaFornecedores.setForeground(Color.WHITE);
        listaFornecedores.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        popupMenuFornecedores = new JPopupMenu();
        JScrollPane scrollPane = new JScrollPane(listaFornecedores);
        scrollPane.setPreferredSize(new Dimension(215, 150));
        popupMenuFornecedores.add(scrollPane);

        txtFornecedor.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { mostrarSugestoesFornecedor(); }
            @Override
            public void removeUpdate(DocumentEvent e) { mostrarSugestoesFornecedor(); }
            @Override
            public void changedUpdate(DocumentEvent e) { mostrarSugestoesFornecedor(); }
        });

        listaFornecedores.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                FornecedorDTO selecionado = listaFornecedores.getSelectedValue();
                if (selecionado != null) {
                    txtFornecedor.setText(selecionado.getNomeFornecedor());
                    popupMenuFornecedores.setVisible(false);
                }
            }
        });
    }

    private void mostrarSugestoesFornecedor() {
        SwingUtilities.invokeLater(() -> {
            String texto = txtFornecedor.getText().trim();

            if (texto.isEmpty()) {
                popupMenuFornecedores.setVisible(false);
                return;
            }

            try {
                List<FornecedorDTO> fornecedores = fornecedorImpl.buscarFornecedoresPorNome(texto);
                DefaultListModel<FornecedorDTO> modelo = new DefaultListModel<>();
                for (FornecedorDTO fornecedor : fornecedores) {
                    modelo.addElement(fornecedor);
                }
                listaFornecedores.setModel(modelo);

                if (modelo.isEmpty()) {
                    popupMenuFornecedores.setVisible(false);
                    return;
                }

                int altura = Math.min(modelo.getSize() * 25, 150);
                listaFornecedores.setPreferredSize(new Dimension(txtFornecedor.getWidth(), altura));

                if (!popupMenuFornecedores.isVisible()) {
                    popupMenuFornecedores.show(txtFornecedor, 0, txtFornecedor.getHeight());
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao buscar fornecedores para autocomplete: " + e.getMessage(), e);
                popupMenuFornecedores.setVisible(false);
                // Não mostra JOptionPane aqui para evitar spam de popups em cada digitação
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

        jPanel1 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        txtNome = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtMarca = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        comBoxCategoria = new javax.swing.JComboBox<>();
        jLabel11 = new javax.swing.JLabel();
        txtFornecedor = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        txtPreco = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        txtDescricao = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        txtModelo1 = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtQuantidade = new javax.swing.JTextField();
        btnSalvar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(40, 34, 44));

        jLabel5.setBackground(new java.awt.Color(21, 22, 27));
        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("NOME INSTRUMENTO"); // Alterado de ID para NOME INSTRUMENTO

        txtNome.setBackground(new java.awt.Color(22, 21, 27));
        txtNome.setForeground(new java.awt.Color(255, 255, 255));
        txtNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNomeActionPerformed(evt);
            }
        });

        jLabel6.setBackground(new java.awt.Color(21, 22, 27));
        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("MARCA");

        txtMarca.setBackground(new java.awt.Color(22, 21, 27));
        txtMarca.setForeground(new java.awt.Color(255, 255, 255));
        txtMarca.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMarcaActionPerformed(evt);
            }
        });

        jLabel4.setBackground(new java.awt.Color(21, 22, 27));
        jLabel4.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("CATEGORIA");

        comBoxCategoria.setBackground(new java.awt.Color(21, 22, 27));
        comBoxCategoria.setForeground(new java.awt.Color(255, 255, 255));
        comBoxCategoria.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "SOPRO", "CORDAS", "PERCUSSAO", "ELETRONICO" }));
        comBoxCategoria.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comBoxCategoriaActionPerformed(evt);
            }
        });

        jLabel11.setBackground(new java.awt.Color(21, 22, 27));
        jLabel11.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("FORNECEDOR");

        txtFornecedor.setBackground(new java.awt.Color(22, 21, 27));
        txtFornecedor.setForeground(new java.awt.Color(255, 255, 255));
        txtFornecedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtFornecedorActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(21, 22, 27));
        jLabel10.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("PRECO");

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

        txtModelo1.setBackground(new java.awt.Color(22, 21, 27));
        txtModelo1.setForeground(new java.awt.Color(255, 255, 255));
        txtModelo1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtModelo1ActionPerformed(evt);
            }
        });

        jLabel13.setBackground(new java.awt.Color(21, 22, 27));
        jLabel13.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("QUANTIDADE");

        txtQuantidade.setBackground(new java.awt.Color(22, 21, 27));
        txtQuantidade.setForeground(new java.awt.Color(255, 255, 255));
        txtQuantidade.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtQuantidadeActionPerformed(evt);
            }
        });

        btnSalvar.setBackground(new java.awt.Color(202, 207, 214));
        btnSalvar.setForeground(new java.awt.Color(4, 138, 129));
        btnSalvar.setText("SALVAR");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt); // Removido 'throws SQLException'
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(76, 76, 76)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 645, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel12)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(txtPreco)
                                                        .addComponent(jLabel10)
                                                        .addComponent(txtFornecedor)
                                                        .addComponent(jLabel11)
                                                        .addComponent(comBoxCategoria, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(txtMarca, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
                                                        .addComponent(jLabel6)
                                                        .addComponent(txtNome)
                                                        .addComponent(jLabel5))
                                                .addGap(232, 232, 232)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel9)
                                                        .addComponent(txtModelo1, javax.swing.GroupLayout.PREFERRED_SIZE, 215, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                                .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.LEADING)
                                                                .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                                .addContainerGap(299, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(98, 98, 98)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtModelo1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel6)
                                        .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(35, 35, 35)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comBoxCategoria, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel11)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtPreco)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(39, 39, 39))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1037, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 621, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNomeActionPerformed
        // Ação para o campo txtNome (se necessário)
    }//GEN-LAST:event_txtNomeActionPerformed

    private void txtMarcaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMarcaActionPerformed
        // Ação para o campo txtMarca (se necessário)
    }//GEN-LAST:event_txtMarcaActionPerformed

    private void comBoxCategoriaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comBoxCategoriaActionPerformed
        // Ação para o JComboBox comBoxCategoria (se necessário)
    }//GEN-LAST:event_comBoxCategoriaActionPerformed

    private void txtFornecedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtFornecedorActionPerformed
        // Ação para o campo txtFornecedor (se necessário)
    }//GEN-LAST:event_txtFornecedorActionPerformed

    private void txtPrecoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPrecoActionPerformed
        // Ação para o campo txtPreco (se necessário)
    }//GEN-LAST:event_txtPrecoActionPerformed

    private void txtDescricaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtDescricaoActionPerformed
        // Ação para o campo txtDescricao (se necessário)
    }//GEN-LAST:event_txtDescricaoActionPerformed

    private void txtModelo1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtModelo1ActionPerformed
        // Ação para o campo txtModelo1 (se necessário)
    }//GEN-LAST:event_txtModelo1ActionPerformed

    private void txtQuantidadeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtQuantidadeActionPerformed
        // Ação para o campo txtQuantidade (se necessário)
    }//GEN-LAST:event_txtQuantidadeActionPerformed

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) { // Removido 'throws SQLException'
        // Validação das instâncias DAO
        if (produtoImpl == null || instrumentoImpl == null || fornecedorImpl == null) {
            JOptionPane.showMessageDialog(this, "O serviço de banco de dados não foi inicializado corretamente.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "DAO(s) nula(s) no btnSalvarActionPerformed. Verifique a inicialização no construtor.");
            return;
        }

        String nomeInstrumento = txtNome.getText().trim();
        String categoria = (String) comBoxCategoria.getSelectedItem();
        String descricao = txtDescricao.getText().trim();
        String modelo = txtModelo1.getText().trim();
        String marca = txtMarca.getText().trim();
        String precoTexto = txtPreco.getText().trim();
        String quantidadeTexto = txtQuantidade.getText().trim();
        String nomeFornecedor = txtFornecedor.getText().trim();

        // Validação de campos vazios
        if (nomeInstrumento.isEmpty() || categoria == null || descricao.isEmpty()
                || modelo.isEmpty() || marca.isEmpty() || precoTexto.isEmpty()
                || quantidadeTexto.isEmpty() || nomeFornecedor.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos!", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            logger.log(Level.WARNING, "Tentativa de salvar produto com campos vazios.");
            return;
        }

        double preco;
        int quantidade;
        try {
            preco = Double.parseDouble(precoTexto);
            quantidade = Integer.parseInt(quantidadeTexto);
            if (preco < 0 || quantidade < 0) { // Preço e quantidade não podem ser negativos
                JOptionPane.showMessageDialog(this, "Preço e Quantidade não podem ser números negativos.", "Valor Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Preço e Quantidade devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, "Erro de formato ao parsear Preço ou Quantidade: " + e.getMessage(), e);
            return;
        }

        try {
            // 1. Criar ou buscar o Instrumento
            InstrumentoDTO instrumento;
            // Tenta buscar o instrumento pelo nome (agora retorna Optional)
            Optional<InstrumentoDTO> instrumentoOpt = instrumentoImpl.buscarPorNome(nomeInstrumento);

            if (instrumentoOpt.isPresent()) { // Se o instrumento já existe, usa ele
                instrumento = instrumentoOpt.get();
                logger.log(Level.INFO, "Usando instrumento existente: " + nomeInstrumento);
                // Opcional: Atualizar a categoria se for diferente
                if (!instrumento.getCategoria().name().equals(categoria)) {
                    instrumento.setCategoria(Categoria.valueOf(categoria));
                    instrumentoImpl.atualizar(instrumento); // Método de atualização do instrumento
                    logger.log(Level.INFO, "Categoria do instrumento atualizada para: " + categoria);
                }
            } else { // Se o instrumento não existe, cria um novo
                instrumento = new InstrumentoDTO();
                instrumento.setNome(nomeInstrumento);
                instrumento.setCategoria(Categoria.valueOf(categoria));
                // Salvar o novo instrumento (deve retornar o DTO com o ID gerado)
                instrumento = instrumentoImpl.salvar(instrumento); // InstrumentoDAO.salvar retorna InstrumentoDTO
                logger.log(Level.INFO, "Novo instrumento cadastrado com ID: " + instrumento.getIdInstrumento() + ", Nome: " + nomeInstrumento);
            }

            // 2. Buscar o Fornecedor pelo nome
            // FornecedorDAO.buscarFornecedoresPorNome retorna List<FornecedorDTO>
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
                JOptionPane.showMessageDialog(this, "Fornecedor '" + nomeFornecedor + "' não encontrado ou correspondência exata não encontrada. Por favor, cadastre o fornecedor primeiro ou insira um nome válido.", "Fornecedor Inválido", JOptionPane.WARNING_MESSAGE);
                logger.log(Level.WARNING, "Tentativa de cadastrar produto com fornecedor inexistente: " + nomeFornecedor);
                return;
            }

            // 3. Criar e Salvar o Produto
            ProdutoDTO produto = new ProdutoDTO();
            produto.setDescricao(descricao);
            produto.setModelo(modelo);
            produto.setMarca(marca);
            produto.setPreco(preco);
            produto.setQuantidadeEstoque(quantidade);
            produto.setIdInstrumento(instrumento.getIdInstrumento()); // Usa o ID do instrumento (existente ou novo)
            produto.setIdFornecedor(fornecedor.getId()); // Usa o ID do fornecedor

            produtoImpl.salvar(produto);

            JOptionPane.showMessageDialog(this, "Produto cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            logger.log(Level.INFO, "Produto cadastrado com sucesso: " + nomeInstrumento + " - Modelo: " + modelo);
            limparCampos(); // Limpa os campos após o sucesso
            dispose(); // Fecha o JDialog
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, "Categoria inválida selecionada. Por favor, selecione uma categoria válida.", "Erro de Categoria", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "Erro ao converter categoria no cadastro de produto: " + categoria, e);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Erro SQL ao cadastrar produto: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Erro ao interagir com o banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ocorreu um erro inesperado ao salvar o produto: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao salvar o produto: " + ex.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }
//GEN-LAST:event_btnSalvarActionPerformed

    /**
     * Método auxiliar para limpar os campos do formulário.
     */
    private void limparCampos() {
        txtNome.setText("");
        comBoxCategoria.setSelectedIndex(0);
        txtDescricao.setText("");
        txtModelo1.setText("");
        txtMarca.setText("");
        txtPreco.setText("");
        txtQuantidade.setText("");
        txtFornecedor.setText("");
    }

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
            java.util.logging.Logger.getLogger(cadastrarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(cadastrarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(cadastrarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(cadastrarProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // Adicionado try-catch para lidar com SQLException do construtor
                cadastrarProduto dialog = new cadastrarProduto(new JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<String> comBoxCategoria;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtDescricao;
    private javax.swing.JTextField txtFornecedor;
    private javax.swing.JTextField txtMarca;
    private javax.swing.JTextField txtModelo1;
    private javax.swing.JTextField txtNome;
    private javax.swing.JTextField txtPreco;
    private javax.swing.JTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}
