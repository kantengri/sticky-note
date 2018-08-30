package my.app;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class Config {
	public String pass;
	public String site;
	
	static File file = new File("config.json");
	
	public Config() {
		this.pass = "123";
		this.site = "/user1";
	}

    static ObjectMapper getMapper() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new Jdk8Module());
    	return mapper;
    }
	
	public static Config load() throws IOException {

    	ObjectMapper mapper = getMapper();
    	
    	String str = FileUtils.readFileToString(file, "UTF-8");
    	Config rsp = mapper.readValue(str, Config.class);
    	return rsp;
		
	}
	
	public void save() throws Exception {
    	ObjectMapper mapper = getMapper();
		String str = mapper.writeValueAsString(this);
		FileUtils.writeStringToFile(file, str, "UTF-8");
	}
}
