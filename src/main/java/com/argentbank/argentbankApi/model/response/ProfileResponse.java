package com.argentbank.argentbankApi.model.response;

/**
 * @param id
 * @param email
 * @param firstName
 * @param lastName
 * @param userName
 */
public record ProfileResponse(String id, String email, String firstName, String lastName, String userName) {
}
