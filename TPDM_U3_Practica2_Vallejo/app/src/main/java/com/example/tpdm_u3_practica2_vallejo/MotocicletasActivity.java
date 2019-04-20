package com.example.tpdm_u3_practica2_vallejo;

import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MotocicletasActivity extends AppCompatActivity {
    Button agregarMarca, eliminarMarca,
            agregarModelo, eliminarModelo, buscarModelo;
    EditText modelo;
    Spinner spinnerMarcas;
    ListView listViewModelos;
    FirebaseFirestore servicioBaseDatos;
    List todoslosModelos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_motocicletas);

        agregarMarca = findViewById(R.id.insertarMarcaMoto);
        eliminarMarca = findViewById(R.id.eliminarMarcaMoto);
        agregarModelo = findViewById(R.id.insertarModeloMoto);
        eliminarModelo = findViewById(R.id.eliminarModeloMoto);
        buscarModelo = findViewById(R.id.btnBuscarModeloMoto);

        modelo = findViewById(R.id.editTextModeloMoto);

        spinnerMarcas = findViewById(R.id.marcasMoto);

        listViewModelos = findViewById(R.id.listaModelosMoto);

        servicioBaseDatos = FirebaseFirestore.getInstance();

        consultarMarcas();

        //consultarModelos();

        spinnerMarcas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
                modelo.setText(todoslosModelos.get(position).toString());
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
                        for (int i = 0; i<todoslosModelos.size(); i++){
                            if(todoslosModelos.get(i).equals(modeloAbuscar.getText().toString())){
                                modelo.setText(todoslosModelos.get(i).toString());
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
        AlertDialog.Builder alertaModelo = new AlertDialog.Builder(MotocicletasActivity.this);
        alertaModelo.setTitle("Eliminar Modelo")
                .setView(modeloAEliminar)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder confirmacion = new AlertDialog.Builder(MotocicletasActivity.this);
                        confirmacion.setTitle("ESTAS SEGURO DE ELIMINAR A "+modeloAEliminar.getText().toString())
                                .setPositiveButton("SEGURISIMO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        servicioBaseDatos.collection("Motocicletas")
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
                                                        Toast.makeText(MotocicletasActivity.this,"ERROR AL ELIMINAR MODELO", Toast.LENGTH_SHORT).show();
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

        Map<String,Object> myMap = new HashMap<>();
        servicioBaseDatos.collection("Motocicletas")
                .document(spinnerMarcas.getSelectedItem().toString())
                .collection("Modelos")
                .document(modelo.getText().toString())
                .set(myMap)
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
    }

    private void funcionEliminarMarca() {
        final EditText marcaAEliminar = new EditText(this);
        marcaAEliminar.setHint("Marca a eliminar");
        AlertDialog.Builder alertaMarca = new AlertDialog.Builder(MotocicletasActivity.this);
        alertaMarca.setTitle("Eliminar Marca")
                .setView(marcaAEliminar)
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder confirmacion = new AlertDialog.Builder(MotocicletasActivity.this);
                        confirmacion.setTitle("ESTAS SEGURO DE ELIMINAR A "+marcaAEliminar.getText().toString())
                                .setPositiveButton("SEGURISIMO", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        servicioBaseDatos.collection("Motocicletas")
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
                                                        Toast.makeText(MotocicletasActivity.this,"ERROR AL ELIMINAR MARCA", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(MotocicletasActivity.this,men, Toast.LENGTH_SHORT).show();
    }


    private void insertarMarca() {

        final EditText nombreMarcaNueva = new EditText(this);
        nombreMarcaNueva.setHint("Nombre nueva marca");
        AlertDialog.Builder alertaMarca = new AlertDialog.Builder(MotocicletasActivity.this);
        alertaMarca.setTitle("Nueva Marca")
                .setView(nombreMarcaNueva)
                .setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String,Object> myMap = new HashMap<>();
                        myMap.put("Modelos", nombreMarcaNueva.getText().toString());
                        servicioBaseDatos.collection("Motocicletas")
                                .document(nombreMarcaNueva.getText().toString())
                                .set(myMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(MotocicletasActivity.this,"MARCA AGREGADA EXITOSAMENTE", Toast.LENGTH_SHORT).show();
                                        consultarMarcas();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MotocicletasActivity.this,"ERROR AL AGREGAR MARCA", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
        consultarMarcas();
    }

    private void consultarModelos() {

        servicioBaseDatos.collection("Motocicletas")
                .document(spinnerMarcas.getSelectedItem().toString())
                .collection("Modelos")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            todoslosModelos = new ArrayList<>();
                            for(QueryDocumentSnapshot document: task.getResult()){
                                todoslosModelos.add(document.getId());
                                //Toast.makeText(MotocicletasActivity.this,document.getId(), Toast.LENGTH_SHORT).show();
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MotocicletasActivity.this,
                                    android.R.layout.simple_list_item_1, todoslosModelos);
                            listViewModelos.setAdapter(adapter);
                        }else {
                            Toast.makeText(MotocicletasActivity.this,"No hay modelos", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void consultarMarcas() {
        servicioBaseDatos.collection("Motocicletas")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            List<String> marcas = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                marcas.add(document.getId());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(MotocicletasActivity.this,
                                    android.R.layout.simple_list_item_1, marcas);
                            spinnerMarcas.setAdapter(adapter);


                        }else {
                            Toast.makeText(MotocicletasActivity.this,"Error obteniendo marcas", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
