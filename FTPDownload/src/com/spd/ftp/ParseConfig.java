package com.spd.ftp;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.spd.model.Task;

public class ParseConfig {
	private static String root=Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1); 
	public List<Task> getConfig() throws Exception {
		Gson gson = new Gson();
		String filepath = root+"/task.json";
		FileInputStream in = new FileInputStream(filepath);
		BufferedReader br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		StringBuilder json = new StringBuilder();
		String lineTxt = null;
		while((lineTxt = br.readLine()) != null){
			json.append(lineTxt);	
        }
		JsonParser parser = new JsonParser();
		JsonArray jsonArray = parser.parse(json.toString()).getAsJsonArray();
		List<Task> lsTask = new ArrayList();
		for (JsonElement je : jsonArray) {
			Task task = gson.fromJson(je, Task.class);
			lsTask.add(task);
		}
		return lsTask;
	}
}
