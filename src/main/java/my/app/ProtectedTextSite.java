package my.app;

public class ProtectedTextSite {
	
	final ClientState clientState;
	
	public ProtectedTextSite(Config conf) throws Exception {
		String siteURLArg = conf.site;
		String pass = conf.pass;
		clientState = new ClientState(siteURLArg, pass);
	}
	
	public String load() throws Exception {
		clientState.executeReloadSite();
		return clientState.content;
	}
	
	public void save(String content) throws Exception {
		clientState.executeSaveSite(content);
	}
}
