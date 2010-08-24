/*
 * smugmug-pms3, a ps3mediaserver DLNA plugin for the SmugMug photo hosting service
 * Copyright (C) 2010  Matthew Kennedy
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 */
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
		PMS.info("Loading smugmug-pms3 Plugin");
		System.err.println("smugmug-pms3 Plugin, Copyright (C) 2010 Matthew Kennedy"
				+ "smugmug-pms3 comes with ABSOLUTELY NO WARRANTY"
				+ "This is free software, and you are welcome to redistribute it"
				+ "under the GNU GENERAL PUBLIC LICENSE Version 2");
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
