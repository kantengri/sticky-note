package my.app;

public class ProtectedTextSite {
	
	final ClientState clientState;
	
	public ProtectedTextSite() throws Exception {
		String siteURLArg = "/kantengri2";
		String pass = "Fgfhatyjd1";
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
