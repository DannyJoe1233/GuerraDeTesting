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
        this.codigo = codigo.toUpperCase();
        this.nombre = nombre;
        this.carrera = carrera;
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
        this.nombre = nuevoNombre;
        this.carrera = nuevaCarrera;
    }

    public void darDeBaja() {
        this.activo = false;
    }

    public void registrarCursoAprobado(String idCurso) {
        cursosAprobados.add(idCurso.toUpperCase());
    }

    public boolean tieneCursoAprobado(String idCurso) {
        return cursosAprobados.contains(idCurso.toUpperCase());
    }

    public void registrarMatricula(String idCurso) {
        cursosMatriculados.add(idCurso.toUpperCase());
    }

    public void retirarMatricula(String idCurso) {
        cursosMatriculados.remove(idCurso.toUpperCase());
    }

    public boolean yaEstaMatriculado(String idCurso) {
        return cursosMatriculados.contains(idCurso.toUpperCase());
    }

    public Set<String> getCursosAprobados() {
        return Collections.unmodifiableSet(cursosAprobados);
    }

    public Set<String> getCursosMatriculados() {
        return Collections.unmodifiableSet(cursosMatriculados);
    }
}
