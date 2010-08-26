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
import static net.pms.external.smugmug.SmugMugPlugin.getAccount;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import net.pms.PMS;
import net.pms.dlna.virtual.VirtualFolder;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.kallasoft.smugmug.api.json.entity.Album;
import com.kallasoft.smugmug.api.json.entity.Image;
import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Get;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Get.GetResponse;

public class RecentAdditionsFolder extends VirtualFolder {
	
	private final String accountId;
	private final DateFormat dateFormat;
	
	public RecentAdditionsFolder(String id) {
		super("Recent Photos", null);
		this.accountId = id;
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}

	@Override
	public void discoverChildren() {
		super.discoverChildren();
		Account account = getAccount(accountId);
		Get get = new Get();
		GetResponse response = get.execute(APIVersionConstants.UNSECURE_SERVER_URL, 
				account.getApikey(), 
				account.getSessionId(), 
				true);
		if (response.isError()) {
			PMS.error("Error getting albums: " + response.getError(), null);
			return;
		}
		Calendar calendar = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
		calendar.add(Calendar.MONTH, -1); // FIXME move this out to configuration file
		final Date target = calendar.getTime();
		Iterable<Album> albums = filter(response.getAlbumList(), new Predicate<Album>() {
			@Override
			public boolean apply(Album album) {
				try {
					Date date = dateFormat.parse(album.getLastUpdated());
					return date.getTime() > target.getTime();
				} catch (Exception e) {
					PMS.error("Error parsing last updated date: " + album.getLastUpdated(), e);
					return false;
				}
			}}); 

		final Predicate<Image> predicate = new Predicate<Image>() {
			@Override
			public boolean apply(Image image) {
				try {
					Date date = dateFormat.parse(image.getLastUpdated());
					return date.getTime() > target.getTime();
				} catch (ParseException e) {
					PMS.error("Error parsing image last updated date: " + image.getLastUpdated(), e);
					return false;
				}
			}};
			
		for (AlbumFolder folder : Iterables.transform(albums, new Function<Album, AlbumFolder>() {
				@Override
				public AlbumFolder apply(Album album) {
					return new AlbumFolder(accountId, album.getID(), album.getAlbumKey(), album.getTitle(), predicate);
				}})) {
			addChild(folder);
		}
	}

	@Override
	public String toString() {
		return String.format("RecentAdditionsFolder [accountId=%s]", accountId);
	}
}
