package back_end;

import java.util.*;

public class GrafoPonderado {
    private Map<String, Vertice> vertices;
    private List<Arista> aristas;
    private boolean esDirigido;

    public GrafoPonderado(boolean esDirigido) {
        this.vertices = new LinkedHashMap<>();
        this.aristas = new ArrayList<>();
        this.esDirigido = esDirigido;
    }

    public boolean agregarVertice(Vertice v) {
        if (v == null || v.getId() == null || v.getId().trim().isEmpty()) {
            throw new IllegalArgumentException("El ID del vértice no puede estar vacío.");
        }
        if (vertices.containsKey(v.getId())) {
            return false;
        }
        vertices.put(v.getId(), v);
        return true;
    }

    public boolean agregarArista(String idOrigen, String idDestino, double peso) {
        if (peso <= 0 || Double.isNaN(peso) || Double.isInfinite(peso)) {
            throw new IllegalArgumentException("El peso debe ser un número finito y estrictamente positivo.");
        }
        Vertice origen = vertices.get(idOrigen);
        Vertice destino = vertices.get(idDestino);

        if (origen == null || destino == null) {
            return false;
        }

        aristas.removeIf(a -> a.getOrigen().getId().equals(idOrigen) && a.getDestino().getId().equals(idDestino));
        aristas.add(new Arista(origen, destino, peso));

        if (!esDirigido) {
            aristas.removeIf(a -> a.getOrigen().getId().equals(idDestino) && a.getDestino().getId().equals(idOrigen));
            aristas.add(new Arista(destino, origen, peso));
        }
        return true;
    }

    public boolean eliminarVertice(String id) {
        if (!vertices.containsKey(id)) return false;
        aristas.removeIf(a -> a.getOrigen().getId().equals(id) || a.getDestino().getId().equals(id));
        vertices.remove(id);
        return true;
    }

    public boolean eliminarArista(String idOrigen, String idDestino) {
        boolean eliminada = aristas.removeIf(a -> a.getOrigen().getId().equals(idOrigen) && a.getDestino().getId().equals(idDestino));
        if (!esDirigido) {
            aristas.removeIf(a -> a.getOrigen().getId().equals(idDestino) && a.getDestino().getId().equals(idOrigen));
        }
        return eliminada;
    }

    public Vertice buscarVertice(String id) { return vertices.get(id); }
    public List<Vertice> getVertices() { return new ArrayList<>(vertices.values()); }
    public List<Arista> getAristas() { return aristas; }

    public void destruirGrafo() {
        vertices.clear();
        aristas.clear();
    }

    public List<String> recorridoBFS(String idInicio) {
        if (!vertices.containsKey(idInicio)) return Collections.emptyList();
        List<String> resultado = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        Queue<String> cola = new LinkedList<>();

        cola.add(idInicio);
        visitados.add(idInicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            resultado.add(actual);
            for (Arista a : aristas) {
                if (a.getOrigen().getId().equals(actual)) {
                    String vecino = a.getDestino().getId();
                    if (!visitados.contains(vecino)) {
                        visitados.add(vecino);
                        cola.add(vecino);
                    }
                }
            }
        }
        return resultado;
    }

    public List<String> recorridoDFS(String idInicio) {
        if (!vertices.containsKey(idInicio)) return Collections.emptyList();
        List<String> resultado = new ArrayList<>();
        Set<String> visitados = new HashSet<>();
        dfsAux(idInicio, visitados, resultado);
        return resultado;
    }

    private void dfsAux(String idActual, Set<String> visitados, List<String> resultado) {
        visitados.add(idActual);
        resultado.add(idActual);
        for (Arista a : aristas) {
            if (a.getOrigen().getId().equals(idActual)) {
                String vecino = a.getDestino().getId();
                if (!visitados.contains(vecino)) {
                    dfsAux(vecino, visitados, resultado);
                }
            }
        }
    }

    public String obtenerMatrizAdyacencia() {
        if (vertices.isEmpty()) return "El grafo está vacío.";
        List<String> keys = new ArrayList<>(vertices.keySet());
        int n = keys.size();
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("%-10s", ""));
        for (String k : keys) sb.append(String.format("%-10s", k));
        sb.append("\n");

