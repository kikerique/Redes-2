import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.lang.Thread;
import java.io.IOException;

public class Servidor {
    
    public void iniciaServer(){
        String HOST="",respuesta="";
        int PORT=0,numeroConexiones=0;
        Scanner leer=new Scanner(System.in);
        ServerSocket  s; //Socket servidor
        Socket  sc; //Socket cliente
        System.out.print("Ingresa la direccion IP donde quieres que se levante el servidor: ");
        HOST=leer.nextLine();
        System.out.print("Ingresa el puerto donde quieres que se levante el servidor: ");
        PORT=leer.nextInt();
        System.out.print("Ingresa el numero maximo de conexiones para el servidor: ");
        numeroConexiones=leer.nextInt();
        try {
            //Creo el socket server
            s = new ServerSocket (PORT,numeroConexiones,InetAddress.getByName(HOST));
            
            System.out.println("Servidor escuchando en la IP: "+s.getInetAddress().toString());
            System.out.println("Servidor escuchando en el puerto: "+s.getLocalPort());

            while ( true ) {
                //Invoco el metodo accept del socket servidor, me devuelve una referencia al socket cliente
                sc = s.accept();
                //Instanciamos a la clase hiloServidor para crear un nuevo hilo que atenderá al cliente
                Runnable nuevoCliente = new hiloServidor(sc);
                Thread hilo = new Thread(nuevoCliente);
                hilo.start();     
            }
            
        } catch (IOException  e) {
            System.out.println(e.toString());
            System .out.println("No puedo crear el socket");
        }
    }


    public static void main(String [] args) {
        Servidor s=new Servidor();
        s.iniciaServer();
    }

}