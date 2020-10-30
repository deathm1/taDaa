package com.koshurTech.tadaa.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.koshurTech.tadaa.R;
import com.koshurTech.tadaa.loading;

import java.text.BreakIterator;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class toDoList extends Fragment {


    RecyclerView recyclerView;

    private  FirestoreRecyclerAdapter adapter;

    static String currentCollection;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_to_do_list, container, false);
        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();


        SharedPreferences activeList = getActivity().getSharedPreferences("listActive", Context.MODE_PRIVATE);
        if(activeList.getString("activeList", null)!=null){
            currentCollection = activeList.getString("activeList", null);
        }
        else {
            currentCollection = "defaultList";
        }





        recyclerView = (RecyclerView) v.findViewById(R.id.toDoRL);

        Query q = FirebaseFirestore.getInstance()
                .collection("users")
                .document(id)
                .collection("toDoList")
                .document(currentCollection)
                .collection("tasks").orderBy("timeStamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<post> options = new FirestoreRecyclerOptions.Builder<post>().setQuery(q, post.class).build();



        final ImageView imageView = (ImageView) v.findViewById(R.id.imgBG);

         adapter = new FirestoreRecyclerAdapter<post, viewH>(options) {
            @NonNull
            @Override
            public viewH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_layout, parent, false);
                return new viewH(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull viewH holder, int position, @NonNull post model) {
                holder.task.setText(model.getUserTask());
                holder.ts.setText(model.getTimeStamp());
                imageView.setVisibility(View.GONE);
            }
        };


         recyclerView.setHasFixedSize(true);
         recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

         recyclerView.setAdapter(adapter);







        return v;
    }

    private void loadPostsFromDatabase() {

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

    public class viewH extends RecyclerView.ViewHolder {
        private TextView task;
        private TextView ts;
        private CheckBox isDone;

        public viewH(@NonNull View itemView) {
            super(itemView);



            task = itemView.findViewById(R.id.i1);
            ts = itemView.findViewById(R.id.i2);
            isDone = itemView.findViewById(R.id.isDone);

            isDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final loading loading = new loading(getActivity());
                    loading.startLoadingAnimation();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            DocumentReference cr = FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(id)
                                    .collection("toDoList")
                                    .document(currentCollection)
                                    .collection("tasks").document(ts.getText().toString());

                            cr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                                    Date date = new Date();

                                    String dateStart = ts.getText().toString();
                                    String dateStop = formatter.format(date);

                                    Date d1 = null;
                                    Date d2 = null;
                                    try {
                                        d1 = formatter.parse(dateStart);
                                        d2 = formatter.parse(dateStop);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }

                                    long diff = d2.getTime() - d1.getTime();
                                    long diffSeconds = diff / 1000 % 60;
                                    long diffMinutes = diff / (60 * 1000) % 60;
                                    long diffHours = diff / (60 * 60 * 1000);


                                    String taskAge = "Task Age : "+"[ "+ diffSeconds + " Seconds ]     "+ "[ "+ diffMinutes + " Minutes ]     " + "[ "+ diffHours + " Hours ]";

                                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DocumentReference cr = FirebaseFirestore.getInstance()
                                            .collection("users")
                                            .document(id)
                                            .collection("toDoList")
                                            .document(currentCollection)
                                            .collection("tasksDone").document("Task Completed : "+formatter.format(date));

                                    Map<String, Object> payload = new HashMap<String, Object>();
                                    payload.put("userTaskCompleted", task.getText().toString());
                                    payload.put("timeStamp", ts.getText().toString());
                                    payload.put("completionTimeStamp", "Task Completed : "+formatter.format(date));
                                    payload.put("taskAge", taskAge);
                                    cr.set(payload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            isDone.setChecked(false);
                                        }
                                    })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(getActivity().getApplicationContext(), "Failure: "+e.toString(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            });

                            loading.dismissDialog();
                        }
                    },500);
                }
            });
        }
    }
}