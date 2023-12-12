package org.javaEffective.charapter2.TipificacionRetornoCovariante;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TipificacionRetornoCovarianteTest {
    @Test
    public void testEditarTituloEnArticulo() {
        Articulo articulo = new Articulo("Titulo Original", "Este es el texto del articulo.");
        Articulo articuloEditado = articulo.editarTitulo("Titulo Nuevo");

        // Comprobamos que el título se ha actualizado correctamente
        assertEquals("Titulo Nuevo", articuloEditado.getTitulo());

        // Comprobamos que el método devuelve una instancia de Articulo
        assertTrue(articuloEditado instanceof Articulo);
    }

    @Test
    public void testEditarTituloEnVideo() {
        Video video = new Video("Video Original", 120);
        Video videoEditado = video.editarTitulo("Video Nuevo");

        // Comprobamos que el título se ha actualizado correctamente
        assertEquals("Video Nuevo", videoEditado.getTitulo());

        // Comprobamos que el método devuelve una instancia de Video
        assertTrue(videoEditado instanceof Video);
    }
}