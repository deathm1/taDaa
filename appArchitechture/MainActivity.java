package com.koshurTech.tadaa;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBar;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.koshurTech.tadaa.auth.googleSignIn;
import com.koshurTech.tadaa.fragments.myListView;
import com.koshurTech.tadaa.fragments.pageAdapter;
import com.koshurTech.tadaa.fragments.post;
import com.koshurTech.tadaa.fragments.taskCompleted;
import com.koshurTech.tadaa.fragments.toDoList;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;


public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedpreferences;
    BottomAppBar bottomAppBar;


    SharedPreferences listStatus;

    String selectedCollection;


    TabLayout tabLayout;
    TabItem tabItem1;
    TabItem tabItem2;
    ViewPager viewPager;
    boolean b = false;

    CardView sib;

    Button btn;

    private FirestoreRecyclerAdapter adapter;
    RecyclerView recyclerView;

    LinearLayout llT;
    LinearLayout llN;

    RadioButton tasks;
    RadioButton notes;

    ArrayList<String> maintitle = new ArrayList<>();

    String listName;

    SharedPreferences sharedPreferencesToSaveList;




    @Override
    protected void onStart() {
        super.onStart();
        checkTheme();
    }



    //firebase shit
    FloatingActionButton floatingActionButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference documentReference;

    loading l;





    //encryption in next update
    SecretKey secretKey;
    private SecretKey generateKey() {
        try {
            String secret=FirebaseAuth.getInstance().getCurrentUser().getUid();
            byte[] ba = secret.getBytes();
            secretKey = new SecretKeySpec(ba, "AES");
        }
        catch (Exception e){
            Log.e("Security Failure", "Error Obtaining Decrypt Key.");
        }
        return secretKey;
    }
    private byte[] encryptMsg(String message, SecretKey secret){
        try {
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secret);
            byte[] cipherText = cipher.doFinal(message.getBytes("UTF-8"));
            return cipherText;
        }
        catch (Exception e){
            Log.e("Security Failure", "Error Performing Encryption");
        }
        return null;

    }
    private String decrypt(byte[] cipherText, SecretKey secret){
        try {
            /* Decrypt the message, given derived encContentValues and initialization vector. */
            Cipher cipher = null;
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secret);
            String decryptString = new String(cipher.doFinal(cipherText), "UTF-8");
            return decryptString;
        }
        catch (Exception e){
            Log.e("Security Failure", "Error Performing Decryption");
        }

        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final SharedPreferences spx = getSharedPreferences("ppD", MODE_PRIVATE);
        final SharedPreferences.Editor editorspx = spx.edit();

        Toolbar toolbarTop = (Toolbar) findViewById(R.id.mainToolbar);

        final TextView tbtv = (TextView) findViewById(R.id.toolBarText);
        toolbarTop.setTitle("");

        setSupportActionBar(toolbarTop);


        getSupportActionBar().setDisplayShowCustomEnabled(true);



        l = new loading(MainActivity.this);



        final SharedPreferences selectedList = getSharedPreferences("listActive", MODE_PRIVATE);

        final SharedPreferences.Editor editorsl  = selectedList.edit();

        if(selectedList.getString("activeList", null)==null){
            tbtv.setText("defaultList");
            selectedCollection = "defaultList";
        }

        else{
            tbtv.setText(selectedList.getString("activeList", null));
            selectedCollection = selectedList.getString("activeList", null);
        }



        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an alert builder
                final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Add New");
                builder.setIcon(R.drawable.ic_baseline_note_add_24);


                // set the custom layout
                final View customLayout = getLayoutInflater().inflate(R.layout.take_note, null);
                builder.setView(customLayout);

                final TextInputLayout textInputLayout = (TextInputLayout) customLayout.findViewById(R.id.payload);

                llT = (LinearLayout) customLayout.findViewById(R.id.addTask);
                llN = (LinearLayout) customLayout.findViewById(R.id.addNote);

                tasks = (RadioButton) customLayout.findViewById(R.id.radioButton1);
                notes = (RadioButton) customLayout.findViewById(R.id.radioButton2);

                final Button doneTask = (Button) customLayout.findViewById(R.id.doneButton);
                Button setReminder = (Button) customLayout.findViewById(R.id.setReminder);

                final TextInputLayout addNotes = (TextInputLayout) customLayout.findViewById(R.id.addNoteOnly);
                Button doneNotes = (Button) customLayout.findViewById(R.id.doneButtonNotes);

                final AlertDialog dialog = builder.create();

                setReminder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       if(!textInputLayout.getEditText().getText().toString().isEmpty()){
                           Calendar cal = Calendar.getInstance();
                           Intent intent = new Intent(Intent.ACTION_EDIT);
                           intent.setType("vnd.android.cursor.item/event");
                           intent.putExtra("beginTime", cal.getTimeInMillis());
                           intent.putExtra("allDay", false);
                           intent.putExtra("rule", "FREQ=DAILY");
                           intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
                           intent.putExtra("title", textInputLayout.getEditText().getText().toString().trim());
                           startActivity(intent);
                       }
                       else {
                           textInputLayout.setError("Can not be blank.");
                       }
                    }
                });

                doneTask.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = textInputLayout.getEditText().getText().toString().trim();
                        if(!input.isEmpty()){
                            final loading loading = new loading(MainActivity.this);
                            loading.startLoadingAnimation();
                            firebaseFirestore = FirebaseFirestore.getInstance();
                            CollectionReference cr = firebaseFirestore.collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("toDoList");
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date date = new Date();
                            documentReference = cr.document(selectedCollection).collection("tasks").document(formatter.format(date));
                            Map<String, Object> payload = new HashMap<String, Object>();
                            payload.put("userTask", textInputLayout.getEditText().getText().toString().trim());
                            payload.put("timeStamp", formatter.format(date));
                            documentReference.set(payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    textInputLayout.setError(null);
//                                    Toast toast= Toast.makeText(getApplicationContext(),
//                                            "Task Created", Toast.LENGTH_SHORT);
//                                    toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL, 0, 0);
//                                    toast.show();
                                    dialog.dismiss();
                                    loading.dismissDialog();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    loading.dismissDialog();
                                    Toast.makeText(getApplicationContext(), "Failure : "+ e.toString(), Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                        else {
                            textInputLayout.setError("Can not be blank.");
                        }
                    }
                });



                llN.setVisibility(View.GONE);

                tasks.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        notes.setChecked(false);
                        llT.setVisibility(View.VISIBLE);
                        llN.setVisibility(View.GONE);
                    }
                });

                notes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tasks.setChecked(false);
                        llT.setVisibility(View.GONE);
                        llN.setVisibility(View.VISIBLE);
                    }
                });





                doneNotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String input = addNotes.getEditText().getText().toString().trim();
                        if(!input.isEmpty()){
                            final loading loading = new loading(MainActivity.this);
                            loading.startLoadingAnimation();
                            firebaseFirestore = FirebaseFirestore.getInstance();

                            CollectionReference cr = firebaseFirestore.collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("userNotes");
                            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                            Date date = new Date();
                            documentReference = cr.document("allNotes")
                                    .collection("notes")
                                    .document(formatter.format(date));

                            Map<String, Object> payload = new HashMap<String, Object>();
                            payload.put("userNote", addNotes.getEditText().getText().toString().trim());
                            payload.put("timeStamp", formatter.format(date));


                            documentReference.set(payload).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    addNotes.setError(null);
//                                    Toast toast= Toast.makeText(getApplicationContext(),
//                                            "Duly Noted", Toast.LENGTH_SHORT);
//                                    toast.setGravity(Gravity.CENTER| Gravity.CENTER_HORIZONTAL, 0, 0);
//                                    toast.show();

                                    dialog.dismiss();
                                    loading.dismissDialog();


                                }
                            });
                        }
                        else {
                            addNotes.setError("Can not be blank.");
                        }
                    }
                });



                dialog.show();

            }
        });






