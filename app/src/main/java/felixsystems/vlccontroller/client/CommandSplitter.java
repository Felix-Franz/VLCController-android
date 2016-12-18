package felixsystems.vlccontroller.client;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import felixsystems.vlccontroller.exception.telnetException;

public class CommandSplitter {
	private ArrayList<TelnetConnector> server = new ArrayList<>();
	
	public void addServer( TelnetConnector connector ){
		server.add(connector);
	}

	public void clearServer() { server.clear(); }
	
	public void runCommand( String command ){
		if ( command.equals( "exit" ) ){
			for ( TelnetConnector connector: server ){
				try {
					connector.close();
				} catch ( telnetException e ) {
				}
			}
		}
	}

	public void runCommand( ArrayList<Client> clientList, String command ){
		for (TelnetConnector connector: server) {
			Boolean isInListAndActive = false;

			for ( Client client: clientList ) {
				if ( connector.getIp().equals( client.getIp() ) && client.getStatus() )
					isInListAndActive = true;
			}

			if ( isInListAndActive ) {
				try {
					Log.d( "test", command );
					connector.sendCommand( command );
				} catch ( telnetException e ) {
					Log.d( "Error", "Error sending command to one of the server!" );
				}
			}
		}
	}
}
