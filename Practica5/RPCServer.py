from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler
import sys
import os
import posixpath
import urllib.parse as urlparse
import os.path as verifica
import xmlrpc.client as datos

# Restrict to a particular path.
class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)

def translate_path(path):
        """Translate a /-separated PATH to the local filename syntax.

        Components that mean special things to the local file system
        (e.g. drive or directory names) are ignored.  (XXX They should
        probably be diagnosed.)

        """
        # abandon query parameters
        path = path.split('?',1)[0]
        path = path.split('#',1)[0]
        # Don't forget explicit trailing slash when normalizing. Issue17324
        trailing_slash = path.rstrip().endswith('/')
        path = posixpath.normpath(urlparse.unquote(path))
        words = path.split('/')
        words = filter(None, words)
        path = os.getcwd()
        for word in words:
            if os.path.dirname(word) or word in (os.curdir, os.pardir):
                # Ignore components that are not a simple file/directory name
                continue
            path = os.path.join(path, word)
        if trailing_slash:
            path += '/'
        return path

class archivos:
    root=translate_path("root/")

    def leer(self,nombreArchivo):
        archivo=verifica.join(self.root,nombreArchivo)
        if not(verifica.exists(archivo)):
            return "El archivo que quieres leer no existe"
        elif verifica.isdir(archivo):
            return "El path proporcionado no es un archivo"
        with open(archivo, "rb") as file:
            return datos.Binary(file.read())

    def escribir(self,nombreArchivo,datos):
        nombre=verifica.join(self.root,nombreArchivo)
        try:
            archivo=open(nombre,"wb")
            archivo.write(datos.data)
            archivo.close()
            return "Modificado con exito"
        except Exception as e:
            return str(e)

    def crear(self,nombreArchivo,datos,ruta=""):
        nombreArchivo=verifica.join(self.root,verifica.join(ruta,nombreArchivo))
        if verifica.isfile(nombreArchivo):
            return "El archivo ya existe en el servidor"
        else:
            try:
                archivo=open(nombreArchivo,"wb")
                archivo.write(datos.data)
                archivo.close()
                return "Archivo creado con exito"
            except FileNotFoundError as e:
                return "La ruta de destino no existe"
            
    def eliminarArchivo(self,path):
        path=verifica.join(self.root,path)
        if verifica.isfile(path):
            try:
                os.remove(path)
                return "Archivo eliminado con exito"
            except Exception as e:
                return str(e)
        else:
            return "El archivo no existe"
    def eliminarDirectorio(self,path):
        path=verifica.join(self.root,path)
        if verifica.isdir(path):
            try:
                os.rmdir(path)
                return "Directorio eliminado con exito"
            except Exception as e:
                return str(e)
        else:
            return "El directorio no existe"
    def crearDirectorio(self,nombre):
        nombreDirectorio=verifica.join(self.root,nombre)
        if verifica.isdir(nombreDirectorio):
            return "El Directorio ya existe en el servidor"
        else:
            try:
                os.makedirs(nombreDirectorio)
                return "Directorio creado con exito"
            except Exception as e:
                return str(e)
    def listarDirectorios(self, direccion="/"):
        path=verifica.join(self.root,direccion)
        string="Arbol de la direccion: "+direccion+"\n"
        try:
            list = os.listdir(path)
        except os.error:
            return "No tengo permisos para esa accion"
        list.sort(key=lambda a: a.lower())
        for name in list:
            fullname = verifica.join(path, name)
            displayname = linkname = name
            # Append / for directories or @ for symbolic links
            if verifica.isdir(fullname):
                displayname = name + "/"
                linkname = name + "/"
            string+=urlparse.quote(linkname)+"\n"
        return string
if len(sys.argv)!=3:
    print("Modo de uso: ejecutable HOST PORT")
    sys.exit(1)
try:
    server= SimpleXMLRPCServer((sys.argv[1],int(sys.argv[2])),requestHandler=RequestHandler)
    server.register_introspection_functions()
    server.register_instance(archivos())
    print("Escuchando en la direccion "+sys.argv[1]+":"+sys.argv[2])
    # Run the server's main loop
    server.serve_forever()
except Exception as e:
    print(str(e))
except KeyboardInterrupt:
    print("\nKeyboard interrupt recibida, saliendo.")
    sys.exit(0)
