package org.montrealjug.billetterie.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.ui.Model;

@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public String handleEntityNotFoundException(EntityNotFoundException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("event", null);
        return exception.getViewName();
    }
    @ExceptionHandler(RedirectableNotFoundException.class)
    public ModelAndView handleRedirectableNotFound(RedirectableNotFoundException exception, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", exception.getFlashMessage());
        return new ModelAndView("redirect:" + exception.getRedirectUrl());
    }


    @ExceptionHandler(value={RequestException.class})
    public String handleRequestException(RequestException exception, Model model) {
        model.addAttribute("errorMessage", exception.getMessage());
        model.addAttribute("event", null);
        return exception.getViewName();
    }
}
