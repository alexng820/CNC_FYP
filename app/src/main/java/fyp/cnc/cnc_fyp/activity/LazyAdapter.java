package fyp.cnc.cnc_fyp.activity;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.HashMap;

import fyp.cnc.cnc_fyp.R;

public class LazyAdapter extends BaseAdapter {
    
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    private static LayoutInflater inflater=null;
    public ImageLoader imageLoader;

    public LazyAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data=d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }
    
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.list_row, null);

        TextView topic = (TextView)vi.findViewById(R.id.topic); // title
        TextView guest = (TextView)vi.findViewById(R.id.guest); // artist name
        TextView participant = (TextView)vi.findViewById(R.id.participant); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
        HashMap<String, String> event = new HashMap<String, String>();
        event = data.get(position);
        
        // Setting all values in listview
        topic.setText(event.get("topic"));
        guest.setText(event.get("guest"));
        if(event.get("participant")=="Full"){
            participant.setTextColor(0xfffc3131);
        }
        participant.setText(event.get("participant"));
        imageLoader.DisplayImage("", thumb_image);
        return vi;
    }
}