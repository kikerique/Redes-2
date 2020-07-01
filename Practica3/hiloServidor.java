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
    int casillas=0,numJugadores=0,jugadores=0;
    tablero tab;
    boolean bandera=false;
    ThreadGroup grupo;
    String nombre="";
	
	public hiloServidor(Socket socket,String d,tablero t,ThreadGroup g, int j,String nombre){
		this.cliente=socket;
		this.tab=t;
		this.dificultad=d;
        this.casillas=tab.casillas;
        this.grupo=g;
        this.numJugadores=j;
        this.nombre=nombre;
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
                    return "Escoge una casilla que no este desbloqueada,has perdido el turno";
                }else{
                    this.tab.setTablero(jugada,"D");
                    if(checaJuegoTerminado())
                    {
                        return "Has ganado el juego";
                    }
                    return "Desbloqueaste una casilla";
                }
            }else{
                if(this.tab.tablero.get(jugada).equals("M")){
                    return "Escoge una casilla que no este desbloqueada,has perdido el turno";
                }else{
                    this.tab.setTablero(jugada,"M");
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
            if((vacios+min)==10){
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
            if((vacios+min)==40){
                p.println("Todas las casillas han sido desbloqueadas\nJuego Terminado");
                return true;
            }
            return false;
        }
        return false;
    }
	public void run(){
        String actual;
        //tab.addObserver(this);
        //imprimeTablero(tab.getTablero().toString());
        p.println("Bienvenido Espera tu turno\n");
        try{
        	inicio = LocalDateTime.now();
            while (true){
                synchronized(tab.tablero){
                    if(grupo.activeCount()<numJugadores && bandera!=true){
                        p.println("Esperando a todos los jugadores");
                        p.println("Jugadores actualmente conectados: "+grupo.activeCount());
                        p.println("Jugadores necesarios para el inicio del juego: "+numJugadores);
                        bandera=true;
                        tab.turnos.add(this.nombre);
                        tab.tablero.wait();
                        //tab.tablero.notify();
                    }
                    if((grupo.activeCount()==numJugadores && numJugadores>1) && bandera==false){
                        p.println("Hay un jugador antes que tu, por favor espera tu turno");
                        tab.tablero.notify();
                        bandera=true;
                        tab.turnos.add(this.nombre);
                        tab.tablero.wait();
                    }
                    //tab.tablero.notify();
                    if(checaJuegoTerminado()){ 
                        tab.tablero.notify();
                        break;
                    }
                    if(grupo.activeCount()>1){
                           actual= tab.turnos.peek();
                        while(!actual.equals(this.nombre)){
                            tab.tablero.notify();
                            tab.tablero.wait();
                            actual=tab.turnos.peek();
                        }
                    }
                    tab.turnos.poll();
                    imprimeTablero();
                	p.println("Ingresa la casilla que quieres jugar (ejemplo a0): ");
                	mensaje=b.readLine();
                	if (mensaje.equals("by")) {
                    	fin=LocalDateTime.now();
                    	p.println("Duración de la partida (HH:MM:SS): "+(fin.getHour()-inicio.getHour())+":"+(fin.getMinute()-inicio.getMinute())+":"+(fin.getSecond()-inicio.getSecond()));
                        tab.tablero.notify();
                        tab.turnos.remove(this.nombre);
                    	break;
                    }else{
                	    mensaje=validaJugada(mensaje);
                	    if(mensaje.equals("Explotaste una mina\nGame Over") || mensaje.equals("Has ganado el juego"))
                	    {
                            fin=LocalDateTime.now();
                            p.println("Duración de la partida (HH:MM:SS): "+(fin.getHour()-inicio.getHour())+":"+(fin.getMinute()-inicio.getMinute())+":"+(fin.getSecond()-inicio.getSecond()));
                    	    p.println(mensaje);  
                            tab.turnos.remove(this.nombre);                              
                            tab.tablero.notify();
                            break;
                	    }
                	    p.println(mensaje);
                        tab.tablero.notify();
                            //System.out.println("Esto hay activo:"+this.activeCount());
                	}
                    if(grupo.activeCount()>1){
                        tab.turnos.add(this.nombre);
                        tab.tablero.wait();
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