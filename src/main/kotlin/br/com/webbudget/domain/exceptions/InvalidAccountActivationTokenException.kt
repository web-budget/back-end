package br.com.webbudget.domain.exceptions

class InvalidAccountActivationTokenException(userEmail: String) :
    BusinessException("account-activation.errors.invalid-token", "Invalid token provided for user [$userEmail]")
