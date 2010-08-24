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

public class Utils {
	
	public static boolean isEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}

}
