package my.app;

import java.util.Optional;
//import java.util.logging.Level;
//import java.util.logging.Logger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;

public class ClientState {
	
	String eOrigContent;
	boolean isNew;
	int currentDBVersion;
	int expectedDBVersion;
	private String site;
	private String siteHash;
	private String initHashContent;
	String content;
	private String password;
	String urlBase = "https://www.protectedtext.com";
	
	private final Logger logger = LoggerFactory.getLogger(ClientState.class);

	public ClientState(String siteURLArg, String pass) throws Exception {
		
		
	    // provided from server:
	    this.site = siteURLArg; // URL of the site
	    this.eOrigContent = null; // encrypted content received from server
	    this.isNew = true; // is the site new
	    this.currentDBVersion = 2;     // Database versions are provided to client because: in case hashContent computation is changed,
	    this.expectedDBVersion = 2;   // only client can decrypt the content and compute new hashContent that will be saved on server.
	    
	    // computed:
	    this.siteHash = CryptoJS.SHA512(site); // hash of site URL, added to content before it's encrypted,
	                                                     // so that password correctness can be tested when decrypting the content
	    //this.initHashContent; // Initial hash of decrypted content, used for testing user's right to save changes and for overwrite protection.
	                         // Server allows the user to save and delete the current site only if he knows this hashContent value,
	                         // which is computed using dectypted content, so only the user who was able to decrypt the content has the right to change it.
	                         // When new content is save, new hashContent is provided to server, which will be used to verify the user next time.
	    this.content = ""; // text shown in textarea
	    this.password = pass; // users password. It never leaves the client, and isn't part of any hash or encrypted data sent to server.
		
	}
	
	
    void setInitHashContent() throws Exception {
        initHashContent = this.computeHashContentForDBVersion(content, password, currentDBVersion);
    }


	private String computeHashContentForDBVersion(String contentForHash, String passwordForHash, int dbVersion) throws Exception {
        if (dbVersion == 1)
            return CryptoJS.SHA512(contentForHash).toString();
        else if (dbVersion == 2)
            return CryptoJS.SHA512(contentForHash + CryptoJS.SHA512(passwordForHash).toString()).toString() + dbVersion;
        else {
            // loaded site in the browser hasn't refreshed for a long time, so long that the new dbVersion code wasn't ever loaded.
            // we have to force the refresh in order to prevent recursive calls from saveSite function. 
//            $(window).off('beforeunload');
//            location.reload(true);
        	return null;
        }
	};
	
	
    // sets login password and content, and return true if password is correct
    boolean setLoginPasswordAndContentIfCorrect(String pass) {
        String newContent = AES256Cryptor.decrypt(eOrigContent, pass); // try decrypting content
        
        if (newContent.indexOf(siteHash, newContent.length() - siteHash.length()) != -1) { // if newContent.endsWith(siteHash)
            content = newContent.substring(0, newContent.length() - siteHash.length()); 
            logger.debug("decrypted content: {}", newContent);
            password = pass;
            return true;
        }
        logger.warn(String.format("expected hash: %s, given: %s", siteHash, newContent));
        return false;
    };
    
    public static class GetRsp {
    	public String eContent;
    	public boolean isNew;
    	public int currentDBVersion;
    	public int expectedDBVersion;
    }
    
    ObjectMapper getMapper() {
    	ObjectMapper mapper = new ObjectMapper();
    	mapper.registerModule(new Jdk8Module());
    	return mapper;
    }
    
    GetRsp httpGet(String ... args) throws Exception {
    	String ret = HTTPHelper.get(this.urlBase + this.site, args);
    	logger.debug(ret);
    	ObjectMapper mapper = getMapper();
    	GetRsp rsp = mapper.readValue(ret, GetRsp.class);
		return rsp;
    }

    public static class PostRsp {
    	public String status;
    	public Optional<Integer> expectedDBVersion;
    }
    
	boolean httpPost(String ... args) throws Exception {
		String ret = HTTPHelper.post(this.urlBase + this.site, args);
    	logger.debug(ret);
    	ObjectMapper mapper = getMapper();
    	PostRsp rsp = mapper.readValue(ret, PostRsp.class);
    	return "success".equals(rsp.status);
	}
    
    void executeSaveSite (String content) throws Exception { 
         
    	String passwordToUse = this.password;
        String newHashContent = this.computeHashContentForDBVersion(content, passwordToUse, expectedDBVersion);
        String eContent = AES256Cryptor.encrypt((content + siteHash), passwordToUse); // encrypt(content + siteHash, password)
        
		if (!httpPost("initHashContent" , initHashContent, "currentHashContent" , newHashContent, "encryptedContent" , eContent, "action" , "save")) {
			throw new PostFailedException("post: status != success");
		}

        isNew = false;
        password = passwordToUse;
        currentDBVersion = expectedDBVersion;
        this.setInitHashContent();
		
    };
    
    
    void executeReloadSite() throws Exception { // function that reloads the site
    	GetRsp responseObject = httpGet("action", "getJSON");
    	
        eOrigContent = responseObject.eContent;
        currentDBVersion = responseObject.currentDBVersion;
        expectedDBVersion = responseObject.expectedDBVersion;
        isNew = responseObject.isNew;
        if (isNew == true) { 
            content = "";
            this.setInitHashContent();
        } else {
            decryptContentAndFinishInitialization(); // try using old password
        }
        
    };
    
    
    void decryptContentAndFinishInitialization() throws Exception {
        boolean success = this.setLoginPasswordAndContentIfCorrect(this.password);
        if (success == false) {
        	throw new LoadFailedException("failed to decrypt content with current password");
        }
        this.setInitHashContent();
    }
	
}
