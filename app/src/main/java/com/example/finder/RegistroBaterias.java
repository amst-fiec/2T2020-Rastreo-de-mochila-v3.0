package com.example.finder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistroBaterias extends AppCompatActivity {

    public BarChart graficoBarras;
    private RequestQueue ListaRequest = null;
    private String token = "eyJ0eXAi...........................-mMIArvMc";
    LinearLayout contenedorBaterias;
    private Map<String, TextView> bateriasTVs;
    private Map<String, TextView> fechasTVs;
    private RegistroBaterias contexto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_baterias);

        setTitle("Grafico de barras");
        bateriasTVs = new HashMap<String,TextView>();
        fechasTVs = new HashMap<String,TextView>();
        ListaRequest = Volley.newRequestQueue(this);
        contexto= this;
        /* GRAFICO */
        this.iniciarGrafico();
        this.solicitarBaterias();
    }

    public void iniciarGrafico() {
        graficoBarras = findViewById(R.id.barChart);
        graficoBarras.getDescription().setEnabled(false);
        graficoBarras.setMaxVisibleValueCount(60);
        graficoBarras.setPinchZoom(false);
        graficoBarras.setDrawBarShadow(false);
        graficoBarras.setDrawGridBackground(false);
        XAxis xAxis = graficoBarras.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        graficoBarras.getAxisLeft().setDrawGridLines(false);
        graficoBarras.animateY(1500);
        graficoBarras.getLegend().setEnabled(false);
    }

    public void solicitarBaterias(){
        String url_registros = "https://finder-fa909.firebaseio.com/Dispositivos.json";
        JsonArrayRequest requestRegistros =
                new JsonArrayRequest(Request.Method.GET,
                        url_registros, null, new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        mostrarBaterias(response);
                        actualizarGrafico(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    } }
                ){  @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "JWT " + token);
                    return params;
                } };
        ListaRequest.add(requestRegistros);
    }

    private void mostrarBaterias(JSONArray baterias){
        String registroId;
        JSONObject registroBat;
        LinearLayout nuevoRegistro;
        TextView fechaRegistro;
        TextView valorRegistro;
        contenedorBaterias = findViewById(R.id.cont_baterias);
        LinearLayout.LayoutParams parametrosLayout = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        try
        {
            for (int i = 0; i < baterias.length(); i++) {
                registroBat = (JSONObject) baterias.get(i);
                registroId = registroBat.getString("id");
                    if (bateriasTVs.containsKey(registroId) && fechasTVs.containsKey(registroId)) {
                        fechaRegistro = fechasTVs.get(registroId);
                        valorRegistro = bateriasTVs.get(registroId);
                        fechaRegistro.setText(registroBat.getString("fecha"));
                        valorRegistro.setText(registroBat.getString("bateria") + " %");
                    } else {
                        nuevoRegistro = new LinearLayout(this);
                        nuevoRegistro.setOrientation(LinearLayout.HORIZONTAL);
                        fechaRegistro = new TextView(this);
                        fechaRegistro.setLayoutParams(parametrosLayout);
                        fechaRegistro.setText(registroBat.getString("fecha"));
                        nuevoRegistro.addView(fechaRegistro);
                        valorRegistro = new TextView(this);
                        valorRegistro.setLayoutParams(parametrosLayout);
                        valorRegistro.setText(registroBat.getString("bateria") + " %");
                        nuevoRegistro.addView(valorRegistro);
                        contenedorBaterias.addView(nuevoRegistro);
                        fechasTVs.put(registroId, fechaRegistro);
                        bateriasTVs.put(registroId, valorRegistro);
                    }
            }

        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("error");
        }
    }

    private void actualizarGrafico(JSONArray baterias){
        JSONObject registro_baterias;
        String bat;
        String date;
        int count = 0;
        float bat_val;
        ArrayList<BarEntry> dato_temp = new ArrayList<>();
        try
        {
            for (int i = 0; i < baterias.length(); i++) { registro_baterias = (JSONObject) baterias.get(i);
                bat = registro_baterias.getString("bateria");
                date = registro_baterias.getString("fecha");
                bat_val = Float.parseFloat(bat);
                dato_temp.add(new BarEntry(count, bat_val)); count++;
            }
        } catch (JSONException e) { e.printStackTrace(); System.out.println("error");
        }
        System.out.println(dato_temp);
        llenarGrafico(dato_temp);
    }

    private void llenarGrafico(ArrayList<BarEntry> dato_bat){
        BarDataSet bateriasDataSet;
        if ( graficoBarras.getData() != null &&
                graficoBarras.getData().getDataSetCount() > 0) {
            bateriasDataSet = (BarDataSet)
                    graficoBarras.getData().getDataSetByIndex(0);
            bateriasDataSet.setValues(dato_bat); graficoBarras.getData().notifyDataChanged();
            graficoBarras.notifyDataSetChanged();
        } else {
            bateriasDataSet = new BarDataSet(dato_bat, "Data Set");
            bateriasDataSet.setColors(ColorTemplate.VORDIPLOM_COLORS);
            bateriasDataSet.setDrawValues(true);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(bateriasDataSet);
            BarData data = new BarData(dataSets);
            graficoBarras.setData(data);
            graficoBarras.setFitBars(true);
        }
        graficoBarras.invalidate();
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                solicitarBaterias();
            } };
        handler.postDelayed(runnable, 3000);
    }

    public void regresarHome(View view){
        startActivity(new Intent(getApplicationContext(), Home.class));
    }

}
