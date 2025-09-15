#!/usr/bin/env python3

from http.server import HTTPServer, BaseHTTPRequestHandler
import time
from urllib.parse import urlparse
import tempfile
import os
from subprocess import PIPE,run, CalledProcessError

class HandleRequests(BaseHTTPRequestHandler):

    def __init__(self, request, client_address, server):
        super(HandleRequests, self).__init__(request, client_address, server)
        self.protocol_version = "HTTP/1.1"

    def _write_error(self, error_code, stderr):
        self.send_response(error_code)
        self.send_header("Content-type", "text/plain; charset=UTF-8")
        self.end_headers()
        for line in stderr.split('\n'):
            print(line)
            self.wfile.write(line.encode("utf-8"))
 

    def _set_headers(self):
        self.send_response(200)
        self.send_header("Content-type", "text/html")
        self.end_headers()

    def do_GET(self):

        parsed = urlparse(self.path)
        tempFileName=tempfile.NamedTemporaryFile()
        tempFileName.close()
        fName=tempFileName.name + ".jfr"
        command=["/run_jcmd.sh", fName, parsed.query]
        try:
             result = run(command, encoding='utf-8', stderr=PIPE)
        except CalledProcessError as e:
            self._write_error(500, result.stderr)
            return

        if result.returncode != 0:
            self._write_error(400, result.stderr)
        else:
            if (os.path.isfile(fName) != True):
                self._write_error(500, "jcmd is not started well")
                return


            while(os.stat(fName).st_size == 0):
                time.sleep(5)
                print("wait jcmd")

            self.send_response(200)
            self.send_header("Content-type", "application/octet-stream")
            self.send_header("Content-Length", os.stat(fName).st_size)
            self.end_headers()
            with open(fName, mode="rb") as f:
                self.wfile.write(f.read())
            os.remove(fName)



if __name__ == '__main__':
    from sys import argv

    if len(argv) == 3:
        server_address = (argv[1], int(argv[2]))
    else:
        if len(argv) == 2:
            server_address = ("0.0.0.0", int(argv[1]))
        else:
            server_address = ("0.0.0.0", 12345)

    print("listen on: " + str(server_address))

    HTTPServer(server_address, HandleRequests).serve_forever()

