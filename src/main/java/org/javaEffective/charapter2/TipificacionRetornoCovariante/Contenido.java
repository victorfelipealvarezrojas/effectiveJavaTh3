package org.javaEffective.charapter2.TipificacionRetornoCovariante;

public abstract class Contenido {
    private String titulo;

    public Contenido(String titulo) {
        this.titulo = titulo;
    }

    // Método que será sobrescrito con tipificación de retorno covariante
    public abstract Contenido editarTitulo(String nuevoTitulo);

    public String getTitulo() {
        return titulo;
    }

    protected void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    // Otros métodos comunes para todos los contenidos...
}