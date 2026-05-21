package GuerraDeTesting_GrupoB;

import org.junit.jupiter.api.BeforeEach;

abstract class BaseTestConfig {
    protected DataStore dataStore;
    protected SistemaMatricula sistema;

    @BeforeEach
    void resetDataStore() {
        dataStore = DataStore.getInstance();
        dataStore.resetForTests();
        sistema = dataStore.obtenerSistema();
    }
}
