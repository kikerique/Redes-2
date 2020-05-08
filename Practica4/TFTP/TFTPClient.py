#!/usr/bin/python3
# vim: ts=4 sw=4 et ai:
# -*- coding: utf8 -*-

import sys, logging, os
from optparse import OptionParser
import tftpy

log = logging.getLogger('tftpy')
log.setLevel(logging.INFO)

# console handler
handler = logging.StreamHandler()
handler.setLevel(logging.DEBUG)
default_formatter = logging.Formatter('[%(asctime)s] %(message)s')
handler.setFormatter(default_formatter)
log.addHandler(handler)

def main():
    usage=""
    opc=""
    fileName=""
    fileName2=""
    parser = OptionParser(usage=usage)
    parser.add_option('-H',
                      '--host',
                      help='Direccion ip del servidor')
    parser.add_option('-p',
                      '--port',
                      help='Puerto de escucha del servidor (default: 69)',
                      default=69)
    parser.add_option('-b',
                      '--blksize',
                      help='longitud de los paquetes udp a utilizar (default: 512)')
    parser.add_option('-t',
                      '--tsize',
                      action='store_true',
                      default=False,
                      help="ask client to send tsize option in download")
    options, args = parser.parse_args()

    if not options.host:
        sys.stderr.write("Es necesario que ingreses la direccion de host al menos\n")
        parser.print_help()
        sys.exit(1)


    class Progress(object):
        def __init__(self, out):
            self.progress = 0
            self.out = out

        def progresshook(self, pkt):
            if isinstance(pkt, tftpy.TftpPacketTypes.TftpPacketDAT):
                self.progress += len(pkt.data)
                self.out("Transferred %d bytes" % self.progress)
            elif isinstance(pkt, tftpy.TftpPacketTypes.TftpPacketOACK):
                self.out("Received OACK, options are: %s" % pkt.options)
        
    progresshook = Progress(log.info).progresshook
    #opciones extra del protocolo TFTP
    tftp_options = {}
    if options.blksize:
        tftp_options['blksize'] = int(options.blksize)
    if options.tsize:
        tftp_options['tsize'] = int(options.tsize)

    try:
      tclient = tftpy.TftpClient(options.host,
                               int(options.port),
                               tftp_options)
    except tftpy.TftpException as e:
      sys.stderr.write("Error: %s\n" % str(e))
      sys.exit(1)
    while opc!="3":
      print("Bienvenido Cliente, Escoge la accion a realizar:\n1:Solicitar lectura de un archivo\n2:Solicitar escritura de un archivo\n3:Salir")
      opc=input()
      try:
          if opc=="1":
              print("Ingresa el nombre del archivo que quieres leer:")
              fileName=input()
              print("Ingresa el nombre del archivo de destino:")
              fileName2=input()
              tclient.download(fileName,
                               fileName2,
                               progresshook)
          if opc=="2":
            print("Ingresa el nombre del archivo externo")
            fileName=input()
            print("Ingresa el nombre del archivo local")
            fileName2=input()
            tclient.upload(fileName,
                            fileName2,
                             progresshook)
          if opc=="3":
            print("Hasta luego")
            break
      except tftpy.TftpException as err:
          sys.stderr.write("Error: %s\n" % str(err))
      except KeyboardInterrupt:
          pass

if __name__ == '__main__':
    main()
