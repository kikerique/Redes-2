EL presente archivo especifica el funcionamiento de mi practica 3

Servidor:

el usuario ejecuta el servidor previemante compilado con el comando:

java Servidor HOST PORT numeroConexiones 

Cliente:

el usuario ejecuta el cliente previamente compilado con el comando:

java Cliente HOST PORT

Funcionamiento:

El servidor se pone en estado de escucha y acepta todas las conexiones entrantes en su puerto,
le solicita al cliente la dificultad en la que desea jugar y si hay espacio en esa dificultad
ejecuta su hilo, el servidor acepta "numeroConexiones" de jugadores para cada dificultad,
en dado caso que la dificultad que el cliente desea este llena el servidor le notifica al cliente
que esta lleno y cierra su conexión.

El juego inicia una vez que haya "numeroConexiones" de jugadores para cada dificultad respectivamente,
si un cliente explota una mina este pierde el juego y los demás jugadores pueden continuar jugando,
un cliente se puede conectar a una partida en curso siempre y cuando haya espacio en esta.

Cada cliente tiene su turno asignado según se haya conectado, el servidor imprimirá el tablero en cada turno
y le solicitará una casilla para realizar su jugada, si la jugada es inválida (casilla inexistente o previamente desbloqueada)
el jugador perderá su turno, el tablero es actualizado después de cada jugada, por lo que el jugador en el turno actual siempre
tendrá el último tablero conseguido.

El juego termina una vez que todas las casillas posibles estén desbloqueadas o bien que todas las minas posibles estén expuestas.