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

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.transform;
import net.pms.PMS;
import net.pms.dlna.FeedItem;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.formats.Format;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.kallasoft.smugmug.api.json.entity.Image;
import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.images.Get;
import com.kallasoft.smugmug.api.json.v1_2_1.images.Get.GetResponse;

import net.pms.external.smugmug.Account.SmugSize;

public class AlbumFolder extends VirtualFolder {

	private final String id;
	private final int albumId;
	private final String albumKey;
	private Predicate<Image> predicate;
	
	public AlbumFolder(String id, int albumId, String albumKey, String title) {
		this(id, albumId, albumKey, title, Predicates.<Image> alwaysTrue());
	}

	public AlbumFolder(String id, int albumId, String albumKey, String title, Predicate<Image> predicate) {
		super(title, null);
		this.id = id;
		this.albumId = albumId;
		this.albumKey = albumKey;
		this.predicate = predicate;
	}

	@Override
	public void discoverChildren() {
		super.discoverChildren();
		Account account = SmugMugPlugin.getAccount(id);
		Get get = new Get();
		GetResponse getResponse = get.execute(APIVersionConstants.UNSECURE_SERVER_URL,
				account.getApikey(), 
				account.getSessionId(),
				albumId,
				albumKey,
				true);
		if (getResponse.isError()) {
			PMS.error("Error getting images for album with ID " + albumId + ": " + getResponse.getError(), null);
			return;
		}

		for (FeedItem feedItem : transform(
				filter(getResponse.getImageList(), predicate), 
				new Function<Image, FeedItem>() {
					@Override
					public FeedItem apply(Image image) {
						return new FeedItem(image.getFileName(), getBestURL(image), image.getThumbURL(), null, Format.IMAGE);
					}})) {
			addChild(feedItem);
		}
	}

	String getBestURL(Image im) {
		// Starting at the preferred size,
		// try successively smaller urls until we get one.
		SmugSize sz = SmugMugPlugin.getAccount(id).getSmugSize();
		String url = null;
		while (url == null)
		{
			switch(sz)
			{
				case ORIGINAL:
					url = im.getOriginalURL();
					break;
				case X3LARGE:
					url = im.getX3LargeURL();
					break;
				case X2LARGE:
					url = im.getX2LargeURL();
					break;
				case XLARGE:
					url = im.getXLargeURL();
					break;
				case LARGE:
					url = im.getLargeURL();
					break;
				case MEDIUM:
					url = im.getMediumURL();
					break;
				case SMALL:
					url = im.getSmallURL();
					break;
				case THUMB:
					url = im.getThumbURL();
					break;
				case TINY:
					url = im.getTinyURL();
					break;
				default:
					return null;
			}
			// sz++;
			sz = SmugSize.values()[sz.ordinal() + 1];
		}
		return url;
	}
}
