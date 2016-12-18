package felixsystems.vlccontroller.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import felixsystems.vlccontroller.R;

/**
 * Created by xifizurk on 26.10.16.
 */
public class ClientListAdapter extends BaseAdapter {

    private ArrayList<Client> mListItems;
    private LayoutInflater mLayoutInflater;

    public ClientListAdapter( Context context, ArrayList<Client> arrayList ){

        mListItems = arrayList;

        //get the layout inflater
        mLayoutInflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
    }

    @Override
    public int getCount() {
        //getCount() represents how many items are in the list
        return mListItems.size();
    }

    @Override
    //get the data of an item from a specific position
    //i represents the position of the item in the list
    public Object getItem(int i) {
        return null;
    }

    @Override
    //get the position id of the item from the list
    public long getItemId(int i) {
        return 0;
    }

    @Override

    public View getView( int position, View view, ViewGroup viewGroup ) {

        //check to see if the reused view is null or not, if is not null then reuse it
        if ( view == null ) {
            view = mLayoutInflater.inflate( R.layout.list_item, null );
        }

        //get the string item from the position "position" from array list to put it on the TextView
        Client client = mListItems.get( position );
        if ( client != null ) {

            TextView clientName = (TextView) view.findViewById( R.id.clientName );
            TextView clientIp = (TextView) view.findViewById( R.id.clientIp );
            if ( clientName != null ) {
                //set the item name on the TextView
                clientName.setText( client.getName() );
                clientIp.setText( client.getIp() );
            }
        }

        //this method must return the view corresponding to the data at the specified position.
        return view;

    }
}
