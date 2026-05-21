# Sistema de Matrícula Grupo B

## Architecture Overview
El estado de la matrícula se unifica mediante un Singleton `DataStore` que carga y persiste el `SistemaMatricula` en JSON usando Jackson. El `DataStore` actúa como fuente única de verdad y registra un shutdown hook para serializar el estado al finalizar la ejecución.

## Prerequisites
- Java 25 (LTS)
- Maven

## Build Instructions
```bash
mvn clean
mvn compile
mvn package
```

## Execution Paths
**CLI (MenuPrincipal)**
```bash
mvn -DskipTests package
java -cp target/classes GuerraDeTesting_GrupoB.MenuPrincipal
```

**GUI Swing (VentanaMatricula)**
```bash
mvn -DskipTests package
java -cp target/classes GuerraDeTesting_GrupoB.VentanaMatricula
```

## Testing Protocol
```bash
mvn test
```
La suite JUnit 5 cubre 166 casos de borde y reglas de validación documentadas.

## Audit Trail
El proyecto utiliza comentarios en línea con el formato `// FIX [Phase]:` para trazar correcciones de arquitectura y lógica de dominio. Cada etiqueta documenta el motivo técnico que disparó el cambio (persistencia, unificación de estado, reglas RF y control de integridad).
