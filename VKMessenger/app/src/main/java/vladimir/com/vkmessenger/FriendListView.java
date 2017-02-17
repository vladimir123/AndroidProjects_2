package vladimir.com.vkmessenger;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vladimir on 14.08.2016.
 */
public class FriendListView extends ArrayAdapter {
    private final Activity context;
    private final ArrayList<String> photo_url;
    private final ArrayList<String> friend_name;

    private final ArrayList<String> id;
    ArrayList<String> online_ids;


    public FriendListView(Activity context, ArrayList<String> photo_url, ArrayList<String> friend_name, ArrayList<String> f_id, ArrayList<String> online_) {
        super(context, R.layout.friend_view, friend_name);

        this.context = context;
        this.photo_url = photo_url;
        this.friend_name = friend_name;
        this.id = f_id;
        this.online_ids = online_;
    }

    @SuppressLint("LongLogTag")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.friend_view, null, true);

        TextView txt_friend = (TextView) rowView.findViewById(R.id.txt_friend);
        TableRow row = (TableRow) rowView.findViewById(R.id.tbl_row);

        //change row bg color if friend is online

        Log.d("VK_ONLINE_FRIENDS_IN_LIST", String.valueOf(online_ids));

        for ( int i = 0; i < online_ids.size(); i++ )
        {
            Log.d("VK_LIST_ONLINE_1", online_ids.get(i));
            Log.d("VK_LIST_ONLINE_2", String.valueOf(id.get(position).equals(online_ids.get(i))));

            if ( id.get(position).equals(online_ids.get(i)) )
                row.setBackgroundColor(Color.parseColor("#13EC16"));
        }
        //on friend selecting
        row.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Log.d("VK_", "friend selected => "+friend_name.get(position)+" id => "+ id.get(position));

                Intent intent = new Intent(view.getContext(), Chat.class);
                intent.putExtra("user_id", id.get(position));
                intent.putExtra("user_photo", photo_url.get(position));
                intent.putExtra("user_name", friend_name.get(position));
                view.getContext().startActivity(intent);
            }
        });

        txt_friend.setText(friend_name.get(position));
        //show image from url into ImageView
        Picasso.with(context).load(photo_url.get(position)).transform(new CircleTransform()).into((ImageView) rowView.findViewById(R.id.img_photo));
        return rowView;
    }
}
