package com.hb.ecocollectte;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private WasteInformationAdapter adapter;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        viewPager = view.findViewById(R.id.viewPager);
        tabLayout = view.findViewById(R.id.tabLayout);

        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getActivity());
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {

            if (position == 0) {
                tab.setText("Tab 1");
            } else if (position == 1) {
                tab.setText("Tab 2");
            }
        }).attach();


        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        databaseReference = FirebaseDatabase.getInstance().getReference().child("waste_information");
        adapter = new WasteInformationAdapter(databaseReference);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<WasteInformation> wasteInformationList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    WasteInformation wasteInformation = snapshot.getValue(WasteInformation.class);
                    if (wasteInformation != null) {
                        wasteInformation.setId(snapshot.getKey());
                        wasteInformationList.add(wasteInformation);
                    }
                }
                adapter.setWasteInformationList(wasteInformationList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "Failed to read data from Firebase", Toast.LENGTH_SHORT).show();
            }
        });

        ImageView profileImage = view.findViewById(R.id.profile_image);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), profile.class);
                startActivity(intent);
            }
        });

        return view;
    }


    public static class WasteInformationAdapter extends RecyclerView.Adapter<WasteInformationAdapter.ViewHolder> {
        private List<WasteInformation> wasteInformationList;
        private DatabaseReference databaseReference;

        public WasteInformationAdapter(DatabaseReference databaseReference) {
            this.databaseReference = databaseReference;
        }

        public void setWasteInformationList(List<WasteInformation> wasteInformationList) {
            this.wasteInformationList = wasteInformationList;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.waste_information_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            WasteInformation wasteInformation = wasteInformationList.get(position);
            holder.bind(wasteInformation);

            // Set background color based on type
            String type = wasteInformation.getType();
            Context context = holder.itemView.getContext();
            int color;

            switch (type.toLowerCase()) {
                case "bois":
                    color = ContextCompat.getColor(context, R.color.brown); // Brown
                    break;
                case "carton":
                    color = ContextCompat.getColor(context, R.color.beige);
                    break;
                case "fer":
                    color = ContextCompat.getColor(context, R.color.gray);
                    break;
                case "plastique":
                    color = ContextCompat.getColor(context, R.color.light_blue);
                    break;
                default:
                    color = ContextCompat.getColor(context, R.color.white);
                    break;
            }


            GradientDrawable background = new GradientDrawable();
            background.setColor(color);
            background.setCornerRadius(80);
            holder.itemContainer.setBackground(background);


            holder.buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String id = wasteInformation.getId();
                    databaseReference.child(id).removeValue();
                    wasteInformationList.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return wasteInformationList != null ? wasteInformationList.size() : 0;
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            private TextView textType, textLocation, textSize, textDate;
            private Button buttonDelete;
            private LinearLayout itemContainer;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                textType = itemView.findViewById(R.id.text_type);
                textLocation = itemView.findViewById(R.id.text_location);
                textSize = itemView.findViewById(R.id.text_size);
                textDate = itemView.findViewById(R.id.text_date);
                buttonDelete = itemView.findViewById(R.id.button_delete);
                itemContainer = itemView.findViewById(R.id.item_container);
            }

            public void bind(WasteInformation wasteInformation) {
                textType.setText(wasteInformation.getType());
                textLocation.setText(wasteInformation.getLocation());
                textSize.setText(wasteInformation.getSize());
                textDate.setText(wasteInformation.getDate());
            }
        }
    }


    public static class WasteInformation {
        private String id;
        private String type;
        private String location;
        private String size;
        private String date;

        public WasteInformation() {

        }

        public WasteInformation(String type, String location, String size, String date) {
            this.type = type;
            this.location = location;
            this.size = size;
            this.date = date;
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

        public String getSize() {
            return size;
        }

        public void setSize(String size) {
            this.size = size;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}


