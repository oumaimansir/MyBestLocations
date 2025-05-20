package nsir.oumaima.mybestlocations.ui.gallery;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import nsir.oumaima.mybestlocations.R;

public class DashboardFragment extends Fragment {
    private EditText edTxtIp;
    private Button btnSaveIp;

    public DashboardFragment() {
        super(R.layout.fragment_dashboard);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);

        edTxtIp = root.findViewById(R.id.edTxtIp);
        btnSaveIp = root.findViewById(R.id.btnSaveIp);


        SharedPreferences prefs = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        prefs.edit().remove("server_ip").apply();  // Remove the saved IP

        btnSaveIp.setOnClickListener(v -> {
            String ip = edTxtIp.getText().toString().trim();
            if (!ip.isEmpty()) {
                prefs.edit().putString("server_ip", ip).apply();  // Save the new IP
                Toast.makeText(getActivity(), "Adresse IP enregistrée", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Veuillez entrer une adresse IP", Toast.LENGTH_SHORT).show();
            }
        });


        return root;
    }
}

