package common;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class FileUtil {
	public static void makeDir(String dirName) {
		boolean result = false;
		File file = new File(dirName);
		try {
			result = file.exists();
			if (result) {
				System.out.println("Folder exists.");
			} else {
				result = file.mkdirs();
				if (result) {
					System.out.println("Create folder successfully");
				} else {
					System.out.println("Error on create folder");
				}
			}
		} catch (Exception err) {
			System.err.println("unexcepted error");
			err.printStackTrace();
		}
	}
	
	public static void readLines(String file, ArrayList<String> lines) {
		BufferedReader reader = null;
		
		try {
			reader = new BufferedReader(new FileReader(new File(file)));
			String line = null;
			while((line = reader.readLine()) != null) {
				lines.add(line);
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void writeLines(String file, HashMap<?,?> hashMap) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(file)));
			
			Set<?> s = hashMap.entrySet();
			Iterator<?> it = s.iterator();
			while(it.hasNext()) {
				Map.Entry<?, ?> m = (Map.Entry<?, ?>) it.next();
				writer.write(m.getKey() + "\t" + m.getValue() + "\n");	
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void writeLines(String file, ArrayList<?> arrayList) {
		BufferedWriter writer = null;
		try {
			writer = new BufferedWriter(new FileWriter(new File(file)));
			
			for (int i = 0; i < arrayList.size(); ++i) {
				writer.write(arrayList.get(i) + "\n");
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			if (writer != null) {
				try {
					writer.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
}
