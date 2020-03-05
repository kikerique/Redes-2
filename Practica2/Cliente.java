import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;
public class Cliente {

    public static void main(String [] args) {

        Socket  s;
        PrintStream  p;
        BufferedReader  b;
        String HOST="";
        int PORT=0;
        String  respuesta;
        if(args.length==2){
            //Referencia a la entrada por consola (System.in)
            BufferedReader  in = new BufferedReader (new InputStreamReader (System .in));
            HOST=args[0];
            PORT=Integer.parseInt(args[1]);
            try {

                //Creo una conexion al socket servidor
                s = new Socket (HOST,PORT);
                //s.setSoTimeout(3*1000);

                //Creo las referencias al canal de escritura y lectura del socket
                p = new PrintStream (s.getOutputStream());
                b = new BufferedReader  ( new InputStreamReader  ( s.getInputStream() ) );
                while ( true ) {
                    respuesta=b.readLine();
                    System.out.println(respuesta);
                    if (respuesta.equals("Hasta luego")) {
                        break;
                    }else if(respuesta.equals("Q.-Salir") || respuesta.equals("Ingresa la casilla que quieres jugar (ejemplo a0): ")){
                        p.println(in.readLine());
                    }
                }
                
                p.close();
                b.close();
                s.close();
            }catch ( java.net.SocketTimeoutException e){
                System.out.println("El servidor no acepta conexiones por el momento");

            } catch (UnknownHostException  e) {
                System.out.println(e.getMessage());
                System .out.println("No puedo conectarme a " + HOST + ":" + PORT);
            } catch (IOException  e) {
                System .out.println("Error de E/S en " + HOST + ":" + PORT);
            }
        }
        System.out.println("uso: java ejecutable HOST PORT");

    }
}