import java.util.HashMap;
import java.util.Arrays;
import java.util.Observable;
import java.util.LinkedList;
public class tablero{
	HashMap<String,String> tablero; //Tablero del juego
    HashMap<String,String> minas; //Posición de las minas dentro del tablero
    String dificultad;
    int casillas;
    LinkedList<String> turnos;

	public tablero(String d){
		this.dificultad=d;
        this.turnos= new LinkedList<String>();
		creaMatriz();
	}

	public void creaMatriz(){
        int ancho=0,ganadoras=0;
        Double r=0.0;
        if(this.dificultad.equals("1")){
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
    public HashMap<String,String> getTablero(){
        return this.tablero;
    }
    public void setTablero(String jugada,String dato){
    	this.tablero.replace(jugada,dato);
        /*// Indica que el tablero ha cambiado
        this.setChanged();
        // Notifica a los observadores que el mensaje ha cambiado y se lo pasa
        // (Internamente notifyObservers llama al metodo update del observador)
        this.notifyObservers(this.getTablero());*/
    }

}