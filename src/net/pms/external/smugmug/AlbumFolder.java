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

import net.pms.PMS;
import net.pms.dlna.FeedItem;
import net.pms.dlna.virtual.VirtualFolder;
import net.pms.formats.Format;

import com.kallasoft.smugmug.api.json.entity.Image;
import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.images.Get;
import com.kallasoft.smugmug.api.json.v1_2_1.images.Get.GetResponse;

public class AlbumFolder extends VirtualFolder {

	private final String id;
	private final int albumId;
	private final String albumKey;
	
	public AlbumFolder(String id, int albumId, String albumKey, String title) {
		super(title, null);
		// FIXME get the thumbnail for the feature photo
		this.id = id;
		this.albumId = albumId;
		this.albumKey = albumKey;
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
		for (Image image : getResponse.getImageList()) {
			FeedItem feedItem = new FeedItem(image.getFileName(), image.getLargeURL(), image.getThumbURL(), null, Format.IMAGE);
			addChild(feedItem);
		}
	}
}
