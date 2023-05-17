package br.com.webbudget.domain.exceptions

class InvalidPasswordRecoverToken(userEmail: String) :
    BusinessException("recover-password.errors.invalid-token", "Invalid token provided for user [$userEmail]")
