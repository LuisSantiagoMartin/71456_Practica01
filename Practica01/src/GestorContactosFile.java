import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class GestorContactosFile {
    static File archivo = new File("contactos.txt");

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n--- GESTOR DE CONTACTOS (File) ---");
            System.out.println("1. Agregar contacto");
            System.out.println("2. Ver contactos");
            System.out.println("3. Buscar contacto por nombre");
            System.out.println("4. Eliminar contacto");
            System.out.println("5. Eliminar archivo de contactos");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");
            
            // Manejo de errores para entradas no numéricas
            if (sc.hasNextInt()) {
                opcion = sc.nextInt();
                sc.nextLine();
            } else {
                opcion = 0; // Opción no válida
                sc.nextLine(); // Consumir la entrada no válida
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
        } while (opcion != 6);
        sc.close();
    }

    // --- Métodos de la clase ---

    static void agregarContacto(Scanner sc) {
        try {
            // true para añadir al final (append)
            FileWriter fw = new FileWriter(archivo, true); 
            System.out.print("Nombre: ");
            String nombre = sc.nextLine();
            System.out.print("Teléfono: ");
            String tel = sc.nextLine();
            fw.write(nombre + "," + tel + "\n");
            fw.close();
            System.out.println("Contacto agregado correctamente.");
        } catch (IOException e) {
            System.out.println("Error al escribir en el archivo: " + e.getMessage());
        }
    }

    static void verContactos() {
        try {
            if (!archivo.exists() || archivo.length() == 0) {
                System.out.println("No hay contactos.");
                return;
            }
            Scanner lector = new Scanner(archivo);
            System.out.println("\n--- LISTA DE CONTACTOS ---");
            while (lector.hasNextLine()) {
                String linea = lector.nextLine();
                String[] partes = linea.split(",");
                if (partes.length >= 2) {
                    System.out.printf("Nombre: %-20s Teléfono: %s%n", partes[0].trim(), partes[1].trim());
                } else {
                    System.out.println(linea); 
                }
            }
            lector.close();
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    static void buscarContacto(Scanner sc) {
        try {
            if (!archivo.exists()) {
                System.out.println("No hay contactos para buscar.");
                return;
            }
            System.out.print("Introduce parte del nombre a buscar: ");
            // Convierte a minúsculas para una búsqueda sin distinción entre mayúsculas y minúsculas
            String nombreBuscado = sc.nextLine().toLowerCase();
            Scanner lector = new Scanner(archivo);
            boolean encontrado = false;
            
            System.out.println("\n--- RESULTADOS DE BÚSQUEDA ---");
            while (lector.hasNextLine()) {
                String linea = lector.nextLine();
                String[] partes = linea.split(",");
                String nombreContacto = partes.length > 0 ? partes[0].trim() : "";
                
                // Si el nombre del contacto contiene la subcadena buscada
                if (nombreContacto.toLowerCase().contains(nombreBuscado)) {
                    System.out.println("Encontrado: " + linea);
                    encontrado = true;
                }
            }
            if (!encontrado) System.out.println("No se encontró ningún contacto con ese nombre.");
            lector.close();
        } catch (IOException e) {
            System.out.println("Error al buscar el contacto: " + e.getMessage());
        }
    }

    // --- MÉTODO ELIMINAR CONTACTO ACTUALIZADO PARA SELECCIÓN ---
    static void eliminarContacto(Scanner sc) {
        try {
            if (!archivo.exists()) {
                System.out.println("No hay contactos para eliminar.");
                return;
            }
            
            // 1. Leer todas las líneas del archivo en una lista
            List<String> todasLasLineas = new ArrayList<>();
            Scanner lector = new Scanner(archivo);
            while (lector.hasNextLine()) {
                todasLasLineas.add(lector.nextLine());
            }
            lector.close();

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
                // Obtener el índice en la lista ORIGINAL (todasLasLineas)
                int indiceReal = indicesCoincidentes.get(seleccion - 1);
                String contactoEliminado = todasLasLineas.remove(indiceReal);
                
                // 5. Sobrescribir el archivo
                FileWriter fw = new FileWriter(archivo, false); // sobrescribe
                for (String linea : todasLasLineas) {
                    fw.write(linea + "\n");
                }
                fw.close();

                System.out.println("\n¡Éxito! El contacto fue eliminado:");
                System.out.println(contactoEliminado);
                
            } else {
                System.out.println("Número de selección no válido. Eliminación cancelada.");
            }

        } catch (IOException e) {
            System.out.println("Error al procesar la eliminación: " + e.getMessage());
        }
    }

    static void eliminarArchivo() {
        if (archivo.delete()) {
            System.out.println("Archivo de contactos eliminado.");
        } else {
            System.out.println("No se pudo eliminar el archivo (puede que ya no exista).");
        }
    }
}