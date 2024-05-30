package com.hb.ecocollectte;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class tablefragment extends Fragment {
    Button buttonScan;

    private Spinner wasteTypeSpinner;
    private EditText wasteLocationEditText, wasteDateEditText, wasteSizeEditText;

    DatabaseReference wasteDatabaseReference;
    private String uniqueId;

    private static final int MAP_REQUEST_CODE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tablefragment, container, false);


        wasteDatabaseReference = FirebaseDatabase.getInstance().getReference("waste_information");
        uniqueId = wasteDatabaseReference.push().getKey();


        wasteTypeSpinner = view.findViewById(R.id.type_spinner);
        wasteLocationEditText = view.findViewById(R.id.location);
        wasteDateEditText = view.findViewById(R.id.date);
        wasteSizeEditText = view.findViewById(R.id.size);
        ImageView imageViewLoc = view.findViewById(R.id.imageViewLoc);
        Button saveButton = view.findViewById(R.id.save);
        buttonScan = view.findViewById(R.id.button_scan); // Initialize the scan button


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.types_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        wasteTypeSpinner.setAdapter(adapter);


        imageViewLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMap();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveDataToFirebase();
            }
        });


        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), scan.class);
                startActivity(intent);
            }
        });

        return view;
    }


    private void openMap() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("geo:0,0?q="));

        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(intent, MAP_REQUEST_CODE);
        } else {
            Toast.makeText(getActivity(), "No app found to handle this action", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAP_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri locationUri = data.getData();
                String selectedLocation = locationUri.toString();
                wasteLocationEditText.setText(selectedLocation);
            }
        }
    }

    private void saveDataToFirebase() {
        String type = wasteTypeSpinner.getSelectedItem().toString();
        String location = wasteLocationEditText.getText().toString().trim();
        String date = wasteDateEditText.getText().toString().trim();
        String size = wasteSizeEditText.getText().toString().trim();

        if (type.isEmpty() || location.isEmpty() || date.isEmpty() || size.isEmpty()) {
            Toast.makeText(getActivity(), "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String id = uniqueId;

        WasteInformation wasteInformation = new WasteInformation(id, type, location, date, size);

        wasteDatabaseReference.child(id).setValue(wasteInformation)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Data saved successfully", Toast.LENGTH_SHORT).show();


                            Bundle bundle = new Bundle();
                            bundle.putString("type", type);
                            bundle.putString("location", location);
                            bundle.putString("date", date);

                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            homeFragment fragment = new homeFragment();
                            fragment.setArguments(bundle);
                            transaction.addToBackStack(null);
                            transaction.commit();
                        } else {
                            Toast.makeText(getActivity(), "Failed to save data", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public static class WasteInformation {
        private String id;
        private String type;
        private String location;
        private String date;
        private String size;

        public WasteInformation() {}

        public WasteInformation(String id, String type, String location, String date, String size) {
            this.id = id;
            this.type = type;
            this.location = location;
            this.date = date;
            this.size = size;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLocation() {
            return location;
        }

        public void setLocation(String location) {
            this.location = location;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }
    }
}