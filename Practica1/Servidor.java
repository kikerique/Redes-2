import java.io.BufferedReader;
import java.lang.Math;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Scanner;
import java.util.HashMap;
import java.util.Arrays;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.InetAddress;

public class Servidor {
    HashMap<String,String> tablero;
    HashMap<String,String> minas;
    int casillas=0;
    public void creaMatriz(String dificultad){
        int ancho=0,ganadoras=0;
        Double r=0.0;
        if(dificultad.equals("1")){
            ancho=9;
            ganadoras=10;
            casillas=(ancho*ancho)-ganadoras;
        }else{
            ancho=16;
            ganadoras=40;
            casillas=(ancho*ancho)-ganadoras;
        }
        this.tablero=new HashMap<>(ancho*ancho);
        this.minas=new HashMap<>(ganadoras);
        for(int i=97;i<97+ancho;i++){
            for(int j=0;j<ancho;j++){
                r=Math.random();
                if((r>0.5 && r<0.7) && ganadoras>0){
                    ganadoras=ganadoras-1;
                    this.minas.put(Character.toString((char) i)+Integer.toString(j),"Mina");
                }
                this.tablero.put(Character.toString((char) i)+Integer.toString(j),"V");
            }
        }
        
    }
    public void imprimeTablero(PrintStream p, String dificultad){
        int ancho=0;
        if(dificultad.equals("1")){
            ancho=9;
        }else{
            ancho=16;
        }
        p.print("0 ");
        for(int i=97;i<97+ancho;i++){
            p.print(Character.toString((char)i)+"\t");
        }
        p.println();
        for(int j=0;j<ancho;j++){
            p.print("\n"+j+" ");
            for(int i=97;i<97+ancho;i++){
                p.print(this.tablero.get(Character.toString((char) i)+Integer.toString(j))+"\t");
            }
            p.println();
        }
    }
    public String validaJugada(String jugada){
        if(this.tablero.get(jugada)!=null){
            if(this.minas.get(jugada)==null){
                if(this.tablero.get(jugada).equals("D")){
                    return "Escoge una casilla que no este desbloqueada";
                }else{
                    casillas=casillas-1;
                    this.tablero.replace(jugada,"D");
                    if(casillas==0)
                    {
                        return "Has ganado el juego\nHasta luego";
                    }
                    return "Desbloqueaste una casilla";
                }
            }else{
                return "Explotaste una mina\nGame Over\nHasta luego";
            }
        }
        return "Casilla Invalida";

    }
    public void iniciaServer(){
        String HOST="",dificultad="",mensaje="";
        int PORT=0;
        Scanner leer=new Scanner(System.in);
        ServerSocket  s; //Socket servidor
        Socket  sc; //Socket cliente

        PrintStream  p; //Canal de escritura
        BufferedReader  b; //Canal de Lectura

        System.out.print("Ingresa la direccion IP donde quieres que se levante el servidor: ");
        HOST=leer.nextLine();
        System.out.print("Ingresa el puerto donde quieres que se levante el servidor: ");
        PORT=leer.nextInt();

        try {
            //Creo el socket server
            s = new ServerSocket ();
            s.bind(new InetSocketAddress(InetAddress.getByName(HOST),PORT));
            System.out.println("Servidor escuchando en la IP: "+s.getInetAddress().toString());
            System.out.println("Servidor escuchando en el puerto: "+s.getLocalPort());

            //Invoco el metodo accept del socket servidor, me devuelve una referencia al socket cliente
            sc = s.accept();

            //Obtengo una referencia a los canales de escritura y lectura del socket cliente
            b = new BufferedReader ( new InputStreamReader  ( sc.getInputStream() ) );
            p = new PrintStream  ( sc.getOutputStream() );
            p.println("Bienvenido Usuario: " + sc.getLocalSocketAddress().toString());
            p.println("Ingresa la dificultad\n1.-Principiante\n2.-Experto\nQ.-Salir");
            //Leo lo que escribio el socket cliente en el canal de lectura
            dificultad = b.readLine();
            creaMatriz(dificultad);
            System.out.println("Aqui estan las minas:\n"+Arrays.asList(minas));
            while ( true ) {
                imprimeTablero(p,dificultad);
                p.println("Ingresa la casilla que quieres jugar (ejemplo a0): ");
                mensaje=b.readLine();
                if (mensaje.equals("by")) {
                    p.println("Hasta luego");
                    break;
                }else{
                    mensaje=validaJugada(mensaje);
                    if(mensaje.equals("Explotaste una mina\nGame Over\nHasta luego") || mensaje.equals("Has ganado el juego\nHasta luego"))
                    {
                        p.println(mensaje);
                        break;
                    }
                    p.println(mensaje);
                }
            }

            p.close();
            b.close();

            sc.close();
            s.close();
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