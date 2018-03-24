package com.V4Creations.mydrive.model.interfaces;

import com.V4Creations.mydrive.model.ResourceStackItem;

public interface ResourceLoaderInterface {
	public void onStartLoading();

	public void onLoadingSuccess(ResourceStackItem resourceStackItem);

	public void onLoadingFailure(String message, String trackMessage);
}