        for (int i = 0; i < n; i++) {
            String orig = keys.get(i);
            sb.append(String.format("%-10s", orig));
            for (int j = 0; j < n; j++) {
                String dest = keys.get(j);
                if (orig.equals(dest)) {
                    sb.append(String.format("%-10s", "0.0"));
                } else {
                    double peso = getPesoEntre(orig, dest);
                    if (peso > 0) sb.append(String.format("%-10.1f", peso));
                    else sb.append(String.format("%-10s", "∞"));
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private double getPesoEntre(String orig, String dest) {
        for (Arista a : aristas) {
            if (a.getOrigen().getId().equals(orig) && a.getDestino().getId().equals(dest)) return a.getPeso();
        }
        return -1;
    }

    public String evaluarConectividad() {
        if (vertices.isEmpty()) return "El grafo está vacío.";
        int n = vertices.size();
        boolean fuertementeConectado = true;

        for (String v : vertices.keySet()) {
            if (recorridoBFS(v).size() < n) {
                fuertementeConectado = false;
                break;
            }
        }
        if (fuertementeConectado) return "El grafo está FUERTEMENTE CONECTADO.";

        Set<String> visitadosDebil = new HashSet<>();
        Queue<String> cola = new LinkedList<>();
        String inicio = vertices.keySet().iterator().next();
        cola.add(inicio);
        visitadosDebil.add(inicio);

        while (!cola.isEmpty()) {
            String actual = cola.poll();
            for (Arista a : aristas) {
                String u = a.getOrigen().getId();
                String v = a.getDestino().getId();
                String vecino = u.equals(actual) ? v : (v.equals(actual) ? u : null);
                if (vecino != null && !visitadosDebil.contains(vecino)) {
                    visitadosDebil.add(vecino);
                    cola.add(vecino);
                }
            }
        }

        return visitadosDebil.size() == n ? "El grafo está DÉBILMENTE CONECTADO." : "El grafo está DESCONECTADO.";
    }

    public ResultadoCamino calcularDijkstra(String origenId, String destinoId) {
        if (!vertices.containsKey(origenId) || !vertices.containsKey(destinoId)) {
            throw new IllegalArgumentException("Origen o destino no existen en el grafo.");
        }

        StringBuilder traza = new StringBuilder();
        int evals = 0, desc = 0;

        traza.append("========== EJECUCIÓN: ALGORITMO DE DIJKSTRA ==========\n");
        traza.append("1. FASE DE INICIALIZACIÓN:\n");

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();
        Set<String> visitados = new HashSet<>();

        for (String id : vertices.keySet()) {
            dist.put(id, Double.POSITIVE_INFINITY);
            prev.put(id, null);
            traza.append(String.format("   Nodo [%s]: Distancia Inicial = ∞\n", id));
        }

        dist.put(origenId, 0.0);
        traza.append(String.format("   -> Nodo Origen [%s] asignado a Distancia = 0.0\n\n", origenId));
        traza.append("2. PROCESO DE EXPLORACIÓN Y RELAJACIÓN DE ARISTAS:\n");

        PriorityQueue<String> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(origenId);

        while (!pq.isEmpty()) {
            String u = pq.poll();
            if (visitados.contains(u)) continue;
            visitados.add(u);

            traza.append(String.format(" -> Extraído de Prioridad: Nodo [%s] (Distancia actual: %.2f)\n", u, dist.get(u)));

            for (Arista a : aristas) {
                if (a.getOrigen().getId().equals(u)) {
                    String v = a.getDestino().getId();
                    double peso = a.getPeso();
                    evals++;

                    if (peso < 0) throw new IllegalStateException("Dijkstra no admite pesos negativos.");

                    double alt = dist.get(u) + peso;
                    if (alt < dist.get(v)) {
                        traza.append(String.format("    [ACEPTADO] Ruta %s -> %s | Peso: %.2f | Nueva dist[%s] = %.2f (Antes: %s)\n",
                                u, v, peso, v, alt, dist.get(v) == Double.POSITIVE_INFINITY ? "∞" : String.format("%.2f", dist.get(v))));
                        dist.put(v, alt);
                        prev.put(v, u);
                        pq.add(v);
                    } else {
                        desc++;
                        traza.append(String.format("    [DESCARTADO] Ruta %s -> %s | Costo alternativo (%.2f) >= Costo registrado (%.2f)\n",
                                u, v, alt, dist.get(v)));
                    }
                }
            }
        }
        return construirResultado("Dijkstra", origenId, destinoId, dist, prev, traza, evals, desc);
    }


    public ResultadoCamino calcularBellmanFord(String origenId, String destinoId) {
        if (!vertices.containsKey(origenId) || !vertices.containsKey(destinoId)) {
            throw new IllegalArgumentException("Origen o destino no existen en el grafo.");
        }

        StringBuilder traza = new StringBuilder();
        int evals = 0, desc = 0;

        traza.append("========== EJECUCIÓN: ALGORITMO DE BELLMAN-FORD ==========\n");
        traza.append("1. FASE DE INICIALIZACIÓN:\n");

        Map<String, Double> dist = new HashMap<>();
        Map<String, String> prev = new HashMap<>();

        for (String id : vertices.keySet()) {
            dist.put(id, Double.POSITIVE_INFINITY);
            prev.put(id, null);
            traza.append(String.format("   Nodo [%s]: Distancia Inicial = ∞\n", id));
        }

        dist.put(origenId, 0.0);
        traza.append(String.format("   -> Nodo Origen [%s] asignado a Distancia = 0.0\n\n", origenId));
        traza.append("2. PASOS DE RELAJACIÓN SISTEMÁTICA (V - 1 Iteraciones):\n");

        int totalV = vertices.size();
        for (int i = 1; i < totalV; i++) {
            traza.append(String.format(" --- Iteración %d de %d ---\n", i, totalV - 1));
            boolean huboCambios = false;

            for (Arista a : aristas) {
                String u = a.getOrigen().getId();
                String v = a.getDestino().getId();
                double peso = a.getPeso();
                evals++;

                if (dist.get(u) != Double.POSITIVE_INFINITY) {
                    double alt = dist.get(u) + peso;
                    if (alt < dist.get(v)) {
                        traza.append(String.format("    [ACEPTADO] Arista %s -> %s (Peso %.2f) reduce dist[%s] a %.2f\n", u, v, peso, v, alt));
                        dist.put(v, alt);
                        prev.put(v, u);
                        huboCambios = true;
                    } else {
                        desc++;
                        traza.append(String.format("    [DESCARTADO] Arista %s -> %s (Peso %.2f) no mejora dist[%s] (%.2f)\n", u, v, peso, v, dist.get(v)));
                    }
                }
            }
            if (!huboCambios) {
                traza.append("   (Convergencia alcanzada: Sin cambios en esta iteración).\n");
                break;
            }
        }
        return construirResultado("Bellman-Ford", origenId, destinoId, dist, prev, traza, evals, desc);
    }

    private ResultadoCamino construirResultado(String algo, String origenId, String destinoId, 
            Map<String, Double> dist, Map<String, String> prev, StringBuilder traza, int evals, int desc) {
        
        traza.append("\n3. RESULTADO Y SECUENCIA DE RUTA:\n");
        double costoFinal = dist.get(destinoId);

        if (costoFinal == Double.POSITIVE_INFINITY) {
            traza.append(String.format("   [INACCESIBLE] No existe ningún camino posible entre [%s] y [%s].\n", origenId, destinoId));
            return new ResultadoCamino(Collections.emptyList(), Double.POSITIVE_INFINITY, traza.toString(), evals, desc);
        }

        LinkedList<String> ruta = new LinkedList<>();
        String actual = destinoId;
        while (actual != null) {
            ruta.addFirst(actual);
            actual = prev.get(actual);
        }

        traza.append(String.format("   Secuencia Óptima: %s\n", String.join(" -> ", ruta)));
        traza.append(String.format("   Costo Total Mínimo: %.2f\n", costoFinal));

        return new ResultadoCamino(ruta, costoFinal, traza.toString(), evals, desc);
    }
}