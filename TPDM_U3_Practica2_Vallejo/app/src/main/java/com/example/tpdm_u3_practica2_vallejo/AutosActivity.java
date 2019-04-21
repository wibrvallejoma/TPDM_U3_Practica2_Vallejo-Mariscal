package com.example.tpdm_u3_practica2_vallejo;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutosActivity extends AppCompatActivity {
     Button agregarMarca, eliminarMarca,
        agregarModelo, eliminarModelo, buscarModelo;
     EditText nombreModelo, fechaModelo;
     CheckBox disponibleModelo;
     Spinner spinnerMarcas;
    ListView listViewModelos;
    FirebaseFirestore servicioBaseDatos;
    //List todoslosModelos;
    List<Modelo> modelos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autos);


        agregarMarca = findViewById(R.id.insertarMarcaAuto);
        eliminarMarca = findViewById(R.id.eliminarMarcaAuto);
        agregarModelo = findViewById(R.id.insertarModeloAuto);
        eliminarModelo = findViewById(R.id.eliminarModeloAuto);
        buscarModelo = findViewById(R.id.btnBuscarModelo);

        nombreModelo = findViewById(R.id.nombreModeloAuto);
        fechaModelo = findViewById(R.id.fechaModeloAuto);
        disponibleModelo = findViewById(R.id.disponibleModeloAuto);

        spinnerMarcas = findViewById(R.id.marcasAuto);

        listViewModelos = findViewById(R.id.listaModelos);

        servicioBaseDatos = FirebaseFirestore.getInstance();

        consultarMarcas();

        //consultarModelos();

        spinnerMarcas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nombreModelo.setText(""); fechaModelo.setText(""); disponibleModelo.setChecked(false);
                consultarModelos();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        agregarMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertarMarca();
            }
        });

        eliminarMarca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionEliminarMarca();
            }
        });

        agregarModelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionAgregarModelo();
            }
        });

        eliminarModelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                solicitarEliminarModelo();
            }
        });

        buscarModelo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                funcionBuscarModelo();
            }
        });

        listViewModelos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                nombreModelo.setText(modelos.get(position).Nombre);
                fechaModelo.setText(modelos.get(position).Fecha);
                disponibleModelo.setChecked(modelos.get(position).Disponible);
            }
        });
    }

    private void funcionBuscarModelo() {
        final EditText modeloAbuscar = new EditText(this);
        AlertDialog.Builder alerta = new AlertDialog.Builder(this);
        alerta.setTitle("BUSQUEDA")
                .setMessage("MODELO A BUSCAR")
                .setView(modeloAbuscar)
                .setPositiveButton("BUSCAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int i = 0; i<modelos.size(); i++){
                            if(modelos.get(i).Nombre.equals(modeloAbuscar.getText().toString())){
                                nombreModelo.setText(modelos.get(i).Nombre);
                                fechaModelo.setText(modelos.get(i).Fecha);
                                disponibleModelo.setChecked(modelos.get(i).Disponible);
                                return;
                            }
                        }
                        mensaje("NO SE ENCONTRO MODELO");
                    }
                })
                .setNegativeButton("CANCELAR", null)
                .show();

    }

    private void solicitarEliminarModelo() {
        final EditText modeloAEliminar = new EditText(this);
        modeloAEliminar.setHint("Modelo a eliminar");
        AlertDialog.Builder alertaModelo = new AlertDialog.Builder(AutosActivity.this);
        alertaModelo.setTitle("Eliminar Modelo")
                .setView(modeloAEliminar)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder confirmacion = new AlertDialog.Builder(AutosActivity.this);
                        confirmacion.setTitle("ESTAS SEGURO DE ELIMINAR A "+modeloAEliminar.getText().toString())
                                .setPositiveButton("SEGURISIMO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        servicioBaseDatos.collection("Autos")
                                                .document(spinnerMarcas.getSelectedItem().toString())
                                                .collection("Modelos")
                                                .document(modeloAEliminar.getText().toString())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mensaje(modeloAEliminar.getText().toString() + " ELIMINADO CORRECTAMENTE");
                                                        consultarModelos();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AutosActivity.this,"ERROR AL ELIMINAR MODELO", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("NO", null)
                                .show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void funcionAgregarModelo() {
        if (nombreModelo.getText().toString().equals("") || nombreModelo.getText().toString().equals(null)) {
            mensaje("INGRESA MODELO VALIDO");
            return;
        }
        Modelo nuevoModelo = new Modelo(nombreModelo.getText().toString(), fechaModelo.getText().toString(), disponibleModelo.isChecked());
        //Map<String,Object> myMap = new HashMap<>();
        servicioBaseDatos.collection("Autos")
                .document(spinnerMarcas.getSelectedItem().toString())
                .collection("Modelos")
                .document(nuevoModelo.Nombre)
                .set(nuevoModelo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        mensaje("MODELO AGREGADO EXITOSAMENTE");
                        consultarModelos();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mensaje("ERROR AL AGREGAR MODELO");
                    }
                });
        nombreModelo.setText(""); fechaModelo.setText(""); disponibleModelo.setChecked(false);
    }

    private void funcionEliminarMarca() {
        final EditText marcaAEliminar = new EditText(this);
        marcaAEliminar.setHint("Marca a eliminar");
        AlertDialog.Builder alertaMarca = new AlertDialog.Builder(AutosActivity.this);
        alertaMarca.setTitle("Eliminar Marca")
                .setView(marcaAEliminar)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder confirmacion = new AlertDialog.Builder(AutosActivity.this);
                        confirmacion.setTitle("ESTAS SEGURO DE ELIMINAR A "+marcaAEliminar.getText().toString())
                                .setPositiveButton("SEGURISIMO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        servicioBaseDatos.collection("Autos")
                                                .document(marcaAEliminar.getText().toString())
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        mensaje(marcaAEliminar.getText().toString() + " ELIMINADO CORRECTAMENTE");
                                                        consultarMarcas();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(AutosActivity.this,"ERROR AL ELIMINAR MARCA", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                })
                                .setNegativeButton("NO", null)
                                .show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();

    }

    private void mensaje(String men){
        Toast.makeText(AutosActivity.this,men, Toast.LENGTH_SHORT).show();
    }


    private void insertarMarca() {

        final EditText nombreMarcaNueva = new EditText(this);
        nombreMarcaNueva.setHint("Nombre nueva marca");
        AlertDialog.Builder alertaMarca = new AlertDialog.Builder(AutosActivity.this);
        alertaMarca.setTitle("Nueva Marca")
                .setView(nombreMarcaNueva)
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,Object> myMap = new HashMap<>();
                        myMap.put("Modelos", nombreMarcaNueva.getText().toString());
                        servicioBaseDatos.collection("Autos")
                                .document(nombreMarcaNueva.getText().toString())
                                .set(myMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(AutosActivity.this,"MARCA AGREGADA EXITOSAMENTE", Toast.LENGTH_SHORT).show();
                                        consultarMarcas();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(AutosActivity.this,"ERROR AL AGREGAR MARCA", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
        consultarMarcas();
    }

    private void consultarModelos() {

        servicioBaseDatos.collection("Autos")
                .document(spinnerMarcas.getSelectedItem().toString())
                .collection("Modelos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            //todoslosModelos = new ArrayList<>();
                            modelos = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                Map<String, Object> datos = document.getData();
                                modelos.add(new Modelo(document.getId(), datos.get("Fecha").toString(), (boolean)datos.get("Disponible")));


                                //todoslosModelos.add(document.getId());
                                //Toast.makeText(AutosActivity.this,document.getId(), Toast.LENGTH_SHORT).show();
                            }
                            String [] modelosArray = new String[modelos.size()];
                            for(int i = 0; i<modelos.size(); i++){
                                modelosArray[i] = modelos.get(i).Nombre;
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AutosActivity.this,
                                    android.R.layout.simple_list_item_1, modelosArray);
                            listViewModelos.setAdapter(adapter);
                        }else {
                            Toast.makeText(AutosActivity.this,"No hay modelos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void consultarMarcas() {
        servicioBaseDatos.collection("Autos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> marcas = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                marcas.add(document.getId());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(AutosActivity.this,
                                    android.R.layout.simple_list_item_1, marcas);
                            spinnerMarcas.setAdapter(adapter);


                        }else {
                            Toast.makeText(AutosActivity.this,"Error obteniendo marcas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
