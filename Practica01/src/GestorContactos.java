import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class GestorContactos {
    private static final String NOMBRE_ARCHIVO = "contactos.txt";
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        int opcion;
        
        do {
            mostrarMenu();
            opcion = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            
            switch (opcion) {
                case 1 -> agregarContacto();
                case 2 -> mostrarContactos();
                case 3 -> buscarContacto();
                case 4 -> eliminarArchivo();
                case 5 -> System.out.println("¡Hasta pronto!");
                default -> System.out.println("Opción no válida. Intente nuevamente.");
            }
            
        } while (opcion != 5);
    }

    private static void mostrarMenu() {
        System.out.println("\n=== GESTOR DE CONTACTOS ===");
        System.out.println("1. Agregar contacto");
        System.out.println("2. Mostrar todos los contactos");
        System.out.println("3. Buscar contacto por nombre");
        System.out.println("4. Eliminar archivo de contactos");
        System.out.println("5. Salir");
        System.out.print("Seleccione una opción: ");
    }

    private static void agregarContacto() {
        System.out.print("Introduce nombre del contacto: ");
        String nombre = scanner.nextLine();
        System.out.print("Introduce número del contacto: ");
        String numero = scanner.nextLine();

        try (FileWriter fw = new FileWriter(NOMBRE_ARCHIVO, true)) {
            fw.write(nombre + "," + numero + "\n");
            System.out.println("✅ Contacto agregado con éxito.");
        } catch (IOException e) {
            System.out.println("❌ Error al agregar contacto.");
        }
    }

    private static void mostrarContactos() {
        System.out.println("\n--- LISTA DE CONTACTOS ---");
        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("⚠ No hay contactos.");
            return;
        }

        try (FileReader fr = new FileReader(archivo)) {
            int c;
            StringBuilder linea = new StringBuilder();
            while ((c = fr.read()) != -1) {
                if (c == '\n') {
                    imprimirLinea(linea.toString());
                    linea.setLength(0);
                } else {
                    linea.append((char) c);
                }
            }
            if (linea.length() > 0) imprimirLinea(linea.toString());
        } catch (IOException e) {
            System.out.println("❌ Error al leer contactos.");
        }
    }

    private static void imprimirLinea(String linea) {
        String[] datos = linea.split(",");
        if (datos.length == 2) {
            System.out.println("👤 " + datos[0] + " | 📞 " + datos[1]);
        }
    }

    private static void buscarContacto() {
        System.out.println("\n--- BUSCAR CONTACTO ---");
        System.out.print("Ingrese el nombre a buscar (respetando mayúsculas/minúsculas): ");
        String nombreBuscado = scanner.nextLine();

        File archivo = new File(NOMBRE_ARCHIVO);
        if (!archivo.exists()) {
            System.out.println("⚠ No hay contactos.");
            return;
        }

        boolean encontrado = false;
        try (FileReader fr = new FileReader(archivo)) {
            int c;
            StringBuilder linea = new StringBuilder();
            while ((c = fr.read()) != -1) {
                if (c == '\n') {
                    if (procesarBusqueda(linea.toString(), nombreBuscado)) encontrado = true;
                    linea.setLength(0);
                } else {
                    linea.append((char) c);
                }
            }
            if (linea.length() > 0)
                if (procesarBusqueda(linea.toString(), nombreBuscado)) encontrado = true;
        } catch (IOException e) {
            System.out.println("❌ Error al buscar contacto.");
        }

        if (!encontrado) System.out.println("⚠ Contacto no encontrado.");
    }

    private static boolean procesarBusqueda(String linea, String nombreBuscado) {
        String[] datos = linea.split(",");
        if (datos.length == 2 && datos[0].equals(nombreBuscado)) {
            System.out.println("✅ Encontrado -> 👤 " + datos[0] + " | 📞 " + datos[1]);
            return true;
        }
        return false;
    }

    private static void eliminarArchivo() {
        File archivo = new File(NOMBRE_ARCHIVO);
        if (archivo.exists() && archivo.delete()) {
            System.out.println("✅ Archivo eliminado.");
        } else {
            System.out.println("⚠ No se pudo eliminar el archivo.");
        }
    }
}
