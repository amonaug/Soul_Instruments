/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.roncolatoandpedro.soulinstruments.ui;

import com.roncolatoandpedro.soulinstruments.dao.DAOFactory;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.PedidoDAO;
import com.roncolatoandpedro.soulinstruments.dao.interfaces.ProdutoDAO; // Para buscar nome do produto
import com.roncolatoandpedro.soulinstruments.dto.PedidoDTO;
import com.roncolatoandpedro.soulinstruments.dto.ProdutoDTO;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date; // Usar java.util.Date para consistência com PedidoDTO e JDBC
import java.util.List;
import java.util.Optional; // Importe Optional
import java.util.Vector; // Para uso com DefaultTableModel
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AmonA
 */
public class consultarPedido extends javax.swing.JDialog {

    private static final Logger logger = Logger.getLogger(consultarPedido.class.getName());

    // Instâncias das DAOs
    private PedidoDAO pedidoImpl;
    private ProdutoDAO produtoImpl;

    // Para o autocomplete de nome do cliente
    private JList<String> listaClientes;
    private JPopupMenu popupMenuClientes;


    /**
     * Creates new form consultarPedido
     * @param parent O Frame pai do diálogo.
     * @param modal Define se o diálogo é modal (bloqueia a interação com a janela pai).
     */
    public consultarPedido(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        try {
            // Inicializa as DAOs usando a DAOFactory
            this.pedidoImpl = DAOFactory.criarPedidoDAO();
            this.produtoImpl = DAOFactory.criarProdutoDAO();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao inicializar DAOs para consulta de pedidos: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this,
                    "Erro ao conectar ao banco de dados para consulta de pedidos: " + e.getMessage(),
                    "Erro de Inicialização",
                    JOptionPane.ERROR_MESSAGE);
            dispose(); // Fecha o diálogo se não conseguir inicializar as DAOs
            return;
        }

