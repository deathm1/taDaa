package com.koshurTech.tadaa.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.koshurTech.tadaa.R;
import com.koshurTech.tadaa.loading;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class notes extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    RecyclerView recyclerView;
    private FirestoreRecyclerAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_notes, container, false);


        recyclerView = (RecyclerView) v.findViewById(R.id.viewNotes);

        final ImageView imageView = (ImageView) v.findViewById(R.id.noteBG);

        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Query q = FirebaseFirestore.getInstance()
                .collection("users")
                .document(id)
                .collection("userNotes")
                .document("allNotes")
                .collection("notes").orderBy("timeStamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<displayNote> options = new FirestoreRecyclerOptions.Builder<displayNote>().setQuery(q, displayNote.class).build();


        adapter = new FirestoreRecyclerAdapter<displayNote, viewNote>(options){

            @NonNull
            @Override
            public viewNote onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_layout, parent, false);
                return new notes.viewNote(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull viewNote holder, int position, @NonNull displayNote model) {
                holder.note.setText(model.getUserNote());
                holder.ts.setText(model.getTimeStamp());
                imageView.setVisibility(View.GONE);
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        recyclerView.setAdapter(adapter);



        return v;
    }

    @Override
    public void onStop() {
        super.onStop();

        adapter.stopListening();
    }


    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }



    private class viewNote extends RecyclerView.ViewHolder{


        private TextView note;
        private TextView ts;

        private CardView noteItem;



        public viewNote(@NonNull View itemView) {
            super(itemView);


            note = itemView.findViewById(R.id.currentNote);
            ts = itemView.findViewById(R.id.noteTS);

            noteItem = itemView.findViewById(R.id.noteItem);

            noteItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setCancelable(true);
                    builder.setIcon(R.drawable.ic_baseline_delete_forever_24);
                    builder.setMessage("Delete [ "+ note.getText()+" ] note forever ?");
                    builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            final loading loading = new loading(getActivity());
                            loading.startLoadingAnimation();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                    DocumentReference cr = FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(id)
                                            .collection("userNotes")
                                            .document("allNotes")
                                            .collection("notes").document(ts.getText().toString());

                                    cr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                        }
                                    });

                                    loading.dismissDialog();

                                }
                            },300);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();

                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);


                }
            });
        }
    }
}