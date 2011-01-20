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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.pms.dlna.virtual.VirtualFolder;

import com.kallasoft.smugmug.api.json.entity.Album;

public class CategoryFolder extends VirtualFolder {

	// To avoid writing these nested generics.
	static class AlbumList extends ArrayList<Album> { };
	static class CatListMap extends LinkedHashMap<String,Cat> { };
	static class Cat {	// cat or sub-cat
		CatListMap	subCats = new CatListMap();
		AlbumList	albums = new AlbumList();
	}
	
	private final String accountId;
	private CatListMap	subCats;
	private AlbumList	albums;

	public CategoryFolder(String accountId, String name, CatListMap subCats, AlbumList albums) {
		super(name, null);
		this.accountId = accountId;
		this.subCats = subCats;
		this.albums = albums;
	}

	@Override
	public void discoverChildren() {
		super.discoverChildren();

		// subcategories
		if (subCats != null) {
			for (Entry<String,Cat> sub : subCats.entrySet()) {
				addChild(new CategoryFolder(accountId, sub.getKey(), null, sub.getValue().albums));
			}
		}

		// albums in this category with no subcategory
		if (albums != null) {
			for (Album album : albums) {
				addChild(new AlbumFolder(accountId, album.getID(), album.getAlbumKey(), album.getTitle()));
			}
		}
	}
}
