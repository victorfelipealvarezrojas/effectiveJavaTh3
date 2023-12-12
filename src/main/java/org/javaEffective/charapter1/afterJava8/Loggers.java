package org.javaEffective.charapter1.afterJava8;

/**  @Clase_compañera_no_instanciable, antes las interfaces no podían tener métodos estáticos
     y por eso se creaban clases compañeras no instanciables con métodos estáticos. Ahora
     las interfaces pueden tener métodos estáticos y por eso esta clase compañera no es
     necesaria.
 */
public class Loggers {
    public static Logger getFileLogger() {
        return (Logger) new Exception("Not implemented yet");
    }

    public static Logger getConsoleLogger() {
        return (Logger) new Exception("Not implemented yet");
    }
}
