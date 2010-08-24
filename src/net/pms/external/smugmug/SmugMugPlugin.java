package net.pms.external.smugmug;

import java.io.File;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JComponent;

import net.pms.PMS;
import net.pms.dlna.DLNAResource;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.external.AdditionalFolderAtRoot;

public class SmugMugPlugin implements AdditionalFolderAtRoot {
	
	private static final SortedMap<String, Account> ACCOUNT = new TreeMap<String, Account>();
	
	public static Account getAccount(String id) {
		return ACCOUNT.get(id);
	}
	
	public SmugMugPlugin() {
		PMS.info("Loading SmugMug Plugin");
		// FIXME allow selection of smugmug configuration through GUI
		final File file = new File(System.getProperty("user.home"), ".smugmug.properties");
		PMS.info("Reading account configurations from: " + file);
		try {
			Configuration configuration = new Configuration(file);
			for (Account account : configuration.getAccount()) 
				ACCOUNT.put(account.getId(), account);
		} catch (ConfigurationException e) {
			PMS.error("Error reading configuration", e);
		}
	}

	@Override
	public JComponent config() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String name() {
		return "SmugMug Plugin";
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DLNAResource getChild() {
		VirtualFolder folder = new VirtualFolder("SmugMug Accounts", null) {
			@Override
			public InputStream getThumbnailInputStream() {
				// FIXME find a logo image ...
				return super.getThumbnailInputStream();
			}
		};
		try {
			for (Account account : ACCOUNT.values()) {
				AccountFolder accountFolder = new AccountFolder(account.getId());
				folder.addChild(accountFolder);
			}
		} catch (Exception e) {
			PMS.error("Error getting account details", e);
		}
		return folder;
	}
}
