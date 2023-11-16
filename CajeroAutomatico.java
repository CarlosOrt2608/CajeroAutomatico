import java.io.*;
import java.util.*;

public class CajeroAutomatico {
    private String usuarioActual;
    private int saldo;
    private Map<Integer, Integer> billetes;
    private String archivoBilletes = "billetes2.dat";
    private String archivoLogs = "logs.txt";

    public CajeroAutomatico() {
        this.usuarioActual = null;
        this.saldo = 0;
        this.billetes = new HashMap<>();
        cargarBilletes();
        cargarLogs();
    }

    private void cargarBilletes() {
        try {
        	File file = new File(archivoBilletes);
        	
        	
            // Si el archivo no existe es creado
        	if (!file.exists()) {
                file.createNewFile();
                
            }else {
            	FileInputStream readImage = new FileInputStream(file);
            	readImage.close();
            	if (file.delete()) {
            		file.createNewFile();;
                } else {
                    System.out.println("No se pudo eliminar el archivo.");
                }
            }
            
            FileInputStream fileStream = new FileInputStream(archivoBilletes);
            DataInputStream objectInputStream = new DataInputStream(fileStream);
            
            Map<Integer, Integer> hashMap = new HashMap<>();
            if (fileStream.available() > 0) {
            	String clave = objectInputStream.readUTF();
                int valor = objectInputStream.readInt();

                // Almacenar HashMap
                hashMap.put(Integer.parseInt(clave), valor);
                this.billetes = hashMap;
            } else {
                inicializarBilletes();
            }
            
            objectInputStream.close();
           
            fileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void inicializarBilletes() {
        this.billetes.put(100, 100);
        this.billetes.put(200, 100);
        this.billetes.put(500, 20);
        this.billetes.put(1000, 10);
    }

    private void guardarBilletes() {
        try {
            FileOutputStream fileStream = new FileOutputStream(archivoBilletes);
            ObjectOutputStream objectStream = new ObjectOutputStream(fileStream);

            objectStream.writeObject(billetes);
            objectStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarLogs() {
        
    }

    private void guardarLog(String accion, int cantidad, String seRealizo) {
        try {
            FileWriter fileWriter = new FileWriter(archivoLogs, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println(accion + "," + usuarioActual + "," + cantidad + "," + seRealizo);
            fileWriter.close();
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarLog() {
        try (BufferedReader reader = new BufferedReader(new FileReader("logs.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void accesoCajero() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese su nombre o tarjeta, cuenta CLABE: ");
        String nombre = scanner.nextLine();
        System.out.print("Clave de seguridad NIP de 4 dígitos: ");
        int nip = scanner.nextInt();

        if (nombre.equals("Admin") && nip == 3243) {
            modoAdministrador();
        } else {
            System.out.println("¡Bienvenido al modo Cajero!");
            this.usuarioActual = nombre;
            this.saldo = new Random().nextInt(49001) + 1000;
            System.out.println("Su saldo actual es: $" + this.saldo);
        }
    }
    private void modoAdministrador() {
        Scanner scanner = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\nMenú de Administrador:");
            System.out.println("1. Mostrar acciones en el log");
            System.out.println("2. Mostrar cantidad de billetes restantes");
            System.out.println("3. Salir");
            System.out.print("Ingrese su opción: ");
            opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    mostrarLog();
                    break;
                case 2:
                    mostrarCantidadBilletes();
                    break;
                case 3:
                    System.out.println("Salida del modo administrador.");
                    break;
                default:
                    System.out.println("Opción Incorrecta, intente mas tarde.");
                    break;
            }
        } while (opcion != 3);
    }

    private void mostrarCantidadBilletes() {
    }

    public void consultarSaldo() {
        System.out.println("Su saldo actual es: $" + this.saldo);
        guardarLog("consultar", this.saldo, "SI");
    }

    public void retirarEfectivo() {
        Scanner scanner = new Scanner(System.in);

        int montoMinimo = Math.min(this.saldo, Collections.min(billetes.keySet()) * 100);
        System.out.println("Monto mínimo a retirar: $" + montoMinimo);

        System.out.print("Ingrese la cantidad a retirar: ");
        int cantidad = scanner.nextInt();

        if (cantidad > this.saldo || cantidad > Collections.min(billetes.keySet()) * 100) {
            System.out.println("Error: Cantidad no válida. Intente de nuevo.");
            retirarEfectivo();
            return;
        }

        if (!verificarDisponibilidadBilletes(cantidad)) {
            System.out.println("No existen billetes suficientes, intente otro saldo.");
            retirarEfectivo();
            return;
        }

        realizarRetiro(cantidad);
        guardarLog("retirar", cantidad, "SI");
    }

    private boolean verificarDisponibilidadBilletes(int cantidad) {
        int cantidadRestante = cantidad;
        for (int denominacion : billetes.keySet()) {
            int billetesDisponibles = billetes.get(denominacion);
            int billetesNecesarios = cantidadRestante / denominacion;

            if (billetesNecesarios > 0 && billetesDisponibles >= billetesNecesarios) {
                cantidadRestante -= billetesNecesarios * denominacion;
            }
        }

        return cantidadRestante == 0;
    }

    private void realizarRetiro(int cantidad) {
        this.saldo -= cantidad;

        for (int denominacion : billetes.keySet()) {
            int billetesNecesarios = cantidad / denominacion;
            this.billetes.put(denominacion, billetes.get(denominacion) - billetesNecesarios);
            cantidad -= billetesNecesarios * denominacion;
        }

        System.out.println("Retiro realizado con exito. Saldo Actualizado: $" + this.saldo);
        guardarBilletes();
    }
}
