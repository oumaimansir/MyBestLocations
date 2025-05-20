package nsir.oumaima.mybestlocations.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import nsir.oumaima.mybestlocations.Config;
import nsir.oumaima.mybestlocations.JSONParser;
import nsir.oumaima.mybestlocations.R;
import nsir.oumaima.mybestlocations.databinding.FragmentGalleryBinding;

public class GalleryFragment extends Fragment {
    private FragmentGalleryBinding binding;
    private ActivityResultLauncher<Intent> mapActivityResultLauncher;


    private FusedLocationProviderClient fusedLocationClient;
    private ActivityResultLauncher<String> locationPermissionRequest;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.btnEnregistrer.setEnabled(false);
        binding.btnEnregistrer.setAlpha(0.5f);  // Make the button appear disabled (low opacity)

        // Get the IP from SharedPreferences in GalleryFragment (but we are removing it on DashboardFragment)
        SharedPreferences prefs = requireActivity().getSharedPreferences("APP_PREFS", Context.MODE_PRIVATE);
        String serverIp = prefs.getString("server_ip", "");

        // Check if the server IP is saved
        if (!serverIp.isEmpty()) {
            // Enable the button if the IP is configured
            binding.btnEnregistrer.setEnabled(true);
            binding.btnEnregistrer.setAlpha(1f);  // Make it fully visible
        } else {
            // Show a toast if the IP is not configured
            Toast.makeText(getActivity(), "Configurez l'adresse IP dans le menu Dashboard", Toast.LENGTH_LONG).show();
        }

        binding.btnRetour.setOnClickListener(v -> {
            NavController navController = Navigation.findNavController(v);
            navController.navigate(R.id.nav_home);
    });
        binding.btnMap.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MapFragment.class);
            intent.putExtra("mode", "current");
            mapActivityResultLauncher.launch(intent);
        });


        mapActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Intent data = result.getData();
                        double latitude = data.getDoubleExtra("latitude", 0);
                        double longitude = data.getDoubleExtra("longitude", 0);
                        binding.edTxtLat.setText(String.valueOf(latitude));
                        binding.edTxtLong.setText(String.valueOf(longitude));
                    }
                }
        );
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        locationPermissionRequest = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        getLastKnownLocation();
                    } else {
                        Toast.makeText(getActivity(), "Permission de localisation refusée", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        binding.btnEnregistrer.setOnClickListener(v -> {
            String pseudo = binding.edTxtPseudo.getText().toString().trim();
            String numero = binding.edTxtNum.getText().toString().trim();
            String longitude = binding.edTxtLong.getText().toString().trim();
            String latitude = binding.edTxtLat.getText().toString().trim();
            Log.e("Enregistrer", "Pseudo: " + pseudo + ", Numero: " + numero + ", Longitude: " + longitude + ", Latitude: " + latitude);

            if (pseudo.isEmpty() || numero.isEmpty() || longitude.isEmpty() || latitude.isEmpty()) {
                Toast.makeText(getActivity(), "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Envoyer les données
            envoyerPosition(pseudo, numero, longitude, latitude);
        });
        getLastKnownLocation();

        return root;
    }
        private void envoyerPosition(String pseudo, String numero, String longitude, String latitude) {
            new Thread(() -> {
                HashMap<String, String> params = new HashMap<>();
                params.put("pseudo", pseudo);
                params.put("numero", numero);
                params.put("longitude", longitude);
                params.put("latitude", latitude);

                JSONParser jsonParser = new JSONParser();
                String url = Config.getAddPositionUrl(requireContext());
                if (url.isEmpty()) return;
                JSONObject jsonObject = jsonParser.makeHttpRequest(url, "POST", params);


                if (jsonObject != null) {
                    try {
                        String message = jsonObject.getString("message");
                        getActivity().runOnUiThread(() -> Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        private void getLastKnownLocation() {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        binding.edTxtLat.setText(String.valueOf(latitude));
                        binding.edTxtLong.setText(String.valueOf(longitude));
                    } else {
                        Toast.makeText(getActivity(), "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            }
        }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}