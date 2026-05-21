package GuerraDeTesting_GrupoB;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class DataStore {
    // FIX [Persistence]: Single JSON state source enforced
    private static final DataStore INSTANCE = new DataStore();
    private static final String ARCHIVO_DATOS = "sistema-matricula.json";

    private final ObjectMapper mapper;
    private final Path rutaArchivo;
    private SistemaMatricula sistema;
    private boolean cargadoDesdeArchivo;
    private boolean hookRegistrado;

    private DataStore() {
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.rutaArchivo = Paths.get(ARCHIVO_DATOS);
    }

    public static DataStore getInstance() {
        return INSTANCE;
    }

    public synchronized SistemaMatricula obtenerSistema() {
        if (sistema == null) {
            sistema = cargarSistema();
            registrarHook();
        }
        return sistema;
    }

    public synchronized boolean isCargadoDesdeArchivo() {
        return cargadoDesdeArchivo;
    }

    public synchronized void guardarSistema() {
        if (sistema == null) {
            return;
        }
        try {
            mapper.writeValue(rutaArchivo.toFile(), sistema);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo guardar el sistema en " + rutaArchivo.toAbsolutePath(),
                    e
            );
        }
    }

    private SistemaMatricula cargarSistema() {
        if (!Files.exists(rutaArchivo)) {
            cargadoDesdeArchivo = false;
            return new SistemaMatricula();
        }
        try {
            SistemaMatricula cargado = mapper.readValue(rutaArchivo.toFile(), SistemaMatricula.class);
            cargadoDesdeArchivo = true;
            return cargado;
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo leer el sistema desde " + rutaArchivo.toAbsolutePath(),
                    e
            );
        }
    }

    private void registrarHook() {
        if (hookRegistrado) {
            return;
        }
        hookRegistrado = true;
        // FIX [Persistence]: Shutdown persistence hook required
        Runtime.getRuntime().addShutdownHook(
                new Thread(this::guardarSistema, "DataStoreShutdownHook")
        );
    }

    synchronized void resetForTests() {
        sistema = null;
        cargadoDesdeArchivo = false;
        try {
            Files.deleteIfExists(rutaArchivo);
        } catch (IOException e) {
            throw new IllegalStateException(
                    "No se pudo limpiar el estado persistido en " + rutaArchivo.toAbsolutePath(),
                    e
            );
        }
    }
}
