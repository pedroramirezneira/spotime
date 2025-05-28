package com.mediaverse.spotime.authentication

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject

class BiometricAuthManager
    @Inject
    constructor() {
        fun authenticate(
            context: Context,
            onError: () -> Unit,
            onSuccess: () -> Unit,
            onFail: () -> Unit,
        ) {
            val executor = ContextCompat.getMainExecutor(context)
            val biometricPrompt =
                BiometricPrompt(
                    context as FragmentActivity,
                    executor,
                    object : BiometricPrompt.AuthenticationCallback() {
                        override fun onAuthenticationError(
                            errorCode: Int,
                            errString: CharSequence,
                        ) {
                            super.onAuthenticationError(errorCode, errString)
                            // handle authentication error here
                            onError()
                        }

                        @RequiresApi(Build.VERSION_CODES.R)
                        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                            super.onAuthenticationSucceeded(result)
                            // handle authentication success here
                            onSuccess()
                        }

                        override fun onAuthenticationFailed() {
                            super.onAuthenticationFailed()
                            // handle authentication failure here
                            onFail()
                        }
                    },
                )

            val promptInfo =
                BiometricPrompt.PromptInfo
                    .Builder()
                    .setAllowedAuthenticators(
                        BiometricManager.Authenticators.BIOMETRIC_STRONG
                            or BiometricManager.Authenticators.DEVICE_CREDENTIAL,
                    ).setTitle("Biometric Authentication")
                    .build()

            biometricPrompt.authenticate(promptInfo)
        }
    }
