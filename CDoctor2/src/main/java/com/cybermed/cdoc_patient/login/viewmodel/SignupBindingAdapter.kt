package com.cybermed.cdoc_patient.login.viewmodel

import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import androidx.databinding.BindingAdapter
import com.cybermed.cdoc_patient.R
import com.cybermed.cdoc_patient.login.signup.ValidationUtils
import com.google.android.material.textfield.TextInputLayout
import okio.JvmStatic

class SignupBindingAdapter {

    companion object {

        /**
         * Basic info next button enable/disable check
         */
        @JvmStatic
        @BindingAdapter("email", "password", "confirmPassword", "mobile")
        fun enableSignUp1(view: Button, email: String?, password: String?, confirmPassword: String?, mobile: String?) {
            view.isEnabled = !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) &&
                    !TextUtils.isEmpty(confirmPassword) && !TextUtils.isEmpty(mobile)
        }

        /**
         * error set listner
         */
        @JvmStatic
        @BindingAdapter("app:errorText")
        fun errorText(view: TextInputLayout, error: String?) {
            view.error = error
        }


        /**
         * Basic info phonetext changer
         */
        @JvmStatic
        @BindingAdapter("app:textChangedListener")
        fun phoneTextWatcher(view: EditText, mobile: String?) {
            view.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (view.getText().length === 12 && ValidationUtils.isPhoneNum(view.getText().toString())) {
                        view.setError(null)
                    } else if (view.getText().length === 12) {
                        view.setError(view.context.getString(R.string.regist_error_phone))
                        view.requestFocus()
                    }
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if (count > before) {
                        if (s!!.length == 3) {
                            view.setText("$s-")
                            view.setSelection(view.getText().length)
                        } else if (s.length == 7) {
                            view.setText("$s-")
                            view.setSelection(view.getText().length)
                        }
                    } else if (before > count) {
                        if (s!!.length == 7) {
                            // s = s.subSequence(0, 6)
                            view.setText(s.subSequence(0, 6))
                            view.setSelection(view.getText().length)
                        } else if (s.length == 3) {
                            // s = s.subSequence(0, 2)
                            view.setText(s.subSequence(0, 2))
                            view.setSelection(view.getText().length)
                        }
                    }
                }

            }
            )
        }

        /**
         * Account info button enable/disable check
         */
        @JvmStatic
        @BindingAdapter("firstName", "lastName", "gender", "dob")
        fun enableSignUp2(view: Button, firstName: String?, lastName: String?, gender: String?, dob: String?) {
            view.isEnabled = !TextUtils.isEmpty(firstName) && !TextUtils.isEmpty(lastName) &&
                    !TextUtils.isEmpty(gender) && !TextUtils.isEmpty(dob)
        }

        /**
         * Contact info button enable/disable check
         */
        @JvmStatic
        @BindingAdapter("addressLine1", "city", "state", "zip")
        fun enableSignUp3(view: Button, addressLine1: String?, city: String?, state: String?, zip: String?) {
            view.isEnabled = !TextUtils.isEmpty(addressLine1) && !TextUtils.isEmpty(city) &&
                    !TextUtils.isEmpty(state) && !TextUtils.isEmpty(zip)
        }

        /**
         * Select Mode button enable/disable
         */
        @JvmStatic
        @BindingAdapter("enable")
        fun enableSelectMode(view: Button, text: String?) {
            view.isEnabled = !TextUtils.isEmpty(text)
        }

        /**
         * Login Screen button enable/disable check
         */
        @JvmStatic
        @BindingAdapter("email", "password")
        fun enableLoginButton(view: Button, email: String?, password: String?) {
            view.isEnabled = !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)
        }

    }
}