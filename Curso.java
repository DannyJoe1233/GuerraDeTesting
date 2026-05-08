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
    private final Set<String> alumnosMatriculados = new LinkedHashSet<>();
    private final Queue<String> listaEspera = new ArrayDeque<>();

    public Curso(String id, String nombre, int creditos, int cupos, String horario) {
        this(id, nombre, creditos, cupos, horario, Collections.emptyList());
    }

    public Curso(String id, String nombre, int creditos, int cupos, String horario, List<String> requisitos) {
        this.id = id;
        this.nombre = nombre;
        this.creditos = creditos;
        this.cuposMaximos = cupos;
        this.horario = horario;
        this.requisitos = new ArrayList<>();
        if (requisitos != null) {
            for (String requisito : requisitos) {
                if (requisito != null && !requisito.trim().isEmpty()) {
                    this.requisitos.add(requisito.trim().toUpperCase());
                }
            }
        }
    }

    public boolean tieneCupo() {
        return alumnosMatriculados.size() < cuposMaximos;
    }

    public boolean matricularAlumno(String codigoAlumno) {
        if (!tieneCupo()) {
            return false;
        }
        return alumnosMatriculados.add(codigoAlumno.toUpperCase());
    }

    public boolean retirarAlumno(String codigoAlumno) {
        return alumnosMatriculados.remove(codigoAlumno.toUpperCase());
    }

    public boolean estaMatriculado(String codigoAlumno) {
        return alumnosMatriculados.contains(codigoAlumno.toUpperCase());
    }

    public boolean agregarListaEspera(String codigoAlumno) {
        String normalizado = codigoAlumno.toUpperCase();
        if (listaEspera.contains(normalizado)) {
            return false;
        }
        listaEspera.offer(normalizado);
        return true;
    }

    public boolean retirarDeListaEspera(String codigoAlumno) {
        return listaEspera.remove(codigoAlumno.toUpperCase());
    }

    public String obtenerSiguienteEnEspera() {
        return listaEspera.poll();
    }

    public boolean estaEnListaEspera(String codigoAlumno) {
        return listaEspera.contains(codigoAlumno.toUpperCase());
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
}
