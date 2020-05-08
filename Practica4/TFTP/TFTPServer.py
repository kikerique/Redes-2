#!/usr/bin/python3
# vim: ts=4 sw=4 et ai:
# -*- coding: utf8 -*-

import sys, logging
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
    parser = OptionParser(usage=usage)
    parser.add_option('-i',
                      '--ip',
                      type='string',
                      help='Direccion ip donde se iniciara el servidor',
                      default="")
    parser.add_option('-p',
                      '--port',
                      type='int',
                      help='Puerto donde se hara la comunicación con el servidor (default: 69)',
                      default=69)
    parser.add_option('-r',
                      '--root',
                      type='string',
                      help='Dirección raíz del servidor',
                      default=None)
    options, args = parser.parse_args()

    if not options.root:
        parser.print_help()
        sys.exit(1)

    server = tftpy.TftpServer(options.root)
    try:
        server.listen(options.ip, options.port)
    except tftpy.TftpException as err:
        sys.stderr.write("%s\n" % str(err))
        sys.exit(1)
    except KeyboardInterrupt:
        print("Deteniendo el servidor")
        server.stop()
        sys.exit(1)

if __name__ == '__main__':
    main()
