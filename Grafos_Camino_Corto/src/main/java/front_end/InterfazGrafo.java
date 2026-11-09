
package front_end;

import back_end.GrafoPonderado;
import back_end.Vertice;
import back_end.ResultadoCamino;
import javax.swing.JOptionPane;
import java.util.List;


public class InterfazGrafo extends javax.swing.JFrame {
    
    private GrafoPonderado grafo;
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(InterfazGrafo.class.getName());

    
    public InterfazGrafo() {
        initComponents();
        this.grafo = new GrafoPonderado(true);
        this.setLocationRelativeTo(null);

        panelVisual.setLayout(new java.awt.BorderLayout());
        panelVisual.add(new front_end.PanelGrafo(), java.awt.BorderLayout.CENTER);
    }
    
    private void agregarVertice() {
        try {
            String id = txtId.getText().trim();
            String nombre = txtNombre.getText().trim();
            String tipo = txtTipo.getText().trim();
            String estado = txtEstado.getText().trim();
            String capStr = txtCapacidad.getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || tipo.isEmpty() || estado.isEmpty() || capStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double capacidad = Double.parseDouble(capStr);
            if (capacidad < 0) {
                JOptionPane.showMessageDialog(this, "La capacidad debe ser positiva.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vertice v = new Vertice(id, nombre, tipo, estado, capacidad);
            if (grafo.agregarVertice(v)) {
                imprimir("Vértice registrado: " + v);
                limpiarCamposVertice();
                actualizarCombosNodos();
                actualizarVisualizacion();
            } else {
                JOptionPane.showMessageDialog(this, "El ID '" + id + "' ya existe.", "ID Duplicado", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "La capacidad debe ser un número.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarVertice() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del vértice.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Vertice v = grafo.buscarVertice(id);
        if (v != null) {
            imprimir("--- VÉRTICE ENCONTRADO ---");
            imprimir("ID: " + v.getId() + " | Nombre: " + v.getNombre());
            imprimir("Tipo: " + v.getTipo() + " | Estado: " + v.getEstado());
            imprimir("Capacidad: " + v.getCapacidad());
            imprimir("--------------------------");
        } else {
            imprimir("El vértice con ID '" + id + "' no existe.");
        }
    }

    private void eliminarVertice() {
        String id = txtId.getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID del vértice a eliminar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (grafo.eliminarVertice(id)) {
            imprimir("Vértice [" + id + "] y sus aristas asociadas eliminados.");
            limpiarCamposVertice();
            actualizarCombosNodos();
            actualizarVisualizacion();
        } else {
            imprimir("No se encontró el vértice [" + id + "].");
        }
    }

    private void agregarArista() {
        try {
            String orig = txtOrigen.getText().trim();
            String dest = txtDestino.getText().trim();
            String pesoStr = txtPeso.getText().trim();

            if (orig.isEmpty() || dest.isEmpty() || pesoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Complete Origen, Destino y Peso.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double peso = Double.parseDouble(pesoStr);
            if (peso <= 0) {
                JOptionPane.showMessageDialog(this, "El peso debe ser positivo.", "Validación", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (grafo.agregarArista(orig, dest, peso)) {
                imprimir("Arista creada: " + orig + " -> " + dest + " (Peso: " + peso + ")");
                txtOrigen.setText(""); txtDestino.setText(""); txtPeso.setText("");
                actualizarVisualizacion();
            } else {
                JOptionPane.showMessageDialog(this, "Uno o ambos vértices no existen.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El peso debe ser numérico.", "Error de Formato", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarArista() {
        String orig = txtOrigen.getText().trim();
        String dest = txtDestino.getText().trim();

        if (orig.isEmpty() || dest.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Indique los vértices origen y destino.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (grafo.eliminarArista(orig, dest)) {
            imprimir("Arista " + orig + " -> " + dest + " eliminada.");
            txtOrigen.setText(""); txtDestino.setText("");
            actualizarVisualizacion();
        } else {
            imprimir("No existe arista entre " + orig + " y " + dest + ".");
        }
    }

    private void ejecutarBFS() {
        String inicio = txtId.getText().trim();
        if (inicio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID en 'ID Nodo' para iniciar BFS.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<String> res = grafo.recorridoBFS(inicio);
        if (res.isEmpty()) imprimir("Vértice inicial no válido.");
        else imprimir("Recorrido BFS (" + inicio + "): " + String.join(" -> ", res));
    }

    private void ejecutarDFS() {
        String inicio = txtId.getText().trim();
        if (inicio.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese el ID en 'ID Nodo' para iniciar DFS.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        List<String> res = grafo.recorridoDFS(inicio);
        if (res.isEmpty()) imprimir("Vértice inicial no válido.");
        else imprimir("Recorrido DFS (" + inicio + "): " + String.join(" -> ", res));
    }

    private void mostrarMatriz() {
        imprimir("=== MATRIZ DE ADYACENCIA ===");
        imprimir(grafo.obtenerMatrizAdyacencia());
    }

    private void evaluarConectividad() {
        imprimir("=== CONECTIVIDAD DEL GRAFO ===");
        imprimir(grafo.evaluarConectividad());
    }

    private void destruirGrafo() {
        int r = JOptionPane.showConfirmDialog(this, "¿Destruir todo el grafo?", "Confirmación", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            grafo.destruirGrafo();
            txtConsola.setText("");
            txtTraza.setText("");
            actualizarCombosNodos();
            imprimir("Grafo destruido.");
            actualizarVisualizacion();
        }
    }

   

    private void actualizarCombosNodos() {
        if (cmbOrigen == null || cmbDestino == null) return;
        cmbOrigen.removeAllItems();
        cmbDestino.removeAllItems();
        for (Vertice v : grafo.getVertices()) {
            cmbOrigen.addItem(v.getId());
            cmbDestino.addItem(v.getId());
        }
    }

    private void calcularRutaOptima() {
        if (cmbOrigen.getSelectedItem() == null || cmbDestino.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione nodos Origen y Destino válidos.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orig = cmbOrigen.getSelectedItem().toString();
        String dest = cmbDestino.getSelectedItem().toString();
        String algo = cmbAlgoritmo.getSelectedItem().toString();

        try {
            ResultadoCamino res;
            if (algo.equalsIgnoreCase("Dijkstra")) {
                res = grafo.calcularDijkstra(orig, dest);
            } else {
                res = grafo.calcularBellmanFord(orig, dest);
            }
            txtTraza.setText(res.getTrazaDetallada());
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en Cálculo", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void compararAlgoritmos() {
        if (cmbOrigen.getSelectedItem() == null || cmbDestino.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione nodos Origen y Destino.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String orig = cmbOrigen.getSelectedItem().toString();
        String dest = cmbDestino.getSelectedItem().toString();

        try {
            ResultadoCamino resDijkstra = grafo.calcularDijkstra(orig, dest);
            ResultadoCamino resBellman = grafo.calcularBellmanFord(orig, dest);

            StringBuilder comp = new StringBuilder();
            comp.append("=====================================================\n");
            comp.append("          CUADRO COMPARATIVO DE DESEMPEÑO           \n");
            comp.append("=====================================================\n");
            comp.append(String.format("Origen: [%s]  -->  Destino: [%s]\n\n", orig, dest));

            comp.append(String.format("%-18s | %-12s | %-14s | %-10s\n", "Algoritmo", "Costo Óptimo", "Evaluaciones", "Descartes"));
            comp.append("-----------------------------------------------------\n");
            comp.append(String.format("%-18s | %-12.2f | %-14d | %-10d\n", "Dijkstra", resDijkstra.getCostoTotal(), resDijkstra.getEvaluaciones(), resDijkstra.getDescartes()));
            comp.append(String.format("%-18s | %-12.2f | %-14d | %-10d\n", "Bellman-Ford", resBellman.getCostoTotal(), resBellman.getEvaluaciones(), resBellman.getDescartes()));
            comp.append("-----------------------------------------------------\n");

            if (resDijkstra.getCostoTotal() == resBellman.getCostoTotal()) {
                comp.append("VERDICTO: Ambos algoritmos convergieron al mismo costo mínimo.\n");
            } else {
                comp.append("VERDICTO: Se detectó discrepancia en los costos.\n");
            }

            txtTraza.setText(comp.toString());

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error en Comparación", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarTraza() {
        txtTraza.setText("");
    }

    private void actualizarVisualizacion() {
        if (panelVisual.getComponentCount() > 0 && panelVisual.getComponent(0) instanceof front_end.PanelGrafo) {
            front_end.PanelGrafo miPanel = (front_end.PanelGrafo) panelVisual.getComponent(0);
            miPanel.actualizarDatos(grafo.getVertices(), grafo.getAristas());
        }
    }

    private void imprimir(String msj) { txtConsola.append(msj + "\n"); }

    private void limpiarCamposVertice() {
        txtId.setText(""); txtNombre.setText(""); txtTipo.setText(""); 
        txtEstado.setText(""); txtCapacidad.setText("");
    }

    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        txtId = new javax.swing.JTextField();
        txtNombre = new javax.swing.JTextField();
        txtTipo = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtEstado = new javax.swing.JTextField();
        txtCapacidad = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        btnAgregarVertice = new javax.swing.JButton();
        btnBuscarVertice = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        txtOrigen = new javax.swing.JTextField();
        txtDestino = new javax.swing.JTextField();
        txtPeso = new javax.swing.JTextField();
        btnAgregarArista = new javax.swing.JButton();
        btnEliminarArista = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        btnEliminarVertice = new javax.swing.JButton();
        btnDFS = new javax.swing.JButton();
        btnConectividad = new javax.swing.JButton();
        btnBFS = new javax.swing.JButton();
        btnMatriz = new javax.swing.JButton();
        btnDestruir = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        cmbOrigen = new javax.swing.JComboBox<>();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        cmbDestino = new javax.swing.JComboBox<>();
        cmbAlgoritmo = new javax.swing.JComboBox<>();
        btnCalcularRuta = new javax.swing.JButton();
        btnComparar = new javax.swing.JButton();
        btnLimpiarTraza = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtConsola = new javax.swing.JTextArea();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtTraza = new javax.swing.JTextArea();
        panelVisual = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Vertices");

        jLabel2.setText("ID Nodo");

        jLabel3.setText("Tipo");

        jLabel4.setText("Estado");

        jLabel5.setText("Capacidad");

        txtId.addActionListener(this::txtIdActionPerformed);

        jLabel6.setText("Nombre");

        txtCapacidad.addActionListener(this::txtCapacidadActionPerformed);

        btnAgregarVertice.setText("Agregar Vertice");
        btnAgregarVertice.addActionListener(this::btnAgregarVerticeActionPerformed);

        btnBuscarVertice.setText("Buscar Vertice");
        btnBuscarVertice.addActionListener(this::btnBuscarVerticeActionPerformed);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnAgregarVertice))
                    .addComponent(btnBuscarVertice, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(btnAgregarVertice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBuscarVertice)
                .addContainerGap(45, Short.MAX_VALUE))
        );

        jLabel7.setText("ID Origen");

        jLabel8.setText("ID Destino");

        jLabel9.setText("Peso");

        btnAgregarArista.setText("Agregar Arista");
        btnAgregarArista.addActionListener(this::btnAgregarAristaActionPerformed);

        btnEliminarArista.setText("Eliminar Arista");
        btnEliminarArista.addActionListener(this::btnEliminarAristaActionPerformed);

        jLabel10.setText("Aristas");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(38, 38, 38)
                        .addComponent(btnAgregarArista)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnEliminarArista))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel10)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel5Layout.createSequentialGroup()
                                        .addComponent(jLabel9)
                                        .addGap(49, 49, 49))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel8))
                                        .addGap(18, 18, 18)))
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(txtOrigen, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                                    .addComponent(txtDestino)
                                    .addComponent(txtPeso))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel10)
                .addGap(9, 9, 9)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(txtOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(txtDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPeso, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregarArista)
                    .addComponent(btnEliminarArista))
                .addContainerGap(15, Short.MAX_VALUE))
        );

        btnEliminarVertice.setText("Eliminar Vertice");
        btnEliminarVertice.addActionListener(this::btnEliminarVerticeActionPerformed);

        btnDFS.setText("DFS");
        btnDFS.addActionListener(this::btnDFSActionPerformed);

        btnConectividad.setText("Conectividad");
        btnConectividad.addActionListener(this::btnConectividadActionPerformed);

        btnBFS.setText("BFS");
        btnBFS.addActionListener(this::btnBFSActionPerformed);

        btnMatriz.setText("Matriz");
        btnMatriz.addActionListener(this::btnMatrizActionPerformed);

        btnDestruir.setText("Destruir");
        btnDestruir.addActionListener(this::btnDestruirActionPerformed);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEliminarVertice)
                    .addComponent(btnConectividad)
                    .addComponent(btnDFS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 69, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBFS)
                    .addComponent(btnMatriz)
                    .addComponent(btnDestruir))
                .addGap(57, 57, 57))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnEliminarVertice)
                    .addComponent(btnBFS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDFS)
                    .addComponent(btnMatriz))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConectividad)
                    .addComponent(btnDestruir))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel11.setText("Optimizacion");

        jLabel12.setText("Origen");

        cmbOrigen.addActionListener(this::cmbOrigenActionPerformed);

        jLabel13.setText("Destino");

        jLabel14.setText("Algoritmo");

        cmbDestino.addActionListener(this::cmbDestinoActionPerformed);

        cmbAlgoritmo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Dijkstra", "Bellman-Ford" }));
        cmbAlgoritmo.addActionListener(this::cmbAlgoritmoActionPerformed);

        btnCalcularRuta.setText("Calcular Ruta");
        btnCalcularRuta.addActionListener(this::btnCalcularRutaActionPerformed);

        btnComparar.setText("Comparar");
        btnComparar.addActionListener(this::btnCompararActionPerformed);

        btnLimpiarTraza.setText("Limpiar");
        btnLimpiarTraza.addActionListener(this::btnLimpiarTrazaActionPerformed);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(143, 143, 143)
                        .addComponent(jLabel11))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addGap(44, 44, 44)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmbOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmbAlgoritmo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnLimpiarTraza)
                            .addComponent(btnComparar)
                            .addComponent(btnCalcularRuta))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(cmbOrigen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCalcularRuta))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(cmbDestino, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnComparar))
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(cmbAlgoritmo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(8, 8, 8)
                        .addComponent(btnLimpiarTraza)))
                .addContainerGap(33, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel4))
                                .addGap(44, 44, 44)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGap(27, 27, 27)
                                        .addComponent(jLabel1))
                                    .addComponent(txtNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                                    .addComponent(txtId)
                                    .addComponent(txtTipo)
                                    .addComponent(txtEstado)
                                    .addComponent(txtCapacidad))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel2)
                                    .addComponent(txtId, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(4, 4, 4)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6)
                                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(txtTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(34, 34, 34))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtEstado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel4)))
                        .addGap(5, 5, 5)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(txtCapacidad, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(24, 24, 24)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        txtConsola.setColumns(20);
        txtConsola.setRows(5);
        jScrollPane1.setViewportView(txtConsola);

        txtTraza.setColumns(20);
        txtTraza.setFont(new java.awt.Font("Monospaced", 0, 12)); // NOI18N
        txtTraza.setRows(5);
        jScrollPane2.setViewportView(txtTraza);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE)
                    .addComponent(jScrollPane2))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 355, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout panelVisualLayout = new javax.swing.GroupLayout(panelVisual);
        panelVisual.setLayout(panelVisualLayout);
        panelVisualLayout.setHorizontalGroup(
            panelVisualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
        );
        panelVisualLayout.setVerticalGroup(
            panelVisualLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(panelVisual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(panelVisual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtIdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtIdActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtIdActionPerformed

    private void txtCapacidadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCapacidadActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCapacidadActionPerformed

    private void cmbOrigenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbOrigenActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbOrigenActionPerformed

    private void cmbDestinoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbDestinoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDestinoActionPerformed

    private void cmbAlgoritmoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbAlgoritmoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbAlgoritmoActionPerformed

    private void btnAgregarVerticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarVerticeActionPerformed
     agregarVertice();
    }//GEN-LAST:event_btnAgregarVerticeActionPerformed

    private void btnBuscarVerticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarVerticeActionPerformed
     buscarVertice();
    }//GEN-LAST:event_btnBuscarVerticeActionPerformed

    private void btnAgregarAristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarAristaActionPerformed
     agregarArista();
    }//GEN-LAST:event_btnAgregarAristaActionPerformed

    private void btnEliminarAristaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarAristaActionPerformed
     eliminarArista();
    }//GEN-LAST:event_btnEliminarAristaActionPerformed

    private void btnEliminarVerticeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarVerticeActionPerformed
     eliminarVertice();
    }//GEN-LAST:event_btnEliminarVerticeActionPerformed

    private void btnDFSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDFSActionPerformed
    ejecutarDFS();
    }//GEN-LAST:event_btnDFSActionPerformed

    private void btnConectividadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConectividadActionPerformed
     evaluarConectividad();
    }//GEN-LAST:event_btnConectividadActionPerformed

    private void btnBFSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBFSActionPerformed
      ejecutarBFS();
    }//GEN-LAST:event_btnBFSActionPerformed

    private void btnMatrizActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatrizActionPerformed
    mostrarMatriz();
    }//GEN-LAST:event_btnMatrizActionPerformed

    private void btnDestruirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDestruirActionPerformed
     destruirGrafo();
    }//GEN-LAST:event_btnDestruirActionPerformed

    private void btnCalcularRutaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalcularRutaActionPerformed
      calcularRutaOptima();
    }//GEN-LAST:event_btnCalcularRutaActionPerformed

    private void btnCompararActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCompararActionPerformed
      compararAlgoritmos();
    }//GEN-LAST:event_btnCompararActionPerformed

    private void btnLimpiarTrazaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarTrazaActionPerformed
      limpiarTraza();
    }//GEN-LAST:event_btnLimpiarTrazaActionPerformed

   
    public static void main(String args[]) {
       
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
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        
        java.awt.EventQueue.invokeLater(() -> new InterfazGrafo().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarArista;
    private javax.swing.JButton btnAgregarVertice;
    private javax.swing.JButton btnBFS;
    private javax.swing.JButton btnBuscarVertice;
    private javax.swing.JButton btnCalcularRuta;
    private javax.swing.JButton btnComparar;
    private javax.swing.JButton btnConectividad;
    private javax.swing.JButton btnDFS;
    private javax.swing.JButton btnDestruir;
    private javax.swing.JButton btnEliminarArista;
    private javax.swing.JButton btnEliminarVertice;
    private javax.swing.JButton btnLimpiarTraza;
    private javax.swing.JButton btnMatriz;
    private javax.swing.JComboBox<String> cmbAlgoritmo;
    private javax.swing.JComboBox<String> cmbDestino;
    private javax.swing.JComboBox<String> cmbOrigen;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel panelVisual;
    private javax.swing.JTextField txtCapacidad;
    private javax.swing.JTextArea txtConsola;
    private javax.swing.JTextField txtDestino;
    private javax.swing.JTextField txtEstado;
    private javax.swing.JTextField txtId;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtOrigen;
    private javax.swing.JTextField txtPeso;
    private javax.swing.JTextField txtTipo;
    private javax.swing.JTextArea txtTraza;
    // End of variables declaration//GEN-END:variables
}
