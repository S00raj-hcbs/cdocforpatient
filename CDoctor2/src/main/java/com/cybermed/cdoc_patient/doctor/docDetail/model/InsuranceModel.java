package com.cybermed.cdoc_patient.doctor.docDetail.model;

import com.cdfortis.datainterface.JsonSerializable;
import com.cdfortis.datainterface.annotation.DataField;
import com.cdfortis.datainterface.soap.model.SoapObjectData;

import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

public class InsuranceModel extends SoapObjectData implements JsonSerializable {

    @DataField
    public String company_code;
    @DataField
    public String company_name;
    @DataField
    public String insurance_id;
    @DataField
    public String copay_amount;
    @DataField
    public String claim_type;
    @DataField
    public String deductible_amount;
    @DataField
    public String emp_name;
    @DataField
    public String group_name;
    @DataField
    public String ins_type_code;
    @DataField
    public String payor_id;
    @DataField
    public String policy_effective_date;
    @DataField
    public String sequence_number;
    public String insured_first_name;
    public String insured_last_name;
    public InsuranceModel(SoapObject soapObject) {
        super(soapObject);
    }

    public InsuranceModel() {
    }

    @Override
    public void deserialize(JSONObject jsonObject) {
        company_code = jsonObject.optString("company_code", "");
        company_name = jsonObject.optString("company_name", "");
        insurance_id = jsonObject.optString("insurance_id", "");
        copay_amount = jsonObject.optString("copay_amount", "");
        claim_type = jsonObject.optString("claim_type", "");
        deductible_amount = jsonObject.optString("deductible_amount", "");
        emp_name = jsonObject.optString("emp_name", "");
        group_name = jsonObject.optString("group_name", "");
        ins_type_code = jsonObject.optString("ins_type_code", "");
        payor_id = jsonObject.optString("payor_id", "");
        policy_effective_date = jsonObject.optString("policy_effective_date", "");
        sequence_number = jsonObject.optString("sequence_number", "");
        insured_first_name = jsonObject.optString("insured_first_name", "");
        insured_last_name = jsonObject.optString("insured_last_name", "");
    }

    @Override
    public void serialize(JSONObject jsonObject) {

    }

    public String getInsured_first_name() {
        return insured_first_name == null? "": insured_first_name;
    }

    public void setInsured_first_name(String insured_first_name) {
        this.insured_first_name = insured_first_name;
    }

    public String getInsured_last_name() {
        return insured_last_name == null? "": insured_last_name;
    }

    public void setInsured_last_name(String insured_last_name) {
        this.insured_last_name = insured_last_name;
    }

    public String getCompany_code() {
        return company_code == null? "": company_code;
    }

    public void setCompany_code(String company_code) {
        this.company_code = company_code;
    }

    public String getCompany_name() {
        return company_name == null? "": company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
    }

    public String getInsurance_id() {
        return insurance_id == null? "": insurance_id;
    }

    public void setInsurance_id(String insurance_id) {
        this.insurance_id = insurance_id;
    }

    public String getCopay_amount() {
        return copay_amount == null? "": copay_amount;
    }

    public void setCopay_amount(String copay_amount) {
        this.copay_amount = copay_amount;
    }

    public String getClaim_type() {
        return claim_type == null? "": claim_type;
    }

    public void setClaim_type(String claim_type) {
        this.claim_type = claim_type;
    }

    public String getDeductible_amount() {
        return deductible_amount == null? "": deductible_amount;

    }

    public void setDeductible_amount(String deductible_amount) {
        this.deductible_amount = deductible_amount;
    }

    public String getEmp_name() {
        return emp_name == null? "": emp_name;
    }

    public void setEmp_name(String emp_name) {
        this.emp_name = emp_name;
    }

    public String getGroup_name() {
        return group_name == null? "": group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getIns_type_code() {
        return ins_type_code == null? "": ins_type_code;
    }

    public void setIns_type_code(String ins_type_code) {
        this.ins_type_code = ins_type_code;
    }

    public String getPayor_id() {
        return payor_id == null? "": payor_id;
    }

    public void setPayor_id(String payor_id) {
        this.payor_id = payor_id;
    }

    public String getPolicy_effective_date() {
        return policy_effective_date == null? "": policy_effective_date;
    }

    public void setPolicy_effective_date(String policy_effective_date) {
        this.policy_effective_date = policy_effective_date;
    }

    public String getSequence_number() {
        return sequence_number == null? "": sequence_number;
    }

    public void setSequence_number(String sequence_number) {
        this.sequence_number = sequence_number;
    }
}
