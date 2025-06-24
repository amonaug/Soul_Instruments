/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.roncolatoandpedro.soulinstruments.ui;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.FornecedorDAO; // Para buscar fornecedor
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;     // Para salvar o pedido
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO;     // Para buscar detalhes do produto e seu preço
import com.roncolatoandpedro.soulinstruments.dto.FornecedorDTO;
import com.roncolatoandpedro.soulinstruments.dto.ItemPedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList; // Para usar na lista de sugestões
import java.util.Date;       // Para a data do pedido
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AmonA
 */
public class cadastrarPedido extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(cadastrarPedido.class.getName());

    // Instâncias das DAOs
    private PedidoDAO pedidoImpl;
    private ProdutoDAO produtoImpl;
    private FornecedorDAO fornecedorImpl; // Para buscar fornecedores por nome (autocomplete)

    // Para o autocomplete de fornecedores
    private JList<FornecedorDTO> listaFornecedores;
    private JPopupMenu popupMenuFornecedores;

    /**
     * Creates new form cadastrarPedido
     * @param parent O Frame pai do diálogo.
     * @param modal Define se o diálogo é modal (bloqueia a interação com a janela pai).
     */
    public cadastrarPedido(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        try {
            // Inicializa as DAOs usando a DAOFactory
            this.produtoImpl = DAOFactory.criarProdutoDAO();
            this.fornecedorImpl = DAOFactory.criarFornecedorDAO();
            // Para PedidoDAO, é necessário passar ProdutoDAO e ItemPedidoDAO para o construtor.
            // Aqui, criamos uma ItemPedidoDAOImpl separada para injeção.
            // ATENÇÃO: Se DAOFactory.criarPedidoDAO() já gerencia ItemPedidoDAO internamente,
            // então você pode apenas chamar DAOFactory.criarPedidoDAO().
            // Pelo nosso PedidoDAOImpl anterior, ele espera ProdutoDAO e ItemPedidoDAO.
            this.pedidoImpl = DAOFactory.criarPedidoDAO(); // DAOFactory.criarPedidoDAO() já injeta as dependências
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao inicializar DAOs para cadastro de pedido: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar ao banco de dados para o cadastro de pedido: " + e.getMessage(),
                    "Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);
            // Se a conexão for crítica, é melhor fechar o diálogo.
            dispose();
            return; // Garante que não prossegue com DAOs nulas
        }

        // Popula o JComboBox com status de pedido
        comBoxStatus.removeAllItems();
        comBoxStatus.addItem("PENDENTE");
        comBoxStatus.addItem("PROCESSANDO");
        comBoxStatus.addItem("CONCLUIDO");
        comBoxStatus.addItem("CANCELADO");
        comBoxStatus.setSelectedIndex(0); // Seleciona "PENDENTE" por padrão

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
                // Nao mostra JOptionPane aqui para evitar spam de popups em cada digitação
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
        jLabel2 = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtProdutoId = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtQuantidade = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        comBoxStatus = new javax.swing.JComboBox<>();
        btnSalvar = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        lblProdutoNome = new javax.swing.JLabel();
        btnBuscarProduto = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        lblEstoqueAtual = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtFornecedor = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(40, 34, 44));

        jLabel5.setBackground(new java.awt.Color(21, 22, 27));
        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // Aumentado fonte
        jLabel5.setForeground(new java.awt.Color(4, 138, 129)); // Cor azul-petróleo
        jLabel5.setText("CADASTRAR NOVO PEDIDO");

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("NOME DO CLIENTE:");

        txtCliente.setBackground(new java.awt.Color(22, 21, 27));
        txtCliente.setForeground(new java.awt.Color(255, 255, 255));
        txtCliente.setSelectionColor(new java.awt.Color(4, 138, 129));

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("ID DO PRODUTO:");

        txtProdutoId.setBackground(new java.awt.Color(22, 21, 27));
        txtProdutoId.setForeground(new java.awt.Color(255, 255, 255));
        txtProdutoId.setSelectionColor(new java.awt.Color(4, 138, 129));

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("QUANTIDADE:");

        txtQuantidade.setBackground(new java.awt.Color(22, 21, 27));
        txtQuantidade.setForeground(new java.awt.Color(255, 255, 255));
        txtQuantidade.setSelectionColor(new java.awt.Color(4, 138, 129));

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("STATUS:");

        comBoxStatus.setBackground(new java.awt.Color(22, 21, 27));
        comBoxStatus.setForeground(new java.awt.Color(255, 255, 255));
        comBoxStatus.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "PENDENTE", "PROCESSANDO", "CONCLUIDO", "CANCELADO" }));

        btnSalvar.setBackground(new java.awt.Color(4, 138, 129));
        btnSalvar.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        btnSalvar.setForeground(new java.awt.Color(255, 255, 255));
        btnSalvar.setText("SALVAR PEDIDO");
        btnSalvar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSalvarActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("PRODUTO:");

        lblProdutoNome.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        lblProdutoNome.setForeground(new java.awt.Color(200, 200, 200));
        lblProdutoNome.setText("N/A");

        btnBuscarProduto.setBackground(new java.awt.Color(202, 207, 214));
        btnBuscarProduto.setForeground(new java.awt.Color(4, 138, 129));
        btnBuscarProduto.setText("BUSCAR");
        btnBuscarProduto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProdutoActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("ESTOQUE ATUAL:");

        lblEstoqueAtual.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        lblEstoqueAtual.setForeground(new java.awt.Color(200, 200, 200));
        lblEstoqueAtual.setText("N/A");

        jLabel9.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("FORNECEDOR:");

        txtFornecedor.setBackground(new java.awt.Color(22, 21, 27));
        txtFornecedor.setForeground(new java.awt.Color(255, 255, 255));
        txtFornecedor.setSelectionColor(new java.awt.Color(4, 138, 129));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(50, 50, 50)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel5)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                        .addComponent(txtFornecedor, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(comBoxStatus, javax.swing.GroupLayout.Alignment.LEADING, 0, 250, Short.MAX_VALUE)
                                                        .addComponent(txtQuantidade, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtCliente, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                                                                .addComponent(jLabel3)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(btnBuscarProduto))
                                                        .addComponent(txtProdutoId, javax.swing.GroupLayout.Alignment.LEADING))
                                                .addGap(18, 18, 18)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(lblProdutoNome)
                                                        .addComponent(jLabel7)
                                                        .addComponent(lblEstoqueAtual)
                                                        .addComponent(jLabel8))))
                                .addContainerGap(597, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(30, 30, 30)
                                .addComponent(jLabel5)
                                .addGap(30, 30, 30)
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtProdutoId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblProdutoNome)
                                        .addComponent(btnBuscarProduto))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(jLabel8))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtQuantidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblEstoqueAtual))
                                .addGap(18, 18, 18)
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtFornecedor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(comBoxStatus, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(40, 40, 40)
                                .addComponent(btnSalvar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap(127, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnSalvarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSalvarActionPerformed
        // Validação das instâncias DAO
        if (pedidoImpl == null || produtoImpl == null || fornecedorImpl == null) {
            JOptionPane.showMessageDialog(this, "O serviço de banco de dados não foi inicializado corretamente.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "DAO(s) nula(s) no btnSalvarActionPerformed. Verifique a inicialização no construtor.");
            return;
        }

        String clienteNome = txtCliente.getText().trim();
        String produtoIdText = txtProdutoId.getText().trim();
        String quantidadeText = txtQuantidade.getText().trim();
        String fornecedorNome = txtFornecedor.getText().trim(); // Novo campo
        String status = (String) comBoxStatus.getSelectedItem();

        // Validação de campos vazios
        if (clienteNome.isEmpty() || produtoIdText.isEmpty() || quantidadeText.isEmpty() || fornecedorNome.isEmpty() || status == null) {
            JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos!", "Campos Vazios", JOptionPane.WARNING_MESSAGE);
            logger.log(Level.WARNING, "Tentativa de salvar pedido com campos vazios.");
            return;
        }

        Long produtoId;
        int quantidade;
        try {
            produtoId = Long.parseLong(produtoIdText);
            quantidade = Integer.parseInt(quantidadeText);
            if (quantidade <= 0) {
                JOptionPane.showMessageDialog(this, "A quantidade deve ser um número positivo.", "Valor Inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID do Produto e Quantidade devem ser números válidos.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, "Erro de formato ao parsear ID do Produto ou Quantidade: " + e.getMessage(), e);
            return;
        }

        try {
            // 1. Buscar Fornecedor
            List<FornecedorDTO> fornecedoresEncontrados = fornecedorImpl.buscarFornecedoresPorNome(fornecedorNome);
            FornecedorDTO fornecedor = null;
            if (fornecedoresEncontrados != null && !fornecedoresEncontrados.isEmpty()) {
                // Tenta encontrar uma correspondência exata para o nome do fornecedor
                for (FornecedorDTO f : fornecedoresEncontrados) {
                    if (f.getNomeFornecedor().equalsIgnoreCase(fornecedorNome)) {
                        fornecedor = f;
                        break;
                    }
                }
            }
            if (fornecedor == null) {
                JOptionPane.showMessageDialog(this, "Fornecedor '" + fornecedorNome + "' não encontrado ou correspondência exata não encontrada. Por favor, selecione um fornecedor válido ou adicione-o.", "Fornecedor Inválido", JOptionPane.WARNING_MESSAGE);
                logger.log(Level.WARNING, "Tentativa de criar pedido com fornecedor inexistente: " + fornecedorNome);
                return;
            }


            // 2. Verificar se o Produto existe e obter preço
            Optional<ProdutoDTO> produtoOpt = produtoImpl.buscarPorId(produtoId);
            if (produtoOpt.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Produto com ID " + produtoId + " não encontrado. Verifique o ID.", "Produto Não Encontrado", JOptionPane.WARNING_MESSAGE);
                logger.log(Level.WARNING, "Tentativa de criar pedido com produto ID inexistente: " + produtoId);
                return;
            }
            ProdutoDTO produtoExistente = produtoOpt.get();

            // 3. Verificar estoque suficiente
            if (produtoExistente.getQuantidadeEstoque() < quantidade) {
                JOptionPane.showMessageDialog(this, "Estoque insuficiente para o produto " + produtoExistente.getModelo() + ". Estoque atual: " + produtoExistente.getQuantidadeEstoque(), "Estoque Insuficiente", JOptionPane.WARNING_MESSAGE);
                logger.log(Level.WARNING, "Tentativa de criar pedido com estoque insuficiente para produto ID: " + produtoId + ". Necessário: " + quantidade + ", Disponível: " + produtoExistente.getQuantidadeEstoque());
                return;
            }

            // Criação do objeto DTO Pedido
            PedidoDTO pedido = new PedidoDTO();
            pedido.setNomeCliente(clienteNome);
            pedido.setDataPedido(new Date()); // Data atual
            pedido.setStatus(status);
            pedido.setIdFornecedor(fornecedor.getId()); // Define o ID do fornecedor principal do pedido

            // Criação do Item do Pedido
            ItemPedidoDTO itemPedido = new ItemPedidoDTO();
            itemPedido.setIdProduto(produtoId);
            itemPedido.setQuantidade(quantidade);
            itemPedido.setValorUnitario(produtoExistente.getPreco()); // Pega o preço do produto existente
            itemPedido.calcularValorTotal(); // Calcula o valor total do item

            pedido.addItem(itemPedido); // Adiciona o item ao pedido

            // Salvar no banco
            pedidoImpl.salvar(pedido);

            // Opcional: Atualizar a quantidade em estoque do produto
            // if ("CONCLUIDO".equals(status)) { // Apenas se o status indica saída de estoque imediata
            //     produtoExistente.setQuantidadeEstoque(produtoExistente.getQuantidadeEstoque() - quantidade);
            //     produtoImpl.atualizar(produtoExistente);
            // }

            JOptionPane.showMessageDialog(this, "Pedido cadastrado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            logger.log(Level.INFO, "Pedido cadastrado com sucesso para o cliente: " + clienteNome + ", Produto ID: " + produtoId + ", Status: " + status);
            limparCampos(); // Limpa os campos após o sucesso
            dispose(); // Fecha o JDialog após salvar
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Erro SQL ao salvar pedido ou buscar dados: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Erro ao interagir com o banco de dados: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ocorreu um erro inesperado ao salvar o pedido: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao salvar o pedido: " + ex.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSalvarActionPerformed

    private void btnBuscarProdutoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProdutoActionPerformed
        if (produtoImpl == null) {
            JOptionPane.showMessageDialog(this, "O serviço de banco de dados não foi inicializado corretamente.", "Erro Interno", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.SEVERE, "produtoImpl é nulo no btnBuscarProdutoActionPerformed.");
            return;
        }

        String produtoIdText = txtProdutoId.getText().trim();
        if (produtoIdText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, insira um ID de Produto para buscar.", "Campo Vazio", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Long produtoId = Long.parseLong(produtoIdText);
            Optional<ProdutoDTO> produtoOpt = produtoImpl.buscarPorId(produtoId);

            if (produtoOpt.isPresent()) {
                ProdutoDTO produto = produtoOpt.get();
                lblProdutoNome.setText(produto.getModelo() + " (" + produto.getMarca() + ")");
                lblEstoqueAtual.setText(String.valueOf(produto.getQuantidadeEstoque()));
                // Opcional: Preencher fornecedor se o produto estiver associado a um fornecedor padrão
                // Se você tiver a InstrumentoDAO injetada, pode buscar o nome do instrumento aqui.
                // InstrumentoDTO instrumento = instrumentoImpl.buscarPorId(produto.getIdInstrumento());
                // lblProdutoNome.setText(instrumento.getNome() + " - " + produto.getModelo());
                JOptionPane.showMessageDialog(this, "Produto encontrado!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } else {
                lblProdutoNome.setText("N/A");
                lblEstoqueAtual.setText("N/A");
                JOptionPane.showMessageDialog(this, "Produto com ID " + produtoId + " não encontrado.", "Produto Não Encontrado", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID do Produto deve ser um número válido.", "Erro de Formato", JOptionPane.ERROR_MESSAGE);
            logger.log(Level.WARNING, "Erro de formato ao parsear ID do Produto: " + e.getMessage(), e);
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, "Erro SQL ao buscar produto: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Erro ao interagir com o banco de dados ao buscar produto: " + ex.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Ocorreu um erro inesperado ao buscar o produto: " + ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, "Ocorreu um erro inesperado ao buscar o produto: " + ex.getMessage(), "Erro Inesperado", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnBuscarProdutoActionPerformed

    private void limparCampos() {
        txtCliente.setText("");
        txtProdutoId.setText("");
        txtQuantidade.setText("");
        txtFornecedor.setText("");
        comBoxStatus.setSelectedIndex(0);
        lblProdutoNome.setText("N/A");
        lblEstoqueAtual.setText("N/A");
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
            java.util.logging.Logger.getLogger(cadastrarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(cadastrarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(cadastrarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(cadastrarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                cadastrarPedido dialog = new cadastrarPedido(new JFrame(), true);
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
    private javax.swing.JButton btnBuscarProduto;
    private javax.swing.JButton btnSalvar;
    private javax.swing.JComboBox<String> comBoxStatus;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblEstoqueAtual;
    private javax.swing.JLabel lblProdutoNome;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtFornecedor;
    private javax.swing.JTextField txtProdutoId;
    private javax.swing.JTextField txtQuantidade;
    // End of variables declaration//GEN-END:variables
}
