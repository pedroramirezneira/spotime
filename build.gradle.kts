plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.kapt) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.com.diffplug.spotless)
}

spotless {
    kotlin {
        target("**/*.kt")
        ktlint()
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:no-wildcard-imports"
        }
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:property-naming"
        }
        suppressLintsFor {
            step = "ktlint"
            shortCode = "standard:function-naming"
        }
    }
}
