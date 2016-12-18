package felixsystems.vlccontroller;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Type;
import java.util.ArrayList;

import felixsystems.vlccontroller.client.Client;
import felixsystems.vlccontroller.client.ClientListAdapter;
import felixsystems.vlccontroller.client.CommandSplitter;
import felixsystems.vlccontroller.client.TelnetConnector;
import felixsystems.vlccontroller.exception.telnetException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity  {

    CommandSplitter splitter;
    Button toggleButton;
    private ArrayList<Client> clientList = new ArrayList<>();
    private ClientListAdapter mAdapter;
    private ListView cList;
    private Client selectedClient = null;
    Menu myActionBarMenu;

    @Override
    protected void onCreate( Bundle savedInstanceState ) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //Normalerweise darf man auf Android Netzwerkverbindungen nicht im Main Thread ausführen, so überschreibt man die Regel!
        StrictMode.setThreadPolicy(policy);

        splitter = new CommandSplitter();

        // load tasks from preference
        SharedPreferences prefs = getSharedPreferences( "storedClientList", Context.MODE_PRIVATE );

        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Client>>() {}.getType();
        clientList = gson.fromJson( prefs.getString( "storedClientListElement", gson.toJson(clientList) ), type );

        for ( Client client: clientList ) {
            TelnetConnector connector = null;
            try {
                connector = new TelnetConnector( client.getIp(), 4212, "test" );
            } catch ( telnetException e ) {
                e.printStackTrace();
                Log.d( "errorxx", e.getMessage() );
            }
            splitter.addServer( connector );
        }


        cList = (ListView) findViewById( R.id.clientView );
        mAdapter = new ClientListAdapter( this, clientList );
        cList.setAdapter( mAdapter );
        cList.setSelection( mAdapter.getCount() - 1 ); // scroll down

        cList.setClickable( true );
        cList.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick (AdapterView<?> adapter, View v, int position, long arg3 ) {
                Client currentClient = (Client) mAdapter.getItem( position );
                try {
                    ViewGroup group = (ViewGroup) v;
                    if( group != null ) {
                        View currView;
                        for( int i = 0; i < group.getChildCount(); i++ ) {
                            currView = group.getChildAt(i);
                            if( currView instanceof CheckBox ) {
                                CheckBox checkBox = (CheckBox) currView;
                                if ( checkBox.isChecked() ) {
                                    checkBox.setChecked( false );
                                    clientList.get( position ).setStatus( false );
                                }
                                else {
                                    checkBox.setChecked( true );
                                    clientList.get( position ).setStatus( true );
                                }
                                break;
                            }
                        }
                    }
                } catch ( Exception e ) {
                    Log.e( "error", e.toString() );
                }
            }
        });

        cList.setOnItemLongClickListener (new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                MenuItem remove = myActionBarMenu.findItem(R.id.deleteClient);
                MenuItem add = myActionBarMenu.findItem(R.id.addClient);

                if ( selectedClient == clientList.get( position ) ) {
                    selectedClient = null;
                    view.setBackgroundResource(R.drawable.client);
                    remove.setVisible( false );
                    add.setVisible( true );
                }
                else if ( selectedClient == null ) {
                    selectedClient = clientList.get( position );
                    view.setBackgroundResource( R.drawable.client_select );
                    remove.setVisible( true );
                    add.setVisible( false );
                }
                return true;
            }
        });
    }

    public void clearSelectionColor()
    {
        if ( myActionBarMenu != null )
        {
            MenuItem remove = myActionBarMenu.findItem( R.id.deleteClient );
            remove.setVisible( false );
        }

        if (selectedClient != null)
        {
            for( int i=0; i < cList.getCount(); i++ )
                if ( cList.getChildAt(i) != null )
                    cList.getChildAt(i).setBackgroundResource( R.drawable.client );
        }
        selectedClient = null;

    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item ) {

        switch ( item.getItemId() )
        {
            case R.id.addClient:
                final EditText inputName = new EditText( MainActivity.this );
                final EditText inputIp = new EditText( MainActivity.this );
                final TextView ipText = new TextView( MainActivity.this );
                final TextView nameText = new TextView( MainActivity.this );

                LinearLayout layout = new LinearLayout( this );
                layout.setOrientation( LinearLayout.VERTICAL );

                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                nameText.setLayoutParams( lp );
                nameText.setText( "PC-Name:" );
                layout.addView( nameText );

                inputName.setLayoutParams( lp );
                layout.addView( inputName );

                ipText.setLayoutParams( lp );
                ipText.setText( "IP-Adresse:" );
                layout.addView( ipText );

                inputIp.setLayoutParams( lp );
                layout.addView( inputIp );

                new AlertDialog.Builder( this )
                        .setTitle( "Add a client" )
                        .setMessage( "Please enter a name and the IP of the client." )
                        .setView( layout )
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                TelnetConnector connector = null;
                                try {
                                    connector = new TelnetConnector( inputIp.getText().toString(), 4212, "test" );
                                } catch (telnetException e) {
                                    e.printStackTrace();
                                    Log.d( "errorxx", e.getMessage() );
                                }
                                splitter.addServer(connector);
                                clientList.add( new Client( inputName.getText().toString(), inputIp.getText().toString(), 4212, "test", true ) );
                                mAdapter.notifyDataSetChanged();

                                // save the task list to preference
                                SharedPreferences prefs = getSharedPreferences( "storedClientList", Context.MODE_PRIVATE );
                                SharedPreferences.Editor editor = prefs.edit();

                                Gson gson = new Gson();
                                String setJson = gson.toJson( clientList );
                                editor.putString( "storedClientListElement", setJson );
                                editor.commit();
                            }
                        })
                        .setNegativeButton( android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        } )
                    .setIcon( android.R.drawable.ic_dialog_info )
                    .show();
                break;
            case R.id.deleteClient:
                if ( selectedClient != null ) {
                    clientList.remove( selectedClient );
                    mAdapter.notifyDataSetChanged();


                    // save the task list to preference
                    SharedPreferences prefs = getSharedPreferences( "storedClientList", Context.MODE_PRIVATE );
                    SharedPreferences.Editor editor = prefs.edit();

                    Gson gson = new Gson();
                    String setJson = gson.toJson( clientList );
                    editor.putString( "storedClientListElement", setJson );
                    editor.commit();

                    MenuItem add = myActionBarMenu.findItem( R.id.addClient );
                    add.setVisible( true );
                }
                // delete background highlight
                clearSelectionColor();
                break;
            case R.id.reConnect:
                splitter.clearServer();
                for ( Client client: clientList ) {
                    TelnetConnector connector = null;
                    try {
                        connector = new TelnetConnector( client.getIp(), 4212, "test" );
                    } catch ( telnetException e ) {
                        e.printStackTrace();
                        Log.d( "errorxx", e.getMessage() );
                    }
                    splitter.addServer(connector);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() //activity was resumed and is visible again
    {
        super.onResume();

        clearSelectionColor();
    }

    @Override
    public boolean onCreateOptionsMenu( Menu menu ) {
        this.myActionBarMenu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate( R.menu.main_actionbar, menu );

        ActionBar acMenu = getSupportActionBar();
        acMenu.setDisplayShowHomeEnabled( true );
        return true;
    }

    public void play(View v) {
        //Toast.makeText(getApplicationContext(), "Toggling Play/Pause", Toast.LENGTH_LONG).show();
        toggleButton = (Button) findViewById( R.id.play );
        if ( toggleButton.getText().toString().equals( "Play" ) ) {
            splitter.runCommand( clientList, "play" );
            toggleButton.setText( "Pause" );
        }
        else {
            splitter.runCommand( clientList, "pause" );
            toggleButton.setText( "Play" );
        }
    }

    public void updateClientList( View v ) {
        //Log.d("test", Integer.toString(v.g .getId()));
    }

    public void nextVideo(View v){
        //Toast.makeText(getApplicationContext(), "Toggling Play/Pause", Toast.LENGTH_LONG).show();
        splitter.runCommand( clientList, "next");
    }

    public void prevVideo(View v){
        //Toast.makeText(getApplicationContext(), "Toggling Play/Pause", Toast.LENGTH_LONG).show();
        splitter.runCommand( clientList, "prev");
    }

    public void fullscreen(View v){
        //Toast.makeText(getApplicationContext(), "Toggling Play/Pause", Toast.LENGTH_LONG).show();
        splitter.runCommand( clientList, "fullscreen");
    }

    public void loop(View v){
        //Toast.makeText(getApplicationContext(), "Toggling Play/Pause", Toast.LENGTH_LONG).show();
        splitter.runCommand( clientList, "loop");
    }
}
