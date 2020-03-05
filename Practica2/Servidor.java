import java.util.Scanner;
import java.io.BufferedReader;
import java.lang.Math;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.lang.Thread;
import java.io.IOException;
import java.util.Arrays;

public class Servidor {
    String dificultad="";
    Socket  cliente; //Socket cliente
    PrintStream  p; //Canal de escritura
    BufferedReader  b; //Canal de Lectura
    tablero tab=null,tab16=null;//Tableros para los dos tipos de juego

    /*
       void pideDificultad(Socket sc)
        método que le solicita la dificultad a un cliente y crea un nuevo tablero si es necesario
    */
    public void pideDificultad(Socket sc){
        try{
            b = new BufferedReader ( new InputStreamReader  ( sc.getInputStream() ) );
            p = new PrintStream  ( sc.getOutputStream() );
            p.println("Bienvenido Usuario: " + sc.getLocalSocketAddress().toString());
            p.println("Ingresa la dificultad\n1.-Principiante\n2.-Experto\nQ.-Salir");
            //Leo lo que escribio el socket cliente en el canal de lectura
            this.dificultad = b.readLine();
            if(dificultad.equals("1")){
                if(this.tab==null){
                    this.tab=new tablero(dificultad);
                    System.out.println("INICIANDO UN NUEVO JUEGO DE PRINCIPIANTES");
                    System.out.println("Aqui estan las minas:\n"+Arrays.asList(tab.minas));
                }
            }
            if(dificultad.equals("2")){
                if(this.tab16==null){
                    this.tab16=new tablero(dificultad);
                    System.out.println("INICIANDO UN NUEVO JUEGO DE EXPERTOS");
                    System.out.println("Aqui estan las minas:\n"+Arrays.asList(tab16.minas));
                }
            }
            
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
        
    }

    /*
       void iniciaServer(String HOST,int PORT,int numeroConexiones)
        Método que crea un objeto de tipo Server Socket en la dirección ip y puertos especificados
        y se queda a la espera de nuevas conexiones
    */
    
    public void iniciaServer(String HOST,int PORT,int numeroConexiones){
        ThreadGroup principiantes,expertos;
        Thread hilo,hiloexp;
        String respuesta="";
        int conexionesActivasp=0,conexionesActivase=0;
        Runnable nuevoCliente;
        Scanner leer=new Scanner(System.in);
        ServerSocket  s; //Socket servidor
        Socket  sc; //Socket cliente
        principiantes=new ThreadGroup("principiantes");
        expertos=new ThreadGroup("expertos");
        try {
            //Creo el socket server
            s = new ServerSocket (PORT,numeroConexiones,InetAddress.getByName(HOST));
            
            System.out.println("Servidor escuchando en la IP: "+s.getInetAddress().toString());
            System.out.println("Servidor escuchando en el puerto: "+s.getLocalPort());
            System.out.println("Principiantes iniciales:"+ principiantes.activeCount());
            System.out.println("expertos iniciales: "+ expertos.activeCount());
            while ( true ) {
                //Invoco el metodo accept del socket servidor, me devuelve una referencia al socket cliente
                sc = s.accept();
                conexionesActivasp=principiantes.activeCount();
                conexionesActivase=expertos.activeCount();
                p = new PrintStream  ( sc.getOutputStream() );
                if(conexionesActivasp==0){
                    tab=null;
                }
                if(conexionesActivase==0){
                    tab16=null;
                }
                pideDificultad(sc);    
                if(dificultad.equals("1")){
                    nuevoCliente = new hiloServidor(sc,dificultad,tab,principiantes);
                    hilo=new Thread(principiantes,nuevoCliente);
                    //System.out.println("principiantes: "+conexionesActivasp);
                    if((conexionesActivasp)<numeroConexiones){
                        hilo.start();
                        //conexionesActivasp=principiantes.activeCount();
                    }else{
                        p.println("Servidor lleno");
                        p.println("Hasta luego");
                        sc.close();
                    }
                }
                if(dificultad.equals("2")){
                    nuevoCliente = new hiloServidor(sc,dificultad,tab16,expertos);
                    hiloexp=new Thread(expertos,nuevoCliente);
                    //System.out.println("expertos: "+conexionesActivase);
                    if((conexionesActivase)<numeroConexiones){
                        hiloexp.start();
                        //conexionesActivase=expertos.activeCount();
                    }else{
                        p.println("Servidor lleno");
                        p.println("Hasta luego");
                        sc.close();
                    }
                }
                    //hilo.start();
                    //System.out.println("gg: " +nuevoCliente.activeCount());

                    //conexionesActivas--; 
            }
            
        } catch (IOException  e) {
            System.out.println(e.toString());
            System .out.println("No puedo crear el socket");
        }
    }


    public static void main(String [] args) {
        if(args.length<3){
            System.out.println("Uso: java ejecutable HOST PORT #conexiones-permitidas");
        }else{
            Servidor s=new Servidor();
            s.iniciaServer(args[0],Integer.parseInt(args[1]),Integer.parseInt(args[2]));
        }
    }

}