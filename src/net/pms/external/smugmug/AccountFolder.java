package net.pms.external.smugmug;

import static net.pms.external.smugmug.SmugMugPlugin.getAccount;
import net.pms.PMS;
import net.pms.dlna.virtual.VirtualFolder;

import com.kallasoft.smugmug.api.json.entity.Album;
import com.kallasoft.smugmug.api.json.v1_2_1.APIVersionConstants;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Get;
import com.kallasoft.smugmug.api.json.v1_2_1.albums.Get.GetResponse;

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
				false);
		if (getResponse.isError()) {
			PMS.error("Error getting album list: " + getResponse.getError(), null);
			return;
		}
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
