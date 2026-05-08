import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class Alumno {
    private final String codigo;
    private String nombre;
    private String carrera;
    private boolean activo = true;
    private final Set<String> cursosAprobados = new LinkedHashSet<>();
    private final Set<String> cursosMatriculados = new LinkedHashSet<>();

    public Alumno(String codigo, String nombre, String carrera) {
        if (isBlank(codigo) || isBlank(nombre) || isBlank(carrera)) {
            throw new IllegalArgumentException("Código, nombre y carrera son obligatorios.");
        }
        this.codigo = codigo.trim().toUpperCase();
        this.nombre = nombre.trim();
        this.carrera = carrera.trim();
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCarrera() {
        return carrera;
    }

    public boolean isActivo() {
        return activo;
    }

    public void editarDatos(String nuevoNombre, String nuevaCarrera) {
        if (isBlank(nuevoNombre) || isBlank(nuevaCarrera)) {
            throw new IllegalArgumentException("Nombre y carrera son obligatorios.");
        }
        this.nombre = nuevoNombre.trim();
        this.carrera = nuevaCarrera.trim();
    }

    public void darDeBaja() {
        this.activo = false;
    }

    public void registrarCursoAprobado(String idCurso) {
        String normalizado = normalizarIdCurso(idCurso);
        if (normalizado != null) {
            cursosAprobados.add(normalizado);
        }
    }

    public boolean tieneCursoAprobado(String idCurso) {
        String normalizado = normalizarIdCurso(idCurso);
        return normalizado != null && cursosAprobados.contains(normalizado);
    }

    public void registrarMatricula(String idCurso) {
        String normalizado = normalizarIdCurso(idCurso);
        if (normalizado != null) {
            cursosMatriculados.add(normalizado);
        }
    }

    public void retirarMatricula(String idCurso) {
        String normalizado = normalizarIdCurso(idCurso);
        if (normalizado != null) {
            cursosMatriculados.remove(normalizado);
        }
    }

    public boolean yaEstaMatriculado(String idCurso) {
        String normalizado = normalizarIdCurso(idCurso);
        return normalizado != null && cursosMatriculados.contains(normalizado);
    }

    public Set<String> getCursosAprobados() {
        return Collections.unmodifiableSet(cursosAprobados);
    }

    public Set<String> getCursosMatriculados() {
        return Collections.unmodifiableSet(cursosMatriculados);
    }

    private String normalizarIdCurso(String idCurso) {
        if (isBlank(idCurso)) {
            return null;
        }
        return idCurso.trim().toUpperCase();
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