//        Toolbar toolbarTop = (Toolbar) findViewById(R.id.toolbar_top);
//        TextView mTitle = (TextView) toolbarTop.findViewById(R.id.toolbar_title);
//
//        setSupportActionBar(toolbarTop);
//        // Set title to false AFTER toolbar has been set
//        try
//        {
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//        }
//        catch (NullPointerException e){}



        bottomAppBar = (BottomAppBar) findViewById(R.id.bap);
        bottomAppBar.replaceMenu(R.menu.options_menu);

        bottomAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()){
                    case R.id.item1:
                        enterReveal();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                if(b){
                                    Menu menu;
                                    menu = bottomAppBar.getMenu();
                                    menu.getItem(0).setIcon(R.drawable.ic_baseline_nights_stay_24);
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    saveMode(true);
                                    b=false;
                                    finish();
                                }
                                else {
                                    Menu menu;
                                    menu = bottomAppBar.getMenu();
                                    menu.getItem(0).setIcon(R.drawable.ic_baseline_nights_stay_24g);
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    saveMode(false);
                                    b=true;
                                    finish();
                                }
                            }
                        },500);

                        return true;
                }
                return true;
            }
        });

        bottomAppBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                btmNav btmNav = new btmNav();
//                btmNav.show(getSupportFragmentManager(), "sdfsdf");

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.bsd);

                View bottm = LayoutInflater.from(getApplicationContext()).inflate(R.layout.sheet_nav, (LinearLayout) findViewById(R.id.bsc));

                sib = (CardView) bottm.findViewById(R.id.signOut);

                final CardView taskCompleted = (CardView) bottm.findViewById(R.id.taskCompleted);
                CardView myList = (CardView) bottm.findViewById(R.id.createSelectList);
                CardView help = (CardView) bottm.findViewById(R.id.feedback);

                CardView about = (CardView) bottm.findViewById(R.id.about);


                about.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(getApplicationContext(), aboutActivity.class));
                    }
                });




                taskCompleted.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Create an alert builder
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("Tasks Completed");
                        builder.setIcon(R.drawable.ic_baseline_done_24);


                        // set the custom layout
                        final View customLayout = getLayoutInflater().inflate(R.layout.list_management, null);
                        builder.setView(customLayout);


                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        });

                        final AlertDialog dialog = builder.create();




                        recyclerView = (RecyclerView) customLayout.findViewById(R.id.viewTaskList);


                        String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        Query q = FirebaseFirestore.getInstance()
                                .collection("users")
                                .document(id)
                                .collection("toDoList")
                                .document(selectedCollection)
                                .collection("tasksDone").orderBy("completionTimeStamp", Query.Direction.DESCENDING);


                        FirestoreRecyclerOptions<taskCompleted> options2 = new FirestoreRecyclerOptions.Builder<com.koshurTech.tadaa.fragments.taskCompleted>()
                                .setQuery(q, taskCompleted.class)
                                .build();


                        adapter = new FirestoreRecyclerAdapter<taskCompleted, viewH2>(options2) {
                            @NonNull
                            @Override
                            public viewH2 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tc_l, parent, false);
                                return new MainActivity.viewH2(view);
                            }

                            @Override
                            protected void onBindViewHolder(@NonNull viewH2 holder, int position, @NonNull com.koshurTech.tadaa.fragments.taskCompleted model) {
                                holder.uTc.setText(model.getUserTaskCompleted());
                                holder.tA.setText(model.getTaskAge());
                                holder.cTs.setText(model.getCompletionTimeStamp());
                            }
                        };

                        adapter.startListening();




                        recyclerView.setHasFixedSize(true);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));

                        recyclerView.setAdapter(adapter);

                        dialog.show();
                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));




                    }
                });


                myList.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setTitle("My Lists");
                        builder.setIcon(R.drawable.ic_baseline_list_24);

                        final View customLayout = getLayoutInflater().inflate(R.layout.my_lists, null);
                        builder.setView(customLayout);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                maintitle.clear();
                            }
                        });
                        builder.setCancelable(false);






                        Button addList = (Button) customLayout.findViewById(R.id.addNewListBtn);

                        final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();





                        final customAdapter customAdapter = new customAdapter(MainActivity.this, maintitle);

                        final ListView list=(ListView) customLayout.findViewById(R.id.allListDisplay);

                        list.setAdapter(customAdapter);



                        if(spx.getString("populate",null)==null){


                            DocumentReference cr = FirebaseFirestore.getInstance()
                                    .collection("users")
                                    .document(id)
                                    .collection("toDoList").document("listStructure");
                            cr.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                    if(value.exists() && value!=null){
                                        try {
                                            String[] fromFirebase = value.getString("structure").trim().split("--");
                                            maintitle.add("defaultList");
                                            maintitle.addAll(Arrays.asList(fromFirebase));
                                            list.setAdapter(customAdapter);

                                        }
                                        catch (Exception e){
                                            String fromFirebase =  value.getString("structure");
                                            maintitle.add("defaultList");
                                            maintitle.add(fromFirebase);
                                            list.setAdapter(customAdapter);
                                        }
                                    }
                                    else {
                                        maintitle.add("defaultList");
                                        list.setAdapter(customAdapter);
                                    }
                                }
                            });



                        }
                        else {
                            try {
                                System.out.println(spx.getString("populate",null));
                                String[] structure = spx.getString("populate",null).trim().split("--");
                                System.out.println(Arrays.toString(structure));
                                maintitle.add("defaultList");
                                maintitle.addAll(Arrays.asList(structure));
                                list.setAdapter(customAdapter);

                            }catch (Exception e){
                                maintitle.add("defaultList");
                                maintitle.add(spx.getString("populate",null));
                                list.setAdapter(customAdapter);
                            }
                        }





                        final AlertDialog alertDialog = builder.create();


                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                TextView tv = view.findViewById(R.id.listName);
                                selectedCollection = tv.getText().toString();
                                editorsl.putString("activeList", selectedCollection);
                                editorsl.apply();

                                tbtv.setText(tv.getText().toString());
                                alertDialog.dismiss();
                                maintitle.clear();
                                list.setAdapter(customAdapter);
                                bottomSheetDialog.dismiss();
                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                finish();
                            }
                        });


                        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                                final loading lx = new loading(MainActivity.this);


                                final TextView tv = view.findViewById(R.id.listName);

                                if(tv.getText().equals("defaultList")){
                                    Toast.makeText(getApplicationContext(), "Default Collection can not be deleted.", Toast.LENGTH_LONG).show();
                                }

                                else {



                                    AlertDialog.Builder builderx = new AlertDialog.Builder(MainActivity.this);
                                    builderx.setCancelable(false);
                                    builderx.setTitle("Delete List Forever");
                                    builderx.setIcon(R.drawable.ic_baseline_delete_forever_24);
                                    builderx.setMessage("Are you sure you want to delete [ "+tv.getText()+" ] ?\nTHIS ACTION IS NOT REVERSIBLE.");



                                    builderx.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    }).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            lx.startLoadingAnimation();


                                            final String toBeDeleted = tv.getText().toString();





                                            final String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                            final CollectionReference crcc = FirebaseFirestore
                                                    .getInstance()
                                                    .collection("users")
                                                    .document(id)
                                                    .collection("toDoList")
                                                    .document(tv.getText().toString()).collection("tasks");

                                            final CollectionReference crcc2 = FirebaseFirestore
                                                    .getInstance()
                                                    .collection("users")
                                                    .document(id)
                                                    .collection("toDoList")
                                                    .document(tv.getText().toString()).collection("tasksDone");

                                            crcc.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                            String docNmae = document.getId();

                                                            DocumentReference dr = crcc.document(docNmae);

                                                            dr.delete();

                                                        }
                                                    } else {

                                                    }
                                                }
                                            });



                                            crcc2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        for (QueryDocumentSnapshot document : task.getResult()) {

                                                            String docNmae = document.getId();

                                                            DocumentReference dr = crcc2.document(docNmae);

                                                            dr.delete();

                                                        }
                                                    } else {

                                                    }
                                                }
                                            });



                                            DocumentReference cr2 = FirebaseFirestore.getInstance()
                                                    .collection("users")
                                                    .document(id)
                                                    .collection("toDoList").document("listStructure");

                                            cr2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                                @Override
                                                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                                    if(value.exists() && value!=null){
                                                        try {
                                                            String[] fromFirebase = value.getString("structure").trim().split("--");

                                                            System.out.println(Arrays.toString(fromFirebase));

                                                            String store="";

                                                            for(int i=0; i<fromFirebase.length; i++){
                                                                if(fromFirebase[i].equals(toBeDeleted)){
                                                                    store = store;
                                                                }
                                                                else {
                                                                    store = store + "--" + fromFirebase[i];
                                                                }
                                                            }

                                                            System.out.println(store);


                                                            String x = charRemoveAt(store,0);
                                                            String finalStr = charRemoveAt(x,0);


                                                            selectedCollection = "defaultList";
                                                            editorsl.putString("activeList", selectedCollection);
                                                            editorsl.apply();

                                                            editorspx.putString("populate", finalStr);
                                                            editorspx.apply();

                                                            DocumentReference updateReq = FirebaseFirestore.getInstance()
                                                                    .collection("users")
                                                                    .document(id)
                                                                    .collection("toDoList").document("listStructure");

                                                            Map<String, Object> payload = new HashMap<String, Object>();
                                                            payload.put("structure", spx.getString("populate",null));
                                                            updateReq.set(payload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    lx.dismissDialog();
                                                                    alertDialog.dismiss();
                                                                    maintitle.clear();
                                                                    bottomSheetDialog.dismiss();
                                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                    finish();

                                                                }
                                                            }).addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(getApplicationContext(), "Failure: "+e.toString(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });





                                                        }
                                                        catch (Exception e){
                                                            String fromFirebase =  value.getString("structure");

                                                            maintitle.add("defaultList");
                                                            list.setAdapter(customAdapter);


                                                            DocumentReference updateReq = FirebaseFirestore.getInstance()
                                                                    .collection("users")
                                                                    .document(id)
                                                                    .collection("toDoList").document("listStructure");


                                                            updateReq.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    selectedCollection = "defaultList";
                                                                    editorsl.putString("activeList", selectedCollection);
                                                                    editorsl.apply();

                                                                    editorspx.putString("populate", null);
                                                                    editorspx.apply();


                                                                    lx.dismissDialog();
                                                                    alertDialog.dismiss();
                                                                    maintitle.clear();
                                                                    bottomSheetDialog.dismiss();
                                                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                                    finish();

                                                                }
                                                            });
                                                        }
                                                    }
                                                    else {

                                                    }



                                                }
                                            });




                                        }
                                    });

                                    AlertDialog alertDialog = builderx.create();
                                    alertDialog.show();

                                    alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.RED);
                                    alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GREEN);
                                }





                                return true;
                            }
                        });


                        addList.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final TextInputLayout textInputLayout = (TextInputLayout) customLayout.findViewById(R.id.listNameAdd);
                                final String text = textInputLayout.getEditText().getText().toString().trim();

                                String regex = "^[a-zA-Z0-9\\s]+$";
                                Pattern pattern = Pattern.compile(regex);
                                Matcher matcher = pattern.matcher(text);

                                if(text.isEmpty()){
                                    textInputLayout.setError("Invalid Input");
                                }
                                else if(!matcher.matches()){
                                    textInputLayout.setError("Invalid Input");
                                }
                                else {

                                    final loading loading = new loading(MainActivity.this);
                                    loading.startLoadingAnimation();
                                    String currentPopulate = spx.getString("populate",null);
                                    if(currentPopulate==null){
                                        currentPopulate = text;
                                        editorspx.putString("populate", currentPopulate);
                                        editorspx.apply();


                                        DocumentReference cr = FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(id)
                                                .collection("toDoList").document("listStructure");

                                        Map<String, Object> payload = new HashMap<String, Object>();
                                        payload.put("structure", spx.getString("populate",null));
                                        cr.set(payload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                maintitle.clear();
                                                list.setAdapter(customAdapter);
                                                selectedCollection = text;
                                                alertDialog.dismiss();
                                                tbtv.setText(text);
                                                loading.dismissDialog();
                                                editorsl.putString("activeList", text);
                                                editorsl.apply();
                                                bottomSheetDialog.dismiss();
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }
                                        });
                                    }
                                    else {
                                        currentPopulate =spx.getString("populate",null) + "--" + text;
                                        editorspx.putString("populate", currentPopulate);
                                        editorspx.apply();


                                        DocumentReference cr = FirebaseFirestore.getInstance()
                                                .collection("users")
                                                .document(id)
                                                .collection("toDoList").document("listStructure");

                                        Map<String, Object> payload = new HashMap<String, Object>();
                                        payload.put("structure", spx.getString("populate",null));
                                        cr.set(payload).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                                maintitle.clear();
                                                list.setAdapter(customAdapter);
                                                selectedCollection = text;
                                                alertDialog.dismiss();
                                                tbtv.setText(text);

                                                editorsl.putString("activeList", text);


                                                loading.dismissDialog();
                                                editorsl.apply();
                                                bottomSheetDialog.dismiss();





                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            }
                                        });

                                    }
                                }

                            }
                        });



                        alertDialog.show();

                        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));

                    }
                });


                help.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        
                    }
                });

                TextView userName = (TextView) bottm.findViewById(R.id.userName);
                TextView userEmail = (TextView) bottm.findViewById(R.id.userEmail);
                ImageView userPP = (ImageView) bottm.findViewById(R.id.userPP);


                userName.setText(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                userEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                String ppic = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();

                Picasso.get().load(ppic).into(userPP);

                bottomSheetDialog.setContentView(bottm);
                bottomSheetDialog.setCanceledOnTouchOutside(true);
                bottomSheetDialog.show();

                sib.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(getApplicationContext(), googleSignIn.class));
                        bottomSheetDialog.dismiss();
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }
                });

            }
        });







        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabItem1 = (TabItem) findViewById(R.id.toDoList1) ;
        tabItem2 = (TabItem) findViewById(R.id.notes) ;
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        final pageAdapter pageAdapter = new pageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());

        viewPager.setAdapter(pageAdapter);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if(tab.getPosition()==0 || tab.getPosition()==1){
                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


    }


    private void saveMode(boolean b) {
        sharedpreferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        editor.putBoolean("theme", b);
        editor.apply();
    }



    public static String charRemoveAt(String str, int p) {
        return str.substring(0, p) + str.substring(p + 1);
    }


    private void checkTheme() {
        SharedPreferences sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        if(sp.getBoolean("theme", false)){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            b=false;

            Menu menu;
            menu = bottomAppBar.getMenu();
            menu.getItem(0).setIcon(R.drawable.ic_baseline_nights_stay_24g);
        }
        else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            b=true;


            Menu menu;
            menu = bottomAppBar.getMenu();
            menu.getItem(0).setIcon(R.drawable.ic_baseline_nights_stay_24);
        }
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void enterReveal() {
        // previously invisible view
        final View myView = findViewById(R.id.my_view);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the final radius for the clipping circle
        int finalRadius = Math.max(myView.getWidth(), myView.getHeight()) / 2;

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void exitReveal() {
        // previously visible view
        final View myView = findViewById(R.id.my_view);

        // get the center for the clipping circle
        int cx = myView.getMeasuredWidth() / 2;
        int cy = myView.getMeasuredHeight() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = myView.getWidth() / 2;

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();
    }


    private class viewH2 extends RecyclerView.ViewHolder {

        private TextView cTs;
        private TextView tA;
        private TextView uTc;
        private ImageButton delete;




        public viewH2(@NonNull View itemView) {
            super(itemView);
            cTs = itemView.findViewById(R.id.ii2);
            tA = itemView.findViewById(R.id.ii3);
            uTc = itemView.findViewById(R.id.ii1);

            uTc.setPaintFlags(uTc.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            delete = itemView.findViewById(R.id.deleteButton);


            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String id = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DocumentReference cr = FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(id)
                            .collection("toDoList")
                            .document(selectedCollection)
                            .collection("tasksDone").document(cTs.getText().toString());

                    cr.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.v("Status","Deleted");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("Failure","Deletion Failed");
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }

        return super.onKeyDown(keyCode, event);

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}