package nsir.oumaima.mybestlocations.ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import nsir.oumaima.mybestlocations.Position;
import nsir.oumaima.mybestlocations.R;

public class PositionAdapter extends ArrayAdapter<Position> {
    private Context context;
    private ArrayList<Position> data;
    private OnItemClickListener listener;  // Interface for button clicks

    public PositionAdapter(Context context, ArrayList<Position> data) {
        super(context, 0, data);
        this.context = context;
        this.data = data;
    }

    // Custom listener interface
    public interface OnItemClickListener {
        void onMapClick(Position position);

        void onActivityResult(int requestCode, int resultCode, @Nullable Intent data);

        void onDeleteClick(Position position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_position, parent, false);
        }

        Position currentPosition = data.get(position);
        Position p = data.get(position);
        TextView txtPseudo = convertView.findViewById(R.id.txtPseudo);
        TextView txtNumero = convertView.findViewById(R.id.txtNumero);
        TextView txtLocation = convertView.findViewById(R.id.txtLocation);
        Button btnMap = convertView.findViewById(R.id.btnMap);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        // Set the data to the views
        txtPseudo.setText("Pseudo: " + currentPosition.getPseudo());
        txtNumero.setText("Numero: " + currentPosition.getNumero());
        txtLocation.setText("Location: " + currentPosition.getLatitude() + ", " + currentPosition.getLongitude());
        Log.d("DEBUG", "currentPosition: " + currentPosition.getLatitude() + ", " + currentPosition.getLongitude());

        // Map Button
        btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String uri = "geo:" + p.latitude + "," + p.longitude + "?q=" + p.latitude + "," + p.longitude + "(" + p.pseudo + ")";
            intent.setData(android.net.Uri.parse(uri));
            context.startActivity(intent);
        });



        // Delete Button: Remove from list and update the database (optional)
        btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(currentPosition);
            }
        });

        return convertView;
    }
}

