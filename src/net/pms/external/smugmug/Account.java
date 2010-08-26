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
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithPassword;
import com.kallasoft.smugmug.api.json.v1_2_1.login.WithPassword.WithPasswordResponse;

public class Account {

	private static final long REFRESH_INTERVAL = 60 * 60 * 1000; // 60 minutes
	
	final private String id;
	final private String email;
	final private String password;
	final private String name;
	final private String apikey;
	
	@Override
	public String toString() {
		return String.format(
				"Account [id=%s, email=%s, refresh=%s, sessionId=%s]", 
				id,
				email, 
				refresh, 
				withPasswordResponse != null 
					? withPasswordResponse.getSessionID() 
							: null);
	}

	private Date refresh = new Date(0L);
	private WithPasswordResponse withPasswordResponse; 
	
	public Account(String id, String apikey, String email, String password, String name) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.name = name;
		this.apikey = apikey;
	}
	
	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getName() {
		return name == null ? getEmail() : name; 
	}
	public String getApikey() {
		return apikey;
	}

	public String getId() {
		return id;
	}
	
	public String getSessionId() {
		maybeRefresh();
		return withPasswordResponse.getSessionID();
	}

	private void maybeRefresh() {
		Date current = new Date();
		final long interval = refresh.getTime() - current.getTime();
		if (withPasswordResponse == null || interval > REFRESH_INTERVAL) {
			PMS.info("Refresh interval exceeded (" + interval / 1000 + " seconds since last refresh)");
			refresh = current;
			WithPassword withPassword = new WithPassword();
			withPasswordResponse = 
				withPassword.execute(APIVersionConstants.SECURE_SERVER_URL, 
						getApikey(), 
						getEmail(), 
						getPassword());
		}
	}

	public String getNickName() {
		maybeRefresh();
		return withPasswordResponse.getNickName();
	}
}
