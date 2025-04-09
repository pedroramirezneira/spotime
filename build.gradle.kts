// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.diffplug.spotless") version "7.0.3"
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