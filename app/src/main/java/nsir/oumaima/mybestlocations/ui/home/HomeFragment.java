package nsir.oumaima.mybestlocations.ui.home;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import nsir.oumaima.mybestlocations.Config;
import nsir.oumaima.mybestlocations.JSONParser;
import nsir.oumaima.mybestlocations.Position;
import nsir.oumaima.mybestlocations.databinding.FragmentHomeBinding;
import nsir.oumaima.mybestlocations.ui.PositionAdapter;
import nsir.oumaima.mybestlocations.ui.gallery.MapFragment;

public class HomeFragment extends Fragment {
    ArrayList<Position> data=new ArrayList<Position>();
    private ActivityResultLauncher<Intent> mapLauncher;
    AlertDialog progressDialog = null;

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mapLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            double latitude = data.getDoubleExtra("latitude", 0);
                            double longitude = data.getDoubleExtra("longitude", 0);

                            Toast.makeText(getContext(), "Lat: " + latitude + ", Lng: " + longitude, Toast.LENGTH_LONG).show();
                            // TODO: Use the coordinates
                        }
                    }
                }
        );

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the saved IP address from SharedPreferences
                SharedPreferences prefs = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
                String ipAddress = prefs.getString("server_ip", "");

                if (ipAddress.isEmpty()) {
                    // If the IP address is empty, show a toast asking the user to enter it
                    Toast.makeText(getContext(), "Please enter an IP address in the Dashboard", Toast.LENGTH_SHORT).show();
                } else {
                    // If the IP address is set, proceed with the download task
                    Telechargement t = new Telechargement();
                    t.execute();
                }
            }
        });

        return root;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    class Telechargement extends AsyncTask{
        AlertDialog alert;
        @Override
        protected void onPreExecute() {
        //userinterface thread=Mainthread
            if (getActivity() == null || getActivity().isFinishing()) {
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Téléchargement en cours...");
            builder.setCancelable(false);
            progressDialog = builder.create();

            if (!progressDialog.isShowing()) {
                progressDialog.show();
            }

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            //second thread
            JSONParser parser=new JSONParser();
            JSONObject result=parser.makeRequest(Config.getGetAllUrl(requireContext()));
            Log.e("response",result.toString());
            try {
                int success=result.getInt("success");
                if(success==0){
                    String message=result.getString("message");
                    Log.e("message",message);
                }else{
                    JSONArray tab=result.getJSONArray("positions");
                    data.clear();
                    for (int i = 0; i < tab.length(); i++) {
                        JSONObject ligne=tab.getJSONObject(i);
                        int idposition=ligne.getInt("idposition");
                        String pseudo=ligne.getString("pseudo");
                        String numero=ligne.getString("numero");
                        String longitude=ligne.getString("longitude");
                        String latitude=ligne.getString("latitude");
                        Position p=new Position(idposition,pseudo,numero,longitude,latitude);
                        data.add(p);
                    }
                }
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {

            //userinterface thread
            if (  progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }

            PositionAdapter adapter = new PositionAdapter(getActivity(), data);
            binding.idListView.setAdapter(adapter);
            adapter.setOnItemClickListener(new PositionAdapter.OnItemClickListener() {
                public void onMapClick(Position position) {
                    Intent intent = new Intent(getContext(), MapFragment.class);
                    mapLauncher.launch(intent); 

                }

                @Override
                public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

                }

                @Override
                public void onDeleteClick(Position position) {

                    data.remove(position);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(getActivity(), "Location deleted", Toast.LENGTH_SHORT).show();

                    new Thread(() -> {
                        try {
                            JSONParser parser = new JSONParser();
                            String deleteUrl = Config.getDeleteUrl(requireContext()) + "?id=" + position.getIdposition();
                            JSONObject result = parser.makeRequest(deleteUrl);

                            int success = result.getInt("success");

                            requireActivity().runOnUiThread(() -> {
                                if (success == 1) {
                                    Toast.makeText(getActivity(), "Location successfully deleted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getActivity(), "Error deleting location", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(getActivity(), "Error deleting location", Toast.LENGTH_SHORT).show()
                            );
                        }
                    }).start();
                }

            });
        }
        //ajout -> addposition nafs code telechagement makehttpsrequest
    }
}