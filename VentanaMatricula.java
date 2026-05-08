import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.StringJoiner;

public class VentanaMatricula extends JFrame {
    private final SistemaMatricula sistema = new SistemaMatricula();

    private final DefaultTableModel modeloAlumnos = new DefaultTableModel(
            new Object[]{"Código", "Nombre", "Carrera", "Estado", "Aprobados"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final DefaultTableModel modeloCursos = new DefaultTableModel(
            new Object[]{"ID", "Curso", "Créditos", "Horario", "Cupos", "Espera", "Requisitos"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable tablaAlumnos = new JTable(modeloAlumnos);
    private final JTable tablaCursos = new JTable(modeloCursos);
    private final JComboBox<String> comboAlumnoAccion = new JComboBox<>();
    private final JComboBox<String> comboCursoLista = new JComboBox<>();
    private final JTextField txtFiltroLista = new JTextField(16);
    private final JTextArea areaListaClase = new JTextArea();
    private final JTextArea areaDetalleCurso = new JTextArea();
    private final JLabel lblEstado = new JLabel("Listo.");

    private final JTextField txtCodigoAlumno = new JTextField(12);
    private final JTextField txtNombreAlumno = new JTextField(18);
    private final JTextField txtCarreraAlumno = new JTextField(18);

    public VentanaMatricula() {
        setTitle("Sistema de Matrícula UCSM - RF01 a RF08");
        setSize(1120, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cargarDatosDemo();
        construirInterfaz();
        refrescarTodo();
    }

    private void cargarDatosDemo() {
        sistema.registrarCurso(new Curso("INF-01", "Testing de Sistemas", 4, 2, "Lunes 08:00"));
        sistema.registrarCurso(new Curso("INF-02", "Base de Datos", 3, 2, "Martes 10:00", List.of("INF-01")));
        sistema.registrarCurso(new Curso("INF-03", "Programación Java", 4, 1, "Lunes 08:00", List.of("INF-01")));
        sistema.registrarCurso(new Curso("INF-04", "Arquitectura de Software", 3, 2, "Miércoles 10:00", List.of("INF-02")));

        sistema.registrarAlumno("20230001", "Miguel Andia", "Ingeniería de Sistemas");
        sistema.registrarAlumno("20230002", "Noelia Gonzales", "Ingeniería de Sistemas");
        sistema.registrarAlumno("20230003", "Danny Carlos", "Ingeniería de Sistemas");

        sistema.registrarCursoAprobado("20230001", "INF-01");
        sistema.registrarCursoAprobado("20230002", "INF-01");
        sistema.registrarCursoAprobado("20230002", "INF-02");
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        root.add(crearEncabezado(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("RF01 - Alumnos", crearTabAlumnos());
        tabs.addTab("RF02/RF03/RF04/RF06/RF07/RF08 - Matrícula", crearTabMatricula());
        tabs.addTab("RF05 - Listas de clase", crearTabListas());

        root.add(tabs, BorderLayout.CENTER);
        root.add(crearPie(), BorderLayout.SOUTH);
        add(root);
    }

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(216, 227, 243)),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        panel.setBackground(new Color(245, 249, 255));

        JLabel titulo = new JLabel("Módulo de Matrícula - Cobertura RF01 a RF08");
        titulo.setFont(new Font("SansSerif", Font.BOLD, 20));
        JLabel subtitulo = new JLabel("Gestión de alumnos, matrícula, retiro, requisitos, cupos, espera y listas.");
        subtitulo.setForeground(new Color(75, 75, 75));

        JPanel textos = new JPanel(new GridLayout(2, 1));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        panel.add(textos, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearTabAlumnos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        tablaAlumnos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaAlumnos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                cargarAlumnoSeleccionadoEnFormulario();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaAlumnos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Alumnos registrados"));
        panel.add(scrollTabla, BorderLayout.CENTER);

        JPanel formulario = new JPanel(new GridBagLayout());
        formulario.setBorder(BorderFactory.createTitledBorder("Gestión RF01 / Requisitos RF06"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0;
        formulario.add(new JLabel("Código:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtCodigoAlumno, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        formulario.add(new JLabel("Nombre:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtNombreAlumno, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        formulario.add(new JLabel("Carrera:"), gbc);
        gbc.gridx = 1;
        formulario.add(txtCarreraAlumno, gbc);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        JButton btnRegistrar = new JButton("Registrar");
        JButton btnEditar = new JButton("Editar");
        JButton btnBaja = new JButton("Dar de baja");
        JButton btnAprobado = new JButton("Agregar aprobado");
        JButton btnLimpiar = new JButton("Limpiar");

        btnRegistrar.addActionListener(e -> ejecutarAccion(sistema.registrarAlumno(
                txtCodigoAlumno.getText(),
                txtNombreAlumno.getText(),
                txtCarreraAlumno.getText()
        )));

        btnEditar.addActionListener(e -> ejecutarAccion(sistema.editarAlumno(
                txtCodigoAlumno.getText(),
                txtNombreAlumno.getText(),
                txtCarreraAlumno.getText()
        )));

        btnBaja.addActionListener(e -> ejecutarAccion(sistema.darBajaAlumno(txtCodigoAlumno.getText())));

        btnAprobado.addActionListener(e -> {
            String idCurso = JOptionPane.showInputDialog(this, "ID del curso aprobado (RF06):");
            if (idCurso != null) {
                ejecutarAccion(sistema.registrarCursoAprobado(txtCodigoAlumno.getText(), idCurso));
            }
        });

        btnLimpiar.addActionListener(e -> limpiarFormularioAlumno());

        acciones.add(btnRegistrar);
        acciones.add(btnEditar);
        acciones.add(btnBaja);
        acciones.add(btnAprobado);
        acciones.add(btnLimpiar);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        formulario.add(acciones, gbc);

        panel.add(formulario, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearTabMatricula() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        JPanel superior = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        superior.add(new JLabel("Alumno activo:"));
        superior.add(comboAlumnoAccion);

        JButton btnMatricular = new JButton("Matricular curso seleccionado");
        JButton btnRetirar = new JButton("Retirar curso seleccionado");
        JButton btnRefrescar = new JButton("Actualizar vista");

        btnMatricular.addActionListener(e -> {
            String alumno = codigoDeItemCombo((String) comboAlumnoAccion.getSelectedItem());
            String curso = idCursoSeleccionado();
            if (alumno.isEmpty() || curso.isEmpty()) {
                mostrarAdvertencia("Selecciona alumno y curso.");
                return;
            }
            ejecutarAccion(sistema.matricularAlumnoEnCurso(alumno, curso));
        });

        btnRetirar.addActionListener(e -> {
            String alumno = codigoDeItemCombo((String) comboAlumnoAccion.getSelectedItem());
            String curso = idCursoSeleccionado();
            if (alumno.isEmpty() || curso.isEmpty()) {
                mostrarAdvertencia("Selecciona alumno y curso.");
                return;
            }
            ejecutarAccion(sistema.retirarCurso(alumno, curso));
        });

        btnRefrescar.addActionListener(e -> refrescarTodo());

        superior.add(btnMatricular);
        superior.add(btnRetirar);
        superior.add(btnRefrescar);

        tablaCursos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaCursos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalleCurso();
            }
        });

        JScrollPane scrollTabla = new JScrollPane(tablaCursos);
        scrollTabla.setBorder(BorderFactory.createTitledBorder("Catálogo de cursos (RF02)"));

        areaDetalleCurso.setEditable(false);
        areaDetalleCurso.setFont(new Font("Monospaced", Font.PLAIN, 13));
        areaDetalleCurso.setLineWrap(true);
        areaDetalleCurso.setWrapStyleWord(true);
        areaDetalleCurso.setText("Selecciona un curso para ver su estado.");
        JScrollPane scrollDetalle = new JScrollPane(areaDetalleCurso);
        scrollDetalle.setBorder(BorderFactory.createTitledBorder("Detalle de curso / cupos / espera"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTabla, scrollDetalle);
        split.setResizeWeight(0.65);
        split.setDividerLocation(700);

        panel.add(superior, BorderLayout.NORTH);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearTabListas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JPanel filtros = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtros.add(new JLabel("Curso:"));
        filtros.add(comboCursoLista);
        filtros.add(new JLabel("Filtrar por nombre:"));
        filtros.add(txtFiltroLista);

        JButton btnAplicar = new JButton("Ver lista");
        btnAplicar.addActionListener(e -> refrescarListaClase());
        filtros.add(btnAplicar);

        areaListaClase.setEditable(false);
        areaListaClase.setFont(new Font("Monospaced", Font.PLAIN, 13));
        JScrollPane scroll = new JScrollPane(areaListaClase);
        scroll.setBorder(BorderFactory.createTitledBorder("Lista de clase (RF05)"));

        panel.add(filtros, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPie() {
        JPanel pie = new JPanel(new BorderLayout());
        lblEstado.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        pie.add(lblEstado, BorderLayout.CENTER);

        JButton btnSalir = new JButton("Cerrar sistema");
        btnSalir.addActionListener(e -> System.exit(0));
        pie.add(btnSalir, BorderLayout.EAST);
        return pie;
    }

    private void ejecutarAccion(String resultado) {
        mostrarFeedback(resultado);
        refrescarTodo();
    }

    private void refrescarTodo() {
        refrescarAlumnos();
        refrescarCursos();
        refrescarCombos();
        refrescarListaClase();
        mostrarDetalleCurso();
    }

    private void refrescarAlumnos() {
        modeloAlumnos.setRowCount(0);
        for (Alumno alumno : sistema.listarAlumnos()) {
            modeloAlumnos.addRow(new Object[]{
                    alumno.getCodigo(),
                    alumno.getNombre(),
                    alumno.getCarrera(),
                    alumno.isActivo() ? "Activo" : "Baja",
                    String.join(", ", alumno.getCursosAprobados())
            });
        }
    }

    private void refrescarCursos() {
        modeloCursos.setRowCount(0);
        for (Curso curso : sistema.listarCursos()) {
            modeloCursos.addRow(new Object[]{
                    curso.getId(),
                    curso.getNombre(),
                    curso.getCreditos(),
                    curso.getHorario(),
                    curso.getCuposOcupados() + "/" + curso.getCuposMaximos(),
                    curso.getCantidadEspera(),
                    curso.getRequisitos().isEmpty() ? "-" : String.join(", ", curso.getRequisitos())
            });
        }
    }

    private void refrescarCombos() {
        String alumnoSeleccionado = codigoDeItemCombo((String) comboAlumnoAccion.getSelectedItem());
        comboAlumnoAccion.removeAllItems();
        for (Alumno alumno : sistema.listarAlumnosActivos()) {
            comboAlumnoAccion.addItem(alumno.getCodigo() + " - " + alumno.getNombre());
        }
        seleccionarAlumnoEnCombo(alumnoSeleccionado);

        String cursoSeleccionado = codigoDeItemCombo((String) comboCursoLista.getSelectedItem());
        comboCursoLista.removeAllItems();
        for (Curso curso : sistema.listarCursos()) {
            comboCursoLista.addItem(curso.getId() + " - " + curso.getNombre());
        }
        seleccionarCursoEnCombo(cursoSeleccionado);
    }

    private void refrescarListaClase() {
        String idCurso = codigoDeItemCombo((String) comboCursoLista.getSelectedItem());
        if (idCurso.isEmpty()) {
            areaListaClase.setText("No hay curso seleccionado.");
            return;
        }

        List<String> lista = sistema.verListaClase(idCurso, txtFiltroLista.getText());
        StringJoiner sj = new StringJoiner("\n");
        for (String fila : lista) {
            sj.add(fila);
        }
        areaListaClase.setText(sj.toString());
    }

    private void mostrarDetalleCurso() {
        String idCurso = idCursoSeleccionado();
        if (idCurso.isEmpty()) {
            areaDetalleCurso.setText("Selecciona un curso para ver su estado.");
            return;
        }

        Curso curso = sistema.buscarCurso(idCurso);
        if (curso == null) {
            areaDetalleCurso.setText("Curso no encontrado.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(curso.getId()).append('\n');
        sb.append("Curso: ").append(curso.getNombre()).append('\n');
        sb.append("Horario: ").append(curso.getHorario()).append('\n');
        sb.append("Créditos: ").append(curso.getCreditos()).append('\n');
        sb.append("Cupos: ").append(curso.getCuposOcupados()).append("/").append(curso.getCuposMaximos()).append('\n');
        sb.append("Lista de espera: ").append(curso.getCantidadEspera()).append('\n');
        sb.append("Requisitos: ").append(curso.getRequisitos().isEmpty() ? "-" : String.join(", ", curso.getRequisitos())).append("\n\n");
        sb.append("Matriculados:\n");
        if (curso.getAlumnosMatriculados().isEmpty()) {
            sb.append("- (sin alumnos)\n");
        } else {
            for (String codigo : curso.getAlumnosMatriculados()) {
                Alumno alumno = sistema.buscarAlumno(codigo);
                sb.append("- ").append(codigo);
                if (alumno != null) {
                    sb.append(" ").append(alumno.getNombre());
                }
                sb.append('\n');
            }
        }
        sb.append("\nEn espera:\n");
        if (curso.getListaEspera().isEmpty()) {
            sb.append("- (sin espera)");
        } else {
            for (String codigo : curso.getListaEspera()) {
                Alumno alumno = sistema.buscarAlumno(codigo);
                sb.append("- ").append(codigo);
                if (alumno != null) {
                    sb.append(" ").append(alumno.getNombre());
                }
                sb.append('\n');
            }
        }
        areaDetalleCurso.setText(sb.toString());
    }

    private void cargarAlumnoSeleccionadoEnFormulario() {
        int fila = tablaAlumnos.getSelectedRow();
        if (fila < 0) {
            return;
        }
        txtCodigoAlumno.setText(String.valueOf(tablaAlumnos.getValueAt(fila, 0)));
        txtNombreAlumno.setText(String.valueOf(tablaAlumnos.getValueAt(fila, 1)));
        txtCarreraAlumno.setText(String.valueOf(tablaAlumnos.getValueAt(fila, 2)));
    }

    private void limpiarFormularioAlumno() {
        txtCodigoAlumno.setText("");
        txtNombreAlumno.setText("");
        txtCarreraAlumno.setText("");
        tablaAlumnos.clearSelection();
    }

    private String idCursoSeleccionado() {
        int fila = tablaCursos.getSelectedRow();
        if (fila < 0) {
            return "";
        }
        return String.valueOf(tablaCursos.getValueAt(fila, 0));
    }

    private String codigoDeItemCombo(String item) {
        if (item == null || item.isBlank()) {
            return "";
        }
        int i = item.indexOf(" - ");
        return (i < 0 ? item : item.substring(0, i)).trim().toUpperCase();
    }

    private void seleccionarAlumnoEnCombo(String codigoBuscado) {
        if (codigoBuscado == null || codigoBuscado.isBlank()) {
            if (comboAlumnoAccion.getItemCount() > 0) {
                comboAlumnoAccion.setSelectedIndex(0);
            }
            return;
        }
        for (int i = 0; i < comboAlumnoAccion.getItemCount(); i++) {
            if (codigoDeItemCombo(comboAlumnoAccion.getItemAt(i)).equalsIgnoreCase(codigoBuscado)) {
                comboAlumnoAccion.setSelectedIndex(i);
                return;
            }
        }
    }

    private void seleccionarCursoEnCombo(String codigoBuscado) {
        if (codigoBuscado == null || codigoBuscado.isBlank()) {
            if (comboCursoLista.getItemCount() > 0) {
                comboCursoLista.setSelectedIndex(0);
            }
            return;
        }
        for (int i = 0; i < comboCursoLista.getItemCount(); i++) {
            if (codigoDeItemCombo(comboCursoLista.getItemAt(i)).equalsIgnoreCase(codigoBuscado)) {
                comboCursoLista.setSelectedIndex(i);
                return;
            }
        }
    }

    private void setEstado(String mensaje) {
        lblEstado.setText(mensaje == null ? "" : mensaje);
    }

    private void mostrarAdvertencia(String mensaje) {
        setEstado(mensaje);
        Toolkit.getDefaultToolkit().beep();
        JOptionPane.showMessageDialog(this, mensaje, "Atención", JOptionPane.WARNING_MESSAGE);
    }

    private void mostrarFeedback(String mensaje) {
        setEstado(mensaje);
        if (mensaje == null || mensaje.isBlank()) {
            return;
        }

        String titulo = "Información";
        int tipo = JOptionPane.INFORMATION_MESSAGE;
        if (mensaje.startsWith("FALLO")) {
            titulo = "Error de validación";
            tipo = JOptionPane.ERROR_MESSAGE;
            Toolkit.getDefaultToolkit().beep();
        } else if (mensaje.startsWith("AVISO")) {
            titulo = "Aviso";
            tipo = JOptionPane.WARNING_MESSAGE;
        } else if (mensaje.startsWith("ÉXITO")) {
            titulo = "Operación exitosa";
            tipo = JOptionPane.INFORMATION_MESSAGE;
        }

        JOptionPane.showMessageDialog(this, mensaje, titulo, tipo);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new VentanaMatricula().setVisible(true);
        });
    }
}
