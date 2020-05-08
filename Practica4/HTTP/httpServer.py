"""
Ejemplo de un servidor HTTP simple usando la libreria http.server de python

Equipo: Los 4 fantásticos 
"""

from http.server import HTTPServer, BaseHTTPRequestHandler
from io import BytesIO,StringIO
import os
import posixpath
import urllib.parse as urlparse
import cgi
import sys
import shutil
import mimetypes

#Clase que implementa la clase BaseHTTPRequestHandler para atender a las peticiones 
class SimpleHTTPRequestHandler(BaseHTTPRequestHandler):

    def do_GET(self):
        #Método necesario para manejar las peticiones GET
        f = self.send_head()
        if f:
            try:
                self.copyfile(f, self.wfile)
            finally:
                f.close()

    def do_HEAD(self):
        """
        Método necesario para manejar las peticiones HEAD
        estas peticiones no se contestan con ningún mensaje, sólo con un código de status de respuesta
        """
        self.send_head()

    def do_POST(self):
        #Método necesario para manejar las peticiones POST
        path=self.translate_path(self.path)
        if os.path.exists(path):
            content_length = int(self.headers['Content-Length'])
            body = self.rfile.read(content_length)  #rfile especifica el flujo de entrada para leer los datos recibidos
            self.send_response(200)
            self.end_headers()
            response = BytesIO()  #Crea un buffer para escribir datos
            response.write(b'Hiciste una peticion POST. ') #El metodo write agrega los datos enviados como parámetros al buffer creado
            response.write(b'Recibi esto: ')
            response.write(body)
            response.write(b"Ahora deberia ejecutar el archivo referente a la direccion de la peticion")
            self.wfile.write(response.getvalue())
        else:
            self.send_error(404)

    def do_PUT(self):
        #Método necesario para manejar las peticiones PUT
        path=self.translate_path(self.path)
        if os.path.exists(path):
            if os.path.isdir(path):
                self.send_error(409)
                self.end_headers()
            else:
                response = BytesIO()  #Crea un buffer para escribir datos
                try:
                    f=open(path,"wb")
                    self.send_response(200)
                    self.end_headers()
                    content_length = int(self.headers['Content-Length'])
                    body = self.rfile.read(content_length)  #rfile especifica el flujo de entrada para leer los datos recibidos
                    response.write(body)
                    f.write(body)
                    f.close()
                except PermissionError:
                    self.send_error(401)
                    self.end_headers()
                
        else:
            if(os.path.isdir(path)):
                self.send_error(409)
                self.end_headers()
            else:
                response = BytesIO()  #Crea un buffer para escribir datos
                try:
                    f=open(path,"wb")
                    self.send_response(201)
                    self.end_headers()
                    content_length = int(self.headers['Content-Length'])
                    body = self.rfile.read(content_length)  #rfile especifica el flujo de entrada para leer los datos recibidos
                    response.write(body)
                    f.write(body)
                    f.close()
                except PermissionError:
                    self.send_error(401)
                    self.end_headers()
    def do_DELETE(self):
        """
        Método necesario para manejar las peticiones DELETE
        """
        #Método necesario para manejar las peticiones PUT
        path=self.translate_path(self.path)
        if os.path.exists(path):
            if os.path.isdir(path):
                self.send_error(401)
                self.end_headers()
            else:
                response = BytesIO()  #Crea un buffer para escribir datos
                try:
                    f=open(path,"wb")
                    self.send_response(200)
                    self.end_headers()
                    f.close()
                    os.chmod(path,0o000)
                except PermissionError:
                    self.send_error(401)
                    self.end_headers()
        else:
            self.send_error(404)
            self.end_headers()

    def do_CONNECT(self):
        """
        Método necesario para manejar las peticiones CONNECT
        """
        self.send_response(200)
        self.end_headers()
        self.send_response(200)
        self.end_headers()
        response = BytesIO()  #Crea un buffer para escribir datos
        response.write(b'Hiciste una peticion CONNECT. ') #El metodo write agrega los datos enviados como parámetros al buffer creado
        string="\nAhora deberia hace un puente entre tu y la direccion " + self.path+ " pero no puedo XD"
        response.write(string.encode())
        self.wfile.write(response.getvalue())

    def do_OPTIONS(self):
        """
        Método necesario para manejar las peticiones CONNECT
        """
        self.send_response(200)
        self.end_headers()
        response = BytesIO()  #Crea un buffer para escribir datos
        response.write(b'Hiciste una peticion OPTIONS. ') #El metodo write agrega los datos enviados como parámetros al buffer creado
        self.wfile.write(response.getvalue())

    def do_TRACE(self):
        """
        Método necesario para manejar las peticiones CONNECT
        """
        self.send_response(200)
        self.end_headers()
        response = BytesIO()  #Crea un buffer para escribir datos
        response.write(b'Hiciste una peticion TRACE. ') #El metodo write agrega los datos enviados como parámetros al buffer creado
        self.wfile.write(response.getvalue())

 
    def send_head(self):
        path = self.translate_path(self.path)
        f = None
        if os.path.isdir(path):
            parts = urlparse.urlsplit(self.path)
            if not parts.path.endswith('/'):
                # redirect browser - doing basically what apache does
                self.send_response(301)
                new_parts = (parts[0], parts[1], parts[2] + '/',
                             parts[3], parts[4])
                new_url = urlparse.urlunsplit(new_parts)
                self.send_header("Location", new_url)
                self.end_headers()
                return None
            for index in "index.html", "index.htm":
                index = os.path.join(path, index)
                if os.path.exists(index):
                    path = index
                    break
            else:
                return self.list_directory(path)
        ctype = self.guess_type(path)
        try:
            # Always read in binary mode. Opening files in text mode may cause
            # newline translations, making the actual size of the content
            # transmitted *less* than the content-length!
            f = open(path, 'rb')
        except IOError:
            self.send_error(404, "File not found")
            return None
        try:
            self.send_response(200)
            self.send_header("Content-type", ctype)
            fs = os.fstat(f.fileno())
            self.send_header("Content-Length", str(fs[6]))
            self.send_header("Last-Modified", self.date_time_string(fs.st_mtime))
            self.end_headers()
            return f
        except:
            f.close()
            raise

    def list_directory(self, path):
        """Helper to produce a directory listing (absent index.html).

        Return value is either a file object, or None (indicating an
        error).  In either case, the headers are sent, making the
        interface the same as for send_head().

        """
        try:
            list = os.listdir(path)
        except os.error:
            self.send_error(404, "No permission to list directory")
            return None
        list.sort(key=lambda a: a.lower())
        f = BytesIO()
        displaypath = cgi.escape(urlparse.unquote(self.path)).encode()

        f.write(b'<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 3.2 Final//EN">')
        string="<html>\n<title>Directory listing for %s</title>\n" % displaypath
        f.write(string.encode())
        string="<body>\n<h2>Directory listing for %s</h2>\n" % displaypath
        f.write(string.encode())
        f.write(b"<hr>\n<ul>\n")
        for name in list:
            fullname = os.path.join(path, name)
            displayname = linkname = name
            # Append / for directories or @ for symbolic links
            if os.path.isdir(fullname):
                displayname = name + "/"
                linkname = name + "/"
            if os.path.islink(fullname):
                displayname = name + "@"
                # Note: a link to a directory displays with @ and links with /
            string='<li><a href="%s">%s</a>\n'% (urlparse.quote(linkname), cgi.escape(displayname))
            f.write(string.encode())
        f.write(b"</ul>\n<hr>\n</body>\n</html>\n")
        length = f.tell()
        f.seek(0)
        self.send_response(200)
        encoding = sys.getfilesystemencoding()
        self.send_header("Content-type", "text/html; charset=%s" % encoding)
        self.send_header("Content-Length", str(length))
        self.end_headers()
        return f

    def translate_path(self, path):
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

    def copyfile(self, source, outputfile):
        """Copy all data between two file objects.

        The SOURCE argument is a file object open for reading
        (or anything with a read() method) and the DESTINATION
        argument is a file object open for writing (or
        anything with a write() method).

        The only reason for overriding this would be to change
        the block size or perhaps to replace newlines by CRLF
        -- note however that this the default server uses this
        to copy binary data as well.

        """
        shutil.copyfileobj(source, outputfile)

    def guess_type(self, path):
        """Guess the type of a file.

        Argument is a PATH (a filename).

        Return value is a string of the form type/subtype,
        usable for a MIME Content-type header.

        The default implementation looks the file's extension
        up in the table self.extensions_map, using application/octet-stream
        as a default; however it would be permissible (if
        slow) to look inside the data to make a better guess.

        """

        base, ext = posixpath.splitext(path)
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        ext = ext.lower()
        if ext in self.extensions_map:
            return self.extensions_map[ext]
        else:
            return self.extensions_map['']

    if not mimetypes.inited:
        mimetypes.init() # try to read system mime.types
    extensions_map = mimetypes.types_map.copy()
    extensions_map.update({
        '': 'application/octet-stream', # Default
        '.py': 'text/plain',
        '.c': 'text/plain',
        '.h': 'text/plain',
        })
try:
    #Instanciamos la clase HTTPServer, establecemos su dirección de HOST y  el PORT junto con la clase que atenderá las peticiones
    server = HTTPServer(('localhost', 8000), SimpleHTTPRequestHandler) 
    print("Servidor listo en la direccion: localhost:8000")
    server.serve_forever()  #Ponemos el servidor en espera de manera indefinida para que acepte las peticiones de los clientes

except KeyboardInterrupt:
    print(' recibido, se cerrara el Servidor')
    server.socket.close() #Cerramos el socket por donde se comunicaba el servidor