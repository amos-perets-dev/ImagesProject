package com.example.imagesproject.models.validation

class SearchValidationImpl(
    private val maxLengthSearchTerm: Int,
    private val errorMsgTextEmptyType: String,
    private val errorMsgTextLengthType: String,
    private val errorMsgTextLastPage: String,
    private val errorMsgTextNoInternetConnection: String
) :
    ISearchValidation {

    override fun isSearchValid(searchTerm: String): ValidationState =
        when {
            searchTerm.isEmpty() -> ValidationState.NOT_VALID_EMPTY
            searchTerm.length > this.maxLengthSearchTerm -> ValidationState.NOT_VALID_LENGTH
            else -> ValidationState.VALID
        }

    override fun getErrorTextByState(validationState: Int): String =
        when (validationState) {
            ValidationState.NOT_VALID_LENGTH.ordinal -> this.errorMsgTextLengthType
            ValidationState.LAST_PAGE.ordinal -> this.errorMsgTextLastPage
            ValidationState.NO_INTERNET_CONNECTION.ordinal -> this.errorMsgTextNoInternetConnection
            else -> this.errorMsgTextEmptyType
        }

}