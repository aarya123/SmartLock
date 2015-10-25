#!/usr/bin/python
import SimpleHTTPServer
import SocketServer
import sys

from gcm import GCM

if len(sys.argv) > 2:
    PORT = int(sys.argv[2])
    I = sys.argv[1]
elif len(sys.argv) > 1:
    PORT = int(sys.argv[1])
    I = ""
else:
    PORT = 8000
    I = ""
devices = set()
gcm = GCM("AIzaSyBZf1umzXPNtgP9EqWCx3OhnSq5GW6MUtE")

class ServerHandler(SimpleHTTPServer.SimpleHTTPRequestHandler):
    def notify_doorbell(self):
        print gcm.json_request(list(devices), data={"Notice": "Someone is at the door!"})

    def get_params(self, data):
        param_list = data.split("\n")
        param_list.remove("")
        params = {}
        for param in param_list:
            key_value = param.split("=")
            params[key_value[0]] = key_value[1]
        return params

    def do_GET(self):
        self.send_response(200)
        self.send_header("Content-type", "text/plain")
        self.end_headers()
        if "Content-Length" in self.headers:
            data = self.rfile.read(int(self.headers["Content-Length"]))
            params = self.get_params(data)
            if "register" in params:
                devices.add(params["register"])
                self.notify_doorbell()
        self.wfile.write("hello world!")
        self.wfile.close()


Handler = ServerHandler
httpd = SocketServer.TCPServer(("", PORT), Handler, bind_and_activate=False)
httpd.allow_reuse_address = True
httpd.server_bind()
httpd.server_activate()
try:
    httpd.serve_forever()
except BaseException:
    pass
httpd.server_close()
