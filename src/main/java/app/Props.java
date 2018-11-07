package app;

import java.io.*;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class Props {
	
	private static final String PROPERTIES_LOCATION;
	
	static {
		String location = "";
		try {
			location = new File(Props.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent() + "/patients.properties";
			if (!new File(location).exists()) {
				location = Props.class.getClassLoader().getResource("patients.properties").toURI().getPath();
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		PROPERTIES_LOCATION = location;
	}
	
	public static Set<Map.Entry<Object, Object>> getAll() {
		
		Properties props = new Properties();
		Set<Map.Entry<Object, Object>> result = null;
		
		try (InputStream input = new FileInputStream(PROPERTIES_LOCATION)) {
			
			props.load(input);
			result = props.entrySet();
			
		} catch (IOException io) {
			io.printStackTrace();
		}
		return result;
	}
	
	public static void set(String name, String value) {
		
		Properties targetProps = new Properties();
		
		try (InputStream input = new FileInputStream(PROPERTIES_LOCATION)) {
			
			targetProps.load(input);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
		
		try (OutputStream output = new FileOutputStream(PROPERTIES_LOCATION)) {
			targetProps.setProperty(name, value);
			targetProps.store(output, null);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
	
	public static void delete(String firstName, String lastName) {
		
		Properties targetProps = new Properties();
		
		try (InputStream input = new FileInputStream(PROPERTIES_LOCATION)) {
			
			targetProps.load(input);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
		
		try (OutputStream output = new FileOutputStream(PROPERTIES_LOCATION)) {
			
			for (Iterator<Map.Entry<Object, Object>> iterator = targetProps.entrySet().iterator(); iterator.hasNext(); ) {
				Map.Entry<Object, Object> entry = iterator.next();
				Object key1 = entry.getKey();
				String key = key1.toString();
				if (key.contains(firstName) && key.contains(lastName)) {
					iterator.remove();
				}
			}
			targetProps.store(output, null);
			
		} catch (IOException io) {
			io.printStackTrace();
		}
	}
}
