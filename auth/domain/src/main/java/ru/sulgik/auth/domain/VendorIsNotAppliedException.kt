package ru.sulgik.auth.domain

import ru.sulgik.auth.domain.data.AuthorizationWithoutVendor

class VendorIsNotAppliedException(val authorization: AuthorizationWithoutVendor) :
    Exception("vendor for account is not applied")

class IllegalVendorForAccountException : Exception("vendor for account is illegal")