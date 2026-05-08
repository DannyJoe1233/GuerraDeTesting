import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SistemaMatricula {
    private final Map<String, Alumno> alumnos = new LinkedHashMap<>();
    private final Map<String, Curso> cursos = new LinkedHashMap<>();

    public String registrarAlumno(String codigo, String nombre, String carrera) {
        if (isBlank(codigo) || isBlank(nombre) || isBlank(carrera)) {
            return "FALLO RF01: Código, nombre y carrera son obligatorios.";
        }
        String normalizado = codigo.trim().toUpperCase();
        if (alumnos.containsKey(normalizado)) {
            return "FALLO RF01: El alumno " + normalizado + " ya existe.";
        }
        alumnos.put(normalizado, new Alumno(normalizado, nombre.trim(), carrera.trim()));
        return "ÉXITO RF01: Alumno " + normalizado + " registrado.";
    }

    public String editarAlumno(String codigo, String nuevoNombre, String nuevaCarrera) {
        Alumno alumno = buscarAlumno(codigo);
        if (alumno == null) {
            return "FALLO RF01: Alumno no encontrado.";
        }
        if (isBlank(nuevoNombre) || isBlank(nuevaCarrera)) {
            return "FALLO RF01: El nombre y la carrera no pueden quedar vacíos.";
        }
        alumno.editarDatos(nuevoNombre.trim(), nuevaCarrera.trim());
        return "ÉXITO RF01: Datos de " + alumno.getCodigo() + " actualizados.";
    }

    public String darBajaAlumno(String codigo) {
        Alumno alumno = buscarAlumno(codigo);
        if (alumno == null) {
            return "FALLO RF01: Alumno no encontrado.";
        }
        if (!alumno.isActivo()) {
            return "FALLO RF01: El alumno ya estaba de baja.";
        }

        for (String idCurso : new ArrayList<>(alumno.getCursosMatriculados())) {
            retirarCurso(alumno.getCodigo(), idCurso);
        }

        alumno.darDeBaja();
        return "ÉXITO RF01: Alumno " + alumno.getCodigo() + " dado de baja.";
    }

    public String registrarCursoAprobado(String codigoAlumno, String idCurso) {
        Alumno alumno = buscarAlumno(codigoAlumno);
        if (alumno == null) {
            return "FALLO RF06: Alumno no encontrado.";
        }
        if (isBlank(idCurso)) {
            return "FALLO RF06: Debes indicar un curso aprobado.";
        }
        alumno.registrarCursoAprobado(idCurso.trim().toUpperCase());
        return "ÉXITO RF06: Curso aprobado registrado para " + alumno.getCodigo() + ".";
    }

    public String registrarCurso(Curso curso) {
        if (curso == null) {
            return "FALLO RF02: El curso es inválido.";
        }
        String id = curso.getId().toUpperCase();
        if (cursos.containsKey(id)) {
            return "FALLO RF02: El curso " + id + " ya existe.";
        }
        cursos.put(id, curso);
        return "ÉXITO RF02: Curso " + id + " registrado en catálogo.";
    }

    public String matricularAlumnoEnCurso(String codigoAlumno, String idCurso) {
        Alumno alumno = buscarAlumno(codigoAlumno);
        Curso curso = buscarCurso(idCurso);

        if (alumno == null) {
            return "FALLO RF03: Alumno no encontrado.";
        }
        if (!alumno.isActivo()) {
            return "FALLO RF03: El alumno está inactivo.";
        }
        if (curso == null) {
            return "FALLO RF03: Curso no encontrado.";
        }
        if (alumno.yaEstaMatriculado(curso.getId())) {
            return "FALLO RF03: El alumno ya está matriculado en " + curso.getId() + ".";
        }
        if (curso.estaEnListaEspera(alumno.getCodigo())) {
            return "FALLO RF08: El alumno ya está en lista de espera de " + curso.getId() + ".";
        }

        String incumplidos = requisitosIncumplidos(alumno, curso);
        if (!incumplidos.isEmpty()) {
            return "FALLO RF06: Faltan requisitos para " + curso.getId() + ": " + incumplidos;
        }

        if (tieneCruceHorario(alumno, curso)) {
            return "FALLO RF07: Cruce horario con " + curso.getHorario() + ".";
        }

        if (curso.tieneCupo()) {
            curso.matricularAlumno(alumno.getCodigo());
            alumno.registrarMatricula(curso.getId());
            return "ÉXITO RF03: Matrícula confirmada en " + curso.getId() + ".";
        }

        curso.agregarListaEspera(alumno.getCodigo());
        return "AVISO RF08: Sin cupo en " + curso.getId() + ". Alumno agregado a lista de espera.";
    }

    public String retirarCurso(String codigoAlumno, String idCurso) {
        Alumno alumno = buscarAlumno(codigoAlumno);
        Curso curso = buscarCurso(idCurso);

        if (alumno == null) {
            return "FALLO RF04: Alumno no encontrado.";
        }
        if (curso == null) {
            return "FALLO RF04: Curso no encontrado.";
        }

        if (curso.retirarAlumno(alumno.getCodigo())) {
            alumno.retirarMatricula(curso.getId());
            String resultado = "ÉXITO RF04: " + alumno.getCodigo() + " retirado de " + curso.getId() + ".";
            String promocion = promoverDesdeListaEspera(curso);
            if (!promocion.isEmpty()) {
                resultado = resultado + " " + promocion;
            }
            return resultado;
        }

        if (curso.retirarDeListaEspera(alumno.getCodigo())) {
            return "ÉXITO RF04: " + alumno.getCodigo() + " retirado de la lista de espera de " + curso.getId() + ".";
        }

        return "FALLO RF04: El alumno no estaba matriculado ni en espera para " + curso.getId() + ".";
    }

    public List<String> verListaClase(String idCurso, String filtroNombre) {
        Curso curso = buscarCurso(idCurso);
        if (curso == null) {
            return Collections.singletonList("Curso no encontrado.");
        }

        String filtro = filtroNombre == null ? "" : filtroNombre.trim().toLowerCase(Locale.ROOT);
        List<String> salida = new ArrayList<>();
        for (String codigoAlumno : curso.getAlumnosMatriculados()) {
            Alumno alumno = alumnos.get(codigoAlumno);
            if (alumno == null) {
                continue;
            }
            if (!filtro.isEmpty() && !alumno.getNombre().toLowerCase(Locale.ROOT).contains(filtro)) {
                continue;
            }
            salida.add(alumno.getCodigo() + " - " + alumno.getNombre() + " (" + alumno.getCarrera() + ")");
        }
        if (salida.isEmpty()) {
            salida.add("No hay alumnos que coincidan con el filtro.");
        }
        return salida;
    }

    public List<Alumno> listarAlumnos() {
        return new ArrayList<>(alumnos.values());
    }

    public List<Alumno> listarAlumnosActivos() {
        List<Alumno> activos = new ArrayList<>();
        for (Alumno alumno : alumnos.values()) {
            if (alumno.isActivo()) {
                activos.add(alumno);
            }
        }
        return activos;
    }

    public List<Curso> listarCursos() {
        return new ArrayList<>(cursos.values());
    }

    public Alumno buscarAlumno(String codigo) {
        if (isBlank(codigo)) {
            return null;
        }
        return alumnos.get(codigo.trim().toUpperCase());
    }

    public Curso buscarCurso(String idCurso) {
        if (isBlank(idCurso)) {
            return null;
        }
        return cursos.get(idCurso.trim().toUpperCase());
    }

    private String requisitosIncumplidos(Alumno alumno, Curso curso) {
        List<String> faltantes = new ArrayList<>();
        for (String requisito : curso.getRequisitos()) {
            if (!alumno.tieneCursoAprobado(requisito)) {
                faltantes.add(requisito);
            }
        }
        return String.join(", ", faltantes);
    }

    private boolean tieneCruceHorario(Alumno alumno, Curso nuevoCurso) {
        for (String idMatriculado : alumno.getCursosMatriculados()) {
            Curso yaMatriculado = cursos.get(idMatriculado);
            if (yaMatriculado != null && yaMatriculado.getHorario().equalsIgnoreCase(nuevoCurso.getHorario())) {
                return true;
            }
        }
        return false;
    }

    private String promoverDesdeListaEspera(Curso curso) {
        while (curso.tieneCupo()) {
            String codigo = curso.obtenerSiguienteEnEspera();
            if (codigo == null) {
                return "";
            }
            Alumno candidato = alumnos.get(codigo);
            if (candidato == null || !candidato.isActivo()) {
                continue;
            }
            if (candidato.yaEstaMatriculado(curso.getId())) {
                continue;
            }
            if (!requisitosIncumplidos(candidato, curso).isEmpty()) {
                continue;
            }
            if (tieneCruceHorario(candidato, curso)) {
                continue;
            }
            curso.matricularAlumno(candidato.getCodigo());
            candidato.registrarMatricula(curso.getId());
            return "RF08: Se promovió desde lista de espera a " + candidato.getCodigo() + ".";
        }
        return "";
    }

    private boolean isBlank(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
