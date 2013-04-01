package org.mem.medesc.data;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mem.medesc.beans.Alternativa;
import org.mem.medesc.beans.Pregunta;
import org.mem.medesc.beans.Respuesta;
import org.mem.medesc.utils.Pair;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbHandler {
	static final String TBL_PREGUNTA = "pregunta";
	static final String TBL_ALTERNATIVA = "alternativa";
	static final String TBL_MEDICION = "medicion";
	static final String TBL_RESPUESTA = "respuesta";
	
	static public List<Pregunta> getAllPreguntas(SQLiteDatabase db) {
		List<Pregunta> preguntas = new ArrayList<Pregunta>();
		String selectQuery = "SELECT * FROM " + TBL_PREGUNTA;
		Cursor cursor = db.rawQuery(selectQuery, null);
		
		
		if(cursor.moveToFirst()) {
			do {
				Pregunta pregunta = new Pregunta();
				pregunta.setId(Long.parseLong(cursor.getString(0)));
				pregunta.setImgPath(cursor.getString(1));
				pregunta.setUnidad(Integer.parseInt(cursor.getString(2)));
				pregunta.setTipo(Integer.parseInt(cursor.getString(3)));
				pregunta.setAlternativas(getAlternativas(db, pregunta.getId()));
				
				preguntas.add(pregunta);
			} while(cursor.moveToNext());
		}
		
		return preguntas;
	}
	
	static public List<Alternativa> getAlternativas(SQLiteDatabase db, long idPregunta) {
		List<Alternativa> alternativas = new ArrayList<Alternativa>();
		Cursor cursor = db.query(TBL_ALTERNATIVA, 
				new String[] {"id", "id_pregunta", "img_contenido", "tipo", "apunta_a"}, 
				"id_pregunta=?",
				new String[] { String.valueOf(idPregunta) }, null, null, null, null);
		
		if(cursor.moveToFirst()) {
			do {
				Alternativa alt = new Alternativa();
				alt.setId(Long.parseLong(cursor.getString(0)));
				alt.setIdPregunta(Long.parseLong(cursor.getString(1)));
				alt.setImgPath(cursor.getString(2));
				alt.setTipo(Integer.parseInt(cursor.getString(3)));
				alt.setApuntaA(Integer.parseInt(cursor.getString(4)));
				
				alternativas.add(alt);
			} while(cursor.moveToNext());
		}
		
		return alternativas;
	}
	
	static public String[] tipoAlternativas = {
		"B", "X", "Y", "D"
	};
	
	static public List<String[]> getResultados(SQLiteDatabase db) {		
		String query = "SELECT medicion.id, nombre_alumno, timestamp, respuesta.id_pregunta, letra, respuesta.tipo, orden, duracion FROM medicion " +
				"INNER JOIN respuesta ON medicion.id = respuesta.id_medicion INNER JOIN alternativa ON alternativa.id = id_alternativa ORDER BY medicion.id, orden;";
		Cursor cursor = db.rawQuery(query, null);		
		
		List<String[]> results = new ArrayList<String[]>();
		String[] headers = new String[] {
				"Nro medicion", "Nombre alumno", "Fecha medicion", 
				"Nro Pregunta", "Alternativa", "Tipo Alternativa",
				"Orden", "Duracion"
		};
		
		results.add(headers);
		
		if(cursor.moveToFirst()) {
			do {
				String[] result = new String[8];
				result[0] = cursor.getString(0);
				result[1] = cursor.getString(1);
				result[2] = cursor.getString(2);
				result[3] = cursor.getString(3);
				result[4] = cursor.getString(4);
				result[5] = tipoAlternativas[Integer.parseInt(cursor.getString(5))];
				result[6] = cursor.getString(6);
				result[7] = cursor.getString(7);
				
				results.add(result);
			} while(cursor.moveToNext());
		}
		
		return results;
	}
	
	static public long addMedicion(SQLiteDatabase db, String nombreAlumno) {
		ContentValues values = new ContentValues();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		Date date = new Date();
		
		values.put("nombre_alumno", nombreAlumno);
		values.put("timestamp", dateFormat.format(date));
		long idMedicion = db.insert(TBL_MEDICION, null, values);
		
		return idMedicion;
	}
	
	static public long addRespuesta(SQLiteDatabase db, Respuesta respuesta) {
		ContentValues values = new ContentValues();
		values.put("id_medicion", respuesta.getIdMedicion());
		values.put("id_pregunta", respuesta.getIdPregunta());
		values.put("id_alternativa", respuesta.getIdAlternativa());
		values.put("tipo", respuesta.getTipo());
		values.put("orden", respuesta.getOrden());
		values.put("duracion", respuesta.getDuracion());
		
		return db.insert(TBL_RESPUESTA, null, values);
	}
	

}
