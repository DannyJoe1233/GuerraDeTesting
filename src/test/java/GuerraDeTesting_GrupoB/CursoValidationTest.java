package GuerraDeTesting_GrupoB;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CursoValidationTest extends BaseTestConfig {

    @ParameterizedTest
    @ValueSource(strings = {"123", "---"})
    void registrarCursoRechazaNombresInvalidos(String nombre) {
        Curso curso = new Curso("INF-99", nombre, 3, 10, "Lunes 08:00-10:00");
        String resultado = sistema.registrarCurso(curso);
        assertEquals("FALLO RF02: El nombre del curso es inválido.", resultado);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void constructorCursoRechazaNombreVacio(String nombre) {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new Curso("INF-99", nombre, 3, 10, "Lunes 08:00-10:00")
        );
        assertTrue(ex.getMessage().contains("obligatorios"));
    }

    @Test
    void registrarCursoRechazaRequisitoAutoReferenciado() {
        Curso curso = new Curso(
                "INF-01",
                "Algoritmos",
                3,
                10,
                "Lunes 08:00-10:00",
                List.of("INF-01")
        );
        String resultado = sistema.registrarCurso(curso);
        assertEquals("FALLO RF02: El curso no puede ser requisito de sí mismo.", resultado);
    }
}
