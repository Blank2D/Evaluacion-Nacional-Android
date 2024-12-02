package com.example.appmqtt;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ViewAnimator;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseActivity extends AppCompatActivity {

    private EditText txtPatente, txtModelo, txtDueño, txtPrecio;
    private ListView lista;
    private Spinner spMarca;
    private FirebaseFirestore db;
    String[] marcasAutos = {"Honda","Toyota","Audi","Mazda","Chevrolet"};
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase);

        CargarListaFireStore();

        db = FirebaseFirestore.getInstance();

        txtPatente = findViewById(R.id.txtPatente);
        txtModelo = findViewById(R.id.txtModelo);
        txtDueño = findViewById(R.id.txtDueño);
        txtPrecio = findViewById(R.id.txtPrecio);
        spMarca = findViewById(R.id.spMarca);
        lista = findViewById(R.id.lista);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, marcasAutos);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMarca.setAdapter(adapter);


    }
    public void CargarListaFireStore(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("autos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()){
                            List<String> listaAutos = new ArrayList<>();
                            for (QueryDocumentSnapshot document: task.getResult()){
                                String linea = "|| "+document.getString("patente") + "|| "+
                                        document.getString("modelo")+"|| "+
                                        document.getString("Marca")+"|| "+
                                        document.getString("dueño")+"|| "+
                                        document.getString("precio");
                                listaAutos.add(linea);
                            }
                            ArrayAdapter<String> adaptor = new ArrayAdapter<>(
                                    FirebaseActivity.this,
                                    android.R.layout.simple_list_item_1,
                                    listaAutos
                            );
                            lista.setAdapter(adaptor);
                        }else{
                            Log.e("TAG","Error al obtener datos firestore", task.getException());
                        }
                    }
                });
    }

    public void CargarLista(View view){
        CargarListaFireStore();
    }


    public void enviarDatosFireStore(View view){
        String patente = txtPatente.getText().toString();
        String modelo = txtModelo.getText().toString();
        String dueño = txtDueño.getText().toString();
        String precio = txtPrecio.getText().toString();
        String Marca = spMarca.getSelectedItem().toString();

        Map<String, Object> mascota = new HashMap<>();
        mascota.put("patente", patente);
        mascota.put("modelo", modelo);
        mascota.put("dueño", dueño);
        mascota.put("precio", precio);
        mascota.put("Marca", Marca);

        db.collection("autos")
                .document(patente)
                .set(mascota)
                .addOnSuccessListener(aVoid ->{
                    Toast.makeText(FirebaseActivity.this, "Datos enviados a FireStore Correctamente", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e ->{
                    Toast.makeText(FirebaseActivity.this, "Error al Enviar los Datos a Firestore" + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }
    public void onClickMqtt(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
