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

import java.util.Date;

import net.pms.PMS;

import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.login.Anonymously;
import com.kallasoft.smugmug.api.json.v1_2_1.login.Anonymously.AnonymouslyResponse;
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithPassword;
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithPassword.WithPasswordResponse;

public class Account {

	private static final long REFRESH_INTERVAL = 60 * 60 * 1000; // 60 minutes
	public enum SmugSize {
		// largest to smallest
		ORIGINAL, X3LARGE, X2LARGE, XLARGE, LARGE, MEDIUM, SMALL, THUMB, TINY, INVALID
	}

	static SmugSize getSmugSize(String size) {
		if (size == null)
			return SmugSize.LARGE;			// default
		else if ("x3large".equalsIgnoreCase(size))
			return SmugSize.X3LARGE;
		else if ("x2large".equalsIgnoreCase(size))
			return SmugSize.X2LARGE;
		else if ("xlarge".equalsIgnoreCase(size))
			return SmugSize.XLARGE;
		else if ("large".equalsIgnoreCase(size))
			return SmugSize.LARGE;
		else if ("medium".equalsIgnoreCase(size))
			return SmugSize.MEDIUM;
		else if ("small".equalsIgnoreCase(size))
			return SmugSize.SMALL;
		else if ("thumb".equalsIgnoreCase(size))
			return SmugSize.THUMB;
		else if ("tiny".equalsIgnoreCase(size))
			return SmugSize.TINY;
		else 
			return SmugSize.LARGE;			// default
	}
	
	final private String id;
	final private String email;
	final private String password;
	final private String nickname;
	final private String name;
	final private String apikey;
	final private String imagesize;
	final private SmugSize smugsize;
	
	@Override
	public String toString() {
		return String.format(
				"Account [id=%s, email=%s, nick=%, refresh=%s, sessionId=%s]", 
				id,
				email, 
				nickname,
				refresh, 
				withPasswordResponse != null 
					? withPasswordResponse.getSessionID() 
							: null);
	}

	private Date refresh = new Date(0L);
	private WithPasswordResponse withPasswordResponse; 
	private AnonymouslyResponse anonResponse; 
	
	public Account(String id, String apikey, String email, String password, String nickname, String name, String imagesize) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
		this.name = name;
		this.apikey = apikey;
		this.imagesize = imagesize;
		this.smugsize = getSmugSize(imagesize);
	}
	
	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

	public String getName() {
		if (name != null)
			return name;
		// one of these two must be set
		if (getNickname() != null)
			return getNickname();
		return getEmail();
	}

	public String getApikey() {
		return apikey;
	}

	public String getImageSize() {
		return imagesize;
	}

	public SmugSize getSmugSize() {
		return smugsize;
	}

	public String getId() {
		return id;
	}
	
	public String getSessionId() {
		maybeRefresh();
		if (anonResponse != null)
			return anonResponse.getSessionID();
		else
			return withPasswordResponse.getSessionID();
	}

	private void maybeRefresh() {
		Date current = new Date();
		final long interval = current.getTime() - refresh.getTime();

		if (interval <= REFRESH_INTERVAL)
			return;

		PMS.info("Refresh interval exceeded (" + interval / 1000 + " seconds since last refresh)");
		refresh = current;
		if (getEmail() != null && getPassword() != null) {
			WithPassword withPassword = new WithPassword();
			withPasswordResponse = 
				withPassword.execute(APIVersionConstants.SECURE_SERVER_URL, 
						getApikey(), 
						getEmail(), 
						getPassword());
		} else {
			Anonymously anon = new Anonymously();
			anonResponse = anon.execute(APIVersionConstants.SECURE_SERVER_URL, 
						getApikey());
		}
	}

	public String getNickName() {
		maybeRefresh();
		return withPasswordResponse.getNickName();
	}
}
