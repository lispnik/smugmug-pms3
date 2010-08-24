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
