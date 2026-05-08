import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class SistemaMatricula {
    private static final Pattern CODIGO_ALUMNO_PATTERN = Pattern.compile("^\\d{8}$");
    private static final Pattern ID_CURSO_PATTERN = Pattern.compile("^[A-Z]{3}-\\d{2}$");
    private static final Pattern NOMBRE_PATTERN = Pattern.compile("^[\\p{L} .'-]{3,80}$");
    private static final Pattern HORARIO_PATTERN = Pattern.compile(
            "^(Lunes|Martes|Mi[eé]rcoles|Jueves|Viernes|S[aá]bado|Domingo)\\s([01]\\d|2[0-3]):[0-5]\\d$",
            Pattern.CASE_INSENSITIVE
    );

    private final Map<String, Alumno> alumnos = new LinkedHashMap<>();
    private final Map<String, Curso> cursos = new LinkedHashMap<>();

    public String registrarAlumno(String codigo, String nombre, String carrera) {
        if (isBlank(codigo) || isBlank(nombre) || isBlank(carrera)) {
            return "FALLO RF01: Código, nombre y carrera son obligatorios.";
        }
        String normalizado = normalizarCodigoAlumno(codigo);
        if (normalizado == null) {
            return "FALLO RF01: El código de alumno debe tener 8 dígitos numéricos.";
        }
        if (!NOMBRE_PATTERN.matcher(nombre.trim()).matches()) {
            return "FALLO RF01: El nombre contiene caracteres inválidos o longitud no permitida.";
        }
        if (!esTextoValido(carrera, 3, 80)) {
            return "FALLO RF01: La carrera debe tener entre 3 y 80 caracteres válidos.";
        }
        if (alumnos.containsKey(normalizado)) {
            return "FALLO RF01: El alumno " + normalizado + " ya existe.";
        }
        alumnos.put(normalizado, new Alumno(normalizado, nombre, carrera));
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
        if (!NOMBRE_PATTERN.matcher(nuevoNombre.trim()).matches()) {
            return "FALLO RF01: El nombre contiene caracteres inválidos o longitud no permitida.";
        }
        if (!esTextoValido(nuevaCarrera, 3, 80)) {
            return "FALLO RF01: La carrera debe tener entre 3 y 80 caracteres válidos.";
        }
        alumno.editarDatos(nuevoNombre, nuevaCarrera);
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
        String idNormalizado = normalizarIdCurso(idCurso);
        if (idNormalizado == null) {
            return "FALLO RF06: Debes indicar un curso aprobado.";
        }
        if (buscarCurso(idNormalizado) == null) {
            return "FALLO RF06: El curso " + idNormalizado + " no existe en el catálogo.";
        }
        if (alumno.tieneCursoAprobado(idNormalizado)) {
            return "AVISO RF06: El alumno ya tenía registrado " + idNormalizado + " como aprobado.";
        }
        alumno.registrarCursoAprobado(idNormalizado);
        return "ÉXITO RF06: Curso aprobado registrado para " + alumno.getCodigo() + ".";
    }

    public String registrarCurso(Curso curso) {
        if (curso == null) {
            return "FALLO RF02: El curso es inválido.";
        }
        String id = normalizarIdCurso(curso.getId());
        if (id == null) {
            return "FALLO RF02: El ID del curso debe seguir el formato ABC-99.";
        }
        if (!esTextoValido(curso.getNombre(), 3, 100)) {
            return "FALLO RF02: El nombre del curso es inválido.";
        }
        if (curso.getCreditos() <= 0 || curso.getCreditos() > 10) {
            return "FALLO RF02: Los créditos del curso deben estar entre 1 y 10.";
        }
        if (curso.getCuposMaximos() <= 0 || curso.getCuposMaximos() > 200) {
            return "FALLO RF02: El cupo máximo debe estar entre 1 y 200.";
        }
        if (!HORARIO_PATTERN.matcher(curso.getHorario().trim()).matches()) {
            return "FALLO RF02: El horario debe tener formato 'Lunes 08:00'.";
        }
        if (cursos.containsKey(id)) {
            return "FALLO RF02: El curso " + id + " ya existe.";
        }
        for (String requisito : curso.getRequisitos()) {
            if (normalizarIdCurso(requisito) == null) {
                return "FALLO RF02: Requisito inválido '" + requisito + "'.";
            }
            if (!cursos.containsKey(requisito)) {
                return "FALLO RF02: El requisito " + requisito + " no existe en catálogo.";
            }
        }
        cursos.put(id, curso);
        return "ÉXITO RF02: Curso " + id + " registrado en catálogo.";
    }

    public String matricularAlumnoEnCurso(String codigoAlumno, String idCurso) {
        if (normalizarCodigoAlumno(codigoAlumno) == null) {
            return "FALLO RF03: Código de alumno inválido (8 dígitos).";
        }
        if (normalizarIdCurso(idCurso) == null) {
            return "FALLO RF03: ID de curso inválido (formato ABC-99).";
        }
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
        if (normalizarCodigoAlumno(codigoAlumno) == null) {
            return "FALLO RF04: Código de alumno inválido (8 dígitos).";
        }
        if (normalizarIdCurso(idCurso) == null) {
            return "FALLO RF04: ID de curso inválido (formato ABC-99).";
        }
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
        if (normalizarIdCurso(idCurso) == null) {
            return Collections.singletonList("ID de curso inválido.");
        }
        if (!isBlank(filtroNombre) && !esTextoBusquedaValido(filtroNombre)) {
            return Collections.singletonList("Filtro inválido: usa solo letras, espacios o guiones.");
        }
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
        String normalizado = normalizarCodigoAlumno(codigo);
        if (normalizado == null) {
            return null;
        }
        return alumnos.get(normalizado);
    }

    public Curso buscarCurso(String idCurso) {
        String normalizado = normalizarIdCurso(idCurso);
        if (normalizado == null) {
            return null;
        }
        return cursos.get(normalizado);
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

    private String normalizarCodigoAlumno(String codigo) {
        if (isBlank(codigo)) {
            return null;
        }
        String normalizado = codigo.trim();
        if (!CODIGO_ALUMNO_PATTERN.matcher(normalizado).matches()) {
            return null;
        }
        return normalizado;
    }

    private String normalizarIdCurso(String idCurso) {
        if (isBlank(idCurso)) {
            return null;
        }
        String normalizado = idCurso.trim().toUpperCase();
        if (!ID_CURSO_PATTERN.matcher(normalizado).matches()) {
            return null;
        }
        return normalizado;
    }

    private boolean esTextoValido(String texto, int min, int max) {
        if (isBlank(texto)) {
            return false;
        }
        String limpio = texto.trim();
        if (limpio.length() < min || limpio.length() > max) {
            return false;
        }
        for (char c : limpio.toCharArray()) {
            if (!(Character.isLetterOrDigit(c) || Character.isSpaceChar(c) || c == '.' || c == '-' || c == '\'')) {
                return false;
            }
        }
        return true;
    }

    private boolean esTextoBusquedaValido(String texto) {
        String limpio = texto.trim();
        if (limpio.length() > 80) {
            return false;
        }
        for (char c : limpio.toCharArray()) {
            if (!(Character.isLetter(c) || Character.isSpaceChar(c) || c == '-' || c == '\'')) {
                return false;
            }
        }
        return true;
    }
}
