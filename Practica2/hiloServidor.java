import java.io.BufferedReader;
import java.lang.Math;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.net.Socket;
import java.time.LocalDateTime;
import java.lang.Thread;

public class hiloServidor implements Runnable {
	String mensaje="",dificultad="";
	PrintStream  p; //Canal de escritura
    BufferedReader  b; //Canal de Lectura
    Socket  cliente; //Socket cliente
    LocalDateTime inicio,fin;
    int casillas=0,jugadores=0;
    tablero tab;
    ThreadGroup grupo;
	
	public hiloServidor(Socket socket,String d,tablero t,ThreadGroup g){
		this.cliente=socket;
		this.tab=t;
		this.dificultad=d;
        this.casillas=tab.casillas;
        this.grupo=g;
        try{
            this.b = new BufferedReader ( new InputStreamReader  ( cliente.getInputStream() ) );
            this.p = new PrintStream  ( cliente.getOutputStream() );
        }catch(IOException e){
            System.out.println(e.getMessage());
        }
	}
	
    
    public void imprimeTablero(){
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
                p.print(tab.tablero.get(Character.toString((char) i)+Integer.toString(j))+"\t");
            }
            p.println();
        }
        //p.print(imprime);
        //p.println();
    }
    public String validaJugada(String jugada){
        if(this.tab.tablero.get(jugada)!=null){
            if(this.tab.minas.get(jugada)==null){
                if(this.tab.tablero.get(jugada).equals("D")){
                    this.tab.tablero.notify();
                    return "Escoge una casilla que no este desbloqueada,has perdido el turno";
                }else{
                    casillas=casillas-1;
                    this.tab.setTablero(jugada,"D");
                    this.tab.tablero.notify();
                    if(casillas==0)
                    {
                        return "Has ganado el juego";
                    }
                    return "Desbloqueaste una casilla";
                }
            }else{
                if(this.tab.tablero.get(jugada).equals("M")){
                    this.tab.tablero.notify();
                    return "Escoge una casilla que no este desbloqueada,has perdido el turno";
                }else{
                    this.tab.setTablero(jugada,"M");
                    this.tab.tablero.notify();
                    return "Explotaste una mina\nGame Over";
                }
                
            }
        }
        return "Casilla Invalida";

    }
    public boolean checaJuegoTerminado(){
        Iterator it = tab.tablero.values().iterator();
        int min=0,vacios=0,desbloqueados=0;
        while (it.hasNext()) {
            String valor = it.next().toString();
            if(valor.equals("M")){
                min++;
            }
            if(valor.equals("V")){
                vacios++;
            }
            //it.remove(); // avoids a ConcurrentModificationException
        }
        if(tab.dificultad.equals("1")){
            if(min==10){
                p.println("Todas las minas han sido explotadas\nJuego Terminado");
                return true;
            }
            if(vacios==0){
                p.println("Todas las casillas han sido desbloqueadas\nJuego Terminado");
                return true;
            }
            return false;
        }
        if(tab.dificultad.equals("2")){
            if(min==40){
                p.println("Todas las minas han sido explotadas\nJuego Terminado");
                return true;
            }
            if(vacios==0){
                p.println("Todas las casillas han sido desbloqueadas\nJuego Terminado");
                return true;
            }
            return false;
        }
        return false;
    }
	public void run(){
        //tab.addObserver(this);
        //imprimeTablero(tab.getTablero().toString());
        p.println("Bienvenido Espera tu turno\n");
        jugadores=grupo.activeCount();
        	try{
        	    inicio = LocalDateTime.now();
            	while (true){
                    synchronized(tab.tablero){
                        if(checaJuegoTerminado()){
                            tab.tablero.notify();
                            break;
                        }
                        imprimeTablero();
                	    p.println("Ingresa la casilla que quieres jugar (ejemplo a0): ");
                	    mensaje=b.readLine();
                	    if (mensaje.equals("by")) {
                	        fin=LocalDateTime.now();
                	        p.println("Duración de la partida (HH:MM:SS): "+(fin.getHour()-inicio.getHour())+":"+(fin.getMinute()-inicio.getMinute())+":"+(fin.getSecond()-inicio.getSecond()));
                            tab.tablero.notify();
                	        break;
                	    }else{
                	        mensaje=validaJugada(mensaje);
                	        if(mensaje.equals("Explotaste una mina\nGame Over") || mensaje.equals("Has ganado el juego"))
                	        {
                                p.println("Duración de la partida (HH:MM:SS): "+(fin.getHour()-inicio.getHour())+":"+(fin.getMinute()-inicio.getMinute())+":"+(fin.getSecond()-inicio.getSecond()));
                	            p.println(mensaje);
                                tab.tablero.notify();
                                break;
                	        }
                	        p.println(mensaje);
                            if(jugadores>1){
                                tab.tablero.wait();
                            }
                            //System.out.println("Esto hay activo:"+this.activeCount());
                	    }
                    }
                    //break;
            	}
                p.println("Hasta luego");
        	    p.close();
        	    b.close();
        	    this.cliente.close();
            }catch(InterruptedException e){
                System.out.println(e.toString());
            }catch(IOException e){
            	System.out.println(e.toString());
                System .out.println("No puedo crear el socket");
        	}
	}

}