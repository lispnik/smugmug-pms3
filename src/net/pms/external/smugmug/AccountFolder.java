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

import static net.pms.external.smugmug.SmugMugPlugin.getAccount;
import net.pms.PMS;
import net.pms.dlna.virtual.VirtualFolder;

import com.kallasoft.smugmug.api.json.entity.Album;
import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Get;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Get.GetResponse;

import net.pms.external.smugmug.CategoryFolder;

public class AccountFolder extends VirtualFolder {

	private String accountId;

	public AccountFolder(String id) {
		super(SmugMugPlugin.getAccount(id).getName(), null);
		this.accountId = id;
	}

	@Override
	public void discoverChildren() {
		addChild(new RecentAdditionsFolder(accountId));
		Account account = getAccount(accountId);
		Get get = new Get();
		final GetResponse getResponse = get.execute(APIVersionConstants.UNSECURE_SERVER_URL, 
				account.getApikey(), 
				account.getSessionId(), 
				account.getNickname(),
				false,
				null);
		if (getResponse.isError()) {
			PMS.error("Error getting album list: " + getResponse.getError(), null);
			return;
		}

		// categories
		addChild(new VirtualFolder("Categories", null) {
			@Override
			public void discoverChildren() {
				super.discoverChildren();
				for (CategoryFolder cf : CategoryFolder.getFolders(accountId, getResponse.getAlbumList())) {
					addChild(cf);
				}


			}
		});

		// albums
		addChild(new VirtualFolder("Albums", null) {
			@Override
			public void discoverChildren() {
				super.discoverChildren();
				for (Album album : getResponse.getAlbumList()) {
					addChild(new AlbumFolder(accountId, album.getID(), album.getAlbumKey(), album.getTitle()));
				}
			}
		});
	}
}
