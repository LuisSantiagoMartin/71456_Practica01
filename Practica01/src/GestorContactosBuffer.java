import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorContactosBuffer {
    // Usar BufferedReader y BufferedWriter es más eficiente para I/O de archivos
    static File archivo = new File("contactos_buffer.txt");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- GESTOR DE CONTACTOS (Buffer) ---");
            System.out.println("1. Agregar contacto");
            System.out.println("2. Ver contactos");
            System.out.println("3. Buscar contacto por nombre");
            System.out.println("4. Eliminar contacto");
            System.out.println("5. Eliminar archivo de contactos");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");
            
            // Manejo básico de entrada no numérica
            if (sc.hasNextInt()) {
                opcion = sc.nextInt();
                sc.nextLine();
            } else {
                opcion = 0; // Opción no válida
                sc.nextLine();
            }

            switch (opcion) {
                case 1 -> agregarContacto(sc);
                case 2 -> verContactos();
                case 3 -> buscarContacto(sc);
                case 4 -> eliminarContacto(sc);
                case 5 -> eliminarArchivo();
                case 6 -> System.out.println("Saliendo...");
                default -> System.out.println("Opción no válida.");
            }
        } while (opcion != 6); // Cambiado a 6, que es la opción de Salir
        sc.close();
    }

    // --- Métodos de Contacto ---

    static void agregarContacto(Scanner sc) {
        // Uso de try-with-resources para asegurar el cierre de recursos (BufferedWriter/FileWriter)
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Teléfono: ");
            String tel = sc.nextLine();
            bw.write(nombre + "," + tel);
            bw.newLine();
            System.out.println("Contacto agregado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir: " + e.getMessage());
        }
    }

    static void verContactos() {
        if (!archivo.exists() || archivo.length() == 0) {
            System.out.println("No hay contactos.");
            return;
        }

        // Uso de try-with-resources para asegurar el cierre de recursos (BufferedReader/FileReader)
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            System.out.println("\n--- LISTA DE CONTACTOS ---");
            while ((linea = br.readLine()) != null) {
                // Formato mejorado para la vista
                String[] partes = linea.split(",");
                if (partes.length >= 2) {
                    System.out.printf("Nombre: %-20s Teléfono: %s%n", partes[0].trim(), partes[1].trim());
                } else {
                    System.out.println(linea); 
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer: " + e.getMessage());
        }
    }

    static void buscarContacto(Scanner sc) {
        if (!archivo.exists() || archivo.length() == 0) {
            System.out.println("No hay contactos para buscar.");
            return;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            System.out.print("Introduce parte del nombre a buscar: ");
            // Para búsqueda parcial (la primera letra o cualquier subcadena)
            String nombreBuscado = sc.nextLine().toLowerCase();
            String linea;
            boolean encontrado = false;
            
            System.out.println("\n--- RESULTADOS DE BÚSQUEDA ---");
            while ((linea = br.readLine()) != null) {
                // Extracción precisa del nombre para evitar coincidencias con el teléfono
                String[] partes = linea.split(",");
                String nombreContacto = partes.length > 0 ? partes[0].trim() : "";
                
                // Búsqueda sin distinción de mayúsculas/minúsculas y por subcadena
                if (nombreContacto.toLowerCase().contains(nombreBuscado)) {
                    System.out.println("Encontrado: " + linea);
                    encontrado = true;
                }
            }
            if (!encontrado) System.out.println("No se encontró ningún contacto con ese nombre.");
        } catch (IOException e) {
            System.out.println("Error al buscar: " + e.getMessage());
        }
    }

    /**
     * Busca coincidencias parciales y pide al usuario seleccionar el contacto a eliminar.
     */
    static void eliminarContacto(Scanner sc) {
        if (!archivo.exists() || archivo.length() == 0) {
            System.out.println("No hay contactos para eliminar.");
            return;
        }

        // 1. Leer todas las líneas del archivo en una lista (para manipulación)
        List<String> todasLasLineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                todasLasLineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
            return;
        }

        System.out.print("Introduce parte del nombre del contacto a eliminar: ");
        String nombreEliminar = sc.nextLine().toLowerCase();
        
        // 2. Encontrar y recolectar coincidencias
        List<String> coincidencias = new ArrayList<>();
        List<Integer> indicesCoincidentes = new ArrayList<>();

        for (int i = 0; i < todasLasLineas.size(); i++) {
            String linea = todasLasLineas.get(i);
            String[] partes = linea.split(",");
            String nombreContacto = partes.length > 0 ? partes[0].trim() : "";
            
            if (nombreContacto.toLowerCase().contains(nombreEliminar)) {
                coincidencias.add(linea);
                indicesCoincidentes.add(i); // Guardar el índice en la lista original
            }
        }
        
        if (coincidencias.isEmpty()) {
            System.out.println("No se encontró ningún contacto con ese nombre.");
            return;
        }
        
        // 3. Mostrar opciones al usuario
        System.out.println("\n--- COINCIDENCIAS ENCONTRADAS ---");
        for (int i = 0; i < coincidencias.size(); i++) {
            System.out.printf("%d. %s%n", (i + 1), coincidencias.get(i));
        }

        System.out.print("Introduce el número del contacto a ELIMINAR (o 0 para cancelar): ");
        int seleccion = -1;
        if (sc.hasNextInt()) {
            seleccion = sc.nextInt();
            sc.nextLine(); 
        } else {
            sc.nextLine();
            System.out.println("Entrada no válida. Cancelando la eliminación.");
            return;
        }
        
        if (seleccion == 0) {
            System.out.println("Eliminación cancelada.");
            return;
        }
        
        // 4. Validar y eliminar el contacto seleccionado
        if (seleccion >= 1 && seleccion <= coincidencias.size()) {
            // Obtener el índice real en 'todasLasLineas'
            int indiceReal = indicesCoincidentes.get(seleccion - 1);
            String contactoEliminado = todasLasLineas.remove(indiceReal);
            
            // 5. Sobrescribir el archivo usando BufferedWriter
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, false))) {
                for (String linea : todasLasLineas) {
                    bw.write(linea);
                    bw.newLine();
                }
                System.out.println("\n¡Éxito! El contacto fue eliminado:");
                System.out.println(contactoEliminado);
            } catch (IOException e) {
                 System.out.println("Error al sobrescribir el archivo: " + e.getMessage());
            }
            
        } else {
            System.out.println("Número de selección no válido. Eliminación cancelada.");
        }
    }

    static void eliminarArchivo() {
        if (archivo.delete()) {
            System.out.println("Archivo de contactos eliminado.");
        } else {
            // Esto es si el archivo no existe o no se tienen permisos
            System.out.println("No se pudo eliminar el archivo (puede que ya no exista).");
        }
    }
}