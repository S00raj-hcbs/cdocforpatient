package com.cybermed.cdoc_patient.me.securemessages.model;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class ResponseProvidersList{

	@SerializedName("providers")
	private List<ProvidersItem> providers;

	public List<ProvidersItem> getProviders(){
		return providers;
	}
}