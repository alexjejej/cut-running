package com.cut.android.running.common

class EmailDomainValidation {

    companion object {
        var allowedDomains = listOf<String>(
            "academicoa.udg.mx",
            "alumnos.udg.mx",
            "cutonala.udg.mx"
        )

        public fun domainValidation(email: String): Boolean {
            val emailDomain = email.substringAfterLast("@")
            if (allowedDomains.contains(emailDomain)) return true;
            return false;
        }
    }
}