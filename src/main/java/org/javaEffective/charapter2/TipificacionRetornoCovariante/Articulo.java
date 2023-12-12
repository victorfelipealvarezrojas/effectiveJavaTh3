package org.javaEffective.charapter2.TipificacionRetornoCovariante;

public class Articulo extends Contenido {
    private String texto;

    public Articulo(String titulo, String texto) {
        super(titulo);
        this.texto = texto;
    }

    // Sobrescribiendo con tipificación de retorno covariante
    @Override
    public Articulo editarTitulo(String nuevoTitulo) {
        setTitulo(nuevoTitulo);
        return this;
    }

    // Métodos específicos para Articulo...
}