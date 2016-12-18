package felixsystems.vlccontroller.client;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import felixsystems.vlccontroller.exception.telnetException;

public class TelnetConnector {

    private Socket sock = null;
    private BufferedReader in = null;
    private PrintWriter out = null;
    private String ip;

    public TelnetConnector( String ip, int port, String password ) throws telnetException {
        this.ip = ip;
        try {
            sock = new Socket( ip, port );        //Hier tritt der Fehler auf

            in  = new BufferedReader( new InputStreamReader( sock.getInputStream() ) );
            out = new PrintWriter( sock.getOutputStream() );

            out.println( password );
            out.flush();

        } catch ( Exception e ) {
            Log.d( "Error", e.getMessage() );
            throw new telnetException( "Error connection to " + ip + ":" + port + "!" );
        }
    }

    public String getIp() {
        return this.ip;
    }

    public void sendCommand( String command ) throws telnetException{
        try {
            out.println( command );
            out.flush();
        } catch (Exception e) {
            throw new telnetException( "Error sending command to " + sock.getLocalAddress() + ":" + sock.getPort() + "!" );
        }
    }

    public void close() throws telnetException{
        try {
            in.close();
            out.close();
            sock.close();
        } catch ( Exception e ) {
            throw new telnetException( "Error while closing the connection to " + sock.getLocalAddress() + ":" + sock.getPort() + "!" );
        }
    }
}