        configurarTabelaPedidos();
        configurarFiltros();
        configurarAutoCompleteCliente();
        carregarPedidosNaTabela(null, null); // Carrega todos os pedidos inicialmente
    }


    private void configurarTabelaPedidos() {
        DefaultTableModel model = new DefaultTableModel(
                new Object [][] {},
                new String [] { "ID Pedido", "Cliente", "ID Produto", "Nome Produto", "Quantidade", "Status", "Data do Pedido" }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Torna as células não editáveis
            }
        };
        jTable1.setModel(model);

        // Opcional: Ajustar largura das colunas
        jTable1.getColumnModel().getColumn(0).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(1).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(2).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(3).setPreferredWidth(150);
        jTable1.getColumnModel().getColumn(4).setPreferredWidth(80);
        jTable1.getColumnModel().getColumn(5).setPreferredWidth(100);
        jTable1.getColumnModel().getColumn(6).setPreferredWidth(150);
    }

    private void configurarFiltros() {
        // Popula o JComboBox de Status
        comBoxStatusFiltro.removeAllItems();
        comBoxStatusFiltro.addItem("TODOS");
        comBoxStatusFiltro.addItem("PENDENTE");
        comBoxStatusFiltro.addItem("PROCESSANDO");
        comBoxStatusFiltro.addItem("CONCLUIDO");
        comBoxStatusFiltro.addItem("CANCELADO");
        comBoxStatusFiltro.setSelectedIndex(0); // Seleciona "TODOS" por padrão
    }

    private void configurarAutoCompleteCliente() {
        listaClientes = new JList<>();
        listaClientes.setBackground(new Color(22, 21, 27));
        listaClientes.setForeground(Color.WHITE);
        listaClientes.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        popupMenuClientes = new JPopupMenu();
        JScrollPane scrollPane = new JScrollPane(listaClientes);
        scrollPane.setPreferredSize(new Dimension(215, 150));
        popupMenuClientes.add(scrollPane);

        txtClienteFiltro.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { mostrarSugestoesCliente(); }
            @Override
            public void removeUpdate(DocumentEvent e) { mostrarSugestoesCliente(); }
            @Override
            public void changedUpdate(DocumentEvent e) { mostrarSugestoesCliente(); }
        });

        listaClientes.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selecionado = listaClientes.getSelectedValue();
                if (selecionado != null) {
                    txtClienteFiltro.setText(selecionado);
                    popupMenuClientes.setVisible(false);
                }
            }
        });
    }

    private void mostrarSugestoesCliente() {
        SwingUtilities.invokeLater(() -> {
            String texto = txtClienteFiltro.getText().trim();

            if (texto.isEmpty()) {
                popupMenuClientes.setVisible(false);
                return;
            }

            try {
                // Usa o método da PedidoDAO para buscar nomes de clientes que correspondem ao texto
                List<PedidoDTO> clientes = pedidoImpl.buscarPorNomeCliente(texto); // Novo método na PedidoDAOImpl
                DefaultListModel<String> modelo = new DefaultListModel<>();
                for (PedidoDTO cliente : clientes) {
                    modelo.addElement(String.valueOf(cliente));
                }
                listaClientes.setModel(modelo);

                if (modelo.isEmpty()) {
                    popupMenuClientes.setVisible(false);
                    return;
                }

                int altura = Math.min(modelo.getSize() * 25, 150);
                listaClientes.setPreferredSize(new Dimension(txtClienteFiltro.getWidth(), altura));

                if (!popupMenuClientes.isVisible()) {
                    popupMenuClientes.show(txtClienteFiltro, 0, txtClienteFiltro.getHeight());
                }

            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Erro ao buscar clientes para autocomplete: " + e.getMessage(), e);
                popupMenuClientes.setVisible(false);
                // Não mostra JOptionPane aqui para evitar spam de popups em cada digitação
            }
        });
    }


    private void carregarPedidosNaTabela(String clienteFiltro, String statusFiltro) {
        if (pedidoImpl == null || produtoImpl == null) {
            logger.log(Level.SEVERE, "DAOs não inicializadas. Não é possível carregar pedidos.");
            return;
        }

        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        model.setRowCount(0); // Limpa as linhas existentes

        try {
            List<PedidoDTO> pedidos;
            // Aplica filtros se houver. Prioriza nome do cliente, depois status.
            if (clienteFiltro != null && !clienteFiltro.isEmpty()) {
                pedidos = pedidoImpl.buscarPorNomeCliente(clienteFiltro);
            } else if (statusFiltro != null && !"TODOS".equals(statusFiltro)) {
                pedidos = pedidoImpl.buscarPorStatus(statusFiltro);
            } else {
                pedidos = pedidoImpl.listarTodos(); // Se nenhum filtro, lista todos
            }

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            for (PedidoDTO pedido : pedidos) {
                // Para exibir o nome do produto na tabela, precisamos dos itens do pedido.
                // Como PedidoDTO agora contém uma lista de ItemPedidoDTO, podemos iterar sobre eles.
                // IMPORTANTE: PedidoDTO tem idProduto e quantidade diretamente agora (para compatibilidade com cadastrarPedido.java)
                // Se um PedidoDTO puder ter múltiplos ItemPedidoDTOs, a lógica abaixo precisaria ser ajustada para iterar sobre 'pedido.getItens()'.
                // Para este exemplo, vou assumir que idProduto e quantidade no PedidoDTO representam o "item principal" do pedido,
                // ou que cada pedido na tabela representa um único item de pedido para simplificação na GUI de consulta.
                // Se um pedido tem múltiplos itens, você precisaria decidir como representar isso na tabela (e.g., uma linha por item, ou detalhes agregados).

                String nomeProduto = "N/A";
                // Assumindo que o PedidoDTO possui idProduto e quantidade diretamente para fins de UI de cadastro simplificada
                // Se PedidoDTO tivesse apenas List<ItemPedidoDTO> itens, a lógica seria:
                // for (ItemPedidoDTO item : pedido.getItens()) {
                //    Optional<ProdutoDTO> pOpt = produtoImpl.buscarPorId(item.getIdProduto());
                //    if (pOpt.isPresent()) { nomeProduto = pOpt.get().getNome() + " (" + pOpt.get().getModelo() + ")"; break; }
                // }
                // Com a nova estrutura do PedidoDTO (com idProduto e quantidade), vamos usá-la.
                Optional<ProdutoDTO> pOpt = produtoImpl.buscarPorId(pedido.getIdProduto());
                if (pOpt.isPresent()) {
                    ProdutoDTO produto = pOpt.get();
                    nomeProduto = produto.getDescricao() + " (" + produto.getModelo() + ")";
                }

                Vector<Object> row = new Vector<>();
                row.add(pedido.getIdPedido());
                row.add(pedido.getNomeCliente()); // Campo agora no PedidoDTO
                row.add(pedido.getIdProduto());   // Campo agora no PedidoDTO
                row.add(nomeProduto);
                row.add(pedido.getQuantidade());  // Campo agora no PedidoDTO
                row.add(pedido.getStatus());      // Campo agora no PedidoDTO
                row.add(pedido.getDataPedido() != null ? dateFormat.format(pedido.getDataPedido()) : "N/A");
                model.addRow(row);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro SQL ao carregar pedidos na tabela: " + e.getMessage(), e);
            JOptionPane.showMessageDialog(this, "Erro ao carregar pedidos: " + e.getMessage(), "Erro de Banco de Dados", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Ocorreu um erro inesperado ao carregar pedidos: " + e.getMessage(), e);
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

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        btnHome = new javax.swing.JButton();
        btnEstoque = new javax.swing.JButton();
        btnAtualizar = new javax.swing.JButton();
        btnEntradaSaida = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        txtClienteFiltro = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        comBoxStatusFiltro = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        btnBuscar = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

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
                        {null, null, null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        jScrollPane1.setViewportView(jTable1);

        txtClienteFiltro.setBackground(new java.awt.Color(22, 21, 27));
        txtClienteFiltro.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(21, 22, 27));
        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("FILTRAR POR CLIENTE:");

        comBoxStatusFiltro.setBackground(new java.awt.Color(21, 22, 27));
        comBoxStatusFiltro.setForeground(new java.awt.Color(255, 255, 255));
        comBoxStatusFiltro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        comBoxStatusFiltro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                comBoxStatusFiltroActionPerformed(evt);
            }
        });

        jLabel3.setBackground(new java.awt.Color(21, 22, 27));
        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 15)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("FILTRAR POR STATUS:");

        btnBuscar.setBackground(new java.awt.Color(4, 138, 129));
        btnBuscar.setForeground(new java.awt.Color(255, 255, 255));
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
                                                        .addComponent(txtClienteFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(115, 115, 115)
                                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                                .addComponent(comBoxStatusFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, 353, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                                        .addComponent(txtClienteFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(comBoxStatusFiltro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar))
                                .addGap(18, 18, 18)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 462, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(61, 61, 61))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1290, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addContainerGap()))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 725, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnHomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHomeActionPerformed
        this.dispose(); // Fecha a janela atual
        java.awt.EventQueue.invokeLater(() -> new janelaPrincipalGUI().setVisible(true));
    }//GEN-LAST:event_btnHomeActionPerformed

    private void btnEstoqueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEstoqueActionPerformed
        this.dispose(); // Fecha a janela atual
        java.awt.EventQueue.invokeLater(() -> new estoqueGUI().setVisible(true));
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

    private void comBoxStatusFiltroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_comBoxStatusFiltroActionPerformed
        // Quando o status muda, atualiza a tabela com o filtro
        btnBuscarActionPerformed(evt); // Chama o método de busca para aplicar o filtro
    }//GEN-LAST:event_comBoxStatusFiltroActionPerformed

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        String clienteFiltro = txtClienteFiltro.getText().trim();
        String statusFiltro = (String) comBoxStatusFiltro.getSelectedItem();

        // Se o campo de cliente estiver vazio, passa null para o filtro de cliente
        if (clienteFiltro.isEmpty()) {
            clienteFiltro = null;
        }

        // Se "TODOS" for selecionado no status, passa null para o filtro de status
        if ("TODOS".equals(statusFiltro)) {
            statusFiltro = null;
        }

        carregarPedidosNaTabela(clienteFiltro, statusFiltro);
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
            java.util.logging.Logger.getLogger(consultarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(consultarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(consultarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(consultarPedido.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new consultarPedido(new JFrame(), true).setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAtualizar;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnEntradaSaida;
    private javax.swing.JButton btnEstoque;
    private javax.swing.JButton btnHome;
    private javax.swing.JComboBox<String> comBoxStatusFiltro;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField txtClienteFiltro;
    // End of variables declaration//GEN-END:variables
}
