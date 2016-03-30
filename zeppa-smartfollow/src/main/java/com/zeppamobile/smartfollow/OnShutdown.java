package com.zeppamobile.smartfollow;

import it.uniroma1.lcl.adw.utils.GeneralUtils;

public class OnShutdown extends Thread {
	public void run() {
		GeneralUtils.deleteDownloadedSemSigs();
	}
}
