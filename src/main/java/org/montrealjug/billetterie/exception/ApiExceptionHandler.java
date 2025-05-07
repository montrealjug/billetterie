// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public Object handleEntityNotFoundException(EntityNotFoundException exception, Model model) {
        if (exception.getViewName() == null) {
            // not view set? it means we deal with an Ajax call
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("{\"message\":\"" + exception.getMessage() + "\"}");
        } else {
            model.addAttribute("errorMessage", exception.getMessage());
            model.addAttribute("event", null);
            return exception.getViewName();
        }
    }

    @ExceptionHandler(RedirectableNotFoundException.class)
    public ModelAndView handleRedirectableNotFound(
        RedirectableNotFoundException exception,
        RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("errorMessage", exception.getFlashMessage());
        return new ModelAndView("redirect:" + exception.getRedirectUrl());
    }

    @ExceptionHandler(value = { RequestException.class })
    public String handleRequestException(RequestException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("event", null);
        return exception.getViewName();
    }
}
