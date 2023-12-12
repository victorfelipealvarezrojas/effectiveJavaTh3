package org.javaEffective.charapter2.TipificacionRetornoCovariante;

public class Video extends Contenido {
    private int duracion; // Duración en segundos

    public Video(String titulo, int duracion) {
        super(titulo);
        this.duracion = duracion;
    }

    // Sobrescribiendo con tipificación de retorno covariante
    @Override
    public Video editarTitulo(String nuevoTitulo) {
        setTitulo(nuevoTitulo);
        return this;
    }

    // Métodos específicos para Video...
}
