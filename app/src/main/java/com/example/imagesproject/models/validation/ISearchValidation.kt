package com.example.imagesproject.models.validation

interface ISearchValidation {

    /**
     * Check if the search term is valid ot not
     *
     * @return [ValidationState] state of the search term check
     */
    fun isSearchValid(searchTerm: String): ValidationState

    /**
     * Get the error msg text
     *
     * @return [String] msg
     */
    fun getErrorTextByState(validationState: Int): String

}