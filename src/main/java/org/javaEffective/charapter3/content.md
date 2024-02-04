Aunque la clase Object es una clase concreta, está diseñada principalmente para ser extendida. Todos sus métodos no finales (equals, hashCode, toString, clone y finalize) tienen contratos generales explícitos porque están diseñados para ser sobrescritos. Es responsabilidad de cualquier clase que sobrescriba estos métodos obedecer sus contratos generales; no hacerlo impedirá que otras clases que dependan de los contratos (como HashMap y HashSet) funcionen correctamente en conjunto con la clase. Este capítulo te indica cuándo y cómo sobrescribir los métodos no finales de Object.

El método finalize se omite en este capítulo porque se discutió en el ítem 8. Aunque Comparable.compareTo no es un método de Object, se discute en este capítulo debido a su carácter similar.


