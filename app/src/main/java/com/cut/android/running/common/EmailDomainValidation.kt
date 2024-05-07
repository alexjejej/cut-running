package com.cut.android.running.common

class EmailDomainValidation {

    companion object {
        var allowedDomains = listOf<String>(
            "academico.udg.mx",
            "alumnos.udg.mx"
        )

        public fun domainValidation(email: String): Boolean {
            val emailDomain = email.substringAfterLast("@")
            if (allowedDomains.contains(emailDomain)) return true;
            return false;
        }
    }
}