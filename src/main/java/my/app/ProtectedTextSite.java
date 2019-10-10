package my.app;

public class ProtectedTextSite {
	
	final ClientState clientState;
	boolean lastLoadOpFailed = false;
	
	public ProtectedTextSite(Config conf) throws Exception {
		String siteURLArg = conf.site;
		String pass = conf.pass;
		clientState = new ClientState(siteURLArg, pass);
	}
	
	public String load() throws Exception {
		lastLoadOpFailed = false;
		try {
			clientState.executeReloadSite();
		} catch (Exception e) {
			lastLoadOpFailed = true;
			throw e;
		}
		return clientState.content;
	}
	
	public void save(String content) throws Exception {
		if (this.lastLoadOpFailed) {
			throw new Exception("last load operation failed");
		}
		clientState.executeSaveSite(content);
	}
}
