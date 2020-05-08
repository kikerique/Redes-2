"""
Ejemplo de un cliente HTTP simple usando la libreria http.client de python

Equipo: Los 4 fantásticos 
"""
import argparse
import http.client, urllib.parse
import sys
params = urllib.parse.urlencode({'number':12524, 'type': 'issue', 'action':'show'}).encode() #codifica los parametros de la peticion POST
headers = {"Content-type": "application/x-www-form-urlencoded","Accept": "text/plain"} #Cabeceras para la petición POST
opc=""
path=""

if len(sys.argv)<3:
     print("Uso ejecutable HOST PORT")
     sys.exit(1)

conn = http.client.HTTPConnection(sys.argv[1],sys.argv[2]) #Instanciamos la clase HTTPConnection en la URL de nuestro servidor


def imprimeRespuesta():
     r1 = conn.getresponse()  #Obtenemos la respuesta del servidor
     print(r1.status, r1.reason)  #Imprimimos el codigo de Status y la descripción
     while True:     
          chunk = r1.read(200)  #Leemos los datos de la respuesta con un valor arbitrario de 200 bytes
          if not chunk:
               break
          print(repr(chunk))  #Imprimimos la respuesta del servidor
while opc!="9":
     print("Bienvenido usuario ingresa la petición que quieres enviar al servidor")
     print("1:GET\n2:POST\n3:HEAD\n4:PUT\n5:DELETE\n6:CONNECT\n7:OPTIONS\n8:TRACE\n9:Salir")
     opc=input()
     try:
          if opc=="1":
               print("Ingrese la direccion del destino de la peticion:")
               path=input()
               conn.request("GET", path)
               imprimeRespuesta()
          if opc=="2":
               print("Ingrese la direccion del destino de la peticion:")
               path=input()
               conn.request("POST", path, params, headers)
               imprimeRespuesta()
          if opc=="3":
               print("Ingrese la direccion del destino de la peticion:")
               path=input()
               conn.request("HEAD", path)
               imprimeRespuesta()
          if opc=="4":
               print("Ingrese la direccion del destino de la peticion:")
               path=input()
               print("Ingrese el nombre del archivo que contiene la informacion")
               archivo=open(input(),"r").read()
               conn.request("PUT", path,archivo,{"Content-Lenght":len(archivo)})
               imprimeRespuesta()
               conn.request("GET", path)
               imprimeRespuesta()
          if opc=="5":
               print("Ingrese la direccion objetivo de la peticion:")
               path=input()
               conn.request("DELETE", path)
               imprimeRespuesta()
          if opc=="6":
               print("Ingrese la direccion objetivo de la peticion:")
               path=input()
               conn.request("CONNECT",path)
               imprimeRespuesta()

          if opc=="7":
               print("Ingrese la direccion del destino de la peticion:")
               path=input()
               conn.request("OPTIONS", path)
               imprimeRespuesta()
          if opc=="8":
               print("Ingrese la direccion del destino de la peticion:")
               path=input()
               conn.request("TRACE", path)
               imprimeRespuesta()
          if opc=="9":
               print("Hasta luego")
               break
     except Exception as e:
          print(str(e))

conn.close()     #Cerramos la conexion con el servidor