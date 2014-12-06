package com.gdrive.desktop.client.Authorization;

import com.gdrive.desktop.client.Global.DriveDesktopClient;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.services.drive.DriveScopes;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Used for Authenticate App ID and Authorize user Credential.
 * 
 * @author harsh
 */
public class UserAutorization {
	/**
	 * <p>
	 * load client secrets(App Client ID & secret Code); return null if not able
	 * to authorize users
	 * </p>
	 * 
	 * @return Credential
	 */
	public Credential authorize() {
		Credential credential = null;
		List<String> scopesList = new ArrayList<String>();

		if (DriveDesktopClient.SCOPES != null) {
			scopesList = DriveDesktopClient.SCOPES;
		} else {
			return credential;
		}

		try {
			final GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
					DriveDesktopClient.HTTP_TRANSPORT,
					DriveDesktopClient.JSON_FACTORY,
					DriveDesktopClient.CLIENT_ID,
					DriveDesktopClient.CLIENT_SECRET, scopesList)
					.setDataStoreFactory(DriveDesktopClient.DATA_STORE_FACOTRY)
					.build();
			// authorize user credential read from folder(set by us in user
			// folder) else ask for authorization from user on browser and store
			// its credential in local folder
			credential = new AuthorizationCodeInstalledApp(flow,
					new LocalServerReceiver()).authorize("user");
		} catch (final IOException ex) {
			Logger.getLogger(UserAutorization.class.getName()).log(
					Level.SEVERE, null, ex);
			credential = null;
		}

		return credential;
	}
}
