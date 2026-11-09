package back_end;

import java.util.List;

public class ResultadoCamino {
    private List<String> ruta;
    private double costoTotal;
    private String trazaDetallada;
    private int evaluaciones;
    private int descartes;

    public ResultadoCamino(List<String> ruta, double costoTotal, String trazaDetallada, int evaluaciones, int descartes) {
        this.ruta = ruta;
        this.costoTotal = costoTotal;
        this.trazaDetallada = trazaDetallada;
        this.evaluaciones = evaluaciones;
        this.descartes = descartes;
    }

    public List<String> getRuta() { return ruta; }
    public double getCostoTotal() { return costoTotal; }
    public String getTrazaDetallada() { return trazaDetallada; }
    public int getEvaluaciones() { return evaluaciones; }
    public int getDescartes() { return descartes; }
}