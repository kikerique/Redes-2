import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
public class Cliente {

    public static void main(String [] args) {

        Socket  s;
        PrintStream  p;
        BufferedReader  b;
        Scanner sc=new Scanner(System.in);
        String HOST="";
        int PORT=0;
        String  respuesta;

        //Referencia a la entrada por consola (System.in)
        BufferedReader  in = new BufferedReader (new InputStreamReader (System .in));
        System.out.println("Ingresa la IP del servidor:");
        HOST=sc.nextLine();
        System.out.println("Ingresa el puerto del servidor:");
        PORT=sc.nextInt();
        try {

            //Creo una conexion al socket servidor
            s = new Socket (HOST,PORT);

            //Creo las referencias al canal de escritura y lectura del socket
            p = new PrintStream (s.getOutputStream());
            b = new BufferedReader  ( new InputStreamReader  ( s.getInputStream() ) );

            while ( true ) {
                //Espero la respuesta por el canal de lectura
                respuesta = b.readLine();
                System .out.println(respuesta);
                //System.out.println(respuesta.equals("2.-Experto"));
                if (respuesta.equals("Hasta luego")) {
                    break;
                }else if(respuesta.equals("Q.-Salir") || respuesta.equals("Ingresa la casilla que quieres jugar (ejemplo a0): ")){
                    p.println(in.readLine());
                }
            }
            
            p.close();
            b.close();
            s.close();

        } catch (UnknownHostException  e) {
            System .out.println("No puedo conectarme a " + HOST + ":" + PORT);
        } catch (IOException  e) {
            System .out.println("Error de E/S en " + HOST + ":" + PORT);
        }
    }
}