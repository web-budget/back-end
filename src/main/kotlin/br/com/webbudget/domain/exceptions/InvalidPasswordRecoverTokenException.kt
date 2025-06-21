package br.com.webbudget.domain.exceptions

import br.com.webbudget.domain.exceptions.ErrorCodes.INVALID_TOKEN

class InvalidPasswordRecoverTokenException(userEmail: String) :
    BusinessException("Invalid token provided for user [$userEmail]", INVALID_TOKEN)
