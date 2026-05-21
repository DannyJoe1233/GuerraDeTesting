package GuerraDeTesting_GrupoB;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Curso {
    private final String id;
    private final String nombre;
    private final int creditos;
    private final int cuposMaximos;
    private final String horario;
    private final List<String> requisitos;
    private final boolean requisitoAutoReferenciado;
    private final Set<String> alumnosMatriculados = new LinkedHashSet<>();
    private final Queue<String> listaEspera = new ArrayDeque<>();

    public Curso(String id, String nombre, int creditos, int cupos, String horario) {
        this(id, nombre, creditos, cupos, horario, Collections.emptyList());
    }

    // FIX [Persistence]: JSON hydration constructor required
    @JsonCreator
    public Curso(
            @JsonProperty("id") String id,
            @JsonProperty("nombre") String nombre,
            @JsonProperty("creditos") int creditos,
            @JsonProperty("cuposMaximos") int cuposMaximos,
            @JsonProperty("horario") String horario,
            @JsonProperty("requisitos") List<String> requisitos,
            @JsonProperty("alumnosMatriculados") List<String> alumnosMatriculados,
            @JsonProperty("listaEspera") List<String> listaEspera
    ) {
        this(id, nombre, creditos, cuposMaximos, horario, requisitos);
        if (alumnosMatriculados != null) {
            for (String codigoAlumno : alumnosMatriculados) {
                String normalizado = normalizarCodigo(codigoAlumno);
                if (normalizado != null) {
                    this.alumnosMatriculados.add(normalizado);
                }
            }
        }
        if (listaEspera != null) {
            for (String codigoAlumno : listaEspera) {
                String normalizado = normalizarCodigo(codigoAlumno);
                if (normalizado != null) {
                    this.listaEspera.offer(normalizado);
                }
            }
        }
    }

    public Curso(String id, String nombre, int creditos, int cupos, String horario, List<String> requisitos) {
        if (isBlank(id) || isBlank(nombre) || isBlank(horario)) {
            throw new IllegalArgumentException("ID, nombre y horario del curso son obligatorios.");
        }
        if (creditos <= 0) {
            throw new IllegalArgumentException("Los créditos del curso deben ser mayores a 0.");
        }
        if (cupos <= 0) {
            throw new IllegalArgumentException("El cupo máximo del curso debe ser mayor a 0.");
        }

        this.id = id.trim().toUpperCase();
        this.nombre = nombre.trim();
        this.creditos = creditos;
        this.cuposMaximos = cupos;
        this.horario = horario.trim();
        this.requisitos = new ArrayList<>();
        boolean autoReferenciado = false;
        if (requisitos != null) {
            for (String requisito : requisitos) {
                if (requisito != null && !requisito.trim().isEmpty()) {
                    String normalizado = requisito.trim().toUpperCase();
                    if (normalizado.equals(this.id)) {
                        autoReferenciado = true;
                        continue;
                    }
                    if (!this.requisitos.contains(normalizado)) {
                        this.requisitos.add(normalizado);
                    }
                }
            }
        }
        this.requisitoAutoReferenciado = autoReferenciado;
    }

    public boolean tieneCupo() {
        return alumnosMatriculados.size() < cuposMaximos;
    }

    public boolean matricularAlumno(String codigoAlumno) {
        String normalizado = normalizarCodigo(codigoAlumno);
        if (normalizado == null) {
            return false;
        }
        if (!tieneCupo()) {
            return false;
        }
        return alumnosMatriculados.add(normalizado);
    }

    public boolean retirarAlumno(String codigoAlumno) {
        String normalizado = normalizarCodigo(codigoAlumno);
        if (normalizado == null) {
            return false;
        }
        return alumnosMatriculados.remove(normalizado);
    }

    public boolean estaMatriculado(String codigoAlumno) {
        String normalizado = normalizarCodigo(codigoAlumno);
        return normalizado != null && alumnosMatriculados.contains(normalizado);
    }

    public boolean agregarListaEspera(String codigoAlumno) {
        String normalizado = normalizarCodigo(codigoAlumno);
        if (normalizado == null) {
            return false;
        }
        if (listaEspera.contains(normalizado)) {
            return false;
        }
        listaEspera.offer(normalizado);
        return true;
    }

    public boolean retirarDeListaEspera(String codigoAlumno) {
        String normalizado = normalizarCodigo(codigoAlumno);
        if (normalizado == null) {
            return false;
        }
        return listaEspera.remove(normalizado);
    }

    public String obtenerSiguienteEnEspera() {
        return listaEspera.poll();
    }

    public boolean estaEnListaEspera(String codigoAlumno) {
        String normalizado = normalizarCodigo(codigoAlumno);
        return normalizado != null && listaEspera.contains(normalizado);
    }

    public String getHorario() { return horario; }
    public String getNombre() { return nombre; }
    public String getId() { return id; }
    public int getCreditos() { return creditos; }
    public int getCuposOcupados() { return alumnosMatriculados.size(); }
    public int getCuposMaximos() { return cuposMaximos; }
    public int getCantidadEspera() { return listaEspera.size(); }
    public List<String> getRequisitos() { return Collections.unmodifiableList(requisitos); }
    public List<String> getAlumnosMatriculados() { return new ArrayList<>(alumnosMatriculados); }
    public List<String> getListaEspera() { return new ArrayList<>(listaEspera); }
    boolean tieneRequisitoAutoReferenciado() { return requisitoAutoReferenciado; }

    private String normalizarCodigo(String codigoAlumno) {
        if (isBlank(codigoAlumno)) {
            return null;
        }
        return codigoAlumno.trim().toUpperCase();
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
