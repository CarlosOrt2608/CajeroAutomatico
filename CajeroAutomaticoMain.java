import java.util.Scanner;

public class CajeroAutomaticoMain {
    public static void main(String[] args) {
        CajeroAutomatico cajero = new CajeroAutomatico();
        cajero.accesoCajero();

        while (true) {
            System.out.println("\nAcciones disponibles:");
            System.out.println("1. Consultar Saldo");
            System.out.println("2. Retirar Efectivo");
            System.out.println("3. Salir");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Seleccione una opción: ");
            int opcion = scanner.nextInt();

            switch (opcion) {
                case 1:
                    cajero.consultarSaldo();
                    break;
                case 2:
                    cajero.retirarEfectivo();
                    break;
                case 3:
                    System.out.println("Se agradece el uso de este cajero. Banco 'Carlos Ortega' le atendió");
                    System.exit(0);
                default:
                    System.out.println("Esta opcion no es correcta, no ah podido ser completada.");
            }
        }
    }
}