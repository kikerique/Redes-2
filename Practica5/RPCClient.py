import xmlrpc.client as RPC
import os.path
import os, subprocess
import hashlib
import sys
def getsha256file(archivo):
    try:
        hashsha = hashlib.sha256()
        with open(archivo, "rb") as f:
            for bloque in iter(lambda: f.read(4096), b""):
                hashsha.update(bloque)
        return hashsha.hexdigest()
    except Exception as e:
        print("Error: %s" % (e))
        return ""
def abrirArchivo(nombreArchivo,e):
    datos=s.leer(nombreArchivo)
    if not(isinstance(datos,str)):
        archivo=open(str("temp" + e),"wb")
        archivo.write(datos.data)
        archivo.close()
    else:
        return datos

if len(sys.argv)!=3:
    print("Modo de uso: ejecutable HOST PORT")
    sys.exit(1)

s = RPC.ServerProxy('http://'+sys.argv[1]+":"+sys.argv[2])
opc=""
try:
    while opc!="8":
        print("\nIngresa la opcion que quieras\n1 Leer un archivo\n2 Subir un archivo\n3 Modificar un archivo\n4 Eliminar un archivo\n5 Crear una carpeta\n6 Eliminar una carpeta\n7 Mostrar arbol de archivos\n8 Salir")
        opc=input()
        if opc=="1":
            print("Ingresa la direccion del archivo que quieres leer")
            nombreArchivo=input()
            n,e=os.path.splitext(nombreArchivo)
            prueba=abrirArchivo(nombreArchivo,e)
            if not(isinstance(prueba,str)):
                subprocess.call(('xdg-open', str("temp"+e)))
                input("Haz clic para continuar")
                if not(input("Deseas guardar el archivo? S/N: ").lower()=="s"):
                    try:
                        os.remove(str("temp"+e))
                    except:
                        pass
                else:
                    nombre=input("Ingresa el nombre con el que guardaras el archivo: ")
                    os.rename(str("temp"+e),str(nombre+e))
            else:
            	print(prueba)
        if opc=="2":
            print("Ingresa el nombre del archivo que quieres subir")
            nombreArchivo=input()
            print("Ingresa la direccion donde quieres que sea guardado (enter si deseas guardarlo en la raiz del servidor)")
            ruta=input()
            try:
                with open(nombreArchivo,"rb") as archivo:
                    print(s.crear(nombreArchivo,archivo.read(),ruta))
            except Exception as e:
                print(str(e))
            
        if opc=="3":
            print("Ingresa la direccion del archivo que quieres modificar")
            nombreArchivo=input()
            n,e=os.path.splitext(nombreArchivo)
            prueba=abrirArchivo(nombreArchivo,e)
            if not(isinstance(prueba,str)):
                sha=getsha256file("temp"+e)
                subprocess.call(('xdg-open', str("temp"+e)))
                input("Haz clic para continuar")
                if sha!=getsha256file("temp"+e):
                    with open(str("temp"+e),"rb") as archivo:
                        print(s.escribir(nombreArchivo,archivo.read()))
                else:
                    print("El archivo no sufrio ningun cambio")
                try:
                    os.remove(str("temp"+e))
                except:
                    pass
            else:
                print(prueba)
        if opc=="4":
            print("Ingresa la direccion del archivo que quieres eliminar")
            print(s.eliminarArchivo(input()))
        if opc=="5":
            print("Ingresa el nombre de la(s) carpeta(s) a crear")
            path=input()
            print(s.crearDirectorio(path))
        if opc=="6":
            print("Ingresa la direccion de la carpeta a eliminar")
            path=input()
            print(s.eliminarDirectorio(path))
        if opc=="7":
            print("Ingresa la direccion a mostrar")
            path=input()
            print(s.listarDirectorios(path))
        if opc=="8":
            print("Hasta luego")
            break
except ConnectionRefusedError:
    print("Servidor no disponible por el momento")


