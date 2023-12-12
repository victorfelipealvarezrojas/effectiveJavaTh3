package org.javaEffective.charapter1.afterJava8;

/** @FunctionalInterface, antes las interfaces no podían tener métodos estáticos y por eso se creaban
    clases compañeras no instanciables con métodos estáticos. Ahora las interfaces pueden tener
    métodos estáticos y por eso la clase compañera no es necesaria.
*/
public interface Logger {
    void log(String message);
}

