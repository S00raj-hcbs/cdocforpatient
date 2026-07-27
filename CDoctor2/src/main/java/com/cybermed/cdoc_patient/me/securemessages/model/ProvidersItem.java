package com.cybermed.cdoc_patient.me.securemessages.model;

import com.cybermed.cdoc_patient.me.securemessages.Filterable;
import com.google.gson.annotations.SerializedName;

public class ProvidersItem implements Filterable {

	@SerializedName("provider_id")
	private String providerId;

	@SerializedName("provider_name")
	private String providerName;

	public String getProviderId(){
		return providerId;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public String getProviderName(){
		return providerName;
	}

	@Override
	public String getFilter(boolean asLocale) {
		return getProviderName();
	}
}