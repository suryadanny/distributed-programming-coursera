package edu.coursera.distributed;

import java.net.ServerSocket;
import java.net.Socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.io.File;

/**
 * A basic and very limited implementation of a file server that responds to GET
 * requests from HTTP clients.
 */
public final class FileServer {
    /**
     * Main entrypoint for the basic file server.
     *
     * @param socket Provided socket to accept connections on.
     * @param fs A proxy filesystem to serve files from. See the PCDPFilesystem
     *           class for more detailed documentation of its usage.
     * @throws IOException If an I/O error is detected on the server. This
     *                     should be a fatal error, your file server
     *                     implementation is not expected to ever throw
     *                     IOExceptions during normal operation.
     */
    public void run(final ServerSocket socket, final PCDPFilesystem fs)
            throws IOException {
        /*
         * Enter a spin loop for handling client requests to the provided
         * ServerSocket object.
         */
    	while (true) {

            // TODO Delete this once you start working on your solution.
            
            // TODO 1) Use socket.accept to get a Socket object
             Socket socketAcquired = socket.accept();
             
             InputStream stream = socketAcquired.getInputStream();
             InputStreamReader streamReader = new InputStreamReader(stream);
             BufferedReader reader = new BufferedReader(streamReader);
             
             String line = reader.readLine();
             String dir = line.split(" ")[1];
             String st = fs.readFile(new PCDPPath(dir));
             
             OutputStream outStream =  socketAcquired.getOutputStream();
             PrintWriter streamWriter = new PrintWriter(outStream);
             if(st != null) {
            	streamWriter.write("HTTP/1.0 200 OK\r\n");
            	streamWriter.write("Server: FileServer\r\n");
            	streamWriter.write("\r\n");
            	streamWriter.write(st +"\r\n");
            	
             }else {
            	 streamWriter.write("HTTP/1.0 404 Not Found\r\n");
             	 streamWriter.write("Server: FileServer\r\n");
             	 streamWriter.write("\r\n");
             	 
             }
            
             streamWriter.close();
             reader.close();
            // socketAcquired.close();
             /*
             * TODO 2) Using Socket.getInputStream(), parse the received HTTP
             * packet. In particular, we are interested in confirming this
             * message is a GET and parsing out the path to the file we are
             * GETing. Recall that for GET HTTP packets, the first line of the
             * received packet will look something like:
             *
             *     GET /path/to/file HTTP/1.1
             */

            /*
             * TODO 3) Using the parsed path to the target file, construct an
             * HTTP reply and write it to Socket.getOutputStream(). If the file
             * exists, the HTTP reply should be formatted as follows:
             *
             *   HTTP/1.0 200 OK\r\n
             *   Server: FileServer\r\n
             *   \r\n
             *   FILE CONTENTS HERE\r\n
             *
             * If the specified file does not exist, you should return a reply
             * with an error code 404 Not Found. This reply should be formatted
             * as:
             *
             *   HTTP/1.0 404 Not Found\r\n
             *   Server: FileServer\r\n
             *   \r\n
             *
             * Don't forget to close the output stream.
             */
        }
    }
}
