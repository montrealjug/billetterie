// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.ui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.montrealjug.billetterie.entity.Participant;

class RegistrationControllerTest {

    @Test
    void retrieveBaseUrlTest() {
        // Create the controller
        RegistrationController controller =
                new RegistrationController(null, null, null, null, null, null);

        // Create a mock HttpServletRequest
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

        // Set up the mock to return specific values
        var requestURL = new StringBuffer("http://localhost:8080/some/path");
        when(request.getRequestURL()).thenReturn(requestURL);
        when(request.getRequestURI()).thenReturn("/some/path");
        when(request.getContextPath()).thenReturn("");

        // Call the method and verify the result
        String baseUrl = controller.retrieveBaseUrl(request);
        assertEquals("http://localhost:8080", baseUrl);

        // Test with a different URL and context path
        requestURL = new StringBuffer("https://example.com/some/path");
        when(request.getRequestURL()).thenReturn(requestURL);
        when(request.getRequestURI()).thenReturn("/some/path");
        when(request.getContextPath()).thenReturn("/app");

        baseUrl = controller.retrieveBaseUrl(request);
        assertEquals("https://example.com/app", baseUrl);
    }

    @Test
    public void isSameParticipantTest() {
        // Create the controller the same way as in retrieveBaseUrlTest
        RegistrationController controller =
                new RegistrationController(null, null, null, null, null, null);

        // Create a Participant
        Participant participant = new org.montrealjug.billetterie.entity.Participant();
        participant.setFirstName("John");
        participant.setLastName("Doe");
        participant.setYearOfBirth(1990);

        // Test with matching submission
        ParticipantSubmission matchingSubmission =
                new ParticipantSubmission("John", "Doe", 1990, 1L, "signature");
        boolean result = controller.isSameParticipant(participant, matchingSubmission);
        assertTrue(result, "Should return true for matching participant");

        // Test with different first name
        ParticipantSubmission differentFirstNameSubmission =
                new ParticipantSubmission("Jane", "Doe", 1990, 1L, "signature");
        result = controller.isSameParticipant(participant, differentFirstNameSubmission);
        assertFalse(result, "Should return false for different first name");

        // Test with different last name
        ParticipantSubmission differentLastNameSubmission =
                new ParticipantSubmission("John", "Smith", 1990, 1L, "signature");
        result = controller.isSameParticipant(participant, differentLastNameSubmission);
        assertFalse(result, "Should return false for different last name");

        // Test with different year of birth
        ParticipantSubmission differentYearSubmission =
                new ParticipantSubmission("John", "Doe", 1991, 1L, "signature");
        result = controller.isSameParticipant(participant, differentYearSubmission);
        assertFalse(result, "Should return false for different year of birth");

        // Test case insensitivity
        ParticipantSubmission caseInsensitiveSubmission =
                new ParticipantSubmission("JOHN", "DOE", 1990, 1L, "signature");
        result = controller.isSameParticipant(participant, caseInsensitiveSubmission);
        assertTrue(result, "Should return true for case-insensitive match");
    }
}
